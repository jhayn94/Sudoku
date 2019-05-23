package sudoku.solver;

import java.util.ArrayList;
import java.util.List;

import sudoku.Candidate;
import sudoku.Sudoku2;
import sudoku.model.SudokuSet;
import sudoku.view.util.PuzzleSolutionStep;
import sudoku.view.util.SolutionTechnique;

public class WingSolver extends AbstractSolver {

	/** One global step for eliminations */
	private final PuzzleSolutionStep globalStep = new PuzzleSolutionStep(SolutionTechnique.FULL_HOUSE);
	/** A list for all steps found in one search */
	private List<PuzzleSolutionStep> steps = new ArrayList<PuzzleSolutionStep>();
	/** A set for elimination checks */
	private final SudokuSet preCalcSet1 = new SudokuSet();
	/** A set for elimination checks */
	private final SudokuSet preCalcSet2 = new SudokuSet();
	/** A set for elimination checks */
	private final SudokuSet elimSet = new SudokuSet();
	/** The indices of all bivalue cells in the current sudoku (for XY-Wing) */
	private final int[] biCells = new int[Sudoku2.LENGTH];
	/** The indices of all trivalue cells in the current sudoku (for XYZ-Wing) */
	private final int[] triCells = new int[Sudoku2.LENGTH];
	/** The first index of the strong link for W-Wings */
	private int wIndex1 = -1;
	/** The second index of the strong link for W-Wings */
	private int wIndex2 = -1;

	/**
	 * Creates a new instance of WingSolver
	 *
	 * @param finder
	 */
	public WingSolver(SudokuStepFinder finder) {
		super(finder);
	}

	@Override
	protected PuzzleSolutionStep getStep(SolutionTechnique type) {
		PuzzleSolutionStep result = null;
		this.sudoku = this.finder.getSudoku();
		switch (type) {
		case XY_WING:
			result = this.getXYWing();
			break;
		case XYZ_WING:
			result = this.getXYZWing();
			break;
		case W_WING:
			result = this.getWWing(true);
			break;
		}
		return result;
	}

	@Override
	protected boolean doStep(PuzzleSolutionStep step) {
		boolean handled = true;
		this.sudoku = this.finder.getSudoku();
		switch (step.getTechnique()) {
		case XY_WING:
		case W_WING:
		case XYZ_WING:
			for (final Candidate cand : step.getCandidatesToDelete()) {
				this.sudoku.delCandidate(cand.getIndex(), cand.getValue());
			}
			break;
		default:
			handled = false;
		}
		return handled;
	}

	private PuzzleSolutionStep getXYWing() {
		return this.getWing(false, true);
	}

	private PuzzleSolutionStep getXYZWing() {
		return this.getWing(true, true);
	}

	protected List<PuzzleSolutionStep> getAllWings() {
		this.sudoku = this.finder.getSudoku();
		final List<PuzzleSolutionStep> newSteps = new ArrayList<PuzzleSolutionStep>();
		final List<PuzzleSolutionStep> oldSteps = this.steps;
		this.steps = newSteps;

		this.getWing(true, false);
		this.getWing(false, false);
		this.getWWing(false);

		this.steps = oldSteps;
		return newSteps;
	}

	/**
	 * Try all combinations of three bivalue cells (for xyz: one trivalue and two
	 * bivalue cells). The following restrictions are in place:
	 * <ul>
	 * <li>The three cells must have exactly three candidates together</li>
	 * <li>The first cell (pivot) must see both other cells (pincers)</li>
	 * <li>The pincers must have exactly one candidate that is the same (candidate
	 * z)</li>
	 * <li>z can be excluded from all cells that see both pincers (for xyz they must
	 * see the pivot as well)</li>
	 * </ul>
	 *
	 */
	private PuzzleSolutionStep getWing(boolean xyz, boolean onlyOne) {
		// first get all bivalue/trivalue cells
		int biValueCount = 0;
		int triValueCount = 0;
		for (int i = 0; i < Sudoku2.LENGTH; i++) {
			if (this.sudoku.getAnzCandidates(i) == 2) {
				this.biCells[biValueCount++] = i;
			}
			if (xyz && this.sudoku.getAnzCandidates(i) == 3) {
				this.triCells[triValueCount++] = i;
			}
		}
		// now iterate them; use local variables to cover xy and xyz
		final int endIndex = xyz ? triValueCount : biValueCount;
		final int[] biTri = xyz ? this.triCells : this.biCells;
		// we check all combinations of bivalue cells (one tri + 2 bi for xyz)
		for (int i = 0; i < endIndex; i++) {
			for (int j = xyz ? 0 : i + 1; j < biValueCount; j++) {
				// any given combination of two cells must give exactly three
				// candidates; if that is not the case, skip it right away
				if (Sudoku2.ANZ_VALUES[this.sudoku.getCell(biTri[i]) | this.sudoku.getCell(this.biCells[j])] != 3) {
					// cant become a wing
					continue;
				}
				for (int k = j + 1; k < biValueCount; k++) {
					int index1 = biTri[i];
					int index2 = this.biCells[j];
					int index3 = this.biCells[k];
					int cell1 = this.sudoku.getCell(index1);
					int cell2 = this.sudoku.getCell(index2);
					int cell3 = this.sudoku.getCell(index3);
					// all three cells combined must have exactly three candidates
					if (Sudoku2.ANZ_VALUES[cell1 | cell2 | cell3] != 3) {
						// incorrect number of candidates
						continue;
					}
					// none of the cells may be equal
					if (cell1 == cell2 || cell2 == cell3 || cell3 == cell1) {
						// cant be a wing
						continue;
					}
					// three possibilities for XY-Wing: each cell could be the pincer
					// XYZ-Wing exits the loop after the first iteration
					final int maxTries = xyz ? 1 : 3;
					for (int tries = 0; tries < maxTries; tries++) {
						// swap cells accordingly
						if (tries == 1) {
							index1 = this.biCells[j];
							index2 = biTri[i];
							cell1 = this.sudoku.getCell(index1);
							cell2 = this.sudoku.getCell(index2);
						} else if (tries == 2) {
							index1 = this.biCells[k];
							index2 = this.biCells[j];
							index3 = biTri[i];
							cell1 = this.sudoku.getCell(index1);
							cell2 = this.sudoku.getCell(index2);
							cell3 = this.sudoku.getCell(index3);
						}
						// the pivot must see the pincers
						if (!Sudoku2.buddies[index1].contains(index2) || !Sudoku2.buddies[index1].contains(index3)) {
							// doesnt see them -> try another
							continue;
						}
						// the pincers must have exactly one candidate that is the same in both cells
						final short cell = (short) (cell2 & cell3);
						if (Sudoku2.ANZ_VALUES[cell] != 1) {
							// no wing, sorry
							continue;
						}
						final int candZ = Sudoku2.CAND_FROM_MASK[cell];
						// are there candidates that can see the pincers?
						this.elimSet.setAnd(Sudoku2.buddies[index2], Sudoku2.buddies[index3]);
						this.elimSet.and(this.finder.getCandidates()[candZ]);
						if (xyz) {
							// the pivot as well
							this.elimSet.and(Sudoku2.buddies[index1]);
						}
						if (this.elimSet.isEmpty()) {
							// no candidates to delete
							continue;
						}
						// ok, wing found!
						this.globalStep.reset();
						if (xyz) {
							this.globalStep.setType(SolutionTechnique.XYZ_WING);
						} else {
							this.globalStep.setType(SolutionTechnique.XY_WING);
						}
						final int[] cands = this.sudoku.getAllCandidates(index1);
						this.globalStep.addValue(cands[0]);
						this.globalStep.addValue(cands[1]);
						if (xyz) {
							this.globalStep.addValue(cands[2]);
						} else {
							this.globalStep.addValue(candZ);
						}
						this.globalStep.addIndex(index1);
						this.globalStep.addIndex(index2);
						this.globalStep.addIndex(index3);
						if (xyz) {
							this.globalStep.addFin(index1, candZ);
						}
						this.globalStep.addFin(index2, candZ);
						this.globalStep.addFin(index3, candZ);
						for (int l = 0; l < this.elimSet.size(); l++) {
							this.globalStep.addCandidateToDelete(this.elimSet.get(l), candZ);
						}
						final PuzzleSolutionStep step = (PuzzleSolutionStep) this.globalStep.clone();
						if (onlyOne) {
							return step;
						} else {
							this.steps.add(step);
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * Searches for W-Wings: look for all combinations of bivalue cells with the
	 * same candidates. If one is found and it could theoretically eliminate
	 * something, a connecting strong link is searched for.
	 *
	 * @param onlyOne
	 * @return
	 */
	private PuzzleSolutionStep getWWing(boolean onlyOne) {
		for (int i = 0; i < this.sudoku.getCells().length; i++) {
			if (this.sudoku.getValue(i) != 0 || this.sudoku.getAnzCandidates(i) != 2) {
				continue;
			}
			// bivalue cell found
			final short cell1 = this.sudoku.getCell(i);
			final int cand1 = this.sudoku.getAllCandidates(i)[0];
			final int cand2 = this.sudoku.getAllCandidates(i)[1];
			// prepare for elimination checks
			this.preCalcSet1.setAnd(Sudoku2.buddies[i], this.finder.getCandidates()[cand1]);
			this.preCalcSet2.setAnd(Sudoku2.buddies[i], this.finder.getCandidates()[cand2]);
			// check all other cells
			for (int j = i + 1; j < this.sudoku.getCells().length; j++) {
				if (this.sudoku.getCell(j) != cell1) {
					// doesnt fit!
					continue;
				}
				// ok, we have a pair; can anything be eliminated?
				this.elimSet.setAnd(this.preCalcSet1, Sudoku2.buddies[j]);
				if (!this.elimSet.isEmpty()) {
					// check for W-Wing for candidate cand1
					final PuzzleSolutionStep step = this.checkLink(cand1, cand2, i, j, this.elimSet, onlyOne);
					if (onlyOne && step != null) {
						return step;
					}
				}
				this.elimSet.setAnd(this.preCalcSet2, Sudoku2.buddies[j]);
				if (!this.elimSet.isEmpty()) {
					// check for W-Wing for candidate cand2
					final PuzzleSolutionStep step = this.checkLink(cand2, cand1, i, j, this.elimSet, onlyOne);
					if (onlyOne && step != null) {
						return step;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Searches for a strong link for candidate <code>cand</code> that connects
	 * <code>index1</code> and <code>index2</code> (both indices are seen by the
	 * strong link).
	 *
	 * @param cand1
	 * @param cand2
	 * @param index1
	 * @param index2
	 * @param elimSet
	 * @param onlyOne
	 * @return
	 */
	private PuzzleSolutionStep checkLink(int cand1, int cand2, int index1, int index2, SudokuSet elimSet,
			boolean onlyOne) {
		final byte[][] free = this.sudoku.getFree();
		for (int constr = 0; constr < free.length; constr++) {
			if (free[constr][cand2] == 2) {
				// strong link; does it fit?
				boolean sees1 = false;
				boolean sees2 = false;
				final int[] indices = Sudoku2.ALL_UNITS[constr];
				for (int i = 0; i < indices.length; i++) {
					final int aktIndex = indices[i];
					if (aktIndex != index1 && aktIndex != index2 && this.sudoku.isCandidate(aktIndex, cand2)) {
						// CAUTION: one cell of the strong link can see both bivalue cells -> forbidden
						if (Sudoku2.buddies[aktIndex].contains(index1)) {
							sees1 = true;
							this.wIndex1 = aktIndex;
						} else if (Sudoku2.buddies[aktIndex].contains(index2)) {
							sees2 = true;
							this.wIndex2 = aktIndex;
						}
					}
					if (sees1 && sees2) {
						// done
						break;
					}
				}
				if (sees1 && sees2) {
					// valid W-Wing!
					final PuzzleSolutionStep step = this.createWWingStep(cand1, cand2, index1, index2, elimSet,
							onlyOne);
					if (onlyOne && step != null) {
						return step;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Creates a step for a W-Wing. <code>cand1</code> is the candidate for which
	 * eliminations can be made, <code>cand2</code> is the connecting candidate.
	 * <code>index1</code> and <code>index2</code> are the bivalue cells,
	 * {@link #wIndex1} and {@link #wIndex2} are the strong link.
	 * <code>elimSet</code> holds all cells where candidates can be eliminated.
	 *
	 */
	private PuzzleSolutionStep createWWingStep(int cand1, int cand2, int index1, int index2, SudokuSet elimSet,
			boolean onlyOne) {
		this.globalStep.reset();
		this.globalStep.setTechnique(SolutionTechnique.W_WING);
		this.globalStep.addValue(cand1);
		this.globalStep.addValue(cand2);
		this.globalStep.addIndex(index1);
		this.globalStep.addIndex(index2);
		this.globalStep.addFin(index1, cand2);
		this.globalStep.addFin(index2, cand2);
		this.globalStep.addFin(this.wIndex1, cand2);
		this.globalStep.addFin(this.wIndex2, cand2);
		for (int i = 0; i < elimSet.size(); i++) {
			this.globalStep.addCandidateToDelete(elimSet.get(i), cand1);
		}
		final PuzzleSolutionStep step = (PuzzleSolutionStep) this.globalStep.clone();
		if (onlyOne) {
			return step;
		} else {
			this.steps.add(step);
		}
		return null;
	}

}
