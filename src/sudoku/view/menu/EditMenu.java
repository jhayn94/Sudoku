package sudoku.view.menu;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import sudoku.core.ModelController;
import sudoku.core.ViewController;
import sudoku.view.util.LabelConstants;

public class EditMenu extends Menu {

	public EditMenu() {
		super();
		this.configure();
	}

	private void configure() {
		this.setText(LabelConstants.EDIT);
		this.createChildElements();
	}

	private void createChildElements() {
		final MenuItem undoMenuItem = new MenuItem(LabelConstants.UNDO_LONG);
		undoMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN));
		undoMenuItem.setOnAction(event -> ModelController.getInstance().transitionToUndoActionState());
		ViewController.getInstance().setUndoMenuItem(undoMenuItem);

		final MenuItem redoMenuItem = new MenuItem(LabelConstants.REDO_LONG);
		redoMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.Y, KeyCombination.CONTROL_DOWN));
		redoMenuItem.setOnAction(event -> ModelController.getInstance().transitionToRedoActionState());
		ViewController.getInstance().setRedoMenuItem(redoMenuItem);

		final MenuItem copyCellsMenuItem = new MenuItem(LabelConstants.COPY_CELLS);
		copyCellsMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN));
		copyCellsMenuItem.setOnAction(event -> ModelController.getInstance().transitionToCopyPuzzleState(false));

		final MenuItem copyGivensMenuItem = new MenuItem(LabelConstants.COPY_GIVENS);
		copyGivensMenuItem
				.setAccelerator(new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN));
		copyGivensMenuItem.setOnAction(event -> ModelController.getInstance().transitionToCopyPuzzleState(true));

		final MenuItem pasteMenuItem = new MenuItem(LabelConstants.PASTE);
		pasteMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_DOWN));
		pasteMenuItem.setOnAction(event -> ModelController.getInstance().transitionToPastePuzzleState());

		final MenuItem setGivensMenuItem = new MenuItem(LabelConstants.SET_GIVENS);
		setGivensMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN));
		setGivensMenuItem.setOnAction(event -> ModelController.getInstance().transitionToSetGivenCellsState());

		final MenuItem restartMenuItem = new MenuItem(LabelConstants.RESTART);
		restartMenuItem.setOnAction(event -> ModelController.getInstance().transitionToRestartPuzzleState());

		this.getItems().addAll(undoMenuItem, redoMenuItem, new SeparatorMenuItem(), copyCellsMenuItem, copyGivensMenuItem,
				pasteMenuItem, new SeparatorMenuItem(), setGivensMenuItem, restartMenuItem);
	}
}
