package sudoku.view;

import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import sudoku.factories.MenuFactory;
import sudoku.view.menu.ApplicationMenuSpacer;
import sudoku.view.menu.button.AbstractMenuButton;
import sudoku.view.menu.button.ApplicationMenuButtonType;
import sudoku.view.util.LabelConstants;

public class ModalDialog extends BorderPane {

	private static final String MENU_BAR_CSS_CLASS = "menu-bar";

	protected final Stage stage;

	private Button confirmButton;

	public ModalDialog(final Stage stage) {
		super();
		this.stage = stage;
		this.configure();
	}

	/** Adds a confirm button. Returns this for convenience. */
	public ModalDialog withConfirmButton(final Button confirmButton) {
		this.confirmButton = confirmButton;
		this.confirmButton.setOnAction(event -> {
			// Override the on action to call the original handler, then close the stage.
			this.confirmButton.getOnAction().handle(event);
			// Client may have included this in their handler.
			if (this.stage != null) {
				this.stage.close();
			}
		});
		final HBox buttonPane = new HBox();
		buttonPane.getChildren().add(this.confirmButton);
		this.setBottom(buttonPane);
		return this;
	}

	protected void configure() {
		this.createChildElements();
	}

	private void createChildElements() {
		final AbstractMenuButton closeButton = MenuFactory.getInstance()
				.createApplicationMenuButton(ApplicationMenuButtonType.CLOSE);
		closeButton.setOnAction(event -> {
			this.stage.close();
		});
		final ApplicationMenuSpacer applicationMenuSpacer = MenuFactory.getInstance().createApplicationMenuSpacer();
		applicationMenuSpacer.setTitle(LabelConstants.HELP);
		final HBox systemMenuBar = new HBox();
		systemMenuBar.getStyleClass().add(MENU_BAR_CSS_CLASS);
		HBox.setHgrow(applicationMenuSpacer, Priority.SOMETIMES);
		this.setTop(systemMenuBar);
		systemMenuBar.getChildren().addAll(applicationMenuSpacer, closeButton);
	}

}
