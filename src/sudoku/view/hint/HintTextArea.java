package sudoku.view.hint;

import javafx.geometry.Insets;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;

/**
 * This class contains methods to display a hint to the user.
 */
public class HintTextArea extends Pane {

	private static final int PADDING_FOR_PANE = 15;

	private static final String CSS_CLASS = "sudoku-transparent-pane";

	private static final int DEFAULT_WIDTH = 300;

	private static final int TEXT_AREA_HEIGHT = 373;

	private TextArea hintTextArea;

	public HintTextArea() {
		this.configure();
	}

	private void configure() {
		this.getStyleClass().add(CSS_CLASS);
		this.setPadding(new Insets(PADDING_FOR_PANE));
		this.setMinWidth(DEFAULT_WIDTH);
		this.setMaxWidth(DEFAULT_WIDTH);
		this.createChildElements();
	}

	private void createChildElements() {
		this.hintTextArea = new TextArea();
		this.hintTextArea.setFocusTraversable(false);
		this.hintTextArea.setWrapText(true);
		this.hintTextArea.setEditable(false);
		this.hintTextArea.setMinWidth(DEFAULT_WIDTH - PADDING_FOR_PANE);
		this.hintTextArea.setMaxWidth(DEFAULT_WIDTH - PADDING_FOR_PANE);
		this.hintTextArea.setMinHeight(TEXT_AREA_HEIGHT);
		this.getChildren().add(this.hintTextArea);
	}

	public TextArea getHintTextArea() {
		return this.hintTextArea;
	}
}
