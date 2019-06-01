package sudoku.view.puzzle;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import sudoku.core.ViewController;
import sudoku.model.SudokuPuzzleValues;

/**
 * This class contains utility methods for interacting with multiple
 * SudokuPuzzleCells. These methods are used mostly by the state machine to get
 * the current state of the view.
 *
 * Sometimes these methods are mis-used as the current model state (instead of
 * using the real model, SudokuPuzzleValues), but in certain cases, it just
 * makes more sense or is easier to use the view component directly.
 *
 */
public class SudokuPuzzleCellUtils {

	/** Gets a list of cells seen from the given row and column. */
	public static List<SudokuPuzzleCell> getCellsSeenFrom(final int row, final int col) {
		final SudokuPuzzleCell cell = ViewController.getInstance().getSudokuPuzzleCell(row, col);
		final List<SudokuPuzzleCell> cells = new ArrayList<>();
		for (int rowIndex = 0; rowIndex < SudokuPuzzleValues.CELLS_PER_HOUSE; rowIndex++) {
			if (rowIndex != row) {
				cells.add(ViewController.getInstance().getSudokuPuzzleCell(rowIndex, col));
			}
		}
		for (int colIndex = 0; colIndex < SudokuPuzzleValues.CELLS_PER_HOUSE; colIndex++) {
			if (colIndex != col) {
				cells.add(ViewController.getInstance().getSudokuPuzzleCell(row, colIndex));
			}
		}
		final int boxForCell = SudokuPuzzleCellUtils.getBoxForCell(cell.getRow(), cell.getCol());
		SudokuPuzzleCellUtils.getCellsInBox(boxForCell).forEach(cells::add);
		return cells.stream().distinct().collect(Collectors.toList());
	}

	/**
	 * Returns a list of cells in the given box (1 - 9). Any other inputs will
	 * return an empty list.
	 */
	public static List<SudokuPuzzleCell> getCellsInBox(final int box) {
		final List<SudokuPuzzleCell> cells = new ArrayList<>();
		for (int rowIndex = 0; rowIndex < SudokuPuzzleValues.CELLS_PER_HOUSE; rowIndex++) {
			for (int colIndex = 0; colIndex < SudokuPuzzleValues.CELLS_PER_HOUSE; colIndex++) {
				final SudokuPuzzleCell cell = ViewController.getInstance().getSudokuPuzzleCell(rowIndex, colIndex);
				if (SudokuPuzzleCellUtils.getBoxForCell(rowIndex, colIndex) == box) {
					cells.add(cell);
				}
			}
		}
		return cells;
	}

	/**
	 * Returns true iff the cell at the given row and column see a fixed cell with
	 * the given digit.
	 */
	public static boolean doesCellSeeFixedDigit(final int row, final int col, final int fixedDigit) {
		final List<SudokuPuzzleCell> visibleCells = SudokuPuzzleCellUtils.getCellsSeenFrom(row, col);
		final long numDigitInstancesSeen = visibleCells.stream().filter(cell -> cell.getFixedDigit() == fixedDigit).count();
		return numDigitInstancesSeen > 0;
	}

	/**
	 * Gets the box number of the given cell. Returns -1 if row and col are outside
	 * of the puzzle dimensions.
	 */
	public static int getBoxForCell(final int row, final int col) {
		if (row <= 2 && col <= 2) {
			return 1;
		} else if (row <= 2 && col <= 5) {
			return 2;
		} else if (row <= 2 && col <= 8) {
			return 3;
		} else if (row <= 5 && col <= 2) {
			return 4;
		} else if (row <= 5 && col <= 5) {
			return 5;
		} else if (row <= 5 && col <= 8) {
			return 6;
		} else if (row <= 8 && col <= 2) {
			return 7;
		} else if (row <= 8 && col <= 5) {
			return 8;
		} else if (row <= 8 && col <= 8) {
			return 9;
		}
		return -1;
	}

	private SudokuPuzzleCellUtils() {
		// Private constructor to prevent instantiation.
	}
}
