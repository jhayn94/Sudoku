package sudoku.core;

import sudoku.view.util.PuzzleDifficulty;
import sudoku.view.util.SolutionCategory;
import sudoku.view.util.SolutionTechnique;
import sudoku.view.util.SolutionTechniqueConfiguration;

/** This class contains methods for storing and retrieving cached puzzles. */
public class PuzzleConfiguration {

	private static final int NUM_DIFFICULTY_LEVELS = PuzzleDifficulty.values().length;

	private static final int DEFAULT_DIFFICULTY_LEVEL = 0;

	private static PuzzleConfiguration instance;

	private final String[][] standardPuzzles;

	private final String[] puzzlesSolvedToSpecificStep;

	private final String[] puzzlesWithSpecificStep;

	private final int practisingPuzzlesLevel;

	private final SolutionTechniqueConfiguration[] orgSolverSteps;

	public static PuzzleConfiguration getInstance() {
		if (instance == null) {
			instance = new PuzzleConfiguration();
		}
		return instance;
	}

	// Private constructor to prevent external instantiation.
	private PuzzleConfiguration() {
		this.standardPuzzles = new String[NUM_DIFFICULTY_LEVELS][CACHE_SIZE];
		this.puzzlesSolvedToSpecificStep = new String[CACHE_SIZE];
		this.puzzlesWithSpecificStep = new String[CACHE_SIZE];
		this.practisingPuzzlesLevel = -1;
		this.orgSolverSteps = DEFAULT_SOLVER_STEPS;
	}

	public static final int CACHE_SIZE = 10;

	public String[][] getStandardPuzzles() {
		return this.standardPuzzles;
	}

	public String[] getLearningPuzzles() {
		return this.puzzlesSolvedToSpecificStep;
	}

	public String[] getPracticeTechniquePuzzles() {
		return this.puzzlesWithSpecificStep;
	}

	public int getPractisingPuzzlesLevel() {
		return this.practisingPuzzlesLevel;
	}

	public SolutionTechniqueConfiguration[] getOrgSolverSteps() {
		return this.orgSolverSteps;
	}

	public static final SolutionTechniqueConfiguration[] DEFAULT_SOLVER_STEPS = {
			new SolutionTechniqueConfiguration(Integer.MAX_VALUE - 1, SolutionTechnique.INCOMPLETE,
					PuzzleDifficulty.INVALID.ordinal(), SolutionCategory.LAST_RESORT, 0, false, false,
					Integer.MAX_VALUE - 1, false, false),
			new SolutionTechniqueConfiguration(Integer.MAX_VALUE, SolutionTechnique.GIVE_UP,
					PuzzleDifficulty.DIABOLICAL.ordinal(), SolutionCategory.LAST_RESORT, 20000, true, false,
					Integer.MAX_VALUE, true, false),
			new SolutionTechniqueConfiguration(100, SolutionTechnique.FULL_HOUSE, PuzzleDifficulty.EASY.ordinal(),
					SolutionCategory.SINGLES, 4, true, true, 100, true, false),
			new SolutionTechniqueConfiguration(200, SolutionTechnique.NAKED_SINGLE, PuzzleDifficulty.EASY.ordinal(),
					SolutionCategory.SINGLES, 4, true, true, 200, true, false),
			new SolutionTechniqueConfiguration(300, SolutionTechnique.HIDDEN_SINGLE, PuzzleDifficulty.EASY.ordinal(),
					SolutionCategory.SINGLES, 14, true, true, 300, true, false),
			new SolutionTechniqueConfiguration(1000, SolutionTechnique.LOCKED_PAIR, PuzzleDifficulty.MEDIUM.ordinal(),
					SolutionCategory.INTERSECTIONS, 40, true, true, 1000, true, false),
			new SolutionTechniqueConfiguration(1100, SolutionTechnique.LOCKED_TRIPLE, PuzzleDifficulty.MEDIUM.ordinal(),
					SolutionCategory.INTERSECTIONS, 60, true, true, 1100, true, false),
			// new SolutionTechniqueConfiguration(1200, SolutionTechnique.LOCKED_CANDIDATES,
			// PuzzleDifficulty.MEDIUM.ordinal(), SolutionCategory.INTERSECTIONS, 50, 0,
			// true,
			// true, 1200, true, false),
			new SolutionTechniqueConfiguration(1200, SolutionTechnique.LOCKED_CANDIDATES_1,
					PuzzleDifficulty.MEDIUM.ordinal(), SolutionCategory.INTERSECTIONS, 50, true, true, 1200, true,
					false),
			new SolutionTechniqueConfiguration(1300, SolutionTechnique.NAKED_PAIR, PuzzleDifficulty.MEDIUM.ordinal(),
					SolutionCategory.SUBSETS, 60, true, true, 1300, true, false),
			new SolutionTechniqueConfiguration(1400, SolutionTechnique.NAKED_TRIPLE, PuzzleDifficulty.MEDIUM.ordinal(),
					SolutionCategory.SUBSETS, 80, true, true, 1400, true, false),
			new SolutionTechniqueConfiguration(1500, SolutionTechnique.HIDDEN_PAIR, PuzzleDifficulty.MEDIUM.ordinal(),
					SolutionCategory.SUBSETS, 70, true, true, 1500, true, false),
			new SolutionTechniqueConfiguration(1600, SolutionTechnique.HIDDEN_TRIPLE, PuzzleDifficulty.MEDIUM.ordinal(),
					SolutionCategory.SUBSETS, 100, true, true, 1600, true, false),
			new SolutionTechniqueConfiguration(2000, SolutionTechnique.NAKED_QUADRUPLE, PuzzleDifficulty.HARD.ordinal(),
					SolutionCategory.SUBSETS, 120, true, true, 2000, true, false),
			new SolutionTechniqueConfiguration(2100, SolutionTechnique.HIDDEN_QUADRUPLE,
					PuzzleDifficulty.HARD.ordinal(), SolutionCategory.SUBSETS, 150, true, true, 2100, true, false),
			new SolutionTechniqueConfiguration(2200, SolutionTechnique.X_WING, PuzzleDifficulty.HARD.ordinal(),
					SolutionCategory.BASIC_FISH, 140, true, false, 2200, false, false),
			new SolutionTechniqueConfiguration(2300, SolutionTechnique.SWORDFISH, PuzzleDifficulty.HARD.ordinal(),
					SolutionCategory.BASIC_FISH, 150, true, false, 2300, false, false),
			new SolutionTechniqueConfiguration(2400, SolutionTechnique.JELLYFISH, PuzzleDifficulty.HARD.ordinal(),
					SolutionCategory.BASIC_FISH, 160, true, false, 2400, false, false),
			new SolutionTechniqueConfiguration(2500, SolutionTechnique.SQUIRMBAG, PuzzleDifficulty.VERY_HARD.ordinal(),
					SolutionCategory.BASIC_FISH, 470, false, false, 2500, false, false),
			new SolutionTechniqueConfiguration(2600, SolutionTechnique.WHALE, PuzzleDifficulty.VERY_HARD.ordinal(),
					SolutionCategory.BASIC_FISH, 470, false, false, 2600, false, false),
			new SolutionTechniqueConfiguration(2700, SolutionTechnique.LEVIATHAN, PuzzleDifficulty.VERY_HARD.ordinal(),
					SolutionCategory.BASIC_FISH, 470, false, false, 2700, false, false),
			new SolutionTechniqueConfiguration(2800, SolutionTechnique.REMOTE_PAIR, PuzzleDifficulty.HARD.ordinal(),
					SolutionCategory.CHAINS_AND_LOOPS, 110, true, true, 2800, false, false),
			new SolutionTechniqueConfiguration(2900, SolutionTechnique.BUG_PLUS_1, PuzzleDifficulty.HARD.ordinal(),
					SolutionCategory.UNIQUENESS, 100, true, true, 2900, false, false),
			new SolutionTechniqueConfiguration(3000, SolutionTechnique.SKYSCRAPER, PuzzleDifficulty.HARD.ordinal(),
					SolutionCategory.SINGLE_DIGIT_PATTERNS, 130, true, true, 3000, false, false),
			new SolutionTechniqueConfiguration(3200, SolutionTechnique.W_WING, PuzzleDifficulty.HARD.ordinal(),
					SolutionCategory.WINGS, 150, true, true, 3200, false, false),
			new SolutionTechniqueConfiguration(3100, SolutionTechnique.TWO_STRING_KITE, PuzzleDifficulty.HARD.ordinal(),
					SolutionCategory.SINGLE_DIGIT_PATTERNS, 150, true, true, 3100, false, false),
			new SolutionTechniqueConfiguration(3300, SolutionTechnique.XY_WING, PuzzleDifficulty.HARD.ordinal(),
					SolutionCategory.WINGS, 160, true, true, 3300, false, false),
			new SolutionTechniqueConfiguration(3400, SolutionTechnique.XYZ_WING, PuzzleDifficulty.HARD.ordinal(),
					SolutionCategory.WINGS, 180, true, true, 3400, false, false),
			new SolutionTechniqueConfiguration(3500, SolutionTechnique.UNIQUENESS_1, PuzzleDifficulty.HARD.ordinal(),
					SolutionCategory.UNIQUENESS, 100, true, true, 3500, false, false),
			new SolutionTechniqueConfiguration(3600, SolutionTechnique.UNIQUENESS_2, PuzzleDifficulty.HARD.ordinal(),
					SolutionCategory.UNIQUENESS, 100, true, true, 3600, false, false),
			new SolutionTechniqueConfiguration(3700, SolutionTechnique.UNIQUENESS_3, PuzzleDifficulty.HARD.ordinal(),
					SolutionCategory.UNIQUENESS, 100, true, true, 3700, false, false),
			new SolutionTechniqueConfiguration(3800, SolutionTechnique.UNIQUENESS_4, PuzzleDifficulty.HARD.ordinal(),
					SolutionCategory.UNIQUENESS, 100, true, true, 3800, false, false),
			new SolutionTechniqueConfiguration(3900, SolutionTechnique.UNIQUENESS_5, PuzzleDifficulty.HARD.ordinal(),
					SolutionCategory.UNIQUENESS, 100, true, true, 3900, false, false),
			new SolutionTechniqueConfiguration(4000, SolutionTechnique.UNIQUENESS_6, PuzzleDifficulty.HARD.ordinal(),
					SolutionCategory.UNIQUENESS, 100, true, true, 4000, false, false),
			new SolutionTechniqueConfiguration(4100, SolutionTechnique.FINNED_X_WING, PuzzleDifficulty.HARD.ordinal(),
					SolutionCategory.FINNED_BASIC_FISH, 130, true, false, 4100, false, false),
			new SolutionTechniqueConfiguration(4200, SolutionTechnique.SASHIMI_X_WING, PuzzleDifficulty.HARD.ordinal(),
					SolutionCategory.FINNED_BASIC_FISH, 150, true, false, 4200, false, false),
			new SolutionTechniqueConfiguration(4300, SolutionTechnique.FINNED_SWORDFISH,
					PuzzleDifficulty.VERY_HARD.ordinal(), SolutionCategory.FINNED_BASIC_FISH, 200, true, false, 4300,
					false, false),
			new SolutionTechniqueConfiguration(4400, SolutionTechnique.SASHIMI_SWORDFISH,
					PuzzleDifficulty.VERY_HARD.ordinal(), SolutionCategory.FINNED_BASIC_FISH, 240, true, false, 4400,
					false, false),
			new SolutionTechniqueConfiguration(4500, SolutionTechnique.FINNED_JELLYFISH,
					PuzzleDifficulty.VERY_HARD.ordinal(), SolutionCategory.FINNED_BASIC_FISH, 250, true, false, 4500,
					false, false),
			new SolutionTechniqueConfiguration(4600, SolutionTechnique.SASHIMI_JELLYFISH,
					PuzzleDifficulty.VERY_HARD.ordinal(), SolutionCategory.FINNED_BASIC_FISH, 260, true, false, 4600,
					false, false),
			new SolutionTechniqueConfiguration(4700, SolutionTechnique.FINNED_SQUIRMBAG,
					PuzzleDifficulty.VERY_HARD.ordinal(), SolutionCategory.FINNED_BASIC_FISH, 470, false, false, 4700,
					false, false),
			new SolutionTechniqueConfiguration(4800, SolutionTechnique.SASHIMI_SQUIRMBAG,
					PuzzleDifficulty.VERY_HARD.ordinal(), SolutionCategory.FINNED_BASIC_FISH, 470, false, false, 4800,
					false, false),
			new SolutionTechniqueConfiguration(4900, SolutionTechnique.FINNED_WHALE,
					PuzzleDifficulty.VERY_HARD.ordinal(), SolutionCategory.FINNED_BASIC_FISH, 470, false, false, 4900,
					false, false),
			new SolutionTechniqueConfiguration(5000, SolutionTechnique.SASHIMI_WHALE,
					PuzzleDifficulty.VERY_HARD.ordinal(), SolutionCategory.FINNED_BASIC_FISH, 470, false, false, 5000,
					false, false),
			new SolutionTechniqueConfiguration(5100, SolutionTechnique.FINNED_LEVIATHAN,
					PuzzleDifficulty.VERY_HARD.ordinal(), SolutionCategory.FINNED_BASIC_FISH, 470, false, false, 5100,
					false, false),
			new SolutionTechniqueConfiguration(5200, SolutionTechnique.SASHIMI_LEVIATHAN,
					PuzzleDifficulty.VERY_HARD.ordinal(), SolutionCategory.FINNED_BASIC_FISH, 470, false, false, 5200,
					false, false),
			new SolutionTechniqueConfiguration(5300, SolutionTechnique.SUE_DE_COQ, PuzzleDifficulty.VERY_HARD.ordinal(),
					SolutionCategory.MISCELLANEOUS, 250, true, true, 5300, false, false),
			new SolutionTechniqueConfiguration(5400, SolutionTechnique.X_CHAIN, PuzzleDifficulty.VERY_HARD.ordinal(),
					SolutionCategory.CHAINS_AND_LOOPS, 260, true, true, 5400, false, false),
			new SolutionTechniqueConfiguration(5500, SolutionTechnique.XY_CHAIN, PuzzleDifficulty.VERY_HARD.ordinal(),
					SolutionCategory.CHAINS_AND_LOOPS, 260, true, true, 5500, false, false),
			new SolutionTechniqueConfiguration(5600, SolutionTechnique.NICE_LOOP, PuzzleDifficulty.VERY_HARD.ordinal(),
					SolutionCategory.CHAINS_AND_LOOPS, 280, true, true, 5600, false, false),
			new SolutionTechniqueConfiguration(5700, SolutionTechnique.ALS_XZ, PuzzleDifficulty.VERY_HARD.ordinal(),
					SolutionCategory.ALMOST_LOCKED_SETS, 300, true, true, 5700, false, false),
			new SolutionTechniqueConfiguration(5800, SolutionTechnique.ALS_XY_WING,
					PuzzleDifficulty.VERY_HARD.ordinal(), SolutionCategory.ALMOST_LOCKED_SETS, 320, true, true, 5800,
					false, false),
			new SolutionTechniqueConfiguration(5900, SolutionTechnique.ALS_XY_CHAIN,
					PuzzleDifficulty.VERY_HARD.ordinal(), SolutionCategory.ALMOST_LOCKED_SETS, 340, true, true, 5900,
					false, false),
			new SolutionTechniqueConfiguration(6000, SolutionTechnique.DEATH_BLOSSOM,
					PuzzleDifficulty.VERY_HARD.ordinal(), SolutionCategory.ALMOST_LOCKED_SETS, 360, false, true, 6000,
					false, false),
			new SolutionTechniqueConfiguration(6100, SolutionTechnique.FRANKEN_X_WING,
					PuzzleDifficulty.VERY_HARD.ordinal(), SolutionCategory.FRANKEN_FISH, 300, true, false, 6100, false,
					false),
			new SolutionTechniqueConfiguration(6200, SolutionTechnique.FRANKEN_SWORDFISH,
					PuzzleDifficulty.VERY_HARD.ordinal(), SolutionCategory.FRANKEN_FISH, 350, true, false, 6200, false,
					false),
			new SolutionTechniqueConfiguration(6300, SolutionTechnique.FRANKEN_JELLYFISH,
					PuzzleDifficulty.VERY_HARD.ordinal(), SolutionCategory.FRANKEN_FISH, 370, false, false, 6300, false,
					false),
			new SolutionTechniqueConfiguration(6400, SolutionTechnique.FRANKEN_SQUIRMBAG,
					PuzzleDifficulty.DIABOLICAL.ordinal(), SolutionCategory.FRANKEN_FISH, 470, false, false, 6400,
					false, false),
			new SolutionTechniqueConfiguration(6500, SolutionTechnique.FRANKEN_WHALE,
					PuzzleDifficulty.DIABOLICAL.ordinal(), SolutionCategory.FRANKEN_FISH, 470, false, false, 6500,
					false, false),
			new SolutionTechniqueConfiguration(6600, SolutionTechnique.FRANKEN_LEVIATHAN,
					PuzzleDifficulty.DIABOLICAL.ordinal(), SolutionCategory.FRANKEN_FISH, 470, false, false, 6600,
					false, false),
			new SolutionTechniqueConfiguration(6700, SolutionTechnique.FINNED_FRANKEN_X_WING,
					PuzzleDifficulty.VERY_HARD.ordinal(), SolutionCategory.FINNED_FRANKEN_FISH, 390, true, false, 6700,
					false, false),
			new SolutionTechniqueConfiguration(6800, SolutionTechnique.FINNED_FRANKEN_SWORDFISH,
					PuzzleDifficulty.VERY_HARD.ordinal(), SolutionCategory.FINNED_FRANKEN_FISH, 410, true, false, 6800,
					false, false),
			new SolutionTechniqueConfiguration(6900, SolutionTechnique.FINNED_FRANKEN_JELLYFISH,
					PuzzleDifficulty.VERY_HARD.ordinal(), SolutionCategory.FINNED_FRANKEN_FISH, 430, false, false, 6900,
					false, false),
			new SolutionTechniqueConfiguration(7000, SolutionTechnique.FINNED_FRANKEN_SQUIRMBAG,
					PuzzleDifficulty.DIABOLICAL.ordinal(), SolutionCategory.FINNED_FRANKEN_FISH, 470, false, false,
					7000, false, false),
			new SolutionTechniqueConfiguration(7100, SolutionTechnique.FINNED_FRANKEN_WHALE,
					PuzzleDifficulty.DIABOLICAL.ordinal(), SolutionCategory.FINNED_FRANKEN_FISH, 470, false, false,
					7100, false, false),
			new SolutionTechniqueConfiguration(7200, SolutionTechnique.FINNED_FRANKEN_LEVIATHAN,
					PuzzleDifficulty.DIABOLICAL.ordinal(), SolutionCategory.FINNED_FRANKEN_FISH, 470, false, false,
					7200, false, false),
			new SolutionTechniqueConfiguration(7300, SolutionTechnique.MUTANT_X_WING,
					PuzzleDifficulty.DIABOLICAL.ordinal(), SolutionCategory.MUTANT_FISH, 450, false, false, 7300, false,
					false),
			new SolutionTechniqueConfiguration(7400, SolutionTechnique.MUTANT_SWORDFISH,
					PuzzleDifficulty.DIABOLICAL.ordinal(), SolutionCategory.MUTANT_FISH, 450, false, false, 7400, false,
					false),
			new SolutionTechniqueConfiguration(7500, SolutionTechnique.MUTANT_JELLYFISH,
					PuzzleDifficulty.DIABOLICAL.ordinal(), SolutionCategory.MUTANT_FISH, 450, false, false, 7500, false,
					false),
			new SolutionTechniqueConfiguration(7600, SolutionTechnique.MUTANT_SQUIRMBAG,
					PuzzleDifficulty.DIABOLICAL.ordinal(), SolutionCategory.MUTANT_FISH, 470, false, false, 7600, false,
					false),
			new SolutionTechniqueConfiguration(7700, SolutionTechnique.MUTANT_WHALE,
					PuzzleDifficulty.DIABOLICAL.ordinal(), SolutionCategory.MUTANT_FISH, 470, false, false, 7700, false,
					false),
			new SolutionTechniqueConfiguration(7800, SolutionTechnique.MUTANT_LEVIATHAN,
					PuzzleDifficulty.DIABOLICAL.ordinal(), SolutionCategory.MUTANT_FISH, 470, false, false, 7800, false,
					false),
			new SolutionTechniqueConfiguration(7900, SolutionTechnique.FINNED_MUTANT_X_WING,
					PuzzleDifficulty.DIABOLICAL.ordinal(), SolutionCategory.FINNED_MUTANT_FISH, 470, false, false, 7900,
					false, false),
			new SolutionTechniqueConfiguration(8000, SolutionTechnique.FINNED_MUTANT_SWORDFISH,
					PuzzleDifficulty.DIABOLICAL.ordinal(), SolutionCategory.FINNED_MUTANT_FISH, 470, false, false, 8000,
					false, false),
			new SolutionTechniqueConfiguration(8100, SolutionTechnique.FINNED_MUTANT_JELLYFISH,
					PuzzleDifficulty.DIABOLICAL.ordinal(), SolutionCategory.FINNED_MUTANT_FISH, 470, false, false, 8100,
					false, false),
			new SolutionTechniqueConfiguration(8200, SolutionTechnique.FINNED_MUTANT_SQUIRMBAG,
					PuzzleDifficulty.DIABOLICAL.ordinal(), SolutionCategory.FINNED_MUTANT_FISH, 470, false, false, 8200,
					false, false),
			new SolutionTechniqueConfiguration(8300, SolutionTechnique.FINNED_MUTANT_WHALE,
					PuzzleDifficulty.DIABOLICAL.ordinal(), SolutionCategory.FINNED_MUTANT_FISH, 470, false, false, 8300,
					false, false),
			new SolutionTechniqueConfiguration(8400, SolutionTechnique.FINNED_MUTANT_LEVIATHAN,
					PuzzleDifficulty.DIABOLICAL.ordinal(), SolutionCategory.FINNED_MUTANT_FISH, 470, false, false, 8400,
					false, false),
			new SolutionTechniqueConfiguration(8700, SolutionTechnique.TEMPLATE_SET,
					PuzzleDifficulty.DIABOLICAL.ordinal(), SolutionCategory.LAST_RESORT, 10000, false, false, 8700,
					false, false),
			new SolutionTechniqueConfiguration(8800, SolutionTechnique.TEMPLATE_DEL,
					PuzzleDifficulty.DIABOLICAL.ordinal(), SolutionCategory.LAST_RESORT, 10000, false, false, 8800,
					false, false),
			new SolutionTechniqueConfiguration(8500, SolutionTechnique.FORCING_CHAIN,
					PuzzleDifficulty.DIABOLICAL.ordinal(), SolutionCategory.LAST_RESORT, 500, true, false, 8500, false,
					false),
			new SolutionTechniqueConfiguration(8600, SolutionTechnique.FORCING_NET,
					PuzzleDifficulty.DIABOLICAL.ordinal(), SolutionCategory.LAST_RESORT, 700, true, false, 8600, false,
					false),
			new SolutionTechniqueConfiguration(8900, SolutionTechnique.BRUTE_FORCE,
					PuzzleDifficulty.DIABOLICAL.ordinal(), SolutionCategory.LAST_RESORT, 10000, true, false, 8900,
					false, false),
			new SolutionTechniqueConfiguration(5650, SolutionTechnique.GROUPED_NICE_LOOP,
					PuzzleDifficulty.VERY_HARD.ordinal(), SolutionCategory.CHAINS_AND_LOOPS, 300, true, true, 5650,
					false, false),
			new SolutionTechniqueConfiguration(3170, SolutionTechnique.EMPTY_RECTANGLE, PuzzleDifficulty.HARD.ordinal(),
					SolutionCategory.SINGLE_DIGIT_PATTERNS, 120, true, true, 3170, false, false),
			new SolutionTechniqueConfiguration(4010, SolutionTechnique.HIDDEN_RECTANGLE,
					PuzzleDifficulty.HARD.ordinal(), SolutionCategory.UNIQUENESS, 100, true, true, 4010, false, false),
			new SolutionTechniqueConfiguration(4020, SolutionTechnique.AVOIDABLE_RECTANGLE_1,
					PuzzleDifficulty.HARD.ordinal(), SolutionCategory.UNIQUENESS, 100, true, true, 4020, false, false),
			new SolutionTechniqueConfiguration(4030, SolutionTechnique.AVOIDABLE_RECTANGLE_2,
					PuzzleDifficulty.HARD.ordinal(), SolutionCategory.UNIQUENESS, 100, true, true, 4030, false, false),
			new SolutionTechniqueConfiguration(5330, SolutionTechnique.SIMPLE_COLORS, PuzzleDifficulty.HARD.ordinal(),
					SolutionCategory.COLORING, 150, true, true, 5330, false, false),
			new SolutionTechniqueConfiguration(5360, SolutionTechnique.MULTI_COLORS, PuzzleDifficulty.HARD.ordinal(),
					SolutionCategory.COLORING, 200, true, true, 5360, false, false),
			new SolutionTechniqueConfiguration(8450, SolutionTechnique.KRAKEN_FISH,
					PuzzleDifficulty.DIABOLICAL.ordinal(), SolutionCategory.LAST_RESORT, 500, false, false, 8450, false,
					false),
			new SolutionTechniqueConfiguration(3120, SolutionTechnique.TURBOT_FISH, PuzzleDifficulty.HARD.ordinal(),
					SolutionCategory.SINGLE_DIGIT_PATTERNS, 120, true, true, 3120, false, false),
			new SolutionTechniqueConfiguration(1210, SolutionTechnique.LOCKED_CANDIDATES_2,
					PuzzleDifficulty.MEDIUM.ordinal(), SolutionCategory.INTERSECTIONS, 50, true, true, 1210, true,
					false) };

	public int getActiveDifficultyLevel() {
		// TODO Auto-generated method stub
		return 0;
	}

}
