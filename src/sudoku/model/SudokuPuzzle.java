package sudoku.model;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents all the underlying data for the sudoku cells. Note that
 * each cell has similar values as well.
 */
public class SudokuPuzzle {

	public static final int NUMBER_OF_CELLS_PER_DIMENSION = 9;

	private final Integer[][] givenCells;

	private final Integer[][] fixedCells;

	private final List<Integer>[][] candidatesForCells;

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
				this.givenCells[col][row] = null;
				this.fixedCells[col][row] = null;
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

}
