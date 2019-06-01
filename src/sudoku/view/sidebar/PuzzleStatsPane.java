package sudoku.view.sidebar;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import sudoku.view.util.LabelConstants;
import sudoku.view.util.TooltipConstants;

/**
 * This class corresponds to the various puzzle data in the bottom left corner
 * of the view.
 */
public class PuzzleStatsPane extends GridPane {

	private static final String UNEDITABLE_TEXT_FIELD_CSS_CLASS = "sudoku-uneditable-text-field";

	private static final String CSS_CLASS = "sudoku-transparent-pane";

	private static final int DEFAULT_WIDTH = 300;

	private static final int DEFAULT_HEIGHT = 116;

	private static final int MAX_TEXT_FIELD_WIDTH = 137;

	private TextField difficultyTextField;

	private TextField ratingTextField;

	private TextField remainingRatingTextField;

	public PuzzleStatsPane() {
		this.configure();
	}

	private void configure() {
		this.getStyleClass().add(CSS_CLASS);
		this.setMinWidth(DEFAULT_WIDTH);
		this.setMaxWidth(DEFAULT_WIDTH);
		this.setMinHeight(DEFAULT_HEIGHT);
		this.setMaxHeight(DEFAULT_HEIGHT);
		this.createChildElements();
	}

	private void createChildElements() {
		final GridPane statsGridPane = new GridPane();
		statsGridPane.setVgap(12);
		statsGridPane.setHgap(60);
		final Label difficultyLabel = new Label(LabelConstants.DIFFICULTY + ":");
		statsGridPane.add(difficultyLabel, 0, 0);
		this.difficultyTextField = new TextField();
		this.difficultyTextField.setFocusTraversable(false);
		this.difficultyTextField.setEditable(false);
		this.difficultyTextField.getStyleClass().add(UNEDITABLE_TEXT_FIELD_CSS_CLASS);
		this.difficultyTextField.setMaxWidth(MAX_TEXT_FIELD_WIDTH);
		this.difficultyTextField.setTooltip(new Tooltip(TooltipConstants.DIFFICULTY_DISPLAY));
		statsGridPane.add(this.difficultyTextField, 1, 0);
		final Label ratingLabel = new Label(LabelConstants.RATING);
		statsGridPane.add(ratingLabel, 0, 1);
		this.ratingTextField = new TextField();
		this.ratingTextField.setFocusTraversable(false);
		this.ratingTextField.setEditable(false);
		this.ratingTextField.getStyleClass().add(UNEDITABLE_TEXT_FIELD_CSS_CLASS);
		this.ratingTextField.setMaxWidth(MAX_TEXT_FIELD_WIDTH);
		this.ratingTextField.setTooltip(new Tooltip(TooltipConstants.RATING));
		statsGridPane.add(this.ratingTextField, 1, 1);
		final Label remainingRatingLabel = new Label(LabelConstants.REMAINING_RATING);
		statsGridPane.add(remainingRatingLabel, 0, 2);
		this.remainingRatingTextField = new TextField();
		this.remainingRatingTextField.setFocusTraversable(false);
		this.remainingRatingTextField.setEditable(false);
		this.remainingRatingTextField.getStyleClass().add(UNEDITABLE_TEXT_FIELD_CSS_CLASS);
		this.remainingRatingTextField.setMaxWidth(MAX_TEXT_FIELD_WIDTH);
		this.remainingRatingTextField.setTooltip(new Tooltip(TooltipConstants.REMAINING_RATING));
		statsGridPane.add(this.remainingRatingTextField, 1, 2);
		this.getChildren().add(statsGridPane);
	}

	public TextField getDifficultyTextField() {
		return this.difficultyTextField;
	}

	public TextField getRatingTextField() {
		return this.ratingTextField;
	}

	public TextField getRemainingRatingTextField() {
		return this.remainingRatingTextField;
	}

}
