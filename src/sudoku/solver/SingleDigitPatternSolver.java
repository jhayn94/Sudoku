package sudoku.solver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Empty Rectangles:
 *
 * Every box can hold nine different empty rectangles ('X' means 'candidate not
 * present', digits below are lines/cols):
 *
 * + - - - + + - - - + + - - - + | X X . | | X . X | | . X X | | X X . | | X . X
 * | | . X X | | . . . | | . . . | | . . . | + - - - + + - - - + + - - - + 2 2 2
 * 1 2 0 + - - - + + - - - + + - - - + | X X . | | X . X | | . X X | | . . . | |
 * . . . | | . . . | | X X . | | X . X | | . X X | + - - - + + - - - + + - - - +
 * 1 2 1 1 1 0 + - - - + + - - - + + - - - + | . . . | | . . . | | . . . | | X X
 * . | | X . X | | . X X | | X X . | | X . X | | . X X | + - - - + + - - - + + -
 * - - + 0 2 0 1 0 0
 *
 * The '.' cells must contain at least three candidates, at least one
 * exclusively within the row/col (with two candidates the basic ER move
 * degenerates into an X-Chain, with all three candidates only in a row/col it
 * doesn't work at all).
 *
 * For easy comparison SudokuSets with all possible combinations of empty cells
 * for all blocks are created at startup.
 *
 * @author hobiwan
 */
public class SingleDigitPatternSolver extends AbstractSolver {

	/** empty rectangles: all possible empty cells relative to cell 0 */
	private static final int[][] erOffsets = new int[][] { { 0, 1, 9, 10 }, { 0, 2, 9, 11 }, { 1, 2, 10, 11 },
			{ 0, 1, 18, 19 }, { 0, 2, 18, 20 }, { 1, 2, 19, 20 }, { 9, 10, 18, 19 }, { 9, 11, 18, 20 },
			{ 10, 11, 19, 20 } };
	/**
	 * empty rectangles: all possible ER lines relative to line 0, synchronized with
	 * {@link #erOffsets}
	 */
	private static final int[] erLineOffsets = new int[] { 2, 2, 2, 1, 1, 1, 0, 0, 0 };
	/**
	 * empty rectangles: all possible ER cols relative to col 0, synchronized with
	 * {@link #erOffsets}
	 */
	private static final int[] erColOffsets = new int[] { 2, 1, 0, 2, 1, 0, 2, 1, 0 };
	/**
	 * Bitmaps for all possible ERs for all blocks (all cells set except those that
	 * have to be empty; if anded with the availble candidates in a block the result
	 * has to be empty too)
	 */
	private static final SudokuSet[][] erSets = new SudokuSet[9][9];
	/** All possible ER lines for all blocks, synchronized with {@link #erSets} */
	private static final int[][] erLines = new int[9][9];
	/** All possible ER cols for all blocks, synchronized with {@link #erSets} */
	private static final int[][] erCols = new int[9][9];
	/** All candidates in a block (for ER search) */
	private final SudokuSet blockCands = new SudokuSet();
	/** A set for various checks */
	private final SudokuSet tmpSet = new SudokuSet();
	/** A list with all steps found */
	private List<PuzzleSolutionStep> steps = new ArrayList<PuzzleSolutionStep>();
	/** One global instance for optimization */
	private final PuzzleSolutionStep globalStep = new PuzzleSolutionStep();
	/** For all entries in {@link #only2Constraints} the indices of the two cells */
	private final int[][] only2Indices = new int[2 * SudokuPuzzle.UNITS][2];
	/** A set to check for eliminations */
	private final SudokuSet firstUnit = new SudokuSet();

	/**
	 * Creates a new instance of SimpleSolver
	 * 
	 * @param finder
	 */
	protected SingleDigitPatternSolver(SudokuStepFinder finder) {
		super(finder);
	}

	static {
		// initialize erSets, erLines, erCols
		int indexOffset = 0;
		int lineOffset = 0;
		int colOffset = 0;
		for (int i = 0; i < SudokuPuzzle.BLOCKS.length; i++) {
			for (int j = 0; j < erOffsets.length; j++) {
				erSets[i][j] = new SudokuSet();
				for (int k = 0; k < erOffsets[j].length; k++) {
					erSets[i][j].add(erOffsets[j][k] + indexOffset);
				}
			}
			erLines[i] = new int[9];
			erCols[i] = new int[9];
			for (int j = 0; j < erLineOffsets.length; j++) {
				erLines[i][j] = erLineOffsets[j] + lineOffset;
				erCols[i][j] = erColOffsets[j] + colOffset;
			}
			// on to the next block
			indexOffset += 3;
			colOffset += 3;
			if ((i % 3) == 2) {
				indexOffset += 18;
				lineOffset += 3;
				colOffset = 0;
			}

		}
	}

	@Override
	protected PuzzleSolutionStep getStep(SolutionTechnique type) {
		PuzzleSolutionStep result = null;
		this.sudoku = this.finder.getSudoku();
		switch (type) {
		case SKYSCRAPER:
			result = this.findSkyscraper();
			break;
		case TWO_STRING_KITE:
			result = this.findTwoStringKite();
			break;
		case EMPTY_RECTANGLE:
			result = this.findEmptyRectangle();
			break;
		}
		return result;
	}

	@Override
	protected boolean doStep(PuzzleSolutionStep step) {
		boolean handled = true;
		this.sudoku = this.finder.getSudoku();
		switch (step.getType()) {
		case SKYSCRAPER:
		case TWO_STRING_KITE:
		case DUAL_TWO_STRING_KITE:
		case EMPTY_RECTANGLE:
		case DUAL_EMPTY_RECTANGLE:
			for (final Candidate cand : step.getCandidatesToDelete()) {
				this.sudoku.delCandidate(cand.getIndex(), cand.getValue());
			}
			break;
		default:
			handled = false;
		}
		return handled;
	}

	/**
	 * Finds all Empty Rectangles
	 * 
	 * @return
	 */
	protected List<PuzzleSolutionStep> findAllEmptyRectangles() {
		this.sudoku = this.finder.getSudoku();
		final List<PuzzleSolutionStep> oldList = this.steps;
		final List<PuzzleSolutionStep> newList = new ArrayList<PuzzleSolutionStep>();
		this.steps = newList;
		this.findEmptyRectangles(false);
		this.findDualEmptyRectangles(this.steps);
		Collections.sort(this.steps);
		this.steps = oldList;
		return newList;
	}

	/**
	 * Find a single ER. If {@link Options#allowDualsAndSiamese} is set, Dual ERs
	 * are found as well.
	 * 
	 * @return
	 */
	protected PuzzleSolutionStep findEmptyRectangle() {
		this.steps.clear();
		final PuzzleSolutionStep step = this.findEmptyRectangles(true);
		if (step != null && !Options.getInstance().isAllowDualsAndSiamese()) {
			return step;
		}
		if (this.steps.size() > 0 && Options.getInstance().isAllowDualsAndSiamese()) {
			this.findDualEmptyRectangles(this.steps);
			Collections.sort(this.steps);
			return this.steps.get(0);
		}
		return null;
	}

	/**
	 * Finds all empty rectangles that provide eliminations (only simple case with
	 * one conjugate pair). The search is actually delegated to
	 * {@link #findEmptyRectanglesForCandidate(int)}.
	 * 
	 * @param onlyOne
	 * @return
	 */
	private PuzzleSolutionStep findEmptyRectangles(boolean onlyOne) {
		for (int i = 1; i <= 9; i++) {
			final PuzzleSolutionStep step = this.findEmptyRectanglesForCandidate(i, onlyOne);
			if (step != null && onlyOne && !Options.getInstance().isAllowDualsAndSiamese()) {
				return step;
			}
		}
		return null;
	}

	/**
	 * Try all blocks: for every block check whether all the cells in
	 * erSets[block][i] don't have the candidate in question. If this is true
	 * neither the ER line nor the ER col may be empty (without crossing point!) and
	 * at least one of them has to hold at least two candidates.
	 * 
	 * For any ER try to find a conjugate pair with one candidate in the row/col of
	 * the ER, and one single candidate in ther intersection of the second ca didate
	 * of the conjugate pair and the col/row of the ER.
	 * 
	 * @param cand    candidate for which the grid is searched
	 * @param onlyOne
	 * @return
	 */
	private PuzzleSolutionStep findEmptyRectanglesForCandidate(int cand, boolean onlyOne) {
		// scan all blocks
		final byte[][] free = this.sudoku.getFree();
		for (int i = 0; i < SudokuPuzzle.BLOCK_TEMPLATES.length; i++) {
			// if the block holds less than two or more than five candidates,
			// it cant be a ER
			if (free[18 + i][cand] < 2 || free[18 + i][cand] > 5) {
				// impossible
				continue;
			}
			// get all occurrencies for cand in block i
			this.blockCands.set(this.finder.getCandidates()[cand]);
			this.blockCands.and(SudokuPuzzle.BLOCK_TEMPLATES[i]);
			// check all possible ERs for that block
			for (int j = 0; j < erSets[i].length; j++) {
				int erLine = 0;
				int erCol = 0;
				boolean notEnoughCandidates = true;
				// are the correct cells empty?
				this.tmpSet.setAnd(this.blockCands, erSets[i][j]);
				if (!this.tmpSet.isEmpty()) {
					// definitely not this type of ER
					continue;
				}
				// now check the candidates in the lines
				this.tmpSet.setAnd(this.blockCands, SudokuPuzzle.LINE_TEMPLATES[erLines[i][j]]);
				if (this.tmpSet.size() >= 2) {
					notEnoughCandidates = false;
				}
				this.tmpSet.andNot(SudokuPuzzle.COL_TEMPLATES[erCols[i][j]]);
				if (this.tmpSet.isEmpty()) {
					// not valid!
					continue;
				}
				erLine = erLines[i][j];
				// and the candidates in the cols
				this.tmpSet.setAnd(this.blockCands, SudokuPuzzle.COL_TEMPLATES[erCols[i][j]]);
				if (this.tmpSet.size() >= 2) {
					notEnoughCandidates = false;
				}
				this.tmpSet.andNot(SudokuPuzzle.LINE_TEMPLATES[erLines[i][j]]);
				if (this.tmpSet.isEmpty()) {
					// not valid!
					continue;
				}
				erCol = erCols[i][j];
				if (notEnoughCandidates && Options.getInstance().isAllowErsWithOnlyTwoCandidates() == false) {
					// both row and col have only one candidate -> invalid
					continue;
				}
				// empty rectangle found: erLine and erCol hold the lineNumbers
				// try all cells in indices erLine; if a cell that is not part of the ER holds
				// a candidate, check whether it forms a conjugate pair in the respective col
				PuzzleSolutionStep step = this.checkEmptyRectangle(cand, i, this.blockCands, SudokuPuzzle.LINES[erLine],
						SudokuPuzzle.LINE_TEMPLATES, SudokuPuzzle.COL_TEMPLATES, erCol, false, onlyOne);
				if (onlyOne && step != null && !Options.getInstance().isAllowDualsAndSiamese()) {
					return step;
				}
				step = this.checkEmptyRectangle(cand, i, this.blockCands, SudokuPuzzle.COLS[erCol], SudokuPuzzle.COL_TEMPLATES,
						SudokuPuzzle.LINE_TEMPLATES, erLine, true, onlyOne);
				if (onlyOne && step != null && !Options.getInstance().isAllowDualsAndSiamese()) {
					return step;
				}
			}
		}
		return null;
	}

	/**
	 * Checks possible eliminations for a given ER. The names of the parameters are
	 * chosen for a conjugate pair search in the columns, but it works for the lines
	 * too, if all indices/col parameters are exchanged in the method call.
	 * 
	 * The method tries to find a conjugate pair in a column where one of the
	 * candidates is in indices firstLine. If so all candidates in the indices of
	 * the second cell of the conjugate pair are checked. If one of them lies in
	 * column firstCol, it can be eliminated.
	 * 
	 * @param cand            The candidate for which the check is made
	 * @param block           The index of the block holding the ER
	 * @param blockCands      All Candidates that comprise the ER
	 * @param indices         Indices of all cells in firstLine/firstCol
	 * @param LINE_TEMPLATES  SudokuPuzzle.LINE_TEMPLATES/SudokuPuzzle.COL_TEMPLATES
	 * @param COL_TEMPLATES   SudokuPuzzle.COL_TEMPLATES/SudokuPuzzle.LINE_TEMPLATES
	 * @param firstCol        Index of the col/indices of the ER
	 * @param lineColReversed If <code>true</code>, all lines/columns are
	 *                        interchanged
	 * @param onlyOne
	 * @return
	 */
	private PuzzleSolutionStep checkEmptyRectangle(int cand, int block, SudokuSet blockCands, int[] indices,
			SudokuSet[] lineTemplates, SudokuSet[] colTemplates, int firstCol, boolean lineColReversed,
			boolean onlyOne) {
		for (int i = 0; i < indices.length; i++) {
			final int index = indices[i];
			if (this.sudoku.getValue(index) != 0) {
				// cell already set
				continue;
			}
			if (SudokuPuzzle.getBlock(index) == block) {
				// cell part of the ER
				continue;
			}
			if (this.sudoku.isCandidate(index, cand)) {
				// possible conjugate pair -> check
				this.tmpSet.set(this.finder.getCandidates()[cand]);
				int actCol = SudokuPuzzle.getCol(index);
				if (lineColReversed) {
					actCol = SudokuPuzzle.getLine(index);
				}
				this.tmpSet.and(colTemplates[actCol]);
				if (this.tmpSet.size() == 2) {
					// conjugate pair found
					int index2 = this.tmpSet.get(0);
					if (index2 == index) {
						index2 = this.tmpSet.get(1);
					}
					// now check, whether a candidate in the row of index2
					// sees the col of the ER
					int actLine = SudokuPuzzle.getLine(index2);
					if (lineColReversed) {
						actLine = SudokuPuzzle.getCol(index2);
					}
					this.tmpSet.set(this.finder.getCandidates()[cand]);
					this.tmpSet.and(lineTemplates[actLine]);
					for (int j = 0; j < this.tmpSet.size(); j++) {
						final int indexDel = this.tmpSet.get(j);
						if (SudokuPuzzle.getBlock(indexDel) == block) {
							// cannot eliminate an ER candidate
							continue;
						}
						int colDel = SudokuPuzzle.getCol(indexDel);
						if (lineColReversed) {
							colDel = SudokuPuzzle.getLine(indexDel);
						}
						if (colDel == firstCol) {
							// elimination found!
							this.globalStep.reset();
							this.globalStep.setType(SolutionTechnique.EMPTY_RECTANGLE);
							this.globalStep.setEntity(SudokuPuzzle.BLOCK);
							this.globalStep.setEntityNumber(block + 1);
							this.globalStep.addValue(cand);
							this.globalStep.addIndex(index);
							this.globalStep.addIndex(index2);
							for (int k = 0; k < blockCands.size(); k++) {
								this.globalStep.addFin(blockCands.get(k), cand);
							}
							this.globalStep.addCandidateToDelete(indexDel, cand);
							final PuzzleSolutionStep step = (PuzzleSolutionStep) this.globalStep.clone();
							// only one elimination per conjugate pair possible
							if (onlyOne && !Options.getInstance().isAllowDualsAndSiamese()) {
								return step;
							} else {
								this.steps.add(step);
							}
							break;
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * A dual Empty Rectangle consists of two ERs, that have the same candidates in
	 * the ER box but lead to different eliminations.
	 * 
	 * Try all combinations of steps: - entity and entityNumber have to be the same
	 * - box candidiates have to be the same (fins!) - elimination has to be
	 * different Create new step with indices/eliminations from both, fins from
	 * first, add to ers
	 * 
	 * @param kites All available 2-String-Kites
	 */
	private void findDualEmptyRectangles(List<PuzzleSolutionStep> ers) {
		if (!Options.getInstance().isAllowDualsAndSiamese()) {
			// do nothing
			return;
		}
		// read current size (list can be changed by DUALS)
		final int maxIndex = ers.size();
		for (int i = 0; i < maxIndex - 1; i++) {
			for (int j = i + 1; j < maxIndex; j++) {
				final PuzzleSolutionStep step1 = ers.get(i);
				final PuzzleSolutionStep step2 = ers.get(j);
				if (step1.getEntity() != step2.getEntity() || step1.getEntityNumber() != step2.getEntityNumber()) {
					// different boxes -> cant be a dual
					continue;
				}
				if (step1.getFins().size() != step2.getFins().size()) {
					// different number of candidates in box -> cant be a dual
					continue;
				}
				boolean finsEqual = true;
				for (int k = 0; k < step1.getFins().size(); k++) {
					if (!step1.getFins().get(k).equals(step2.getFins().get(k))) {
//                        System.out.println("  " + step1.getFins().get(k) + " - " + step2.getFins().get(k));
						finsEqual = false;
						break;
					}
				}
				if (!finsEqual) {
					// not the same ER -> cant be a dual
					continue;
				}
				// possible dual ER; different eliminations?
				if (step1.getCandidatesToDelete().get(0).equals(step2.getCandidatesToDelete().get(0))) {
					// same step twice -> no dual
					continue;
				}
				// ok: dual!
				final PuzzleSolutionStep dual = (PuzzleSolutionStep) step1.clone();
				dual.setType(SolutionTechnique.DUAL_EMPTY_RECTANGLE);
				dual.addIndex(step2.getIndices().get(0));
				dual.addIndex(step2.getIndices().get(1));
				dual.addCandidateToDelete(step2.getCandidatesToDelete().get(0));
				ers.add(dual);
			}
		}
	}

	/**
	 * Search for all Skyscrapers
	 * 
	 * @return
	 */
	protected List<PuzzleSolutionStep> findAllSkyscrapers() {
		this.sudoku = this.finder.getSudoku();
		final List<PuzzleSolutionStep> oldList = this.steps;
		final List<PuzzleSolutionStep> newList = new ArrayList<PuzzleSolutionStep>();
		this.steps = newList;
		this.findSkyscraper(true, false);
		this.findSkyscraper(false, false);
		Collections.sort(this.steps);
		this.steps = oldList;
		return newList;
	}

	/**
	 * Search the grid for Skyscrapers
	 * 
	 * @return
	 */
	protected PuzzleSolutionStep findSkyscraper() {
		this.steps.clear();
		final PuzzleSolutionStep step = this.findSkyscraper(true, true);
		if (step != null) {
			return step;
		}
		return this.findSkyscraper(false, true);
	}

	/**
	 * Search for Skyscrapers in the lines or in the columns. Two calls are
	 * necessary to get all possible steps.<br>
	 * The search:
	 * <ul>
	 * <li>Iterate over all candidates</li>
	 * <li>For each candidate look at all lines (cols) and check which have only two
	 * candidates left</li>
	 * <li></li>
	 * <li></li>
	 * </ul>
	 * 
	 * @param lines
	 * @param onlyOne
	 * @return
	 */
	private PuzzleSolutionStep findSkyscraper(boolean lines, boolean onlyOne) {
		// indices in free
		int cStart = 0;
		int cEnd = 9;
		if (!lines) {
			// adjust for columns
			cStart += 9;
			cEnd += 9;
		}
		final byte[][] free = this.sudoku.getFree();
		// try every candidate
		for (int cand = 1; cand <= 9; cand++) {
			// get all constraints with only two candidates and the indices of the cells
			int constrCount = 0;
			for (int constr = cStart; constr < cEnd; constr++) {
				if (free[constr][cand] == 2) {
					// constraint has only two candidates left -> get the indices of the cells
					final int[] indices = SudokuPuzzle.ALL_UNITS[constr];
					int candIndex = 0;
					for (int i = 0; i < indices.length; i++) {
						if (this.sudoku.isCandidate(indices[i], cand)) {
							this.only2Indices[constrCount][candIndex++] = indices[i];
							if (candIndex >= 2) {
								break;
							}
						}
					}
					constrCount++;
				}
			}
			// ok: now try all combinations of those constraints
			for (int i = 0; i < constrCount; i++) {
				for (int j = i + 1; j < constrCount; j++) {
					// one end has to be in the same line/col
					boolean found = false;
					int otherIndex = 1;
					if (lines) {
						// must be in the same col
						if (SudokuPuzzle.getCol(this.only2Indices[i][0]) == SudokuPuzzle.getCol(this.only2Indices[j][0])) {
							found = true;
						}
						if (!found
								&& SudokuPuzzle.getCol(this.only2Indices[i][1]) == SudokuPuzzle.getCol(this.only2Indices[j][1])) {
							found = true;
							otherIndex = 0;
						}
					} else {
						// must be in the same line
						if (SudokuPuzzle.getLine(this.only2Indices[i][0]) == SudokuPuzzle.getLine(this.only2Indices[j][0])) {
							found = true;
						}
						if (!found && SudokuPuzzle.getLine(this.only2Indices[i][1]) == SudokuPuzzle
								.getLine(this.only2Indices[j][1])) {
							found = true;
							otherIndex = 0;
						}
					}
					if (!found) {
						// invalid combination
						continue;
					}
					// the "free ends" must not be in the same unit or it would be an X-Wing
					if (lines
							&& SudokuPuzzle.getCol(this.only2Indices[i][otherIndex]) == SudokuPuzzle
									.getCol(this.only2Indices[j][otherIndex])
							|| !lines && SudokuPuzzle.getLine(this.only2Indices[i][otherIndex]) == SudokuPuzzle
									.getLine(this.only2Indices[j][otherIndex])) {
						// step is X-Wing -> ignore
						continue;
					}
					// can something be eliminated?
					this.firstUnit.setAnd(this.finder.getCandidates()[cand],
							SudokuPuzzle.buddies[this.only2Indices[i][otherIndex]]);
					this.firstUnit.and(SudokuPuzzle.buddies[this.only2Indices[j][otherIndex]]);
					if (!this.firstUnit.isEmpty()) {
						// Skyscraper found!
						final PuzzleSolutionStep step = new PuzzleSolutionStep(SolutionTechnique.SKYSCRAPER);
						step.addValue(cand);
						if (otherIndex == 0) {
							step.addIndex(this.only2Indices[i][0]);
							step.addIndex(this.only2Indices[j][0]);
							step.addIndex(this.only2Indices[i][1]);
							step.addIndex(this.only2Indices[j][1]);
						} else {
							step.addIndex(this.only2Indices[i][1]);
							step.addIndex(this.only2Indices[j][1]);
							step.addIndex(this.only2Indices[i][0]);
							step.addIndex(this.only2Indices[j][0]);
						}
						for (int k = 0; k < this.firstUnit.size(); k++) {
							step.addCandidateToDelete(this.firstUnit.get(k), cand);
						}
//                        if (onlyOne && ! Options.getInstance().isAllowDualsAndSiamese()) {
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
	 * FIns all 2-String-Kites in the grid
	 * 
	 * @return
	 */
	protected List<PuzzleSolutionStep> findAllTwoStringKites() {
		this.sudoku = this.finder.getSudoku();
		final List<PuzzleSolutionStep> oldList = this.steps;
		final List<PuzzleSolutionStep> newList = new ArrayList<PuzzleSolutionStep>();
		this.steps = newList;
		this.findTwoStringKite(false);
		if (Options.getInstance().isAllowDualsAndSiamese()) {
			this.findDualTwoStringKites(this.steps);
		}
		Collections.sort(this.steps);
		this.steps = oldList;
		return newList;
	}

	/**
	 * Find the next 2-String-Kite
	 * 
	 * @return
	 */
	protected PuzzleSolutionStep findTwoStringKite() {
		this.steps.clear();
		final PuzzleSolutionStep step = this.findTwoStringKite(true);
		if (step != null && !Options.getInstance().isAllowDualsAndSiamese()) {
			return step;
		}
		this.findDualTwoStringKites(this.steps);
		if (this.steps.size() > 0) {
			Collections.sort(this.steps);
			return this.steps.get(0);
		} else {
			return null;
		}
	}

	/**
	 * Search for 2-String-Kites: We need a strong link in a line and one in a col.
	 * The two strong links must be connected by a box and the "free ends" must see
	 * a candidate.
	 * 
	 * @param onlyOne
	 * @return
	 */
	private PuzzleSolutionStep findTwoStringKite(boolean onlyOne) {
		// search for lines and columns with exactly two candidates
		final byte[][] free = this.sudoku.getFree();
		// try every candidate
		for (int cand = 1; cand <= 9; cand++) {
			// get all constraints with only two candidates and the indices of the cells
			// all lines are in only2Indices[0 .. constr1Count - 1], all cols
			// are in only2Indices[constr1Count .. constr2Count - 1]
			int constr1Count = 0;
			int constr2Count = 0;
			for (int constr = 0; constr < 18; constr++) {
				if (free[constr][cand] == 2) {
					// constraint has only two candidates left -> get the indices of the cells
					final int[] indices = SudokuPuzzle.ALL_UNITS[constr];
					int candIndex = 0;
					for (int i = 0; i < indices.length; i++) {
						if (this.sudoku.isCandidate(indices[i], cand)) {
							this.only2Indices[constr1Count + constr2Count][candIndex++] = indices[i];
							if (candIndex >= 2) {
								break;
							}
						}
					}
					if (constr < 9) {
						constr1Count++;
					} else {
						constr2Count++;
					}
				}
			}
			// ok: now try all combinations of those constraints
			for (int i = 0; i < constr1Count; i++) {
				for (int j = constr1Count; j < constr1Count + constr2Count; j++) {
					// one end has to be in the same line/col, but: all 4 combinations are possible
					// the indices in the same block end up in only2Indices[][0], the "free ends"
					// in only2indices[][1]
					if (SudokuPuzzle.getBlock(this.only2Indices[i][0]) == SudokuPuzzle.getBlock(this.only2Indices[j][0])) {
						// everything is as it should be -> do nothing
					} else if (SudokuPuzzle.getBlock(this.only2Indices[i][0]) == SudokuPuzzle.getBlock(this.only2Indices[j][1])) {
						final int tmp = this.only2Indices[j][0];
						this.only2Indices[j][0] = this.only2Indices[j][1];
						this.only2Indices[j][1] = tmp;
					} else if (SudokuPuzzle.getBlock(this.only2Indices[i][1]) == SudokuPuzzle.getBlock(this.only2Indices[j][0])) {
						final int tmp = this.only2Indices[i][0];
						this.only2Indices[i][0] = this.only2Indices[i][1];
						this.only2Indices[i][1] = tmp;
					} else if (SudokuPuzzle.getBlock(this.only2Indices[i][1]) == SudokuPuzzle.getBlock(this.only2Indices[j][1])) {
						int tmp = this.only2Indices[j][0];
						this.only2Indices[j][0] = this.only2Indices[j][1];
						this.only2Indices[j][1] = tmp;
						tmp = this.only2Indices[i][0];
						this.only2Indices[i][0] = this.only2Indices[i][1];
						this.only2Indices[i][1] = tmp;
					} else {
						// nothing found -> continue with next column
						continue;
					}
					// the indices within the connecting box could be the same -> not a
					// 2-String-Kite
					if (this.only2Indices[i][0] == this.only2Indices[j][0]
							|| this.only2Indices[i][0] == this.only2Indices[j][1]
							|| this.only2Indices[i][1] == this.only2Indices[j][0]
							|| this.only2Indices[i][1] == this.only2Indices[j][1]) {
						// invalid!
						continue;
					}
					// ok: two strong links, connected in a box; can anything be deleted?
					final int crossIndex = SudokuPuzzle.getIndex(SudokuPuzzle.getLine(this.only2Indices[j][1]),
							SudokuPuzzle.getCol(this.only2Indices[i][1]));
					if (this.sudoku.isCandidate(crossIndex, cand)) {
						// valid 2-String-Kite!
						final PuzzleSolutionStep step = new PuzzleSolutionStep(SolutionTechnique.TWO_STRING_KITE);
						step.addValue(cand);
						step.addIndex(this.only2Indices[i][1]);
						step.addIndex(this.only2Indices[j][1]);
						step.addIndex(this.only2Indices[i][0]);
						step.addIndex(this.only2Indices[j][0]);
						step.addCandidateToDelete(crossIndex, cand);
						// the candidates in the connecting block are added as fins (will be painted
						// in a different color)
						step.addFin(this.only2Indices[i][0], cand);
						step.addFin(this.only2Indices[j][0], cand);
						if (onlyOne && !Options.getInstance().isAllowDualsAndSiamese()) {
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
	 * A dual 2-String-Kite consists of two kites, that have the same candidates in
	 * the connecting box but lead to different eliminations.
	 * 
	 * Try all combinations of steps: - box candidates have to be the same (fins!) -
	 * elimination has to be different Create new step with indices/eliminations
	 * from both, fins from first, add to kites
	 * 
	 * @param kites All available 2-String-Kites
	 */
	private void findDualTwoStringKites(List<PuzzleSolutionStep> kites) {
		if (!Options.getInstance().isAllowDualsAndSiamese()) {
			// do nothing
			return;
		}
		// read current size (list can be changed by DUALS)
		final int maxIndex = kites.size();
		for (int i = 0; i < maxIndex - 1; i++) {
			for (int j = i + 1; j < maxIndex; j++) {
				final PuzzleSolutionStep step1 = kites.get(i);
				final PuzzleSolutionStep step2 = kites.get(j);
				final int b11 = step1.getIndices().get(2);
				final int b12 = step1.getIndices().get(3);
				final int b21 = step2.getIndices().get(2);
				final int b22 = step2.getIndices().get(3);
				if ((b11 == b21 && b12 == b22) || (b12 == b21 && b11 == b22)) {
					// possible dual kite; different eliminations?
					if (step1.getCandidatesToDelete().get(0).equals(step2.getCandidatesToDelete().get(0))) {
						// same step twice -> no dual
						continue;
					}
					// ok: dual!
					final PuzzleSolutionStep dual = (PuzzleSolutionStep) step1.clone();
					dual.setType(SolutionTechnique.DUAL_TWO_STRING_KITE);
					dual.addIndex(step2.getIndices().get(0));
					dual.addIndex(step2.getIndices().get(1));
					dual.addIndex(step2.getIndices().get(2));
					dual.addIndex(step2.getIndices().get(3));
					dual.addCandidateToDelete(step2.getCandidatesToDelete().get(0));
					kites.add(dual);
				}
			}
		}
	}

	public static void main(String[] args) {
		final SudokuPuzzle sudoku = new SudokuPuzzle();
		sudoku.setSudoku(
				":0401:3:+156+87+49+3+2.4+762.+18+528....+4+7+6....8.+5+9.73....618+8.5...+32.........+3.7.5...49....487.1::381::");
		sudoku.setSudoku(
				":0401:3:9.567.1..61.5+4...+9.849+3+15+6....8.39.....+2.+9....+987.4...+5+61.+9782.+8+7+9.+26.51..2+1857+96:249 261 165 367 369:328::");

		final SudokuSolver solver = SudokuSolverFactory.getDefaultSolverInstance();
		final PuzzleSolutionStep step = solver.getHint(sudoku, false);
		System.out.println(step);
		System.exit(0);
	}
}
