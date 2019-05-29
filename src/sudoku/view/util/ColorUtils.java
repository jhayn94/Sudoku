package sudoku.view.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.apache.logging.log4j.util.Strings;

import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import sudoku.model.ApplicationSettings;

/**
 * This class contains various utilities for coloring cells or candidate.
 */
public class ColorUtils {

	private static final List<KeyCode> APPLY_COLOR_KEY_CODES = Arrays.asList(KeyCode.A, KeyCode.S, KeyCode.D, KeyCode.F,
			KeyCode.G);

	private static final String COLOR1A_ENTITY_CSS_CLASS = "sudoku-puzzle-color1a-entity";

	private static final String COLOR1B_ENTITY_CSS_CLASS = "sudoku-puzzle-color1b-entity";

	private static final String COLOR2A_ENTITY_CSS_CLASS = "sudoku-puzzle-color2a-entity";

	private static final String COLOR2B_ENTITY_CSS_CLASS = "sudoku-puzzle-color2b-entity";

	private static final String COLOR3A_ENTITY_CSS_CLASS = "sudoku-puzzle-color3a-entity";

	private static final String COLOR3B_ENTITY_CSS_CLASS = "sudoku-puzzle-color3b-entity";

	private static final String COLOR4A_ENTITY_CSS_CLASS = "sudoku-puzzle-color4a-entity";

	private static final String COLOR4B_ENTITY_CSS_CLASS = "sudoku-puzzle-color4b-entity";

	private static final String COLOR5A_ENTITY_CSS_CLASS = "sudoku-puzzle-color5a-entity";

	private static final String COLOR5B_ENTITY_CSS_CLASS = "sudoku-puzzle-color5b-entity";

	private static final List<String> COLOR_CSS_CLASSES = Arrays.asList(COLOR1A_ENTITY_CSS_CLASS,
			COLOR1B_ENTITY_CSS_CLASS, COLOR2A_ENTITY_CSS_CLASS, COLOR2B_ENTITY_CSS_CLASS, COLOR3A_ENTITY_CSS_CLASS,
			COLOR3B_ENTITY_CSS_CLASS, COLOR4A_ENTITY_CSS_CLASS, COLOR4B_ENTITY_CSS_CLASS, COLOR5A_ENTITY_CSS_CLASS,
			COLOR5B_ENTITY_CSS_CLASS);

	/**
	 * Returns the "base" colors used for coloring in the application. Note that
	 * by default, each alternate color should be a lighter version of the base
	 * color, but the user is permitted to deviate from this guideline.
	 */
	public static List<Color> getColors() {
		final List<String> hexCodes = new ArrayList<>();
		final String[] colorsUsedInColoring = ApplicationSettings.getInstance().getColorsUsedInColoring();
		for (int index = 0; index < colorsUsedInColoring.length; index += 2) {
			hexCodes.add(colorsUsedInColoring[index]);
		}
		return hexCodes.stream().map(Color::valueOf).collect(Collectors.toList());
	}

	public List<String> getCssColorClasses() {
		return COLOR_CSS_CLASSES;
	}

	public static List<KeyCode> getApplyColorKeyCodes() {
		return APPLY_COLOR_KEY_CODES;
	}

	public enum ColorState {
		COLORSTATE1A(0, false, COLOR1A_ENTITY_CSS_CLASS),
		COLORSTATE1B(0, true, COLOR1B_ENTITY_CSS_CLASS),
		COLORSTATE2A(1, false, COLOR2A_ENTITY_CSS_CLASS),
		COLORSTATE2B(1, true, COLOR2B_ENTITY_CSS_CLASS),
		COLORSTATE3A(2, false, COLOR3A_ENTITY_CSS_CLASS),
		COLORSTATE3B(2, true, COLOR3B_ENTITY_CSS_CLASS),
		COLORSTATE4A(3, false, COLOR4A_ENTITY_CSS_CLASS),
		COLORSTATE4B(3, true, COLOR4B_ENTITY_CSS_CLASS),
		COLORSTATE5A(4, false, COLOR5A_ENTITY_CSS_CLASS),
		COLORSTATE5B(4, true, COLOR5B_ENTITY_CSS_CLASS),
		NONE;

		private final int keyIndex;

		private final boolean withShift;

		private final String cssClass;

		private ColorState() {
			this.keyIndex = -1;
			this.withShift = false;
			this.cssClass = Strings.EMPTY;
		}

		private ColorState(final int keyIndex, final boolean withShift, final String cssClass) {
			this.keyIndex = keyIndex;
			this.withShift = withShift;
			this.cssClass = cssClass;
		}

		public KeyCode getKey() {
			return ColorUtils.getApplyColorKeyCodes().get(this.keyIndex);
		}

		public boolean isWithShift() {
			return this.withShift;
		}

		public String getCssClass() {
			return this.cssClass;
		}

		/**
		 * Gets a color state based on the color passed. This only will retrieve the
		 * base color state, not the alternative (with shift down). To do this, get
		 * the base color state, then call getFromKeyCode(baseState.getKey(), true).
		 */
		public static ColorState getStateForBaseColor(final Color baseColor) {
			return Arrays.asList(ColorState.values()).stream()
					// The keys + color array list are in the same order, so we can use
					// their
					// indices interchangeably.
					.filter(colorState -> ColorUtils.getColors().indexOf(baseColor) == colorState.keyIndex).findFirst()
					.orElseThrow(NoSuchElementException::new);
		}

		public static ColorState getFromKeyCode(final KeyCode keyCode, final boolean isWithShift) {
			return Arrays.asList(ColorState.values()).stream()
					.filter(colorState -> ColorUtils.getApplyColorKeyCodes().get(colorState.keyIndex) == keyCode
							&& colorState.withShift == isWithShift)
					.findFirst().orElseThrow(NoSuchElementException::new);
		}
	}

}
