package sudoku.model;

import java.util.ArrayList;
import java.util.List;

/** This class represents all the underlying data for the sudoku cells. */
public class SudokuPuzzle {

	public static final int NUMBER_OF_CELLS_PER_DIMENSION = 9;
	private final Integer[][] givenCells;

	private final Integer[][] definedCells;

	private final List<Integer>[][] candidatesForCells;

	@SuppressWarnings("unchecked")
	public SudokuPuzzle() {
		this.givenCells = new Integer[NUMBER_OF_CELLS_PER_DIMENSION][NUMBER_OF_CELLS_PER_DIMENSION];
		this.definedCells = new Integer[NUMBER_OF_CELLS_PER_DIMENSION][NUMBER_OF_CELLS_PER_DIMENSION];
		this.candidatesForCells = new ArrayList[NUMBER_OF_CELLS_PER_DIMENSION][NUMBER_OF_CELLS_PER_DIMENSION];
		for (int row = 0; row < NUMBER_OF_CELLS_PER_DIMENSION; row++) {
			for (int col = 0; col < NUMBER_OF_CELLS_PER_DIMENSION; col++) {
				this.givenCells[col][row] = null;
				this.definedCells[col][row] = null;
				this.candidatesForCells[col][row] = new ArrayList<Integer>();
			}
		}
	}

}
