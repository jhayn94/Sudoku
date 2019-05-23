package sudoku.view.util;

/**
 * This class represents the relative difficulty of a puzzle. This is based on
 * the complexity of moves required to solve it.
 */
public enum PuzzleDifficulty {

	EASY(800), MEDIUM(1000), HARD(1600), VERY_HARD(1800), DIABOLICAL(Integer.MAX_VALUE), INVALID(0);

	public static PuzzleDifficulty fromOrdinal(int ordinal) {
		if (ordinal == 0) {
			return EASY;
		} else if (ordinal == 1) {
			return MEDIUM;
		} else if (ordinal == 2) {
			return HARD;
		} else if (ordinal == 3) {
			return VERY_HARD;
		} else if (ordinal == 4) {
			return DIABOLICAL;
		}
		return INVALID;
	}

	private int maxScore;

	private PuzzleDifficulty(int maxScore) {
		this.maxScore = maxScore;
	}

	public int getMaxScore() {
		return this.maxScore;
	}

}
