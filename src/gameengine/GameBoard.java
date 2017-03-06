package gameengine;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.*;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class GameBoard extends Application {

	private static int COLUMNS = 7;
	private static int ROWS = 6;
	
	private Stage stage;
	private GridPane root;
	private Scene scene;
	private ArrayList<Circle> list;
	private boolean p1turn = true;
	private PlayChip[][] chips = new PlayChip[COLUMNS][ROWS];
	private Pane chipPane = new GridPane();
	private int drawGame = 0;
	private GameState state;
	private ArrayList<GameMoves> moves;
	private Players players;
	private DatabaseHandler db;
	
	
	//Launches the game
	public static void launchGame(){
		launch();
	}
	
	//Sets up the game, with all dependencies
	public void start(Stage stage) throws Exception {
		this.stage = stage;
		

		//Opens a connection to the database, sends out an Error if it failed
		db = new DatabaseHandler();
		if (!db.openConnection("connectfour.db")) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Exiting game");
			alert.setHeaderText("Exiting game");
			alert.setContentText("Database not found, please make sure you have and its named \"connectfour.db\" ");
			alert.showAndWait();
			System.exit(0);
		}else{
			System.out.println("Success");
		}

		
		//Creates contents and set initial screen to low size.
		scene = new Scene(createContent());
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		stage.show();
		stage.setTitle("Connect Four");
		stage.setHeight(500);
		stage.setWidth(500);
		stage.setMaxHeight(screenSize.getHeight());
		stage.setMaxWidth(screenSize.getHeight());
		stage.minWidthProperty().bind(scene.heightProperty());
		stage.minHeightProperty().bind(scene.widthProperty());
		stage.setFullScreen(false);
		stage.setScene(scene);
		
		
		//Creates objects needed to for functionallity
		players = new Players("", "");
		players.changePlayerNames();
		state = new GameState(this);
		moves = new ArrayList<GameMoves>();
		


	}
	
	//Resets the game, more or less by reacreating it
	public void restartGame(){
		scene = new Scene(createContent());
		stage.setScene(scene);
		chips = new PlayChip[COLUMNS][ROWS];
		p1turn = true;
		drawGame = 0;
	}
	
	
	//Undoes the last moves and resets the GUI to its old state
	public void undoMove(){
		if(!(moves.size() == 0)){		
		chips[moves.get(moves.size()-1).getCol()][moves.get(moves.size() - 1).getRow()] = null;
		
		list.get(moves.get(moves.size()-1).getCol() 
				+ ((COLUMNS) * (moves.get(moves.size() - 1).getRow())))
		.setFill(Color.WHITE);
		
		moves.remove(moves.size()-1);
		p1turn = !p1turn;
		drawGame--;
		}
		
		
	}
	
	//Creates all the parts that makes up the userinterface
	private Parent createContent() {
		root = new GridPane();

		Rectangle rec = makeRectangle();
		Pane recpane = new Pane();
		recpane.getChildren().add(rec);
		root.add(recpane, 0, 1);

		Pane circlepane = new GridPane();

		makeCircles();
		for (Circle c : list) {
			circlepane.getChildren().add(c);
		}

		root.add(chipPane, 0, 1);
		root.add(circlepane, 0, 1);
		MenuBarHolder menu = new MenuBarHolder(this, players, db);
		root.add(menu.getMenu(), 0, 0);
		return root;
	}

	
	//Creates the base blue rectangle
	private Rectangle makeRectangle() {
		Rectangle rectangle = new Rectangle();

		// here we bind rectangle size to pane size
		rectangle.widthProperty().bind(stage.widthProperty());
		rectangle.heightProperty().bind(stage.heightProperty());

		rectangle.setFill(Color.BLUE);

		return rectangle;
	}

	
	//Creates all the circleshapes in the board and also makes them respond to mousemovents and current player
	private void makeCircles() {
		list = new ArrayList<Circle>();
		NumberBinding CircleRadSize = Bindings.min(stage.heightProperty(), stage.widthProperty());
		for (int i = 0; i < ROWS; i++) {
			for (int j = 0; j < COLUMNS; j++) {
				Circle circle = new Circle();
				final int column = j;
				final int row = i;
				
				//Binds the circles properties to the windowsize
				circle.radiusProperty().bind(CircleRadSize.divide(2.4 * Math.max(ROWS, COLUMNS)));
				circle.setTranslateX(j * stage.widthProperty().doubleValue() / COLUMNS);
				circle.setTranslateY(i * stage.heightProperty().doubleValue() / (ROWS + 0.5));

				circle.translateXProperty().bind(stage.widthProperty().divide(COLUMNS).multiply(j));
				circle.translateYProperty().bind(stage.heightProperty().divide((ROWS + 0.5)).multiply(i));
				circle.setFill(Color.WHITE);
				
				//Sets the color of the circle to represent whos turn it is
				circle.setOnMouseEntered(e -> {
					if (!getPlayChip(column, row).isPresent()) {
						circle.setFill(p1turn ? Color.DARKRED : Color.rgb(255, 255, 0, 0.8));

					}
				});
				
				//Set the color back to it original, unless a PlayChip is inplace
				circle.setOnMouseExited(e -> {
					if (!getPlayChip(column, row).isPresent()) {
						circle.setFill(Color.WHITE);

					}
				});
				
				
				//Places a chip in the column that the button was pressed
				circle.setOnMouseClicked(e -> {
					placeChip(new PlayChip(p1turn), column);
				});

				list.add(circle);
			}
		}
	}
	
	
	//Places a playchip on the board
	public void placeChip(PlayChip chip, int column) {
		//Finds where the lowest point is, also good if you want to animate the fall of the chip
		int row = ROWS - 1;
		while (row >= 0) {
			if (!getPlayChip(column, row).isPresent()) {
				break;
			}
			row--;
		}
		
		//Checks if the chip fell below the board
		if (row < 0) {
			return;
		}
		
		//Adds a chip and binds it to the windows size
		chips[column][row] = chip;
		chipPane.getChildren().add(chip);
		chip.setTranslateX(column * stage.widthProperty().doubleValue() / COLUMNS);
		chip.translateXProperty().bind(stage.widthProperty().divide(COLUMNS).multiply(column));
		chip.setTranslateY(row * stage.heightProperty().doubleValue() / ROWS + 0.5);
		chip.translateYProperty().bind(stage.heightProperty().divide(ROWS + 0.5).multiply(row));
		
		
		//Set the circles color to transparent to show the chip, since it is behind the circles (for animation purpose)
		//also logs the latest move
		list.get(column + ((COLUMNS) * (row))).setFill(Color.TRANSPARENT);
		moves.add(new GameMoves(column, row));
		
		
		//Checks if this move ended the game
		if (state.gameEnd(column, row, p1turn)) {
			gameOver();
			return;
		}
		
		//Checks if there are no more moves
		drawGame++;
		state.drawGame(drawGame, COLUMNS, ROWS);

		//Passes the turn over
		p1turn = !p1turn;


	}

	
	//Called if the game is over, presents the user with a couple of choices
	private void gameOver() {
		
		
		
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Game Over");
		alert.setHeaderText("The game is over, winner is: " + (p1turn ? players.getp1(): players.getp2()));
		alert.setContentText("New Game adds winner to highscore and restarts the game\nRestart game without highscore"
				+ " \nIf you are done, save the result and exit. \nIf you messed up, feel free to undo the last moves");

		ButtonType buttonNewGame = new ButtonType("New Game");
		ButtonType buttonRestartGame = new ButtonType("Restart Game");
		ButtonType SaveAndExit = new ButtonType("Save & Exit");
		ButtonType undoMove = new ButtonType("Undo Last Move");

		alert.getButtonTypes().setAll(buttonNewGame, buttonRestartGame, SaveAndExit, undoMove);

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == buttonNewGame){
			db.updateHighscore(p1turn ? players.getp1() : players.getp2());
			restartGame();
		} else if (result.get() == buttonRestartGame) {
			restartGame();
		} else if (result.get() == SaveAndExit) {
			db.updateHighscore(p1turn ? players.getp1() : players.getp2());
			exitGame();
		} else if(result.get() == undoMove){
				undoMove();
		}

	}
	
	
	
	public void drawGame(){
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Game Over");
		alert.setHeaderText("The game is over, its a tie!");
		alert.setContentText("Restart game"
				+ " \nIf you are done, press Exit. \nIf you messed up, feel free to undo the last moves");

		ButtonType buttonRestartGame = new ButtonType("Restart Game");
		ButtonType SaveAndExit = new ButtonType("Exit game");
		ButtonType undoMove = new ButtonType("Undo Last Move");

		alert.getButtonTypes().setAll(buttonRestartGame, SaveAndExit, undoMove);

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == buttonRestartGame) {
			restartGame();
		} else if (result.get() == SaveAndExit) {
			db.updateHighscore(p1turn ? players.getp1() : players.getp2());
			exitGame();
		} else if(result.get() == undoMove){
				undoMove();
		}

	}
	
	
	
	
	//Confirms the user chouice and exits the game
	public void exitGame(){
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Exiting game");
		alert.setHeaderText("Exiting game");
		alert.setContentText("Save complete, press ok to exit");
		alert.showAndWait();
		System.exit(0);
	}

	
	//Returns a playchip if it is present at that location
	public Optional<PlayChip> getPlayChip(int column, int row) {
		if (column < 0 || column >= COLUMNS || row < 0 || row >= ROWS) {
			return Optional.empty();
		}
		return Optional.ofNullable(chips[column][row]);
	}

	
	//A method to set the BoardSize before game is launched, not in use but still here so it can be implemented easily
	public static void setBoardSize(int col, int row) {
		COLUMNS = col;
		ROWS = row;
	}


	
	
	
	//A nested class, that takes the circles shape and represent a playchip on the board.
	public class PlayChip extends Circle {
		private boolean p1turn;

		public PlayChip(boolean p1turn) {
			super();
			this.p1turn = p1turn;
			NumberBinding CircleRadSize = Bindings.min(stage.heightProperty(), stage.widthProperty());

			this.radiusProperty().bind(CircleRadSize.divide(2.4 * Math.max(ROWS, COLUMNS)));
			this.setFill(p1turn ? Color.RED : Color.YELLOW);

		}
		//Returns the turn the Chip represents
		public boolean getTurn() {
			return p1turn;
		}
	}

}
