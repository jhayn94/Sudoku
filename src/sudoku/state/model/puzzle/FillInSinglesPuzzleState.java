package sudoku.state.model.puzzle;

import org.apache.logging.log4j.util.Strings;

import sudoku.core.HodokuFacade;
import sudoku.core.ViewController;
import sudoku.model.SudokuPuzzleValues;
import sudoku.state.model.ApplicationModelState;
import sudoku.view.hint.HintButtonPane;
import sudoku.view.hint.HintTextArea;
import sudoku.view.puzzle.SudokuPuzzleCell;
import sudoku.view.util.ColorUtils;

/**
 * This class updates the state of the application when the user presses the
 * "Fill in Singles" menu item.
 */
public class FillInSinglesPuzzleState extends ApplicationModelState {

	public FillInSinglesPuzzleState(final ApplicationModelState lastState) {
		super(lastState, true);
	}

	@Override
	public void onEnter() {
		final String updatedPuzzleString = HodokuFacade.getInstance()
				.solveAllSingles(this.sudokuPuzzleValues.toString(false));

		for (int row = 0; row < SudokuPuzzleValues.CELLS_PER_HOUSE; row++) {
			for (int col = 0; col < SudokuPuzzleValues.CELLS_PER_HOUSE; col++) {
				final int fixedCellDigit = this.sudokuPuzzleValues.getFixedCellDigit(row, col);
				if (fixedCellDigit == 0) {
					this.applyChangesForCell(updatedPuzzleString, row, col);
				}
			}
		}
		this.resetColorStates(false, true, ColorUtils.getHintColorStates());
		this.reapplyActiveFilter();
		this.updateRemainingScoreForPuzzle();
		final HintTextArea hintTextArea = ViewController.getInstance().getHintTextArea();
		hintTextArea.getHintTextArea().setText(Strings.EMPTY);

		final HintButtonPane hintButtonPane = ViewController.getInstance().getHintButtonPane();
		hintButtonPane.getApplyHintButton().setDisable(true);
		hintButtonPane.getHideHintButton().setDisable(true);
	}

	private void applyChangesForCell(final String updatedPuzzleString, final int row, final int col) {
		final int linearIndex = row * SudokuPuzzleValues.CELLS_PER_HOUSE + col;
		final int newFixedDigit = Integer.parseInt(updatedPuzzleString.substring(linearIndex, linearIndex + 1));
		this.sudokuPuzzleValues.setCellFixedDigit(row, col, newFixedDigit);
		final SudokuPuzzleCell sudokuPuzzleCell = ViewController.getInstance().getSudokuPuzzleCell(row, col);
		sudokuPuzzleCell.setCandidatesVisible(false);
		sudokuPuzzleCell.setFixedDigit(String.valueOf(newFixedDigit));
		this.updateFixedCellTypeCssClass(sudokuPuzzleCell, FIXED_CELL_CSS_CLASS);
		this.removeImpermissibleCandidates(sudokuPuzzleCell);
	}

}
