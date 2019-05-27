package sudoku.core;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import sudoku.model.SudokuPuzzleValues;
import sudoku.view.hint.HintButtonPane;
import sudoku.view.hint.HintTextArea;
import sudoku.view.menu.button.ContextMenuButton;
import sudoku.view.menu.button.MaximizeMenuButton;
import sudoku.view.puzzle.SudokuPuzzleCell;
import sudoku.view.puzzle.SudokuPuzzleView;
import sudoku.view.sidebar.ColorSelectionPane;
import sudoku.view.sidebar.FilterButtonPane;

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

	private Button maximizeWindowButton;

	private FilterButtonPane filterButtonPane;

	private SudokuPuzzleView sudokuPuzzleView;

	private final SudokuPuzzleCell[][] sudokuPuzzleCells;

	private Label activeColoringCandidateLabel;

	private ColorSelectionPane colorSelectionPane;

	private HintButtonPane hintButtonPane;

	private HintTextArea hintTextArea;

	private MenuItem undoMenuItem;

	private MenuItem redoMenuItem;

	private ContextMenuButton contextMenuButton;

	private ViewController() {
		this.stage = null;
		this.maximizeWindowButton = null;
		this.filterButtonPane = null;
		this.sudokuPuzzleView = null;
		this.sudokuPuzzleCells = new SudokuPuzzleCell[SudokuPuzzleValues.CELLS_PER_HOUSE][SudokuPuzzleValues.CELLS_PER_HOUSE];
		this.activeColoringCandidateLabel = null;
		this.colorSelectionPane = null;
		this.hintButtonPane = null;
		this.hintTextArea = null;
		this.undoMenuItem = null;
		this.redoMenuItem = null;
		this.contextMenuButton = null;
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

	public Label getActiveColoringCandidateLabel() {
		return this.activeColoringCandidateLabel;
	}

	public ColorSelectionPane getColorSelectionPane() {
		return this.colorSelectionPane;
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

	public void setActiveColoringCandidateLabel(final Label activeColoringCandidateLabel) {
		this.activeColoringCandidateLabel = activeColoringCandidateLabel;
	}

	public void setColorSelectionPane(final ColorSelectionPane colorSelectionPane) {
		this.colorSelectionPane = colorSelectionPane;
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

}
