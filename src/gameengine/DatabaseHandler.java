package gameengine;

import java.sql.*;
import java.util.ArrayList;

import javafx.util.Pair;


//DatabaseHandler is a class that specifies the interface to the database, uses JDBC
public class DatabaseHandler {

	private Connection conn;

	// Create the database interface object. Connection to the database is performed later.
	 
	public DatabaseHandler() {
		conn = null;
	}

	//Opens a connection to the database
	public boolean openConnection(String filename) {
		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite::resource:gameengine/" + filename);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	//Closes the connection to the database, if that is needed
	public void closeConnection() {
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	//Checks if the DB is connected, else returns false
	public boolean isConnected() {
		return conn != null;
	}
	
	
	//Asks the database to provide the highscorelist
	public ArrayList<Pair<String, String>> getHighScoreList(){
		PreparedStatement stmt = null;
		ArrayList<Pair<String, String>> list = new ArrayList<Pair<String, String>>();
		try{
			String sql = "SELECT username, games_won FROM highscore";
			stmt = conn.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				list.add(new Pair<>(rs.getString("username"), rs.getString("games_won")));
			
			}
		}catch(SQLException e){
			e.printStackTrace();
		} finally{
			try{
				stmt.close();
			} catch(SQLException e){
				
			}
		}
		return list;
	}
			
			
	//Updates the highscore database 
	public boolean updateHighscore(String playername) {
	PreparedStatement stmt = null;
	

		//Checks is the playername is in the database and if it is, increments the games_won by 1
		for(Pair<String, String> p: getHighScoreList()){
			if(playername.toUpperCase().equals(p.getKey().toUpperCase())){
				try{
				String sql2 = "UPDATE highscore SET games_won = (SELECT games_won + 1 FROM highscore WHERE username = ?) WHERE username = ?";
				stmt = conn.prepareStatement(sql2);
				stmt.setString(1, playername.toUpperCase());
				stmt.setString(2, playername.toUpperCase());
				stmt.executeUpdate();
				conn.commit();
				}catch(SQLException e){
					return false;
				}finally{
					try{
				stmt.close();
					}catch(SQLException e){return false;
					}
				}
				return true;
			}
		}
		//If player was not in the database, inserts the player into the database with one game won
		try{
		String sql = "INSERT INTO highscore(username, games_won) VALUES (?, ?)";
		stmt = conn.prepareStatement(sql);
		stmt.setString(1, playername.toUpperCase());
		stmt.setInt(2, 1);
		stmt.executeUpdate();
		}catch(SQLException e){
			return false;
		}finally{
			try{
			stmt.close();
			}catch(SQLException e){
			return false;
			}
		}
		return true;
	}

	
	//Resets the table
	public void resetTabel() {
		PreparedStatement stmt = null;
		try{
			String sql = "DELETE FROM highscore";
			stmt = conn.prepareStatement(sql);
			stmt.executeUpdate();
		}catch(SQLException e){
		} finally{
			try{
				stmt.close();
			} catch(SQLException e){
				
			}
		}
	}

		

			
	
}
