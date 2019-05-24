package sudoku.core;

import javafx.scene.control.Button;
import javafx.stage.Stage;
import sudoku.model.SudokuPuzzle;
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

	private static ViewController instance;

	public static ViewController getInstance() {
		if (ViewController.instance == null) {
			ViewController.instance = new ViewController();
		}
		return ViewController.instance;
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
		this.sudokuPuzzleCells = new SudokuPuzzleCell[SudokuPuzzle.NUMBER_OF_CELLS_PER_DIMENSION][SudokuPuzzle.NUMBER_OF_CELLS_PER_DIMENSION];
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

	public SudokuPuzzleCell getSudokuPuzzleCell(final int row, final int col) {
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

	public void setNumericButtonPane(final NumericButtonPane numericButtonPane) {
		this.numericButtonPane = numericButtonPane;
	}

	public void setSudokuPuzzleView(final SudokuPuzzleView sudokuPuzzleView) {
		this.sudokuPuzzleView = sudokuPuzzleView;
	}

	public void registerSudokuPuzzleCell(final SudokuPuzzleCell sudokuPuzzleCell, final int col, final int row) {
		this.sudokuPuzzleCells[col][row] = sudokuPuzzleCell;
	}

}
