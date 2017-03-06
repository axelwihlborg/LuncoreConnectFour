package gameengine;

import java.util.Optional;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

public class Players {
	
	private String p1;
	private String p2;
	
	//Stores playernames in uppcase to prevent same player to get multiple highscores with different capitalization
	public Players(String p1, String p2){
		p1.toUpperCase();
		p2.toUpperCase();
		this.p1 = p1;
		this.p2 = p2;
	}
	//Returns name of player1
	public String getp1(){
		return p1;
	}
	//Returns name of player2
	public String getp2(){
		return p2;
	}
	
	public void changePlayerNames(){
		// Create the custom dialog.
				Dialog<Pair<String, String>> dialog = new Dialog<>();
				dialog.setTitle("Player Names");
				dialog.setHeaderText("Please enter the name of player 1 and 2 \n"
						+ "Current P1: " + p1 + "\n" + "Current P2: " + p2);

				// Set the button types.
				ButtonType loginButtonType = new ButtonType("Enter", ButtonData.OK_DONE);
				dialog.getDialogPane().getButtonTypes().addAll(loginButtonType);

				// Create the username labels and fields.
				GridPane grid = new GridPane();
				grid.setHgap(10);
				grid.setVgap(10);
				grid.setPadding(new Insets(20, 150, 10, 10));

				TextField username = new TextField();
				username.setPromptText(p1);
				TextField usrname2 = new TextField();
				usrname2.setPromptText(p2);

				grid.add(new Label("Player 1 Name:"), 0, 0);
				grid.add(username, 1, 0);
				grid.add(new Label("Player 2 Name:"), 0, 1);
				grid.add(usrname2, 1, 1);

				// Enable/Disable login button depending on whether a username was entered.
				Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
				loginButton.setDisable(true);

				
				// Validates if the box is empty
				username.textProperty().addListener((observable, oldValue, newValue) -> {
				    loginButton.setDisable(newValue.trim().isEmpty());
				});
				
				usrname2.textProperty().addListener((observable, oldValue, newValue) -> {
				    loginButton.setDisable(newValue.trim().isEmpty());
				});


				dialog.getDialogPane().setContent(grid);

				// Request focus on the P1 field by default.
				Platform.runLater(() -> username.requestFocus());

				// Convert the result to a playernames-pair when the login button is clicked.
				dialog.setResultConverter(dialogButton -> {
				    if (dialogButton == loginButtonType) {
				        return new Pair<>(username.getText(), usrname2.getText());
				    }
				    return null;
				});

				Optional<Pair<String, String>> result = dialog.showAndWait();

				result.ifPresent(names -> {
				    p1 = names.getKey();
				    p2 = names.getValue();
				});
				
	}

}
