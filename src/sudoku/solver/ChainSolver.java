package sudoku.solver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import sudoku.Candidate;
import sudoku.Chain;
import sudoku.Options;
import sudoku.SolutionStep;
import sudoku.SolutionType;
import sudoku.Sudoku2;
import sudoku.SudokuSet;
import sudoku.SudokuSetBase;

/**
 * Searches for simple chains: Remote Pairs, Turbot Fish, X-Chain and XY-Chain.
 *
 * @author hobiwan
 */
public class ChainSolver extends AbstractSolver {

	/** A simple chain without overlap cant have more links than 2 * cells */
	private static final int MAX_CHAIN_LENGTH = 2 * Sudoku2.LENGTH;
	/** X-Chains: Only links for one candidate, arbitrary length */
	private static final int X_CHAIN = 0;
	/** XY-Chains: Only bivalue cells, all links between the cells are weak */
	private static final int XY_CHAIN = 1;
	/** Remote Pairs: Only linke between bivalue cells with the same candidates */
	private static final int REMOTE_PAIR = 2;
	/**
	 * Nice Loops: Chains that link back to the start cell. Currently handled by
	 * {@link TablingSolver}.
	 */
	private static final int NICE_LOOP = 3;
	/** Turbot Fish: X-Chain with length 5 */
	private static final int TURBOT_FISH = 4;
	/** A custom comparator for chains */
	private static ChainComparator chainComparator = null;

	/** An entry in the recursion stack */
	class StackEntry {
		/** The cell index of the current link */
		int cellIndex;
		/** The candidate of the current link */
		int candidate;
		/** If <code>true</code>, the next link must be strong */
		boolean strongOnly;
		/** The current index into {@link #links}. */
		int aktIndex;
		/** The index of the last {@link #links link} for that cell + 1. */
		int endIndex;
	}

	/** The recursion stack */
	private final StackEntry[] stack = new StackEntry[MAX_CHAIN_LENGTH];
	/** The index in {@link #stack} and {@link #chain}. */
	private int stackLevel;
	/** The maximum length of the chain in number of links */
	private int chainMaxLength;
	/** A set for all candidates that can see the start cell (low oder dword) */
	private long startCellSetM1;
	/** A set for all candidates that can see the start cell (high oder dword) */
	private long startCellSetM2;
	/**
	 * A set for all candidates that can see the start cell (alternative candidate
	 * for Remote Pair) (low oder dword)
	 */
	private long startCellSet2M1;
	/**
	 * A set for all candidates that can see the start cell (alternative candidate
	 * for Remote Pair) (high oder dword)
	 */
	private long startCellSet2M2;

	/**
	 * Array containing all links for all cells an candidates. the links for one
	 * specific candidate start at links[{@link #startIndices}] and end at
	 * links[{@link #endIndices} - 1].
	 */
	private final int[] links = new int[20000];
	/**
	 * The start indices in {@link #links} for all cell/candidate combinations
	 * (index cellIndex * 10 + candidate).
	 */
	private final int[] startIndices = new int[810];
	/**
	 * The end indices + 1 in {@link #links} for all cell/candidate combinations
	 * (index cellIndex * 10 + candidate).
	 */
	private final int[] endIndices = new int[810];
	/** One global chain, is copied if a chain is actually found */
	private final int[] chain = new int[MAX_CHAIN_LENGTH];
	/**
	 * A set containing all cells from {@link #chain} (for loop/lasso check). The
	 * last cell from {@link #chain} can be missing.
	 */
	private final SudokuSet chainSet = new SudokuSet();
	/** The index of the cell from {@link #chain chain[0]}. */
	private int startIndex = 0;
	/** The start candidate of the chain */
	private int startCandidate = 0;
	/** The second start candidate for Remote Pairs */
	private int startCandidate2 = 0;
	/** The candidates of the start cell of a Remote Pair */
	private int rpCell = 0;
	/** Check for candidates to delete */
	private final SudokuSet checkBuddies = new SudokuSet();
	/** Additional checks for Remote Pairs */
	private final SudokuSet rpCand1 = new SudokuSet();
	/** Additional checks for Remote Pairs */
	private final SudokuSet rpCand2 = new SudokuSet();
	/** Additional checks for Remote Pairs */
	private final SudokuSet rpTmp = new SudokuSet();
	/**
	 * Contains all chains that have already be found. "String" are the
	 * eliminations, "Integer" is the chain length
	 */
	private final SortedMap<String, Integer> deletesMap = new TreeMap<String, Integer>();
	/** One global step for optimization */
	private final SolutionStep globalStep = new SolutionStep(SolutionTechnique.FULL_HOUSE);
	/** A list for all chain steps */
	private List<SolutionStep> steps;
	/** The last {@link SudokuStepFinder#stepNumber} for caching links */
	private int lastStepNumber = -1;
	/** Will be set at the beginning of each search */
	private boolean turbotOrXSeen;

	/** For timing */
	private long linkTNanos;
	/** For timing */
	private long chainTNanos;
	/** For timing */
	private long linkRNanos;
	/** For timing */
	private long chainRNanos;
	/** For timing */
	private long linkXNanos;
	/** For timing */
	private long chainXNanos;
	/** For timing */
	private long linkYNanos;
	/** For timing */
	private long chainYNanos;
	/** For timing */
	private int anzT;
	/** For timing */
	private int anzR;
	/** For timing */
	private int anzX;
	/** For timing */
	private int anzY;

	static {
		chainComparator = new ChainComparator();
	}

	/**
	 * Creates a new instance of ChainSolver
	 *
	 * @param finder
	 */
	public ChainSolver(SudokuStepFinder finder) {
		super(finder);
		for (int i = 0; i < this.stack.length; i++) {
			this.stack[i] = new StackEntry();
		}
	}

	@Override
	protected SolutionStep getStep(SolutionTechnique type) {
		SolutionStep result = null;
		this.sudoku = this.finder.getSudoku();
		switch (type) {
		case X_CHAIN:
			result = this.getXChains();
			break;
		case XY_CHAIN:
			result = this.getXYChains();
			break;
		case REMOTE_PAIR:
			result = this.getRemotePairs();
			break;
		case TURBOT_FISH:
			result = this.getTurbotChains();
			break;
//            case NICE_LOOP:
//            case CONTINUOUS_NICE_LOOP:
//            case DISCONTINUOUS_NICE_LOOP:
//                result = getNiceLoops();
//                break;
		}
		return result;
	}

	@Override
	protected boolean doStep(SolutionStep step) {
		boolean handled = true;
		this.sudoku = this.finder.getSudoku();
		switch (step.getType()) {
		case X_CHAIN:
		case XY_CHAIN:
		case REMOTE_PAIR:
		case NICE_LOOP:
		case TURBOT_FISH:
//            case CONTINUOUS_NICE_LOOP:
//            case DISCONTINUOUS_NICE_LOOP:
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
	 * Get an X-Chain. All chains are found and sorted, one is returned
	 *
	 * @return
	 */
	private SolutionStep getXChains() {
		this.steps = new ArrayList<SolutionStep>();
		this.getChains(X_CHAIN);
		if (this.steps.size() > 0) {
			Collections.sort(this.steps);
			return this.steps.get(0);
		}
		return null;
	}

	/**
	 * Get a Turbot Fish. All chains are found and sorted, one is returned
	 *
	 * @return
	 */
	private SolutionStep getTurbotChains() {
		this.steps = new ArrayList<SolutionStep>();
		this.getChains(TURBOT_FISH);
		if (this.steps.size() > 0) {
			Collections.sort(this.steps);
			return this.steps.get(0);
		}
		return null;
	}

	/**
	 * Get an XY-Chain. All chains are found and sorted, one is returned
	 *
	 * @return
	 */
	private SolutionStep getXYChains() {
		this.steps = new ArrayList<SolutionStep>();
		this.getChains(XY_CHAIN);
		if (this.steps.size() > 0) {
			Collections.sort(this.steps);
			return this.steps.get(0);
		}
		return null;
	}

	/**
	 * Get a Remote Pair. All chains are found and sorted, one is returned
	 *
	 * @return
	 */
	private SolutionStep getRemotePairs() {
		this.steps = new ArrayList<SolutionStep>();
		this.getChains(REMOTE_PAIR);
		if (this.steps.size() > 0) {
			Collections.sort(this.steps);
			return this.steps.get(0);
		}
		return null;
	}
//    private SolutionStep getNiceLoops() {
//        steps = new ArrayList<SolutionStep>();
//        getChains(NICE_LOOP);
//        if (steps.size() > 0) {
//            Collections.sort(steps);
//            return steps.get(0);
//        }
//        return null;
//    }

	/**
	 * Convenience method: Finds all simple chains and stores them a list-
	 *
	 * @return
	 */
	protected List<SolutionStep> getAllChains() {
		this.sudoku = this.finder.getSudoku();
		List<SolutionStep> tmpSteps = new ArrayList<SolutionStep>();
		tmpSteps = this.getAllChains(tmpSteps);
		Collections.sort(tmpSteps, chainComparator);
		return tmpSteps;
	}

	/**
	 * Gets all simple chains and stores them in <code>allSteps</code>.
	 *
	 * @param allSteps
	 * @return
	 */
	private List<SolutionStep> getAllChains(List<SolutionStep> allSteps) {
		// initialisieren
		long ticks = System.currentTimeMillis();
		this.steps = new ArrayList<SolutionStep>();
		allSteps.clear();

		this.getChains(TURBOT_FISH);
		Collections.sort(this.steps);

		this.getChains(X_CHAIN);
		Collections.sort(this.steps);

		this.getChains(XY_CHAIN);
		Collections.sort(this.steps);

		this.getChains(REMOTE_PAIR);
		Collections.sort(this.steps);

		allSteps.addAll(this.steps);

//        getChains(NICE_LOOP);
//        Collections.sort(this.steps);
		ticks = System.currentTimeMillis() - ticks;
		Logger.getLogger(this.getClass().getName()).log(Level.FINE, "getAllChains() gesamt: {0}ms", ticks);

		return allSteps;
	}

	/**
	 * Iterates over all candidates in the sudoku and does a complete search for
	 * simple chains (up to a given maximum length). Collects all links for the
	 * <code>type</code> of chain and then performes the search.
	 *
	 * @param typ
	 */
	private void getChains(int type) {
//        System.out.println("getChains(" + type + ")");
		final long nanos = System.nanoTime();
		this.getAllLinks(type);

		// calculate maximum length of chain
		this.chainMaxLength = MAX_CHAIN_LENGTH - 1;
		if (Options.getInstance().isRestrictChainSize()) {
			if (type == NICE_LOOP) {
				this.chainMaxLength = Options.getInstance().getRestrictNiceLoopLength();
			} else {
				this.chainMaxLength = Options.getInstance().getRestrictChainLength();
			}
		}
		if (type == TURBOT_FISH) {
			this.chainMaxLength = 3;
		}

		this.deletesMap.clear();
		// checkLoopSetsIndex = 0;
		// CAUTION: Only for testing! MUST BE FALSE IN PRODUCTION RELEASES!
		final boolean onlyOne = false;
		if (onlyOne && type != TURBOT_FISH) {
			return;
		}
		// startIndex is an attribute of the class
		for (this.startIndex = 0; this.startIndex < this.sudoku.getCells().length; this.startIndex++) {
			if (this.sudoku.getValue(this.startIndex) != 0) {
				// cell already set -> no chain possible
				continue;
			}
			if (onlyOne && this.startIndex != 10) {
				continue;
			}
			final int[] startCandidates = this.sudoku.getAllCandidates(this.startIndex);
			for (int i = 0; i < startCandidates.length; i++) {
				this.startCandidate = startCandidates[i];
				if (onlyOne && this.startCandidate != 1) {
					continue;
				}
				final int linkStartIndex = this.startIndex * 10 + this.startCandidate;
//                System.out.println("Links: " + linkStartIndex + "/" + startIndices[linkStartIndex] + "/" + endIndices[linkStartIndex]);
				for (int linkIndex = this.startIndices[linkStartIndex]; linkIndex < this.endIndices[linkStartIndex]; linkIndex++) {
					if ((type == X_CHAIN || type == XY_CHAIN || type == REMOTE_PAIR || type == TURBOT_FISH)
							&& !Chain.isSStrong(this.links[linkIndex])) {
						// dont start with weak link
						continue;
					}
					if ((type == X_CHAIN || type == TURBOT_FISH)
							&& Chain.getSCandidate(this.links[linkIndex]) != this.startCandidate) {
						// link is a different candidate -> no X-Chain possible
						continue;
					}
					if ((type == XY_CHAIN || type == REMOTE_PAIR)
							&& this.sudoku.getAnzCandidates(Chain.getSCellIndex(this.links[linkIndex])) != 2) {
						// cell is not bivalue -> no XY-Chain/RP possible
						continue;
					}
					if ((type == XY_CHAIN || type == REMOTE_PAIR)
							&& Chain.getSCellIndex(this.links[linkIndex]) != this.startIndex) {
						// The first strong link must be within the cell
						continue;
					}
//                        if (typ == NICE_LOOP && (links[k] / 10) % 100 == i) {
//                            // bei NICE_LOOPS muss der erste Link aus der Zelle herausgehen, sonst gibt es
//                            // Doppeldeutigkeiten
//                            continue;
//                        }
					if (type == REMOTE_PAIR) {
						this.rpCell = this.sudoku.getCell(this.startIndex);
						// get the second candidate from the cell (RP is for both candidates)
						final int[] cands = this.sudoku.getAllCandidates(this.startIndex);
						if (cands[0] != this.startCandidate) {
							this.startCandidate2 = cands[0];
						} else {
							this.startCandidate2 = cands[1];
						}
					}
					this.stackLevel = 1;
					this.chain[0] = Chain.makeSEntry(this.startIndex, this.startCandidate, false);
					this.chain[1] = this.links[linkIndex];
					final StackEntry entry = this.stack[this.stackLevel];
					entry.cellIndex = Chain.getSCellIndex(this.chain[1]);
					entry.candidate = Chain.getSCandidate(this.chain[1]);
					entry.strongOnly = !Chain.isSStrong(this.chain[1]);
					entry.aktIndex = this.startIndices[entry.cellIndex * 10 + entry.candidate];
					entry.endIndex = this.endIndices[entry.cellIndex * 10 + entry.candidate];
					this.chainSet.clear();
					this.chainSet.add(this.startIndex);
					// prepare the sets: all instances of startCandidate that can see the cell at
					// startIndex
					this.startCellSetM1 = Sudoku2.buddiesM1[this.startIndex]
							& this.finder.getCandidates()[this.startCandidate].getMask1();
					this.startCellSetM2 = Sudoku2.buddiesM2[this.startIndex]
							& this.finder.getCandidates()[this.startCandidate].getMask2();
					if (type == REMOTE_PAIR) {
						// the same for the second candidate in a RP
						this.startCellSet2M1 = Sudoku2.buddiesM1[this.startIndex]
								& this.finder.getCandidates()[this.startCandidate2].getMask1();
						this.startCellSet2M2 = Sudoku2.buddiesM2[this.startIndex]
								& this.finder.getCandidates()[this.startCandidate2].getMask2();
					}
					// now do it
					this.getChain(entry, type);
				}
			}
		}
		switch (type) {
		case TURBOT_FISH:
			this.chainTNanos += System.nanoTime() - nanos;
			this.anzT++;
			break;
		case REMOTE_PAIR:
			this.chainRNanos += System.nanoTime() - nanos;
			this.anzR++;
			break;
		case X_CHAIN:
			this.chainXNanos += System.nanoTime() - nanos;
			this.anzX++;
			break;
		case XY_CHAIN:
			this.chainYNanos += System.nanoTime() - nanos;
			this.anzY++;
			break;
		}
	}

	/**
	 * Performes the search: For every candidate iterate over all links and check
	 * them. If the link can prolong the chain, insert it and repeat the
	 * process.<br>
	 * A link is not used if one of the following conditions is true:
	 * <ul>
	 * <li>The link links back to itself (can happen, when a weak and a strong link
	 * are present for the same candidate; only one of them can be taken)</li>
	 * <li>The link links back to the middle of the chain (but it can stay in the
	 * same cell as the link directly before it; the next link must then leave the
	 * cell)</li>
	 * <li>A link that loops back to the start of the chain produces a Nice Loop,
	 * but the search has to be stopped afterwards</li>
	 * </ul>
	 * A chain (except Nice Loop) is valid, if first and last link are strong and
	 * can eliminate a candidate. To make that check faster, a set containing all
	 * existing buddies of the start cell is precalculated: It is ANDed with the
	 * buddies of the new last cell. If the result is not empty, a chain has been
	 * found.
	 *
	 * @param entry
	 * @param typ
	 */
	private void getChain(StackEntry entry, int typ) {
		while (true) {
			// check cell and go back one level if necessary
			while ((entry.aktIndex >= entry.endIndex)) {
				// done with this cell, go back one level
				this.stackLevel--;
				entry = this.stack[this.stackLevel];
//                chainSet.remove(Chain.getSCellIndex(chain[stackLevel]));
				this.chainSet.remove(entry.cellIndex);
				if (this.stackLevel <= 0) {
					// done!
					return;
				}
			}
			// now get the next link and check it
			int newLink = this.links[entry.aktIndex++];
			boolean newLinkIsStrong = Chain.isSStrong(newLink);
			if (entry.strongOnly && !newLinkIsStrong) {
				// link must be strong but isnt -> forbidden
				continue;
			}
			final int newLinkIndex = Chain.getSCellIndex(newLink);
			final int newLinkCandidate = Chain.getSCandidate(newLink);
			if (entry.cellIndex == newLinkIndex && entry.candidate == newLinkCandidate) {
				// cell links to itself -> forbidden
				continue;
			}
			// additional check according to type
			if (typ == REMOTE_PAIR) {
				if (this.sudoku.getCell(newLinkIndex) != this.rpCell) {
					// Remote Pair: All cells must contain the same two candidates
					continue;
				}
			}
			if ((typ == X_CHAIN || typ == TURBOT_FISH) && newLinkCandidate != this.startCandidate) {
				// all links must be for the same candidate
				continue;
			}
			if ((typ == XY_CHAIN || typ == REMOTE_PAIR) && this.sudoku.getAnzCandidates(newLinkIndex) != 2) {
				// cell is not bivalue -> no XY-Chain or RP possible
				continue;
			}
			if ((typ == XY_CHAIN || typ == REMOTE_PAIR) && entry.strongOnly && newLinkIndex != entry.cellIndex) {
				// all strong links must be within a cell
				continue;
			}
			// the new link must not link back to the middle of the chain
			// (link to the start is allowed for Nice Loops, handled below)
			boolean isLoop = false;
			if (this.chainSet.contains(newLinkIndex)) {
				if (this.startIndex != newLinkIndex) {
					// chain links back to itself (somewhere in the middle)
					continue;
				}
				isLoop = true;
			}
			// ok: new link is valid -> take it and check the chain
			// chainSet always contains only the second last link (lasso check wouldnt
			// work otherwise: the last cell can be in the chain twice, but never three
			// times)
			this.chainSet.add(entry.cellIndex);
			if (!entry.strongOnly && newLinkIsStrong) {
				// might be strong link, but only weak is needed -> change it
				newLink = Chain.setSStrong(newLink, false);
				newLinkIsStrong = false;
			}
			// add it to the chain
			this.chain[++this.stackLevel] = newLink;

			// could be a valid chain -> check it
			// in a Nice Loop a chain is valid if it links back to the start
			// in all other simple chains, the last link must be strong, for the start
			// candidate
			// and have at least one candidate that can be eliminated
			// Special case RP: A possible larger RP with more eliminations is found as
			// extra chain with a different
			// start cell; startCandidate2 doesnt have to be checked, it will be found again
			// later
			if (typ == NICE_LOOP) {
				if (isLoop) {
					this.checkNiceLoop(newLink, this.stackLevel);
				}
			} else {
				if (this.stackLevel > 1 && newLinkIsStrong && newLinkCandidate == this.startCandidate) {
					// check if the first and last cells can delete anything
					final long m1 = this.startCellSetM1 & Sudoku2.buddiesM1[newLinkIndex];
					final long m2 = this.startCellSetM2 & Sudoku2.buddiesM2[newLinkIndex];
//                    printSet("  check:", m1, m2);
					if (m1 != 0 || m2 != 0) {
						// liminations exist -> check
						switch (typ) {
						case X_CHAIN:
							this.checkXChain(m1, m2, false);
							break;
						case TURBOT_FISH:
							if (this.stackLevel == 3) {
								this.checkXChain(m1, m2, true);
							}
							break;
						case XY_CHAIN:
							this.checkXYChain(m1, m2);
							break;
						case REMOTE_PAIR:
							if (this.stackLevel >= 7) {
								this.checkRemotePairs(m1, m2, newLinkIndex);
							}
							break;
						}
					}
				}
			}

			// go to next level
			final boolean oldStrongOnly = entry.strongOnly;
			entry = this.stack[this.stackLevel];
			// chains can be restricted in size: if the chain gets too long, the search is
			// stopped
			if (this.stackLevel < this.chainMaxLength && !isLoop) {
				// ok: next level
				entry.cellIndex = newLinkIndex;
				entry.candidate = newLinkCandidate;
				entry.strongOnly = !oldStrongOnly;
				entry.aktIndex = this.startIndices[entry.cellIndex * 10 + entry.candidate];
				entry.endIndex = this.endIndices[entry.cellIndex * 10 + entry.candidate];
			} else {
				// we are in the next level, but it must not be search anymore -> stop the
				// search
				entry.aktIndex = entry.endIndex;
			}
		}
	}

	/**
	 * An X-Chain exists between strong links of the same candidate. Any candidate
	 * that sees both ends of the chain can be eliminated.<br>
	 * The following checks have to be made <b>before</b> this method is called: The
	 * chain must be at least three links long (exactly three links for
	 * {@link #TURBOT_FISH}), first and last link have to be strong and for the same
	 * candidate, chain must not be a loop, candidates can be eliminated
	 * (precalculated set in <code>m1</code> and <code>m2</code>).
	 *
	 * @param m1
	 * @param m2
	 * @param isTurbot
	 */
	private void checkXChain(long m1, long m2, boolean isTurbot) {
		this.globalStep.reset();
		if (isTurbot) {
			this.globalStep.setType(SolutionTechnique.TURBOT_FISH);
		} else {
			this.globalStep.setType(SolutionTechnique.X_CHAIN);
		}
		this.globalStep.addValue(this.startCandidate);
		this.checkBuddies.set(m1, m2);
		for (int i = 0; i < this.checkBuddies.size(); i++) {
			this.globalStep.addCandidateToDelete(this.checkBuddies.get(i), this.startCandidate);
		}

		// check if the chain has already been found
		// dont do the check for Turbot fishes
		if (isTurbot == false) {
			final String del = this.globalStep.getCandidateString();
			final Integer oldLength = this.deletesMap.get(del);
			if (oldLength != null && oldLength.intValue() <= this.stackLevel) {
				// a chain already exists that delete the same candidate(s) and it was shorter
				// than the new one

				return;
			}
			this.deletesMap.put(del, this.stackLevel);
		}

		// dont forget to copy the chain
		final int[] newChain = new int[this.stackLevel + 1];
//        for (int i = 0; i < newChain.length; i++) {
//            newChain[i] = chain[i];
//        }
		System.arraycopy(this.chain, 0, newChain, 0, newChain.length);
		this.globalStep.addChain(0, this.stackLevel, newChain);
		this.steps.add((SolutionStep) this.globalStep.clone());
	}

	/**
	 * An XY-Chain exists between strong links of the same candidate. Any candidate
	 * that sees both ends of the chain can be eliminated.<br>
	 * the following checks have to be made <b>before</b> this method is called: The
	 * chain must be at least three links long, first and last link have to be
	 * strong and for the same candidate, chain must not be a loop, candidates can
	 * be eliminated (precalculated set in <code>m1</code> and <code>m2</code>).
	 *
	 * @param m1
	 * @param m2
	 */
	private void checkXYChain(long m1, long m2) {
		this.globalStep.reset();
		this.globalStep.setType(SolutionTechnique.XY_CHAIN);
		this.globalStep.addValue(this.startCandidate);
		this.checkBuddies.set(m1, m2);
		for (int i = 0; i < this.checkBuddies.size(); i++) {
			this.globalStep.addCandidateToDelete(this.checkBuddies.get(i), this.startCandidate);
		}

		// check if the chain has already been found
		final String del = this.globalStep.getCandidateString();
		final Integer oldLength = this.deletesMap.get(del);
		if (oldLength != null && oldLength.intValue() <= this.stackLevel) {
			// a chain for this set of eliminations already exists and is shorter than the
			// new one
			return;
		}
		this.deletesMap.put(del, this.stackLevel);

		// dont forget to copy the chain
		final int[] newChain = new int[this.stackLevel + 1];
//        for (int i = 0; i < newChain.length; i++) {
//            newChain[i] = chain[i];
//        }
		System.arraycopy(this.chain, 0, newChain, 0, newChain.length);
		this.globalStep.addChain(0, this.stackLevel, newChain);
		this.steps.add((SolutionStep) this.globalStep.clone());
	}

	/**
	 * Check if {@link #chain} contains a valid Remote Pair. <code>m1</code> and
	 * <code>m2</code> contain all candidates that can be deleted for
	 * {@link #startCandidate}, {@link #startIndex} and the end cell.<br>
	 * A Remote Pair must be at least 4 cells (chain length >= 7), first and last
	 * link must be strong and for the same candidate: all of the above must be
	 * checked <b>before</b> calling this method.<br>
	 * If the chain is longe than 4 cells, eliminations can occur between all cells
	 * with opposite polarity.<br>
	 * Since we find only RPs where first and last link are for the same candidate
	 * we find longer chains with more eliminations as two separate shorter RPs.
	 *
	 * @param m1
	 * @param m2
	 * @param endIndex
	 */
	private void checkRemotePairs(long m1, long m2, int endIndex) {
		this.globalStep.reset();
		this.globalStep.setType(SolutionTechnique.REMOTE_PAIR);
		this.rpCand1.clear();
		this.rpCand2.clear();
		// if the cell has more than 4 cells, all combinations have to be checked.
		if (this.stackLevel > 7) {
			// get all deletable candidates for all combinations of cells
			for (int i = 0; i <= this.stackLevel; i += 2) {
				// the first cell with opposite polarity is 6 links away, all
				// others are 4 more away
				for (int j = i + 6; j <= this.stackLevel; j += 4) {
					this.rpTmp.set(Sudoku2.buddies[Chain.getSCellIndex(this.chain[i])]);
					this.rpTmp.and(Sudoku2.buddies[Chain.getSCellIndex(this.chain[j])]);
					this.checkBuddies.set(this.rpTmp);
					this.checkBuddies.and(this.finder.getCandidates()[this.startCandidate]);
					this.rpCand1.or(this.checkBuddies);
					this.checkBuddies.set(this.rpTmp);
					this.checkBuddies.and(this.finder.getCandidates()[this.startCandidate2]);
					this.rpCand2.or(this.checkBuddies);
				}
			}
		} else {
			// shortest possible RP, only eliminations for end cells possible
			final long m21 = this.startCellSet2M1 & Sudoku2.buddiesM1[endIndex];
			final long m22 = this.startCellSet2M2 & Sudoku2.buddiesM2[endIndex];
			this.rpCand1.set(m1, m2);
			this.rpCand2.set(m21, m22);
		}
		// no create the step
		this.globalStep.addValue(this.startCandidate);
		this.globalStep.addValue(this.startCandidate2);
		for (int i = 0; i < this.rpCand1.size(); i++) {
			this.globalStep.addCandidateToDelete(this.rpCand1.get(i), this.startCandidate);
		}
		for (int i = 0; i < this.rpCand2.size(); i++) {
			this.globalStep.addCandidateToDelete(this.rpCand2.get(i), this.startCandidate2);
		}

		// check if the chain has already been found
		final String del = this.globalStep.getCandidateString();
		final Integer oldLength = this.deletesMap.get(del);
		if (oldLength != null && oldLength.intValue() <= this.stackLevel) {
			// a chain for this set of eliminations already exists and is shorter than the
			// new one
			return;
		}
		this.deletesMap.put(del, this.stackLevel);

		// dont forget to copy the chain
		final int[] newChain = new int[this.stackLevel + 1];
//        for (int i = 0; i < newChain.length; i++) {
//            newChain[i] = chain[i];
//        }
		System.arraycopy(this.chain, 0, newChain, 0, newChain.length);
		this.globalStep.addChain(0, this.stackLevel, newChain);
		this.steps.add((SolutionStep) this.globalStep.clone());
	}

	/**
	 * Nice Loops have been moved to the {@link TablingSolver}.
	 *
	 * Wenn die erste und die letzte Zelle der Chain identisch sind, ist es ein Nice
	 * Loop.
	 *
	 * Discontinous Nice Loop: - Erster und letzter Link sind weak für den selben
	 * Kandidaten -> Kandidat kann in erster Zelle gelöscht werden - Erster und
	 * letzter Link sind strong für den selben Kandidaten -> Kandidat kann in
	 * erster Zelle gesetzt werden (alle anderen Kandidaten löschen, ist einfacher
	 * in der Programmlogik) - Ein link ist weak und einer strong, die Kandidaten
	 * sind verschieden -> Kandidat mit weak link kann in erster Zelle gelöscht
	 * werden
	 *
	 * Continuous Nice Loop: - Zwei weak links: Erste Zelle muss bivalue sein,
	 * Kandidaten müssen verschieden sein - Zwei strong links: Kandidaten müssen
	 * verschieden sein - Ein strong, ein weak link: Kandidaten müssen gleich sein
	 *
	 * -> eine Zelle mit zwei strong links: alle anderen Kandidaten von dieser Zelle
	 * löschen -> weak link zwischen zwei Zellen: Kandidat des Links kann von allen
	 * Zellen gelöscht werden, die beide Zellen sehen
	 */
	private void checkNiceLoop(int lastLink, int chainIndex) {
		// int endIndex = (lastLink / 10) % 100;
		final int endIndex = Chain.getSCellIndex(lastLink);
		// Mindestlänge: 3 Links
		if (endIndex != this.startIndex) {
			// kein Loop
			return;
		}
		// auf Looptyp prüfen
		this.globalStep.reset();
		this.globalStep.setType(SolutionTechnique.DISCONTINUOUS_NICE_LOOP);
		// boolean firstLinkStrong = chain[1] / 1000 > 0;
		final boolean firstLinkStrong = Chain.isSStrong(this.chain[1]);
		// boolean lastLinkStrong = lastLink / 1000 > 0;
		final boolean lastLinkStrong = Chain.isSStrong(lastLink);
		// int endCandidate = lastLink % 10;
		final int endCandidate = Chain.getSCandidate(lastLink);
		if (!firstLinkStrong && !lastLinkStrong && this.startCandidate == endCandidate) {
			// Discontinous -> startCandidate in erster Zelle löschen
			this.globalStep.addCandidateToDelete(this.startIndex, this.startCandidate);
		} else if (firstLinkStrong && lastLinkStrong && this.startCandidate == endCandidate) {
			// Discontinous -> alle anderen Kandidaten löschen
			final int[] cands = this.sudoku.getAllCandidates(this.startIndex);
			for (int i = 0; i < cands.length; i++) {
				if (cands[i] != this.startCandidate) {
					this.globalStep.addCandidateToDelete(this.startIndex, cands[i]);
				}
			}
		} else if (firstLinkStrong != lastLinkStrong && this.startCandidate != endCandidate) {
			// Discontinous -> weak link löschen
			if (!firstLinkStrong) {
				this.globalStep.addCandidateToDelete(this.startIndex, this.startCandidate);
			} else {
				this.globalStep.addCandidateToDelete(this.startIndex, endCandidate);
			}
		} else if ((!firstLinkStrong && !lastLinkStrong && this.sudoku.getAnzCandidates(this.startIndex) == 2
				&& this.startCandidate != endCandidate)
				|| (firstLinkStrong && lastLinkStrong && this.startCandidate != endCandidate)
				|| (firstLinkStrong != lastLinkStrong && this.startCandidate == endCandidate)) {
			// Continous -> auf Löschen prüfen
			this.globalStep.setType(SolutionTechnique.CONTINUOUS_NICE_LOOP);
			// Zelle mit zwei strong links: strong link zwischen Zellen, weak link in der
			// Zelle, strong link zu nächster Zelle
			// weak link zwischen Zellen: trivial
			for (int i = 1; i <= chainIndex; i++) {
				// if (chain[i] / 1000 > 0 && i <= chainIndex - 2 && (chain[i - 1] / 10) % 100
				// != (chain[i] / 10) % 100) {
				if (Chain.isSStrong(this.chain[i]) && i <= chainIndex - 2
						&& Chain.getSCellIndex(this.chain[i - 1]) != Chain.getSCellIndex(this.chain[i])) {
					// mögliche Zelle mit zwei strong links: nächster Link muss weak sein auf
					// selbe Zelle, danach strong auf nächste Zelle
					// if (chain[i + 1] / 1000 == 0 && (chain[i] / 10) % 100 == (chain[i + 1] / 10)
					// % 100 &&
					// chain[i + 2] / 1000 > 0 && (chain[i + 1] / 10) % 100 != (chain[i + 2] / 10) %
					// 100) {
					if (!Chain.isSStrong(this.chain[i + 1])
							&& Chain.getSCellIndex(this.chain[i]) == Chain.getSCellIndex(this.chain[i + 1])
							&& Chain.isSStrong(this.chain[i + 2])
							&& Chain.getSCellIndex(this.chain[i + 1]) != Chain.getSCellIndex(this.chain[i + 2])) {
						// in der Zelle chain[i] alle kandidaten außer den beiden strong links löschen
						// int c1 = chain[i] % 10;
						final int c1 = Chain.getSCandidate(this.chain[i]);
						// int c2 = chain[i + 2] % 10;
						final int c2 = Chain.getSCandidate(this.chain[i + 2]);
						// short[] cands = sudoku.getCell((chain[i] / 10) %
						// 100).getAllCandidates(candType);
						final int[] cands = this.sudoku.getAllCandidates(Chain.getSCellIndex(this.chain[i]));
						for (int j = 0; j < cands.length; j++) {
							if (cands[j] != c1 && cands[j] != c2) {
								// globalStep.addCandidateToDelete((chain[i] / 10) % 100, cands[j]);
								this.globalStep.addCandidateToDelete(Chain.getSCellIndex(this.chain[i]), cands[j]);
							}
						}
					}
				}
				// if (chain[i] / 1000 == 0 && (chain[i - 1] / 10) % 100 != (chain[i] / 10) %
				// 100) {
				if (!Chain.isSStrong(this.chain[i])
						&& Chain.getSCellIndex(this.chain[i - 1]) != Chain.getSCellIndex(this.chain[i])) {
					// weak link zwischen zwei Zellen
					// checkBuddies.set(sudoku.buddies[(chain[i - 1] / 10) % 100]);
					this.checkBuddies.set(Sudoku2.buddies[Chain.getSCellIndex(this.chain[i - 1])]);
					// checkBuddies.and(sudoku.buddies[(chain[i] / 10) % 100]);
					this.checkBuddies.and(Sudoku2.buddies[Chain.getSCellIndex(this.chain[i])]);
					this.checkBuddies.andNot(this.chainSet);
					this.checkBuddies.remove(endIndex);
					// checkBuddies.and(sudoku.getCandidates()[chain[i] % 10]);
					this.checkBuddies.and(this.finder.getCandidates()[Chain.getSCandidate(this.chain[i])]);
					if (!this.checkBuddies.isEmpty()) {
						for (int j = 0; j < this.checkBuddies.size(); j++) {
							// globalStep.addCandidateToDelete(checkBuddies.get(j), chain[i] % 10);
							this.globalStep.addCandidateToDelete(this.checkBuddies.get(j),
									Chain.getSCandidate(this.chain[i]));
						}
					}
				}
			}
		}

		if (this.globalStep.getCandidatesToDelete().size() > 0) {
			// ok, Loop ist nicht redundant -> einschreiben, wenn es die Kombination nicht
			// schon gibt
			final String del = this.globalStep.getCandidateString();
			final Integer oldLength = this.deletesMap.get(del);
			if (oldLength != null && oldLength.intValue() <= chainIndex) {
				// F�r diese Kandidaten gibt es schon eine Chain und sie ist k�rzer als die
				// neue
				return;
			}
			this.deletesMap.put(del, chainIndex);
			// Die Chain muss kopiert werden
			final int[] newChain = new int[chainIndex + 1];
//            for (int i = 0; i < newChain.length; i++) {
//                newChain[i] = chain[i];
//            }
			System.arraycopy(this.chain, 0, newChain, 0, newChain.length);
			this.globalStep.addChain(0, chainIndex, newChain);
			this.steps.add((SolutionStep) this.globalStep.clone());
		}
	}

	/**
	 * Calculates all links for the type of chain.
	 * <ul>
	 * <li>{@link #REMOTE_PAIR}: Only links between bivalue cells with the same
	 * candidates and within the cells</li>
	 * <li>{@link #TURBOT_FISH} and {@link #X_CHAIN}: Only between candidates with
	 * the same value</li>
	 * <li>{@link #XY_CHAIN}: Only bivalue cells</li>
	 * <li>{@link #NICE_LOOP}: All types of links</li>
	 * </ul>
	 * Turbot Fish and X-Chain share exactly the same links and X-CHain is after
	 * Turbot in the default configuration. If a search for one type is done after a
	 * search for the other has found nothing and the sudoku has not changed since,
	 * it is not necessary to reca�culate the links.
	 *
	 * @param type
	 */
	private void getAllLinks(int type) {
		// caching
		if (this.turbotOrXSeen && (type == TURBOT_FISH || type == X_CHAIN)
				&& (this.lastStepNumber) == this.finder.getStepNumber()) {
			// dont recalculate the links
			return;
		}
		if (type == TURBOT_FISH || type == X_CHAIN) {
			this.turbotOrXSeen = true;
		} else {
			this.turbotOrXSeen = false;
		}
		this.lastStepNumber = this.finder.getStepNumber();
		// recalculate the links
		final long nanos = System.nanoTime();
		int index = 0;
		int startEndIndex = 0;
		final byte[][] free = this.sudoku.getFree();
		for (int cellIndex = 0; cellIndex < this.sudoku.getCells().length; cellIndex++) {
			final short cell = this.sudoku.getCell(cellIndex);
			if (cell == 0 || ((type == REMOTE_PAIR || type == XY_CHAIN) && Sudoku2.ANZ_VALUES[cell] != 2)) {
				// ignore filled cells
				continue;
			}
			for (int cellCandidate = 1; cellCandidate <= 9; cellCandidate++) {
//                System.out.println(cellIndex + "/" + cellCandidate);
				startEndIndex = cellIndex * 10 + cellCandidate;
//                if (sudoku.getValue(cellIndex) != 0 || !sudoku.isCandidate(cellIndex, cellCandidate)) {
				if (!this.sudoku.isCandidate(cellIndex, cellCandidate)) {
					this.startIndices[startEndIndex] = index;
					this.endIndices[startEndIndex] = index;
					continue;
				}
				this.startIndices[startEndIndex] = index;
				// within a cell: if the cell has only two candidates left, there is a
				// strong link between them (not X-Chain and Turbot), else there are weak
				// links to all other candidates (only Nice Loop)
				final int[] cands = Sudoku2.POSSIBLE_VALUES[cell];
				if (Sudoku2.ANZ_VALUES[cell] == 2) {
					// bivalue cell
					if (type != X_CHAIN && type != TURBOT_FISH) {
						int cand = cands[0];
						if (cand == cellCandidate) {
							cand = cands[1];
						}
						// strong link
//                        System.out.println("  " + index + ": " + cellIndex + "/" + cand + "/" + true);
						this.links[index++] = Chain.makeSEntry(cellIndex, cand, true);
					}
				} else if (type == NICE_LOOP) {
					for (int k = 0; k < cands.length; k++) {
						if (cands[k] == cellCandidate) {
							continue;
						}
//                        System.out.println("  " + index + ": " + cellIndex + "/" + cands[k] + "/" + false);
						this.links[index++] = Chain.makeSEntry(cellIndex, cands[k], false);
					}
				}
				// now within the houses: if the candidates exists only two times, its a strong
				// link, else weak links
				// if XY-Chain only to a bivalue cell, if RP only to bivalue cell with same two
				// candidates
				// else: only for X-Chain, Turbot and Nice Loop
				for (int constr = 0; constr < Sudoku2.CONSTRAINTS[cellIndex].length; constr++) {
					boolean strong = false;
					if (free[Sudoku2.CONSTRAINTS[cellIndex][constr]][cellCandidate] == 2) {
						// strong link!
						if (type == X_CHAIN || type == TURBOT_FISH || type == NICE_LOOP) {
							strong = true;
						}
					}
					final int[] indices = Sudoku2.ALL_UNITS[Sudoku2.CONSTRAINTS[cellIndex][constr]];
					for (int k = 0; k < indices.length; k++) {
						if (indices[k] != cellIndex && this.sudoku.isCandidate(indices[k], cellCandidate)) {
							// link found
							final short cell2 = this.sudoku.getCell(indices[k]);
							if (type == REMOTE_PAIR && cell2 != cell) {
								// must be the same two values
								continue;
							}
							if (type == XY_CHAIN && Sudoku2.ANZ_VALUES[cell2] != 2) {
								// must be bivalue
								continue;
							}
							if (constr == 2 && (Sudoku2.getLine(cellIndex) == Sudoku2.getLine(indices[k])
									|| Sudoku2.getCol(cellIndex) == Sudoku2.getCol(indices[k]))) {
								// link was already recorded in line/col
								continue;
							}
//                            System.out.println("  " + index + ": " + indices[k] + "/" + cellCandidate + "/" + strong);
							this.links[index++] = Chain.makeSEntry(indices[k], cellCandidate, strong);
						}
					}
				}
				this.endIndices[startEndIndex] = index;
			}
		}
//        System.out.println("getAllLinks: " + index);
		switch (type) {
		case TURBOT_FISH:
			this.linkTNanos += System.nanoTime() - nanos;
			break;
		case REMOTE_PAIR:
			this.linkRNanos += System.nanoTime() - nanos;
			break;
		case X_CHAIN:
			this.linkXNanos += System.nanoTime() - nanos;
			break;
		case XY_CHAIN:
			this.linkYNanos += System.nanoTime() - nanos;
			break;
		}
	}

	/**
	 * For debugging only: Print the contents of a set contained in two long values
	 * to the console.
	 *
	 * @param text
	 * @param m1
	 * @param m2
	 */
	private void printSet(String text, long m1, long m2) {
		final SudokuSetBase set = new SudokuSetBase();
		set.setMask1(m1);
		set.setMask2(m2);
		set.setInitialized(false);
		System.out.println(text + ": " + set);
	}

	/**
	 * Print some timing information
	 */
	protected void printStatistics() {
		final double danzT = this.anzT * 1000.0;
		final double danzR = this.anzR * 1000.0;
		final double danzX = this.anzX * 1000.0;
		final double danzY = this.anzY * 1000.0;
		System.out.printf("Turbot:   %6d/%6.2fus/%6.2fus/%6.2fus\r\n", this.anzT, (this.chainTNanos / danzT),
				(this.linkTNanos / danzT), ((this.chainTNanos - this.linkTNanos) / danzT));
		System.out.printf("RP:       %6d/%6.2fus/%6.2fus/%6.2fus\r\n", this.anzR, (this.chainRNanos / danzR),
				(this.linkRNanos / danzR), ((this.chainRNanos - this.linkRNanos) / danzR));
		System.out.printf("X-Chain:  %6d/%6.2fus/%6.2fus/%6.2fus\r\n", this.anzX, (this.chainXNanos / danzX),
				(this.linkXNanos / danzX), ((this.chainXNanos - this.linkXNanos) / danzX));
		System.out.printf("XY-Chain: %6d/%6.2fus/%6.2fus/%6.2fus\r\n", this.anzY, (this.chainYNanos / danzY),
				(this.linkYNanos / danzY), ((this.chainYNanos - this.linkYNanos) / danzY));
	}

	/**
	 * A Comparator for "find all simple chains". Sorts for type first, than the
	 * natural sort order of {@link SolutionStep}.
	 */
	static class ChainComparator implements Comparator<SolutionStep> {

		/**
		 * getAllChains() should be sorted by type first
		 */
		@Override
		public int compare(SolutionStep o1, SolutionStep o2) {
			if (o1.getType().ordinal() != o2.getType().ordinal()) {
				return o1.getType().ordinal() - o2.getType().ordinal();
			}
			return o1.compareTo(o2);
		}
	}

	public static void main(String[] args) {
		// Sudoku2 sudoku = new Sudoku2(true);
		final Sudoku2 sudoku = new Sudoku2();
		// sudoku.setSudoku("000002540009080000004006071000000234200070006846000000680900300000050100037600000");
		// sudoku.setSudoku(":0702:x:..2..85.4571643.....45.27..25.361.4..164..2..94.2851.67698543..4..12.6..125.364.8:918
		// 332 838 938 939 758 958 759 959:318 338 359:");
		// ACHTUNG: Fehlhafte Candidaten: 9 muss in r1c3 gesetzt werden!
		// sudoku.setSudoku(":0702:x:..2..85.4571643.....45.27..25.361.4..164..2..94.2851.67698543..4..12.6..125.364.8:913
		// 918 332 838 938 939 758 958 759 959:318 338 359:");
		// sudoku.setSudoku("36.859..45194723864.861395.1467382959..541.....59264.1.54387..9.931645....1295.43");
		// sudoku.setSudoku(":0000:x:4.963582.5.842...33268.945..3.7..28.8.23...74794582316.8.9.3.4...31.87..9..26.138:145
		// 146 152 573 582 792::");
		// sudoku.setSudoku(":0702:9:.62143.5.1..5.8.3.5..7.9....28.154..4.56.2..3.16.8.52.6.9851..225...6..1..123.695:711
		// 817 919 422 727 729 929 837 438 838 639 757 957 758 961 772 787 788 792:944
		// 964 985:");
		// sudoku.setSudoku(":0000:x:61.......9.37.62..27..3.6.9.......85....1....79.......8.769..32..62.879..29....6.:517
		// 419 819 138 141 854 756 459 863 169 469 391::");

		// sudoku.setSudoku(":0000:x:6.752.4..54..6..7..2.497.5..7524...1..41895.78....5.4.48...2.9575..1...4..6.547.8:647
		// 364 374 684 288 391::");
		// sudoku.setSudoku(":0706::6.752.4..54..6..7..2.497.5..7524...1..41895.78....5.4.48...2.9575..1...4..6.547.8:112
		// 316 318 123 323 327 329 137 647 364 374 684 288 391:384 826 827 963:");
		// sudoku.setSudoku(":0706::6.752.4..54..6..7..2.497.5..7524...1..41895.78....5.4.48...2.9575..1...4..6.547.8:112
		// 316 318 123 323 826 327 827 329 137 647 963 364 374 384 684 288 391:362 662
		// 962:");
		// BUG: shorter chain available
		// sudoku.setSudoku(":0702:6:.84..53.7..1493.....3.8..412.73.61..3.8...4..1.98....3.1..7.23...2.3....83.2..71.:522
		// 622 731 931 532 632 732 548 549 255 958 959 571 671 581 681 981 482 582 682
		// 984 986 988 989:614 615 631:");
		// BUG: Longest RemotePair is too long - fixed
		// Remote Pair: 8/9 r1c3 -9- r2c2 -8- r4c2 -9- r4c7 -8- r5c9 => r1c7,r2c9<>8,
		// r1c7,r2c9<>9
		// Remote Pair: 8/9 r1c3 -9- r2c2 -8- r4c2 -9- r4c7 => r1c7<>8, r1c7<>9
		// Remote Pair: 8/9 r2c2 -9- r4c2 -8- r4c7 -9- r5c9 => r2c9<>8, r2c9<>9
//        sudoku.setSudoku(":0703:8:45.132..63.1657.4.2768493516.2415.37.1472356.73598612416.594.8..2.3614.554.27861.::817 829 917 929:");
		// BUG: No Remote Pair found (found but not displayed correctly - fixed
		// Remote Pair: 2/4 r1c8 -4- r1c2 -2- r7c2 -4- r9c1 => r9c8<>4
		sudoku.setSudoku(
				":0703:4:5.91673.8.63548.191..23956.952413..6316782495...9561328.53916..637824951.91675..3:432 462 298:498:");
		// BUG: XY_CHAIN not sorted corectly
		// XY-Chain: 1 1- r6c3 -6- r5c3 -2- r5c2 -9- r9c2 -1 => r6c2,r89c3<>1
//        sudoku.setSudoku(":0702:1:8+67+3..+24+9+159+4+72+6+3+8+2+43+9+86+5+7148+51+2+3+967....+4781....+8.+9+4+2.+6+3.79.+15+2......394...2+3.+78+6::162 183 193::11");
		// BUG: Shortest chain not found
		// XY-Chain: 8 8- r1c6 -2- r4c6 -3- r7c6 -8- r7c8 -4- r2c8 -9- r2c7 -4- r6c7 -3-
		// r5c7 -6- r3c7 -8 => r1c9,r3c5<>8
		// XY-Chain: 8 8- r3c7 -6- r3c8 -2- r3c5 -8- r8c5 -3- r7c6 -8- r7c8 -4- r8c9 -9-
		// r4c9 -8 => r1c9,r4c7<>8
		// XY-Chain: 8 8- r1c6 -2- r4c6 -3- r7c6 -8- r7c8 -4- r8c9 -9- r4c9 -8 =>
		// r1c9<>8
		// ...
//        sudoku.setSudoku(":0702:8:39674.15.827516..35143.9..74.16...7.27..94..596...7...7.296.5.16..2.17..189475236:838 845 846 347 249 857 858 265 865 867 468 868 869 889:876::9");
		// Turbot Fish: 1 r2c2 =1= r2c7 -1- r9c7 =1= r8c9 => r8c2<>1
		sudoku.setSudoku(
				":0403:1:9+3...8.+4+5..7.4..+38.843.....16+259+7+3+8+4+8+75+4+2+3...+3+4+9+18+6+5724+9+3+7518+26....3.4+5.....+6+4.9+3::182::");

		final SudokuSolver solver = SudokuSolverFactory.getDefaultSolverInstance();
		final boolean singleHint = false;
		if (singleHint) {
			final SolutionStep step = solver.getHint(sudoku, false);
			System.out.println(step);
		} else {
			final List<SolutionStep> steps = solver.getStepFinder().getAllChains(sudoku);
			solver.getStepFinder().printStatistics();
			if (steps.size() > 0) {
				Collections.sort(steps);
				for (final SolutionStep actStep : steps) {
					System.out.println(actStep);
				}
			}
		}
		System.exit(0);
	}
}
