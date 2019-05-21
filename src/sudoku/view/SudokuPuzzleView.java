package sudoku.view;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

/**
 * This class corresponds to the view on the left side of the screen. It
 * contains all other view elements on this side of the application.
 */
public class SudokuPuzzleView extends GridPane {

	private static final String CSS_CLASS = "sudoku-transparent-pane";

	private static final int DEFAULT_WIDTH = 320;

	private static final int NUM_CELLS = 81;

	private static final int MIN_CELL_HEIGHT = 60;

	private static final int MIN_CELL_WIDTH = 60;

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
			final Pane test = new Pane();
			test.setOnKeyTyped(this.test());
			final Label text = new Label("1");
			text.setPadding(new Insets(5));
			test.setMinHeight(MIN_CELL_HEIGHT);
			test.setMinWidth(MIN_CELL_WIDTH);
			test.getChildren().add(text);
			test.getStyleClass().add("sudoku-puzzle-cell");
			// Integer division intentional!
			this.add(test, (index - 1) % 9, (index - 1) / 9);
		}
	}

	private EventHandler<KeyEvent> test() {
		// TODO - rework, this doesn't work at all. Maybe each cell should be a grid
		// pane?
		return event -> {
			final String character = event.getCharacter();
			final Pane eventSource = (Pane) event.getSource();
			final Label text = (Label) eventSource.getChildren().get(0);
			text.setText(character);
		};
	}
}
