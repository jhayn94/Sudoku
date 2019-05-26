package sudoku.view.sidebar;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Polygon;
import sudoku.core.ModelController;
import sudoku.core.ViewController;
import sudoku.view.util.TooltipConstants;

/**
 * This class corresponds to the combo box in the bottom left of the view. It
 * allows the user to change the mode of the mouse: select cells, color cells,
 * color candidates.
 */
public class ColorCandidateSelectionPane extends HBox {

	private static final int TEXT_FIELD_SIZE = 62;

	private static final String DEFAULT_COLORING_CANDIDATE = "1";

	private static final int BUTTON_HEIGHT = 15;

	private static final int BUTTON_WIDTH = 62;

	private static final String STONE_BLUE_HEX_CODE = "#336b87";

	private static final Double[] UP_ARROW_VERTICES = new Double[] { -5.5, 0.0, 5.5, 0.0, 0.0, -7.0 };

	private static final Double[] DOWN_ARROW_VERTICES = new Double[] { -5.5, 0.0, 5.5, 0.0, 0.0, 7.0 };

	private static final int TEXT_AREA_LEFT_PADDING = 3;

	private static final int TEXT_AREA_RIGHT_PADDING = 10;

	private static final int PADDING_BETWEEN_BUTTONS = 5;

	private static final String CSS_CLASS = "sudoku-transparent-pane";

	private static final int DEFAULT_WIDTH = 160;

	private static final String SIDE_BAR_TEXT_AREA_CSS_CLASS = "sudoku-side-bar-text-area";

	private static final String BUTTON_CSS_CLASS = "sudoku-candidate-increment-decrement-button";

	public ColorCandidateSelectionPane() {
		this.configure();
	}

	private void configure() {
		this.getStyleClass().add(CSS_CLASS);
		this.setMinWidth(DEFAULT_WIDTH);
		this.setMaxWidth(DEFAULT_WIDTH);
		this.createChildElements();
	}

	private void createChildElements() {
		final TextArea candidateToColorDisplayArea = this.createCurrentCandidateDisplayField();
		final VBox buttonPanel = this.createChangeCandidateButtonPanel();
		this.getChildren().addAll(candidateToColorDisplayArea, buttonPanel);
	}

	private TextArea createCurrentCandidateDisplayField() {
		final TextArea candidateToColorDisplayArea = new TextArea(DEFAULT_COLORING_CANDIDATE);
		candidateToColorDisplayArea.getStyleClass().add(SIDE_BAR_TEXT_AREA_CSS_CLASS);
		candidateToColorDisplayArea.setEditable(false);
		candidateToColorDisplayArea.setTooltip(new Tooltip(TooltipConstants.ACTIVE_COLORING_CANDIDATE));
		candidateToColorDisplayArea.setMinWidth(TEXT_FIELD_SIZE);
		candidateToColorDisplayArea.setMaxWidth(TEXT_FIELD_SIZE);
		candidateToColorDisplayArea.setMinHeight(TEXT_FIELD_SIZE);
		candidateToColorDisplayArea.setMaxHeight(TEXT_FIELD_SIZE);
		candidateToColorDisplayArea.setFocusTraversable(false);
		HBox.setMargin(candidateToColorDisplayArea, new Insets(0, TEXT_AREA_RIGHT_PADDING, 0, TEXT_AREA_LEFT_PADDING));
		ViewController.getInstance().setActiveColoringCandidateTextArea(candidateToColorDisplayArea);
		return candidateToColorDisplayArea;
	}

	private VBox createChangeCandidateButtonPanel() {
		final VBox buttonPanel = new VBox();
		final Button incrementNumberButton = this.createIncrementButton();
		final Button decrementNumberButton = this.createDecrementButton();
		VBox.setMargin(decrementNumberButton, new Insets(PADDING_BETWEEN_BUTTONS, 0, 0, 0));
		buttonPanel.getChildren().addAll(incrementNumberButton, decrementNumberButton);
		return buttonPanel;
	}

	private Button createIncrementButton() {
		final Button incrementNumberButton = new Button();
		incrementNumberButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		incrementNumberButton.getStyleClass().add(BUTTON_CSS_CLASS);
		incrementNumberButton.setMaxHeight(BUTTON_HEIGHT);
		incrementNumberButton.setMaxWidth(BUTTON_WIDTH);
		incrementNumberButton.setMinWidth(BUTTON_WIDTH);
		incrementNumberButton.setFocusTraversable(false);
		incrementNumberButton.setOnAction(
				event -> ModelController.getInstance().transitionToToggleActiveCandidateToColorState(KeyCode.PAGE_UP));
		final Polygon upArrowPolygon = new Polygon();
		upArrowPolygon.getPoints().addAll(UP_ARROW_VERTICES);
		upArrowPolygon.setFill(Paint.valueOf(STONE_BLUE_HEX_CODE));
		incrementNumberButton.setGraphic(upArrowPolygon);
		return incrementNumberButton;
	}

	private Button createDecrementButton() {
		final Button decrementNumberButton = new Button();
		decrementNumberButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		decrementNumberButton.getStyleClass().add(BUTTON_CSS_CLASS);
		decrementNumberButton.setMaxHeight(BUTTON_HEIGHT);
		decrementNumberButton.setMaxWidth(BUTTON_WIDTH);
		decrementNumberButton.setMinWidth(BUTTON_WIDTH);
		decrementNumberButton.setFocusTraversable(false);
		decrementNumberButton.setOnAction(
				event -> ModelController.getInstance().transitionToToggleActiveCandidateToColorState(KeyCode.PAGE_DOWN));
		final Polygon downArrowPolygon = new Polygon();
		downArrowPolygon.getPoints().addAll(DOWN_ARROW_VERTICES);
		downArrowPolygon.setFill(Paint.valueOf(STONE_BLUE_HEX_CODE));
		decrementNumberButton.setGraphic(downArrowPolygon);
		return decrementNumberButton;
	}

}
