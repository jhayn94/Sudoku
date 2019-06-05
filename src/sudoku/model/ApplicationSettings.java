package sudoku.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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
import sudoku.core.HodokuFacade;
import sudoku.view.util.Difficulty;
import sudoku.view.util.ResourceConstants;

/**
 * This class contains methods for managing various settings of the application.
 * These settings can be changed based on various menu items.
 */
public class ApplicationSettings {

	private static final Logger LOG = LogManager.getLogger(ApplicationSettings.class);

	private static final String PIPE = "|";

	private static final String EQUALS = "=";

	private static final String NEW_LINE = "\n";

	private static ApplicationSettings instance;

	public static ApplicationSettings getInstance() {
		if (ApplicationSettings.instance == null) {
			ApplicationSettings.instance = new ApplicationSettings(
					ApplicationSettings.readSettingsFromFile(ResourceConstants.SAVED_SETTINGS));
		}
		return ApplicationSettings.instance;
	}

	public static final int NUM_COLORS_USED_IN_COLORING = 10;

	public static final int NUM_COLORS_USED_IN_HINTS = 5;

	public static final int NUM_COLORS_USED_IN_ALSES = 4;

	private static final String TRUE = "true";

	private static final String DIFFICULTY_KEY = "difficulty";

	private static final String SOLVE_TO_REQUIRED_STEP_KEY = "solveToRequiredStep";

	private static final String MUST_CONTAIN_STEP_WITH_NAME_KEY = "mustContainStepWithName";

	private static final String AUTO_MANAGE_CANDIDATES_KEY = "autoManageCandidates";

	private static final String SHOW_PUZZLE_PROGRESS_KEY = "showPuzzleProgress";

	private static final String COLOR_FOR_FILTERING_KEY = "colorForFiltering";

	private static final String COLOR_FOR_COLORING_KEY = "colorsUsedInColoring";

	private static final String COLOR_FOR_HINTS_KEY = "hintColor";

	private static final String COLOR_FOR_ALSES_KEY = "alsColor";

	private static final String HINT_DELETE_COLOR_KEY = "hintDeleteColor";

	private static final String MAX_SCORE_FOR_KEY = "maxScoreFor";

	private static final String STEP_CONFIG_KEY = "stepConfig";

	// Puzzle Generation settings.
	private Difficulty difficulty;

	private String mustContainStepWithName;

	private boolean solveToRequiredStep;

	// Difficulty settings.
	private final Map<Difficulty, Integer> maxScoreForDifficulty;

	// Miscellaneous settings.
	private boolean autoManageCandidates;

	private boolean showPuzzleProgress;

	// Solver settings.
	private List<StepConfig> stepConfigs;

	// Color settings.
	private String colorForFiltering;

	private final String[] colorsUsedInColoring;

	private final String[] hintColors;

	private final String[] alsHintColors;

	private String hintDeleteColor;

	public ApplicationSettings(final Map<String, String> settingsToLoad) {
		this.difficulty = Difficulty.valueOf(settingsToLoad.get(DIFFICULTY_KEY));
		this.solveToRequiredStep = settingsToLoad.get(SOLVE_TO_REQUIRED_STEP_KEY).equals(TRUE);
		this.mustContainStepWithName = settingsToLoad.get(MUST_CONTAIN_STEP_WITH_NAME_KEY);
		this.maxScoreForDifficulty = new EnumMap<>(Difficulty.class);
		for (final Difficulty tmpDifficulty : Difficulty.getValidDifficulties()) {
			this.maxScoreForDifficulty.put(tmpDifficulty,
					Integer.parseInt(settingsToLoad.get(MAX_SCORE_FOR_KEY + tmpDifficulty.name())));
		}
		this.autoManageCandidates = settingsToLoad.get(AUTO_MANAGE_CANDIDATES_KEY).equals(TRUE);
		this.showPuzzleProgress = settingsToLoad.get(SHOW_PUZZLE_PROGRESS_KEY).equals(TRUE);
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
		this.stepConfigs = new ArrayList<>();
		// Use some other copy of the step configs to get access to the different names
		// we need to search for (instead of hard-coding all 30+).
		final List<StepConfig> allStepConfigs = HodokuFacade.getInstance().getSolverConfig();
		allStepConfigs.forEach(tempStepConfig -> {
			final String storedStepConfigData = settingsToLoad.get(STEP_CONFIG_KEY + tempStepConfig.getType().getStepName());
			final String[] stepConfigParameters = storedStepConfigData.split("\\" + PIPE);
			tempStepConfig.setIndex(Integer.parseInt(stepConfigParameters[0]));
			tempStepConfig.setEnabled(Boolean.parseBoolean(stepConfigParameters[1]));
			tempStepConfig.setBaseScore(Integer.parseInt(stepConfigParameters[2]));
			tempStepConfig.setLevel(Integer.parseInt(stepConfigParameters[3]));
			this.stepConfigs.add(tempStepConfig);
		});
		// A little redundant, but saves some code duplication.
		this.setSolverConfig(this.stepConfigs);
	}

	/** Writes the current state of this to the saved settings file. */
	public void writeSettingsToFile() {
		try (BufferedWriter bufferedWriter = new BufferedWriter(
				new FileWriter(new File(ResourceConstants.SAVED_SETTINGS)))) {
			bufferedWriter.write(DIFFICULTY_KEY + EQUALS + this.difficulty.name() + NEW_LINE);
			bufferedWriter.write(MUST_CONTAIN_STEP_WITH_NAME_KEY + EQUALS + this.mustContainStepWithName + NEW_LINE);
			for (final Difficulty tmpDifficulty : Difficulty.getValidDifficulties()) {
				bufferedWriter.write(MAX_SCORE_FOR_KEY + tmpDifficulty.name() + EQUALS
						+ this.maxScoreForDifficulty.get(tmpDifficulty) + NEW_LINE);
			}
			bufferedWriter.write(SOLVE_TO_REQUIRED_STEP_KEY + EQUALS + this.solveToRequiredStep + NEW_LINE);
			bufferedWriter.write(AUTO_MANAGE_CANDIDATES_KEY + EQUALS + this.autoManageCandidates + NEW_LINE);
			bufferedWriter.write(SHOW_PUZZLE_PROGRESS_KEY + EQUALS + this.showPuzzleProgress + NEW_LINE);
			bufferedWriter.write(COLOR_FOR_FILTERING_KEY + EQUALS + this.colorForFiltering + NEW_LINE);
			for (int index = 0; index < this.colorsUsedInColoring.length; index++) {
				bufferedWriter.write(COLOR_FOR_COLORING_KEY + index + EQUALS + this.colorsUsedInColoring[index] + NEW_LINE);
			}
			for (int index = 0; index < this.hintColors.length; index++) {
				bufferedWriter.write(COLOR_FOR_HINTS_KEY + index + EQUALS + this.hintColors[index] + NEW_LINE);
			}
			for (int index = 0; index < this.alsHintColors.length; index++) {
				bufferedWriter.write(COLOR_FOR_ALSES_KEY + index + EQUALS + this.alsHintColors[index] + NEW_LINE);
			}
			bufferedWriter.write(HINT_DELETE_COLOR_KEY + EQUALS + this.hintDeleteColor + NEW_LINE);
			for (int index = 0; index < this.stepConfigs.size(); index++) {
				final StepConfig stepConfig = this.stepConfigs.get(index);
				bufferedWriter.write(STEP_CONFIG_KEY + stepConfig.getType().getStepName() + EQUALS + index + PIPE
						+ stepConfig.isEnabled() + PIPE + stepConfig.getBaseScore() + PIPE + stepConfig.getLevel() + NEW_LINE);
			}
		} catch (final IOException e) {
			LOG.error("{}", e);
			e.printStackTrace();
		}
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

	public void setDifficulty(final Difficulty difficulty) {
		this.difficulty = difficulty;
	}

	public void setMustContainStepWithName(final String mustContainStepWithName) {
		this.mustContainStepWithName = mustContainStepWithName;
	}

	public void setSolveToRequiredStep(final boolean solveToRequiredStep) {
		this.solveToRequiredStep = solveToRequiredStep;
	}

	public void setAutoManageCandidates(final boolean autoManageCandidates) {
		this.autoManageCandidates = autoManageCandidates;
	}

	public void setShowPuzzleProgress(final boolean showPuzzleProgress) {
		this.showPuzzleProgress = showPuzzleProgress;
	}

	public void setColorForFiltering(final String colorForFiltering) {
		this.colorForFiltering = colorForFiltering;
	}

	public void setColorUsedInColoring(final int index, final String color) {
		this.colorsUsedInColoring[index] = color;
	}

	public void setColorUsedInHints(final int index, final String color) {
		this.hintColors[index] = color;
	}

	public void setColorUsedInAlses(final int index, final String color) {
		this.alsHintColors[index] = color;
	}

	public void setHintDeleteColor(final String color) {
		this.hintDeleteColor = color;
	}

	public void setMaxScoreForDifficulty(final String difficultyName, final int maxScore) {
		final Difficulty difficultyToChange = Difficulty.valueOf(difficultyName.toUpperCase());
		this.maxScoreForDifficulty.put(difficultyToChange, maxScore);
		HodokuFacade.getInstance().updateMaxScoreForDifficulty(difficultyToChange, maxScore);
	}

	public void setSolverConfig(final List<StepConfig> stepConfigs) {
		this.stepConfigs = stepConfigs;
		final StepConfig[] newSolverSteps = stepConfigs.stream().toArray(element -> new StepConfig[element]);
		Options.getInstance().solverSteps = Options.getInstance().copyStepConfigs(newSolverSteps, false, true);
		Options.getInstance().adjustOrgSolverSteps();
	}

	public List<StepConfig> getSolverConfig() {
		final List<StepConfig> tempList = Arrays
				.asList(Options.getInstance().copyStepConfigs(Options.getInstance().solverSteps, true, false));
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
