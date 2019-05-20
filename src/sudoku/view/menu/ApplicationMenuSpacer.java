package sudoku.view.menu;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import sudoku.view.util.LabelConstants;

/**
 * This class fills the gap between two elements in an HBox. It is used by
 * ApplicationTitleBar to have a left and a right menu.
 */
public class ApplicationMenuSpacer extends Region {

	private static final String MENU_BAR_CSS_CLASS = "menu-bar";

	public ApplicationMenuSpacer() {
		this.configure();
	}

	private void configure() {
		this.setMaxHeight(50);
		this.getStyleClass().add(MENU_BAR_CSS_CLASS);
		final Label title = new Label(LabelConstants.APPLICATION_TITLE);
		title.setPadding(new Insets(10, 0, 0, 0));
		this.getChildren().add(title);
	}
}
