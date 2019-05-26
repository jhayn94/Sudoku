package sudoku.view.util;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.logging.log4j.util.Strings;

import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

/**
 * This class contains various utilities for coloring cells or candidate.
 */
public class ColorUtils {

	private static final String RED = "#f05c79";

	private static final String BLUE = "#4da6ff";

	private static final String PURPLE = "#a385e0";

	private static final String GREEN = "#79d2a0";

	private static final String ORANGE = "#ffc14d";

	private static final List<Color> COLORS = Arrays.asList(Color.valueOf(RED), Color.valueOf(BLUE),
			Color.valueOf(PURPLE), Color.valueOf(GREEN), Color.valueOf(ORANGE));

	private static final List<KeyCode> APPLY_COLOR_KEY_CODES = Arrays.asList(KeyCode.A, KeyCode.S, KeyCode.D, KeyCode.F,
			KeyCode.G);

	public static final String RED_ENTITY_CSS_CLASS = "sudoku-puzzle-red-entity";

	public static final String ALT_RED_ENTITY_CSS_CLASS = "sudoku-puzzle-alt-red-entity";

	public static final String BLUE_ENTITY_CSS_CLASS = "sudoku-puzzle-blue-entity";

	public static final String ALT_BLUE_ENTITY_CSS_CLASS = "sudoku-puzzle-alt-blue-entity";

	public static final String PURPLE_ENTITY_CSS_CLASS = "sudoku-puzzle-purple-entity";

	public static final String ALT_PURPLE_ENTITY_CSS_CLASS = "sudoku-puzzle-alt-purple-entity";

	public static final String GREEN_ENTITY_CSS_CLASS = "sudoku-puzzle-green-entity";

	public static final String ALT_GREEN_ENTITY_CSS_CLASS = "sudoku-puzzle-alt-green-entity";

	public static final String ORANGE_ENTITY_CSS_CLASS = "sudoku-puzzle-orange-entity";

	public static final String ALT_ORANGE_ENTITY_CSS_CLASS = "sudoku-puzzle-alt-orange-entity";

	private static final List<String> COLOR_CSS_CLASSES = Arrays.asList(RED_ENTITY_CSS_CLASS, ALT_RED_ENTITY_CSS_CLASS,
			BLUE_ENTITY_CSS_CLASS, ALT_BLUE_ENTITY_CSS_CLASS, PURPLE_ENTITY_CSS_CLASS, ALT_PURPLE_ENTITY_CSS_CLASS,
			GREEN_ENTITY_CSS_CLASS, ALT_GREEN_ENTITY_CSS_CLASS, ORANGE_ENTITY_CSS_CLASS, ALT_ORANGE_ENTITY_CSS_CLASS);

	public static List<Color> getColors() {
		return COLORS;
	}

	public List<String> getCssColorClasses() {
		return COLOR_CSS_CLASSES;
	}

	public static List<KeyCode> getApplyColorKeyCodes() {
		return APPLY_COLOR_KEY_CODES;
	}

	public enum ColorState {
		RED(0, false, RED_ENTITY_CSS_CLASS), ALT_RED(0, true, ALT_RED_ENTITY_CSS_CLASS),
		BLUE(1, false, BLUE_ENTITY_CSS_CLASS), ALT_BLUE(1, true, ALT_BLUE_ENTITY_CSS_CLASS),
		PURPLE(2, false, PURPLE_ENTITY_CSS_CLASS), ALT_PURPLE(2, true, ALT_PURPLE_ENTITY_CSS_CLASS),
		GREEN(3, false, GREEN_ENTITY_CSS_CLASS), ALT_GREEN(3, true, ALT_GREEN_ENTITY_CSS_CLASS),
		ORANGE(4, false, ORANGE_ENTITY_CSS_CLASS), ALT_ORANGE(4, true, ALT_ORANGE_ENTITY_CSS_CLASS), NONE;

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
		 * base color state, not the alternative (with shift down). To do this, get the
		 * base color state, then call getFromKeyCode(baseState.getKey(), true).
		 */
		public static ColorState getStateForBaseColor(final Color baseColor) {
			return Arrays.asList(ColorState.values()).stream()
					// The keys + color array list are in the same order, so we can use their
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
