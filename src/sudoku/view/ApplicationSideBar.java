package sudoku.view;

import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import sudoku.factories.LayoutFactory;

/**
 * This class corresponds to the view on the left side of the screen. It
 * contains all other view elements on this side of the application.
 */
public class ApplicationSideBar extends SplitPane {

	private static final String CSS_STYLE_CLASS = "sudoku-side-bar";

	public ApplicationSideBar() {
		this.configure();
	}

	private void configure() {
		this.setOrientation(Orientation.VERTICAL);
		this.getStyleClass().add(CSS_STYLE_CLASS);
		this.setMinWidth(320);
		this.setMaxWidth(320);
		this.createChildElements();
	}

	private void createChildElements() {
		final ObservableList<Node> items = this.getItems();
		final NumericButtonPane numericButtonPane = LayoutFactory.getInstance().createNumericButtonPane();
		items.add(numericButtonPane);
	}
}
