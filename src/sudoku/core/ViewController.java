package sudoku.core;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import sudoku.model.SudokuPuzzleValues;
import sudoku.view.ApplicationRootPane;
import sudoku.view.MainApplicationView;
import sudoku.view.hint.HintAnnotation;
import sudoku.view.hint.HintButtonPane;
import sudoku.view.hint.HintTextArea;
import sudoku.view.menu.button.ContextMenuButton;
import sudoku.view.menu.button.MaximizeMenuButton;
import sudoku.view.puzzle.SudokuPuzzleCell;
import sudoku.view.puzzle.SudokuPuzzleView;
import sudoku.view.settings.ColorSettingsView;
import sudoku.view.settings.DifficultySettingsView;
import sudoku.view.settings.MiscellaneousSettingsView;
import sudoku.view.settings.PuzzleGenerationSettingsView;
import sudoku.view.settings.SolverSettingsView;
import sudoku.view.sidebar.ControlHelperPane;
import sudoku.view.sidebar.FilterButtonPane;
import sudoku.view.sidebar.PuzzleStatsPane;

/**
 * A controller class to facilitate view changes, as result of a model change.
 * This class stores references to key existing views for updating. A reference
 * to static (container) views is not stored.
 */
public class ViewController {

	private static ViewController instance;

	public static ViewController getInstance() {
		if (ViewController.instance == null) {
			ViewController.instance = new ViewController();
		}
		return ViewController.instance;
	}

	private Stage stage;

	private ApplicationRootPane rootPane;

	private MainApplicationView mainApplicationView;

	private Button maximizeWindowButton;

	private FilterButtonPane filterButtonPane;

	private SudokuPuzzleView sudokuPuzzleView;

	private final SudokuPuzzleCell[][] sudokuPuzzleCells;

	private ControlHelperPane controlHelperPane;

	private HintButtonPane hintButtonPane;

	private HintTextArea hintTextArea;

	private MenuItem undoMenuItem;

	private MenuItem redoMenuItem;

	private ContextMenuButton contextMenuButton;

	private ColorSettingsView colorSettingsView;

	private MiscellaneousSettingsView miscellaneousSettingsView;

	private DifficultySettingsView difficultySettingsView;

	private PuzzleGenerationSettingsView puzzleGenerationSettingsView;

	private SolverSettingsView solverSettingsView;

	private PuzzleStatsPane puzzleStatsPane;

	private final List<HintAnnotation> hintAnnotations;

	private ViewController() {
		this.stage = null;
		this.maximizeWindowButton = null;
		this.filterButtonPane = null;
		this.sudokuPuzzleView = null;
		this.sudokuPuzzleCells = new SudokuPuzzleCell[SudokuPuzzleValues.CELLS_PER_HOUSE][SudokuPuzzleValues.CELLS_PER_HOUSE];
		this.controlHelperPane = null;
		this.puzzleStatsPane = null;
		this.mainApplicationView = null;
		this.hintButtonPane = null;
		this.hintTextArea = null;
		this.undoMenuItem = null;
		this.redoMenuItem = null;
		this.contextMenuButton = null;
		this.rootPane = null;
		this.colorSettingsView = null;
		this.miscellaneousSettingsView = null;
		this.difficultySettingsView = null;
		this.puzzleGenerationSettingsView = null;
		this.solverSettingsView = null;
		this.hintAnnotations = new ArrayList<>();
	}

	public Stage getStage() {
		return this.stage;
	}

	public Button getMaximizeWindowButton() {
		return this.maximizeWindowButton;
	}

	public FilterButtonPane getFilterButtonPane() {
		return this.filterButtonPane;
	}

	public SudokuPuzzleView getSudokuPuzzleView() {
		return this.sudokuPuzzleView;
	}

	public SudokuPuzzleCell getSudokuPuzzleCell(final int row, final int col) {
		return this.sudokuPuzzleCells[col][row];
	}

	public ControlHelperPane getControlHelperPane() {
		return this.controlHelperPane;
	}

	public HintButtonPane getHintButtonPane() {
		return this.hintButtonPane;
	}

	public HintTextArea getHintTextArea() {
		return this.hintTextArea;
	}

	public MenuItem getUndoMenuItem() {
		return this.undoMenuItem;
	}

	public MenuItem getRedoMenuItem() {
		return this.redoMenuItem;
	}

	public ContextMenuButton getContextMenuButton() {
		return this.contextMenuButton;
	}

	public ApplicationRootPane getRootPane() {
		return this.rootPane;
	}

	public PuzzleStatsPane getPuzzleStatsPane() {
		return this.puzzleStatsPane;
	}

	public ColorSettingsView getColorSettingsView() {
		return this.colorSettingsView;
	}

	public MiscellaneousSettingsView getMiscellaneousSettingsView() {
		return this.miscellaneousSettingsView;
	}

	public DifficultySettingsView getDifficultySettingsView() {
		return this.difficultySettingsView;
	}

	public PuzzleGenerationSettingsView getPuzzleGenerationSettingsView() {
		return this.puzzleGenerationSettingsView;
	}

	public SolverSettingsView getSolverSettingsView() {
		return this.solverSettingsView;
	}

	public void setColorSettingsView(final ColorSettingsView colorSettingsView) {
		this.colorSettingsView = colorSettingsView;
	}

	public void setRootPane(final ApplicationRootPane rootPane) {
		this.rootPane = rootPane;
	}

	public void setHintTextArea(final HintTextArea hintTextArea) {
		this.hintTextArea = hintTextArea;
	}

	public void setHintButtonPane(final HintButtonPane hintButtonPane) {
		this.hintButtonPane = hintButtonPane;
	}

	public void setStage(final Stage stage) {
		this.stage = stage;
	}

	public void setMaximizeWindowButton(final MaximizeMenuButton maximizeMenuButton) {
		this.maximizeWindowButton = maximizeMenuButton;
	}

	public void setFilterButtonPane(final FilterButtonPane filterButtonPane) {
		this.filterButtonPane = filterButtonPane;
	}

	public void setSudokuPuzzleView(final SudokuPuzzleView sudokuPuzzleView) {
		this.sudokuPuzzleView = sudokuPuzzleView;
	}

	public void registerSudokuPuzzleCell(final SudokuPuzzleCell sudokuPuzzleCell, final int col, final int row) {
		this.sudokuPuzzleCells[col][row] = sudokuPuzzleCell;
	}

	public void setControlHelperPane(final ControlHelperPane controlHelperPane) {
		this.controlHelperPane = controlHelperPane;
	}

	public void setUndoMenuItem(final MenuItem undoMenuItem) {
		this.undoMenuItem = undoMenuItem;
	}

	public void setRedoMenuItem(final MenuItem redoMenuItem) {
		this.redoMenuItem = redoMenuItem;
	}

	public void setContextMenuButton(final ContextMenuButton contextMenuButton) {
		this.contextMenuButton = contextMenuButton;
	}

	public void setMiscellaneousSettingsView(final MiscellaneousSettingsView miscellaneousSettingsView) {
		this.miscellaneousSettingsView = miscellaneousSettingsView;
	}

	public void setDifficultySettingsView(final DifficultySettingsView difficultySettingsView) {
		this.difficultySettingsView = difficultySettingsView;
	}

	public void setPuzzleGenerationSettingsView(final PuzzleGenerationSettingsView puzzleGenerationSettingsView) {
		this.puzzleGenerationSettingsView = puzzleGenerationSettingsView;
	}

	public void setSolverSettingsView(final SolverSettingsView solverSettingsView) {
		this.solverSettingsView = solverSettingsView;
	}

	public void setPuzzleStatsPane(final PuzzleStatsPane puzzleStatsPane) {
		this.puzzleStatsPane = puzzleStatsPane;
	}

	public List<HintAnnotation> getHintAnnotations() {
		return this.hintAnnotations;
	}

	public void registerHintAnnotation(final HintAnnotation newAnnotation) {
		this.hintAnnotations.add(newAnnotation);
	}

	public MainApplicationView getMainApplicationView() {
		return this.mainApplicationView;
	}

	public void setMainApplicationView(final MainApplicationView mainApplicationView) {
		this.mainApplicationView = mainApplicationView;
	}

}
