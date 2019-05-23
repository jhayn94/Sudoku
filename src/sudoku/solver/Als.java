package sudoku.solver;

import sudoku.SolutionStep;
import sudoku.Sudoku2;
import sudoku.SudokuSet;

/**
 *
 * @author hobiwan
 */
public class Als {

	/** All indices that belong to the ALS */
	public SudokuSet indices;
	/**
	 * All numbers that are contained in the ALS (only the numbers, not the actual
	 * candidates!)
	 */
	public short candidates;
	/**
	 * For every number contained in the ALS all cells containing that number as
	 * candidate
	 */
	public SudokuSet[] indicesPerCandidat = new SudokuSet[10];
	/**
	 * For every number contained in the ALS all cells outside the als that are
	 * buddies to all ALS cells holding that candidate
	 */
	public SudokuSet[] buddiesPerCandidat = new SudokuSet[10];
	/**
	 * Like {@link #buddiesPerCandidat} but including the ALS cells holding that
	 * candidate (for RC search).
	 */
	public SudokuSet[] buddiesAlsPerCandidat = new SudokuSet[10];
	/**
	 * All cells outside the als, that contain at least one candidate, that is a
	 * buddy to the ALS
	 */
	public SudokuSet buddies;
	/** The penalty for the ALS (used when calculating chain length) */
	public int chainPenalty = -1;

	/**
	 * Creates a new ALS.<br>
	 * <br>
	 * <b>Note:</b> An ALS created with this constructor cannot be used unless
	 * {@link #computeFields(solver.SudokuStepFinder) } has been called.
	 * 
	 * @param indices
	 * @param candidates
	 */
	public Als(SudokuSet indices, short candidates) {
		this.indices = new SudokuSet(indices);
		this.candidates = candidates;
	}

	/**
	 * Computes all the additional fields; is done after the initial search to
	 * optimize finding doubles.
	 * 
	 * @param finder
	 */
	public void computeFields(SudokuStepFinder finder) {
		this.buddies = new SudokuSet();
		for (int i = 1; i <= 9; i++) {
			if ((this.candidates & Sudoku2.MASKS[i]) != 0) {
				final SudokuSet sudokuCandidates = finder.getCandidates()[i];
				this.indicesPerCandidat[i] = new SudokuSet(this.indices);
				this.indicesPerCandidat[i].and(sudokuCandidates);
				this.buddiesPerCandidat[i] = new SudokuSet();
				Sudoku2.getBuddies(this.indicesPerCandidat[i], this.buddiesPerCandidat[i]);
				this.buddiesPerCandidat[i].andNot(this.indices);
				this.buddiesPerCandidat[i].and(finder.getCandidates()[i]);
				this.buddiesAlsPerCandidat[i] = new SudokuSet(this.buddiesPerCandidat[i]);
				this.buddiesAlsPerCandidat[i].or(this.indicesPerCandidat[i]);
				this.buddies.or(this.buddiesPerCandidat[i]);
			}
		}
	}

	/**
	 * ALS in chains count as one link. This prefers chains containing large ALS
	 * over slightly longer chains with smaller (or non at all) ALS. The penalty is
	 * added to the chain length to suppress that behaviour.
	 * 
	 * @param candSize Number of candidates in the ALS
	 * @return Number of links to be added to the chain size
	 */
	public static int getChainPenalty(int candSize) {
		// return 0;
		if (candSize == 0 || candSize == 1) {
			return 0;
		} else if (candSize == 2) {
			return candSize - 1;
		} else {
			return (candSize - 1) * 2;
		}
	}

	/**
	 * Returns the chain penalty of the ALS (see {@link #getChainPenalty(int)}).
	 * 
	 * @return Number of links to be added to the chain size
	 */
	public int getChainPenalty() {
		if (this.chainPenalty == -1) {
			this.chainPenalty = getChainPenalty(Sudoku2.ANZ_VALUES[this.candidates]);
		}
		return this.chainPenalty;
	}

	/**
	 * Two ALS are equal if they contain the same indices
	 * 
	 * @param o
	 * @return
	 */
	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (!(o instanceof Als)) {
			return false;
		}
		final Als a = (Als) o;
		return this.indices.equals(a.indices);
	}

	/**
	 * Fitting for {@link #equals(java.lang.Object) }.
	 * 
	 * @return
	 */
	@Override
	public int hashCode() {
		int hash = 7;
		hash = 71 * hash + (this.indices != null ? this.indices.hashCode() : 0);
		return hash;
	}

	@Override
	public String toString() {
		// return "ALS: " + candidates.toString() + " - " + indices.toString();
		return "ALS: " + SolutionStep.getAls(this);
	}

}
