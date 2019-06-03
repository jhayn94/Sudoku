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

	// TODO - add the running action which can (somehow!) be cancelled.
	public WaitingDialog(final Stage stage) {
		super(stage);
		this.configure();
	}

	@Override
	protected void configure() {
		this.createChildElements();
	}

	@Override
	protected void createButtonPane() {
		final Button cancelButton = new Button(LabelConstants.CANCEL);
		cancelButton.setOnAction(event -> {
			this.stage.close();
		});
		final HBox buttonPane = new HBox();
		buttonPane.setPadding(new Insets(0, 0, BUTTON_PANE_PADDING, BUTTON_PANE_PADDING));
		buttonPane.getChildren().add(cancelButton);
		this.setBottom(buttonPane);
	}

}
