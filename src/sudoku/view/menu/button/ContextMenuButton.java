package sudoku.view.menu.button;

import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.input.MouseEvent;
import sudoku.core.ViewController;
import sudoku.factories.MenuFactory;
import sudoku.view.menu.ActionMenu;
import sudoku.view.util.ResourceConstants;

public class ContextMenuButton extends AbstractMenuButton {

	private ActionMenu contextMenu;

	public ContextMenuButton() {
		super(ResourceConstants.CONTEXT_MENU_ICON);
		this.configure();
	}

	public void toggleContextMenu() {
		if (this.contextMenu.isShowing()) {
			this.contextMenu.hide();
		} else {
			this.contextMenu.show(this, Side.BOTTOM, 0, 0);
		}
	}

	@Override
	protected void configure() {
		super.configure();
		this.contextMenu = MenuFactory.getInstance().createActionMenu();
		this.setOnMousePressed(this.onMousePressed());
		ViewController.getInstance().setContextMenuButton(this);
		this.setContextMenu(this.contextMenu);
	}

	private EventHandler<? super MouseEvent> onMousePressed() {
		return event -> {
			this.toggleContextMenu();
		};
	}
}
