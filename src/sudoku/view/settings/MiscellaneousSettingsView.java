package sudoku.view.settings;

import javafx.stage.Stage;
import sudoku.view.ModalDialog;
import sudoku.view.util.LabelConstants;

public class MiscellaneousSettingsView extends ModalDialog {

	public MiscellaneousSettingsView(final Stage stage) {
		super(stage);
		this.configure();
	}

	@Override
	protected void configure() {
		super.configure();
		this.setTitle(LabelConstants.MISCELLANEOUS_SETTINGS);
		this.createChildElements();
	}

	private void createChildElements() {

	}

}
