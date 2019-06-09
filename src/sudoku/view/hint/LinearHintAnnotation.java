package sudoku.view.hint;

import javafx.geometry.Bounds;
import javafx.scene.control.Label;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import sudoku.Chain;
import sudoku.core.ViewController;
import sudoku.model.SudokuPuzzleValues;
import sudoku.view.puzzle.SudokuPuzzleCell;
import sudoku.view.util.ColorUtils;

/**
 * This class corresponds to the lines / arrows drawn on the sudoku puzzle. Its
 * data is derived from {@link Chain}. Unfortunately, {@link Chain} a very
 * complicated class with all the bit-wise operations. This class attempts to
 * abstract as much of that away as possible so it is easy to create
 * annotations.
 */
public class LinearHintAnnotation implements HintAnnotation {

	protected static final double COORDINATE_ERROR_X_OFFSET = 9;

	protected static final double COORDINATE_ERROR_Y_OFFSET = 11;

	protected static final double DASHED_LINE_ON_LENGTH = 5.0;

	protected static final double DASHED_LINE_OFF_LENGTH = 7.5;

	protected static final double LINE_WIDTH = 3.0;

	// Approximation of the width of a candidate label. They are a 16x14 pixel
	// rectangle, so obviously this is a rough approximation. This was calculated by
	// 16 (the large dimension) / 2 + a little extra since the rectangle is longer
	// from corner to corner.
	private static final double LABEL_RADIUS = 10;

	protected static final double ARROW_SIDE_LENGTH = 4;

	protected final int startNodeData;

	protected final int endNodeData;

	protected boolean valid;

	protected final Line line;

	protected final Polygon arrowHead;

	private double adjustedYEnd;

	private double adjustedXEnd;

	public LinearHintAnnotation(final int startNodeData, final int endNodeData) {
		this.valid = true;
		this.startNodeData = startNodeData;
		this.endNodeData = endNodeData;
		this.line = new Line();
		this.arrowHead = new Polygon();
		this.configure();
	}

	protected void configure() {
		this.line.setStrokeWidth(LINE_WIDTH);
		this.line.getStyleClass().add(ColorUtils.HINT_COLOR_4_CSS_CLASS);
		this.line.setStrokeLineJoin(StrokeLineJoin.ROUND);
		this.line.setStrokeLineCap(StrokeLineCap.ROUND);
		this.arrowHead.getStyleClass().add(ColorUtils.HINT_COLOR_4_CSS_CLASS);
		this.arrowHead.setStrokeLineJoin(StrokeLineJoin.ROUND);
		this.arrowHead.setStrokeLineCap(StrokeLineCap.ROUND);
		if (!Chain.isSStrong(this.endNodeData)) {
//			this.line.getStrokeDashArray().addAll(DASHED_LINE_ON_LENGTH, DASHED_LINE_OFF_LENGTH);
		}

		final int startCellIndex = Chain.getSCellIndex(this.startNodeData);
		final int endCellIndex = Chain.getSCellIndex(this.endNodeData);
		if (this.endNodeData != Integer.MIN_VALUE && startCellIndex != endCellIndex) {
			this.setInitialCoordinates(startCellIndex, endCellIndex);
			this.adjustPoints();
			this.drawArrowPointer();
		} else {
			this.valid = false;
		}
	}

	protected void drawArrowPointer() {
		final double alpha = Math.atan2(this.line.getEndY() - this.line.getStartY(),
				this.line.getEndX() - this.line.getStartX());
		final double sin = Math.sin(alpha);
		final double cos = Math.cos(alpha);

		final double aX = this.line.getEndX() - cos * ARROW_SIDE_LENGTH;
		final double aY = this.line.getEndY() - sin * ARROW_SIDE_LENGTH;
		final double daX = sin * ARROW_SIDE_LENGTH;
		final double daY = cos * ARROW_SIDE_LENGTH;
		final double x1 = aX - daX;
		final double y1 = aY + daY;
		final double x2 = this.adjustedXEnd;
		final double y2 = this.adjustedYEnd;
		final double x3 = aX + daX;
		final double y3 = aY - daY;
		this.arrowHead.getPoints().addAll(x1, y1, x2, y2, x3, y3);

	}

	/**
	 * Sets the initial start / end points of the line. These points will be used as
	 * the starting point for a few additional calculations.
	 */
	protected void setInitialCoordinates(final int startCellIndex, final int endCellIndex) {
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
		this.line.setStartX(startX - COORDINATE_ERROR_X_OFFSET);
		this.line.setStartY(startY - COORDINATE_ERROR_Y_OFFSET);
		this.line.setEndX(endX - COORDINATE_ERROR_X_OFFSET);
		this.line.setEndY(endY - COORDINATE_ERROR_Y_OFFSET);
	}

	/**
	 * Adjust the end points of an arrow: the arrow should start and end outside the
	 * circular background of the candidate.
	 */
	protected void adjustPoints() {
		final double deltaX = this.line.getEndX() - this.line.getStartX();
		final double deltaY = this.line.getEndY() - this.line.getStartY();
		final double alpha = Math.atan2(deltaY, deltaX);
		final double xOffset = LABEL_RADIUS * Math.cos(alpha);
		final double yOffset = LABEL_RADIUS * Math.sin(alpha);
		this.line.setStartX(this.line.getStartX() + xOffset);
		this.line.setStartY(this.line.getStartY() + yOffset);
		this.line.setEndX(this.line.getEndX() - xOffset);
		this.line.setEndY(this.line.getEndY() - yOffset);
		this.adjustedXEnd = this.line.getEndX();
		this.adjustedYEnd = this.line.getEndY();
		this.line.setEndX(this.line.getEndX() - xOffset * 1.2);
		this.line.setEndY(this.line.getEndY() - yOffset * 1.2);
	}

	/**
	 * Returns if this annotation should be shown. Annotations which start and end
	 * in the same cells are not shown.
	 */
	@Override
	public boolean isValid() {
		return this.valid;
	}

	/**
	 * Returns the entity that represents the line component of the annotation. This
	 * may be a straight line, or a curve.
	 */
	@Override
	public Shape getAnnotationBody() {
		return this.line;
	}

	@Override
	public Polygon getArrowHead() {
		return this.arrowHead;
	}

}
