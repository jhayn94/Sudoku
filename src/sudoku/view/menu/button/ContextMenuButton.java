package sudoku.view.menu.button;

import javafx.geometry.Side;
import sudoku.factories.MenuFactory;
import sudoku.view.menu.ActionMenu;
import sudoku.view.util.ResourceConstants;

public class ContextMenuButton extends AbstractMenuButton {

	public ContextMenuButton() {
		super(ResourceConstants.CONTEXT_MENU_ICON);
		this.configure();
	}

	@Override
	protected void configure() {
		super.configure();
		final ActionMenu contextMenu = MenuFactory.getInstance().createActionMenu();
		this.setOnMousePressed(event -> {
			if (contextMenu.isShowing()) {
				contextMenu.hide();
			} else {
				contextMenu.show(this, Side.BOTTOM, 0, 0);
			}
		});
		this.setContextMenu(contextMenu);
	}
}
