package sudoku.view.util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import sudoku.solver.Als;
import sudoku.solver.RestrictedCommon;

public class PuzzleSolutionStep implements Comparable<PuzzleSolutionStep>, Cloneable {

	private static final String[] entityNames = { "block", "row", "col", "cell" };
	private static final String[] entityShortNames = { "b", "r", "c", "" };
	private static final DecimalFormat FISH_FORMAT = new DecimalFormat("#00");
	private SolutionTechnique technique;
	// For kraken fish: holds the underlying fish type.
	private SolutionTechnique subType;
	private int entity;
	private int entityNumber;
	// For various locked candidates types.
	private int entity2; // für LOCKED_CANDIDATES_X
	private int entity2Number; // für LOCKED_CANDIDATES_X
	private boolean isSiamese; // für Siamese Fish
	private int progressScoreSingles = -1; // number of singles that this step unlocks in the sudoku
	private int progressScoreSinglesOnly = -1; // direct unlocked singles
	private int progressScore = -1; // the resulting score (only no single steps)
	private List<Integer> values = new ArrayList<Integer>();
	private List<Integer> indices = new ArrayList<Integer>();
	private List<Candidate> candidatesToDelete = new ArrayList<Candidate>();
	private List<Candidate> cannibalistic = new ArrayList<Candidate>();
	private List<Candidate> fins = new ArrayList<Candidate>(); // für Finned Fische
	private List<Candidate> endoFins = new ArrayList<Candidate>(); // für Finned Fische
	private List<Entity> baseEntities = new ArrayList<Entity>(); // für Fisch
	private List<Entity> coverEntities = new ArrayList<Entity>(); // für Fisch
	private List<Chain> chains = new ArrayList<Chain>(); // Für alle Arten Chains und Loops
	private List<AlsInSolutionStep> alses = new ArrayList<AlsInSolutionStep>();
	private SortedMap<Integer, Integer> colorCandidates = new TreeMap<Integer, Integer>(); // coloring moves
	private List<RestrictedCommon> restrictedCommons = new ArrayList<RestrictedCommon>(); // ALS Chains
	private SudokuSet potentialCannibalisticEliminations = new SudokuSet(); // for fish only
	private SudokuSet potentialEliminations = new SudokuSet(); // for fish only

	public PuzzleSolutionStep() {
	}

	/**
	 * Creates a new instance of SolutionStep
	 *
	 * @param type
	 */
	public PuzzleSolutionStep(SolutionTechnique solutionTechnique) {
		this.setTechnique(solutionTechnique);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object clone() {
		PuzzleSolutionStep newStep = null;
		try {
			newStep = (PuzzleSolutionStep) super.clone();
			newStep.technique = this.technique;
			newStep.entity = this.entity;
			newStep.entityNumber = this.entityNumber;
			newStep.entity2 = this.entity2;
			newStep.entity2Number = this.entity2Number;
			newStep.isSiamese = this.isSiamese;
			newStep.progressScoreSingles = this.progressScoreSingles;
			newStep.progressScoreSinglesOnly = this.progressScoreSinglesOnly;
			newStep.progressScore = this.progressScore;
			newStep.values = (List<Integer>) ((ArrayList) this.values).clone();
			newStep.indices = (List<Integer>) ((ArrayList) this.indices).clone();
			newStep.candidatesToDelete = (List<Candidate>) ((ArrayList) this.candidatesToDelete).clone();
			newStep.cannibalistic = (List<Candidate>) ((ArrayList) this.cannibalistic).clone();
			newStep.fins = (List<Candidate>) ((ArrayList) this.fins).clone();
			newStep.endoFins = (List<Candidate>) ((ArrayList) this.endoFins).clone();
			newStep.baseEntities = (List<Entity>) ((ArrayList) this.baseEntities).clone();
			newStep.coverEntities = (List<Entity>) ((ArrayList) this.coverEntities).clone();
			newStep.chains = (List<Chain>) ((ArrayList) this.chains).clone();
			newStep.alses = (List<AlsInSolutionStep>) ((ArrayList) this.alses).clone();
			newStep.colorCandidates = (SortedMap<Integer, Integer>) ((TreeMap) this.getColorCandidates()).clone();
			newStep.restrictedCommons = (List<RestrictedCommon>) ((ArrayList) this.restrictedCommons).clone();
			newStep.potentialCannibalisticEliminations = this.potentialCannibalisticEliminations.clone();
			newStep.potentialEliminations = this.potentialEliminations.clone();
		} catch (final CloneNotSupportedException ex) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Error while cloning", ex);
		}

		return newStep;
	}

	public void reset() {
		this.technique = SolutionTechnique.HIDDEN_SINGLE;
		this.entity = 0;
		this.entityNumber = 0;
		this.entity2 = 0;
		this.entity2Number = 0;
		this.isSiamese = false;
		this.progressScoreSingles = -1;
		this.progressScoreSinglesOnly = -1;
		this.progressScore = -1;
		this.values.clear();
		this.indices.clear();
		this.candidatesToDelete.clear();
		this.cannibalistic.clear();
		this.fins.clear();
		this.endoFins.clear();
		this.baseEntities.clear();
		this.coverEntities.clear();
		this.chains.clear();
		this.alses.clear();
		this.colorCandidates.clear();
		this.restrictedCommons.clear();
		this.potentialCannibalisticEliminations.clear();
		this.potentialEliminations.clear();
	}

	public StringBuffer getForcingChainString(Chain chain) {
		return this.getForcingChainString(chain.getChain(), chain.getStart(), chain.getEnd(), true);
	}

	public StringBuffer getForcingChainString(int[] chain, int start, int end, boolean weakLinks) {
		final StringBuffer tmp = new StringBuffer();
		boolean inMin = false;
		this.appendForcingChainEntry(tmp, chain[start]);
		for (int i = start + 1; i <= end - 1; i++) {
			boolean blank = true;
			if (chain[i] == Integer.MIN_VALUE) {
				tmp.append(")");
				inMin = false;
				continue;
			}
			if (!weakLinks && !Chain.isSStrong(chain[i])
					&& (chain[i] > 0 || chain[i] < 0 && chain[i + 1] < 0 && chain[i + 1] != Integer.MIN_VALUE)) {
				if (Chain.getSNodeType(chain[i]) == Chain.NORMAL_NODE) {
					continue;
				}
			}
			if (chain[i] < 0 && !inMin) {
				tmp.append(" (");
				inMin = true;
				blank = false;
			}
			if (chain[i] > 0 && inMin) {
				tmp.append(")");
				inMin = false;
			}
			if (blank) {
				tmp.append(" ");
			}
			this.appendForcingChainEntry(tmp, chain[i]);
		}
		tmp.append(" ");
		this.appendForcingChainEntry(tmp, chain[end]);
		return tmp;
	}

	public void appendForcingChainEntry(StringBuffer buf, int chainEntry) {
		final int entry = chainEntry < 0 ? -chainEntry : chainEntry;
		// buf.append(getCellPrint((entry / 10) % 100, false));
		switch (Chain.getSNodeType(entry)) {
		case Chain.NORMAL_NODE:
			buf.append(getCellPrint(Chain.getSCellIndex(entry), false));
			break;
		case Chain.GROUP_NODE:
			buf.append(getCompactCellPrint(Chain.getSCellIndex(entry), Chain.getSCellIndex2(entry),
					Chain.getSCellIndex3(entry)));
			break;
		case Chain.ALS_NODE:
			final int alsIndex = Chain.getSCellIndex2(entry);
			if (alsIndex >= 0 && alsIndex < this.alses.size()) {
				buf.append("ALS:");
				this.getAls(buf, alsIndex, false);
			} else {
				buf.append("UNKNOWN ALS");
			}
			break;
		}
		if (!Chain.isSStrong(entry)) {
			buf.append("<>");
		} else {
			buf.append("=");
		}
		buf.append(Chain.getSCandidate(entry));
	}

	public StringBuffer getChainString(Chain chain) {
		return this.getChainString(chain.getChain(), chain.getStart(), chain.getEnd(), false, true, true, false);
	}

	public StringBuffer getChainString(Chain chain, boolean internalFormat) {
		return this.getChainString(chain.getChain(), chain.getStart(), chain.getEnd(), true, true, true,
				internalFormat);
	}

	public StringBuffer getChainString(int[] chain, int start, int end, boolean alternate, boolean up) {
		return this.getChainString(chain, start, end, alternate, up, true, false);
	}

	public StringBuffer getChainString(int[] chain, int start, int end, boolean alternate, boolean up,
			boolean asNiceLoop, boolean internalFormat) {
		final StringBuffer tmp = new StringBuffer();
		boolean isStrong = false;
		int lastIndex = -1;
		if (up) {
			for (int i = start; i <= end; i++) {
				if (internalFormat) {
					if (i > start) {
						tmp.append("-");
					}
					tmp.append(chain[i]);
				} else {
					if (i == start + 1) {
						isStrong = Chain.isSStrong(chain[i]);
					} else {
						isStrong = !isStrong;
					}
					if (asNiceLoop && Chain.getSCellIndex(chain[i]) == lastIndex) {
						continue;
					} else {
						lastIndex = Chain.getSCellIndex(chain[i]);
					}
					if (i > start) {
						final int cand = Chain.getSCandidate(chain[i]);
						if (!Chain.isSStrong(chain[i]) || (alternate && !isStrong)) {
							tmp.append(" -");
							tmp.append(cand);
							tmp.append("- ");
						} else {
							tmp.append(" =");
							tmp.append(cand);
							tmp.append("= ");
						}
					}
					switch (Chain.getSNodeType(chain[i])) {
					case Chain.NORMAL_NODE:
						tmp.append(getCellPrint(Chain.getSCellIndex(chain[i]), false));
						break;
					case Chain.GROUP_NODE:
						tmp.append(getCompactCellPrint(Chain.getSCellIndex(chain[i]), Chain.getSCellIndex2(chain[i]),
								Chain.getSCellIndex3(chain[i])));
						break;
					case Chain.ALS_NODE:
						final int alsIndex = Chain.getSCellIndex2(chain[i]);
						if (alsIndex < this.alses.size()) {
							tmp.append("ALS:");
							this.getAls(tmp, alsIndex, false);
						} else {
							tmp.append("UNKNOWN ALS");
						}
						break;
					default:
						tmp.append("INV");
					}
				}
			}
		} else {
			for (int i = end; i >= start; i--) {
				if (internalFormat) {
					if (i > start) {
						tmp.append("-");
					}
					tmp.append(chain[i]);
				} else {
					if (i == end - 1) {
						isStrong = Chain.isSStrong(chain[i + 1]);
					} else {
						isStrong = !isStrong;
					}
					if (Chain.getSCellIndex(chain[i + 1]) == lastIndex) {
						continue;
					} else {
						lastIndex = Chain.getSCellIndex(chain[i + 1]);
					}
					if (i < end) {
						final int cand = Chain.getSCandidate(chain[i]);
						if (!Chain.isSStrong(chain[i + 1]) || (alternate && !isStrong)) {
							tmp.append(" -");
							tmp.append(cand);
							tmp.append("- ");
						} else {
							tmp.append(" =");
							tmp.append(cand);
							tmp.append("= ");
						}
					}
					switch (Chain.getSNodeType(chain[i])) {
					case Chain.NORMAL_NODE:
						tmp.append(getCellPrint(Chain.getSCellIndex(chain[i]), false));
						break;
					case Chain.GROUP_NODE:
						tmp.append(getCompactCellPrint(Chain.getSCellIndex(chain[i]), Chain.getSCellIndex2(chain[i]),
								Chain.getSCellIndex3(chain[i])));
						break;
					case Chain.ALS_NODE:
						final int alsIndex = Chain.getSCellIndex2(chain[i]);
						if (alsIndex < this.alses.size()) {
							tmp.append("ALS:");
							this.getAls(tmp, alsIndex, false);
						} else {
							tmp.append("UNKNOWN ALS");
						}
						break;
					}
				}
			}
		}
		return tmp;
	}

	/**
	 * indices and values hold candidates, that should be marked or set; the two
	 * lists are not necessarily of the same length. The method has to return all
	 * combinations of values and indices.
	 *
	 * @return A string containing all combinations of values and indices in library
	 *         format
	 */
	public String getValueIndexString() {
		final StringBuilder tmp = new StringBuilder();
		for (int i = 0; i < this.values.size(); i++) {
			final int value = this.values.get(i);
			for (int j = 0; j < this.indices.size(); j++) {
				final int index = this.indices.get(j);
				tmp.append(value);
				tmp.append(Integer.toString(Sudoku2.getLine(index) + 1));
				tmp.append(Integer.toString(Sudoku2.getCol(index) + 1));
				tmp.append(" ");
			}
		}
		return tmp.toString().trim();
	}

	public String getSingleCandidateString() {
		return this.getStepName() + ": " + getCompactCellPrint(this.indices) + "=" + this.values.get(0);
	}

	public String getCandidateString() {
		return this.getCandidateString(false, false);
	}

	public String getCandidateString(boolean library) {
		return this.getCandidateString(library, false);
	}

	public String getCandidateString(boolean library, boolean statistics) {
		Collections.sort(this.candidatesToDelete);
		this.eliminateDoubleCandidatesToDelete();
		final StringBuilder candBuff = new StringBuilder();
		int lastCand = -1;
		StringBuffer delPos = new StringBuffer();
		for (final Candidate cand : this.candidatesToDelete) {
			if (cand.getValue() != lastCand) {
				if (lastCand != -1) {
					candBuff.append("/");
				}
				candBuff.append(cand.getValue());
				lastCand = cand.getValue();
			}
			delPos.append(" ");
			if (library) {
				delPos.append(Integer.toString(cand.getValue()))
						.append(Integer.toString(Sudoku2.getLine(cand.getIndex()) + 1))
						.append(Integer.toString(Sudoku2.getCol(cand.getIndex()) + 1));
			}
		}
		if (library) {
			return delPos.toString().trim();
		} else {
			delPos = new StringBuffer();
			this.getCandidatesToDelete(delPos);
			delPos.delete(0, 4); // " => " entfernen
			if (statistics) {
				// return candBuff.toString() + " (" + candidatesToDelete.size() + ")" + "
				// (0/0)";
				return candBuff.toString() + " (" + this.getAnzCandidatesToDelete() + ")" + " (0/0)";
			} else {
				String tmpStepName = this.getStepName();
				if (this.isSiamese) {
					tmpStepName = java.util.ResourceBundle.getBundle("intl/SolutionStep")
							.getString("SolutionStep.siamese") + " " + this.getStepName();
				}
				// return candBuff.toString() + " (" + candidatesToDelete.size() + "):" +
				// delPos.toString() + " (" + tmpStepName + ")";
				return candBuff.toString() + " (" + this.getAnzCandidatesToDelete() + "):" + delPos.toString() + " ("
						+ tmpStepName + ")";
			}
		}
	}

	private void eliminateDoubleCandidatesToDelete() {
		final Set<Candidate> candSet = new TreeSet<Candidate>();
		for (int i = 0; i < this.candidatesToDelete.size(); i++) {
			candSet.add(this.candidatesToDelete.get(i));
		}
		this.candidatesToDelete.clear();
		for (final Candidate cand : candSet) {
			this.candidatesToDelete.add(cand);
		}
	}

	public static String getCellPrint(int index) {
		return getCellPrint(index, true);
	}

	public static String getCellPrint(int index, boolean withParen) {
		if (withParen) {
			return "[r" + (Sudoku2.getLine(index) + 1) + "c" + (Sudoku2.getCol(index) + 1) + "]";
		} else {
			return "r" + (Sudoku2.getLine(index) + 1) + "c" + (Sudoku2.getCol(index) + 1);
		}
	}

	public static String getCompactCellPrint(int index1, int index2, int index3) {
		final TreeSet<Integer> tmpSet = new TreeSet<Integer>();
		tmpSet.add(index1);
		tmpSet.add(index2);
		if (index3 != -1) {
			tmpSet.add(index3);
		}
		final String result = getCompactCellPrint(tmpSet);
		return result;
	}

	public static String getCompactCellPrint(SudokuSet set) {
		final TreeSet<Integer> tmpSet = new TreeSet<Integer>();
		for (int i = 0; i < set.size(); i++) {
			tmpSet.add(set.get(i));
		}
		final String result = getCompactCellPrint(tmpSet);
		return result;
	}

	public static String getCompactCellPrint(List<Integer> indices) {
		return getCompactCellPrint(indices, 0, indices.size() - 1);
	}

	public static String getCompactCellPrint(List<Integer> indices, int start, int end) {
		// Duplikate entfernen!
		final TreeSet<Integer> tmpSet = new TreeSet<Integer>();
		for (int i = start; i <= end; i++) {
			tmpSet.add(indices.get(i));
		}
		return getCompactCellPrint(tmpSet);
	}

	public static String getCompactCellPrint(TreeSet<Integer> tmpSet) {
		final StringBuilder tmp = new StringBuilder();
		boolean first = true;
		while (tmpSet.size() > 0) {
			final int index = tmpSet.pollFirst();
			final int line = Sudoku2.getLine(index);
			final int col = Sudoku2.getCol(index);
			int anzLines = 1;
			int anzCols = 1;
			if (first) {
				first = false;
			} else {
				tmp.append(",");
			}
			tmp.append(getCellPrint(index));
			final Iterator<Integer> it = tmpSet.iterator();
			while (it.hasNext()) {
				final int i1 = it.next();
				final int l1 = Sudoku2.getLine(i1);
				final int c1 = Sudoku2.getCol(i1);
				if (l1 == line && anzLines == 1) {
					// Spalte hinzufügen
					final int pIndex = tmp.lastIndexOf("]");
					tmp.insert(pIndex, c1 + 1);
					it.remove();
					anzCols++;
				} else if (c1 == col && anzCols == 1) {
					// Zeile hinzufügen
					final int pIndex = tmp.lastIndexOf("c");
					tmp.insert(pIndex, l1 + 1);
					it.remove();
					anzLines++;
				}
			}
		}
		int index = 0;
		while ((index = tmp.indexOf("[")) != -1) {
			tmp.deleteCharAt(index);
		}
		while ((index = tmp.indexOf("]")) != -1) {
			tmp.deleteCharAt(index);
		}
		return tmp.toString();
	}

	public final void setTechnique(SolutionTechnique technique) {
		this.technique = technique;
	}

	public void addValue(int value) {
		if (value < 1 || value > 9) {
			throw new RuntimeException(
					java.util.ResourceBundle.getBundle("intl/SolutionStep").getString("SolutionStep.invalid_setValue")
							+ " (" + value + ")");
		}
		this.values.add(value);
	}

	public void addIndex(int index) {
		if (index < 0 || index > 80) {
			throw new RuntimeException(
					java.util.ResourceBundle.getBundle("intl/SolutionStep").getString("SolutionStep.invalid_setIndex")
							+ " (" + index + ")");
		}
		this.indices.add(index);
	}

	public void addCandidateToDelete(Candidate cand) {
		this.candidatesToDelete.add(cand);
	}

	public void addCandidateToDelete(int index, int candidate) {
		this.candidatesToDelete.add(new Candidate(index, candidate));
	}

	public void addCannibalistic(Candidate cand) {
		this.cannibalistic.add(cand);
	}

	public void addCannibalistic(int index, int candidate) {
		this.cannibalistic.add(new Candidate(index, candidate));
	}

	public void addFin(int index, int candidate) {
		this.addFin(new Candidate(index, candidate));
	}

	public void addFin(Candidate fin) {
		this.fins.add(fin);
	}

	public void addEndoFin(int index, int candidate) {
		this.endoFins.add(new Candidate(index, candidate));
	}

	public int getAnzCandidatesToDelete() {
		SortedSet<Candidate> tmpSet = new TreeSet<Candidate>();
		for (int i = 0; i < this.candidatesToDelete.size(); i++) {
			tmpSet.add(this.candidatesToDelete.get(i));
		}
		final int anz = tmpSet.size();
		tmpSet.clear();
		tmpSet = null;
		return anz;
	}

	public int getAnzSet() {
		if (this.technique.isSingle()) {
			return 1;
		}
		if (this.technique == SolutionTechnique.FORCING_CHAIN
				|| this.technique == SolutionTechnique.FORCING_CHAIN_CONTRADICTION
				|| this.technique == SolutionTechnique.FORCING_CHAIN_VERITY
				|| this.technique == SolutionTechnique.FORCING_NET
				|| this.technique == SolutionTechnique.FORCING_NET_CONTRADICTION
				|| this.technique == SolutionTechnique.FORCING_NET_VERITY) {
			if (this.indices.size() > 0) {
				return 1;
			}
		}
		if (this.technique == SolutionTechnique.TEMPLATE_SET) {
			return this.indices.size();
		}
		return 0;
	}

	public SolutionTechnique getTechnique() {
		return this.technique;
	}

	public List<Integer> getValues() {
		return this.values;
	}

	public List<Integer> getIndices() {
		return this.indices;
	}

	public List<Candidate> getCandidatesToDelete() {
		return this.candidatesToDelete;
	}

	public List<Candidate> getCannibalistic() {
		return this.cannibalistic;
	}

	public List<Candidate> getFins() {
		return this.fins;
	}

	public List<Candidate> getEndoFins() {
		return this.endoFins;
	}

	public String getStepName() {
		return this.technique.getStepName();
	}

	public static String getStepName(SolutionTechnique type) {
		return type.getStepName();
	}

	public static String getStepName(int type) {
		return SolutionTechnique.values()[type].getStepName();
	}

	public String getEntityName(int name) {
		return entityNames[name];
	}

	public String getEntityShortName(int name) {
		return entityShortNames[name];
	}

	public String getEntityName() {
		return entityNames[this.entity];
	}

	public String getEntityName2() {
		return entityNames[this.entity2];
	}

	public String getEntityShortName() {
		return entityShortNames[this.entity];
	}

	public String getEntityShortNameNumber() {
		if (this.entity == Constants.CELL) {
			return getCellPrint(this.entityNumber, false);
		} else {
			return entityShortNames[this.entity] + Integer.toString(this.entityNumber + 1);
		}
	}

	public String getEntityShortName2() {
		return entityShortNames[this.entity2];
	}

	@Override
	public String toString() {
		return this.toString(2);
	}

	/**
	 * art == 0: Kurzform art == 1: Mittellang art == 2: ausführlich
	 *
	 * @param art
	 * @return
	 */
	public String toString(int art) {
		String str = null;
		int index = 0;
		StringBuffer tmp;
		switch (this.technique) {
		case FULL_HOUSE:
		case HIDDEN_SINGLE:
		case NAKED_SINGLE:
			index = this.indices.get(0);
			str = this.getStepName();
			if (art == 1) {
				str += ": " + this.values.get(0);
			} else if (art == 2) {
				str += ": " + getCellPrint(index, false) + "=" + this.values.get(0);
			}
			break;
		case HIDDEN_QUADRUPLE:
		case NAKED_QUADRUPLE:
		case HIDDEN_TRIPLE:
		case NAKED_TRIPLE:
		case LOCKED_TRIPLE:
		case HIDDEN_PAIR:
		case NAKED_PAIR:
		case LOCKED_PAIR:
			index = this.indices.get(0);
			str = this.getStepName();
			tmp = new StringBuffer(str);
			if (art >= 1) {
				tmp.append(": ");
				if (this.technique == SolutionTechnique.HIDDEN_PAIR || this.technique == SolutionTechnique.NAKED_PAIR
						|| this.technique == SolutionTechnique.LOCKED_PAIR) {
					tmp.append(this.values.get(0));
					tmp.append(",");
					tmp.append(this.values.get(1));
				} else if (this.technique == SolutionTechnique.HIDDEN_TRIPLE
						|| this.technique == SolutionTechnique.NAKED_TRIPLE
						|| this.technique == SolutionTechnique.LOCKED_TRIPLE) {
					tmp.append(this.values.get(0));
					tmp.append(",");
					tmp.append(this.values.get(1));
					tmp.append(",");
					tmp.append(this.values.get(2));
				} else if (this.technique == SolutionTechnique.HIDDEN_QUADRUPLE
						|| this.technique == SolutionTechnique.NAKED_QUADRUPLE) {
					tmp.append(this.values.get(0));
					tmp.append(",");
					tmp.append(this.values.get(1));
					tmp.append(",");
					tmp.append(this.values.get(2));
					tmp.append(",");
					tmp.append(this.values.get(3));
				}
			}
			if (art >= 2) {
				tmp.append(" ");
				tmp.append(java.util.ResourceBundle.getBundle("intl/SolutionStep").getString("SolutionStep.in"));
				tmp.append(" ");
				tmp.append(getCompactCellPrint(this.indices));
				this.getCandidatesToDelete(tmp);
			}
			str = tmp.toString();
			break;
		case LOCKED_CANDIDATES:
		case LOCKED_CANDIDATES_1:
		case LOCKED_CANDIDATES_2:
			str = this.getStepName();
			if (art >= 1) {
				str += ": " + this.values.get(0);
			}
			if (art >= 2) {
				str += " " + java.util.ResourceBundle.getBundle("intl/SolutionStep").getString("SolutionStep.in") + " "
						+ this.getEntityShortName() + this.getEntityNumber();
				tmp = new StringBuffer(str);
				this.getCandidatesToDelete(tmp);
				str = tmp.toString();
			}
			break;
		case SKYSCRAPER:
		case TWO_STRING_KITE:
		case DUAL_TWO_STRING_KITE:
			str = this.getStepName();
			if (art >= 1) {
				str += ": " + this.values.get(0);
			}
			if (art >= 2) {
				str += " " + java.util.ResourceBundle.getBundle("intl/SolutionStep").getString("SolutionStep.in") + " "
						+ getCompactCellPrint(this.indices, 0, 1);
				if (this.technique == SolutionTechnique.DUAL_TWO_STRING_KITE) {
					str += "/" + java.util.ResourceBundle.getBundle("intl/SolutionStep").getString("SolutionStep.in")
							+ " " + getCompactCellPrint(this.indices, 4, 5);
				}
				str += " ("
						+ java.util.ResourceBundle.getBundle("intl/SolutionStep").getString("SolutionStep.connected_by")
						+ " " + getCompactCellPrint(this.indices, 2, 3) + ")";
				tmp = new StringBuffer(str);
				this.getCandidatesToDelete(tmp);
				str = tmp.toString();
			}
			break;
		case EMPTY_RECTANGLE:
		case DUAL_EMPTY_RECTANGLE:
			str = this.getStepName();
			if (art >= 1) {
				str += ": " + this.values.get(0);
			}
			if (art >= 2) {
				str += " " + java.util.ResourceBundle.getBundle("intl/SolutionStep").getString("SolutionStep.in") + " "
						+ this.getEntityShortName() + this.getEntityNumber() + " ("
						+ getCompactCellPrint(this.indices, 0, 1);
				if (this.technique == SolutionTechnique.DUAL_EMPTY_RECTANGLE) {
					str += "/" + getCompactCellPrint(this.indices, 2, 3);
				}
				str += ")";
				tmp = new StringBuffer(str);
				this.getCandidatesToDelete(tmp);
				str = tmp.toString();
			}
			break;
		case W_WING:
			str = this.getStepName();
			if (art >= 1) {
				str += ": " + this.values.get(0) + "/" + this.values.get(1);
			}
			if (art >= 2) {
				tmp = new StringBuffer(str);
				tmp.append(" ");
				tmp.append(java.util.ResourceBundle.getBundle("intl/SolutionStep").getString("SolutionStep.in"));
				tmp.append(" ");
				tmp.append(getCompactCellPrint(this.indices, 0, 1));
				tmp.append(" ");
				tmp.append(
						java.util.ResourceBundle.getBundle("intl/SolutionStep").getString("SolutionStep.connected_by"));
				tmp.append(" ");
				tmp.append(this.values.get(1));
				tmp.append(" ");
				tmp.append(java.util.ResourceBundle.getBundle("intl/SolutionStep").getString("SolutionStep.in"));
				tmp.append(" ");
				this.getFinSet(tmp, this.fins, false);
				this.getCandidatesToDelete(tmp);
				str = tmp.toString();
			}
			break;
		case XY_WING:
		case XYZ_WING:
			str = this.getStepName();
			if (art >= 1) {
				str += ": " + this.values.get(0) + "/" + this.values.get(1);
			}
			if (art >= 2) {
				str += "/" + this.values.get(2) + " "
						+ java.util.ResourceBundle.getBundle("intl/SolutionStep").getString("SolutionStep.in") + " "
						+ getCompactCellPrint(this.indices);
				tmp = new StringBuffer(str);
				this.getCandidatesToDelete(tmp);
				str = tmp.toString();
			}
			break;
		case SIMPLE_COLORS:
		case SIMPLE_COLORS_TRAP:
		case SIMPLE_COLORS_WRAP:
		case MULTI_COLORS:
		case MULTI_COLORS_1:
		case MULTI_COLORS_2:
			str = this.getStepName();
			if (art >= 1) {
				str += ": " + this.values.get(0);
			}
			if (art >= 2) {
				tmp = new StringBuffer(str);
				this.getColorCellPrint(tmp);
				this.getCandidatesToDelete(tmp);
				str = tmp.toString();
			}
			break;
		case X_CHAIN:
		case XY_CHAIN:
		case REMOTE_PAIR:
		case TURBOT_FISH:
		case NICE_LOOP:
		case CONTINUOUS_NICE_LOOP:
		case DISCONTINUOUS_NICE_LOOP:
		case GROUPED_NICE_LOOP:
		case GROUPED_CONTINUOUS_NICE_LOOP:
		case GROUPED_DISCONTINUOUS_NICE_LOOP:
		case AIC:
		case GROUPED_AIC:
			str = this.getStepName();
			if (art >= 1) {
				if (this.technique == SolutionTechnique.REMOTE_PAIR) {
					str += ": " + this.values.get(0) + "/" + this.values.get(1);
				} else {
					str += ": " + this.getCandidatesToDeleteDigits();
				}
//                    if (type == SolutionTechnique.REMOTE_PAIR) {
//                        str += ": " + values.get(0) + "/" + values.get(1);
//                    } else if (type == SolutionTechnique.X_CHAIN || type == SolutionTechnique.XY_CHAIN) {
//                    //} else if (type == SolutionTechnique.X_CHAIN) {
//                        //str += ": " + values.get(0);
//                        str += ": " + candidatesToDelete.get(0).value;
//                    }
			}
			if (art >= 2) {
				final List<Chain> dummy1 = this.getChains();
				final StringBuffer tmpChain = this.getChainString(this.getChains().get(0));
				// adjust nice loop notation
				if (this.technique == SolutionTechnique.CONTINUOUS_NICE_LOOP
						|| this.technique == SolutionTechnique.GROUPED_CONTINUOUS_NICE_LOOP) {
					final Chain ch = this.getChains().get(0);
					int start = ch.getStart();
					int cellIndex = ch.getCellIndex(start);
					while (ch.getCellIndex(start) == cellIndex) {
						start++;
					}
					int end = ch.getEnd();
					cellIndex = ch.getCellIndex(end);
					while (ch.getCellIndex(end) == cellIndex) {
						end--;
					}
					end++;
					tmpChain.insert(0, ch.getCandidate(end) + "= ");
					tmpChain.append(" =").append(ch.getCandidate(start));
					// System.out.println(Chain.toString(ch.chain[start]) + "/" +
					// Chain.toString(ch.chain[ch.end]));
				}
				if (this.technique == SolutionTechnique.AIC || this.technique == SolutionTechnique.GROUPED_AIC
						|| this.technique == SolutionTechnique.XY_CHAIN) {
					final Chain ch = this.getChains().get(0);
					// System.out.println(Chain.toString(ch.chain[ch.start]) + "/" +
					// Chain.toString(ch.chain[ch.end]));
					tmpChain.insert(0, ch.getCandidate(ch.getStart()) + "- ");
					tmpChain.append(" -").append(ch.getCandidate(ch.getEnd()));
				}
				// str += " " + getChainString(getChains().get(0));
				str += " " + tmpChain;
				tmp = new StringBuffer(str);
				this.getCandidatesToDelete(tmp);
				str = tmp.toString();
			}
			break;
		case FORCING_CHAIN:
		case FORCING_CHAIN_CONTRADICTION:
		case FORCING_CHAIN_VERITY:
		case FORCING_NET:
		case FORCING_NET_CONTRADICTION:
		case FORCING_NET_VERITY:
			str = this.getStepName();
			if (art >= 1) {
				// Keine dezenten Hinweise bei Forcing Chains...
			}
			if (art >= 2) {
				if (this.technique == SolutionTechnique.FORCING_CHAIN_CONTRADICTION
						|| this.technique == SolutionTechnique.FORCING_NET_CONTRADICTION) {
					str += " " + java.util.ResourceBundle.getBundle("intl/SolutionStep").getString("SolutionStep.in")
							+ " " + this.getEntityShortNameNumber();
				} else {
					// str += " Verity";
				}
				if (this.indices.size() > 0) {
					str += " => " + getCellPrint(this.indices.get(0), false) + "=" + this.values.get(0);
				} else {
					tmp = new StringBuffer(str);
					this.getCandidatesToDelete(tmp);
					str = tmp.toString();
				}
				for (int i = 0; i < this.chains.size(); i++) {
					str += "\r\n  " + this.getForcingChainString(this.getChains().get(i));
				}
			}
			break;
		case UNIQUENESS_1:
		case UNIQUENESS_2:
		case UNIQUENESS_3:
		case UNIQUENESS_4:
		case UNIQUENESS_5:
		case UNIQUENESS_6:
		case HIDDEN_RECTANGLE:
		case AVOIDABLE_RECTANGLE_1:
		case AVOIDABLE_RECTANGLE_2:
			str = this.getStepName();
			if (art >= 1) {
				str += ": " + this.values.get(0) + "/" + this.values.get(1);
			}
			if (art >= 2) {
				str += " in " + getCompactCellPrint(this.indices);
				tmp = new StringBuffer(str);
				this.getCandidatesToDelete(tmp);
				str = tmp.toString();
			}
			break;
		case BUG_PLUS_1:
			str = this.getStepName();
			if (art >= 2) {
				tmp = new StringBuffer(str);
				this.getCandidatesToDelete(tmp);
				str = tmp.toString();
			}
			break;
		case X_WING:
		case SWORDFISH:
		case JELLYFISH:
		case SQUIRMBAG:
		case WHALE:
		case LEVIATHAN:
		case FINNED_X_WING:
		case FINNED_SWORDFISH:
		case FINNED_JELLYFISH:
		case FINNED_SQUIRMBAG:
		case FINNED_WHALE:
		case FINNED_LEVIATHAN:
		case SASHIMI_X_WING:
		case SASHIMI_SWORDFISH:
		case SASHIMI_JELLYFISH:
		case SASHIMI_SQUIRMBAG:
		case SASHIMI_WHALE:
		case SASHIMI_LEVIATHAN:
		case FRANKEN_X_WING:
		case FRANKEN_SWORDFISH:
		case FRANKEN_JELLYFISH:
		case FRANKEN_SQUIRMBAG:
		case FRANKEN_WHALE:
		case FRANKEN_LEVIATHAN:
		case FINNED_FRANKEN_X_WING:
		case FINNED_FRANKEN_SWORDFISH:
		case FINNED_FRANKEN_JELLYFISH:
		case FINNED_FRANKEN_SQUIRMBAG:
		case FINNED_FRANKEN_WHALE:
		case FINNED_FRANKEN_LEVIATHAN:
		case MUTANT_X_WING:
		case MUTANT_SWORDFISH:
		case MUTANT_JELLYFISH:
		case MUTANT_SQUIRMBAG:
		case MUTANT_WHALE:
		case MUTANT_LEVIATHAN:
		case FINNED_MUTANT_X_WING:
		case FINNED_MUTANT_SWORDFISH:
		case FINNED_MUTANT_JELLYFISH:
		case FINNED_MUTANT_SQUIRMBAG:
		case FINNED_MUTANT_WHALE:
		case FINNED_MUTANT_LEVIATHAN:
		case KRAKEN_FISH:
		case KRAKEN_FISH_TYPE_1:
		case KRAKEN_FISH_TYPE_2:
			tmp = new StringBuffer();
			if (this.isSiamese) {
				tmp.append(java.util.ResourceBundle.getBundle("intl/SolutionStep").getString("SolutionStep.siamese"))
						.append(" ");
			}
			tmp.append(this.getStepName());
			if (art >= 1) {
				if (this.technique.isKrakenFish()) {
					tmp.append(": ");
					this.getCandidatesToDelete(tmp);
					tmp.append("\r\n  ").append(this.subType.getStepName());
				}
				tmp.append(": ").append(this.values.get(0));
			}
			if (art >= 2) {
				tmp.append(" ");
				this.getEntities(tmp, this.baseEntities, true, false);
				tmp.append(" ");
				this.getEntities(tmp, this.coverEntities, true, true);
				// tmp.append(" Positionen: ");
				int displayMode = Options.getInstance().getFishDisplayMode();
				if (this.technique.isKrakenFish()) {
					// no statistics
					displayMode = 0;
				}
				switch (displayMode) {
				case 0:
					if (this.fins.size() > 0) {
						tmp.append(" ");
						this.getFins(tmp, false, true);
					}
					if (this.endoFins.size() > 0) {
						tmp.append(" ");
						this.getFins(tmp, true, true);
					}
					break;
				case 1:
					this.getFishStatistics(tmp, false);
					break;
				case 2:
					this.getFishStatistics(tmp, true);
					break;
				}
				if (!this.technique.isKrakenFish()) {
					this.getCandidatesToDelete(tmp);
				}
			}
			if (this.technique.isKrakenFish()) {
				for (int i = 0; i < this.chains.size(); i++) {
					tmp.append("\r\n  ").append(this.getChainString(this.chains.get(i)));
				}
			}
			str = tmp.toString();
			break;
		case SUE_DE_COQ:
			str = this.getStepName();
			tmp = new StringBuffer(str + ": ");
			if (art >= 1) {
				this.getIndexValueSet(tmp);
				str = tmp.toString();
			}
			if (art >= 2) {
				tmp.append(" (");
				this.getFinSet(tmp, this.fins);
				tmp.append(", ");
				this.getFinSet(tmp, this.endoFins);
				tmp.append(")");
				this.getCandidatesToDelete(tmp);
				str = tmp.toString();
			}
			break;
		case ALS_XZ:
			// Sets A und B stecken in AlsInSolutionStep, X ist eine 2-Elemente lange Chain,
			// alle Z stecken in fins
			str = this.getStepName();
			tmp = new StringBuffer(str + ": ");
			if (art >= 1) {
				tmp.append("A=");
				this.getAls(tmp, 0);
				str = tmp.toString();
			}
			if (art >= 2) {
				tmp.append(", B=");
				this.getAls(tmp, 1);
				tmp.append(", X=");
				this.getAlsXorZ(tmp, true);
				if (!this.fins.isEmpty()) {
					tmp.append(", Z=");
					this.getAlsXorZ(tmp, false);
				}
				this.getCandidatesToDelete(tmp);
				str = tmp.toString();
			}
			break;
		case ALS_XY_WING:
			// Sets A, B und C stecken in AlsInSolutionStep, alle Y und Z stecken in
			// endoFins, alle X stecken in fins
			str = this.getStepName();
			if (art == 1) {
				tmp = new StringBuffer(str + ": ");
				tmp.append("C=");
				this.getAls(tmp, 2);
				str = tmp.toString();
			}
			if (art >= 2) {
				tmp = new StringBuffer(str + ": ");
				tmp.append("A=");
				this.getAls(tmp, 0);
				tmp.append(", B=");
				this.getAls(tmp, 1);
				tmp.append(", C=");
				this.getAls(tmp, 2);
				tmp.append(", X,Y=");
				this.getAlsXorZ(tmp, true);
				tmp.append(", Z=");
				this.getAlsXorZ(tmp, false);
				this.getCandidatesToDelete(tmp);
				str = tmp.toString();
			}
			break;
		case ALS_XY_CHAIN:
			str = this.getStepName();
			if (this.restrictedCommons.isEmpty()) {
				// old code -> has to remain for correctly displaying saved files
				if (art == 1) {
					tmp = new StringBuffer(str + ": ");
					tmp.append(java.util.ResourceBundle.getBundle("intl/SolutionStep").getString("SolutionStep.start"))
							.append("=");
					this.getAls(tmp, 0);
					tmp.append(", ").append(
							java.util.ResourceBundle.getBundle("intl/SolutionStep").getString("SolutionStep.end"))
							.append("=");
					this.getAls(tmp, this.alses.size() - 1);
					str = tmp.toString();
				}
				if (art >= 2) {
					tmp = new StringBuffer(str + ": ");
					char alsChar = 'A';
					boolean first = true;
					for (int i = 0; i < this.alses.size(); i++) {
						if (first) {
							first = false;
						} else {
							tmp.append(", ");
						}
						tmp.append(alsChar++);
						tmp.append("=");
						this.getAls(tmp, i);
					}
					tmp.append(", RCs=");
					this.getAlsXorZ(tmp, true);
					tmp.append(", X=");
					this.getAlsXorZ(tmp, false);
					this.getCandidatesToDelete(tmp);
					str = tmp.toString();
				}
			} else {
				if (art == 1) {
					tmp = new StringBuffer(str + ": ");
					tmp.append(java.util.ResourceBundle.getBundle("intl/SolutionStep").getString("SolutionStep.start"))
							.append("=");
					this.getAls(tmp, 0);
					tmp.append(", ").append(
							java.util.ResourceBundle.getBundle("intl/SolutionStep").getString("SolutionStep.end"))
							.append("=");
					this.getAls(tmp, this.alses.size() - 1);
					str = tmp.toString();
				}
				if (art >= 2) {
					tmp = new StringBuffer(str + ": ");
					this.getCandidatesToDeleteDigits(tmp);
					tmp.append("- ");
					for (int i = 0; i < this.alses.size(); i++) {
						this.getAls(tmp, i);
						if (i < this.restrictedCommons.size()) {
							this.getRestrictedCommon(this.restrictedCommons.get(i), tmp);
						}
					}
					tmp.append(" -");
					this.getCandidatesToDeleteDigits(tmp);
					this.getCandidatesToDelete(tmp);
					str = tmp.toString();
				}
			}
			break;
		case DEATH_BLOSSOM:
			str = this.getStepName();
			tmp = new StringBuffer(str + ": ");
			if (art >= 1) {
				tmp.append(getCellPrint(this.indices.get(0)));
				str = tmp.toString();
			}
			if (art >= 2) {
				for (int i = 0; i < this.alses.size(); i++) {
					tmp.append(", ");
					this.getRestrictedCommon(this.restrictedCommons.get(i), tmp);
					this.getAls(tmp, i);
				}
				this.getCandidatesToDelete(tmp);
				str = tmp.toString();
			}
			break;
		case TEMPLATE_SET:
			str = this.getStepName();
			if (art == 1) {
				str += ": " + this.values.get(0);
			}
			if (art >= 2) {
				tmp = new StringBuffer(str + ": ");
				tmp.append(getCompactCellPrint(this.indices)).append("=").append(this.values.get(0));
				str = tmp.toString();
			}
			break;
		case TEMPLATE_DEL:
			str = this.getStepName();
			if (art >= 1) {
				// nichts zusätzlich ausgeben
			}
			if (art >= 2) {
				tmp = new StringBuffer(str + ": ");
				this.getCandidatesToDelete(tmp);
				str = tmp.toString();
			}
			break;
		case BRUTE_FORCE:
			str = this.getStepName();
			if (art == 1) {
				str += ": " + this.values.get(0);
			}
			if (art >= 2) {
				tmp = new StringBuffer(str + ": ");
				tmp.append(getCompactCellPrint(this.indices)).append("=").append(this.values.get(0));
				str = tmp.toString();
			}
			break;
		case INCOMPLETE:
			str = java.util.ResourceBundle.getBundle("intl/SolutionStep").getString("SolutionStep.incomplete_solution");
			break;
		case GIVE_UP:
			tmp = new StringBuffer();
			tmp.append(this.getStepName());
			if (art >= 1) {
				tmp.append(": ").append(
						java.util.ResourceBundle.getBundle("intl/SolutionStep").getString("SolutionStep.dont_know"));
			}
			str = tmp.toString();
			break;
		default:
			throw new RuntimeException(
					java.util.ResourceBundle.getBundle("intl/SolutionStep").getString("SolutionStep.invalid_type")
							+ " (" + this.technique + ")!");
		}
		return str;
	}

	/**
	 * Gets information about vertices, fins, eliminations...
	 *
	 * @param tmp
	 * @param cells
	 */
	private void getFishStatistics(StringBuffer tmp, boolean cells) {
		tmp.append(" ");
		final SudokuSet set = new SudokuSet();
		// Vertices: all indices minus potential cannibalistic eliminations
		for (int i = 0; i < this.indices.size(); i++) {
			set.add(this.indices.get(i));
		}
		set.andNot(this.potentialCannibalisticEliminations);
		this.appendFishData(tmp, set, "V", cells);
		// exo fins
		set.clear();
		for (int i = 0; i < this.fins.size(); i++) {
			set.add(this.fins.get(i).getIndex());
		}
		for (int i = 0; i < this.endoFins.size(); i++) {
			set.remove(this.endoFins.get(i).getIndex());
		}
		this.appendFishData(tmp, set, "XF", cells);
		// endo fins
		set.clear();
		for (int i = 0; i < this.endoFins.size(); i++) {
			set.add(this.endoFins.get(i).getIndex());
		}
		this.appendFishData(tmp, set, "NF", cells);
		// eventual eliminations
		set.clear();
		for (int i = 0; i < this.candidatesToDelete.size(); i++) {
			set.add(this.candidatesToDelete.get(i).getIndex());
		}
		this.appendFishData(tmp, set, "EE", cells);
		// cannibalistic eventual eliminations
		set.clear();
		for (int i = 0; i < this.cannibalistic.size(); i++) {
			set.add(this.cannibalistic.get(i).getIndex());
		}
		this.appendFishData(tmp, set, "CE", cells);
		// potential eliminations
		set.set(this.potentialEliminations);
		set.or(this.potentialCannibalisticEliminations);
		this.appendFishData(tmp, set, "PE", cells);
	}

	private void appendFishData(StringBuffer tmp, SudokuSet set, String prefix, boolean cells) {
		tmp.append(prefix);
		tmp.append("(");
		if (cells) {
			tmp.append(getCompactCellPrint(set));
		} else {
			tmp.append(FISH_FORMAT.format(set.size()));
		}
		tmp.append(") ");
	}

	private void getColorCellPrint(StringBuffer tmp) {
		tmp.append(" ");
		final StringBuffer[] bufs = new StringBuffer[Options.getInstance().getColoringColors().length];
		for (final int index : this.getColorCandidates().keySet()) {
			final int color = this.getColorCandidates().get(index);
			if (bufs[color] == null) {
				bufs[color] = new StringBuffer();
				bufs[color].append("(");
			} else {
				bufs[color].append(",");
			}
			bufs[color].append(getCellPrint(index, false));
		}
		for (int i = 0; i < bufs.length; i++) {
			if (bufs[i] != null) {
				bufs[i].append(")");
				if ((i % 2) != 0) {
					tmp.append(" / ");
				} else if (i > 0) {
					tmp.append(", ");
				}
				tmp.append(bufs[i]);
			}
		}
	}

	private void getAlsXorZ(StringBuffer tmp, boolean x) {
		// gemeinsame Kandidaten für AlsInSolutionStep-XZ stehen in fins,
		// restricted commons in endoFins
		final List<Candidate> list = x ? this.endoFins : this.fins;
		final TreeSet<Integer> cands = new TreeSet<Integer>();
		for (int i = 0; i < list.size(); i++) {
			cands.add(list.get(i).getValue());
		}
		boolean first = true;
		for (final int cand : cands) {
			if (first) {
				first = false;
			} else {
				tmp.append(",");
			}
			tmp.append(cand);
		}
	}

	public static String getAls(Als als) {
		return getAls(als, true);
	}

	public static String getAls(Als als, boolean withCandidates) {
		final StringBuilder tmp = new StringBuilder();
		final TreeSet<Integer> set = new TreeSet<Integer>();
		for (int i = 0; i < als.indices.size(); i++) {
			set.add(als.indices.get(i));
		}
		tmp.append(getCompactCellPrint(set));
		if (withCandidates) {
			// tmp.append(" - {");
			tmp.append(" {");
			final int[] cands = Sudoku2.POSSIBLE_VALUES[als.candidates];
			for (int i = 0; i < cands.length; i++) {
				tmp.append(cands[i]);
			}
//            for (int i = 0; i < als.candidates.size(); i++) {
//                tmp.append(als.candidates.get(i));
//            }
			tmp.append("}");
		}
		return tmp.toString();
	}

	public void getAls(StringBuffer tmp, int alsIndex) {
		this.getAls(tmp, alsIndex, true);
	}

	public void getAls(StringBuffer tmp, int alsIndex, boolean withCandidates) {
		final AlsInSolutionStep als = this.alses.get(alsIndex);
		tmp.append(getCompactCellPrint(als.getIndices()));
		if (withCandidates) {
			// tmp.append(" - {");
			tmp.append(" {");
			for (final Integer cand : als.getCandidates()) {
				tmp.append(cand);
			}
			tmp.append("}");
		}
	}

	private void getIndexValueSet(StringBuffer tmp) {
		tmp.append(getCompactCellPrint(this.indices));
		tmp.append(" - {");
		for (final Integer value : this.values) {
			tmp.append(value);
		}
		tmp.append("}");
	}

	/**
	 * Ein Eintrag pro betroffener Zelle und pro betroffenem Kandidaten -> beinhart
	 * Set verwenden!
	 */
	private void getFinSet(StringBuffer tmp, List<Candidate> fins) {
		this.getFinSet(tmp, fins, true);
	}

	private void getFinSet(StringBuffer tmp, List<Candidate> fins, boolean withCandidates) {
		final TreeSet<Integer> indexes = new TreeSet<Integer>();
		final TreeSet<Integer> candidates = new TreeSet<Integer>();
		for (final Candidate cand : fins) {
			indexes.add(cand.getIndex());
			candidates.add(cand.getValue());
		}
		// Alle indexe ausschließen, die in indices enthalten sind
		for (final int index : this.indices) {
			indexes.remove(index);
		}
		tmp.append(getCompactCellPrint(indexes));
		if (withCandidates) {
			tmp.append(" - {");
			for (final int value : candidates) {
				tmp.append(value);
			}
			tmp.append("}");
		}
	}

	public void getEntities(StringBuffer tmp, List<Entity> entities) {
		this.getEntities(tmp, entities, false);
	}

	public void getEntities(StringBuffer tmp, List<Entity> entities, boolean library) {
		this.getEntities(tmp, entities, library, false);
	}

	public void getEntities(StringBuffer tmp, List<Entity> entities, boolean library, boolean checkSiamese) {
		boolean first = true;
		if (!library) {
			tmp.append("(");
		}
		final int siameseIndex = entities.size() / 2 - 1;
		int lastEntityName = -1;
		int index = 0;
		for (final Entity act : entities) {
			if (first) {
				first = false;
			} else {
				if (!library) {
					tmp.append(", ");
				}
			}
			if (library) {
				if (lastEntityName != act.getEntityName()) {
					tmp.append(this.getEntityShortName(act.getEntityName()));
				}
				tmp.append(act.getEntityNumber());
			} else {
				tmp.append(this.getEntityName(act.getEntityName())).append(" ").append(act.getEntityNumber());
			}
			lastEntityName = act.getEntityName();
			if (checkSiamese && this.isSiamese && index == siameseIndex) {
				tmp.append("/");
				lastEntityName = -1;
			}
			index++;
		}
		if (!library) {
			tmp.append(")");
		}
	}

	private void getIndexes(StringBuffer tmp) {
		boolean first = true;
		for (final int index : this.indices) {
			if (first) {
				first = false;
			} else {
				tmp.append(", ");
			}
			tmp.append(getCellPrint(index, false));
		}
	}

	/**
	 * Calculates the String representation of an RC: -ARC- ARC are the actual RCs
	 * depending on the value of actualRC.
	 *
	 * @param rc  The Restricted Common to be displayed
	 * @param tmp Result is appended to tmp
	 */
	private void getRestrictedCommon(RestrictedCommon rc, StringBuffer tmp) {
		int anz = 0;
		tmp.append(" -");
		if (rc.getActualRC() == 1 || rc.getActualRC() == 3) {
			tmp.append(rc.getCand1());
			anz++;
		}
		if (rc.getActualRC() == 2 || rc.getActualRC() == 3) {
			tmp.append(rc.getCand2());
			anz++;
		}
		tmp.append("- ");
	}

	/**
	 * Returns all candidates that are deleted in this. Is needed for displaying ALS
	 * Chains (chain should start and end with the deleted candidates (no indices").
	 *
	 * @param tmp Result is appended to tmp
	 */
	private void getCandidatesToDeleteDigits(StringBuffer tmp) {
		final SortedSet<Integer> candSet = new TreeSet<Integer>();
		for (int i = 0; i < this.candidatesToDelete.size(); i++) {
			candSet.add(this.candidatesToDelete.get(i).getValue());
		}
		for (final int value : candSet) {
			tmp.append(value);
		}
	}

	/**
	 * Similar to {@link #getCandidatesToDeleteDigits(java.lang.StringBuffer) }, but
	 * inserts slashes between the digits
	 */
	private String getCandidatesToDeleteDigits() {
		final StringBuffer tmp = new StringBuffer();
		this.getCandidatesToDeleteDigits(tmp);
		final int compactLength = tmp.length();
		for (int i = 0; i < compactLength - 1; i++) {
			tmp.insert(i * 2 + 1, "/");
		}
		return tmp.toString();
	}

	private void getCandidatesToDelete(StringBuffer tmp) {
		tmp.append(" => ");
		@SuppressWarnings("unchecked")
		final ArrayList<Candidate> tmpList = (ArrayList<Candidate>) ((ArrayList<Candidate>) this.candidatesToDelete)
				.clone();
		boolean first = true;
		final ArrayList<Integer> candList = new ArrayList<Integer>();
		while (tmpList.size() > 0) {
			final Candidate firstCand = tmpList.remove(0);
			candList.clear();
			candList.add(firstCand.getIndex());
			final Iterator<Candidate> it = tmpList.iterator();
			while (it.hasNext()) {
				final Candidate c1 = it.next();
				if (c1.getValue() == firstCand.getValue()) {
					candList.add(c1.getIndex());
					it.remove();
				}
			}
			if (first) {
				first = false;
			} else {
				tmp.append(", ");
			}
			tmp.append(getCompactCellPrint(candList));
			tmp.append("<>");
			tmp.append(firstCand.getValue());
		}
	}

	public void getFins(StringBuffer tmp, boolean endo) {
		this.getFins(tmp, endo, false);
	}

	public void getFins(StringBuffer tmp, boolean endo, boolean library) {
		final List<Candidate> list = endo ? this.endoFins : this.fins;
		if (list.isEmpty()) {
			return;
		}
		if (!library) {
			if (list.size() == 1) {
				if (endo) {
					tmp.append(" ").append(java.util.ResourceBundle.getBundle("intl/SolutionStep")
							.getString("SolutionStep.endofin_in")).append(" ");
				} else {
					tmp.append(" ").append(
							java.util.ResourceBundle.getBundle("intl/SolutionStep").getString("SolutionStep.fin_in"))
							.append(" ");
				}
			} else {
				if (endo) {
					tmp.append(" ").append(java.util.ResourceBundle.getBundle("intl/SolutionStep")
							.getString("SolutionStep.endofins_in")).append(" ");
				} else {
					tmp.append(" ").append(
							java.util.ResourceBundle.getBundle("intl/SolutionStep").getString("SolutionStep.fins_in"))
							.append(" ");
				}
			}
		}
		final String finStr = endo ? "ef" : "f";
		boolean first = true;
		for (final Candidate cand : list) {
			if (first) {
				first = false;
			} else {
				if (library) {
					tmp.append(" ");
				} else {
					tmp.append(", ");
				}
			}
			if (library) {
				tmp.append(finStr).append(getCellPrint(cand.getIndex(), false));
			} else {
				tmp.append(getCellPrint(cand.getIndex(), false));
			}
		}
	}

	public int getEntity() {
		return this.entity;
	}

	public void setEntity(int entity) {
		this.entity = entity;
	}

	public int getEntityNumber() {
		return this.entityNumber;
	}

	public void setEntityNumber(int entityNumber) {
		this.entityNumber = entityNumber;
	}

	public int getEntity2() {
		return this.entity2;
	}

	public void setEntity2(int entity2) {
		this.entity2 = entity2;
	}

	public int getEntity2Number() {
		return this.entity2Number;
	}

	public void setEntity2Number(int entity2Number) {
		this.entity2Number = entity2Number;
	}

	public void addBaseEntity(int name, int number) {
		this.baseEntities.add(new Entity(name, number));
	}

	public void addBaseEntity(Entity e) {
		this.baseEntities.add(e);
	}

	public void addCoverEntity(int name, int number) {
		this.coverEntities.add(new Entity(name, number));
	}

	public void addCoverEntity(Entity e) {
		this.coverEntities.add(e);
	}

	public void addChain(int start, int end, int[] chain) {
		this.chains.add(new Chain(start, end, chain));
	}

	public void addChain(Chain chain) {
		chain.resetLength();
		this.chains.add(chain);
	}

	public List<Chain> getChains() {
		return this.chains;
	}

	public int getChainLength() {
		int length = 0;
		for (int i = 0; i < this.chains.size(); i++) {
			length += this.chains.get(i).getLength(this.alses);
		}
		return length;
	}

	public int getChainAnz() {
		return this.chains.size();
	}

	public boolean isNet() {
		if (this.chains.size() > 0) {
			for (int i = 0; i < this.chains.size(); i++) {
				final Chain tmp = this.chains.get(i);
				for (int j = tmp.getStart(); j <= tmp.getEnd(); j++) {
					if (tmp.getChain()[j] < 0) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public int getAlsesIndexCount() {
		int count = 0;
		for (final AlsInSolutionStep als : this.alses) {
			count += als.getIndices().size();
		}
		return count;
	}

	public List<AlsInSolutionStep> getAlses() {
		return this.alses;
	}

	public AlsInSolutionStep getAls(int index) {
		return this.alses.get(index);
	}

	public void addAls(AlsInSolutionStep newAls) {
		this.alses.add(newAls);
	}

	public void addAls(SudokuSet indices, SudokuSet candidates) {
		final AlsInSolutionStep als = new AlsInSolutionStep();
		for (int i = 0; i < indices.size(); i++) {
			als.addIndex(indices.get(i));
		}
		for (int i = 0; i < candidates.size(); i++) {
			als.addCandidate(candidates.get(i));
		}
		this.alses.add(als);
	}

	public void addAls(SudokuSet indices, short candidates) {
		final AlsInSolutionStep als = new AlsInSolutionStep();
		for (int i = 0; i < indices.size(); i++) {
			als.addIndex(indices.get(i));
		}
		final int[] cands = Sudoku2.POSSIBLE_VALUES[candidates];
		for (int i = 0; i < cands.length; i++) {
			als.addCandidate(cands[i]);
		}
		this.alses.add(als);
	}

	public void addRestrictedCommon(RestrictedCommon rc) {
		this.restrictedCommons.add(rc);
	}

	public int getAlsIndex(int index, int chainIndex) {
		if (chainIndex == -1) {
			for (int i = 0; i < this.alses.size(); i++) {
				if (this.alses.get(i).getIndices().contains(index)) {
					return i;
				}
			}
		} else {
			final Chain chain = this.chains.get(chainIndex);
			for (int i = chain.getStart(); i <= chain.getEnd(); i++) {
				if (chain.getNodeType(i) == Chain.ALS_NODE) {
					final int alsIndex = Chain.getSAlsIndex(chain.getChain()[i]);
					final AlsInSolutionStep als = this.alses.get(alsIndex);
					if (als.getIndices().contains(index)) {
						return alsIndex;
					}
				}
			}
		}
		return -1;
	}

	public void addColorCandidate(int index, int color) {
		this.getColorCandidates().put(index, color);
	}

	public void addColorCandidates(SudokuSet indices, int color) {
		for (int i = 0; i < indices.size(); i++) {
			this.addColorCandidate(indices.get(i), color);
		}
	}

	public boolean isEqual(PuzzleSolutionStep s) {
		if (!this.isEquivalent(s)) {
			return false;
		}

		if (!this.isEqualInteger(this.values, s.values)) {
			return false;
		}
		if (!this.isEqualInteger(this.indices, s.indices)) {
			return false;
		}
		if (!this.isEqualCandidate(this.fins, s.fins)) {
			return false;
		}

		return true;
	}

	/**
	 * Zwei Steps sind äquivalent, wenn sie die gleichen zu löschenden Kandidaten
	 * bewirken (oder die gleichen Kandidaten setzen).
	 *
	 * 20081013: Problems with AllStepsPanel, so new try: two steps cannot be equal,
	 * if they have not the same SolutionTechnique Exception: both steps are fish
	 * 20120112: All steps handled specially in compareTo() are to be treated as
	 * equivalent!
	 *
	 * @param s
	 * @return
	 */
	public boolean isEquivalent(PuzzleSolutionStep s) {
		if (this.technique.isFish() && s.getTechnique().isFish()) {
			return true;
		}
		if (this.technique.isKrakenFish() && s.getTechnique().isKrakenFish()) {
			return true;
		}
		if (this.getTechnique() != s.getTechnique()) {
			return false;
		}

		if (this.candidatesToDelete.size() > 0) {
			return this.isEqualCandidate(this.candidatesToDelete, s.candidatesToDelete);
		}
		return this.isEqualInteger(this.indices, s.indices);
	}

	/**
	 * Der aktuelle Step ist eun Substep des übergebenen Steps, wenn alle zu
	 * löschenden Kandidaten auch im übergebenen Step enthalten sind.
	 *
	 * @param s
	 * @return
	 */
	public boolean isSubStep(PuzzleSolutionStep s) {
		if (s.candidatesToDelete.size() < this.candidatesToDelete.size()) {
			// hat weniger Kandidaten -> kann nicht sein
			return false;
		}
		for (final Candidate cand : this.candidatesToDelete) {
			if (!s.candidatesToDelete.contains(cand)) {
				return false;
			}
		}
		return true;
	}

	public boolean isSingle() {
		return this.isSingle(this.technique);
	}

	public boolean isSingle(SolutionTechnique type) {
		return (type == SolutionTechnique.FULL_HOUSE || type == SolutionTechnique.HIDDEN_SINGLE
				|| type == SolutionTechnique.NAKED_SINGLE || type == SolutionTechnique.TEMPLATE_SET);
	}

	public boolean isForcingChainSet() {
		if ((this.technique == SolutionTechnique.FORCING_CHAIN
				|| this.technique == SolutionTechnique.FORCING_CHAIN_CONTRADICTION
				|| this.technique == SolutionTechnique.FORCING_CHAIN_VERITY) && this.indices.size() > 0) {
			return true;
		}
		if ((this.technique == SolutionTechnique.FORCING_NET
				|| this.technique == SolutionTechnique.FORCING_NET_CONTRADICTION
				|| this.technique == SolutionTechnique.FORCING_NET_VERITY) && this.indices.size() > 0) {
			return true;
		}
		return false;
	}

	public int compareChainLengths(PuzzleSolutionStep other) {
		return this.getChainLength() - other.getChainLength();
	}

	@Override
	public int compareTo(PuzzleSolutionStep other) {
		int sum1 = 0, sum2 = 0;

		if (this.isSingle(this.technique) && !this.isSingle(other.technique)) {
			return -1;
		} else if (!this.isSingle(this.technique) && this.isSingle(other.technique)) {
			return 1;
		}

		final int result = other.candidatesToDelete.size() - this.candidatesToDelete.size();
		if (result != 0) {
			return result;
		}

		if (!this.isEquivalent(other)) {
			sum1 = this.getIndexSum(this.candidatesToDelete);
			sum2 = this.getIndexSum(other.candidatesToDelete);
			return (sum1 - sum2);
		}

		// SPECIAL STEPS
		// fish general: sort for
		// - fish type
		// - fish size
		// - cannibalism
		// - number of endo fins
		// - number of fins
		if (this.technique.isFish() && other.getTechnique().isFish()) {
			int ret = this.technique.compare(other.getTechnique());
			if (ret != 0) {
				// different type or different size
				return ret;
			}
			ret = this.getCannibalistic().size() - other.getCannibalistic().size();
			if (ret != 0) {
				return ret;
			}
			ret = this.getEndoFins().size() - other.getEndoFins().size();
			if (ret != 0) {
				return ret;
			}
			ret = this.getFins().size() - other.getFins().size();
			if (ret != 0) {
				return ret;
			}
			if (!this.isEqualInteger(this.values, other.values)) {
				sum1 = this.getSum(this.values);
				sum2 = this.getSum(other.values);
				return sum1 - sum2;
			}
			return 0;
		}

		// kraken fish: sort for (fish type, chain length)
		if (this.technique.isKrakenFish() && other.getTechnique().isKrakenFish()) {
			final int ret = this.subType.compare(other.getSubType());
			if (ret != 0) {
				return ret;
			}
			return this.compareChainLengths(other);
		}

		final int chainDiff = this.compareChainLengths(other);
		if (chainDiff != 0) {
			return chainDiff;
		}

		if (!this.isEqualInteger(this.values, other.values)) {
			sum1 = this.getSum(this.values);
			sum2 = this.getSum(other.values);
			return sum1 - sum2;
		}

		if (!this.isEqualInteger(this.indices, other.indices)) {
			if (this.indices.size() != other.indices.size()) {
				return this.indices.size() - other.indices.size();
			}
			sum1 = this.getSum(this.indices);
			sum2 = this.getSum(other.indices);
			return sum2 - sum1;
		}

		return this.technique.compare(other.getTechnique());
	}

	public boolean isEqualValues(PuzzleSolutionStep other) {
		return this.isEqualInteger(this.values, other.getValues());
	}

	private boolean isEqualInteger(List<Integer> l1, List<Integer> l2) {
		if (l1.size() != l2.size()) {
			return false;
		}
		final int anz = l1.size();
		for (int i = 0; i < anz; i++) {
			final int i1 = l1.get(i);
			boolean found = false;
			for (int j = 0; j < anz; j++) {
				final int i2 = l2.get(j);
				if (i1 == i2) {
					found = true;
					break;
				}
			}
			if (!found) {
				return false;
			}
		}
		return true;
	}

	public boolean isEqualCandidate(PuzzleSolutionStep other) {
		return this.isEqualCandidate(this.candidatesToDelete, other.getCandidatesToDelete());
	}

	private boolean isEqualCandidate(List<Candidate> l1, List<Candidate> l2) {
		if (l1.size() != l2.size()) {
			return false;
		}
		final int anz = l1.size();
		for (int i = 0; i < anz; i++) {
			final Candidate c1 = l1.get(i);
			boolean found = false;
			for (int j = 0; j < anz; j++) {
				final Candidate c2 = l2.get(j);
				if (c1.getIndex() == c2.getIndex() && c1.getValue() == c2.getValue()) {
					found = true;
					break;
				}
			}
			if (!found) {
				return false;
			}
		}
		return true;
	}

	/**
	 * The sum of the indices of a collection of candidates is used as a sorting
	 * criteria. For this to work, the indices have to be weighted or else two
	 * combinations of different indices could lead to the same sum.<br>
	 * <br>
	 *
	 * @param list
	 * @return
	 */
	public int getIndexSum(List<Candidate> list) {
		int sum = 0;
		int offset = 1;
		for (int i = 0; i < list.size(); i++) {
			sum += list.get(i).getIndex() * offset + list.get(i).getValue();
			offset += 80;
		}
		return sum;
	}

	public int getSum(List<Integer> list) {
		int sum = 0;
		for (int i = 0; i < list.size(); i++) {
			sum += list.get(i);
		}
		return sum;
	}

	public int compareCandidatesToDelete(PuzzleSolutionStep other) {
		final int size1 = this.candidatesToDelete.size();
		final int size2 = other.candidatesToDelete.size();
		if (size1 != size2) {
			return size2 - size1;
		}
		int result = 0;
		for (int i = 0; i < size1; i++) {
			final Candidate c1 = this.candidatesToDelete.get(i);
			final Candidate c2 = other.candidatesToDelete.get(i);
			result = (c1.getIndex() * 10 + c1.getValue()) - (c2.getIndex() * 10 + c2.getValue());
			if (result != 0) {
				return result;
			}
		}
		return 0;
	}

	public List<Entity> getBaseEntities() {
		return this.baseEntities;
	}

	public List<Entity> getCoverEntities() {
		return this.coverEntities;
	}

	public void setValues(List<Integer> values) {
		this.values = values;
	}

	public void setIndices(List<Integer> indices) {
		this.indices = indices;
	}

	public void setCandidatesToDelete(List<Candidate> candidatesToDelete) {
		this.candidatesToDelete = candidatesToDelete;
	}

	public void setCannibalistic(List<Candidate> cannibalistic) {
		this.cannibalistic = cannibalistic;
	}

	public void setFins(List<Candidate> fins) {
		this.fins = fins;
	}

	public void setEndoFins(List<Candidate> endoFins) {
		this.endoFins = endoFins;
	}

	public void setBaseEntities(List<Entity> baseEntities) {
		this.baseEntities = baseEntities;
	}

	public void setCoverEntities(List<Entity> coverEntities) {
		this.coverEntities = coverEntities;
	}

	public void setChains(List<Chain> chains) {
		this.chains = chains;
	}

	public void setAlses(List<AlsInSolutionStep> alses) {
		this.alses = alses;
	}

	public SortedMap<Integer, Integer> getColorCandidates() {
		return this.colorCandidates;
	}

	public void setColorCandidates(SortedMap<Integer, Integer> colorCandidates) {
		this.colorCandidates = colorCandidates;
	}

	public SolutionTechnique getSubType() {
		return this.subType;
	}

	public void setSubType(SolutionTechnique subType) {
		this.subType = subType;
	}

	public boolean isIsSiamese() {
		return this.isSiamese;
	}

	public void setIsSiamese(boolean isSiamese) {
		this.isSiamese = isSiamese;
	}

	public List<RestrictedCommon> getRestrictedCommons() {
		return this.restrictedCommons;
	}

	public void setRestrictedCommons(List<RestrictedCommon> restrictedCommons) {
		this.restrictedCommons = restrictedCommons;
	}

	public int getProgressScoreSingles() {
		return this.progressScoreSingles;
	}

	public void setProgressScoreSingles(int progressScoreSingles) {
		this.progressScoreSingles = progressScoreSingles;
	}

	public int getProgressScoreSinglesOnly() {
		return this.progressScoreSinglesOnly;
	}

	public void setProgressScoreSinglesOnly(int progressScoreSinglesOnly) {
		this.progressScoreSinglesOnly = progressScoreSinglesOnly;
	}

	public int getProgressScore() {
		return this.progressScore;
	}

	public void setProgressScore(int progressScore) {
		this.progressScore = progressScore;
	}

	public SudokuSet getPotentialCannibalisticEliminations() {
		return this.potentialCannibalisticEliminations;
	}

	public void setPotentialCannibalisticEliminations(SudokuSet potentialCannibalisticEliminations) {
		this.potentialCannibalisticEliminations = potentialCannibalisticEliminations;
	}

	public SudokuSet getPotentialEliminations() {
		return this.potentialEliminations;
	}

	public void setPotentialEliminations(SudokuSet potentialEliminations) {
		this.potentialEliminations = potentialEliminations;
	}
}
