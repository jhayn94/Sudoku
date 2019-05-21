package sudoku.view;

import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import sudoku.factories.LayoutFactory;
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
		final ObservableList<Node> items = this.getItems();
		final ApplicationSideBar sideBarView = LayoutFactory.getInstance().createApplicationSideBar();
		final SudokuPuzzleView sudokuPuzzleView = LayoutFactory.getInstance().createSudokuPuzzleView();
		items.add(sideBarView);
		items.add(sudokuPuzzleView);
	}
}
