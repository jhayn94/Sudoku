package sudoku.view.util;

import javafx.beans.value.ChangeListener;
import javafx.geometry.Bounds;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class ShadowRectangle extends Rectangle {

	private static final int SHADOW_WIDTH = 15;

	private static final String SHADOW_RECTANGLE_STYLE_CLASS = "decoration-shadow";

	public ShadowRectangle() {
		super();
		this.configure();
	}

	private void configure() {
		this.layoutBoundsProperty().addListener(
				(ChangeListener<Bounds>) (observable, oldBounds, newBounds) -> {
					ShadowRectangle.this.setVisible(true);
				});
		this.setMouseTransparent(true);
		this.getStyleClass().add(SHADOW_RECTANGLE_STYLE_CLASS);
		final DropShadow dropShadow = new DropShadow(BlurType.THREE_PASS_BOX, Color.BLACK,
				SHADOW_WIDTH, 0.1, 0, 0);
		this.setEffect(dropShadow);
	}
}
