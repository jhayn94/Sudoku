package sudoku.view.hint;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import sudoku.factories.LayoutFactory;
import sudoku.view.util.LabelConstants;

/**
 * This class corresponds to the 4 x 3 button grid on the top left of the
 * screen. It contains numeric buttons 1 - 9 , which highlight cells where those
 * digits could go. It also contains X|Y, which highlights bivalue cells.
 * Lastly, this class creates buttons labeled "<" and ">", which are undo and
 * redo, respectively.
 */
public class HintPane extends VBox {

	private static final int PADDING_BETWEEN_CHILDREN = 15;

	private static final int PADDING_FOR_PANE = 15;

	private static final String CSS_CLASS = "sudoku-transparent-pane";

	private static final int DEFAULT_WIDTH = 300;

	public HintPane() {
		this.configure();
	}

	private void configure() {
		this.getStyleClass().add(CSS_CLASS);
		this.setPadding(new Insets(PADDING_FOR_PANE));
		this.setMinWidth(DEFAULT_WIDTH);
		this.setMaxWidth(DEFAULT_WIDTH);
		this.createChildElements();
	}

	private void createChildElements() {
		final Label label = new Label(LabelConstants.HINT);
		VBox.setMargin(label, new Insets(0, 0, PADDING_BETWEEN_CHILDREN, 0));
		final HintButtonPane hintButtonPane = LayoutFactory.getInstance().createHintButtonPane();
		VBox.setMargin(hintButtonPane, new Insets(0, 0, PADDING_BETWEEN_CHILDREN, 0));
		final HintTextArea hintTextArea = LayoutFactory.getInstance().createHintTextArea();
		this.getChildren().addAll(label, hintButtonPane, hintTextArea);
	}

}
