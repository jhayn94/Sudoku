package sudoku.model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sudoku.Options;
import sudoku.StepConfig;
import sudoku.view.util.Difficulty;
import sudoku.view.util.ResourceConstants;

/**
 * This class contains methods for getting the default application settings. The
 * fields cannot be changed, unless you overwrite the file containing the
 * defaults.
 */
public class DefaultApplicationSettings {

	private static final Logger LOG = LogManager.getLogger(DefaultApplicationSettings.class);

	private static DefaultApplicationSettings instance;

	public static DefaultApplicationSettings getInstance() {
		if (DefaultApplicationSettings.instance == null) {
			DefaultApplicationSettings.instance = new DefaultApplicationSettings(
					DefaultApplicationSettings.readSettingsFromFile(ResourceConstants.DEFAULT_SETTINGS));
		}
		return DefaultApplicationSettings.instance;
	}

	private static final int NUM_COLORS_USED_IN_HINTS = 5;

	private static final int NUM_COLORS_USED_IN_ALSES = 4;

	private static final int NUM_COLORS_USED_IN_COLORING = 10;

	private static final String TRUE = "true";

	private static final String DIFFICULTY_KEY = "difficulty";

	private static final String SOLVE_TO_REQUIRED_STEP_KEY = "solveToRequiredStep";

	private static final String MUST_CONTAIN_STEP_WITH_NAME_KEY = "mustContainStepWithName";

	private static final String AUTO_MANAGE_CANDIDATES_KEY = "autoManageCandidates";

	private static final String SHOW_PUZZLE_PROGRESS_KEY = "showPuzzleProgress";

	private static final String USE_DIGIT_BUTTONS_FOR_MOUSE_KEY = "useDigitButtonsForMouseActions";

	private static final String COLOR_FOR_FILTERING_KEY = "colorForFiltering";

	private static final String COLOR_FOR_COLORING_KEY = "colorsUsedInColoring";

	private static final String COLOR_FOR_HINTS_KEY = "hintColor";

	private static final String COLOR_FOR_ALSES_KEY = "alsColor";

	private static final String HINT_DELETE_COLOR_KEY = "hintDeleteColor";

	private static final String MAX_SCORE_FOR_KEY = "maxScoreFor";
	// Puzzle Generation settings.
	private final Difficulty difficulty;

	private final String mustContainStepWithName;

	private final boolean solveToRequiredStep;

	// Difficulty settings.
	private final Map<Difficulty, Integer> maxScoreForDifficulty;

	// Miscellaneous settings.
	private final boolean autoManageCandidates;

	private final boolean showPuzzleProgress;

	private final boolean useDigitButtonsForMouseActions;

	// Solver settings not stored in here because I hand typed all the defaults, and
	// it would be way too tedious to type all those.

	// Color settings.
	private final String colorForFiltering;

	private final String[] colorsUsedInColoring;

	private final String[] hintColors;

	private final String[] alsHintColors;

	private final String hintDeleteColor;

	public DefaultApplicationSettings(final Map<String, String> settingsToLoad) {
		this.autoManageCandidates = settingsToLoad.get(AUTO_MANAGE_CANDIDATES_KEY).equals(TRUE);
		this.showPuzzleProgress = settingsToLoad.get(SHOW_PUZZLE_PROGRESS_KEY).equals(TRUE);
		this.useDigitButtonsForMouseActions = settingsToLoad.get(USE_DIGIT_BUTTONS_FOR_MOUSE_KEY).equals(TRUE);
		this.difficulty = Difficulty.valueOf(settingsToLoad.get(DIFFICULTY_KEY));
		this.solveToRequiredStep = settingsToLoad.get(SOLVE_TO_REQUIRED_STEP_KEY).equals(TRUE);
		this.maxScoreForDifficulty = new EnumMap<>(Difficulty.class);
		for (final Difficulty tmpDifficulty : Difficulty.getValidDifficulties()) {
			this.maxScoreForDifficulty.put(tmpDifficulty,
					Integer.parseInt(settingsToLoad.get(MAX_SCORE_FOR_KEY + tmpDifficulty.name())));
		}
		this.mustContainStepWithName = settingsToLoad.get(MUST_CONTAIN_STEP_WITH_NAME_KEY);
		this.colorForFiltering = settingsToLoad.get(COLOR_FOR_FILTERING_KEY);
		this.colorsUsedInColoring = new String[NUM_COLORS_USED_IN_COLORING];
		for (int index = 0; index < this.colorsUsedInColoring.length; index++) {
			this.colorsUsedInColoring[index] = settingsToLoad.get(COLOR_FOR_COLORING_KEY + index);
		}
		this.hintColors = new String[NUM_COLORS_USED_IN_HINTS];
		for (int index = 0; index < this.hintColors.length; index++) {
			this.hintColors[index] = settingsToLoad.get(COLOR_FOR_HINTS_KEY + index);
		}
		this.alsHintColors = new String[NUM_COLORS_USED_IN_ALSES];
		for (int index = 0; index < this.alsHintColors.length; index++) {
			this.alsHintColors[index] = settingsToLoad.get(COLOR_FOR_ALSES_KEY + index);
		}
		this.hintDeleteColor = settingsToLoad.get(HINT_DELETE_COLOR_KEY);
	}

	public Difficulty getDifficulty() {
		return this.difficulty;
	}

	public String getMustContainStepWithName() {
		return this.mustContainStepWithName;
	}

	public boolean isSolveToRequiredStep() {
		return this.solveToRequiredStep;
	}

	public boolean isAutoManageCandidates() {
		return this.autoManageCandidates;
	}

	public boolean isShowPuzzleProgress() {
		return this.showPuzzleProgress;
	}

	public boolean isUseDigitButtonsForMouseActions() {
		return this.useDigitButtonsForMouseActions;
	}

	public String getColorForFiltering() {
		return this.colorForFiltering;
	}

	public String[] getColorsUsedInColoring() {
		return this.colorsUsedInColoring;
	}

	public String[] getHintColors() {
		return this.hintColors;
	}

	public String[] getAlsColors() {
		return this.alsHintColors;
	}

	public String getHintDeleteColor() {
		return this.hintDeleteColor;
	}

	public int getMaxScoreForDifficulty(final String difficultyName) {
		return this.maxScoreForDifficulty.get(Difficulty.valueOf(difficultyName.toUpperCase()));
	}

	/**
	 * Gets the default solver config. Note that this isn't stored in the project's
	 * default settings file. This is because the technique used in
	 * ApplicationSettings wouldn't work here.
	 */
	public List<StepConfig> getSolverConfig() {
		final List<StepConfig> tempList = Arrays
				.asList(Options.getInstance().copyStepConfigs(Options.DEFAULT_SOLVER_STEPS, true, false));
		final List<StepConfig> stepConfigs = new ArrayList<>();
		// The copy to a new list is necessary because some code removes steps from the
		// list.
		stepConfigs.addAll(tempList);
		return stepConfigs;
	}

	private static Map<String, String> readSettingsFromFile(final String filePath) {
		final Map<String, String> settings = new HashMap<>();
		final Path settingsFilePath = Paths.get(new File(filePath).toURI());
		try {
			final List<String> allLines = Files.readAllLines(settingsFilePath);
			// Just so we don't need multiple returns, this is read into a variable
			// then each entry is added to the main mapping.
			final Map<String, String> tempMapping = allLines.stream().filter(line -> !line.isEmpty()).collect(Collectors
					.toMap(line -> line.substring(0, line.indexOf('=')), line -> line.substring(line.indexOf('=') + 1)));
			settings.putAll(tempMapping);
		} catch (final IOException e) {
			LOG.error("{}", e);
		}
		return settings;
	}
}
