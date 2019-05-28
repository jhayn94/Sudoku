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

public class ModalDialog extends BorderPane {

	private static final String MENU_BAR_CSS_CLASS = "menu-bar";

	protected final Stage stage;

	private Button confirmButton;

	private ApplicationMenuSpacer applicationMenuSpacer;

	public ModalDialog(final Stage stage) {
		super();
		this.stage = stage;
		this.configure();
	}

	public void setTitle(final String title) {
		this.applicationMenuSpacer.setTitle(title);
	}

	/** Adds a confirm button. Returns this for convenience. */
	public ModalDialog withConfirmButton(final Button confirmButton) {
		this.confirmButton = confirmButton;
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
		this.applicationMenuSpacer = MenuFactory.getInstance().createApplicationMenuSpacer();
		final HBox systemMenuBar = new HBox();
		systemMenuBar.getStyleClass().add(MENU_BAR_CSS_CLASS);
		HBox.setHgrow(this.applicationMenuSpacer, Priority.SOMETIMES);
		this.setTop(systemMenuBar);
		systemMenuBar.getChildren().addAll(this.applicationMenuSpacer, closeButton);
	}

}
