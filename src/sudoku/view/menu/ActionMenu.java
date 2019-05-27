package sudoku.view.menu;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import sudoku.factories.LayoutFactory;
import sudoku.factories.MenuFactory;
import sudoku.view.util.LabelConstants;

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
		final Menu settingsMenu = this.createSettingsMenu();
		final MenuItem helpMenuItem = new MenuItem(LabelConstants.HELP);
		helpMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.H, KeyCombination.SHORTCUT_DOWN));
		helpMenuItem.setOnAction(event -> LayoutFactory.getInstance().showHelpView());
		this.getItems().addAll(fileMenu, editMenu, settingsMenu, new SeparatorMenuItem(), helpMenuItem);
	}

	private Menu createSettingsMenu() {
		final Menu settingsMenu = new Menu(LabelConstants.SETTINGS);
		final MenuItem puzzleGenerationMenuItem = new MenuItem(LabelConstants.PUZZLE_GENERATION);
		final MenuItem difficultyMenuItem = new MenuItem(LabelConstants.DIFFICULTY);
		final MenuItem solverMenuItem = new MenuItem(LabelConstants.SOLVER);
		final MenuItem colorsMenuItem = new MenuItem(LabelConstants.COLORS);
		final MenuItem miscellaneousMenuItem = new MenuItem(LabelConstants.MISCELLANEOUS);
		settingsMenu.getItems().addAll(puzzleGenerationMenuItem, difficultyMenuItem, solverMenuItem, colorsMenuItem,
				new SeparatorMenuItem(), miscellaneousMenuItem);
		return settingsMenu;
	}
}
