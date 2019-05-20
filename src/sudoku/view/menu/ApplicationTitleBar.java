package sudoku.view.menu;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import sudoku.factories.MenuFactory;

/**
 * This class emulates a title bar with a left and a right grouping. It is
 * actually an HBox.
 */
public class ApplicationTitleBar extends HBox {

	private final Stage stage;

	public ApplicationTitleBar(final Stage stage) {
		this.stage = stage;
		this.configure();
	}

	public Stage getStage() {
		return this.stage;
	}

	private void configure() {
		this.createChildElements();
	}

	private void createChildElements() {
		final ApplicationMenu leftBar = MenuFactory.getInstance().createApplicationMenu();
		final Region spacer = MenuFactory.getInstance().createApplicationMenuSpacer();
		HBox.setHgrow(spacer, Priority.SOMETIMES);
		final SystemMenu rightBar = MenuFactory.getInstance().createSystemMenu();
		this.getChildren().addAll(leftBar, spacer, rightBar);
	}

}
