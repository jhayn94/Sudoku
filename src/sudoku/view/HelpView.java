package sudoku.view;

import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import sudoku.view.dialog.ModalDialog;
import sudoku.view.util.LabelConstants;

public class HelpView extends ModalDialog {

	public HelpView(final Stage stage) {
		super(stage);
		this.configure();
	}

	@Override
	protected void configure() {
		this.setTitle(LabelConstants.ABOUT);
		this.createChildElements();
	}

	@Override
	protected void createChildElements() {
		super.createChildElements();
		final TextArea helpMessageTextArea = new TextArea();
		helpMessageTextArea.setEditable(false);
		helpMessageTextArea.setWrapText(true);
		helpMessageTextArea.setText(LabelConstants.ABOUT_CONTENT);
		this.setCenter(helpMessageTextArea);
	}

}
