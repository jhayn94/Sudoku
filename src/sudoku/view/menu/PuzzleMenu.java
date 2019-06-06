package sudoku.view.menu;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import sudoku.core.ModelController;
import sudoku.view.util.LabelConstants;

public class PuzzleMenu extends Menu {

	public PuzzleMenu() {
		super();
		this.configure();
	}

	private void configure() {
		this.setText(LabelConstants.PUZZLE);
		this.createChildElements();
	}

	private void createChildElements() {
		final MenuItem vagueHintMenuItem = new MenuItem(LabelConstants.VAGUE_HINT + LabelConstants.HINT_MENU_ITEM_SUFFIX);
		vagueHintMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.F12, KeyCombination.ALT_DOWN));
		vagueHintMenuItem.setOnAction(event -> ModelController.getInstance().transitionToShowVagueHintState());

		final MenuItem partialHintMenuItem = new MenuItem(
				LabelConstants.PARTIAL_HINT + LabelConstants.HINT_MENU_ITEM_SUFFIX);
		partialHintMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.F12, KeyCombination.CONTROL_DOWN));
		partialHintMenuItem.setOnAction(event -> ModelController.getInstance().transitionToShowPartialHintState());

		final MenuItem specificHintMenuItem = new MenuItem(
				LabelConstants.SPECIFIC_HINT + LabelConstants.HINT_MENU_ITEM_SUFFIX);
		specificHintMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.F12));
		specificHintMenuItem.setOnAction(event -> ModelController.getInstance().transitionToShowSpecificHintState());

		final MenuItem fillAllSinglesMenuItem = new MenuItem(LabelConstants.FILL_SINGLES);
		fillAllSinglesMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.F11));
		fillAllSinglesMenuItem.setOnAction(event -> ModelController.getInstance().transitionToFillInSinglesPuzzleState());

		this.getItems().addAll(vagueHintMenuItem, partialHintMenuItem, specificHintMenuItem, new SeparatorMenuItem(),
				fillAllSinglesMenuItem);

	}
}
