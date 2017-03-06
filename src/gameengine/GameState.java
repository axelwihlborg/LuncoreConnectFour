package gameengine;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import gameengine.GameBoard.PlayChip;
import javafx.geometry.Point2D;



public class GameState {

	private GameBoard board;
	
	public GameState(GameBoard board){
		this.board = board;
	}
	
	
	//The main algorithm to check if game has ended
	public boolean gameEnd(int column, int row, boolean p1turn){
		
		//Checks vertical
		List<Point2D> vert = IntStream.rangeClosed(row- 3, row+ 3)
				.mapToObj(therow -> new Point2D(column, therow))
				.collect(Collectors.toList());
		//Checks horizontal
		List<Point2D> hori = IntStream.rangeClosed(column- 3, column+ 3)
				.mapToObj(col -> new Point2D(col, row))
				.collect(Collectors.toList());
		//Check left diagonal
		Point2D diagPointLeft = new Point2D(column - 3, row - 3);
		List<Point2D> diagLeft = IntStream.rangeClosed(0, 6)
				.mapToObj(dL -> diagPointLeft.add(dL, dL))
				.collect(Collectors.toList());
		
		
		//Check right diagonal
		Point2D diagPointRight = new Point2D(column - 3, row + 3);
		List<Point2D> diagRight = IntStream.rangeClosed(0, 6)
				.mapToObj(dR -> diagPointRight.add(dR, -dR))
				.collect(Collectors.toList());
		
		return fourConnected(vert, p1turn) || fourConnected(hori, p1turn) 
				|| fourConnected(diagRight, p1turn) || fourConnected(diagLeft, p1turn);
		
		
	}
	
	//Helpmethod to check if there is a chain of four around the point presented
	public boolean fourConnected(List<Point2D> points, boolean p1turn){
		int chain = 0;
		
		//Checks for all points in the list if is a chain.
		for(Point2D p : points){
			int col = (int)p.getX();
			int row = (int)p.getY();
			
			//Checks if there is a chip in that spot, other creates one with wrong color to disrupt the algorithm
			PlayChip chip = board.getPlayChip(col, row).orElse(board.new PlayChip(!p1turn));
			
			//Checks if there is a chain
			if(chip.getTurn() == p1turn){
				chain++;
				if(chain == 4){
					return true;
				}
			}else{
				chain = 0;
			}
		}
		return false;
		
	}
	
	//Checks if the game is a draw
	public void drawGame(int movesDone, int col, int rows){
		if(movesDone == col*rows){
		board.drawGame();
		}
	}

}
