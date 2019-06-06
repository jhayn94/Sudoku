package sudoku.state.model.cell;

import javafx.geometry.Bounds;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import sudoku.core.ViewController;
import sudoku.model.ApplicationSettings;
import sudoku.model.SudokuPuzzleValues;
import sudoku.state.model.ApplicationModelState;
import sudoku.view.puzzle.SudokuPuzzleCell;
import sudoku.view.util.ColorUtils.ColorState;
import sudoku.view.util.MouseMode;

/**
 * This class updates the state of the application when the user clicks a cell.
 * In general, this will only affect the selected cell.
 */
public class ClickedCellState extends ApplicationModelState {

	private final int col;

	private final int row;

	private final MouseEvent event;

	public ClickedCellState(final int row, final int col, final MouseEvent event, final ApplicationModelState lastState) {
		super(lastState, false);
		this.row = row;
		this.col = col;
		this.event = event;
	}

	@Override
	public void onEnter() {
		final boolean useDigitButtonsForMouseActions = ApplicationSettings.getInstance().isUseDigitButtonsForMouseActions();
		final SudokuPuzzleCell clickedCell = ViewController.getInstance().getSudokuPuzzleCell(this.row, this.col);
		if (MouseMode.SELECT_CELLS == this.mouseMode) {
			this.handleSelectCellMouseMode();
		} else if (MouseMode.TOGGLE_CANDIDATES == this.mouseMode) {
			this.handleToggleCandidateMouseMode(useDigitButtonsForMouseActions, clickedCell);
		} else if (MouseMode.COLOR_CELLS == this.mouseMode) {
			this.handleColorCellMouseMode();
		} else {
			this.handleColorCandidateMouseMode(useDigitButtonsForMouseActions);
		}
	}

	private void handleSelectCellMouseMode() {
		if (this.sudokuPuzzleStyle.getSelectedCellRow() != -1 && this.sudokuPuzzleStyle.getSelectedCellCol() != -1) {
			this.getSelectedCell().setIsSelected(false);
		}
		this.updateSelectedCell();
	}

	private void handleToggleCandidateMouseMode(final boolean useDigitButtonsForMouseActions,
			final SudokuPuzzleCell clickedCell) {
		if (useDigitButtonsForMouseActions) {
			this.toggleCandidateActiveForCell(this.sudokuPuzzleStyle.getActiveCandidateDigit(), clickedCell);
		} else {
			final int clickedCandidate = this.getClickedCandidate();
			if (clickedCandidate != -1) {
				this.toggleCandidateActiveForCell(clickedCandidate, clickedCell);
			}
		}
	}

	private void handleColorCellMouseMode() {
		final ColorState baseColorState = ColorState.getStateForBaseColor(this.sudokuPuzzleStyle.getActiveColor());
		final ColorState colorStateToApply = ColorState.getFromKeyCode(baseColorState.getKey(), this.event.isShiftDown());
		this.setColorStateForCell(this.row, this.col, colorStateToApply);
	}

	private void handleColorCandidateMouseMode(final boolean useDigitButtonsForMouseActions) {
		final ColorState baseColorState = ColorState.getStateForBaseColor(this.sudokuPuzzleStyle.getActiveColor());
		final ColorState colorStateToApply = ColorState.getFromKeyCode(baseColorState.getKey(), this.event.isShiftDown());
		if (useDigitButtonsForMouseActions) {
			this.setCandidateColorForCell(this.row, this.col, colorStateToApply,
					this.sudokuPuzzleStyle.getActiveCandidateDigit());
		} else {
			final int clickedCandidate = this.getClickedCandidate();
			if (clickedCandidate != -1) {
				this.setCandidateColorForCell(this.row, this.col, colorStateToApply, clickedCandidate);
			}
		}
	}

	private void updateSelectedCell() {
		// Cell was already selected.
		if (this.row == this.sudokuPuzzleStyle.getSelectedCellRow()
				&& this.col == this.sudokuPuzzleStyle.getSelectedCellCol()) {
			this.sudokuPuzzleStyle.resetSelectedCellIndices();
		} else {
			this.sudokuPuzzleStyle.setSelectedCellRow(this.row);
			this.sudokuPuzzleStyle.setSelectedCellCol(this.col);
			this.getSelectedCell().setIsSelected(true);
		}
	}

	/** Determines and returns the clicked candidate. */
	private int getClickedCandidate() {
		final SudokuPuzzleCell sudokuPuzzleCell = ViewController.getInstance().getSudokuPuzzleCell(this.row, this.col);
		final double sceneX = this.event.getSceneX();
		final double sceneY = this.event.getSceneY();
		for (int candidate = 1; candidate <= SudokuPuzzleValues.CELLS_PER_HOUSE; candidate++) {
			final Label candidateLabelForDigit = sudokuPuzzleCell.getCandidateLabelForDigit(candidate);
			final Bounds candidateLabelBounds = candidateLabelForDigit
					.localToScene(candidateLabelForDigit.getBoundsInLocal());
			final double minX = candidateLabelBounds.getMinX();
			final double maxX = candidateLabelBounds.getMaxX();
			final double minY = candidateLabelBounds.getMinY();
			final double maxY = candidateLabelBounds.getMaxY();
			if (minX < sceneX && sceneX < maxX && minY < sceneY && sceneY < maxY) {
				return candidate;
			}
		}
		return -1;

	}

}