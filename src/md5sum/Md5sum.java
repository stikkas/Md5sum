package md5sum;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Md5sum extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	@SuppressWarnings("empty-statement")
	public void start(Stage stage) throws Exception {
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);

		TextField tf = new TextField();
		tf.setPromptText("Исходный файл");
		tf.setPrefWidth(450);
		grid.add(tf, 0, 0);

		Button btn = new Button("Выбрать");
		grid.add(btn, 1, 0);

		ChoiceBox<String> choice = new ChoiceBox<>(FXCollections.observableArrayList(
				"MD5", "SHA-1", "SHA-256"));
		choice.getSelectionModel().selectFirst();
		grid.add(choice, 0, 1);
		GridPane.setHalignment(choice, HPos.RIGHT);

		Button execButton = new Button("Выполнить");
		grid.add(execButton, 1, 1);

		Label label = new Label();
		grid.add(label, 0, 2, 2, 1);

		AnchorPane pane = new AnchorPane(grid);
		AnchorPane.setLeftAnchor(grid, 5.0);
		AnchorPane.setTopAnchor(grid, 5.0);
		AnchorPane.setRightAnchor(grid, 5.0);

		stage.setScene(new Scene(pane));
		stage.setTitle("My Md5Sum");
		stage.show();
		stage.setMaxHeight(stage.getHeight());
		btn.setPrefWidth(execButton.getWidth());
		label.setPrefWidth(stage.getWidth());
		btn.setOnAction(e -> {
			File file = new FileChooser().showOpenDialog(stage.getOwner());
			if (file != null) {
				tf.setText(file.getAbsolutePath());
			}
		});

		execButton.setOnAction(e -> {
			MessageDigest md;
			try {
				md = MessageDigest.getInstance(choice.getValue());
				try (InputStream is = Files.newInputStream(Paths.get(tf.getText()))) {
					DigestInputStream dis = new DigestInputStream(is, md);
					byte[] buffer = new byte[4096];
					int bytes;
					while ((bytes = is.read(buffer)) > 0) {
						md.update(buffer, 0, bytes);
					}
				} catch (IOException ex) {
					Logger.getLogger(Md5sum.class.getName()).log(Level.SEVERE, null, ex);
				}
				byte[] digest = md.digest();
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < digest.length; ++i) {
					sb.append(String.format("%02x", digest[i]));
				}
				label.setText(sb.toString());
			} catch (NoSuchAlgorithmException ex) {
				Logger.getLogger(Md5sum.class.getName()).log(Level.SEVERE, null, ex);
			}
		});
	}

}
