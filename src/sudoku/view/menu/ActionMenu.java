package sudoku.view.menu;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import sudoku.core.ModelController;
import sudoku.core.ViewController;
import sudoku.factories.LayoutFactory;
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

		final MenuItem helpMenuItem = new MenuItem(LabelConstants.HELP);
		helpMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.H, KeyCombination.SHORTCUT_DOWN));
		helpMenuItem.setOnAction(event -> LayoutFactory.getInstance().showHelpView());

		final MenuItem minimizeMenuItem = new MenuItem(LabelConstants.MINIMIZE);
		minimizeMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.M, KeyCombination.SHORTCUT_DOWN));
		minimizeMenuItem.setOnAction(event -> ModelController.getInstance().transitionToMinimizedState());

		final MenuItem maximizeMenuItem = new MenuItem(LabelConstants.RESTORE);
		maximizeMenuItem.setOnAction(event -> {
			if (ViewController.getInstance().getStage().isMaximized()) {
				maximizeMenuItem.setText(LabelConstants.MAXIMIZE);
				ModelController.getInstance().transitionToRestoredState();
			} else {
				maximizeMenuItem.setText(LabelConstants.RESTORE);
				ModelController.getInstance().transitionToMaximizedState();
			}
		});

		final MenuItem close = new MenuItem(LabelConstants.CLOSE);
		close.setAccelerator(new KeyCodeCombination(KeyCode.F4, KeyCombination.ALT_DOWN));
		close.setOnAction(event -> ModelController.getInstance().transitionToClosedState());
		this.getItems().addAll(helpMenuItem, new SeparatorMenuItem(), new SeparatorMenuItem(), minimizeMenuItem,
				maximizeMenuItem, new SeparatorMenuItem(), close);
	}
}
