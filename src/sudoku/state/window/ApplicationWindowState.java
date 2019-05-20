package sudoku.state.window;

import javafx.geometry.BoundingBox;
import javafx.scene.CacheHint;
import javafx.scene.control.Button;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import sudoku.core.ViewController;

/**
 * This class is a representation of the current state of the application
 * window, with methods to invoke when a state change occurs.
 */
public abstract class ApplicationWindowState {

	protected BoundingBox savedBounds;

	protected ImageView maximizeOrRestoreIconView;

	/** Constructor for the initialization of the application. */
	protected ApplicationWindowState() {
		final Stage stage = ViewController.getInstance().getStage();
		this.savedBounds = new BoundingBox(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight());
		this.maximizeOrRestoreIconView = null;
	}

	/** Constructor for state transitions. */
	protected ApplicationWindowState(final ApplicationWindowState lastState) {
		this.savedBounds = lastState.savedBounds;
		this.maximizeOrRestoreIconView = lastState.maximizeOrRestoreIconView;
		this.onEnter();
	}

	protected abstract void onEnter();

	protected void setIcon(final Button button, final String iconPath) {
		final Image icon = new Image(this.getClass().getResourceAsStream(iconPath));
		this.maximizeOrRestoreIconView = new ImageView(icon);
		this.maximizeOrRestoreIconView.setCache(true);
		this.maximizeOrRestoreIconView.setCacheHint(CacheHint.SPEED);
		this.setIconColor();
		button.setGraphic(this.maximizeOrRestoreIconView);
	}

	protected void restoreSavedBounds(final Stage stage) {
		if (this.savedBounds == null) {
			stage.centerOnScreen();
		} else {
			stage.setX(this.savedBounds.getMinX());
			stage.setY(this.savedBounds.getMinY());
			stage.setWidth(this.savedBounds.getWidth());
			stage.setHeight(this.savedBounds.getHeight());
			this.savedBounds = null;
		}
	}

	protected void restoreSavedSize(final Stage stage) {
		if (this.savedBounds == null) {
			stage.centerOnScreen();
		} else {
			stage.setWidth(this.savedBounds.getWidth());
			stage.setHeight(this.savedBounds.getHeight());
			this.savedBounds = null;
		}
	}

	protected void setIconColor() {
		final ColorAdjust monochrome = new ColorAdjust();
		// This HSB setup approximates the color 'arb-color-stone' in the CSS file.
		monochrome.setHue(-.05);
		monochrome.setSaturation(.57);
		monochrome.setBrightness(-.4);
		this.maximizeOrRestoreIconView.setEffect(monochrome);
	}
}
