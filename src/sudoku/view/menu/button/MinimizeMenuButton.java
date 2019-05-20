package sudoku.view.menu.button;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import sudoku.core.ModelController;
import sudoku.view.util.ResourceConstants;

public class MinimizeMenuButton extends AbstractMenuButton {

	public MinimizeMenuButton() {
		super(ResourceConstants.MINIMIZE_ICON);
		this.configure();
	}

	@Override
	protected void configure() {
		super.configure();
		this.addEventHandler(MouseEvent.MOUSE_CLICKED, this.onClickMinimize());
	}

	private EventHandler<MouseEvent> onClickMinimize() {
		return event -> ModelController.getInstance().transitionToMinimizedState();
	}

}
