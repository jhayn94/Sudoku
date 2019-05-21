package sudoku.view.puzzle;

import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;
import sudoku.factories.LayoutFactory;

/**
 * This class corresponds to the view on the left side of the screen. It
 * contains all other view elements on this side of the application.
 */
public class SudokuPuzzleView extends GridPane {

	private static final String CSS_CLASS = "sudoku-transparent-pane";

	private static final int DEFAULT_WIDTH = 320;

	private static final int NUM_CELLS = 81;

	public SudokuPuzzleView() {
		this.configure();
	}

	private void configure() {
		this.getStyleClass().add(CSS_CLASS);
		this.setPadding(new Insets(25));
		this.setMinWidth(DEFAULT_WIDTH);
		this.createChildElements();
	}

	private void createChildElements() {
		for (int index = 1; index <= NUM_CELLS; index++) {
			final int rowIndex = (index - 1) / 9;
			final int colIndex = (index - 1) % 9;
			final SudokuPuzzleCell sudokuPuzzleCell = LayoutFactory.getInstance().createSudokuPuzzleCell(colIndex,
					rowIndex);
			// TODO - update border thickness based on indices.
			// Integer division intentional!
			this.add(sudokuPuzzleCell, colIndex, rowIndex);
		}
	}
}
