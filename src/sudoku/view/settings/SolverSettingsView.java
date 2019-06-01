package sudoku.view.settings;

import java.util.Arrays;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import sudoku.StepConfig;
import sudoku.core.HodokuFacade;
import sudoku.core.ModelController;
import sudoku.view.ModalDialog;
import sudoku.view.util.Difficulty;
import sudoku.view.util.LabelConstants;

public class SolverSettingsView extends ModalDialog {

	private static final double DIFFICULTY_COMBO_BOX_WIDTH = 150;

	private static final int RATING_TEXT_FIELD_WIDTH = 90;

	private static final int STEP_LIST_VIEW_WIDTH = 350;

	private static final int BUTTON_PANE_PADDING = 5;

	private static final int SMALL_PADDING = 20;

	private static final int LARGE_PADDING = 30;

	private List<StepConfig> stepConfigs;

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
		listView.setMinWidth(STEP_LIST_VIEW_WIDTH);
		listView.setMaxWidth(STEP_LIST_VIEW_WIDTH);
		HBox.setMargin(listView, new Insets(SMALL_PADDING, SMALL_PADDING, 0, 0));
		final ObservableList<StepConfig> items = listView.getItems();
		this.stepConfigs = HodokuFacade.getInstance().getCurrentSolverConfig();
		this.stepConfigs.forEach(items::add);

		final GridPane settingsForStepConfigPane = this.createStepSettingsPane();

		contentPane.getChildren().addAll(listView, settingsForStepConfigPane);
		this.setCenter(contentPane);
		this.createButtonPane();
	}

	private GridPane createStepSettingsPane() {
		final GridPane settingsForStepConfigPane = new GridPane();
		HBox.setMargin(settingsForStepConfigPane, new Insets(SMALL_PADDING, 0, 0, 0));
		settingsForStepConfigPane.setVgap(LARGE_PADDING);
		settingsForStepConfigPane.setHgap(SMALL_PADDING);

		final Label enabledLabel = new Label(LabelConstants.ENABLED);
		final Label difficultyLabel = new Label(LabelConstants.DIFFICULTY + ":");
		final Label ratingLabel = new Label(LabelConstants.RATING);
		final CheckBox allowedCheckbox = new CheckBox();
		final ComboBox<String> difficultyComboBox = new ComboBox<String>();
		difficultyComboBox.setMinWidth(DIFFICULTY_COMBO_BOX_WIDTH);
		difficultyComboBox.setMaxWidth(DIFFICULTY_COMBO_BOX_WIDTH);
		difficultyComboBox.setEditable(true);
		difficultyComboBox.getEditor().setEditable(false);
		final ObservableList<String> difficultyComboBoxItems = difficultyComboBox.getItems();
		Arrays.asList(Difficulty.values()).stream().map(Difficulty::getLabel).forEach(difficultyComboBoxItems::add);
		final TextField ratingTextField = new TextField();
		ratingTextField.setMinWidth(RATING_TEXT_FIELD_WIDTH);
		ratingTextField.setMaxWidth(RATING_TEXT_FIELD_WIDTH);
		settingsForStepConfigPane.add(enabledLabel, 0, 0);
		settingsForStepConfigPane.add(difficultyLabel, 0, 1);
		settingsForStepConfigPane.add(ratingLabel, 0, 2);
		settingsForStepConfigPane.add(allowedCheckbox, 1, 0);
		settingsForStepConfigPane.add(difficultyComboBox, 1, 1);
		settingsForStepConfigPane.add(ratingTextField, 1, 2);
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
}
