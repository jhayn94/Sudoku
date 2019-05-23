package sudoku.generator;

import java.util.List;

import sudoku.core.SolverConfiguration;
import sudoku.model.SudokuPuzzle;
import sudoku.solver.SudokuSolver;
import sudoku.solver.SudokuSolverFactory;
import sudoku.view.util.PuzzleDifficulty;
import sudoku.view.util.PuzzleSolutionStep;
import sudoku.view.util.PuzzleType;

/**
 * A BackgroundGenerator generates sudokus with a given {@link DifficultyLevel}
 * and for a given {@link GameMode}. An instance of this class can be contained
 * within a {@link BackgroundGeneratorThread} or within a
 * {@link GenerateSudokuProgressDialog}.<br>
 * If it is called from a {@link GenerateSudokuProgressDialog}, it uses the
 * default solver and reports the progress to the dialog. If a puzzle has been
 * found, the dialog is closed. The creation process can be aborted at any
 * time.<br>
 * If it is called from a {@link BackgroundGeneratorThread}, it simply delivers
 * the generated puzzle or <code>null</code>, if no puzzle could be found.
 *
 */
public class BackgroundGenerator {
	/**
	 * Maximal number of tries, when called from a
	 * {@link BackgroundGeneratorThread}.
	 */
	private static final int MAX_TRIES = 20000;
	/**
	 * Current number of tries when called from
	 * {@link GenerateSudokuProgressDialog}.
	 */
	private final int anz = 0;

	/**
	 * Generates a new instance.
	 */
	public BackgroundGenerator() {
		// Nothing to do.
	}

	public String generateNewPuzzle(PuzzleDifficulty puzzleDifficulty, PuzzleType puzzleType) {
		SudokuPuzzle sudoku = null;
		final SudokuGenerator creator = SudokuGeneratorFactory.getDefaultGeneratorInstance();
		final SudokuSolver solver = SudokuSolverFactory.getDefaultSolverInstance();
		while (!Thread.currentThread().isInterrupted()) {
			sudoku = creator.generateSudoku(true);
			if (sudoku == null) {
				// If it is impossible to create a sudoku.
				return null;
			}
			final SudokuPuzzle solvedSudoku = sudoku.clone();
			final boolean isPuzzleValid = solver.solve(puzzleDifficulty, solvedSudoku, true, false,
					SolverConfiguration.getInstance().getSolverSteps(), puzzleType);
			boolean containsTrainingStep = true;
			if (puzzleType != PuzzleType.STANDARD) {
				containsTrainingStep = false;
				final List<PuzzleSolutionStep> steps = solver.getSteps();
				for (final PuzzleSolutionStep step : steps) {
					if (step.getTechnique().getStepConfig().isEnabledTraining()) {
						containsTrainingStep = true;
						break;
					}
				}
			}
			if (isPuzzleValid && containsTrainingStep
					&& (solvedSudoku.getDifficulty().ordinal() == puzzleDifficulty.ordinal()
							|| puzzleType == PuzzleType.SOLVED_UP_TO_TACTIC)) {
				sudoku.setLevel(solvedSudoku.getDifficulty());
				sudoku.setScore(solvedSudoku.getScore());
				break;
			}
		}
		SudokuGeneratorFactory.yield(creator);
		SudokuSolverFactory.giveBack(solver);
		if (sudoku != null) {
			return sudoku.getSudoku();
		}
		return null;
	}

}
