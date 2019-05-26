package sudoku.view.sidebar;

import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import sudoku.factories.LayoutFactory;

/**
 * This class corresponds to the two up / down arrow toggles in the bottom left
 * of the screen. These controls define the color and digit colored when in the
 * appropriate mouse mode.
 */
public class MouseToolsPane extends HBox {

	private static final int PADDING_BETWEEN_CHILDREN = 5;

	private static final int PADDING_FOR_PANE = 15;

	private static final String CSS_CLASS = "sudoku-transparent-pane";

	private static final int DEFAULT_WIDTH = 320;

	public MouseToolsPane() {
		this.configure();
	}

	private void configure() {
		this.getStyleClass().add(CSS_CLASS);
		this.setPadding(new Insets(PADDING_FOR_PANE, PADDING_FOR_PANE, PADDING_FOR_PANE, PADDING_FOR_PANE - 3));
		this.setMinWidth(DEFAULT_WIDTH);
		this.setMaxWidth(DEFAULT_WIDTH);
		this.createChildElements();
	}

	private void createChildElements() {
		final CandidateSelectionPane colorCandidateSelectionPane = LayoutFactory.getInstance()
				.createColorCandidateSelectionPane();
		HBox.setMargin(colorCandidateSelectionPane, new Insets(0, PADDING_BETWEEN_CHILDREN, 0, 0));
		final ColorSelectionPane colorSelectionPane = LayoutFactory.getInstance().createColorSelectionPane();
		this.getChildren().addAll(colorCandidateSelectionPane, colorSelectionPane);
	}

}
