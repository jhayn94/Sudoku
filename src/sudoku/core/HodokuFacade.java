package sudoku.core;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import generator.BackgroundGenerator;
import solver.SudokuSolver;
import solver.SudokuSolverFactory;
import sudoku.DifficultyLevel;
import sudoku.GameMode;
import sudoku.Options;
import sudoku.SolutionStep;
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

	private static final Logger LOG = LogManager.getLogger(HodokuFacade.class);

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
		final String generatedSudokuString = generator.generate(Options.getInstance().getDifficultyLevel(ordinal + 1),
				GameMode.PLAYING);
		this.getSolutionForSudoku(generatedSudokuString);
		return generatedSudokuString;
	}

	/**
	 * Returns an order list of steps which can be used to solve the given puzzle.
	 * Note that this is not the only solution.
	 */
	public List<SolutionStep> getSolutionForSudoku(final String sudokuString) {
		final List<SolutionStep> solutionSteps = new ArrayList<>();
		final Sudoku2 tempSudoku = new Sudoku2();
		tempSudoku.setSudoku(sudokuString, true);
		final Sudoku2 solvedSudoku = tempSudoku.clone();
		final SudokuSolver solver = SudokuSolverFactory.getDefaultSolverInstance();
		solver.solve(Options.getInstance().getDifficultyLevel(5), solvedSudoku, true, false,
				Options.getInstance().solverSteps, Options.getInstance().getGameMode());
		tempSudoku.setLevel(solvedSudoku.getLevel());
		tempSudoku.setScore(solvedSudoku.getScore());

		final SudokuSolver sudokuSolver = new SudokuSolver();
		while (!tempSudoku.isSolved()) {
			final SolutionStep solutionStep = sudokuSolver.getHint(tempSudoku, false);
			LOG.debug(solutionStep);
			solutionSteps.add(solutionStep);
			sudokuSolver.doStep(tempSudoku, solutionStep);
		}
		LOG.info(tempSudoku.getLevel().getName());
		LOG.info(tempSudoku.getScore());
		return solutionSteps;
	}

	/** Returns the next solution step for the given puzzle. */
	public SolutionStep getHint(final SudokuPuzzleValues sudoku) {
		final Sudoku2 tempSudoku = new Sudoku2();
		final String sudokuString = sudoku.getStringRepresentation(false);
		tempSudoku.setSudoku(sudokuString, true);
		// Since the 81 digit string passed doesn't reflect user eliminated candidates,
		// they must be manually set individually.
		for (int row = 0; row < SudokuPuzzleValues.CELLS_PER_HOUSE; row++) {
			for (int col = 0; col < SudokuPuzzleValues.CELLS_PER_HOUSE; col++) {
				final List<Integer> candidateDigitsForCell = sudoku.getCandidateDigitsForCell(row, col);
				for (int candidate = 1; candidate <= SudokuPuzzleValues.CELLS_PER_HOUSE; candidate++) {
					tempSudoku.setCandidate(row, col, candidate, candidateDigitsForCell.contains(candidate));
				}
			}
		}
		final Sudoku2 solvedSudoku = tempSudoku.clone();
		final SudokuSolver solver = SudokuSolverFactory.getDefaultSolverInstance();
		final int ordinal = ApplicationSettings.getInstance().getDifficulty().ordinal();
		final DifficultyLevel difficultyLevel = Options.getInstance().getDifficultyLevel(ordinal + 1);
		solver.solve(difficultyLevel, solvedSudoku, true, false, Options.getInstance().solverSteps, GameMode.PLAYING);
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
}
