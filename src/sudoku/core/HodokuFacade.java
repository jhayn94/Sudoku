package sudoku.core;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import generator.BackgroundGenerator;
import solver.SudokuSolver;
import solver.SudokuSolverFactory;
import sudoku.GameMode;
import sudoku.Options;
import sudoku.SolutionStep;
import sudoku.Sudoku2;

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
		return generator.generate(Options.getInstance().getDifficultyLevel(5), GameMode.PLAYING);
	}

	/**
	 * Returns an order list of steps which can be used to solve the given puzzle.
	 * Note that this is not the only solution.
	 */
	public List<SolutionStep> getSolutionForSudoku(final String sudokuString) {
		// TODO - do we want our own class for this as a wrapper?
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

		LOG.debug(tempSudoku.getLevel().getName());
		LOG.debug(tempSudoku.getScore());
		return solutionSteps;
	}

}
