package sudoku.view;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import sudoku.core.ViewController;
import sudoku.factories.MenuFactory;
import sudoku.view.menu.ApplicationMenuSpacer;
import sudoku.view.menu.button.AbstractMenuButton;
import sudoku.view.menu.button.ApplicationMenuButtonType;
import sudoku.view.util.LabelConstants;

public class ModalDialog extends BorderPane {

	private static final String MENU_BAR_CSS_CLASS = "menu-bar";

	protected final Stage stage;

	public ModalDialog(final Stage stage) {
		super();
		this.stage = stage;
		this.configure();
	}

	protected void configure() {
		this.createChildElements();
	}

	private void createChildElements() {
		final AbstractMenuButton closeButton = MenuFactory.getInstance()
				.createApplicationMenuButton(ApplicationMenuButtonType.CLOSE);
		closeButton.setOnAction(event -> {
			this.stage.close();
			ViewController.getInstance().setHelpStage(null);
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
