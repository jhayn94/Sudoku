package sudoku.view.puzzle;

import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

public class SudokuPuzzleCell extends GridPane {

	private static final String CSS_CLASS = "sudoku-puzzle-cell";

	private static final int MIN_CELL_HEIGHT = 60;

	private static final int MIN_CELL_WIDTH = 60;

	private static final int MAX_NUM_CANDIDATES_IN_CELL = 9;

	public SudokuPuzzleCell() {
		super();
		this.configure();
	}

	private void configure() {
		this.setHgap(7);
		this.setVgap(0);
		// TODO - spacing of candidate labels is still slightly off center for each
		// cell.
		this.setOnKeyTyped(this.test());
		this.setOnMouseClicked(this.onClick());
		for (int index = 1; index <= MAX_NUM_CANDIDATES_IN_CELL; index++) {
			final Label candidateLabel = new Label(String.valueOf(index));
			// Integer division intentional!
			this.add(candidateLabel, (index - 1) % 3, (index - 1) / 3);
		}
		this.setMinHeight(MIN_CELL_HEIGHT);
		this.setMinWidth(MIN_CELL_WIDTH);
		this.getStyleClass().add(CSS_CLASS);
	}

	private EventHandler<MouseEvent> onClick() {
		// TODO extract the gridpane into a SudokuCell class.
		// TODO - rework, this doesn't work at all. Maybe each cell should be a grid
		// pane?
		return event -> {
			final GridPane eventSource = (GridPane) event.getSource();
			final Label text = (Label) eventSource.getChildren().get(0);
			text.setText("hello");
		};
	}

	private EventHandler<KeyEvent> test() {
		// TODO - rework, this doesn't work at all. Maybe each cell should be a grid
		// pane?
		return event -> {
			final String character = event.getCharacter();
			final GridPane eventSource = (GridPane) event.getSource();
			final Label text = (Label) eventSource.getChildren().get(0);
			text.setText(character);
		};
	}
}
