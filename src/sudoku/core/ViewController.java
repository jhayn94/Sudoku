package sudoku.core;

import javafx.scene.control.Button;
import javafx.stage.Stage;
import sudoku.view.NumericButtonPane;
import sudoku.view.menu.button.MaximizeMenuButton;
import sudoku.view.puzzle.SudokuPuzzleCell;
import sudoku.view.puzzle.SudokuPuzzleView;

/**
 * A controller class to facilitate view changes, as result of a model change.
 * This class stores references to key existing views for updating. A reference
 * to static (container) views is not stored.
 */
public class ViewController {

	private static final int NUM_CELLS = 9;

	private static ViewController viewControllerInstance;

	public static ViewController getInstance() {
		if (viewControllerInstance == null) {
			viewControllerInstance = new ViewController();
		}
		return viewControllerInstance;
	}

	private Stage stage;

	private Button maximizeWindowButton;

	private Stage helpStage;

	private NumericButtonPane numericButtonPane;

	private SudokuPuzzleView sudokuPuzzleView;

	private final SudokuPuzzleCell[][] sudokuPuzzleCells;

	private ViewController() {
		this.stage = null;
		this.maximizeWindowButton = null;
		this.helpStage = null;
		this.numericButtonPane = null;
		this.sudokuPuzzleView = null;
		this.sudokuPuzzleCells = new SudokuPuzzleCell[NUM_CELLS][NUM_CELLS];
	}

	public Stage getStage() {
		return this.stage;
	}

	public Button getMaximizeWindowButton() {
		return this.maximizeWindowButton;
	}

	public Stage getHelpStage() {
		return this.helpStage;
	}

	public NumericButtonPane getNumericButtonPane() {
		return this.numericButtonPane;
	}

	public SudokuPuzzleView getSudokuPuzzleView() {
		return this.sudokuPuzzleView;
	}

	public SudokuPuzzleCell getSudokuPuzzleCell(int col, int row) {
		return this.sudokuPuzzleCells[col][row];
	}

	public void setStage(final Stage stage) {
		this.stage = stage;
	}

	public void setMaximizeWindowButton(final MaximizeMenuButton maximizeMenuButton) {
		this.maximizeWindowButton = maximizeMenuButton;
	}

	public void setHelpStage(final Stage helpStage) {
		this.helpStage = helpStage;
	}

	public void setNumericButtonPane(NumericButtonPane numericButtonPane) {
		this.numericButtonPane = numericButtonPane;
	}

	public void setSudokuPuzzleView(SudokuPuzzleView sudokuPuzzleView) {
		this.sudokuPuzzleView = sudokuPuzzleView;
	}

	public void registerSudokuPuzzleCell(SudokuPuzzleCell sudokuPuzzleCell, int col, int row) {
		this.sudokuPuzzleCells[col][row] = sudokuPuzzleCell;
	}

}
