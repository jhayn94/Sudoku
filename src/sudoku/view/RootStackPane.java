package sudoku.view;

import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import sudoku.factories.LayoutFactory;
import sudoku.view.util.ShadowRectangle;

/**
 * This class is intended to be the top level root element of any stage created
 * in the application. It defines a shadowed rectangle around the window, as
 * well as some CSS style-able padding area.
 */
public class RootStackPane extends StackPane {

	private static final String CSS_CLASS = "sudoku-transparent-pane";

	private static final int ROUNDED_DELTA = 0;

	private static final int SHADOW_WIDTH = 15;

	private final ShadowRectangle shadowRectangle;

	private final Region applicationView;

	public RootStackPane(Region applicationView) {
		this.applicationView = applicationView;
		applicationView.setPadding(new Insets(10));
		this.shadowRectangle = LayoutFactory.getInstance().createShadowRectangle();
		this.getStyleClass().add(CSS_CLASS);
		this.getChildren().addAll(this.shadowRectangle, applicationView);
	}

	/** Resizes elements to make the shadow visible. */
	@Override
	public void layoutChildren() {
		final Bounds b = super.getLayoutBounds();
		final double w = b.getWidth();
		final double h = b.getHeight();
		this.shadowRectangle.setWidth(w - SHADOW_WIDTH * 2);
		this.shadowRectangle.setHeight(h - SHADOW_WIDTH * 2);
		this.shadowRectangle.setX(SHADOW_WIDTH);
		this.shadowRectangle.setY(SHADOW_WIDTH);
		this.applicationView.resize(w - SHADOW_WIDTH * 2 - ROUNDED_DELTA * 2, h - SHADOW_WIDTH * 2 - ROUNDED_DELTA * 2);
		this.applicationView.setLayoutX(SHADOW_WIDTH + ROUNDED_DELTA);
		this.applicationView.setLayoutY(SHADOW_WIDTH + ROUNDED_DELTA);
	}
}
