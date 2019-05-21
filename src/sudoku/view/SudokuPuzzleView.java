package sudoku.view;

import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

/**
 * This class corresponds to the view on the left side of the screen. It
 * contains all other view elements on this side of the application.
 */
public class SudokuPuzzleView extends GridPane {

	private static final String CSS_CLASS = "sudoku-transparent-pane";

	private static final int DEFAULT_WIDTH = 320;

	private static final int NUM_CELLS = 81;

	private static final int MIN_CELL_HEIGHT = 50;

	private static final int MIN_CELL_WIDTH = 50;

	public SudokuPuzzleView() {
		this.configure();
	}

	private void configure() {
		this.getStyleClass().add(CSS_CLASS);
		this.setPadding(new Insets(25));
		this.setMinWidth(DEFAULT_WIDTH);
//		this.setMaxWidth(DEFAULT_WIDTH);
		this.createChildElements();
	}

	private void createChildElements() {
		for (int index = 1; index <= NUM_CELLS; index++) {
			final Pane test = new Pane();
			test.setPadding(new Insets(25));
			final Text text = new Text("1");
			test.setMinHeight(MIN_CELL_HEIGHT);
			test.setMinWidth(MIN_CELL_WIDTH);
			test.getChildren().add(text);
			test.getStyleClass().add("sudoku-puzzle-cell");
			// Integer division intentional!
			this.add(test, (index - 1) % 9, (index - 1) / 9);
		}
	}
}
