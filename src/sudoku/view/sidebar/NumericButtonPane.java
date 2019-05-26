package sudoku.view.sidebar;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import sudoku.core.ModelController;
import sudoku.factories.LayoutFactory;
import sudoku.view.util.LabelConstants;
import sudoku.view.util.TooltipConstants;

/**
 * This class corresponds to the 4 x 3 button grid on the top left of the
 * screen. It contains numeric buttons 1 - 9 , which highlight cells where those
 * digits could go. It also contains X|Y, which highlights bivalue cells.
 * Lastly, this class creates buttons labeled "<" and ">", which are undo and
 * redo, respectively.
 */
public class NumericButtonPane extends GridPane {

	private static final int PADDING_FOR_PANE = 15;

	private static final String CSS_CLASS = "sudoku-transparent-pane";

	private static final int DEFAULT_WIDTH = 320;

	private static final int NUM_DIGIT_BUTTONS = 9;

	private static final int MIN_BUTTON_HEIGHT = 50;

	private static final int MIN_BUTTON_WIDTH = 75;

	private final List<Button> filterButtons;

	private Button undoButton;

	private Button redoButton;

	public NumericButtonPane() {
		this.filterButtons = new ArrayList<>(10);
		this.configure();
	}

	public List<Button> getFilterButtons() {
		return this.filterButtons;
	}

	public Button getUndoButton() {
		return this.undoButton;
	}

	public Button getRedoButton() {
		return this.redoButton;
	}

	private void configure() {
		this.getStyleClass().add(CSS_CLASS);
		this.setPadding(new Insets(PADDING_FOR_PANE));
		this.setHgap(36.5);
		this.setVgap(20);
		this.setMinWidth(DEFAULT_WIDTH);
		this.setMaxWidth(DEFAULT_WIDTH);
		this.createChildElements();
	}

	private void createChildElements() {
		for (int index = 1; index <= NUM_DIGIT_BUTTONS; index++) {
			final Button digitFilterButton = LayoutFactory.getInstance().createToggleButton(String.valueOf(index));
			// Integer division intentional!
			this.add(digitFilterButton, (index - 1) % 3, (index - 1) / 3);
			this.filterButtons.add(digitFilterButton);
		}
		final Button bivalueCellFilterButton = LayoutFactory.getInstance().createToggleButton(LabelConstants.BIVALUE_CELL);
		this.filterButtons.add(bivalueCellFilterButton);
		this.add(bivalueCellFilterButton, 1, 3);
		this.createUndoButton();
		this.createRedoButton();
	}

	private void createUndoButton() {
		this.undoButton = new Button();
		this.undoButton.setFocusTraversable(false);
		this.undoButton.setText(LabelConstants.UNDO);
		this.undoButton.setTooltip(new Tooltip(TooltipConstants.UNDO));
		this.undoButton.setMinHeight(MIN_BUTTON_HEIGHT);
		this.undoButton.setMinWidth(MIN_BUTTON_WIDTH);
		this.undoButton.setOnAction(event -> ModelController.getInstance().transitionToUndoActionState());
		this.add(this.undoButton, 0, 3);
	}

	private void createRedoButton() {
		this.redoButton = new Button();
		this.redoButton.setFocusTraversable(false);
		this.redoButton.setText(LabelConstants.REDO);
		this.redoButton.setTooltip(new Tooltip(TooltipConstants.REDO));
		this.redoButton.setMinHeight(MIN_BUTTON_HEIGHT);
		this.redoButton.setMinWidth(MIN_BUTTON_WIDTH);
		this.redoButton.setOnAction(event -> ModelController.getInstance().transitionToRedoActionState());
		this.add(this.redoButton, 2, 3);
	}

}
