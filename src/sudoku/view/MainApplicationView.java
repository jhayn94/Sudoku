package sudoku.view;

import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import sudoku.factories.LayoutFactory;

/**
 * This class represents the main content view of the application.
 */
public class MainApplicationView extends SplitPane {

	public MainApplicationView() {
		super();
		this.configure();
	}

	private void configure() {
		this.getStyleClass().add("sudoku-main-app-view");
		this.setOrientation(Orientation.HORIZONTAL);
		this.createChildElements();
	}

	private void createChildElements() {
		final ObservableList<Node> items = this.getItems();
		final ApplicationSideBar sideBarView = LayoutFactory.getInstance().createApplicationSideBar();
		items.add(sideBarView);
	}
}
