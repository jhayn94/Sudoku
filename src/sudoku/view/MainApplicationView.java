package sudoku.view;

import javafx.geometry.Orientation;
import javafx.scene.control.SplitPane;
import sudoku.factories.LayoutFactory;
import sudoku.view.hint.HintPane;
import sudoku.view.puzzle.SudokuPuzzleView;

/**
 * This class represents the main content view of the application.
 */
public class MainApplicationView extends SplitPane {

	private static final String CSS_CLASS = "sudoku-main-app-view";

	public MainApplicationView() {
		super();
		this.configure();
	}

	private void configure() {
		this.getStyleClass().add(CSS_CLASS);
		this.setOrientation(Orientation.HORIZONTAL);
		this.createChildElements();
	}

	private void createChildElements() {
		final ApplicationSideBar sideBarView = LayoutFactory.getInstance().createApplicationSideBar();
		final SudokuPuzzleView sudokuPuzzleView = LayoutFactory.getInstance().createSudokuPuzzleView();
		final HintPane hintPane = LayoutFactory.getInstance().createHintPane();
		this.getItems().addAll(sideBarView, sudokuPuzzleView, hintPane);
	}
}
