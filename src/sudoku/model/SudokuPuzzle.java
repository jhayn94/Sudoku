package sudoku.model;

import java.util.ArrayList;
import java.util.List;

import sudoku.factories.ModelFactory;
import sudoku.view.util.PuzzleDifficulty;

/**
 * This class represents all the underlying data for the sudoku cells. Note that
 * each cell has similar values as well.
 */
public class SudokuPuzzle {

	public static final int NUMBER_OF_CELLS_PER_DIMENSION = 9;

	private final Integer[][] givenCells;

	private final Integer[][] fixedCells;

	private final List<Integer>[][] candidatesForCells;

	private PuzzleDifficulty difficulty;

	private int score;

	/*
	 * TODO - review how candidates should be managed when
	 *
	 * 1) Action is undone, should candidates be restored?
	 *
	 * 2) Digit is placed over an old one; should candidates for other number be
	 * restored? If so, we would need to track something like user set candidates
	 * and auto set candidates
	 *
	 * 3) Digit is placed, do I remove the candidate from that cell?
	 */
	@SuppressWarnings("unchecked")
	public SudokuPuzzle() {
		this.givenCells = new Integer[NUMBER_OF_CELLS_PER_DIMENSION][NUMBER_OF_CELLS_PER_DIMENSION];
		this.fixedCells = new Integer[NUMBER_OF_CELLS_PER_DIMENSION][NUMBER_OF_CELLS_PER_DIMENSION];
		this.candidatesForCells = new ArrayList[NUMBER_OF_CELLS_PER_DIMENSION][NUMBER_OF_CELLS_PER_DIMENSION];
		for (int row = 0; row < NUMBER_OF_CELLS_PER_DIMENSION; row++) {
			for (int col = 0; col < NUMBER_OF_CELLS_PER_DIMENSION; col++) {
				this.givenCells[col][row] = 0;
				this.fixedCells[col][row] = 0;
				this.candidatesForCells[col][row] = new ArrayList<>();
				for (int candidate = 0; candidate < NUMBER_OF_CELLS_PER_DIMENSION; candidate++) {
					this.candidatesForCells[col][row].add(candidate + 1);
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
		for (int row = 0; row < NUMBER_OF_CELLS_PER_DIMENSION; row++) {
			for (int col = 0; col < NUMBER_OF_CELLS_PER_DIMENSION; col++) {
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
		for (int row = 0; row < NUMBER_OF_CELLS_PER_DIMENSION; row++) {
			for (int col = 0; col < NUMBER_OF_CELLS_PER_DIMENSION; col++) {
				result.append(this.fixedCells[col][row]);
			}
		}
		return result.toString();
	}

	/** Returns true iff this puzzle is solved. */
	public boolean isSolved() {
		for (int row = 0; row < NUMBER_OF_CELLS_PER_DIMENSION; row++) {
			for (int col = 0; col < NUMBER_OF_CELLS_PER_DIMENSION; col++) {
				if (this.fixedCells[col][row] == 0) {
					return false;
				}
			}
		}
		return true;
	}

	public PuzzleDifficulty getDifficulty() {
		return this.difficulty;
	}

	public int getScore() {
		return this.score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public void setLevel(PuzzleDifficulty difficulty) {
		this.difficulty = difficulty;
	}

	public int getNumberOfUnsolvedCells() {
		int unsolvedCellsCount = 0;
		for (int row = 0; row < NUMBER_OF_CELLS_PER_DIMENSION; row++) {
			for (int col = 0; col < NUMBER_OF_CELLS_PER_DIMENSION; col++) {
				if (this.fixedCells[col][row] == 0) {
					unsolvedCellsCount++;
				}
			}
		}
		return unsolvedCellsCount;
	}

}
