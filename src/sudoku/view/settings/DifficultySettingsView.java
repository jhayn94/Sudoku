package sudoku.view.settings;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.UnaryOperator;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import sudoku.core.ModelController;
import sudoku.model.ApplicationSettings;
import sudoku.model.DefaultApplicationSettings;
import sudoku.view.ModalDialog;
import sudoku.view.util.Difficulty;
import sudoku.view.util.LabelConstants;
import sudoku.view.util.TooltipConstants;

public class DifficultySettingsView extends ModalDialog {

	private static final String DIGITS_ONLY_REGEX = "^\\d*$";

	private static final int BUTTON_PANE_PADDING = 5;

	private static final int SMALL_PADDING = 20;

	private final Map<Difficulty, TextField> maxScoreInputs;

	public DifficultySettingsView(final Stage stage) {
		super(stage);
		this.maxScoreInputs = new EnumMap<>(Difficulty.class);
		this.configure();
	}

	@Override
	protected void configure() {
		this.setTitle(LabelConstants.DIFFICULTY_SETTINGS);
		this.createChildElements();
	}

	@Override
	protected void createChildElements() {
		final VBox contentPane = new VBox();
		contentPane.setAlignment(Pos.TOP_LEFT);
		contentPane.setPadding(new Insets(SMALL_PADDING));
		final GridPane difficultySettingsGridPane = new GridPane();
		difficultySettingsGridPane.setHgap(30);
		difficultySettingsGridPane.setVgap(30);
		Arrays.asList(Difficulty.values()).forEach(difficulty -> {
			final Label difficultyLabel = new Label(difficulty.getLabel());
			final int maxScoreForDifficulty = ApplicationSettings.getInstance().getMaxScoreForDifficulty(difficulty.name());
			final TextField maxScoreInput = new TextField();
			maxScoreInput.setTooltip(new Tooltip(TooltipConstants.MAX_DIFFICULTY_SCORE_PREFIX + difficulty.getLabel()
					+ TooltipConstants.MAX_DIFFICULTY_SCORE_SUFFIX));
			final UnaryOperator<Change> integerFilter = change -> {
				final String newText = change.getControlNewText();
				if (newText.matches(DIGITS_ONLY_REGEX)) {
					return change;
				}
				return null;
			};
			if (Difficulty.DIABOLICAL == difficulty) {
				maxScoreInput.setEditable(false);
				maxScoreInput.setDisable(true);
			}
			maxScoreInput.setTextFormatter(
					new TextFormatter<Integer>(new IntegerStringConverter(), maxScoreForDifficulty, integerFilter));
			this.maxScoreInputs.put(difficulty, maxScoreInput);
			difficultySettingsGridPane.add(difficultyLabel, 0, difficulty.ordinal());
			difficultySettingsGridPane.add(maxScoreInput, 1, difficulty.ordinal());
		});
		contentPane.getChildren().add(difficultySettingsGridPane);
		this.setCenter(contentPane);
		this.createButtonPane();
	}

	private void createButtonPane() {
		final Button confirmButton = new Button(LabelConstants.SAVE_AND_APPLY);
		confirmButton.setOnAction(event -> {
			ModelController.getInstance().transitionToSaveDifficultySettingsState();
			this.stage.close();
		});
		final Button restoreDefaultsButton = new Button(LabelConstants.RESTORE_DEFAULTS);
		restoreDefaultsButton.setOnAction(event -> this.resetViewToDefaults());
		HBox.setMargin(restoreDefaultsButton, new Insets(0, 0, 0, SMALL_PADDING));
		final HBox buttonPane = new HBox();
		buttonPane.setPadding(new Insets(0, 0, BUTTON_PANE_PADDING, BUTTON_PANE_PADDING));
		buttonPane.getChildren().addAll(confirmButton, restoreDefaultsButton);
		this.setBottom(buttonPane);
	}

	private void resetViewToDefaults() {
		this.maxScoreInputs.forEach((difficulty, maxScoreInput) -> {
			final int maxScoreForDifficulty = DefaultApplicationSettings.getInstance()
					.getMaxScoreForDifficulty(difficulty.name());
			maxScoreInput.setText(String.valueOf(maxScoreForDifficulty));
		});
	}

	public TextField getMaxScoreInput(final Difficulty difficulty) {
		return this.maxScoreInputs.get(difficulty);
	}
}
