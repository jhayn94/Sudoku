package sudoku.view.hint;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import sudoku.view.util.LabelConstants;

/**
 * This class corresponds to the 4 x 3 button grid on the top left of the
 * screen. It contains numeric buttons 1 - 9 , which highlight cells where those
 * digits could go. It also contains X|Y, which highlights bivalue cells.
 * Lastly, this class creates buttons labeled "<" and ">", which are undo and
 * redo, respectively.
 */
public class HintButtonPane extends GridPane {

	private static final int PADDING_FOR_PANE = 15;

	private static final String CSS_CLASS = "sudoku-transparent-pane";

	private static final int DEFAULT_WIDTH = 300;

	private static final int BUTTON_HEIGHT = 50;

	private static final int BUTTON_WIDTH = 120;

	private Button vagueHintButton;

	private Button specificHintButton;

	private Button applyHintButton;

	private Button hideHintButton;

	public HintButtonPane() {
		this.configure();
	}

	private void configure() {
		this.getStyleClass().add(CSS_CLASS);
		this.setPadding(new Insets(0, 0, PADDING_FOR_PANE, 0));
		this.setHgap(44);
		this.setVgap(20);
		this.setMinWidth(DEFAULT_WIDTH);
		this.setMaxWidth(DEFAULT_WIDTH);
		this.createChildElements();
	}

	private void createChildElements() {
		this.vagueHintButton = new Button(LabelConstants.VAGUE_HINT);
		this.vagueHintButton.setMaxHeight(BUTTON_HEIGHT);
		this.vagueHintButton.setMinHeight(BUTTON_HEIGHT);
		this.vagueHintButton.setMinWidth(BUTTON_WIDTH);
		this.vagueHintButton.setMaxWidth(BUTTON_WIDTH);
		this.vagueHintButton.setFocusTraversable(false);
		this.add(this.vagueHintButton, 0, 0);
		this.specificHintButton = new Button(LabelConstants.SPECIFIC_HINT);
		this.specificHintButton.setMinHeight(BUTTON_HEIGHT);
		this.specificHintButton.setMaxHeight(BUTTON_HEIGHT);
		this.specificHintButton.setMinWidth(BUTTON_WIDTH);
		this.specificHintButton.setMaxWidth(BUTTON_WIDTH);
		this.specificHintButton.setFocusTraversable(false);
		this.add(this.specificHintButton, 1, 0);
		this.applyHintButton = new Button(LabelConstants.APPLY_HINT);
		this.applyHintButton.setMinHeight(BUTTON_HEIGHT);
		this.applyHintButton.setMaxHeight(BUTTON_HEIGHT);
		this.applyHintButton.setMinWidth(BUTTON_WIDTH);
		this.applyHintButton.setMaxWidth(BUTTON_WIDTH);
		this.applyHintButton.setFocusTraversable(false);
		this.add(this.applyHintButton, 0, 1);
		this.hideHintButton = new Button(LabelConstants.HIDE_HINT);
		this.hideHintButton.setMinHeight(BUTTON_HEIGHT);
		this.hideHintButton.setMaxHeight(BUTTON_HEIGHT);
		this.hideHintButton.setMinWidth(BUTTON_WIDTH);
		this.hideHintButton.setMaxWidth(BUTTON_WIDTH);
		this.hideHintButton.setFocusTraversable(false);
		this.add(this.hideHintButton, 1, 1);

	}

	public Button getHideHintButton() {
		return this.hideHintButton;
	}

	public Button getVagueHintButton() {
		return this.vagueHintButton;
	}

	public Button getSpecificHintButton() {
		return this.specificHintButton;
	}

	public Button getApplyHintButton() {
		return this.applyHintButton;
	}

}
