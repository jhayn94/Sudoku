package sudoku.model;

import java.util.ArrayList;
import java.util.List;

import sudoku.factories.ModelFactory;

/**
 * This class represents all the underlying data for the sudoku cells. Note that
 * each cell has similar values as well.
 */
public class SudokuPuzzle {

	public static final int CELLS_PER_HOUSE = 9;

	private final Integer[][] givenCells;

	private final Integer[][] fixedCells;

	private final List<Integer>[][] candidatesForCells;

	private int score;

	@SuppressWarnings("unchecked")
	public SudokuPuzzle() {
		this.givenCells = new Integer[CELLS_PER_HOUSE][CELLS_PER_HOUSE];
		this.fixedCells = new Integer[CELLS_PER_HOUSE][CELLS_PER_HOUSE];
		this.candidatesForCells = new ArrayList[CELLS_PER_HOUSE][CELLS_PER_HOUSE];
		for (int row = 0; row < CELLS_PER_HOUSE; row++) {
			for (int col = 0; col < CELLS_PER_HOUSE; col++) {
				this.givenCells[col][row] = 0;
				this.fixedCells[col][row] = 0;
				this.candidatesForCells[col][row] = new ArrayList<>();
				for (int candidate = 0; candidate < CELLS_PER_HOUSE; candidate++) {
					this.candidatesForCells[col][row].add(candidate + 1);
				}
			}
		}
	}

	public SudokuPuzzle(final String initialGivens) {
		this();
		for (int row = 0; row < CELLS_PER_HOUSE; row++) {
			for (int col = 0; col < CELLS_PER_HOUSE; col++) {
				final int position = row * CELLS_PER_HOUSE + col;
				final char charAtPosition = initialGivens.charAt(position);
				if (Character.isDigit(charAtPosition)) {
					this.givenCells[col][row] = Integer.valueOf(charAtPosition);
				}
			}
		}
	}

	public int getGivenCellDigit(final int row, final int col) {
		return this.givenCells[col][row];
	}

	public int getFixedCellDigit(final int row, final int col) {
		return this.fixedCells[col][row];
	}

	public List<Integer> getCandidateDigitsForCell(final int row, final int col) {
		return this.candidatesForCells[col][row];
	}

	public void setGivenCellDigit(final int row, final int col, final int given) {
		this.givenCells[col][row] = given;
	}

	public void setCellFixedDigit(final int row, final int col, final int fixedDigit) {
		this.fixedCells[col][row] = fixedDigit;
	}

	public void setCellCandidateDigits(final int row, final int col, final List<Integer> candidates) {
		this.candidatesForCells[col][row] = candidates;
	}

	/** Creates and returns a deep copy of this. */
	@Override
	public SudokuPuzzle clone() {
		final SudokuPuzzle clone = ModelFactory.getInstance().createSudokuPuzzle();
		for (int row = 0; row < CELLS_PER_HOUSE; row++) {
			for (int col = 0; col < CELLS_PER_HOUSE; col++) {
				clone.givenCells[col][row] = this.givenCells[col][row];
				clone.fixedCells[col][row] = this.fixedCells[col][row];
				clone.candidatesForCells[col][row] = new ArrayList<>();
				clone.candidatesForCells[col][row].addAll(this.candidatesForCells[col][row]);
			}
		}
		return clone;
	}

	/**
	 * Returns the current state of the sudoku as string, where each digit is set if
	 * fixed in the puzzle.. 0 is used if no digit is set.
	 */
	public String getSudoku() {
		final StringBuilder result = new StringBuilder();
		for (int row = 0; row < CELLS_PER_HOUSE; row++) {
			for (int col = 0; col < CELLS_PER_HOUSE; col++) {
				result.append(this.fixedCells[col][row]);
			}
		}
		return result.toString();
	}

	/** Returns true iff this puzzle is solved. */
	public boolean isSolved() {
		for (int row = 0; row < CELLS_PER_HOUSE; row++) {
			for (int col = 0; col < CELLS_PER_HOUSE; col++) {
				if (this.fixedCells[col][row] == 0) {
					return false;
				}
			}
		}
		return true;
	}

	public int getScore() {
		return this.score;
	}

	public void setScore(final int score) {
		this.score = score;
	}

	public int getNumberOfUnsolvedCells() {
		int unsolvedCellsCount = 0;
		for (int row = 0; row < CELLS_PER_HOUSE; row++) {
			for (int col = 0; col < CELLS_PER_HOUSE; col++) {
				if (this.fixedCells[col][row] == 0) {
					unsolvedCellsCount++;
				}
			}
		}
		return unsolvedCellsCount;
	}

	public String getStringRepresentation(final boolean onlyGivens) {
		final StringBuilder sb = new StringBuilder();
		Integer[][] arrayToIterate;
		if (onlyGivens) {
			arrayToIterate = this.fixedCells;
		} else {
			arrayToIterate = this.givenCells;
		}
		for (int row = 0; row < CELLS_PER_HOUSE; row++) {
			for (int col = 0; col < CELLS_PER_HOUSE; col++) {
				final Integer valueForCell = arrayToIterate[col][row];
				sb.append(valueForCell == null ? '.' : valueForCell);
			}
		}
		return sb.toString();
	}
}
