package sudoku.model;

import java.util.ArrayList;
import java.util.List;

/** This class represents all the underlying data for the sudoku cells. */
public class SudokuPuzzle {

	private static final int NUMBER_OF_CELLS = 9;
	private final Integer[][] givenCells;

	private final Integer[][] definedCells;

	private final List<Integer>[][] candidatesForCells;

	@SuppressWarnings("unchecked")
	public SudokuPuzzle() {
		this.givenCells = new Integer[NUMBER_OF_CELLS][NUMBER_OF_CELLS];
		this.definedCells = new Integer[NUMBER_OF_CELLS][NUMBER_OF_CELLS];
		this.candidatesForCells = new ArrayList[NUMBER_OF_CELLS][NUMBER_OF_CELLS];
		for (int row = 0; row < NUMBER_OF_CELLS; row++) {
			for (int col = 0; col < NUMBER_OF_CELLS; col++) {
				this.givenCells[col][row] = null;
				this.definedCells[col][row] = null;
				this.candidatesForCells[col][row] = new ArrayList<Integer>();
			}
		}
	}

}
