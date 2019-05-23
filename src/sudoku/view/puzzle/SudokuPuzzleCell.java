package sudoku.view.puzzle;

import org.apache.logging.log4j.util.Strings;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import sudoku.core.ModelController;

/** This class corresponds to a single cell of a sudoku puzzle. */
public class SudokuPuzzleCell extends StackPane {

	private static final String DIGIT_REPLACE_TEXT = "DIGIT";

	public enum ReasonForChange {
		CLICKED_TO_SELECT, CLICKED_TO_UNSELECT, ARROWED_OFF_OF_CELL, NEW_SELECTION_CLICKED, ARROWED_ON_TO_CELL, NONE;
	}

	private static final String CSS_CLASS = "sudoku-puzzle-cell";

	private static final int CELL_HEIGHT = 60;

	private static final int CELL_WIDTH = 60;

	private static final int MAX_NUM_CANDIDATES_IN_CELL = 9;

	private final Label[] candidateLabels;

	private Label fixedDigitLabel;

	private final int row;

	private final int col;

	private boolean isGiven;

	public SudokuPuzzleCell(final int row, final int col) {
		super();
		this.isGiven = false;
		this.row = row;
		this.col = col;
		this.candidateLabels = new Label[MAX_NUM_CANDIDATES_IN_CELL];
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
	public int getFixedValue() {
		return this.isCellFixed() ? Integer.parseInt(this.fixedDigitLabel.getText()) : -1;
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
	 * Note that the digitIndex is one less than the digit (i.e. index of 1 is 0).
	 */
	public void setCandidateVisible(final int pressedDigit, final boolean visible) {
		this.candidateLabels[pressedDigit - 1].setVisible(visible);
	}

	/** Sets the fixed digit's value. */
	public void setFixedDigit(final String digit) {
		// KeyCode.toString() is passed to this, and it this helps to avoid 9
		// if-statements.
		this.fixedDigitLabel.setText(digit.replace(DIGIT_REPLACE_TEXT, Strings.EMPTY));
	}

	private void configure() {
		this.resetEventHandlers();
		this.setMinWidth(CELL_WIDTH);
		this.setMinHeight(CELL_HEIGHT);
		this.setMaxWidth(CELL_WIDTH);
		this.setMaxHeight(CELL_HEIGHT);
		this.getStyleClass().add(CSS_CLASS);
		this.createChildElements();
	}

	private void createChildElements() {
		final ObservableList<Node> children = this.getChildren();
		final GridPane candidatesGridPane = new GridPane();
		GridPane.setHalignment(candidatesGridPane, HPos.CENTER);
		GridPane.setValignment(candidatesGridPane, VPos.CENTER);
		candidatesGridPane.setAlignment(Pos.CENTER);
		candidatesGridPane.setHgap(7);
		candidatesGridPane.setVgap(0);
		for (int index = 1; index <= MAX_NUM_CANDIDATES_IN_CELL; index++) {
			this.candidateLabels[index - 1] = new Label(String.valueOf(index));
			// Integer division intentional!
			candidatesGridPane.add(this.candidateLabels[index - 1], (index - 1) % 3, (index - 1) / 3);
		}
		final StackPane fixedDigitPane = new StackPane();
		StackPane.setAlignment(fixedDigitPane, Pos.CENTER);
		this.fixedDigitLabel = new Label();
		fixedDigitPane.getChildren().add(this.fixedDigitLabel);
		fixedDigitPane.setVisible(false);
		children.add(candidatesGridPane);
		children.add(fixedDigitPane);
	}

	private EventHandler<MouseEvent> onClick() {
		return event -> ModelController.getInstance().transitionToClickedCellState(this.row, this.col);
	}

	/**
	 * This method resets the cell's event handlers to the current state's
	 * handler. When the cell's state changes, event handlers have to be
	 * re-registered for the new state to be used.
	 */
	private void resetEventHandlers() {
		this.setEventHandler(MouseEvent.MOUSE_CLICKED, this.onClick());
	}

}
