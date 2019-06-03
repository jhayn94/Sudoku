package sudoku.view.dialog;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

	private static final Logger LOG = LogManager.getLogger(WaitingDialog.class);

	private static final int BUTTON_PANE_PADDING = 5;

	private Thread executionThread;

	private Button cancelButton;

	public WaitingDialog(final Stage stage) {
		super(stage);
		this.configure();
	}

	public void onGenerationFailed() {
		this.cancelButton.setText(LabelConstants.OK);
		this.setMessage(LabelConstants.RETRY_GENERATION);
	}

	/**
	 * Closes this dialog.
	 */
	public void close(final boolean forceThreadStop) {
		this.cancelButton.setDisable(true);
		if (forceThreadStop) {
			this.executionThread.interrupt();
		} else {
			try {
				this.executionThread.join();
			} catch (final InterruptedException e) {
				LOG.error("{}", e);
			}
		}
		this.getStage().close();
	}

	@Override
	protected void configure() {
		this.createChildElements();
	}

	@Override
	protected void createButtonPane() {
		this.cancelButton = new Button(LabelConstants.CANCEL);
		this.cancelButton.setOnAction(event -> {
			this.close(true);
		});
		final HBox buttonPane = new HBox();
		buttonPane.setPadding(new Insets(0, 0, BUTTON_PANE_PADDING, BUTTON_PANE_PADDING));
		buttonPane.getChildren().add(this.cancelButton);
		this.setBottom(buttonPane);
	}

	public Thread getExecutionThread() {
		return this.executionThread;
	}

	public void setExecutionThread(final Thread executionThread) {
		this.executionThread = executionThread;
	}

}
