package sudoku.view.settings;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import sudoku.StepConfig;
import sudoku.core.ModelController;
import sudoku.model.ApplicationSettings;
import sudoku.model.DefaultApplicationSettings;
import sudoku.view.ModalDialog;
import sudoku.view.util.Difficulty;
import sudoku.view.util.LabelConstants;

/**
 * This class creates view elements so the user can configure the solver's hint
 * system and puzzle generation.
 */
public class SolverSettingsView extends ModalDialog {

	private static final int PADDING_BETWEEN_BUTTONS = 5;

	private static final int BUTTON_HEIGHT = 15;

	private static final int BUTTON_WIDTH = 62;

	private static final String STONE_BLUE_HEX_CODE = "#336b87";

	private static final Double[] UP_ARROW_VERTICES = new Double[] { -5.5, 0.0, 5.5, 0.0, 0.0, -7.0 };

	private static final Double[] DOWN_ARROW_VERTICES = new Double[] { -5.5, 0.0, 5.5, 0.0, 0.0, 7.0 };

	private static final String DIGITS_ONLY_REGEX = "^\\d*$";

	private static final double DIFFICULTY_COMBO_BOX_WIDTH = 150;

	private static final int RATING_TEXT_FIELD_WIDTH = 90;

	private static final int STEP_LIST_VIEW_WIDTH = 350;

	private static final int BUTTON_PANE_PADDING = 5;

	private static final int SMALL_PADDING = 20;

	private static final int LARGE_PADDING = 30;

	private static final String DISABLED_STEP_CSS_CLASS = "sudoku-disabled-step";

	private static final String ENABLED_STEP_CSS_CLASS = "sudoku-enabled-step";

	private static final String BUTTON_CSS_CLASS = "sudoku-candidate-increment-decrement-button";

	private List<StepConfig> stepConfigs;

	private ComboBox<String> difficultyComboBox;

	private CheckBox enabledCheckbox;

	private TextField ratingTextField;

	private ListView<StepConfig> stepConfigsListView;

	private final Map<StepConfig, ListCell<StepConfig>> listCells;

	private final ChangeListener<Boolean> enabledCheckboxChangeListener;

	private Button moveStepUpButton;

	private Button moveStepDownButton;

	public SolverSettingsView(final Stage stage) {
		super(stage);
		this.listCells = new HashMap<>();
		this.stepConfigs = ApplicationSettings.getInstance().getSolverConfig();
		this.enabledCheckboxChangeListener = this.getEnabledCheckboxChangeListener();
		this.configure();
	}

	@Override
	protected void configure() {
		this.setTitle(LabelConstants.SOLVER_SETTINGS);
		this.createChildElements();
	}

	@Override
	protected void createChildElements() {
		final HBox contentPane = new HBox();
		contentPane.setAlignment(Pos.TOP_LEFT);
		contentPane.setPadding(new Insets(SMALL_PADDING));
		this.createStepConfigsListView();
		final GridPane settingsForStepConfigPane = this.createStepSettingsPane();
		contentPane.getChildren().addAll(this.stepConfigsListView, settingsForStepConfigPane);
		this.setCenter(contentPane);
		this.createButtonPane();
	}

	private void createStepConfigsListView() {
		this.stepConfigsListView = new ListView<StepConfig>();
		final ObservableList<StepConfig> items = this.stepConfigsListView.getItems();
		this.stepConfigs.forEach(items::add);
		this.stepConfigsListView.setMinWidth(STEP_LIST_VIEW_WIDTH);
		this.stepConfigsListView.setMaxWidth(STEP_LIST_VIEW_WIDTH);
		this.stepConfigsListView.getSelectionModel().select(0);
		this.stepConfigsListView.getSelectionModel().selectedItemProperty()
				.addListener(this.onChangeListViewSelectionListener());
		this.stepConfigsListView.setCellFactory(param -> this.getListCellFactory());
		HBox.setMargin(this.stepConfigsListView, new Insets(SMALL_PADDING, SMALL_PADDING, 0, 0));
	}

	private GridPane createStepSettingsPane() {
		final GridPane settingsForStepConfigPane = new GridPane();
		HBox.setMargin(settingsForStepConfigPane, new Insets(SMALL_PADDING, 0, 0, 0));
		settingsForStepConfigPane.setVgap(LARGE_PADDING);
		settingsForStepConfigPane.setHgap(SMALL_PADDING);

		final Label enabledLabel = new Label(LabelConstants.ENABLED);
		final Label difficultyLabel = new Label(LabelConstants.DIFFICULTY + ":");
		final Label ratingLabel = new Label(LabelConstants.RATING);

		final StepConfig firstStepConfig = this.stepConfigs.get(0);
		this.createEnabledCheckbox(firstStepConfig);
		this.createDifficultyComboBox(firstStepConfig);
		this.createRatingTextField(firstStepConfig);
		settingsForStepConfigPane.add(enabledLabel, 0, 0);
		settingsForStepConfigPane.add(difficultyLabel, 0, 1);
		settingsForStepConfigPane.add(ratingLabel, 0, 2);
		settingsForStepConfigPane.add(this.enabledCheckbox, 1, 0);
		settingsForStepConfigPane.add(this.difficultyComboBox, 1, 1);
		settingsForStepConfigPane.add(this.ratingTextField, 1, 2);
		settingsForStepConfigPane.add(this.createChangeStepPositionButtonPanel(), 0, 3);
		return settingsForStepConfigPane;
	}

	private void createEnabledCheckbox(final StepConfig firstStepConfig) {
		this.enabledCheckbox = new CheckBox();
		this.enabledCheckbox.setSelected(firstStepConfig.isEnabled());
		this.enabledCheckbox.selectedProperty().addListener(this.enabledCheckboxChangeListener);
	}

	private void createDifficultyComboBox(final StepConfig firstStepConfig) {
		this.difficultyComboBox = new ComboBox<String>();
		this.difficultyComboBox.setMinWidth(DIFFICULTY_COMBO_BOX_WIDTH);
		this.difficultyComboBox.setMaxWidth(DIFFICULTY_COMBO_BOX_WIDTH);
		this.difficultyComboBox.setEditable(true);
		this.difficultyComboBox.getEditor().setEditable(false);
		final ObservableList<String> difficultyComboBoxItems = this.difficultyComboBox.getItems();
		Difficulty.getValidDifficulties().stream().map(Difficulty::getLabel).forEach(difficultyComboBoxItems::add);
		this.difficultyComboBox.getSelectionModel().select(firstStepConfig.getLevel() - 1);
	}

	private void createRatingTextField(final StepConfig firstStepConfig) {
		this.ratingTextField = new TextField();
		this.ratingTextField.setMinWidth(RATING_TEXT_FIELD_WIDTH);
		this.ratingTextField.setMaxWidth(RATING_TEXT_FIELD_WIDTH);
		final UnaryOperator<Change> integerFilter = this.getIntegerOnlyInputFilter();
		this.ratingTextField.setTextFormatter(
				new TextFormatter<Integer>(new IntegerStringConverter(), firstStepConfig.getBaseScore(), integerFilter));
	}

	private VBox createChangeStepPositionButtonPanel() {
		final VBox buttonPanel = new VBox();
		this.createMoveStepUpButton();
		this.createMoveStepDownButton();
		VBox.setMargin(this.moveStepDownButton, new Insets(PADDING_BETWEEN_BUTTONS, 0, 0, 0));
		buttonPanel.getChildren().addAll(this.moveStepUpButton, this.moveStepDownButton);
		return buttonPanel;
	}

	private void createMoveStepUpButton() {
		this.moveStepUpButton = new Button();
		this.moveStepUpButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		this.moveStepUpButton.getStyleClass().add(BUTTON_CSS_CLASS);
		this.moveStepUpButton.setMaxHeight(BUTTON_HEIGHT);
		this.moveStepUpButton.setMaxWidth(BUTTON_WIDTH);
		this.moveStepUpButton.setMinWidth(BUTTON_WIDTH);
		this.moveStepUpButton.setFocusTraversable(false);
		// Index 0 is always selected by default, so this should be disabled because you
		// can't move this step config up.
		this.moveStepUpButton.setDisable(true);
		this.moveStepUpButton.setOnAction(event -> {
			this.updateSelectedItemIndex(false);
		});
		final Polygon upArrowPolygon = new Polygon();
		upArrowPolygon.getPoints().addAll(UP_ARROW_VERTICES);
		upArrowPolygon.setFill(Paint.valueOf(STONE_BLUE_HEX_CODE));
		this.moveStepUpButton.setGraphic(upArrowPolygon);
	}

	private void createMoveStepDownButton() {
		this.moveStepDownButton = new Button();
		this.moveStepDownButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		this.moveStepDownButton.getStyleClass().add(BUTTON_CSS_CLASS);
		this.moveStepDownButton.setMaxHeight(BUTTON_HEIGHT);
		this.moveStepDownButton.setMaxWidth(BUTTON_WIDTH);
		this.moveStepDownButton.setMinWidth(BUTTON_WIDTH);
		this.moveStepDownButton.setFocusTraversable(false);
		this.moveStepDownButton.setOnAction(event -> this.updateSelectedItemIndex(true));
		final Polygon downArrowPolygon = new Polygon();
		downArrowPolygon.getPoints().addAll(DOWN_ARROW_VERTICES);
		downArrowPolygon.setFill(Paint.valueOf(STONE_BLUE_HEX_CODE));
		this.moveStepDownButton.setGraphic(downArrowPolygon);
	}

	private void createButtonPane() {
		final Button confirmButton = new Button(LabelConstants.SAVE_AND_APPLY);
		confirmButton.setOnAction(event -> {
			final StepConfig stepConfig = this.stepConfigsListView.getSelectionModel().getSelectedItem();
			this.updateStepFromView(stepConfig);
			ModelController.getInstance().transitionToSaveSolverSettingsState(this.stepConfigs);
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

	/**
	 * Updates the given StepConfig to match the view elements' state. This is
	 * triggered before leaving the screen, and when the list's selected item
	 * changes.
	 */
	private void updateStepFromView(final StepConfig oldValue) {
		final int indexOfSelection = this.stepConfigs.indexOf(oldValue);
		final StepConfig stepConfig = this.stepConfigs.get(indexOfSelection);
		final int difficultyLevel = this.difficultyComboBox.getSelectionModel().getSelectedIndex() + 1;
		stepConfig.setLevel(difficultyLevel);
		final String ratingText = this.ratingTextField.getText();
		// Avoid formatting problems by using 1 if the input was null or empty.
		final int ratingForStep = Integer.parseInt(ratingText == null || ratingText.isEmpty() ? "1" : ratingText);
		stepConfig.setBaseScore(ratingForStep);
		stepConfig.setEnabled(this.enabledCheckbox.isSelected());
		this.updateListCellStyleClass(oldValue, this.listCells.get(oldValue));
	}

	private void updateListCellStyleClass(final StepConfig stepConfig, final ListCell<StepConfig> listCellForStep) {
		if (listCellForStep == null) {
			return;
		}
//		if (listCellForStep.isSelected()) {
//			listCellForStep.setStyle("-fx-text-fill: -sudoku-color-off-white;");
//			listCellForStep.setStyle("-fx-background-color: -sudoku-color-stone-blue;");
//		} else
		if (stepConfig.isEnabled()) {
			listCellForStep.setStyle("-fx-text-fill: -sudoku-color-bark;");
//			listCellForStep.getStyleClass().add(ENABLED_STEP_CSS_CLASS);
//			listCellForStep.getStyleClass().remove(DISABLED_STEP_CSS_CLASS);
		} else {
			listCellForStep.setStyle("-fx-text-fill: derive(-sudoku-color-bark, 50%);");
//			listCellForStep.setStyle("-fx-opacity: .65;");
//			if (listCellForStep.isSelected()) {
//				listCellForStep.setStyle("-fx-background-color: derive(-sudoku-color-stone-blue, 30%);");
//			}
//			listCellForStep.getStyleClass().remove(ENABLED_STEP_CSS_CLASS);
//			listCellForStep.getStyleClass().add(DISABLED_STEP_CSS_CLASS);
		}
	}

	private void updateSettingsWithSelectedStep(final StepConfig newValue) {
		final int indexOfSelection = this.stepConfigs.indexOf(newValue);
		final StepConfig selectedStepConfig = this.stepConfigs.get(indexOfSelection);
		this.difficultyComboBox.getSelectionModel().select(selectedStepConfig.getLevel() - 1);
		final boolean enabled = selectedStepConfig.isEnabled();
		this.enabledCheckbox.selectedProperty().removeListener(this.enabledCheckboxChangeListener);
		this.enabledCheckbox.setSelected(enabled);
		this.enabledCheckbox.selectedProperty().addListener(this.enabledCheckboxChangeListener);
		final int baseScore = selectedStepConfig.getBaseScore();
		this.ratingTextField.setText(String.valueOf(baseScore));
		this.updateListCellStyleClass(newValue, this.listCells.get(newValue));
	}

	private void resetViewToDefaults() {
		this.stepConfigs = DefaultApplicationSettings.getInstance().getSolverConfig();
		final ObservableList<StepConfig> items = this.stepConfigsListView.getItems();
		final MultipleSelectionModel<StepConfig> selectionModel = this.stepConfigsListView.getSelectionModel();
		final int selectedIndex = selectionModel.getSelectedIndex();
		items.clear();
		this.stepConfigs.forEach(items::add);
		selectionModel.select(selectedIndex);
		final StepConfig selectedItem = this.stepConfigsListView.getSelectionModel().getSelectedItem();
		this.updateSettingsWithSelectedStep(selectedItem);
		this.updateListCellStyleClass(selectedItem, this.listCells.get(selectedItem));
		// TODO - toggle active / inactive style.
	}

	private ListCell<StepConfig> getListCellFactory() {
		return new ListCell<StepConfig>() {
			@Override
			protected void updateItem(final StepConfig item, final boolean empty) {
				super.updateItem(item, empty);

				if (item == null || empty) {
					this.setText(null);
				} else {
					this.setText(item.getType().getStepName());
					SolverSettingsView.this.updateListCellStyleClass(item, this);
					SolverSettingsView.this.listCells.put(item, this);
				}
			}
		};
	}

	private ChangeListener<StepConfig> onChangeListViewSelectionListener() {
		return (observable, oldValue, newValue) -> {
			// If the selection changed because the list was cleared, there is nothing to
			// do.
			if (this.stepConfigs.contains(oldValue) && newValue != null) {
				this.updateStepFromView(oldValue);
				this.updateSettingsWithSelectedStep(newValue);
				this.moveStepDownButton.setDisable(false);
				this.moveStepUpButton.setDisable(false);
				final ObservableList<StepConfig> stepConfigs = this.stepConfigsListView.getItems();
				if (newValue.equals(stepConfigs.get(0))) {
					this.moveStepUpButton.setDisable(true);
				} else if (newValue.equals(stepConfigs.get(stepConfigs.size() - 1))) {
					this.moveStepDownButton.setDisable(true);
				}
			}
		};
	}

	private ChangeListener<Boolean> getEnabledCheckboxChangeListener() {
		return (ChangeListener<Boolean>) (observable, oldValue, newValue) -> {
			// If the changed occurred because the list was cleared, there is nothing to
			// do.
			if (!this.stepConfigsListView.getItems().isEmpty()) {
				final StepConfig stepConfig = this.stepConfigsListView.getSelectionModel().getSelectedItem();
				stepConfig.setEnabled(newValue);
				this.updateListCellStyleClass(stepConfig, this.listCells.get(stepConfig));
			}
		};
	}

	private UnaryOperator<Change> getIntegerOnlyInputFilter() {
		final UnaryOperator<Change> integerFilter = change -> {
			final String newText = change.getControlNewText();
			if (newText.matches(DIGITS_ONLY_REGEX)) {
				return change;
			}
			return null;
		};
		return integerFilter;
	}

	/** Increments or decrements the index for the selected list item. */
	private void updateSelectedItemIndex(final boolean isIncrement) {
		final MultipleSelectionModel<StepConfig> selectionModel = this.stepConfigsListView.getSelectionModel();
		final int selectedIndex = selectionModel.getSelectedIndex();
		final ObservableList<StepConfig> items = this.stepConfigsListView.getItems();
		final int newIndex = isIncrement ? selectedIndex + 1 : selectedIndex - 1;
		final StepConfig movedStepConfig = items.remove(selectedIndex);
		// Update the stepConfigs used in the view.
		items.add(newIndex, movedStepConfig);
		selectionModel.select(newIndex);

		// Swap the step configs used to actually save settings.
		final StepConfig stepConfigToSwap2 = this.stepConfigs.get(newIndex);
		final StepConfig stepConfigToSwap = this.stepConfigs.remove(selectedIndex);
		this.stepConfigs.add(newIndex, stepConfigToSwap);
		stepConfigToSwap.setIndex(newIndex);
		stepConfigToSwap2.setIndex(selectedIndex);
	}

}
