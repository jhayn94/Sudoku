package sudoku.view.menu;

import java.io.File;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import sudoku.core.HodokuFacade;
import sudoku.core.ModelController;
import sudoku.core.ViewController;
import sudoku.view.ModalStage;
import sudoku.view.util.LabelConstants;

public class FileMenu extends Menu {

	public FileMenu() {
		super();
		this.configure();
	}

	private void configure() {
		this.setText(LabelConstants.FILE);
		this.createChildElements();
	}

	private void createChildElements() {
		final MenuItem newPuzzleMenuItem = this.createNewPuzzleMenuItem();
		final MenuItem newBlankPuzzleMenuItem = this.createNewBlankPuzzleMenuItem();
		final MenuItem openPuzzleMenuItem = new MenuItem(LabelConstants.OPEN);
		openPuzzleMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
		openPuzzleMenuItem.setOnAction(event -> {
			this.onOpenPuzzle();
		});
		final MenuItem savePuzzleMenuItem = new MenuItem(LabelConstants.SAVE);
		savePuzzleMenuItem.setOnAction(event -> {
			this.onSavePuzzle();
		});
		final MenuItem closeMenuItem = new MenuItem(LabelConstants.CLOSE);
		closeMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.F4, KeyCombination.ALT_DOWN));
		closeMenuItem.setOnAction(event -> ModelController.getInstance().transitionToClosedState());
		this.getItems().addAll(newPuzzleMenuItem, newBlankPuzzleMenuItem, new SeparatorMenuItem(), openPuzzleMenuItem,
				savePuzzleMenuItem, new SeparatorMenuItem(), closeMenuItem);
	}

	private MenuItem createNewBlankPuzzleMenuItem() {
		final MenuItem newBlankPuzzleMenuItem = new MenuItem(LabelConstants.NEW_BLANK_PUZZLE);
		newBlankPuzzleMenuItem.setOnAction(event -> {
			ModelController.getInstance().transitionToNewEmptyPuzzleState();
		});
		newBlankPuzzleMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.ALT_DOWN));
		return newBlankPuzzleMenuItem;
	}

	private MenuItem createNewPuzzleMenuItem() {
		final MenuItem newPuzzleMenuItem = new MenuItem(LabelConstants.NEW_PUZZLE);
		newPuzzleMenuItem.setOnAction(event -> {
			final String generateSudokuString = HodokuFacade.getInstance().generateSudokuString();
			ModelController.getInstance().transitionToNewRandomPuzzleState(generateSudokuString);
		});
		newPuzzleMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
		return newPuzzleMenuItem;
	}

	private void onOpenPuzzle() {
		final ModalStage modalStage = new ModalStage();
		final FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle(LabelConstants.OPEN_FILE);
		fileChooser.setSelectedExtensionFilter(new ExtensionFilter("A sudoku puzzle file.", ".spf"));
		final File selectedFile = fileChooser.showOpenDialog(modalStage);
		if (selectedFile != null) {
			ModelController.getInstance().transitionToOpenedFileState(selectedFile);
		}
	}

	private void onSavePuzzle() {
		final FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle(LabelConstants.SAVE_FILE);
		fileChooser.setSelectedExtensionFilter(new ExtensionFilter("A sudoku puzzle file.", ".spf"));
		final File selectedFile = fileChooser.showSaveDialog(ViewController.getInstance().getStage());
		ModelController.getInstance().transitionToSavedFileState(selectedFile);
	}
}
