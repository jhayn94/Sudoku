package sudoku.view;

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import sudoku.factories.LayoutFactory;
import sudoku.view.sidebar.FilterButtonPane;
import sudoku.view.sidebar.MouseModePane;
import sudoku.view.sidebar.ControlHelperPane;

/**
 * This class corresponds to the view on the left side of the screen. It
 * contains all other view elements on this side of the application.
 */
public class ApplicationSideBar extends VBox {

	private static final int DEFAULT_WIDTH = 320;

	private static final String CSS_STYLE_CLASS = "sudoku-side-bar";

	public ApplicationSideBar() {
		this.configure();
	}

	private void configure() {
		this.setAlignment(Pos.TOP_CENTER);
		this.getStyleClass().add(CSS_STYLE_CLASS);
		this.setMinWidth(DEFAULT_WIDTH);
		this.setMaxWidth(DEFAULT_WIDTH);
		this.createChildElements();
	}

	private void createChildElements() {
		final FilterButtonPane filterButtonPane = LayoutFactory.getInstance().createFilterButtonPane();
		final MouseModePane mouseModePane = LayoutFactory.getInstance().createMouseModePane();
		final ControlHelperPane mouseToolsPane = LayoutFactory.getInstance().createControlHelperPane();
		final ObservableList<Node> children = this.getChildren();
		children.addAll(filterButtonPane, mouseModePane, mouseToolsPane);
	}
}
