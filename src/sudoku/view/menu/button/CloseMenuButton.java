package sudoku.view.menu.button;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import sudoku.core.ModelController;
import sudoku.view.util.ResourceConstants;

public class CloseMenuButton extends AbstractMenuButton {

	public CloseMenuButton() {
		super(ResourceConstants.CLOSE_ICON);
		this.configure();
	}

	@Override
	protected void configure() {
		super.configure();
		this.addEventHandler(MouseEvent.MOUSE_CLICKED, this.onClickClose());
	}

	private EventHandler<MouseEvent> onClickClose() {
		return event -> ModelController.getInstance().transitionToClosedState();
	}
}
