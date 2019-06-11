package sudoku.view.hint;

import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;
import sudoku.Chain;

/**
 * This class corresponds to the lines / arrows drawn on the sudoku puzzle. Its
 * data is derived from {@link Chain}. Unfortunately, {@link Chain} a very
 * complicated class with all the bit-wise operations. This class attempts to
 * abstract as much of that away as possible so it is easy to create
 * annotations.
 */
public interface HintAnnotation {

	public boolean isValid();

	/**
	 * Returns the entity that represents the line component of the annotation. This
	 * may be a straight line, or a curve.
	 */
	public Shape getAnnotationBody();

	public Polygon getArrowHead();

	public int getStartNodeData();

	public int getEndNodeData();

	public boolean intersectsWith(int nodeData);
}
