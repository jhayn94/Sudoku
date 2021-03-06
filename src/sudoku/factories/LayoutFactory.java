package sudoku.factories;

import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import sudoku.core.ViewController;
import sudoku.view.ApplicationRootPane;
import sudoku.view.ApplicationSideBar;
import sudoku.view.HelpView;
import sudoku.view.HotkeyView;
import sudoku.view.MainApplicationView;
import sudoku.view.RootStackPane;
import sudoku.view.control.LabeledComboBox;
import sudoku.view.control.ToggleButton;
import sudoku.view.dialog.MessageDialog;
import sudoku.view.dialog.ModalDialog;
import sudoku.view.dialog.ModalStage;
import sudoku.view.dialog.WaitingDialog;
import sudoku.view.hint.CurvedHintAnnotation;
import sudoku.view.hint.HintButtonPane;
import sudoku.view.hint.HintPane;
import sudoku.view.hint.HintTextArea;
import sudoku.view.hint.LinearHintAnnotation;
import sudoku.view.puzzle.SudokuPuzzleCell;
import sudoku.view.puzzle.SudokuPuzzleView;
import sudoku.view.settings.ColorSettingsView;
import sudoku.view.settings.DifficultySettingsView;
import sudoku.view.settings.MiscellaneousSettingsView;
import sudoku.view.settings.PuzzleGenerationSettingsView;
import sudoku.view.settings.SolverSettingsView;
import sudoku.view.sidebar.ControlHelperPane;
import sudoku.view.sidebar.FilterButtonPane;
import sudoku.view.sidebar.MouseModePane;
import sudoku.view.sidebar.PuzzleStatsPane;
import sudoku.view.util.ResourceConstants;
import sudoku.view.util.ShadowRectangle;
import sudoku.view.util.WindowHelper;

/**
 * This class contains methods to instantiate all views shown in the
 * application.
 */
public class LayoutFactory {

	private static final int SOLVER_SETTINGS_DIALOG_WIDTH = 730;

	private static final int COLOR_SETTINGS_DIALOG_WIDTH = 745;

	private static final int COLOR_SETTINGS_DIALOG_HEIGHT = 690;

	private static final int DEFAULT_MODAL_DIALOG_HEIGHT = 600;

	private static final int DEFAULT_MODAL_DIALOG_WIDTH = 700;

	public static final double MESSAGE_DIALOG_WIDTH = 500;

	public static final double HOTKEY_DIALOG_WIDTH = 775;

	public static final double MESSAGE_DIALOG_HEIGHT = 250;

	private static LayoutFactory layoutFactoryInstance;

	public static LayoutFactory getInstance() {
		if (layoutFactoryInstance == null) {
			layoutFactoryInstance = new LayoutFactory();
		}
		return layoutFactoryInstance;
	}

	public ApplicationRootPane createApplicationRootPane() {
		final ApplicationRootPane applicationRootPane = new ApplicationRootPane();
		ViewController.getInstance().setRootPane(applicationRootPane);
		return applicationRootPane;
	}

	public RootStackPane createRootStackPane(final Region applicationView) {
		return new RootStackPane(applicationView);
	}

	public MainApplicationView createMainApplicationView() {
		final MainApplicationView mainApplicationView = new MainApplicationView();
		ViewController.getInstance().setMainApplicationView(mainApplicationView);
		return mainApplicationView;
	}

	public ApplicationSideBar createApplicationSideBar() {
		return new ApplicationSideBar();
	}

	public SudokuPuzzleView createSudokuPuzzleView() {
		final SudokuPuzzleView sudokuPuzzleView = new SudokuPuzzleView();
		ViewController.getInstance().setSudokuPuzzleView(sudokuPuzzleView);
		return sudokuPuzzleView;
	}

	public SudokuPuzzleCell createSudokuPuzzleCell(final int col, final int row) {
		final SudokuPuzzleCell sudokuPuzzleCell = new SudokuPuzzleCell(row, col);
		ViewController.getInstance().registerSudokuPuzzleCell(sudokuPuzzleCell, col, row);
		return sudokuPuzzleCell;
	}

	public FilterButtonPane createFilterButtonPane() {
		final FilterButtonPane filterButtonPane = new FilterButtonPane();
		ViewController.getInstance().setFilterButtonPane(filterButtonPane);
		return filterButtonPane;
	}

	public MouseModePane createMouseModePane() {
		return new MouseModePane();
	}

	public ControlHelperPane createControlHelperPane() {
		final ControlHelperPane controlHelperPane = new ControlHelperPane();
		ViewController.getInstance().setControlHelperPane(controlHelperPane);
		return controlHelperPane;
	}

	public PuzzleStatsPane createPuzzleStatsPane() {
		final PuzzleStatsPane puzzleStatsPane = new PuzzleStatsPane();
		ViewController.getInstance().setPuzzleStatsPane(puzzleStatsPane);
		return puzzleStatsPane;
	}

	public HintPane createHintPane() {
		return new HintPane();
	}

	public HintButtonPane createHintButtonPane() {
		final HintButtonPane hintButtonPane = new HintButtonPane();
		ViewController.getInstance().setHintButtonPane(hintButtonPane);
		return hintButtonPane;
	}

	public HintTextArea createHintTextArea() {
		final HintTextArea hintTextArea = new HintTextArea();
		ViewController.getInstance().setHintTextArea(hintTextArea);
		return hintTextArea;
	}

	public ShadowRectangle createShadowRectangle() {
		return new ShadowRectangle();
	}

	public LabeledComboBox createLabeledComboBox() {
		return new LabeledComboBox();
	}

	public ToggleButton createToggleButton(final String label) {
		return new ToggleButton(label);
	}

	public void showHelpView() {
		final Stage stage = new ModalStage();
		final HelpView helpView = new HelpView(stage);
		this.showNewStageWithRootElement(stage, helpView);
	}

	public void showHotkeyView() {
		final Stage stage = new ModalStage();
		final HotkeyView hotkeyView = new HotkeyView(stage);
		this.showNewStageWithRootElement(stage, hotkeyView, HOTKEY_DIALOG_WIDTH, DEFAULT_MODAL_DIALOG_HEIGHT);
	}

	public void showPuzzleGenerationSettingsView() {
		final Stage settingsStage = new ModalStage();
		final PuzzleGenerationSettingsView puzzleGenerationSettingsView = new PuzzleGenerationSettingsView(settingsStage);
		ViewController.getInstance().setPuzzleGenerationSettingsView(puzzleGenerationSettingsView);
		this.showNewStageWithRootElement(settingsStage, puzzleGenerationSettingsView);
	}

	public void showSolverSettingsView() {
		final Stage settingsStage = new ModalStage();
		final SolverSettingsView solverSettingsView = new SolverSettingsView(settingsStage);
		ViewController.getInstance().setSolverSettingsView(solverSettingsView);
		this.showNewStageWithRootElement(settingsStage, solverSettingsView, SOLVER_SETTINGS_DIALOG_WIDTH,
				DEFAULT_MODAL_DIALOG_HEIGHT);
	}

	public void showDifficultySettingsView() {
		final Stage settingsStage = new ModalStage();
		final DifficultySettingsView difficultySettingsView = new DifficultySettingsView(settingsStage);
		ViewController.getInstance().setDifficultySettingsView(difficultySettingsView);
		this.showNewStageWithRootElement(settingsStage, difficultySettingsView);
	}

	public void showColorSettingsView() {
		final Stage settingsStage = new ModalStage();
		final ColorSettingsView colorSettingsView = new ColorSettingsView(settingsStage);
		ViewController.getInstance().setColorSettingsView(colorSettingsView);
		this.showNewStageWithRootElement(settingsStage, colorSettingsView, COLOR_SETTINGS_DIALOG_WIDTH,
				COLOR_SETTINGS_DIALOG_HEIGHT);
	}

	public void showMiscellaneousSettingsView() {
		final Stage settingsStage = new ModalStage();
		final MiscellaneousSettingsView miscellaneousSettingsView = new MiscellaneousSettingsView(settingsStage);
		ViewController.getInstance().setMiscellaneousSettingsView(miscellaneousSettingsView);
		this.showNewStageWithRootElement(settingsStage, miscellaneousSettingsView);
	}

	public void showMessageDialog(final String title, final String message) {
		final Stage stage = new ModalStage();
		final MessageDialog messageDialog = new MessageDialog(stage);
		messageDialog.setTitle(title);
		messageDialog.setMessage(message);
		this.showNewStageWithRootElement(stage, messageDialog, MESSAGE_DIALOG_WIDTH, MESSAGE_DIALOG_HEIGHT);
	}

	public WaitingDialog createWaitingDialog(final String title, final String message) {
		final Stage stage = new ModalStage();
		final WaitingDialog waitingDialog = new WaitingDialog(stage);
		waitingDialog.setTitle(title);
		waitingDialog.setMessage(message);
		return waitingDialog;
	}

	public void showNewStageWithRootElement(final Stage stage, final ModalDialog modalDialog) {
		this.showNewStageWithRootElement(stage, modalDialog, DEFAULT_MODAL_DIALOG_WIDTH, DEFAULT_MODAL_DIALOG_HEIGHT);
	}

	public void showNewStageWithRootElement(final Stage stage, final ModalDialog modalDialog, final double width,
			final double height) {
		final RootStackPane rootStackPane = this.createRootStackPane(modalDialog);
		final Scene scene = new Scene(rootStackPane, width, height);
		this.configureScene(scene);
		stage.setScene(scene);
		stage.show();
		WindowHelper.addResizeAndDragListener(stage, modalDialog);
	}

	public LinearHintAnnotation createLinearHintAnnotation(final int startNodeData, final int endNodeData) {
		final LinearHintAnnotation hintAnnotation = new LinearHintAnnotation(startNodeData, endNodeData);
		ViewController.getInstance().registerHintAnnotation(hintAnnotation);
		return hintAnnotation;
	}

	public CurvedHintAnnotation createCurvedHintAnnotation(final int startNodeData, final int endNodeData) {
		final CurvedHintAnnotation hintAnnotation = new CurvedHintAnnotation(startNodeData, endNodeData);
		ViewController.getInstance().registerHintAnnotation(hintAnnotation);
		return hintAnnotation;
	}

	/** Offers some standard configuration of a scene for the project. */
	private void configureScene(final Scene scene) {
		scene.getStylesheets().add(ResourceConstants.APPLICATION_CSS);
		scene.setFill(Color.TRANSPARENT);
	}

	private LayoutFactory() {
		// Private constructor to prevent external instantiation.
	}

}
