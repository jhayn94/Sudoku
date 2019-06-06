package sudoku.state.model.coloring;

import java.util.List;

import javafx.scene.paint.Color;
import sudoku.core.ViewController;
import sudoku.state.model.ApplicationModelState;
import sudoku.view.sidebar.ControlHelperPane;
import sudoku.view.util.ColorUtils;

/**
 * This class updates the state of the application when the user changes the
 * active color. This color is used mostly by the mouse when coloring modes are
 * selected.
 */
public class ActiveColorChangedState extends ApplicationModelState {

	private final int colorIndex;

	public ActiveColorChangedState(final int colorIndex, final ApplicationModelState lastState) {
		super(lastState, false);
		this.colorIndex = colorIndex;
	}

	@Override
	public void onEnter() {
		final List<Color> colorsUsedInColoring = ColorUtils.getColors();

		final int oldColorIndex = colorsUsedInColoring.indexOf(this.sudokuPuzzleStyle.getActiveColor());
		final Color newActiveColor = colorsUsedInColoring.get(this.colorIndex);
		this.sudokuPuzzleStyle.setActiveColor(newActiveColor);

		final ControlHelperPane controlHelperPane = ViewController.getInstance().getControlHelperPane();
		controlHelperPane.getColorButton(oldColorIndex).getStyleClass().remove(SELECTED_COLOR_BUTTON_CSS_CLASS);
		controlHelperPane.getColorButton(this.colorIndex).getStyleClass().add(SELECTED_COLOR_BUTTON_CSS_CLASS);
	}
}
