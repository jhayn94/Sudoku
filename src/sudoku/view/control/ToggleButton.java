package sudoku.view.control;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import sudoku.core.ModelController;

/**
 * A button with an on-off state attached to it. Didn't care for the look of the
 * built-in one.
 */
public class ToggleButton extends Button {

	private static final int MIN_HEIGHT = 50;

	private static final int MIN_WIDTH = 75;

	private final String label;

	public ToggleButton(final String label) {
		super();
		this.label = label;
		this.configure();
	}

	private void configure() {
		this.setFocusTraversable(false);
		this.setMinWidth(MIN_WIDTH);
		this.setMinHeight(MIN_HEIGHT);
		this.setText(this.label);
		this.setOnAction(this.onClick());
	}

	private EventHandler<ActionEvent> onClick() {
		return event -> ModelController.getInstance().transitionToApplyFilterState(this.label);

	}

}
