package sudoku.state.model.coloring;

import java.util.List;

import javafx.scene.paint.Color;
import sudoku.core.ViewController;
import sudoku.state.ApplicationModelState;
import sudoku.view.sidebar.ColorSelectionPane;
import sudoku.view.util.ColorUtils;

public class ActiveColorState extends ApplicationModelState {

	private final boolean increment;

	public ActiveColorState(final boolean increment, final ApplicationModelState lastState) {
		super(lastState, false);
		this.increment = increment;
	}

	@Override
	public void onEnter() {
		final Color activeColor = this.sudokuPuzzleStyle.getActiveColor();
		final List<Color> colors = ColorUtils.getColors();
		int indexOfColor = colors.indexOf(activeColor);
		if (this.increment) {
			indexOfColor++;
		} else {
			indexOfColor--;
		}
		indexOfColor = indexOfColor % colors.size();
		if (indexOfColor == -1) {
			indexOfColor += colors.size();
		}
		final Color newColor = colors.get(indexOfColor);
		this.sudokuPuzzleStyle.setActiveColor(newColor);
		final ColorSelectionPane colorSelectionPane = ViewController.getInstance().getColorSelectionPane();
		colorSelectionPane.setLabelBackgroundColor(newColor);
	}
}
