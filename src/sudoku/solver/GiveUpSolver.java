package sudoku.solver;

import sudoku.SolutionStep;
import sudoku.SolutionType;

/**
 *
 * @author hobiwan
 */
public class GiveUpSolver extends AbstractSolver {

	/**
	 * Creates a new instance of GiveUpSolver
	 *
	 * @param finder
	 */
	public GiveUpSolver(SudokuStepFinder finder) {
		super(finder);
	}

	@Override
	protected SolutionStep getStep(SolutionTechnique type) {
		if (type == SolutionTechnique.GIVE_UP) {
			return new SolutionStep(SolutionTechnique.GIVE_UP);
		}
		return null;
	}

	@Override
	protected boolean doStep(SolutionStep step) {
		boolean handled = false;
		switch (step.getType()) {
		case GIVE_UP:
			handled = true;
			break;
		default:
			handled = false;
		}
		return handled;
	}

}
