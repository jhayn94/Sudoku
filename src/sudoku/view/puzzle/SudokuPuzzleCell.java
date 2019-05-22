package sudoku.view.puzzle;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import sudoku.core.ModelController;
import sudoku.state.cell.action.DefaultCellActionState;
import sudoku.state.cell.active.AutomaticallyInactiveCellState;
import sudoku.state.cell.active.DefaultCellActiveState;
import sudoku.state.cell.active.ManuallyInactiveCellState;

/** This class corresponds to a single cell of a sudoku puzzle. */
public class SudokuPuzzleCell extends StackPane {

	private static final String CSS_CLASS = "sudoku-puzzle-cell";

	private static final int CELL_HEIGHT = 60;

	private static final int CELL_WIDTH = 60;

	private static final int MAX_NUM_CANDIDATES_IN_CELL = 9;

	private final Label[] candidateLabels;

	private Label fixedDigitLabel;

	// An action state is the behavioral response to changing the Sudoku relevant
	// state of the cell (add / remove candidates, set / remove number, etc.
	private DefaultCellActionState actionState;

	// An active state handles the behavioral responds to being selected or
	// unselected (when you click a cell, or use the arrow keys for instance).
	private DefaultCellActiveState activeState;

	private final int row;

	private final int col;

	private boolean isGiven;

	public SudokuPuzzleCell(int row, int col) {
		super();
		this.isGiven = false;
		this.row = row;
		this.col = col;
		this.candidateLabels = new Label[MAX_NUM_CANDIDATES_IN_CELL];
		this.fixedDigitLabel = null;
		this.actionState = new DefaultCellActionState(this);
		this.activeState = new DefaultCellActiveState(this);
		this.actionState.onEnter();
		this.activeState.onEnter();
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

	public void setCellGiven(boolean given) {
		this.isGiven = given;
	}

	public DefaultCellActionState getActionState() {
		return this.actionState;
	}

	public DefaultCellActiveState getActiveState() {
		return this.activeState;
	}

	public void setActionState(DefaultCellActionState newActionState) {
		this.actionState = newActionState;
		this.actionState.onEnter();
		// Event handlers have to be re-registered for the new state to be used.
		this.setEventHandler(KeyEvent.KEY_PRESSED, this.onKeyPressed());
		this.setEventHandler(MouseEvent.MOUSE_CLICKED, this.onClick());
		// Notify the model controller that a cell changed, meaning other cells might
		// need to change too (i.e. un-selecting other cells, or eliminating candidates
		// which are not allowed now).
		ModelController.getInstance().transitionToCellChangedState(this.actionState);
	}

	public void setActiveState(DefaultCellActiveState newActiveState) {
		this.activeState = newActiveState;
		this.activeState.onEnter();
		// Event handlers have to be re-registered for the new state to be used.
		this.setEventHandler(KeyEvent.KEY_PRESSED, this.onKeyPressed());
		this.setEventHandler(MouseEvent.MOUSE_CLICKED, this.onClick());
		// Notify the model controller that a cell changed, meaning other cells might
		// need to change too (i.e. un-selecting other cells, or eliminating candidates
		// which are not allowed now).
		ModelController.getInstance().transitionToSelectionChangedState(this.activeState);
	}

	public void unselect(boolean mouseInitiated, boolean shouldRefocus) {
		if (shouldRefocus) {
			this.getParent().requestFocus();
		}
		if (mouseInitiated) {
			this.setActiveState(new ManuallyInactiveCellState(this.getActiveState()));
		} else {
			this.setActiveState(new AutomaticallyInactiveCellState(this.getActiveState()));
		}
	}

	/**
	 * Shows the candidates pane if showCandidates is true, shows the fixed digit
	 * pane otherwise.
	 */
	public void setCandidatesVisible(boolean showCandidates) {
		final ObservableList<Node> children = this.getChildren();
		children.get(0).setVisible(showCandidates);
		children.get(1).setVisible(!showCandidates);
	}

	/**
	 * Sets the candidate at the given index visible based on the passed boolean.
	 * Note that the digitIndex is one less than the digit (i.e. index of 1 is 0).
	 */
	public void setCandidateVisible(int pressedDigitIndex, boolean visible) {
		this.candidateLabels[pressedDigitIndex].setVisible(visible);
	}

	/** Sets the fixed digit's value. */
	public void setFixedDigit(String digit) {
		this.fixedDigitLabel.setText(digit);
	}

	private void configure() {
		this.setEventHandler(KeyEvent.KEY_PRESSED, this.onKeyPressed());
		this.setEventHandler(MouseEvent.MOUSE_CLICKED, this.onClick());
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
		return event -> {
			this.activeState.handleClick(event);
		};
	}

	private EventHandler<KeyEvent> onKeyPressed() {
		return event -> {
			this.activeState.handleKeyPressed(event);
			this.actionState.handleKeyPressed(event);
		};

	}
}
