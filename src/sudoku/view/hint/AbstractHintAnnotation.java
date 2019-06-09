package sudoku.view.hint;

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

	protected final int startNodeData;

	protected final int endNodeData;

	protected boolean valid;

	public AbstractHintAnnotation(final int startNodeData, final int endNodeData) {
		this.valid = true;
		this.startNodeData = startNodeData;
		this.endNodeData = endNodeData;
	}

	/**
	 * Returns if this annotation should be shown. Annotations which start and end
	 * in the same cells are not shown.
	 */
	@Override
	public boolean isValid() {
		return this.valid;
	}
}
