package sudoku.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import sudoku.StepConfig;
import sudoku.view.util.Difficulty;
import sudoku.view.util.ResourceConstants;

/**
 * This class contains methods for managing various settings of the application.
 * These settings can be changed based on various menu items.
 */
public class ApplicationSettings {

	private static final String EQUALS = "=";

	private static final String NEW_LINE = "\n";

	private static ApplicationSettings instance;

	public static ApplicationSettings getInstance() {
		if (ApplicationSettings.instance == null) {
			ApplicationSettings.instance = new ApplicationSettings(readSettingsFromFile(ResourceConstants.SAVED_SETTINGS));
		}
		return ApplicationSettings.instance;
	}

	public static final int NUM_COLORS_USED_IN_COLORING = 10;

	private static final String TRUE = "true";

	private static final String DIFFICULTY_KEY = "difficulty";

	private static final String SOLVE_TO_REQUIRED_STEP_KEY = "solveToRequiredStep";

	private static final String MUST_CONTAIN_STEP_WITH_NAME_KEY = "mustContainStepWithName";

	private static final String AUTO_MANAGE_CANDIDATES_KEY = "autoManageCandidates";

	private static final String COLOR_FOR_FILTERING_KEY = "colorForFiltering";

	private static final String COLOR_FOR_COLORING_KEY = "colorsUsedInColoring";

	private static final String MAX_SCORE_FOR_KEY = "maxScoreFor";

	// Puzzle Generation settings.
	private Difficulty difficulty;

	private String mustContainStepWithName;

	private boolean solveToRequiredStep;

	// Difficulty settings.
	private final Map<Difficulty, Integer> maxScoreForDifficulty;

	// Miscellaneous settings.
	private boolean autoManageCandidates;

	// Solver settings.
	// TODO - confirm how this will work.
	private StepConfig[] solutionStepConfigurations;

	// Color settings.
	private String colorForFiltering;

	private final String[] colorsUsedInColoring;

	public ApplicationSettings(final Map<String, String> settingsToLoad) {
		this.difficulty = Difficulty.valueOf(settingsToLoad.get(DIFFICULTY_KEY));
		this.solveToRequiredStep = settingsToLoad.get(SOLVE_TO_REQUIRED_STEP_KEY).equals(TRUE);
		this.mustContainStepWithName = settingsToLoad.get(MUST_CONTAIN_STEP_WITH_NAME_KEY);
		this.maxScoreForDifficulty = new EnumMap<>(Difficulty.class);
		for (final Difficulty tmpDifficulty : Arrays.asList(Difficulty.values())) {
			this.maxScoreForDifficulty.put(tmpDifficulty,
					Integer.parseInt(settingsToLoad.get(MAX_SCORE_FOR_KEY + tmpDifficulty.name())));
		}
		this.autoManageCandidates = settingsToLoad.get(AUTO_MANAGE_CANDIDATES_KEY).equals(TRUE);
		this.colorForFiltering = settingsToLoad.get(COLOR_FOR_FILTERING_KEY);
		this.colorsUsedInColoring = new String[NUM_COLORS_USED_IN_COLORING];
		for (int index = 0; index < this.colorsUsedInColoring.length; index++) {
			this.colorsUsedInColoring[index] = settingsToLoad.get(COLOR_FOR_COLORING_KEY + index);
		}
	}

	/** Writes the current state of this to the saved settings file. */
	public void writeSettingsToFile() {
		try (BufferedWriter bufferedWriter = new BufferedWriter(
				new FileWriter(new File(ResourceConstants.SAVED_SETTINGS)))) {
			bufferedWriter.write(DIFFICULTY_KEY + EQUALS + this.difficulty.name() + NEW_LINE);
			bufferedWriter.write(MUST_CONTAIN_STEP_WITH_NAME_KEY + EQUALS + this.mustContainStepWithName + NEW_LINE);
			for (final Difficulty tmpDifficulty : Arrays.asList(Difficulty.values())) {
				bufferedWriter.write(MAX_SCORE_FOR_KEY + tmpDifficulty.name() + EQUALS
						+ this.maxScoreForDifficulty.get(tmpDifficulty) + NEW_LINE);
			}
			bufferedWriter.write(SOLVE_TO_REQUIRED_STEP_KEY + EQUALS + this.solveToRequiredStep + NEW_LINE);
			bufferedWriter.write(AUTO_MANAGE_CANDIDATES_KEY + EQUALS + this.autoManageCandidates + NEW_LINE);
			bufferedWriter.write(COLOR_FOR_FILTERING_KEY + EQUALS + this.colorForFiltering + NEW_LINE);
			for (int index = 0; index < this.colorsUsedInColoring.length; index++) {
				bufferedWriter.write(COLOR_FOR_COLORING_KEY + index + EQUALS + this.colorsUsedInColoring[index] + NEW_LINE);
			}
		} catch (final IOException e) {
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

	public StepConfig[] getSolutionStepConfigurations() {
		return this.solutionStepConfigurations;
	}

	public String getColorForFiltering() {
		return this.colorForFiltering;
	}

	public String[] getColorsUsedInColoring() {
		return this.colorsUsedInColoring;
	}

	public int getMaxScoreForDifficulty(final String difficultyName) {
		return this.maxScoreForDifficulty.get(Difficulty.valueOf(difficultyName.toUpperCase()));
	}

	public void setSolutionStepConfigurations(final StepConfig[] solutionStepConfigurations) {
		this.solutionStepConfigurations = solutionStepConfigurations;
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

	public void setColorForFiltering(final String colorForFiltering) {
		this.colorForFiltering = colorForFiltering;
	}

	public void setColorUsedInColoring(final int index, final String color) {
		this.colorsUsedInColoring[index] = color;
	}

	public void setMaxScoreForDifficulty(final String difficultyName, final int maxScore) {
		this.maxScoreForDifficulty.put(Difficulty.valueOf(difficultyName.toUpperCase()), maxScore);
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
			e.printStackTrace();
		}
		return settings;
	}

}
