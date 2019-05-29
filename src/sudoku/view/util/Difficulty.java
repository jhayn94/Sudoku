package sudoku.view.util;

import sudoku.DifficultyType;

/**
 * This class contains represents the perceived difficulty of something in the
 * game. This can apply to a puzzle as a whole, or just a specific solution
 * step. In general, the summation of the solution steps is the difficulty of
 * the puzzle. See the user manual for more on this.
 */
public enum Difficulty {

	EASY(LabelConstants.EASY, DifficultyType.EASY),
	MEDIUM(LabelConstants.MEDIUM, DifficultyType.MEDIUM),
	HARD(LabelConstants.HARD, DifficultyType.HARD),
	VERY_HARD(LabelConstants.VERY_HARD, DifficultyType.UNFAIR),
	DIABOLICAL(LabelConstants.DIABOLICAL, DifficultyType.EXTREME);

	private final DifficultyType internalDifficulty;

	private final String label;

	// This class essentially serves as a wrapper for Hodoku's difficulty settings
	// to minimize library references.
	private Difficulty(final String label, final DifficultyType hodokuDifficulty) {
		this.label = label;
		this.internalDifficulty = hodokuDifficulty;
	}

	public DifficultyType getInternalDifficulty() {
		return this.internalDifficulty;
	}

	public String getLabel() {
		return this.label;
	}
}
