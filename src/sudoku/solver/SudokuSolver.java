package sudoku.solver;

import java.util.ArrayList;
import java.util.List;

import sudoku.core.SolverConfiguration;
import sudoku.model.SudokuPuzzle;
import sudoku.view.util.PuzzleDifficulty;
import sudoku.view.util.PuzzleSolutionStep;
import sudoku.view.util.PuzzleType;
import sudoku.view.util.SolutionCategory;
import sudoku.view.util.SolutionTechnique;
import sudoku.view.util.SolutionTechniqueConfiguration;

/** This class contains method to solve a sudoku puzzle. */
public class SudokuSolver {

	private final SudokuStepFinder stepFinder;

	private SudokuPuzzle sudoku;

	private List<PuzzleSolutionStep> steps;

	// can be freely changed
	private final List<PuzzleSolutionStep> tmpSteps;

	private PuzzleDifficulty level;

	private PuzzleDifficulty maxLevel;

	private int score;

	private int[] anzSteps;

	private final int[] anzStepsProgress;

	private final long[] stepsNanoTime;

	/** Creates a new instance of SudokuSolver */
	public SudokuSolver() {
		this.stepFinder = new SudokuStepFinder();
		this.steps = new ArrayList<>();
		this.tmpSteps = new ArrayList<>();
		this.level = PuzzleDifficulty.DIABOLICAL;
		this.maxLevel = PuzzleDifficulty.DIABOLICAL;
		final int numSolverSteps = SolverConfiguration.getInstance().getSolverSteps().length;
		this.anzSteps = new int[numSolverSteps];
		this.anzStepsProgress = new int[numSolverSteps];
		this.stepsNanoTime = new long[numSolverSteps];
	}

	/**
	 * If the time to solve the sudoku exceeds a certain limit (2s), a progress
	 * dialog is displayed. The dialog is created anyway, it starts the solver in a
	 * seperate thread. If the thread does not complete in a given time,
	 * setVisible(true) is called.
	 *
	 * @param withGui
	 * @return
	 */
	public boolean solve() {
		return this.solve(PuzzleDifficulty.DIABOLICAL, null, false, false);
	}

	/**
	 * Tries to solve the sudoku using only singles.<br>
	 * The internal variables are not changed
	 *
	 * @param newSudoku
	 * @return
	 */
	public boolean solveWithSinglesOnly(SudokuPuzzle newSudoku) {
		final SudokuPuzzle tmpSudoku = this.sudoku;
		final List<PuzzleSolutionStep> oldList = this.steps;
		this.setSudoku(newSudoku);
		this.steps = this.tmpSteps;
		SudokuSolverUtils.clearStepListWithNullify(this.steps);
		final boolean solved = this.solve(PuzzleDifficulty.DIABOLICAL, null, false, true);
		this.steps = oldList;
		this.setSudoku(tmpSudoku);
		return solved;
	}

	public boolean solveWithSteps(SudokuPuzzle newSudoku,
			SolutionTechniqueConfiguration[] solutionTechniqueConfigurations) {
		final SudokuPuzzle tmpSudoku = this.sudoku;
		final List<PuzzleSolutionStep> oldList = this.steps;
		this.setSudoku(newSudoku);
		this.steps = this.tmpSteps;
		SudokuSolverUtils.clearStepListWithNullify(this.steps);
		final boolean solved = this.solve(PuzzleDifficulty.DIABOLICAL, null, false, false,
				solutionTechniqueConfigurations, PuzzleType.STANDARD);
		this.steps = oldList;
		this.setSudoku(tmpSudoku);
		return solved;
	}

	/**
	 * Solves a sudoku using all available techniques.
	 *
	 */
	public boolean solve(PuzzleDifficulty maxLevel, SudokuPuzzle tempSudoku, boolean shouldRejectTooLowScore,
			boolean singlesOnly) {
		return this.solve(maxLevel, tempSudoku, shouldRejectTooLowScore, singlesOnly,
				SolverConfiguration.getInstance().getSolverSteps(), PuzzleType.STANDARD);
	}

	/**
	 * The main real solver method. Can reject a possible solution if the difficulty
	 * doesnt match or if the score of the sudoku is too low. If
	 * <code>SolutionTechniqueConfiguration</code> is
	 * {@link Options#solverStepsProgress}, the method can be used to measure
	 * progress or find backdoors.<br>
	 * If the <code>gameMode</code> is any other than <code>PLAYING</code>, any
	 * puzzle is accepted, that contains at least one step with
	 * <code>SolutionTechniqueConfiguration.isEnabledTraining()</code> true.
	 *
	 */
	public boolean solve(PuzzleDifficulty maxLevel, SudokuPuzzle tempSudoku, boolean shouldRejectTooLowScore,
			boolean singlesOnly, SolutionTechniqueConfiguration[] solutionTechniqueConfigurations,
			PuzzleType puzzleType) {
		if (tempSudoku != null) {
			this.setSudoku(tempSudoku);
		}
		final int anzCells = this.sudoku.getNumberOfUnsolvedCells();
		if ((81 - anzCells) < 10) {
			return false;
		}

		this.maxLevel = maxLevel;
		this.score = 0;
		this.level = PuzzleDifficulty.EASY;

		PuzzleSolutionStep step = null;

		for (int i = 0; i < this.anzSteps.length; i++) {
			this.anzSteps[i] = 0;
		}

		boolean acceptAnyway = false;

		do {
			step = this.getHint(singlesOnly, solutionTechniqueConfigurations, acceptAnyway);
			if (step != null) {
				if (puzzleType != PuzzleType.STANDARD && step.getTechnique().getStepConfig().isEnabledTraining()) {
					acceptAnyway = true;
				}
				this.steps.add(step);
				this.getStepFinder().doStep(step);
				if (step.getTechnique() == SolutionTechnique.GIVE_UP) {
					step = null;
				}
			}
		} while (step != null);
		while (this.score > this.level.getMaxScore()) {
			this.level = PuzzleDifficulty.fromOrdinal(this.level.ordinal() + 1);
		}
		if (this.level.ordinal() > maxLevel.ordinal() && acceptAnyway == false) {
			return false;
		}
		if (shouldRejectTooLowScore && this.level.ordinal() > PuzzleDifficulty.EASY.ordinal()
				&& acceptAnyway == false) {
			if (this.score < PuzzleDifficulty.fromOrdinal(this.level.ordinal() - 1).getMaxScore()) {
				return false;
			}
		}
		this.sudoku.setScore(this.score);
		if (this.sudoku.isSolved()) {
			this.sudoku.setLevel(this.level);
			return true;
		} else {
			this.sudoku.setLevel(PuzzleDifficulty.DIABOLICAL);
			return false;
		}
	}

	/**
	 * Get the next logical step for a given sudoku. If <code>singlesOnly</code> is
	 * set, only singles are tried.<br>
	 * The current state of the solver instance is saved and restored after the
	 * search is complete.
	 *
	 */
	public PuzzleSolutionStep getHint(SudokuPuzzle sudoku, boolean singlesOnly) {
		final SudokuPuzzle save = this.sudoku;
		final PuzzleDifficulty oldMaxLevel = this.maxLevel;
		final PuzzleDifficulty oldLevel = this.level;
		this.maxLevel = PuzzleDifficulty.DIABOLICAL;
		this.level = PuzzleDifficulty.EASY;
		this.setSudoku(sudoku);
		final PuzzleSolutionStep step = this.getHint(singlesOnly);
		this.maxLevel = oldMaxLevel;
		this.level = oldLevel;
		this.setSudoku(save);
		return step;
	}

	/**
	 * Get the next logical step for the internal sudoku. If singlesOnly is set,
	 * only singles are tried.
	 *
	 */
	private PuzzleSolutionStep getHint(boolean singlesOnly) {
		return this.getHint(singlesOnly, SolverConfiguration.getInstance().getSolverSteps(), false);
	}

	/**
	 * Get the next logical step for the internal sudoku. If singlesOnly is set,
	 * only singles are tried. Since the steps are passed as argument this method
	 * can be used to calculate the next step and to calculate the progress measure
	 * for a given sudoku state. Any step is accepted, if the PuzzleType is not
	 * PuzzleType.STANDARD and one of the training techniques is already in the
	 * solution.
	 */
	private PuzzleSolutionStep getHint(boolean singlesOnly, SolutionTechniqueConfiguration[] solverSteps,
			boolean acceptAnyway) {
		if (this.sudoku.isSolved()) {
			return null;
		}
		PuzzleSolutionStep hint = null;

		for (int i = 0; i < solverSteps.length; i++) {
			if (solverSteps == SolverConfiguration.getInstance().getSolverStepsProgress()) {
				if (solverSteps[i].isEnabledProgress() == false) {
					continue;
				}
			} else {
				if (solverSteps[i].isEnabled() == false) {
					continue;
				}
			}
			final SolutionTechnique type = solverSteps[i].getTechnique();
			if (singlesOnly && (type != SolutionTechnique.HIDDEN_SINGLE && type != SolutionTechnique.NAKED_SINGLE
					&& type != SolutionTechnique.FULL_HOUSE)) {
				continue;
			}
			hint = this.getStepFinder().getTechnique(type);
			this.anzStepsProgress[i]++;
			if (hint != null) {
				this.anzSteps[i]++;
				this.score += solverSteps[i].getBaseScore();
				if (solverSteps[i].getLevel() > this.level.ordinal()) {
					this.level = PuzzleDifficulty.fromOrdinal(solverSteps[i].getLevel());
				}
				if (!acceptAnyway) {
					if (this.level.ordinal() > this.maxLevel.ordinal() || this.score >= this.maxLevel.getMaxScore()) {
						return null;
					}
				}
				return hint;
			}
		}
		return null;
	}

	public void doStep(SudokuPuzzle sudoku, PuzzleSolutionStep step) {
		// we mustnt call setSudoku() here or all internal
		// data structures get changed -> just set the field itself
		final SudokuPuzzle oldSudoku = this.getSudoku();
		// setSudoku(sudoku);
		this.getStepFinder().setSudoku(sudoku);
		this.getStepFinder().doStep(step);
		// setSudoku(oldSudoku);
		this.getStepFinder().setSudoku(oldSudoku);
	}

	public SudokuPuzzle getSudoku() {
		return this.sudoku;
	}

	public void setSudoku(SudokuPuzzle sudoku, List<PuzzleSolutionStep> partSteps) {
		// not really sure whether the list may be cleared savely here...
		// SudokuUtil.clearStepList(steps);
		this.steps = new ArrayList<PuzzleSolutionStep>();
		for (int i = 0; i < partSteps.size(); i++) {
			this.steps.add(partSteps.get(i));
		}
		this.sudoku = sudoku;
		this.getStepFinder().setSudoku(sudoku);
	}

	public void setSudoku(SudokuPuzzle sudoku) {
		SudokuSolverUtils.clearStepList(this.steps);
		for (int i = 0; i < this.anzSteps.length; i++) {
			this.anzSteps[i] = 0;
		}
		this.sudoku = sudoku;
		this.getStepFinder().setSudoku(sudoku);
	}

	public List<PuzzleSolutionStep> getSteps() {
		return this.steps;
	}

	public int getAnzUsedSteps() {
		int anz = 0;
		for (int i = 0; i < this.anzSteps.length; i++) {
			if (this.anzSteps[i] > 0) {
				anz++;
			}
		}
		return anz;
	}

	public int[] getAnzSteps() {
		return this.anzSteps;
	}

	public int getScore() {
		return this.score;
	}

	public PuzzleDifficulty getLevel() {
		return this.level;
	}

	public SolutionCategory getCategory(SolutionTechnique type) {
		for (final SolutionTechniqueConfiguration configStep : SolverConfiguration.getInstance().getSolverSteps()) {
			if (type == configStep.getTechnique()) {
				return configStep.getCategory();
			}
		}
		return null;
	}

	public String getCategoryName(SolutionTechnique type) {
		final SolutionCategory cat = this.getCategory(type);
		if (cat == null) {
			return null;
		}
		return cat.getCategoryName();
	}

	public void setSteps(List<PuzzleSolutionStep> steps) {
		this.steps = steps;
	}

	public void setLevel(PuzzleDifficulty level) {
		this.level = level;
	}

	public PuzzleDifficulty getMaxLevel() {
		return this.maxLevel;
	}

	public void setMaxLevel(PuzzleDifficulty maxLevel) {
		this.maxLevel = maxLevel;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public void setAnzSteps(int[] anzSteps) {
		this.anzSteps = anzSteps;
	}

	public SudokuStepFinder getStepFinder() {
		return this.stepFinder;
	}
}
