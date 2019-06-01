package sudoku.view.puzzle;

import org.apache.logging.log4j.util.Strings;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import sudoku.core.ModelController;
import sudoku.model.ApplicationSettings;
import sudoku.model.SudokuPuzzleValues;

/** This class corresponds to a single cell of a sudoku puzzle. */
public class SudokuPuzzleCell extends StackPane {

	private static final String CANDIDATE_LABEL_CSS_CLASS = "sudoku-cell-candidate-label";

	protected static final String SELECTED_CELL_CSS_CLASS = "sudoku-selected-cell";

	private static final int CANDIDATE_LABEL_HEIGHT = 16;

	private static final int CANDIDATE_LABEL_WIDTH = 14;

	private static final String DIGIT_REPLACE_TEXT = "DIGIT";

	private static final String NUMPAD_REPLACE_TEXT = "NUMPAD";

	public enum ReasonForChange {
		CLICKED_TO_SELECT, CLICKED_TO_UNSELECT, ARROWED_OFF_OF_CELL, NEW_SELECTION_CLICKED, ARROWED_ON_TO_CELL, NONE;
	}

	private static final String CSS_CLASS = "sudoku-puzzle-cell";

	private static final int CELL_HEIGHT = 62;

	public static final int CELL_WIDTH = 62;

	private static final int INTERIOR_COMPONENT_HEIGHT = CELL_HEIGHT;

	private static final int INTERIOR_COMPONENT_WIDTH = CELL_WIDTH;

	private final Label[] candidateLabels;

	private Label fixedDigitLabel;

	private final int row;

	private final int col;

	private boolean isGiven;

	private GridPane candidatesGridPane;

	private Pane cellIsSelectedIndicator;

	public SudokuPuzzleCell(final int row, final int col) {
		super();
		this.isGiven = false;
		this.row = row;
		this.col = col;
		this.candidateLabels = new Label[SudokuPuzzleValues.CELLS_PER_HOUSE];
		this.fixedDigitLabel = null;
		this.configure();
	}

	public int getRow() {
		return this.row;
	}

	public int getCol() {
		return this.col;
	}

	public boolean isCellFixed() {
		return !this.fixedDigitLabel.getText().isEmpty();
	}

	public boolean isCellGiven() {
		return this.isGiven;
	}

	public void setCellGiven(final boolean given) {
		this.isGiven = given;
	}

	/** Returns the set value for the cell, or -1 if there isn't one. */
	public int getFixedDigit() {
		return this.isCellFixed() ? Integer.parseInt(this.fixedDigitLabel.getText()) : -1;
	}

	/**
	 * Sets a pane on top of all other elements to have or not have a special CSS
	 * class that denotes the cell as selected by the user.
	 */
	public void setIsSelected(final boolean isSelected) {
		if (isSelected) {
			this.cellIsSelectedIndicator.getStyleClass().add(SELECTED_CELL_CSS_CLASS);
		} else {
			this.cellIsSelectedIndicator.getStyleClass().remove(SELECTED_CELL_CSS_CLASS);
		}
	}

	/**
	 * Shows the candidates pane if showCandidates is true, shows the fixed digit
	 * pane otherwise.
	 */
	public void setCandidatesVisible(final boolean showCandidates) {
		final ObservableList<Node> children = this.getChildren();
		children.get(0).setVisible(showCandidates);
		children.get(1).setVisible(!showCandidates);
	}

	/**
	 * Sets the candidate at the given index visible based on the passed boolean.
	 * Note that pressedDigit is one less than the digit (i.e. index of 1 is 0).
	 */
	public void setCandidateVisible(final int pressedDigit, final boolean visible) {
		this.candidateLabels[pressedDigit - 1].setVisible(visible);
	}

	/** Sets the fixed digit's value. */
	public void setFixedDigit(final String digit) {
		// KeyCode.toString() is passed to this; replacing the keyCode's name is
		// easier than 9 if statements.
		this.fixedDigitLabel
				.setText(digit.replace(DIGIT_REPLACE_TEXT, Strings.EMPTY).replace(NUMPAD_REPLACE_TEXT, Strings.EMPTY));
	}

	public Label getCandidateLabelForDigit(final int digit) {
		if (digit < 1 || digit > this.candidateLabels.length) {
			throw new IllegalArgumentException("Digit must be between 1-9 (inclusive).");
		} else {
			return this.candidateLabels[digit - 1];
		}
	}

	private void configure() {
		this.addEventHandlers();
		this.setMinWidth(CELL_WIDTH);
		this.setMinHeight(CELL_HEIGHT);
		this.setMaxWidth(CELL_WIDTH);
		this.setMaxHeight(CELL_HEIGHT);
		this.getStyleClass().add(CSS_CLASS);
		this.createChildElements();
	}

	private void createChildElements() {
		final ObservableList<Node> children = this.getChildren();
		this.createCandidatesGridPane();
		this.createCandidateLabels();
		final Pane fixedDigitPane = this.createFixedDigitPane();
		children.add(this.candidatesGridPane);
		children.add(fixedDigitPane);
		this.cellIsSelectedIndicator = new Pane();
		children.add(this.cellIsSelectedIndicator);
	}

	private void createCandidatesGridPane() {
		this.candidatesGridPane = new GridPane();
		this.candidatesGridPane.setMinHeight(INTERIOR_COMPONENT_HEIGHT);
		this.candidatesGridPane.setMaxHeight(INTERIOR_COMPONENT_HEIGHT);
		this.candidatesGridPane.setMinWidth(INTERIOR_COMPONENT_WIDTH);
		this.candidatesGridPane.setMaxWidth(INTERIOR_COMPONENT_WIDTH);
		GridPane.setHalignment(this.candidatesGridPane, HPos.CENTER);
		GridPane.setValignment(this.candidatesGridPane, VPos.CENTER);
		this.candidatesGridPane.setAlignment(Pos.CENTER);
		this.candidatesGridPane.setHgap(5);
		this.candidatesGridPane.setVgap(3);
	}

	private Pane createFixedDigitPane() {
		final StackPane fixedDigitPane = new StackPane();
		StackPane.setAlignment(fixedDigitPane, Pos.CENTER);
		this.fixedDigitLabel = new Label();
		this.fixedDigitLabel.setMinHeight(INTERIOR_COMPONENT_HEIGHT);
		this.fixedDigitLabel.setMaxHeight(INTERIOR_COMPONENT_HEIGHT);
		this.fixedDigitLabel.setMinWidth(INTERIOR_COMPONENT_WIDTH);
		this.fixedDigitLabel.setMaxWidth(INTERIOR_COMPONENT_WIDTH);
		this.fixedDigitLabel.setContentDisplay(ContentDisplay.CENTER);
		this.fixedDigitLabel.setAlignment(Pos.CENTER);
		fixedDigitPane.getChildren().add(this.fixedDigitLabel);
		fixedDigitPane.setVisible(false);
		return fixedDigitPane;
	}

	private void createCandidateLabels() {
		for (int index = 1; index <= SudokuPuzzleValues.CELLS_PER_HOUSE; index++) {
			this.candidateLabels[index - 1] = new Label(String.valueOf(index));
			this.candidateLabels[index - 1].getStyleClass().add(CANDIDATE_LABEL_CSS_CLASS);
			this.candidateLabels[index - 1].setAlignment(Pos.CENTER);
			this.candidateLabels[index - 1].setContentDisplay(ContentDisplay.CENTER);
			// Force sizes to be the same for each number.
			this.candidateLabels[index - 1].setMinWidth(CANDIDATE_LABEL_WIDTH);
			this.candidateLabels[index - 1].setMaxWidth(CANDIDATE_LABEL_WIDTH);
			this.candidateLabels[index - 1].setMinHeight(CANDIDATE_LABEL_HEIGHT);
			this.candidateLabels[index - 1].setMaxHeight(CANDIDATE_LABEL_HEIGHT);
			this.candidateLabels[index - 1].setVisible(ApplicationSettings.getInstance().isAutoManageCandidates());
			// Integer division intentional!
			this.candidatesGridPane.add(this.candidateLabels[index - 1], (index - 1) % 3, (index - 1) / 3);
		}
	}

	private EventHandler<MouseEvent> onClick() {
		return event -> ModelController.getInstance().transitionToClickedCellState(this.row, this.col, event.isShiftDown());
	}

	/**
	 * This method resets the cell's event handlers to the current state's handler.
	 * When the cell's state changes, event handlers have to be re-registered for
	 * the new state to be used.
	 */
	private void addEventHandlers() {
		this.setEventHandler(MouseEvent.MOUSE_CLICKED, this.onClick());
	}

}
