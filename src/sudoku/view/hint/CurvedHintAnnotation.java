package sudoku.view.hint;

import javafx.geometry.Bounds;
import javafx.scene.control.Label;
import javafx.scene.shape.CubicCurve;
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
 * This class is an implementation of {@link HintAnnotation} where the
 * annotation body is a straight line.
 */
public class CurvedHintAnnotation extends AbstractHintAnnotation {

	private static final int CURVE_OFFSET = 10;

	private static final double CURVE_LENGTH = 30.0;

	protected final CubicCurve cubicCurve;

	protected final Polygon arrowHead;

	private double adjustedYEnd;

	private double adjustedXEnd;

	public CurvedHintAnnotation(final int startNodeData, final int endNodeData) {
		super(startNodeData, endNodeData);
		this.valid = true;
		this.cubicCurve = new CubicCurve();
		this.arrowHead = new Polygon();
		this.configure();
	}

	protected void configure() {
		this.cubicCurve.setStrokeWidth(LINE_WIDTH);
		this.cubicCurve.getStyleClass().add(ColorUtils.HINT_COLOR_4_CSS_CLASS);
		this.cubicCurve.setStrokeLineJoin(StrokeLineJoin.ROUND);
		this.cubicCurve.setStrokeLineCap(StrokeLineCap.ROUND);
		this.cubicCurve.setFill(null);
		this.arrowHead.getStyleClass().add(ColorUtils.HINT_COLOR_4_CSS_CLASS_ARROW);
		this.arrowHead.setStrokeLineJoin(StrokeLineJoin.ROUND);
		this.arrowHead.setStrokeLineCap(StrokeLineCap.ROUND);
		if (!Chain.isSStrong(this.getEndNodeData())) {
			this.cubicCurve.getStrokeDashArray().addAll(DASHED_LINE_ON_LENGTH, DASHED_LINE_OFF_LENGTH);
		}
		final int startCellIndex = Chain.getSCellIndex(this.getStartNodeData());
		final int endCellIndex = Chain.getSCellIndex(this.getEndNodeData());
		if (this.getEndNodeData() != Integer.MIN_VALUE && startCellIndex != endCellIndex) {
			this.setInitialCoordinates(startCellIndex, endCellIndex);
			this.setControlPoints();
			this.adjustPoints();
			this.drawArrowPointer();
		} else {
			this.valid = false;
		}
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
		final int startCandidate = Chain.getSCandidate(this.getStartNodeData());
		final int endCandidate = Chain.getSCandidate(this.getEndNodeData());
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
		this.cubicCurve.setStartX(startX - COORDINATE_ERROR_X_OFFSET);
		this.cubicCurve.setStartY(startY - COORDINATE_ERROR_Y_OFFSET);
		this.cubicCurve.setEndX(endX - COORDINATE_ERROR_X_OFFSET);
		this.cubicCurve.setEndY(endY - COORDINATE_ERROR_Y_OFFSET);
	}

	/**
	 * Adjust the end points of an arrow: the arrow should start and end outside the
	 * circular background of the candidate.
	 */
	protected void adjustPoints() {
		this.movePointsTowardCurveDirection();
		this.movePointsOffLabels();
	}

	protected void drawArrowPointer() {
		final double angleOfLine = Math.atan2(this.cubicCurve.getEndY() - this.cubicCurve.getControlY1(),
				this.cubicCurve.getEndX() - this.cubicCurve.getControlX1());
		// Use the angle of the arrow to calculate points for the arrow vertices.
		final double yVectorOfLine = Math.sin(angleOfLine);
		final double xVectorOfLine = Math.cos(angleOfLine);
		// The next two doubles represent the point at which the arrow stops (the side
		// opposite of the pointer).
		final double xAtIntersectionWithArrow = this.cubicCurve.getEndX() - xVectorOfLine * ARROW_SIDE_LENGTH;
		final double yAtIntersectionWithArrow = this.cubicCurve.getEndY() - yVectorOfLine * ARROW_SIDE_LENGTH;
		// The next two doubles are the x / y components of the deviation from the
		// intersection point above. Thus, the other two end points of the arrow head
		// are the intersection points plus or minus the x and y deviations (using
		// opposite signs both times).
		final double xDeviationFromLine = yVectorOfLine * ARROW_SIDE_LENGTH;
		final double yDeviationFromLine = xVectorOfLine * ARROW_SIDE_LENGTH;
		// The 3 points of the triangle which make up the arrow head.
		final double x1 = xAtIntersectionWithArrow - xDeviationFromLine;
		final double y1 = yAtIntersectionWithArrow + yDeviationFromLine;
		final double x2 = this.adjustedXEnd;
		final double y2 = this.adjustedYEnd;
		final double x3 = xAtIntersectionWithArrow + xDeviationFromLine;
		final double y3 = yAtIntersectionWithArrow - yDeviationFromLine;
		this.arrowHead.getPoints().addAll(x1, y1, x2, y2, x3, y3);
	}

	/** Defines the control points of the curve. */
	private void setControlPoints() {
		final double deltaX = this.cubicCurve.getEndX() - this.cubicCurve.getStartX();
		final double deltaY = this.cubicCurve.getEndY() - this.cubicCurve.getStartY();
		final double angleOfLine = Math.atan2(deltaY, deltaX);
		double radians = angleOfLine - Math.PI / 4.0;
		double bezierLength = CURVE_LENGTH;

		// Adjust for very short lines
		final double lineDistance = Math.hypot(this.cubicCurve.getStartX() - this.cubicCurve.getEndX(),
				this.cubicCurve.getStartY() - this.cubicCurve.getEndY());
		if (lineDistance < 2.0 * LABEL_RADIUS) {
			bezierLength = lineDistance / 4.0;
		}
		final double controlPointX1 = this.cubicCurve.getStartX() + bezierLength * Math.cos(radians);
		final double controlPointY1 = this.cubicCurve.getStartY() + bezierLength * Math.sin(radians);
		this.cubicCurve.setControlX1(controlPointX1);
		this.cubicCurve.setControlY1(controlPointY1);
		radians = angleOfLine + Math.PI / 4.0;
		final double controlPointX2 = this.cubicCurve.getEndX() - bezierLength * Math.cos(radians);
		final double controlPointY2 = this.cubicCurve.getEndY() - bezierLength * Math.sin(radians);
		this.cubicCurve.setControlX2(controlPointX2);
		this.cubicCurve.setControlY2(controlPointY2);
	}

	private void movePointsOffLabels() {
		final double deltaX = this.cubicCurve.getEndX() - this.cubicCurve.getStartX();
		final double deltaY = this.cubicCurve.getEndY() - this.cubicCurve.getStartY();
		final double angleOfLine = Math.atan2(deltaY, deltaX);
		final double xOffset = LABEL_RADIUS * Math.cos(angleOfLine);
		final double yOffset = LABEL_RADIUS * Math.sin(angleOfLine);
		this.cubicCurve.setStartX(this.cubicCurve.getStartX() + xOffset);
		this.cubicCurve.setStartY(this.cubicCurve.getStartY() + yOffset);
		this.cubicCurve.setEndX(this.cubicCurve.getEndX() - xOffset);
		this.cubicCurve.setEndY(this.cubicCurve.getEndY() - yOffset);
		// Store an initial adjusted value for the end node. We want the arrow to end
		// here. Then, further taper the endpoint in so the end of the annotation
		// body is not colliding with the arrow shape.
		this.adjustedXEnd = this.cubicCurve.getEndX();
		this.adjustedYEnd = this.cubicCurve.getEndY();
		this.cubicCurve.setEndX(this.cubicCurve.getEndX() - xOffset * ADDITIONAL_END_POINT_TAPER_FACTOR);
		this.cubicCurve.setEndY(this.cubicCurve.getEndY() - yOffset * ADDITIONAL_END_POINT_TAPER_FACTOR);
	}

	/**
	 * Moves the start and end points of the curve slightly to help prevent the
	 * curve from intersecting with other labels.
	 */
	private void movePointsTowardCurveDirection() {
		if (Math.abs(this.cubicCurve.getStartX() - this.cubicCurve.getEndX()) < .01) {
			if (this.cubicCurve.getStartY() > this.cubicCurve.getEndY()) {
				// Move the arrow left.
				this.cubicCurve.setStartX(this.cubicCurve.getStartX() - CURVE_OFFSET);
				this.cubicCurve.setEndX(this.cubicCurve.getEndX() - CURVE_OFFSET);
			} else {
				this.cubicCurve.setStartX(this.cubicCurve.getStartX() + CURVE_OFFSET);
				this.cubicCurve.setEndX(this.cubicCurve.getEndX() + CURVE_OFFSET);
			}

		} else if (Math.abs(this.cubicCurve.getStartY() - this.cubicCurve.getEndY()) < .01) {
			if (this.cubicCurve.getStartX() > this.cubicCurve.getEndX()) {
				// Move the arrow down.
				this.cubicCurve.setStartY(this.cubicCurve.getStartY() + CURVE_OFFSET);
				this.cubicCurve.setEndY(this.cubicCurve.getEndY() + CURVE_OFFSET);
			} else {
				this.cubicCurve.setStartY(this.cubicCurve.getStartY() - CURVE_OFFSET);
				this.cubicCurve.setEndY(this.cubicCurve.getEndY() - CURVE_OFFSET);
			}

		}
	}

	/**
	 * Returns the entity that represents the line component of the annotation. This
	 * may be a straight line, or a curve.
	 */
	@Override
	public Shape getAnnotationBody() {
		return this.cubicCurve;
	}

	@Override
	public Polygon getArrowHead() {
		return this.arrowHead;
	}

}
