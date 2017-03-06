package gameengine;

import java.util.Optional;

import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.util.Pair;


public class MenuBarHolder {
	private MenuBar menubar;
	private Menu game, player, information;
	private GameBoard board;
	private Players plObj;
	private DatabaseHandler db;
	
	//Adds the menubar to the top of the board
	public MenuBarHolder(GameBoard board, Players plObj, DatabaseHandler db){
		this.board = board;
		this.plObj = plObj;
		this.db = db;
		
		game = new Menu("Actions");
		player = new Menu("Players");
		information = new Menu("Information");
		menubar = new MenuBar(game, player, information);
		fillGameMenu();
		fillPlayerMenu();
		fillInformationMenu();
	}
	
	//Fills the Actions-menu, for ingame actions 
	private void fillGameMenu() {
		MenuItem undo = new MenuItem("Undo Move");		
		undo.setOnAction(e -> board.undoMove());
		
		//Shows a confirmation, and if ok is pressed restarts the game
		MenuItem restart = new MenuItem("Restart Game");
		restart.setOnAction(e -> {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Restart game");
			alert.setHeaderText("Are you sure you want to restart?");
			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK){
			    board.restartGame();
			} 
		});
		
		
		game.getItems().addAll(undo, restart);
		
	}
	//Fills the playerinfomenu
	private void fillPlayerMenu(){
		MenuItem showplayers = new MenuItem("Show/Change player names");
		
		showplayers.setOnAction(e -> plObj.changePlayerNames());
		
		
		player.getItems().add(showplayers);
		
	}
	//Fills informationmenu
	private void fillInformationMenu(){
		MenuItem highscorelist = new MenuItem("Show Highscore");
		
		//Sets up an alerpane to show the Highscore
		highscorelist.setOnAction(e -> {
		
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("HighscoreList");
		alert.setHeaderText("Highscore ConnectFour" + "\n"
			+ "Player"	 + "		Games Won");
		StringBuilder sb = new StringBuilder();
		
		for(Pair<String, String> p: db.getHighScoreList()){
			sb.append(p.getKey());
			sb.append(": ");
			sb.append(p.getValue());
			sb.append("\n");
		}
		
		ButtonType reset = new ButtonType("Reset Highscore");
		ButtonType ok = new ButtonType("Ok", ButtonData.CANCEL_CLOSE);
		alert.getButtonTypes().setAll(reset, ok);
		alert.setContentText(sb.toString());
		Optional<ButtonType> result = alert.showAndWait();
		
		
		if(result.get() == reset){
			db.resetTabel();		
		}
		

			
			
			
		});
		
		information.getItems().addAll(highscorelist);
	
	}

	public MenuBar getMenu(){
		return menubar;
	}
}
