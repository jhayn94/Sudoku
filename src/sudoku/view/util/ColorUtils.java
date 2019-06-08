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

	public static final String HINT_COLOR_1_CSS_CLASS = "sudoku-puzzle-color-hint1";

	public static final String HINT_COLOR_2_CSS_CLASS = "sudoku-puzzle-color-hint2";

	public static final String HINT_COLOR_3_CSS_CLASS = "sudoku-puzzle-color-hint3";

	public static final String HINT_COLOR_4_CSS_CLASS = "sudoku-puzzle-color-hint4";

	public static final String HINT_COLOR_5_CSS_CLASS = "sudoku-puzzle-color-hint5";

	public static final String DELETABLE_HINT_CANDIDATE_CSS_CLASS = "sudoku-puzzle-color-deletable-hint-candidate";

	public static final String ALS1_HINT_CANDIDATE_CSS_CLASS = "-sudoku-puzzle-color-hint-als1";

	public static final String ALS2_HINT_CANDIDATE_CSS_CLASS = "-sudoku-puzzle-color-hint-als2";

	public static final String ALS3_HINT_CANDIDATE_CSS_CLASS = "-sudoku-puzzle-color-hint-als3";

	public static final String ALS4_HINT_CANDIDATE_CSS_CLASS = "-sudoku-puzzle-color-hint-als4";

	/**
	 * Returns the "base" colors used for coloring in the application. Note that by
	 * default, each alternate color should be a lighter version of the base color,
	 * but the user is permitted to deviate from this guideline.
	 */
	public static List<Color> getColors() {
		final List<String> hexCodes = new ArrayList<>();
		final String[] colorsUsedInColoring = ApplicationSettings.getInstance().getColorsUsedInColoring();
		for (int index = 0; index < colorsUsedInColoring.length; index += 2) {
			hexCodes.add(colorsUsedInColoring[index]);
		}
		return hexCodes.stream().map(Color::valueOf).collect(Collectors.toList());
	}

	public static List<KeyCode> getApplyColorKeyCodes() {
		return APPLY_COLOR_KEY_CODES;
	}

	public enum ColorState {
		COLORSTATE1A(0, false, COLOR1A_ENTITY_CSS_CLASS), COLORSTATE1B(0, true, COLOR1B_ENTITY_CSS_CLASS),
		COLORSTATE2A(1, false, COLOR2A_ENTITY_CSS_CLASS), COLORSTATE2B(1, true, COLOR2B_ENTITY_CSS_CLASS),
		COLORSTATE3A(2, false, COLOR3A_ENTITY_CSS_CLASS), COLORSTATE3B(2, true, COLOR3B_ENTITY_CSS_CLASS),
		COLORSTATE4A(3, false, COLOR4A_ENTITY_CSS_CLASS), COLORSTATE4B(3, true, COLOR4B_ENTITY_CSS_CLASS),
		COLORSTATE5A(4, false, COLOR5A_ENTITY_CSS_CLASS), COLORSTATE5B(4, true, COLOR5B_ENTITY_CSS_CLASS),
		PRIMARY_HINT_CANDIDATE(-1, false, HINT_COLOR_1_CSS_CLASS),
		SECONDARY_HINT_CANDIDATE(-1, false, HINT_COLOR_2_CSS_CLASS),
		TERTIARY_HINT_CANDIDATE(-1, false, HINT_COLOR_3_CSS_CLASS),
		QUATERNARY_HINT_CANDIDATE(-1, false, HINT_COLOR_4_CSS_CLASS),
		QUINARY_HINT_CANDIDATE(-1, false, HINT_COLOR_5_CSS_CLASS),
		DELETABLE_HINT_CANDIDATE(-1, false, DELETABLE_HINT_CANDIDATE_CSS_CLASS),
		ALS1_HINT_CANDIDATE(-1, false, ALS1_HINT_CANDIDATE_CSS_CLASS),
		ALS2_HINT_CANDIDATE(-1, false, ALS2_HINT_CANDIDATE_CSS_CLASS),
		ALS3_HINT_CANDIDATE(-1, false, ALS3_HINT_CANDIDATE_CSS_CLASS),
		ALS4_HINT_CANDIDATE(-1, false, ALS4_HINT_CANDIDATE_CSS_CLASS), NONE;

		private final int keyIndex;

		private final boolean withShift;

		private final String cssClass;

		private ColorState() {
			this.keyIndex = -2;
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
					// The keys + color array list are in the same order, so we can use
					// their indices interchangeably.
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

	public static List<ColorState> getColoringColorStates() {
		return Arrays.asList(ColorState.values()).stream().filter(colorState -> colorState.keyIndex > -1)
				.collect(Collectors.toList());
	}

	public static List<ColorState> getHintColorStates() {
		return Arrays.asList(ColorState.values()).stream().filter(colorState -> colorState.keyIndex == -1)
				.collect(Collectors.toList());
	}

	/**
	 * Translate's HoDoKu's SolutionStep::getColorCandidates map values into color
	 * state pairs. The order of colors used is orange, purple, blue, and green in
	 * order to avoid using green as much as possible (since it is also used for
	 * most other hint annotations).
	 */
	public static ColorState getColorStateForColoringIndex(final int colorIndex) {
		// The darkest + lightest version of each color is used in an attempt to
		// maximize contrast. It can be hard to see the difference otherwise because
		// candidate labels are small.
		if (colorIndex == 0) {
			return ColorState.QUATERNARY_HINT_CANDIDATE;
		} else if (colorIndex == 1) {
			return ColorState.COLORSTATE5B;
		} else if (colorIndex == 2) {
			return ColorState.TERTIARY_HINT_CANDIDATE;
		} else if (colorIndex == 3) {
			return ColorState.COLORSTATE3B;
		} else if (colorIndex == 4) {
			return ColorState.SECONDARY_HINT_CANDIDATE;
		} else if (colorIndex == 5) {
			return ColorState.COLORSTATE2B;
		} else if (colorIndex == 6) {
			return ColorState.PRIMARY_HINT_CANDIDATE;
		} else {
			return ColorState.COLORSTATE4B;
			// Since there are 9 boxes, there can only be 4 mutually exclusive
			// conjugate pairs for a candidate. Thus, 8 colors are needed at most.
		}
	}

	/**
	 * Gets a color state that corresponds to an index of ALS. The colors are sorted
	 * to avoid the blue and purple as much as possible, since these colors are
	 * often used in ALS annotations.
	 */
	public static ColorState getColorStateForAlmostLockedSetIndex(final int alsIndex) {
		if (alsIndex == 0) {
			return ColorState.ALS1_HINT_CANDIDATE;
		} else if (alsIndex == 1) {
			return ColorState.ALS2_HINT_CANDIDATE;
		} else if (alsIndex == 2) {
			return ColorState.ALS3_HINT_CANDIDATE;
		} else if (alsIndex == 3) {
			return ColorState.ALS4_HINT_CANDIDATE;
		}
		// Don't really know how many ALSes there can be at once, since I don't
		// understand this tactic fully. But, if you see this color, more options need
		// to be added.
		return ColorState.QUINARY_HINT_CANDIDATE;
	}

}
