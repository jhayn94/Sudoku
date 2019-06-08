package sudoku.view.hint;

import javafx.geometry.Bounds;
import javafx.scene.control.Label;
import javafx.scene.shape.Line;
import sudoku.Chain;
import sudoku.core.ViewController;
import sudoku.model.SudokuPuzzleValues;
import sudoku.view.puzzle.SudokuPuzzleCell;
import sudoku.view.util.ColorUtils;

/**
 * This class corresponds to the lines / arrows drawn on the sudoku puzzle. Its
 * data is derived from {@link Chain}. Unfortunately, this is a quite
 * complicated class with all the bit-wise operations. This class attempts to
 * abstract as much of that away as possible so it is easy to create
 * annotations.
 */
public class HintAnnotation extends Line {

	private static final int COORDINATE_ERROR_OFFSET = 10;

	private static final double DASHED_LINE_ON_LENGTH = 5.0;

	private static final double DASED_LINE_OFF_LENGTH = 10.0;

	private static final double LINE_WIDTH = 3.0;

	private final int startNodeData;

	private final int endNodeData;

	private boolean valid;

	public HintAnnotation(final int startNodeData, final int endNodeData) {
		super();
		this.valid = true;
		this.startNodeData = startNodeData;
		this.endNodeData = endNodeData;
		this.configure();
	}

	private void configure() {
		this.setStrokeWidth(LINE_WIDTH);
		this.getStyleClass().add(ColorUtils.HINT_COLOR_4_CSS_CLASS);
		if (!Chain.isSStrong(this.endNodeData)) {
			this.getStrokeDashArray().addAll(DASHED_LINE_ON_LENGTH, DASED_LINE_OFF_LENGTH);
		}

		final int startCellIndex = Chain.getSCellIndex(this.startNodeData);
		final int endCellIndex = Chain.getSCellIndex(this.endNodeData);
		if (this.endNodeData != Integer.MIN_VALUE && startCellIndex != endCellIndex) {
			this.setInitialCoordinates(startCellIndex, endCellIndex);
		} else {
			this.valid = false;
		}
	}

	private void setInitialCoordinates(final int startCellIndex, final int endCellIndex) {
		final int startRow = startCellIndex / SudokuPuzzleValues.CELLS_PER_HOUSE;
		final int startCol = startCellIndex % SudokuPuzzleValues.CELLS_PER_HOUSE;
		final int endRow = endCellIndex / SudokuPuzzleValues.CELLS_PER_HOUSE;
		final int endCol = endCellIndex % SudokuPuzzleValues.CELLS_PER_HOUSE;
		final int startCandidate = Chain.getSCandidate(this.startNodeData);
		final int endCandidate = Chain.getSCandidate(this.endNodeData);
		final SudokuPuzzleCell startCell = ViewController.getInstance().getSudokuPuzzleCell(startRow, startCol);
		final SudokuPuzzleCell endCell = ViewController.getInstance().getSudokuPuzzleCell(endRow, endCol);
		final Label startCandidateLabel = startCell.getCandidateLabelForDigit(startCandidate);
		final Label endCandidateLabel = endCell.getCandidateLabelForDigit(endCandidate);
		final Bounds startBounds = startCandidateLabel.localToScreen(startCandidateLabel.getBoundsInLocal());
		final Bounds endBounds = endCandidateLabel.localToScreen(endCandidateLabel.getBoundsInLocal());
		final double startX = (startBounds.getMinX() + startBounds.getMaxX()) / 2.0;
		final double startY = (startBounds.getMinY() + startBounds.getMaxY()) / 2.0;
		final double endX = (endBounds.getMinX() + endBounds.getMaxX()) / 2.0;
		final double endY = (endBounds.getMinY() + endBounds.getMaxY()) / 2.0;
		// This math was just off by a little bit each time. After awhile of trying to
		// figure out why, I instead adjusted the end result to line up with the center
		// of the label. It most likely has something to do with padding or borders.
		this.setStartX(startX - COORDINATE_ERROR_OFFSET);
		this.setStartY(startY - COORDINATE_ERROR_OFFSET);
		this.setEndX(endX - COORDINATE_ERROR_OFFSET);
		this.setEndY(endY - COORDINATE_ERROR_OFFSET);
	}

	/**
	 * Returns if this annotation should be shown. Annotations which start and end
	 * in the same cells are not shown.
	 */
	public boolean isValid() {
		return this.valid;
	}

}
