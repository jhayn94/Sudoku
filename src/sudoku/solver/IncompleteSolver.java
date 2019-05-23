package sudoku.solver;

import sudoku.SolutionStep;
import sudoku.SolutionType;

/**
 *
 * @author hobiwan
 */
public class IncompleteSolver extends AbstractSolver {

	/**
	 * Creates a new instance of IncompleteSolver
	 * 
	 * @param finder
	 */
	public IncompleteSolver(SudokuStepFinder finder) {
		super(finder);
	}

	@Override
	protected SolutionStep getStep(SolutionTechnique type) {
		if (type == SolutionTechnique.INCOMPLETE) {
			return null;
		}
		return null;
	}

	@Override
	protected boolean doStep(SolutionStep step) {
		boolean handled = false;
		switch (step.getType()) {
		case INCOMPLETE:
			handled = true;
			break;
		default:
			handled = false;
		}
		return handled;
	}

}
