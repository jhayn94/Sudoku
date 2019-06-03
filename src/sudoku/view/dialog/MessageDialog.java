package sudoku.view.dialog;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import sudoku.view.util.LabelConstants;

/** This is a base class for creating a dialog to show a message to the user. */
public class MessageDialog extends ModalDialog {

	private static final int BUTTON_PANE_PADDING = 5;

	private Label messageLabel;

	public MessageDialog(final Stage stage) {
		super(stage);
		this.configure();
	}

	public void setMessage(final String message) {
		this.messageLabel.setText(message);
	}

	@Override
	protected void configure() {
		this.createChildElements();
	}

	@Override
	protected void createChildElements() {
		this.messageLabel = new Label();
		this.setCenter(this.messageLabel);
		this.createButtonPane();
	}

	protected void createButtonPane() {
		final Button okButton = new Button(LabelConstants.OK);
		okButton.setOnAction(event -> {
			this.stage.close();
		});
		final HBox buttonPane = new HBox();
		buttonPane.setPadding(new Insets(0, 0, BUTTON_PANE_PADDING, BUTTON_PANE_PADDING));
		buttonPane.getChildren().add(okButton);
		this.setBottom(buttonPane);
	}

}
