package sudoku.view.settings;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import sudoku.core.ModelController;
import sudoku.model.ApplicationSettings;
import sudoku.model.DefaultApplicationSettings;
import sudoku.view.dialog.ModalDialog;
import sudoku.view.util.LabelConstants;
import sudoku.view.util.TooltipConstants;

/**
 * This class contains methods to allow the user to view or change miscellaneous
 * settings of the application.
 */
public class MiscellaneousSettingsView extends ModalDialog {

	private static final int BUTTON_PANE_PADDING = 5;

	private static final int SMALL_PADDING = 20;

	private CheckBox autoManageCandidatesCheckBox;

	private CheckBox showPuzzleProgressCheckBox;

	public MiscellaneousSettingsView(final Stage stage) {
		super(stage);
		this.configure();
	}

	@Override
	protected void configure() {
		this.setTitle(LabelConstants.MISCELLANEOUS_SETTINGS);
		this.createChildElements();
	}

	@Override
	protected void createChildElements() {
		super.createChildElements();
		final VBox contentPane = new VBox();
		contentPane.setAlignment(Pos.TOP_LEFT);
		contentPane.setPadding(new Insets(SMALL_PADDING));
		this.autoManageCandidatesCheckBox = new CheckBox(LabelConstants.AUTO_MANAGE_CANDIDATES);
		this.autoManageCandidatesCheckBox.setTooltip(new Tooltip(TooltipConstants.AUTO_MANAGE_CANDIDATES));
		this.autoManageCandidatesCheckBox.setSelected(ApplicationSettings.getInstance().isAutoManageCandidates());
		VBox.setMargin(this.autoManageCandidatesCheckBox, new Insets(SMALL_PADDING, 0, 0, 0));
		this.showPuzzleProgressCheckBox = new CheckBox(LabelConstants.SHOW_PUZZLE_PROGRESS);
		this.showPuzzleProgressCheckBox.setTooltip(new Tooltip(TooltipConstants.SHOW_PUZZLE_PROGRESS));
		this.showPuzzleProgressCheckBox.setSelected(ApplicationSettings.getInstance().isShowPuzzleProgress());
		VBox.setMargin(this.showPuzzleProgressCheckBox, new Insets(SMALL_PADDING, 0, 0, 0));
		contentPane.getChildren().addAll(this.autoManageCandidatesCheckBox, this.showPuzzleProgressCheckBox);
		this.setCenter(contentPane);
		this.createButtonPane();
	}

	private void createButtonPane() {
		final Button confirmButton = new Button(LabelConstants.SAVE_AND_APPLY);
		confirmButton.setOnAction(event -> {
			ModelController.getInstance().transitionToSaveMiscellaneousSettingsState();
			this.getStage().close();
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
		final boolean isAutoManageCandidates = DefaultApplicationSettings.getInstance().isAutoManageCandidates();
		this.autoManageCandidatesCheckBox.setSelected(isAutoManageCandidates);
		final boolean isShowPuzzleProgress = DefaultApplicationSettings.getInstance().isShowPuzzleProgress();
		this.showPuzzleProgressCheckBox.setSelected(isShowPuzzleProgress);
	}

	public CheckBox getAutoManageCandidatesCheckBox() {
		return this.autoManageCandidatesCheckBox;
	}

	public CheckBox getShowPuzzleProgressCheckBox() {
		return this.showPuzzleProgressCheckBox;
	}

}
