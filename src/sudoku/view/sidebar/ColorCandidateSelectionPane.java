package sudoku.view.sidebar;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Polygon;
import sudoku.view.util.LabelConstants;

/**
 * This class corresponds to the combo box in the bottom left of the view. It
 * allows the user to change the mode of the mouse: select cells, color cells,
 * color candidates.
 */
public class ColorCandidateSelectionPane extends HBox {

	private static final String CSS_CLASS = "sudoku-transparent-pane";

	private static final int DEFAULT_WIDTH = 320;

	private static final String CANDIDATE_TO_COLOR_CSS_CLASS = "sudoku-candidate-to-color-text-area";

	private static final String BUTTON_CSS_CLASS = "sudoku-candidate-increment-decrement-button";

	public ColorCandidateSelectionPane() {
		this.configure();
	}

	private void configure() {
		this.getStyleClass().add(CSS_CLASS);
		this.setPadding(new Insets(15));
		this.setMinWidth(DEFAULT_WIDTH);
		this.setMaxWidth(DEFAULT_WIDTH);
		this.createChildElements();
	}

	private void createChildElements() {
		final Label label = new Label(LabelConstants.CANDIDATE_TO_COLOR);
		label.setWrapText(true);
		label.setMaxWidth(90);
		label.setMaxHeight(50);
		final TextArea candidateToColorInput = new TextArea("5");
		candidateToColorInput.getStyleClass().add(CANDIDATE_TO_COLOR_CSS_CLASS);
		candidateToColorInput.setEditable(false);
		candidateToColorInput.setMinWidth(62);
		candidateToColorInput.setMaxWidth(62);
		candidateToColorInput.setMinHeight(62);
		candidateToColorInput.setMaxHeight(62);
		HBox.setMargin(candidateToColorInput, new Insets(0, 10, 0, 3));
		final VBox buttonPanel = new VBox();
		final Button increaseNumberButton = new Button("^");
		increaseNumberButton.setMaxHeight(15);
		increaseNumberButton.setMaxWidth(62);
		increaseNumberButton.setMinWidth(62);
		increaseNumberButton.getStyleClass().add(BUTTON_CSS_CLASS);
		final Button decreaseNumberButton = new Button();
		decreaseNumberButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

		// TODO - make this copy the combo box's arrow.
		// Also extract all this into methods / constants.
		final Polygon polygon = new Polygon();
		polygon.getPoints().addAll(new Double[] { 0.0, 0.0, 20.0, 10.0, 10.0, 20.0 });
		polygon.setFill(Paint.valueOf("red"));

		decreaseNumberButton.setGraphic(polygon);
		decreaseNumberButton.setMaxHeight(15);
		decreaseNumberButton.setMaxWidth(62);
		decreaseNumberButton.setMinWidth(62);
		decreaseNumberButton.getStyleClass().add(BUTTON_CSS_CLASS);
		buttonPanel.getChildren().addAll(increaseNumberButton, decreaseNumberButton);
		this.getChildren().addAll(label, candidateToColorInput, buttonPanel);
	}

}
