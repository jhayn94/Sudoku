package sudoku.view.dialog;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import sudoku.view.util.LabelConstants;

/**
 * This is a base class for creating a dialog to show a message to the user
 * while they wait for an action to complete. In addition, the user is able to
 * cancel the action by clicking a cancel button.
 */
public class WaitingDialog extends MessageDialog {

	private static final int BUTTON_PANE_PADDING = 5;

	private Button cancelButton;

	public WaitingDialog(final Stage stage) {
		super(stage);
		this.configure();
	}

	/**
	 * Closes this dialog.
	 */
	public void close() {
		// Prevent multiple presses.
		this.cancelButton.setDisable(true);
		this.getStage().close();
		this.setDisabled(true);
	}

	@Override
	protected void configure() {
		this.createChildElements();
	}

	@Override
	protected void createButtonPane() {
		this.cancelButton = new Button(LabelConstants.CANCEL);
		this.cancelButton.setOnAction(event -> this.close());

		final HBox buttonPane = new HBox();
		buttonPane.setPadding(new Insets(0, 0, BUTTON_PANE_PADDING, BUTTON_PANE_PADDING));
		buttonPane.getChildren().add(this.cancelButton);
		this.setBottom(buttonPane);
	}

}
