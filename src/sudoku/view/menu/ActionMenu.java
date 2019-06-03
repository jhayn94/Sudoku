package sudoku.view.menu;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.SeparatorMenuItem;
import sudoku.factories.MenuFactory;

/**
 * Contains code to create a menu of different actions the user can do (save,
 * close, etc.).
 */
public class ActionMenu extends ContextMenu {

	public ActionMenu() {
		this.createChildElements();
	}

	private void createChildElements() {
		final Menu fileMenu = MenuFactory.getInstance().createFileMenu();
		final Menu editMenu = MenuFactory.getInstance().createEditMenu();
		final Menu settingsMenu = MenuFactory.getInstance().createSettingsMenu();
		final Menu helpMenu = MenuFactory.getInstance().createHelpMenu();
		this.getItems().addAll(fileMenu, editMenu, settingsMenu, new SeparatorMenuItem(), helpMenu);
	}

}
