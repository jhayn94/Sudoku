package sudoku.core;

import javafx.scene.control.Button;
import javafx.stage.Stage;
import sudoku.view.NumericButtonPane;
import sudoku.view.SudokuPuzzleView;
import sudoku.view.menu.button.MaximizeMenuButton;

/**
 * A controller class to facilitate view changes, as result of a model change.
 * This class stores references to key existing views for updating. A reference
 * to static (container) views is not stored.
 */
public class ViewController {

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

	private ViewController() {
		this.stage = null;
		this.maximizeWindowButton = null;
		this.helpStage = null;
		this.numericButtonPane = null;
		this.sudokuPuzzleView = null;
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

}
