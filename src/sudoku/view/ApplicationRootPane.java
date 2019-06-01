package sudoku.view;

import javafx.scene.layout.BorderPane;
import sudoku.model.ApplicationSettings;

/**
 * This class represents the root element of the application.
 */
public class ApplicationRootPane extends BorderPane {

	private static final String SUDOKU_COLOR_COLORING_CSS_CLASS_BASE = "-sudoku-color-coloring";

	private static final String SELECTED_CSS_CLASS = "-sudoku-color-selected: ";

	private static final String SEMI_COLON = ";";

	private static final String ROOT_CSS_CLASS = "root";

	public ApplicationRootPane() {
		super();
		this.configure();
	}

	private void configure() {
		this.updateColorSettings();
	}

	/**
	 * Reads color settings and applies them to the root element of the CSS
	 * stylesheet.
	 */
	public void updateColorSettings() {
		// A little trick to apply CSS to the root if you don't know which element
		// is truly the root is to add the class, update the style, then remove the
		// class. This is what is done here.
		this.getStyleClass().add(ROOT_CSS_CLASS);
		final String colorForFiltering = ApplicationSettings.getInstance().getColorForFiltering();
		final String[] colorsUsedInColoring = ApplicationSettings.getInstance().getColorsUsedInColoring();
		final StringBuilder style = new StringBuilder();
		style.append(SELECTED_CSS_CLASS + colorForFiltering + SEMI_COLON);
		for (int index = 0; index < colorsUsedInColoring.length; index++) {
			final int colorPair = index / 2 + 1;
			final String colorPairIndicator = index % 2 == 0 ? "a" : "b";
			final String key = SUDOKU_COLOR_COLORING_CSS_CLASS_BASE + colorPair + colorPairIndicator;
			style.append(key + ": " + colorsUsedInColoring[index] + SEMI_COLON);
		}
		this.setStyle(style.toString());
		this.getStyleClass().remove(ROOT_CSS_CLASS);
	}

}