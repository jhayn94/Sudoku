package sudoku.state.window;

import javafx.collections.ObservableList;
import javafx.geometry.BoundingBox;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;
import sudoku.core.ViewController;
import sudoku.view.util.ResourceConstants;

/**
 * This class contains code to run when the user maximizes the application.
 */
public class MaximizedState extends ApplicationWindowState {

	public MaximizedState() {
		super();
		this.onEnter();
	}

	public MaximizedState(final ApplicationWindowState lastState) {
		super(lastState);
	}

	@Override
	protected void onEnter() {
		final Stage stage = ViewController.getInstance().getStage();
		final ObservableList<Screen> screensForRectangle = Screen.getScreensForRectangle(stage.getX(), stage.getY(),
				stage.getWidth(), stage.getHeight());
		final Screen screen = screensForRectangle.get(0);
		final Rectangle2D visualBounds = screen.getVisualBounds();
		this.savedBounds = new BoundingBox(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight());
		stage.setX(visualBounds.getMinX());
		stage.setY(visualBounds.getMinY());
		stage.setWidth(visualBounds.getWidth());
		stage.setHeight(visualBounds.getHeight());
		stage.setMaximized(true);
		this.setIcon(ViewController.getInstance().getMaximizeWindowButton(), ResourceConstants.RESTORE_ICON);
	}
}
