package sudoku.view.menu;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Platform;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import sudoku.Options;
import sudoku.SolutionType;
import sudoku.StepConfig;
import sudoku.core.HodokuFacade;
import sudoku.core.ModelController;
import sudoku.core.ViewController;
import sudoku.factories.LayoutFactory;
import sudoku.model.ApplicationSettings;
import sudoku.view.dialog.ModalStage;
import sudoku.view.dialog.WaitingDialog;
import sudoku.view.util.Difficulty;
import sudoku.view.util.LabelConstants;

public class FileMenu extends Menu {

	private static final Logger LOG = LogManager.getLogger(FileMenu.class);

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

			final boolean stepLevelTooHigh = this.isStepLevelTooHigh();
			final boolean requiredStepEnabled = this.isRequiredStepEnabled();
			final boolean isOverScoreLimit = this.isOverScoreLimit();
			if (stepLevelTooHigh) {
				LayoutFactory.getInstance().showMessageDialog(LabelConstants.INVALID_SETTINGS,
						LabelConstants.STEP_HARDER_THAN_PUZZLE_DIFFICULTY);
			} else if (!requiredStepEnabled) {
				LayoutFactory.getInstance().showMessageDialog(LabelConstants.INVALID_SETTINGS, LabelConstants.STEP_INACTIVE);
			} else if (isOverScoreLimit) {
				LayoutFactory.getInstance().showMessageDialog(LabelConstants.INVALID_SETTINGS, LabelConstants.OVER_SCORE_LIMIT);
			} else {
				this.generateNewPuzzle();
			}

		});
		newPuzzleMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
		return newPuzzleMenuItem;
	}

	private void generateNewPuzzle() {
		final WaitingDialog waitingDialog = LayoutFactory.getInstance()
				.createWaitingDialog(LabelConstants.GENERATING_PUZZLE_TITLE, LabelConstants.GENERATING_PUZZLE_MESSAGE);
		final Thread puzzleGenerationThread = new Thread(() -> this.createNewPuzzle(waitingDialog));
		waitingDialog.setExecutionThread(puzzleGenerationThread);
		LayoutFactory.getInstance().showNewStageWithRootElement(waitingDialog.getStage(), waitingDialog,
				LayoutFactory.MESSAGE_DIALOG_WIDTH, LayoutFactory.MESSAGE_DIALOG_HEIGHT);
		puzzleGenerationThread.start();
	}

	private boolean isStepLevelTooHigh() {
		final Difficulty difficulty = ApplicationSettings.getInstance().getDifficulty();
		final String mustContainStepWithName = ApplicationSettings.getInstance().getMustContainStepWithName();
		final List<StepConfig> solverSteps = Arrays.asList(Options.getInstance().solverSteps);
		final StepConfig requiredStep = solverSteps.stream()
				.filter(solverStep -> solverStep.getType().getStepName().equals(mustContainStepWithName)).findFirst()
				.orElseGet(() -> null);
		return requiredStep != null && requiredStep.getLevel() > difficulty.ordinal() + 1;
	}

	private boolean isRequiredStepEnabled() {
		final String mustContainStepWithName = ApplicationSettings.getInstance().getMustContainStepWithName();
		final SolutionType solutionType = Arrays.asList(SolutionType.values()).stream()
				.filter(tempSolutionType -> mustContainStepWithName.equals(tempSolutionType.getStepName())).findFirst()
				.orElseGet(() -> null);
		if (solutionType != null) {
			final StepConfig stepConfig = SolutionType.getStepConfig(solutionType);
			return stepConfig != null && stepConfig.isEnabled();
		}
		return true;
	}

	private boolean isOverScoreLimit() {
		final String mustContainStepWithName = ApplicationSettings.getInstance().getMustContainStepWithName();
		final List<StepConfig> solverSteps = Arrays.asList(Options.getInstance().solverSteps);
		final StepConfig requiredStep = solverSteps.stream()
				.filter(solverStep -> solverStep.getType().getStepName().equals(mustContainStepWithName)).findFirst()
				.orElseGet(() -> null);
		final Difficulty difficulty = ApplicationSettings.getInstance().getDifficulty();
		return requiredStep != null && requiredStep.getBaseScore() >= ApplicationSettings.getInstance()
				.getMaxScoreForDifficulty(difficulty.getLabel());
	}

	private void createNewPuzzle(final WaitingDialog waitingDialog) {
		try {
			final String generateSudokuString = HodokuFacade.getInstance().generateSudokuString();
			Platform.runLater(() -> {
				ModelController.getInstance().transitionToNewRandomPuzzleState(generateSudokuString);
				waitingDialog.close(false);
			});
		} catch (final Exception e) {
			// Can't do much about these errors without opening up the HoDoKu source code,
			// but directing the user to retry is better than nothing.
			LOG.error("{}", e);
			Platform.runLater(() -> {
				waitingDialog.onGenerationFailed();
			});
		}
	}

	private void onOpenPuzzle() {
		final ModalStage modalStage = new ModalStage();
		final FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle(LabelConstants.OPEN_FILE);
		fileChooser.getExtensionFilters().add(new ExtensionFilter("Sudoku puzzle file", "*.spf"));
		final File selectedFile = fileChooser.showOpenDialog(modalStage);
		if (selectedFile != null) {
			ModelController.getInstance().transitionToOpenedFileState(selectedFile);
		}
	}

	private void onSavePuzzle() {
		final FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle(LabelConstants.SAVE_FILE);
		fileChooser.getExtensionFilters().add(new ExtensionFilter("Sudoku puzzle file", "*.spf"));
		final File selectedFile = fileChooser.showSaveDialog(ViewController.getInstance().getStage());
		ModelController.getInstance().transitionToSavedFileState(selectedFile);
	}
}
