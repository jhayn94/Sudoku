package sudoku.view.puzzle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import sudoku.state.cell.DefaultSudokuCellState;

/** This class corresponds to a single cell of a sudoku puzzle. */
public class SudokuPuzzleCell extends Pane {

	private static final Logger LOG = LogManager.getLogger(SudokuPuzzleCell.class);

	private static final String CSS_CLASS = "sudoku-puzzle-cell";

	private static final int MIN_CELL_HEIGHT = 60;

	private static final int MIN_CELL_WIDTH = 60;

	private static final int MAX_NUM_CANDIDATES_IN_CELL = 9;

	private final Label[] candidateLabels;

	private Label fixedDigitLabel;

	private final DefaultSudokuCellState state;

	public SudokuPuzzleCell() {
		super();
		this.candidateLabels = new Label[MAX_NUM_CANDIDATES_IN_CELL];
		this.fixedDigitLabel = null;
		this.state = new DefaultSudokuCellState(this);
		this.configure();
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
		// TODO - spacing of candidate labels is still slightly off center for each
		// cell.
		this.addEventHandler(KeyEvent.KEY_PRESSED, this.onKeyPress());
		this.setOnMouseClicked(this.onClick());
		this.setMinHeight(MIN_CELL_HEIGHT);
		this.setMinWidth(MIN_CELL_WIDTH);
		this.getStyleClass().add(CSS_CLASS);
		this.createChildElements();
	}

	private void createChildElements() {
		final ObservableList<Node> children = this.getChildren();
		final GridPane candidatesGridPane = new GridPane();
		candidatesGridPane.setHgap(7);
		candidatesGridPane.setVgap(0);
		for (int index = 1; index <= MAX_NUM_CANDIDATES_IN_CELL; index++) {
			this.candidateLabels[index - 1] = new Label(String.valueOf(index));
			// Integer division intentional!
			candidatesGridPane.add(this.candidateLabels[index - 1], (index - 1) % 3, (index - 1) / 3);
		}
		final Pane fixedDigitPane = new Pane();
		this.fixedDigitLabel = new Label();
		fixedDigitPane.getChildren().add(this.fixedDigitLabel);
		fixedDigitPane.setVisible(false);
		children.add(candidatesGridPane);
		children.add(fixedDigitPane);
	}

	private EventHandler<MouseEvent> onClick() {
		return event -> {
			this.requestFocus();
			// TODO - update CSS class to change border to something else. Don't forget to
			// remove all other focus CSS.
		};
	}

	private EventHandler<KeyEvent> onKeyPress() {
		// TODO - make this a state so we can share model changes.
		return this.state.handleKeyPress();

	}
}
