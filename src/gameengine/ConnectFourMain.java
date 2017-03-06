package gameengine;



public class ConnectFourMain {

	//Mainmethod, there boardsize can we set if wanted to
	public static void main(String[] args) {	
		
		GameBoard.setBoardSize(7, 6);
		GameBoard.launchGame();

	}
}