package sudoku.state.model.puzzle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;

import sudoku.core.HodokuFacade;
import sudoku.core.ViewController;
import sudoku.factories.ModelFactory;
import sudoku.model.ApplicationSettings;
import sudoku.model.SudokuPuzzleValues;
import sudoku.state.model.ApplicationModelState;
import sudoku.view.puzzle.SudokuPuzzleCell;

/**
 * This class updates the state of the application when the user opens an
 * existing puzzle file.
 */
public class OpenedFileState extends ApplicationModelState {

	private static final Logger LOG = LogManager.getLogger(OpenedFileState.class);

	private String puzzleString;

	private String[][] candidatesForCellsFromFile;

	private String givens;

	public OpenedFileState(final File selectedFile, final ApplicationModelState lastState) {
		super(lastState, false);
		this.parseSelectedFile(selectedFile);
	}

	private void parseSelectedFile(final File selectedFile) {
		this.applicationStateHistory.clearRedoStack();
		this.applicationStateHistory.clearUndoStack();
		this.updateUndoRedoButtons();
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
			LOG.error("{}", ioe);
		}
	}

	@Override
	public void onEnter() {
		ViewController.getInstance().getRootPane().removeAllAnnotations();
		this.sudokuPuzzleStyle.setActiveCellFilter(Strings.EMPTY);
		this.resetAllFilters();
		this.updateFilterButtonStates(Strings.EMPTY);
		this.resetAllColorStates();
		this.sudokuPuzzleValues = ModelFactory.getInstance().createSudokuPuzzleValues(this.givens);
		this.updateGivenCells();
		this.updateOtherSetCells();
		this.updateCandidates();

		final int scoreForPuzzle = HodokuFacade.getInstance().getScoreForPuzzle(this.sudokuPuzzleValues, true);
		final int remainingScoreForPuzzle = HodokuFacade.getInstance().getScoreForPuzzle(this.sudokuPuzzleValues, false);
		ViewController.getInstance().getPuzzleStatsPane().getDifficultyTextField()
				.setText(ApplicationSettings.getInstance().getDifficulty().getLabel());
		ViewController.getInstance().getPuzzleStatsPane().getRatingTextField().setText(String.valueOf(scoreForPuzzle));
		ViewController.getInstance().getPuzzleStatsPane().getRemainingRatingTextField()
				.setText(String.valueOf(remainingScoreForPuzzle));
	}

	@Override
	protected void setCandidateVisibility(final int row, final int col, final SudokuPuzzleCell sudokuPuzzleCell,
			final boolean isCellGiven) {
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

	private void updateGivenCells() {
		for (int row = 0; row < SudokuPuzzleValues.CELLS_PER_HOUSE; row++) {
			for (int col = 0; col < SudokuPuzzleValues.CELLS_PER_HOUSE; col++) {
				final SudokuPuzzleCell sudokuPuzzleCell = ViewController.getInstance().getSudokuPuzzleCell(row, col);
				final int givenCellDigit = this.sudokuPuzzleValues.getGivenCellDigit(row, col);
				final boolean isCellGiven = givenCellDigit != 0;
				sudokuPuzzleCell.setFixedDigit(isCellGiven ? String.valueOf(givenCellDigit) : Strings.EMPTY);
				sudokuPuzzleCell.setCandidatesVisible(!isCellGiven);
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
}
