package sudoku.view.settings;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.util.Strings;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import sudoku.SolutionType;
import sudoku.StepConfig;
import sudoku.core.ModelController;
import sudoku.factories.LayoutFactory;
import sudoku.model.ApplicationSettings;
import sudoku.model.DefaultApplicationSettings;
import sudoku.view.control.LabeledComboBox;
import sudoku.view.dialog.ModalDialog;
import sudoku.view.util.Difficulty;
import sudoku.view.util.LabelConstants;
import sudoku.view.util.TooltipConstants;

/**
 * This class contains methods to allow the user to view or change the puzzle
 * generation settings of the application.
 */
public class PuzzleGenerationSettingsView extends ModalDialog {

	private static final int STEP_COMBO_BOX_WIDTH = 400;

	private static final int BUTTON_PANE_PADDING = 5;

	private static final int SMALL_PADDING = 20;

	private static final int LARGE_PADDING = 30;

	private LabeledComboBox difficultyComboBox;

	private LabeledComboBox mustContainTechniqueComboBox;

	private CheckBox solveUpToStepCheckBox;

	private Button confirmButton;

	public PuzzleGenerationSettingsView(final Stage stage) {
		super(stage);
		this.configure();
	}

	@Override
	protected void configure() {
		this.setTitle(LabelConstants.PUZZLE_GENERATION_SETTINGS);
		this.createChildElements();
	}

	@Override
	protected void createChildElements() {
		final VBox contentPane = new VBox();
		contentPane.setAlignment(Pos.TOP_LEFT);
		contentPane.setPadding(new Insets(SMALL_PADDING));
		this.createDifficultyComboBox();
		this.createMustContainStepComboBox();
		this.createSolveUpToStepCheckBox();
		this.createButtonPane();
		contentPane.getChildren().addAll(this.difficultyComboBox, this.mustContainTechniqueComboBox,
				this.solveUpToStepCheckBox);
		this.setCenter(contentPane);
	}

	private void createDifficultyComboBox() {
		this.difficultyComboBox = LayoutFactory.getInstance().createLabeledComboBox();
		Difficulty.getValidDifficulties()
				.forEach(difficulty -> this.difficultyComboBox.getComboBox().getItems().add(difficulty.getLabel()));
		this.difficultyComboBox.getComboBox().getSelectionModel()
				.select(ApplicationSettings.getInstance().getDifficulty().getLabel());
		this.difficultyComboBox.getLabel().setText(LabelConstants.DIFFICULTY + ":");
		VBox.setMargin(this.difficultyComboBox, new Insets(SMALL_PADDING, 0, LARGE_PADDING, 0));
	}

	private void createMustContainStepComboBox() {
		this.mustContainTechniqueComboBox = LayoutFactory.getInstance().createLabeledComboBox();
		final ComboBox<String> comboBox = this.mustContainTechniqueComboBox.getComboBox();
		final SingleSelectionModel<String> selectionModel = comboBox.getSelectionModel();
		selectionModel.select(ApplicationSettings.getInstance().getMustContainStepWithName());
		comboBox.setMinWidth(STEP_COMBO_BOX_WIDTH);
		this.mustContainTechniqueComboBox.getLabel().setText(LabelConstants.MUST_CONTAIN);
		this.mustContainTechniqueComboBox.getComboBox().setTooltip(new Tooltip(TooltipConstants.MUST_CONTAIN));

		comboBox.getEditor().setEditable(true);
		final List<String> disabledStepNames = ApplicationSettings.getInstance().getSolverConfig().stream()
				.filter(stepConfig -> !stepConfig.isEnabled()).map(StepConfig::getType).map(SolutionType::getStepName)
				.collect(Collectors.toList());
		final List<String> stepNames = Arrays.asList(SolutionType.values()).stream()
				.filter(solutionType -> SolutionType.getStepConfig(solutionType) != null
						&& SolutionType.getStepConfig(solutionType).isEnabled())
				.map(SolutionType::getStepName).sorted().collect(Collectors.toList());
		stepNames.removeIf(disabledStepNames::contains);
		stepNames.remove(SolutionType.GIVE_UP.getStepName());
		stepNames.remove(SolutionType.BRUTE_FORCE.getStepName());
		stepNames.remove(SolutionType.INCOMPLETE.getStepName());
		final ObservableList<String> inputOptions = FXCollections.observableArrayList(stepNames);
		inputOptions.add(0, Strings.EMPTY);
		final FilteredList<String> filteredItems = new FilteredList<>(inputOptions, p -> true);
		comboBox.setItems(filteredItems);

//		comboBox.getEditor().textProperty().addListener(this.onValueChanged(comboBox, filteredItems));
		VBox.setMargin(this.mustContainTechniqueComboBox, new Insets(0, 0, LARGE_PADDING, 0));
	}

	private void createSolveUpToStepCheckBox() {
		this.solveUpToStepCheckBox = new CheckBox(LabelConstants.SOLVE_UP_TO);
		VBox.setMargin(this.solveUpToStepCheckBox, new Insets(0));
		this.solveUpToStepCheckBox.setTooltip(new Tooltip(TooltipConstants.SOLVE_UP_TO));
		this.solveUpToStepCheckBox.setSelected(ApplicationSettings.getInstance().isSolveToRequiredStep());
	}

	private void createButtonPane() {
		this.confirmButton = new Button(LabelConstants.SAVE_AND_APPLY);
		this.confirmButton.setOnAction(event -> {
			ModelController.getInstance().transitionToSavePuzzleGenerationSettingsState();
			this.getStage().close();
		});
		final Button restoreDefaultsButton = new Button(LabelConstants.RESTORE_DEFAULTS);
		restoreDefaultsButton.setOnAction(event -> this.resetViewToDefaults());
		HBox.setMargin(restoreDefaultsButton, new Insets(0, 0, 0, SMALL_PADDING));
		final HBox buttonPane = new HBox();
		buttonPane.setPadding(new Insets(0, 0, BUTTON_PANE_PADDING, BUTTON_PANE_PADDING));
		buttonPane.getChildren().addAll(this.confirmButton, restoreDefaultsButton);
		this.setBottom(buttonPane);
	}

	private void resetViewToDefaults() {
		final Difficulty difficulty = DefaultApplicationSettings.getInstance().getDifficulty();
		this.difficultyComboBox.getComboBox().getSelectionModel().select(difficulty.getLabel());
		final String mustContainStepWithName = DefaultApplicationSettings.getInstance().getMustContainStepWithName();
		final SingleSelectionModel<String> selectionModel = this.mustContainTechniqueComboBox.getComboBox()
				.getSelectionModel();
		selectionModel.select(mustContainStepWithName);
		final boolean solveToRequiredStep = DefaultApplicationSettings.getInstance().isSolveToRequiredStep();
		this.solveUpToStepCheckBox.setSelected(solveToRequiredStep);
	}

	/**
	 * Filters the list to matching entries when the list changes. Currently
	 * disabled because of https://bugs.openjdk.java.net/browse/JDK-8145517, which
	 * makes the control clunky and frustrating to use.
	 */
//	private ChangeListener<String> onValueChanged(final ComboBox<String> comboBox,
//			final FilteredList<String> filteredItems) {
//		return (obs, oldValue, newValue) -> {
//			final TextField editor = comboBox.getEditor();
//			final String editorText = editor.getText();
//			final String selected = comboBox.getSelectionModel().getSelectedItem();
//
//			Platform.runLater(() -> {
//				// If the no item in the list is selected or the selected item
//				// isn't equal to the current input, we re-filter the list.
//				if (selected == null || !selected.equals(editorText)) {
//					filteredItems.setPredicate(item -> item.isEmpty() || item.toUpperCase().contains(newValue.toUpperCase()));
//				}
//				this.confirmButton.setDisable(!comboBox.getItems().contains(newValue));
//			});
//		};
//	}

	public LabeledComboBox getDifficultyComboBox() {
		return this.difficultyComboBox;
	}

	public LabeledComboBox getMustContainTechniqueComboBox() {
		return this.mustContainTechniqueComboBox;
	}

	public CheckBox getSolveUpToStepCheckBox() {
		return this.solveUpToStepCheckBox;
	}

}
