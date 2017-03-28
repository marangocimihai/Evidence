package view;

import java.io.PrintWriter;
import java.io.StringWriter;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 * The following class creates an alert with the according stacktrace of the raised exception.
 * 
 * @author marangocimihai
 *
 */
public class DialogException {
	public DialogException(Exception e) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Exception Dialog");
		alert.setHeaderText("An exception has been raised.");
		alert.setResizable(true);
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		String exceptionText = sw.toString();
		Label label = new Label("The exception stacktrace was: ");
		TextArea textArea = new TextArea(exceptionText);
		textArea.setEditable(false);
		textArea.setWrapText(true);
		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);
		GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(label, 0, 0);
		expContent.add(textArea, 0, 1);
		
		alert.getDialogPane().setContent(expContent);
		alert.showAndWait();
	}
}
