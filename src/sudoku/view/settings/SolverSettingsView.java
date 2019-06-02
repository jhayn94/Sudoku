package sudoku.view.settings;

import java.util.Arrays;
import java.util.List;
import java.util.function.UnaryOperator;

import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import sudoku.StepConfig;
import sudoku.core.HodokuFacade;
import sudoku.core.ModelController;
import sudoku.view.ModalDialog;
import sudoku.view.util.Difficulty;
import sudoku.view.util.LabelConstants;

/**
 * This class creates view elements so the user can configure the solver's hint
 * system and puzzle generation.
 */
public class SolverSettingsView extends ModalDialog {

	private static final String DIGITS_ONLY_REGEX = "^\\d*$";

	private static final double DIFFICULTY_COMBO_BOX_WIDTH = 150;

	private static final int RATING_TEXT_FIELD_WIDTH = 90;

	private static final int STEP_LIST_VIEW_WIDTH = 350;

	private static final int BUTTON_PANE_PADDING = 5;

	private static final int SMALL_PADDING = 20;

	private static final int LARGE_PADDING = 30;

	protected static final String DISABLED_STEP_CSS_CLASS = "sudoku-disabled-step";

	protected static final String ENABLED_STEP_CSS_CLASS = "sudoku-enabled-step";

	private List<StepConfig> stepConfigs;

	private ComboBox<String> difficultyComboBox;

	private CheckBox enabledCheckbox;

	private TextField ratingTextField;

	public SolverSettingsView(final Stage stage) {
		super(stage);
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

		final ListView<StepConfig> listView = new ListView<StepConfig>();
		final ObservableList<StepConfig> items = listView.getItems();
		this.stepConfigs = HodokuFacade.getInstance().getCurrentSolverConfig();
		this.stepConfigs.forEach(items::add);
		listView.setMinWidth(STEP_LIST_VIEW_WIDTH);
		listView.setMaxWidth(STEP_LIST_VIEW_WIDTH);
		listView.getSelectionModel().select(0);
		listView.getSelectionModel().selectedItemProperty().addListener(this.onChangeSelectionListener());
		listView.setCellFactory(param -> new ListCell<StepConfig>() {
			@Override
			protected void updateItem(final StepConfig item, final boolean empty) {
				super.updateItem(item, empty);

				if (item == null || empty) {
					this.setText(null);
				} else {
					this.setText(item.getType().getStepName());
					if (item.isEnabled()) {
						this.getStyleClass().add(ENABLED_STEP_CSS_CLASS);
						this.getStyleClass().remove(DISABLED_STEP_CSS_CLASS);
					} else {
						this.getStyleClass().remove(ENABLED_STEP_CSS_CLASS);
						this.getStyleClass().add(DISABLED_STEP_CSS_CLASS);
					}
				}
			}
		});
		HBox.setMargin(listView, new Insets(SMALL_PADDING, SMALL_PADDING, 0, 0));

		final GridPane settingsForStepConfigPane = this.createStepSettingsPane();

		contentPane.getChildren().addAll(listView, settingsForStepConfigPane);
		this.setCenter(contentPane);
		this.createButtonPane();
	}

	private ChangeListener<StepConfig> onChangeSelectionListener() {
		return (observable, oldValue, newValue) -> {
			final int indexOfSelection = this.stepConfigs.indexOf(newValue);
			final StepConfig selectedStepConfig = this.stepConfigs.get(indexOfSelection);
			this.difficultyComboBox.getSelectionModel().select(selectedStepConfig.getLevel() - 1);
			final boolean enabled = selectedStepConfig.isEnabled();
			this.enabledCheckbox.setSelected(enabled);
			final int baseScore = selectedStepConfig.getBaseScore();
			this.ratingTextField.setText(String.valueOf(baseScore));
		};
	}

	private GridPane createStepSettingsPane() {
		final GridPane settingsForStepConfigPane = new GridPane();
		HBox.setMargin(settingsForStepConfigPane, new Insets(SMALL_PADDING, 0, 0, 0));
		settingsForStepConfigPane.setVgap(LARGE_PADDING);
		settingsForStepConfigPane.setHgap(SMALL_PADDING);

		final Label enabledLabel = new Label(LabelConstants.ENABLED);
		final Label difficultyLabel = new Label(LabelConstants.DIFFICULTY + ":");
		final Label ratingLabel = new Label(LabelConstants.RATING);
		this.enabledCheckbox = new CheckBox();
		this.difficultyComboBox = new ComboBox<String>();
		this.difficultyComboBox.setMinWidth(DIFFICULTY_COMBO_BOX_WIDTH);
		this.difficultyComboBox.setMaxWidth(DIFFICULTY_COMBO_BOX_WIDTH);
		this.difficultyComboBox.setEditable(true);
		this.difficultyComboBox.getEditor().setEditable(false);
		final ObservableList<String> difficultyComboBoxItems = this.difficultyComboBox.getItems();
		Arrays.asList(Difficulty.values()).stream().map(Difficulty::getLabel).forEach(difficultyComboBoxItems::add);
		this.ratingTextField = new TextField();
		this.ratingTextField.setMinWidth(RATING_TEXT_FIELD_WIDTH);
		this.ratingTextField.setMaxWidth(RATING_TEXT_FIELD_WIDTH);

		final UnaryOperator<Change> integerFilter = this.getIntegerOnlyInputFilter();
		this.ratingTextField.setTextFormatter(new TextFormatter<Integer>(new IntegerStringConverter(), 0, integerFilter));
		settingsForStepConfigPane.add(enabledLabel, 0, 0);
		settingsForStepConfigPane.add(difficultyLabel, 0, 1);
		settingsForStepConfigPane.add(ratingLabel, 0, 2);
		settingsForStepConfigPane.add(this.enabledCheckbox, 1, 0);
		settingsForStepConfigPane.add(this.difficultyComboBox, 1, 1);
		settingsForStepConfigPane.add(this.ratingTextField, 1, 2);
		return settingsForStepConfigPane;
	}

	private void createButtonPane() {
		final Button confirmButton = new Button(LabelConstants.SAVE_AND_APPLY);
		confirmButton.setOnAction(event -> {
			ModelController.getInstance().transitionToSaveSolverSettingsState();
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

}
