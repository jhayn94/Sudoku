package sudoku.view.hint;

import sudoku.Chain;
import sudoku.model.SudokuPuzzleValues;

/**
 * This class is an implementation of {@link HintAnnotation} where the
 * annotation body is a straight line.
 */
public abstract class AbstractHintAnnotation implements HintAnnotation {

	protected static final double ADDITIONAL_END_POINT_TAPER_FACTOR = 1.2;

	protected static final double COORDINATE_ERROR_X_OFFSET = 9;

	protected static final double COORDINATE_ERROR_Y_OFFSET = 11;

	protected static final double DASHED_LINE_ON_LENGTH = 5.0;

	protected static final double DASHED_LINE_OFF_LENGTH = 7.5;

	protected static final double LINE_WIDTH = 3.0;

	// Approximation of the width of a candidate label. They are a 16x14 pixel
	// rectangle, so obviously this is a rough approximation. This was calculated by
	// 16 (the large dimension) / 2 + a little extra since the rectangle is longer
	// from corner to corner.
	protected static final double LABEL_RADIUS = 10;

	protected static final double ARROW_SIDE_LENGTH = 4;

	private final int startNodeData;

	private final int endNodeData;

	protected boolean valid;

	public AbstractHintAnnotation(final int startNodeData, final int endNodeData) {
		this.valid = true;
		this.startNodeData = startNodeData;
		this.endNodeData = endNodeData;
	}

	/**
	 * Returns true iff this link intersects with the given node data.
	 *
	 * @requires this.valid = true. Behavior is undefined otherwise.
	 */
	@Override
	public boolean intersectsWith(final int nodeData) {
		boolean intersects = false;
		// If the given node is in the link, there is no intersection.

		final int startCellIndex = Chain.getSCellIndex(this.startNodeData);
		final int endCellIndex = Chain.getSCellIndex(this.endNodeData);
		final int otherCellIndex = Chain.getSCellIndex(nodeData);
		// The cells are different, so the candidates must be the same. (If an
		// annotation is valid, it's length must be long enough that it connects two
		// cells).
		final int candidate = Chain.getSCandidate(this.startNodeData) - 1;
		final int otherCandidate = Chain.getSCandidate(nodeData) - 1;

		if ((otherCellIndex != startCellIndex && otherCellIndex != endCellIndex) || candidate != otherCandidate) {
			final int startCellRow = startCellIndex / SudokuPuzzleValues.CELLS_PER_HOUSE;
			final int startCellCol = startCellIndex % SudokuPuzzleValues.CELLS_PER_HOUSE;
			final int endCellRow = endCellIndex / SudokuPuzzleValues.CELLS_PER_HOUSE;
			final int endCellCol = endCellIndex % SudokuPuzzleValues.CELLS_PER_HOUSE;
			final int startCandidateRow = 3 * startCellRow + (candidate / 3);
			final int startCandidateCol = 3 * startCellCol + (candidate % 3);
			if (startCellRow == endCellRow) {
				// Start and end cells in same row.
				final int otherCellCol = otherCellIndex % SudokuPuzzleValues.CELLS_PER_HOUSE;
				final int otherCandidateCol = 3 * otherCellCol + (otherCandidate % 3);
				final int endCandidateCol = 3 * endCellCol + (candidate % 3);
				intersects = this.rowIntersectsWith(startCandidateRow, nodeData)
						&& this.valueIsBetween(otherCandidateCol, startCandidateCol, endCandidateCol);
			} else if (startCellCol == endCellCol) {
				// Start and end cells in same column.
				final int otherCellRow = otherCellIndex / SudokuPuzzleValues.CELLS_PER_HOUSE;
				final int otherCandidateRow = 3 * otherCellRow + (otherCandidate / 3);
				final int endCandidateRow = 3 * endCellRow + (candidate / 3);
				intersects = this.colIntersectsWith(startCandidateCol, nodeData)
						&& this.valueIsBetween(otherCandidateRow, startCandidateRow, endCandidateRow);
			}
		}

		return intersects;
	}

	/**
	 * Returns if this annotation should be shown. Annotations which start and end
	 * in the same cells are not shown.
	 */
	@Override
	public boolean isValid() {
		return this.valid;
	}

	@Override
	public int getStartNodeData() {
		return this.startNodeData;
	}

	@Override
	public int getEndNodeData() {
		return this.endNodeData;
	}

	/**
	 * Returns true iff the given candidate row (0 - 26, or 3 rows per each of the 9
	 * rows of cells) intersects with either candidate in the given link.
	 *
	 */
	private boolean rowIntersectsWith(final int candidateRow, final int nodeData) {
		final int otherCellIndex = Chain.getSCellIndex(nodeData);
		final int otherCellRow = otherCellIndex / SudokuPuzzleValues.CELLS_PER_HOUSE;
		final int otherCandidate = Chain.getSCandidate(nodeData) - 1;
		return candidateRow == 3 * otherCellRow + (otherCandidate / 3);
	}

	/**
	 * Returns true iff the given candidate column (0 - 26, or 3 columns per each of
	 * the 9 rows of cells) intersects with either candidate in the given link.
	 *
	 */
	private boolean colIntersectsWith(final int candidateCol, final int nodeData) {
		final int otherCellIndex = Chain.getSCellIndex(nodeData);
		final int otherCellCol = otherCellIndex % SudokuPuzzleValues.CELLS_PER_HOUSE;
		final int otherCandidate = Chain.getSCandidate(nodeData) - 1;
		return candidateCol == 3 * otherCellCol + (otherCandidate % 3);
	}

	/**
	 * Returns true iff the given candidate row or column is between the given start
	 * and end rows / columns.
	 */
	private boolean valueIsBetween(final int candidatePos, final int startCellPos, final int endCellPos) {
		return (startCellPos < candidatePos && candidatePos < endCellPos)
				|| (endCellPos < candidatePos && candidatePos < startCellPos);
	}

}
