package sudoku.state.model.settings;

import javafx.scene.paint.Color;
import sudoku.core.ViewController;
import sudoku.model.ApplicationSettings;
import sudoku.state.model.ApplicationModelState;
import sudoku.view.settings.ColorSettingsView;
import sudoku.view.util.ColorUtils;

/**
 * This class updates the state of the application when the user clicks save and
 * apply in the miscellaneous settings dialog.
 */
public class SaveColorSettingsState extends AbstractSaveSettingsState {

	private static final int HINT_DELETE_COLOR_INDEX = 5;

	private static final String COLOR_FORMAT_STRING = "#%02x%02x%02x";

	public SaveColorSettingsState(final ApplicationModelState lastState) {
		super(lastState, false);
	}

	@Override
	public void onEnter() {
		final ColorSettingsView colorSettingsView = ViewController.getInstance().getColorSettingsView();
		final Color filterColor = colorSettingsView.getFilterColorPicker().getValue();
		ApplicationSettings.getInstance().setColorForFiltering(this.convertColorToHex(filterColor));

		for (int index = 0; index < ApplicationSettings.NUM_COLORS_USED_IN_COLORING; index++) {
			final Color coloringColor = colorSettingsView.getColoringColorPicker(index).getValue();
			ApplicationSettings.getInstance().setColorUsedInColoring(index, this.convertColorToHex(coloringColor));
		}
		for (int index = 0; index < ApplicationSettings.NUM_COLORS_USED_IN_HINTS
				+ ApplicationSettings.NUM_COLORS_USED_IN_ALSES + 1; index++) {
			final Color hintColor = colorSettingsView.getHintColorPicker(index).getValue();
			if (index < HINT_DELETE_COLOR_INDEX) {
				ApplicationSettings.getInstance().setColorUsedInHints(index, this.convertColorToHex(hintColor));
			} else if (index > HINT_DELETE_COLOR_INDEX) {
				ApplicationSettings.getInstance().setColorUsedInAlses(index - HINT_DELETE_COLOR_INDEX - 1,
						this.convertColorToHex(hintColor));
			} else {
				ApplicationSettings.getInstance().setHintDeleteColor(this.convertColorToHex(hintColor));
			}
		}

		// Update the view in case the old colors were visible somewhere.
		final Color newColor = ColorUtils.getColors().get(0);
		this.sudokuPuzzleStyle.setActiveColor(newColor);
		// TODO - anything needed for color combo buttons?

		ViewController.getInstance().getRootPane().updateColorSettings();
		this.reapplyActiveFilter();
		super.onEnter();
	}

	private String convertColorToHex(final Color color) {
		return String.format(COLOR_FORMAT_STRING, (int) (color.getRed() * 255), (int) (color.getGreen() * 255),
				(int) (color.getBlue() * 255));

	}
}
