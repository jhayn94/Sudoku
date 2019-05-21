package sudoku.state.window;

import javafx.collections.ObservableList;
import javafx.geometry.BoundingBox;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;
import sudoku.core.ViewController;
import sudoku.view.util.ResourceConstants;

/**
 * This class contains code to run when the user drags the title bar of the
 * view, thus invoking a "restore" action".
 */
public class SoftRestoredState extends ApplicationWindowState {

	public SoftRestoredState(final ApplicationWindowState lastState) {
		super(lastState);
	}

	@Override
	protected void onEnter() {
		final Stage stage = ViewController.getInstance().getStage();
		stage.setMaximized(false);
		final ObservableList<Screen> screensForRectangle = Screen.getScreensForRectangle(stage.getX(), stage.getY(),
				stage.getWidth(), stage.getHeight());
		final Screen screen = screensForRectangle.get(0);
		final Rectangle2D visualBounds = screen.getVisualBounds();
		this.savedBounds = new BoundingBox(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight());
		stage.setX(visualBounds.getMinX());
		stage.setY(visualBounds.getMinY());
		stage.setWidth(visualBounds.getWidth());
		stage.setHeight(visualBounds.getHeight());
		this.setIcon(ViewController.getInstance().getMaximizeWindowButton(), ResourceConstants.MAXIMIZE_ICON);
	}

}
