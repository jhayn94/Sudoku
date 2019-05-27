package sudoku.state.model.puzzle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.util.Strings;

import sudoku.core.ViewController;
import sudoku.model.SudokuPuzzleValues;
import sudoku.state.ApplicationModelState;
import sudoku.view.puzzle.SudokuPuzzleCell;

/**
 * This class updates the state of the application when the user invokes a
 * "redo", either through the keyboard or a button press in the UI.
 */
public class OpenedFileState extends ApplicationModelState {

	private String puzzleString;

	private String[][] candidatesForCellsFromFile;

	private String givens;

	public OpenedFileState(final File selectedFile, final ApplicationModelState lastState) {
		super(lastState, false);
		this.parseSelectedFile(selectedFile);
		this.applicationStateHistory.clearRedoStack();
		this.applicationStateHistory.clearUndoStack();
		this.updateUndoRedoButtons();
	}

	private void parseSelectedFile(final File selectedFile) {
		try {
			final FileReader fileReader = new FileReader(selectedFile);
			final BufferedReader bufferedReader = new BufferedReader(fileReader);
			// .SPF files should have exactly 1 line of givens, 1 line of set values, then
			// 81 for the candidates.
			this.givens = bufferedReader.readLine();
			this.puzzleString = bufferedReader.readLine();
			this.candidatesForCellsFromFile = new String[SudokuPuzzleValues.CELLS_PER_HOUSE][SudokuPuzzleValues.CELLS_PER_HOUSE];
			for (int row = 0; row < SudokuPuzzleValues.CELLS_PER_HOUSE; row++) {
				for (int col = 0; col < SudokuPuzzleValues.CELLS_PER_HOUSE; col++) {
					this.candidatesForCellsFromFile[col][row] = bufferedReader.readLine();
				}
			}
			bufferedReader.close();
		} catch (final IOException ioe) {
			ioe.printStackTrace();
		}
	}

	@Override
	public void onEnter() {
		this.resetAllFilters();
		this.updateFilterButtonStates(Strings.EMPTY);
		this.resetColorStates();
		this.sudokuPuzzleValues = new SudokuPuzzleValues(this.givens);
		this.updateGivenCells();
		this.updateOtherSetCells();
		// Must do this after because the cell values need to be finished before setting
		// candidates. Otherwise the doesCellSeeFixedDigit checks will not be correct.
		this.updateCandidates();
	}

	private void updateGivenCells() {
		for (int row = 0; row < SudokuPuzzleValues.CELLS_PER_HOUSE; row++) {
			for (int col = 0; col < SudokuPuzzleValues.CELLS_PER_HOUSE; col++) {
				final SudokuPuzzleCell sudokuPuzzleCell = ViewController.getInstance().getSudokuPuzzleCell(row, col);
				final int givenCellDigit = this.sudokuPuzzleValues.getGivenCellDigit(row, col);
				final boolean isCellGiven = givenCellDigit != 0;
				sudokuPuzzleCell.setFixedDigit(isCellGiven ? String.valueOf(givenCellDigit) : Strings.EMPTY);
				sudokuPuzzleCell.setCandidatesVisible(!isCellGiven);
				sudokuPuzzleCell.setCellGiven(isCellGiven);
				if (isCellGiven) {
					this.updateFixedCellTypeCssClass(sudokuPuzzleCell, GIVEN_CELL_CSS_CLASS);
				}
			}
		}
	}

	private void updateOtherSetCells() {
		for (int row = 0; row < SudokuPuzzleValues.CELLS_PER_HOUSE; row++) {
			for (int col = 0; col < SudokuPuzzleValues.CELLS_PER_HOUSE; col++) {
				final char givenDigit = this.givens.charAt(row * SudokuPuzzleValues.CELLS_PER_HOUSE + col);
				final char setDigit = this.puzzleString.charAt(row * SudokuPuzzleValues.CELLS_PER_HOUSE + col);
				if (givenDigit != setDigit) {
					final boolean isFixed = '0' != setDigit;
					this.sudokuPuzzleValues.setCellFixedDigit(row, col, setDigit - '0');
					final SudokuPuzzleCell sudokuPuzzleCell = ViewController.getInstance().getSudokuPuzzleCell(row, col);
					sudokuPuzzleCell.setFixedDigit(isFixed ? String.valueOf(setDigit) : Strings.EMPTY);
					sudokuPuzzleCell.setCandidatesVisible(!isFixed);
					this.updateFixedCellTypeCssClass(sudokuPuzzleCell, isFixed ? FIXED_CELL_CSS_CLASS : UNFIXED_CELL_CSS_CLASS);
				}
			}
		}
	}

	private void updateCandidates() {
		for (int row = 0; row < SudokuPuzzleValues.CELLS_PER_HOUSE; row++) {
			for (int col = 0; col < SudokuPuzzleValues.CELLS_PER_HOUSE; col++) {
				final SudokuPuzzleCell sudokuPuzzleCell = ViewController.getInstance().getSudokuPuzzleCell(row, col);
				for (int candidate = 1; candidate <= SudokuPuzzleValues.CELLS_PER_HOUSE; candidate++) {
					this.setCandidateVisibility(row, col, sudokuPuzzleCell);
				}
			}
		}
	}

	private void setCandidateVisibility(final int row, final int col, final SudokuPuzzleCell sudokuPuzzleCell) {
		final int givenCellDigit = this.sudokuPuzzleValues.getGivenCellDigit(row, col);
		final boolean isCellGiven = givenCellDigit != 0;
		if (!isCellGiven) {
			final List<Integer> candidateDigitsForCell = this.sudokuPuzzleValues.getCandidateDigitsForCell(row, col);
			for (int candidate = 1; candidate <= SudokuPuzzleValues.CELLS_PER_HOUSE; candidate++) {
				final String candidatesFromFile = this.candidatesForCellsFromFile[col][row];
				final boolean shouldShowCandidate = candidatesFromFile.contains(String.valueOf(candidate));
				sudokuPuzzleCell.setCandidateVisible(candidate, shouldShowCandidate);
				if (!shouldShowCandidate) {
					candidateDigitsForCell.remove((Object) candidate);
				}
			}
		}
	}

}
