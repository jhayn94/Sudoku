package sudoku.generator;

import sudoku.factories.ModelFactory;
import sudoku.model.SudokuPuzzle;

/**
 * This class contains methods for quickly checking if a Sudoku puzzle has a
 * unique solution.
 *
 */
public final class SudokuGenerator {

	private static final int MAX_TRIES = 1000000;

	private static final SudokuPuzzle EMPTY_PUZZLE = ModelFactory.getInstance().createSudokuPuzzle();

	/**
	 * A representation of the puzzle at a particular point in time. The generator
	 * uses a stack of these to solve the puzzle via recursive backtracking.
	 */
	private class PuzzleSolutionState {

		SudokuPuzzle sudoku = ModelFactory.getInstance().createSudokuPuzzle();

		int indexOfCellToTry;

		/** The candidates for cells {@link #indexOfCellToTry}. */
		int[] candidates;

		/** The index of the last tried candidate in {@link #candidates}. */
		int candIndex;
	}

	public SudokuPuzzle generateSudoku(boolean b) {
		// TODO - port this class?
		return null;
	}

	// TODO - do we need the rest ported?, or can it be implemented

}
