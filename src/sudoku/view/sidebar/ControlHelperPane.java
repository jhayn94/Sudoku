package sudoku.view.sidebar;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import sudoku.core.ModelController;
import sudoku.view.util.LabelConstants;

/**
 * This class corresponds to the button pane in the bottom left corner of the
 * grid view. The first row is colors used for coloring with the mouse; the
 * second two are the active digit when coloring candidates with the keyboard.
 */
public class ControlHelperPane extends GridPane {

	protected static final String SELECTED_COLOR_BUTTON_CSS_CLASS = "sudoku-color-button-selected";

	private static final int BUTTON_DIMENSIONS = 52;

	private static final String[] COLOR_BUTTON_CSS_CLASSES = { "sudoku-puzzle-color1a-entity",
			"sudoku-puzzle-color2a-entity", "sudoku-puzzle-color3a-entity", "sudoku-puzzle-color4a-entity",
			"sudoku-puzzle-color5a-entity" };

	private static final int PADDING_FOR_PANE = 15;

	private static final double H_GAP = 9.5;

	private static final int V_GAP = 15;

	private static final String CSS_CLASS_1 = "sudoku-transparent-pane";

	private static final int DEFAULT_WIDTH = 320;

	private static final int NUM_COLOR_BUTTONS = 5;

	private static final int NUM_DIGIT_BUTTONS = 9;

	private final Button[] colorButtons;

	private final Button[] digitButtons;

	public ControlHelperPane() {
		this.colorButtons = new Button[NUM_COLOR_BUTTONS];
		this.digitButtons = new Button[NUM_DIGIT_BUTTONS];
		this.configure();
	}

	private void configure() {
		this.setVgap(V_GAP);
		this.setHgap(H_GAP);
		this.getStyleClass().add(CSS_CLASS_1);
		this.setPadding(new Insets(PADDING_FOR_PANE));
		this.setMinWidth(DEFAULT_WIDTH);
		this.setMaxWidth(DEFAULT_WIDTH);
		this.createChildElements();
	}

	private void createChildElements() {
		for (int index = 0; index < NUM_COLOR_BUTTONS; index++) {
			this.colorButtons[index] = new Button();
			this.colorButtons[index].getStyleClass().add(COLOR_BUTTON_CSS_CLASSES[index]);
			this.colorButtons[index].setFocusTraversable(false);
			this.colorButtons[index].setMinWidth(BUTTON_DIMENSIONS);
			this.colorButtons[index].setMinHeight(BUTTON_DIMENSIONS);
			final int buttonIndex = index;
			this.colorButtons[index]
					.setOnAction(event -> ModelController.getInstance().transitionToActiveColorChangedState(buttonIndex));
			this.add(this.colorButtons[index], index, 0);
		}
		for (int index = 0; index < NUM_DIGIT_BUTTONS; index++) {
			this.digitButtons[index] = new Button(String.valueOf(index + 1));
			this.digitButtons[index].setFocusTraversable(false);
			this.digitButtons[index].setMinWidth(BUTTON_DIMENSIONS);
			this.digitButtons[index].setMinHeight(BUTTON_DIMENSIONS);
			final int buttonIndex = index;
			this.digitButtons[index].setOnAction(event -> ModelController.getInstance()
					.transitionToActiveCandidateChangedState(KeyCode.getKeyCode(String.valueOf(buttonIndex + 1))));
			this.add(this.digitButtons[index], index % 5, index / 5 + 1);
		}
		this.colorButtons[0].getStyleClass().add(SELECTED_COLOR_BUTTON_CSS_CLASS);

		final Button resetColorsButton = new Button(LabelConstants.R);
		resetColorsButton.setMinWidth(BUTTON_DIMENSIONS);
		resetColorsButton.setMinHeight(BUTTON_DIMENSIONS);
		resetColorsButton.setFocusTraversable(false);
		resetColorsButton.setOnAction(event -> ModelController.getInstance().transitionToResetAllColorsState());
		this.add(resetColorsButton, 4, 2);
	}

	public Button getColorButton(final int index) {
		return this.colorButtons[index];
	}

	public Button getDigitButton(final int index) {
		return this.digitButtons[index];
	}

}
