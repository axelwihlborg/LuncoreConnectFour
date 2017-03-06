package gameengine;

public class GameMoves {
	private int col;
	private int row;
	
	
	public GameMoves(int col, int row){
		this.col = col;
		this.row = row;
	}
	
	public int getCol(){
		return col;
	}
	
	public int getRow(){
		return row;
	}

}
