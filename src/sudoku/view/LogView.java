package sudoku.view;

import org.apache.logging.log4j.util.Strings;

import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;

public class LogView extends BorderPane {

	private TextArea logTextField;

	public LogView() {
		this.configure();
	}

	public TextArea getLogTextArea() {
		return this.logTextField;
	}

	private void configure() {
		this.createChildElements();
	}

	private void createChildElements() {
		this.logTextField = new TextArea();
		this.logTextField.setText(Strings.EMPTY);
		this.logTextField.setEditable(false);
		this.logTextField.setWrapText(true);
		this.setCenter(this.logTextField);
	}
}
