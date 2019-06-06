package sudoku.factories;

import java.lang.reflect.InvocationTargetException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.scene.control.Menu;
import javafx.stage.Stage;
import sudoku.view.menu.ActionMenu;
import sudoku.view.menu.ApplicationMenu;
import sudoku.view.menu.ApplicationMenuSpacer;
import sudoku.view.menu.ApplicationTitleBar;
import sudoku.view.menu.EditMenu;
import sudoku.view.menu.FileMenu;
import sudoku.view.menu.HelpMenu;
import sudoku.view.menu.PuzzleMenu;
import sudoku.view.menu.SettingsMenu;
import sudoku.view.menu.SystemMenu;
import sudoku.view.menu.button.AbstractMenuButton;
import sudoku.view.menu.button.ApplicationMenuButtonType;

/**
 * This class contains methods to create menus and their subcomponents.
 */
public class MenuFactory {

	private static final Logger LOG = LogManager.getLogger(MenuFactory.class);

	private static MenuFactory menuFactoryInstance;

	public static MenuFactory getInstance() {
		if (menuFactoryInstance == null) {
			menuFactoryInstance = new MenuFactory();
		}
		return menuFactoryInstance;
	}

	public ApplicationTitleBar createApplicationTitleBar(final Stage stage) {
		return new ApplicationTitleBar(stage);
	}

	public SystemMenu createSystemMenu() {
		return new SystemMenu();
	}

	public ApplicationMenu createApplicationMenu() {
		return new ApplicationMenu();
	}

	public ActionMenu createActionMenu() {
		return new ActionMenu();
	}

	public FileMenu createFileMenu() {
		return new FileMenu();
	}

	public EditMenu createEditMenu() {
		return new EditMenu();
	}

	public PuzzleMenu createPuzzleMenu() {
		return new PuzzleMenu();
	}

	public Menu createSettingsMenu() {
		return new SettingsMenu();
	}

	public Menu createHelpMenu() {
		return new HelpMenu();
	}

	public ApplicationMenuSpacer createApplicationMenuSpacer() {
		return new ApplicationMenuSpacer();
	}

	public AbstractMenuButton createApplicationMenuButton(final ApplicationMenuButtonType type) {
		try {
			return (AbstractMenuButton) type.getClassType().getDeclaredConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			LOG.error(e.getMessage());
		}
		return null;
	}

	private MenuFactory() {
		// Private constructor to prevent external instantiation.
	}

}
