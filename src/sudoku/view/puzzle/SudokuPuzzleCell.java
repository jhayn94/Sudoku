package sudoku.view.puzzle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
import sudoku.state.cell.DefaultSudokuCellState;

/** This class corresponds to a single cell of a sudoku puzzle. */
public class SudokuPuzzleCell extends StackPane {

	private static final Logger LOG = LogManager.getLogger(SudokuPuzzleCell.class);

	private static final String CSS_CLASS = "sudoku-puzzle-cell";

	private static final int CELL_HEIGHT = 60;

	private static final int CELL_WIDTH = 60;

	private static final int MAX_NUM_CANDIDATES_IN_CELL = 9;

	private final Label[] candidateLabels;

	private Label fixedDigitLabel;

	private DefaultSudokuCellState state;

	public SudokuPuzzleCell() {
		super();
		this.candidateLabels = new Label[MAX_NUM_CANDIDATES_IN_CELL];
		this.fixedDigitLabel = null;
		this.state = new DefaultSudokuCellState(this);
		this.configure();
	}

	public void setState(DefaultSudokuCellState newState) {
		this.state = newState;
		this.setEventHandler(KeyEvent.KEY_PRESSED, this.onKeyPress());
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
		this.setEventHandler(KeyEvent.KEY_PRESSED, this.onKeyPress());
		this.setOnMouseClicked(this.onClick());
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
//		candidatesGridPane.setPadding(new Insets(3, 0, 0, 6));
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
//		this.fixedDigitLabel.setPadding(new Insets(7.5, 10, 10, 17.5));
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
