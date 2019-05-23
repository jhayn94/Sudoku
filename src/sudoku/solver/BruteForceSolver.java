package sudoku.solver;

import sudoku.SolutionStep;
import sudoku.SolutionType;
import sudoku.Sudoku2;
import sudoku.SudokuSet;
import sudoku.generator.SudokuGeneratorFactory;

public class BruteForceSolver extends AbstractSolver {

	/**
	 * Creates a new instance of BruteForceSolver
	 *
	 * @param finder
	 */
	public BruteForceSolver(SudokuStepFinder finder) {
		super(finder);
	}

	@Override
	protected SolutionStep getStep(SolutionTechnique type) {
		SolutionStep result = null;
		this.sudoku = this.finder.getSudoku();
		switch (type) {
		case BRUTE_FORCE:
			result = this.getBruteForce();
			break;
		}
		return result;
	}

	@Override
	protected boolean doStep(SolutionStep step) {
		boolean handled = true;
		this.sudoku = this.finder.getSudoku();
		switch (step.getType()) {
		case BRUTE_FORCE:
			final int value = step.getValues().get(0);
			for (final int index : step.getIndices()) {
				this.sudoku.setCell(index, value);
			}
			break;
		default:
			handled = false;
		}
		return handled;
	}

	/**
	 * Das Sudoku2 wird mit Dancing-Links gelöst. Anschließend wird aus den nicht
	 * gesetzten Zellen die mittlere ausgesucht und gesetzt.<br>
	 * If the sudoku is invalid, no result is returned.
	 */
	private SolutionStep getBruteForce() {
//        System.out.println("Brute Force: " + Arrays.toString(sudoku.getValues()));
		if (!this.sudoku.isSolutionSet()) {
			// can happen, when command line mode is used (no brute force solving is done)
			// sets the solution in the sudoku
//            System.out.println("   no solution set");
			final boolean isValid = SudokuGeneratorFactory.getDefaultGeneratorInstance().validSolution(this.sudoku);
			if (!isValid) {
				return null;
			}
		}

		// alle Positionen ermitteln, die im ungelösten Sudoku2 noch nicht gesetzt sind
		final SudokuSet unsolved = new SudokuSet();
		for (int i = 0; i < Sudoku2.LENGTH; i++) {
			if (this.sudoku.getValue(i) == 0) {
//                System.out.println("   adding: " + i);
				unsolved.add(i);
			}
		}

		// jetzt die mittlere Zelle aussuchen
		int index = unsolved.size() / 2;
//        System.out.println("   index = " + index);
		index = unsolved.get(index);

		// Step zusammenbauen
		final SolutionStep step = new SolutionStep(SolutionTechnique.BRUTE_FORCE);
		step.addIndex(index);
		step.addValue(this.sudoku.getSolution(index));

		return step;
	}
}
