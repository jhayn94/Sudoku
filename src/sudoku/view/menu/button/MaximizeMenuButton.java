package sudoku.view.menu.button;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import sudoku.core.ModelController;
import sudoku.core.ViewController;
import sudoku.view.util.ResourceConstants;

public class MaximizeMenuButton extends AbstractMenuButton {

	public MaximizeMenuButton() {
		super(ResourceConstants.MAXIMIZE_ICON);
		ViewController.getInstance().setMaximizeWindowButton(this);
		this.configure();
	}

	@Override
	protected void configure() {
		super.configure();
		this.addEventHandler(MouseEvent.MOUSE_CLICKED, this.onClickMaximize());
	}

	private EventHandler<MouseEvent> onClickMaximize() {
		return event -> {
			if (ViewController.getInstance().getStage().isMaximized()) {
				ModelController.getInstance().transitionToRestoredState();
			} else {
				ModelController.getInstance().transitionToMaximizedState();
			}
		};
	}
}
