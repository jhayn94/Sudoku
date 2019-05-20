package sudoku.view.menu;

import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import sudoku.factories.MenuFactory;
import sudoku.view.menu.button.ApplicationMenuButtonType;

/** A menu bar with buttons to minimize, maximum, and close the window. */
public class SystemMenu extends HBox {

	private static final String MENU_BAR_CSS_CLASS = "menu-bar";

	public SystemMenu() {
		this.configure();
	}

	private void configure() {
		this.getStyleClass().add(MENU_BAR_CSS_CLASS);
		this.setMaxHeight(50);
		this.createChildElements();
	}

	private void createChildElements() {
		final Button minimizeMenuButton = MenuFactory.getInstance()
				.createApplicationMenuButton(ApplicationMenuButtonType.MINIMIZE);
		final Button maximizeButton = MenuFactory.getInstance()
				.createApplicationMenuButton(ApplicationMenuButtonType.MAXIMIZE);
		final Button closeMenuButton = MenuFactory.getInstance()
				.createApplicationMenuButton(ApplicationMenuButtonType.CLOSE);
		this.getChildren().addAll(minimizeMenuButton, maximizeButton, closeMenuButton);
	}

}
