package sudoku.view.menu;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import sudoku.factories.LayoutFactory;
import sudoku.view.util.LabelConstants;

public class HelpMenu extends Menu {

	public HelpMenu() {
		super();
		this.configure();
	}

	private void configure() {
		this.setText(LabelConstants.HELP);
		this.createChildElements();
	}

	private void createChildElements() {
		final MenuItem aboutMenuItem = new MenuItem(LabelConstants.ABOUT);
		aboutMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.H, KeyCombination.CONTROL_DOWN));
		aboutMenuItem.setOnAction(event -> LayoutFactory.getInstance().showHelpView());

		final MenuItem hotkeysMenuItem = new MenuItem(LabelConstants.HOTKEYS);
		hotkeysMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.BACK_QUOTE, KeyCombination.SHORTCUT_DOWN));
		hotkeysMenuItem.setOnAction(event -> LayoutFactory.getInstance().showHotkeyView());

		this.getItems().addAll(aboutMenuItem, hotkeysMenuItem);
	}
}
