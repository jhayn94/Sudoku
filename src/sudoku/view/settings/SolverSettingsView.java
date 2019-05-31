package sudoku.view.settings;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import sudoku.core.ModelController;
import sudoku.view.ModalDialog;
import sudoku.view.util.LabelConstants;

public class SolverSettingsView extends ModalDialog {

	private static final int BUTTON_PANE_PADDING = 5;

	private static final int SMALL_PADDING = 20;

	private static final int LARGE_PADDING = 30;

	public SolverSettingsView(final Stage stage) {
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
		this.createButtonPane();
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
