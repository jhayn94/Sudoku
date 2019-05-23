package sudoku.solver;

import sudoku.Sudoku2;
import sudoku.view.util.PuzzleSolutionStep;
import sudoku.view.util.SolutionTechnique;

/**
 *
 * @author hobiwan
 */
public abstract class AbstractSolver {
	/** The {@link SudokuStepFinder} to which this specialized solver belongs. */
	protected SudokuStepFinder finder;
	/** Every solver needs the sudoku... */
	protected Sudoku2 sudoku;

//    private SudokuSet tmpSet = new SudokuSet();

	/**
	 * Creates a new instance of AbstractSolver
	 *
	 * @param finder
	 */
	public AbstractSolver(SudokuStepFinder finder) {
		this.finder = finder;
	}

	/**
	 * Method for finding a new instance of a specific technique.
	 *
	 * @param type
	 * @return
	 */
	protected abstract PuzzleSolutionStep getStep(SolutionTechnique solutionTechnique);

	protected abstract boolean executeSolutionTechnique(SolutionTechnique solutionTechnique);

	/**
	 * This method is called in regular intervals to clean up data structures. If a
	 * solver wants to use this functionality, it has to override this method. If
	 * the method is overridden, special care has to be taken to synchronize
	 * correctly.
	 */
	protected void cleanUp() {
		// Nothing to do.
	}
}
