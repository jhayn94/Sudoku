package sudoku.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import generator.BackgroundGenerator;
import generator.SudokuGenerator;
import generator.SudokuGeneratorFactory;
import solver.SudokuSolver;
import solver.SudokuSolverFactory;
import sudoku.ClipboardMode;
import sudoku.DifficultyLevel;
import sudoku.GameMode;
import sudoku.Options;
import sudoku.SolutionStep;
import sudoku.StepConfig;
import sudoku.Sudoku2;
import sudoku.model.ApplicationSettings;
import sudoku.model.SudokuPuzzleValues;
import sudoku.view.util.Difficulty;

/**
 * This class contains various methods for accessing the APIs / components in
 * HoDoKu. In general, I found this code to be unmanageable due to file length,
 * bilingual comments, and nested control flow, so I decided not to port it, and
 * use it a .jar instead.
 *
 * In addition, this class offers a SPOC to the HoDoKu library.
 */
public class HodokuFacade {

	private static final Logger LOG = LogManager.getLogger(SudokuMain.class);

	private static HodokuFacade instance;

	public static HodokuFacade getInstance() {
		if (HodokuFacade.instance == null) {
			HodokuFacade.instance = new HodokuFacade();
		}
		return HodokuFacade.instance;
	}

	/**
	 * Generates a string which represents a sudoku puzzle with exactly 1 solution.
	 */
	public String generateSudokuString() {
		final BackgroundGenerator generator = new BackgroundGenerator();
		final int ordinal = ApplicationSettings.getInstance().getDifficulty().ordinal();
		String generatedSudokuString = generator.generate(Options.getInstance().getDifficultyLevel(ordinal + 1),
				GameMode.PLAYING);
		final String mustContainStepWithName = ApplicationSettings.getInstance().getMustContainStepWithName();
		if (!mustContainStepWithName.isEmpty()) {
			List<SolutionStep> solutionForSudoku = this.getSolutionForSudoku(generatedSudokuString);
			long matchingSteps = solutionForSudoku.stream()
					.filter(solutionStep -> solutionStep.getType().getStepName().equals(mustContainStepWithName)).count();
			while (matchingSteps == 0) {
				generatedSudokuString = generator.generate(Options.getInstance().getDifficultyLevel(ordinal + 1),
						GameMode.PLAYING);
				solutionForSudoku = this.getSolutionForSudoku(generatedSudokuString);
				matchingSteps = solutionForSudoku.stream()
						.filter(solutionStep -> solutionStep.getType().getStepName().equals(mustContainStepWithName)).count();
			}
			if (ApplicationSettings.getInstance().isSolveToRequiredStep()) {
				generatedSudokuString = this.solveSudokuUpToFirstInstanceOfStep(generatedSudokuString, mustContainStepWithName);
			}
		}
		return generatedSudokuString;
	}

	/**
	 * Returns an order list of steps which can be used to solve the given puzzle.
	 * Note that this is not the only solution. Also note that this method assumes
	 * no candidate eliminations have been made by the user.
	 */
	public List<SolutionStep> getSolutionForSudoku(final String sudokuString) {
		final List<SolutionStep> solutionSteps = new ArrayList<>();
		final Sudoku2 tempSudoku = new Sudoku2();
		tempSudoku.setSudoku(sudokuString, true);
		final Sudoku2 solvedSudoku = tempSudoku.clone();
		final SudokuSolver solver = SudokuSolverFactory.getDefaultSolverInstance();
		try {
			solver.solve(Options.getInstance().getDifficultyLevel(5), solvedSudoku, false, false,
					Options.getInstance().solverSteps, Options.getInstance().getGameMode());
		} catch (final Exception e) {
			LOG.error("{}", e);
			// Sometimes this method causes exceptions... just catch it and try a new
			// puzzle.
			return new ArrayList<>();
		}
		tempSudoku.setLevel(solvedSudoku.getLevel());
		tempSudoku.setScore(solvedSudoku.getScore());

		final SudokuSolver sudokuSolver = new SudokuSolver();
		while (!tempSudoku.isSolved()) {
			final SolutionStep solutionStep = sudokuSolver.getHint(tempSudoku, false);
			solutionSteps.add(solutionStep);
			sudokuSolver.doStep(tempSudoku, solutionStep);
		}
		return solutionSteps;
	}

	public boolean isPuzzleValid(final SudokuPuzzleValues sudoku) {
		final Sudoku2 tempSudoku = this.convertSudokuPuzzleValuesToSudoku2(sudoku, false);
		final SudokuGenerator generator = SudokuGeneratorFactory.getDefaultGeneratorInstance();
		return generator.getNumberOfSolutions(tempSudoku) == 1;
	}

	/** Returns the rating to finish solving the given puzzle. */
	public int getScoreForPuzzle(final SudokuPuzzleValues sudoku, final boolean onlyGivens) {
		final Sudoku2 tempSudoku = this.convertSudokuPuzzleValuesToSudoku2(sudoku, onlyGivens);
		final Sudoku2 solvedSudoku = tempSudoku.clone();
		final SudokuSolver solver = SudokuSolverFactory.getDefaultSolverInstance();
		solver.solve(Options.getInstance().getDifficultyLevel(5), solvedSudoku, false, false,
				Options.getInstance().solverSteps, Options.getInstance().getGameMode());
		return solvedSudoku.getScore();
	}

	/** Returns the rating to finish solving the given puzzle. */
	public Difficulty getDifficultyForPuzzle(final SudokuPuzzleValues sudoku, final boolean onlyGivens) {
		final Sudoku2 tempSudoku = this.convertSudokuPuzzleValuesToSudoku2(sudoku, onlyGivens);
		final Sudoku2 solvedSudoku = tempSudoku.clone();
		final SudokuSolver solver = SudokuSolverFactory.getDefaultSolverInstance();
		solver.solve(Options.getInstance().getDifficultyLevel(5), solvedSudoku, false, false,
				Options.getInstance().solverSteps, Options.getInstance().getGameMode());
		try {
			return Difficulty.getValidDifficulties().stream()
					.filter(difficulty -> difficulty.getInternalDifficulty().equals(solvedSudoku.getLevel().getType()))
					.findFirst().orElseThrow(NoSuchElementException::new);
		} catch (final Exception npe) {
			return Difficulty.INVALID;
		}
	}

	/**
	 * Solves the given sudoku string to just before where the first occurrence of
	 * the given step name could be used. Note that if the given step is never used,
	 * a solved puzzle will be returned.
	 */
	public String solveSudokuUpToFirstInstanceOfStep(final String sudokuString, final String stepName) {
		final Sudoku2 tempSudoku = new Sudoku2();
		tempSudoku.setSudoku(sudokuString, true);
		final Sudoku2 solvedSudoku = tempSudoku.clone();
		final SudokuSolver solver = SudokuSolverFactory.getDefaultSolverInstance();
		solver.solve(Options.getInstance().getDifficultyLevel(5), solvedSudoku, false, false,
				Options.getInstance().solverSteps, Options.getInstance().getGameMode());
		tempSudoku.setLevel(solvedSudoku.getLevel());
		tempSudoku.setScore(solvedSudoku.getScore());

		final SudokuSolver sudokuSolver = new SudokuSolver();
		while (!tempSudoku.isSolved()) {
			final SolutionStep solutionStep = sudokuSolver.getHint(tempSudoku, false);
			if (solutionStep.getType().getStepName().equals(stepName)) {
				return this.buildStringRepresentation(tempSudoku);
			}
			sudokuSolver.doStep(tempSudoku, solutionStep);
		}
		return this.buildStringRepresentation(tempSudoku);
	}

	private String buildStringRepresentation(final Sudoku2 tempSudoku) {
		final String baseResult = tempSudoku.getSudoku(ClipboardMode.VALUES_ONLY);
		final StringBuilder result = new StringBuilder(baseResult);
		for (int row = 0; row < SudokuPuzzleValues.CELLS_PER_HOUSE; row++) {
			for (int col = 0; col < SudokuPuzzleValues.CELLS_PER_HOUSE; col++) {
				final int linearIndex = row * SudokuPuzzleValues.CELLS_PER_HOUSE + col;
				if (tempSudoku.getCell(linearIndex) > 0) {
					result.append("[r" + row + "c" + col + "=" + tempSudoku.getCandidateString(linearIndex) + "]");
				}
			}
		}
		return result.toString();
	}

	/**
	 * Returns the next solution step for the given puzzle.
	 */
	public SolutionStep getHint(final SudokuPuzzleValues sudoku) {
		final Sudoku2 tempSudoku = this.convertSudokuPuzzleValuesToSudoku2(sudoku, false);
		final Sudoku2 solvedSudoku = tempSudoku.clone();
		final SudokuSolver solver = SudokuSolverFactory.getDefaultSolverInstance();
		final int ordinal = ApplicationSettings.getInstance().getDifficulty().ordinal();
		final DifficultyLevel difficultyLevel = Options.getInstance().getDifficultyLevel(ordinal + 1);
		solver.solve(difficultyLevel, solvedSudoku, false, false, Options.getInstance().solverSteps, GameMode.PLAYING);
		tempSudoku.setLevel(solvedSudoku.getLevel());
		tempSudoku.setScore(solvedSudoku.getScore());
		final SudokuSolver sudokuSolver = new SudokuSolver();
		return sudokuSolver.getHint(tempSudoku, false);
	}

	public void updateMaxScoreForDifficulty(final Difficulty difficultyToChange, final int maxScore) {
		final DifficultyLevel difficultyLevelToChange = Options.getInstance()
				.getDifficultyLevel(difficultyToChange.ordinal() + 1);
		difficultyLevelToChange.setMaxScore(maxScore);
	}

	public void setSolverConfig(final List<StepConfig> stepConfigs) {
		final StepConfig[] newStepConfigs = (StepConfig[]) stepConfigs.toArray();
		Options.getInstance().solverSteps = Options.getInstance().copyStepConfigs(newStepConfigs, false, true);
		Options.getInstance().adjustOrgSolverSteps();
	}

	public List<StepConfig> getSolverConfig() {
		return Arrays.asList(Options.getInstance().copyStepConfigs(Options.getInstance().solverSteps, true, false));
	}

	private Sudoku2 convertSudokuPuzzleValuesToSudoku2(final SudokuPuzzleValues sudoku, final boolean onlyGivens) {
		final Sudoku2 tempSudoku = new Sudoku2();
		final String sudokuString = sudoku.toString(onlyGivens);
		tempSudoku.setSudoku(sudokuString, true);
		// Only remove candidates if they're auto managed. Otherwise, the HoDoKu solver
		// thinks the puzzle is closer to being solved than it actually is due to lots
		// of candidate eliminations.
		if (!onlyGivens && ApplicationSettings.getInstance().isAutoManageCandidates()) {
			this.removeUserInputCandidateChanges(sudoku, tempSudoku);
		}
		return tempSudoku;
	}

	private void removeUserInputCandidateChanges(final SudokuPuzzleValues sudoku, final Sudoku2 tempSudoku) {
		for (int row = 0; row < SudokuPuzzleValues.CELLS_PER_HOUSE; row++) {
			for (int col = 0; col < SudokuPuzzleValues.CELLS_PER_HOUSE; col++) {
				final List<Integer> candidateDigitsForCell = sudoku.getCandidateDigitsForCell(row, col);
				for (int candidate = 1; candidate <= SudokuPuzzleValues.CELLS_PER_HOUSE; candidate++) {
					if (sudoku.getFixedCellDigit(row, col) != 0) {
						tempSudoku.setCandidate(row, col, candidate, false);
					} else {
						tempSudoku.setCandidate(row, col, candidate, candidateDigitsForCell.contains(candidate));
					}
				}
			}
		}
	}
}
