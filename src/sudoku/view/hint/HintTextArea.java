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

	private static final int TEXT_AREA_HEIGHT = 160;

	private TextArea activeHintTextArea;

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
		this.activeHintTextArea = new TextArea();
		this.activeHintTextArea.setFocusTraversable(false);
		this.activeHintTextArea.setWrapText(true);
		this.activeHintTextArea.setEditable(false);
		this.activeHintTextArea.setMinWidth(DEFAULT_WIDTH - PADDING_FOR_PANE);
		this.activeHintTextArea.setMaxWidth(DEFAULT_WIDTH - PADDING_FOR_PANE);
		this.activeHintTextArea.setMinHeight(TEXT_AREA_HEIGHT);
		this.getChildren().add(this.activeHintTextArea);
	}

	public TextArea getHintTextArea() {
		return this.activeHintTextArea;
	}
}
