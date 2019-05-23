package sudoku.solver;

import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import sudoku.Candidate;
import sudoku.Chain;
import sudoku.FindAllStepsProgressDialog;
import sudoku.Options;
import sudoku.SolutionStep;
import sudoku.SolutionType;
import sudoku.Sudoku2;
import sudoku.SudokuSet;
import sudoku.SudokuSetBase;
import sudoku.SudokuUtil;

/**
 * Es gelten die Definitionen aus dem Ultimate Fish Guide:
 * http://www.sudoku.com/boards/viewtopic.php?t=4993
 *
 * Zusätze: - Ein Base-Candidate ist eine Potential Elimination, wenn er in
 * mindestens zwei Cover-Units enthalten ist - Ein Basic-Fish ist Sashimi, wenn
 * die Base-unit, die die Fins enthält, ohne Fins nur noch einen Kandidaten
 * hat. -- stimmt nicht mehr!
 *
 * Kraken Fish:
 *
 * In a finned fish either there is a fish or one of the fins is true. This
 * gives the easiest way to find a Kraken Fish (Type 1):
 *
 * - If a candidate that would be eliminated by the unfinned fish can be linked
 * to all fins (fin set -> candidate not set) than that candidate can be
 * eliminated - In a Type 1 KF the eliminated candidate is the same candidate as
 * the fish candidate
 *
 * The other way is a bit more complicated: In an unfinned fish in every cover
 * set exactly one of the base candidates has to be true. In a finned fish
 * either this is true or one of the fins is true. That leads to the second type
 * of Kraken Fish (Type 2):
 *
 * - If chains can be found that link all base candidates of one cover set to a
 * specific candidate (CEC) (base candidate set -> CEC candidate not set) than
 * that candidate can be eliminated. - If the fish has fins, additional chains
 * have to be found for every fin - In a Type 2 KF the deleted candidate can be
 * an arbitrary candidate
 *
 * Endo fins: Have to be treated like normal fins. In Type 2 KF nothing is
 * changed Cannibalism: In Type 1 KF they count as normal possible elimination.
 * In Type 2 KF no chain from the cannibalistic candidate to the CEC has to be
 * found.
 *
 * Implementation: - Type 1: For every possible elimination look after a chain
 * to all the fins - Type 2: For every intersection of a cover set with all base
 * candidates look for possible eliminations; if they exist, try to link them to
 * the fins
 *
 * Caution: Since the Template optimization cannot be applied, KF search can be
 * very slow!
 *
 * @author hobiwan
 */
public class FishSolver extends AbstractSolver {

	/** All unfinned basic {@link SolutionTechnique SolutionTypes} */
	private static final SolutionTechnique[] BASIC_TYPES = { SolutionTechnique.X_WING, SolutionTechnique.SWORDFISH,
			SolutionTechnique.JELLYFISH, SolutionTechnique.SQUIRMBAG, SolutionTechnique.WHALE, SolutionTechnique.LEVIATHAN };
	/** All finned basic {@link SolutionTechnique SolutionTypes} */
	private static final SolutionTechnique[] FINNED_BASIC_TYPES = { SolutionTechnique.FINNED_X_WING,
			SolutionTechnique.FINNED_SWORDFISH, SolutionTechnique.FINNED_JELLYFISH, SolutionTechnique.FINNED_SQUIRMBAG,
			SolutionTechnique.FINNED_WHALE, SolutionTechnique.FINNED_LEVIATHAN };
	/** All finned sashimi basic {@link SolutionTechnique SolutionTypes} */
	private static final SolutionTechnique[] SASHIMI_BASIC_TYPES = { SolutionTechnique.SASHIMI_X_WING,
			SolutionTechnique.SASHIMI_SWORDFISH, SolutionTechnique.SASHIMI_JELLYFISH, SolutionTechnique.SASHIMI_SQUIRMBAG,
			SolutionTechnique.SASHIMI_WHALE, SolutionTechnique.SASHIMI_LEVIATHAN };
	/** All unfinned franken {@link SolutionTechnique SolutionTypes} */
	private static final SolutionTechnique[] FRANKEN_TYPES = { SolutionTechnique.FRANKEN_X_WING, SolutionTechnique.FRANKEN_SWORDFISH,
			SolutionTechnique.FRANKEN_JELLYFISH, SolutionTechnique.FRANKEN_SQUIRMBAG, SolutionTechnique.FRANKEN_WHALE,
			SolutionTechnique.FRANKEN_LEVIATHAN };
	/** All finned franken {@link SolutionTechnique SolutionTypes} */
	private static final SolutionTechnique[] FINNED_FRANKEN_TYPES = { SolutionTechnique.FINNED_FRANKEN_X_WING,
			SolutionTechnique.FINNED_FRANKEN_SWORDFISH, SolutionTechnique.FINNED_FRANKEN_JELLYFISH,
			SolutionTechnique.FINNED_FRANKEN_SQUIRMBAG, SolutionTechnique.FINNED_FRANKEN_WHALE,
			SolutionTechnique.FINNED_FRANKEN_LEVIATHAN };
	/** All unfinned mutant {@link SolutionTechnique SolutionTypes} */
	private static final SolutionTechnique[] MUTANT_TYPES = { SolutionTechnique.MUTANT_X_WING, SolutionTechnique.MUTANT_SWORDFISH,
			SolutionTechnique.MUTANT_JELLYFISH, SolutionTechnique.MUTANT_SQUIRMBAG, SolutionTechnique.MUTANT_WHALE,
			SolutionTechnique.MUTANT_LEVIATHAN };
	/** All finned mutant {@link SolutionTechnique SolutionTypes} */
	private static final SolutionTechnique[] FINNED_MUTANT_TYPES = { SolutionTechnique.FINNED_MUTANT_X_WING,
			SolutionTechnique.FINNED_MUTANT_SWORDFISH, SolutionTechnique.FINNED_MUTANT_JELLYFISH,
			SolutionTechnique.FINNED_MUTANT_SQUIRMBAG, SolutionTechnique.FINNED_MUTANT_WHALE,
			SolutionTechnique.FINNED_MUTANT_LEVIATHAN };
	/** Set if search is for kraken fish */
	private static final int UNDEFINED = -1;
	/** Search for basic fish */
	private static final int BASIC = 0;
	/** Search for franken fish */
	private static final int FRANKEN = 1;
	/** Search for mutant fish */
	private static final int MUTANT = 2;
	/** Mask for determining the fish shape */
	private static final int LINE_MASK = 0x1;
	/** Mask for determining the fish shape */
	private static final int COL_MASK = 0x2;
	/** Mask for determining the fish shape */
	private static final int BLOCK_MASK = 0x4;
	/** Array with constraint type masks for speedup. */
	private static final int[] MASKS = { BLOCK_MASK, LINE_MASK, COL_MASK };

	/** One entry in the recursion stack for the base unit search */
	private class BaseStackEntry {

		/** The index of the base unit that is currently tried */
		int aktIndex = 0;
		/** The number of the unit that was set previously */
		int lastUnit = 0;
		/**
		 * All cells that are in at least one base set and hold the fish candidate (low
		 * order DWORD)
		 */
		long candidatesM1 = 0;
		/**
		 * All cells that are in at least one base set and hold the fish candidate (high
		 * order DWORD)
		 */
		long candidatesM2 = 0;
		/**
		 * All cells that are in more than one base set and have to be treated as fins
		 * (low order DWORD).
		 */
		long endoFinsM1 = 0;
		/**
		 * All cells that are in more than one base set and have to be treated as fins
		 * (high order DWORD).
		 */
		long endoFinsM2 = 0;
	}

	/** One entry in the recursion stack for the cover unit search */
	private class CoverStackEntry {

		/** The index of the base unit that is currently tried */
		int aktIndex = 0;
		/** The number of the unit that was set previously */
		int lastUnit = 0;
		/**
		 * All cells that are in at least one cover set and hold the fish candidate (low
		 * order DWORD)
		 */
		long candidatesM1 = 0;
		/**
		 * All cells that are in at least one cover set and hold the fish candidate
		 * (high order DWORD)
		 */
		long candidatesM2 = 0;
		/**
		 * All cells that are in more than one cover set and have to be treated as
		 * potential eliminations (low order DWORD).
		 */
		long cannibalisticM1 = 0;
		/**
		 * All cells that are in more than one cover set and have to be treated as
		 * potential eliminations (high order DWORD).
		 */
		long cannibalisticM2 = 0;
	}

	/** The fish candidate */
	private int candidate;
	/**
	 * A set with all positions where the fish candidate is still possible (low
	 * order DWORD)
	 */
	private long candidatesM1;
	/**
	 * A set with all positions where the fish candidate is still possible (high
	 * order DWORD)
	 */
	private long candidatesM2;
	/** A set for template checks (low order DWORD) */
	private long delCandTemplatesM1;
	/** A set for template checks (high order DWORD) */
	private long delCandTemplatesM2;
	/** A set for creating fish steps */
	private final SudokuSet createFishSet = new SudokuSet();
	/** <code>true</code> if only finless fishes should be found */
	private boolean withoutFins;
	/** <code>true</code> if only finned or sashimi fishes should be found */
	private boolean withFins;
	/** <code>true</code> if finned fish can contain endo fins */
	private boolean withEndoFins;
	/**
	 * <code>true</code> if only sashimi fishes should be found ({@link #withFins}
	 * has to be <code>true</code> as well)
	 */
	private boolean sashimi;
	/** <code>true</code> if the search is for kraken fish only */
	private boolean kraken;
	/**
	 * The fish type: {@link #BASIC}, {@link #FRANKEN}, {@link #MUTANT} or
	 * {@link #UNDEFINED} for kraken search.
	 */
	private int fishType = UNDEFINED;
	/** Minimum size of the fish */
	private int minSize;
	/** Maximum size of the fish */
	private int maxSize;
	/**
	 * An array with all possible base units (indices in
	 * {@link Sudoku2#CONSTRAINTS}).
	 */
	private final int[] baseUnits = new int[Sudoku2.UNITS * 3];
	/**
	 * For every base unit all cells where the candiate is set (low order DWORD).
	 */
	private final long[] baseCandidatesM1 = new long[Sudoku2.UNITS * 3];
	/**
	 * For every base unit all cells where the candiate is set (high order DWORD).
	 */
	private final long[] baseCandidatesM2 = new long[Sudoku2.UNITS * 3];
	/** The number of base units in this search */
	private int numberOfBaseUnits = 0;
	/**
	 * An array with all possible cover units (indices in
	 * {@link Sudoku2#CONSTRAINTS}).
	 */
	private final int[] allCoverUnits = new int[Sudoku2.UNITS * 3];
	/**
	 * For every cover unit all cells where the candiate is set (low order DWORD).
	 */
	private final long[] allCoverCandidatesM1 = new long[Sudoku2.UNITS * 3];
	/**
	 * For every cover unit all cells where the candiate is set (high order DWORD).
	 */
	private final long[] allCoverCandidatesM2 = new long[Sudoku2.UNITS * 3];
	/** The number of possible cover units */
	private int numberOfAllCoverUnits = 0;
	/**
	 * An array with all possible cover units for one cover unit search (indices in
	 * {@link Sudoku2#CONSTRAINTS}).
	 */
	private final int[] coverUnits = new int[Sudoku2.UNITS * 3];
	/**
	 * For every entry in {@link #coverUnits} all cells where the candiate is set
	 * (low order DWORD).
	 */
	private final long[] coverCandidatesM1 = new long[Sudoku2.UNITS * 3];
	/**
	 * For every entry in {@link #coverUnits} all cells where the candiate is set
	 * (high order DWORD).
	 */
	private final long[] coverCandidatesM2 = new long[Sudoku2.UNITS * 3];
	/** The number of cover units in this cover search */
	private int numberOfCoverUnits = 0;
	/** True for all base units that are currently used */
	private final boolean[] baseUnitsUsed = new boolean[this.baseUnits.length];
	/** The recursion stack for the base unit search */
	private final BaseStackEntry[] baseStack = new BaseStackEntry[9];
	/** The index of the current level in the {@link #baseStack}. */
	private int baseLevel = 0;
	/** True for all cover units that are currently used */
	private final boolean[] coverUnitsUsed = new boolean[this.allCoverUnits.length];
	/** The recursion stack for the cover unit search */
	private final CoverStackEntry[] coverStack = new CoverStackEntry[9];
	/** The index of the current level in the {@link #coverStack}. */
	private int coverLevel = 0;
	/** Contains one entry for every step (number and indices of eliminations) */
	private final SortedMap<String, Integer> deletesMap = new TreeMap<String, Integer>();
	/** A set to incrementally check for endo fins (low order DWORD) */
	private long aktEndoFinSetM1;
	/** A set to incrementally check for endo fins (high order DWORD) */
	private long aktEndoFinSetM2;
	/** A set to incrementally check for cannibalism (low order DWORD) */
	private long aktCannibalismSetM1;
	/** A set to incrementally check for cannibalism (high order DWORD) */
	private long aktCannibalismSetM2;
	/** A set for template checks */
	private final SudokuSet templateSet = new SudokuSet();
	/** A set that holds potential eliminations for Kraken Fish */
	private final SudokuSet krakenDeleteCandSet = new SudokuSet();
	/** A set that holds all fins for Kraken Fish search */
	private final SudokuSet krakenFinSet = new SudokuSet();
	/** A set for cannibalistic eliminations in Kraken Fish */
	private final SudokuSet krakenCannibalisticSet = new SudokuSet();
	/** A progress dialog for "find all steps" */
	private FindAllStepsProgressDialog dlg = null;
	/** A global {@link SolutionStep}, speeds up search */
	private final SolutionStep globalStep = new SolutionStep(SolutionTechnique.HIDDEN_SINGLE);
	/** A {@link TablingSolver} for Kraken Fish search */
	private TablingSolver tablingSolver = null;
	/** for various checks (low order DWORD) */
	private long tmpSetM1;
	/** for various checks (high order DWORD) */
	private long tmpSetM2;
	/** for various checks (low order DWORD) */
	private long tmpSet1M1;
	/** for various checks (high order DWORD) */
	private long tmpSet1M2;
	/** for various checks (low order DWORD) */
	private long tmpSet2M1;
	/** for various checks (high order DWORD) */
	private long tmpSet2M2;
	/** A set for getting all possible buddies */
	private final SudokuSet getBuddiesSet = new SudokuSet();
	/** For Sashimi check (low order DWORD) */
	private long checkSashimiSetM1;
	/** For Sashimi check (high order DWORD) */
	private long checkSashimiSetM2;
	/** All cells that can be seen by all fins (low order DWORD) */
	private long finBuddiesM1;
	/** All cells that can be seen by all fins (high order DWORD) */
	private long finBuddiesM2;
	/** All fins for a given fish (including endo fins) (low order DWORD) */
	private long finsM1;
	/** All fins for a given fish (including endo fins) (high order DWORD) */
	private long finsM2;
	/** true, if all fishes should be found (searches for inverse in Kraken Fish) */
	private boolean searchAll;
	/** <code>true</code> if Siamese Fish should be found */
	private boolean siamese;
	/** Check for templates */
	private boolean doTemplates;
	/** All steps found by the last search */
	private List<SolutionStep> steps = new ArrayList<SolutionStep>();
	/**
	 * A cache for steps that were found but cannot be used just now (Finned <->
	 * Sashimi)
	 */
	private final List<SolutionStep> cachedSteps = new ArrayList<SolutionStep>();
	/** the {@link SudokuStepFinder#stepNumber} of the last executed step. */
	private int lastStepNumber = 0;
	/** The maximum number of base unit combinations (for progress bar) */
	private int maxBaseCombinations = 0; // Anzahl möglicher Kombinationen aus base-units
	/** number of combinations of base units in fish search */
	private int baseGesamt = 0;
	/** number of combinations of base units in fish search for progress bar */
	private int baseShowGesamt = 0;
	/** number of combinations of cover units in fish search */
	private int coverGesamt = 0;
	/** number of tries for unfinned fish */
	private int versucheFisch = 0;
	/** number of tries for finned fish */
	private int versucheFins = 0;
	/** number of tries for finned fish per number of fins */
	private final int[] anzFins = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

	/**
	 * Creates a new instance of FishSolver
	 *
	 * @param finder
	 */
	protected FishSolver(SudokuStepFinder finder) {
		super(finder);
		for (int i = 0; i < this.baseStack.length; i++) {
			this.baseStack[i] = new BaseStackEntry();
		}
		for (int i = 0; i < this.coverStack.length; i++) {
			this.coverStack[i] = new CoverStackEntry();
		}
	}

	@Override
	protected SolutionStep getStep(SolutionTechnique type) {
		SolutionStep result = null;
		this.sudoku = this.finder.getSudoku();
		int size = 2;
		switch (type) {
		case LEVIATHAN:
			size++;
		case WHALE:
			size++;
		case SQUIRMBAG:
			size++;
		case JELLYFISH:
			size++;
		case SWORDFISH:
			size++;
		case X_WING:
			this.searchAll = false;
			result = this.getAnyFish(size, true, false, false, false, BASIC);
			break;
		case FINNED_LEVIATHAN:
			size++;
		case FINNED_WHALE:
			size++;
		case FINNED_SQUIRMBAG:
			size++;
		case FINNED_JELLYFISH:
			size++;
		case FINNED_SWORDFISH:
			size++;
		case FINNED_X_WING:
			this.searchAll = false;
			result = this.getAnyFish(size, false, true, false, false, BASIC);
			break;
		case SASHIMI_LEVIATHAN:
			size++;
		case SASHIMI_WHALE:
			size++;
		case SASHIMI_SQUIRMBAG:
			size++;
		case SASHIMI_JELLYFISH:
			size++;
		case SASHIMI_SWORDFISH:
			size++;
		case SASHIMI_X_WING:
			this.searchAll = false;
			result = this.getAnyFish(size, false, true, true, false, BASIC);
			break;
		case FRANKEN_LEVIATHAN:
			size++;
		case FRANKEN_WHALE:
			size++;
		case FRANKEN_SQUIRMBAG:
			size++;
		case FRANKEN_JELLYFISH:
			size++;
		case FRANKEN_SWORDFISH:
			size++;
		case FRANKEN_X_WING:
			this.searchAll = false;
			result = this.getAnyFish(size, true, false, false, true, FRANKEN);
			break;
		case FINNED_FRANKEN_LEVIATHAN:
			size++;
		case FINNED_FRANKEN_WHALE:
			size++;
		case FINNED_FRANKEN_SQUIRMBAG:
			size++;
		case FINNED_FRANKEN_JELLYFISH:
			size++;
		case FINNED_FRANKEN_SWORDFISH:
			size++;
		case FINNED_FRANKEN_X_WING:
			this.searchAll = false;
			result = this.getAnyFish(size, false, true, false, true, FRANKEN);
			break;
		case MUTANT_LEVIATHAN:
			size++;
		case MUTANT_WHALE:
			size++;
		case MUTANT_SQUIRMBAG:
			size++;
		case MUTANT_JELLYFISH:
			size++;
		case MUTANT_SWORDFISH:
			size++;
		case MUTANT_X_WING:
			this.searchAll = false;
			result = this.getAnyFish(size, true, false, false, true, MUTANT);
			break;
		case FINNED_MUTANT_LEVIATHAN:
			size++;
		case FINNED_MUTANT_WHALE:
			size++;
		case FINNED_MUTANT_SQUIRMBAG:
			size++;
		case FINNED_MUTANT_JELLYFISH:
			size++;
		case FINNED_MUTANT_SWORDFISH:
			size++;
		case FINNED_MUTANT_X_WING:
			this.searchAll = false;
			result = this.getAnyFish(size, false, true, false, true, MUTANT);
			break;
		case KRAKEN_FISH:
		case KRAKEN_FISH_TYPE_1:
		case KRAKEN_FISH_TYPE_2:
			this.searchAll = false;
			result = this.getKrakenFish();
			break;
		}
		return result;
	}

	@Override
	protected boolean doStep(SolutionStep step) {
		boolean handled = true;

		this.sudoku = this.finder.getSudoku();
		switch (step.getType()) {
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
	 * Get all fishes, display a progress dialog (optional). The search can be
	 * restricted to a certain size, to a candidate and to certain types of fish.
	 *
	 * @param minSize      Minimum fish size for search
	 * @param maxSize      Maximum fish size for search
	 * @param maxFins      Maximum number of fins allowed (speeds up search)
	 * @param maxEndoFins  Maximum number of endo fins allowed (greatly speeds up
	 *                     search)
	 * @param dlg          A progress dialog or <code>null</code>
	 * @param forCandidate -1 means "search for all candidates", all other values
	 *                     restrict the search to those values
	 * @param type         {@link #BASIC}, {@link #FRANKEN} or {@link #MUTANT}: The
	 *                     maximum type for the search
	 * @return
	 */
	protected List<SolutionStep> getAllFishes(int minSize, int maxSize, int maxFins, int maxEndoFins,
			FindAllStepsProgressDialog dlg, int forCandidate, int type) {
		this.dlg = dlg;
		this.sudoku = this.finder.getSudoku();
		final int oldMaxFins = Options.getInstance().getMaxFins();
		final int oldEndoFins = Options.getInstance().getMaxEndoFins();
		Options.getInstance().setMaxFins(maxFins);
		Options.getInstance().setMaxEndoFins(maxEndoFins);
		final List<SolutionStep> oldSteps = this.steps;
		this.steps = new ArrayList<SolutionStep>();
		this.kraken = false;
		this.searchAll = true;
		this.fishType = UNDEFINED;
		long millis1 = System.currentTimeMillis();
		for (int i = 1; i <= 9; i++) {
			if (forCandidate != -1 && forCandidate != i) {
				// not now
				continue;
			}
//            /*K*/System.out.println("getAllFishes() for Candidate " + i);
			long millis = System.currentTimeMillis();
			this.baseGesamt = 0;
			this.baseShowGesamt = 0;
			this.getFishes(i, minSize, maxSize, true, true, false, true, type);
			millis = System.currentTimeMillis() - millis;
//            /*K*/System.out.println("getAllFishes(" + i + "): " + millis + "ms");
//            /*K*/System.out.println(steps.size() + " fishes found!");
		}
		millis1 = System.currentTimeMillis() - millis1;
//        System.out.println("getAllFishes() gesamt: " + millis1 + "ms");
//        System.out.println("baseAnz: " + baseGesamt + "(" + baseShowGesamt + "), coverAnz: " + coverGesamt + ", Fische: " + versucheFisch + ", Fins: " + versucheFins);
//        StringBuffer tmpBuffer = new StringBuffer();
//        for (int i = 0; i < anzFins.length; i++) {
//            tmpBuffer.append(" " + anzFins[i]);
//        }
//        System.out.println(tmpBuffer);
		final List<SolutionStep> result = this.steps;
		if (result != null) {
			this.findSiameseFish(result);
			Collections.sort(result);
		}
		this.steps = oldSteps;
		Options.getInstance().setMaxFins(oldMaxFins);
		Options.getInstance().setMaxEndoFins(oldEndoFins);
		this.dlg = null;
		return result;
	}

	/**
	 * Search for a fish of a given size and shape.
	 *
	 * @param size
	 * @param withoutFins
	 * @param withFins
	 * @param sashimi
	 * @param withEndoFins
	 * @param fishType
	 * @return
	 */
	private SolutionStep getAnyFish(int size, boolean withoutFins, boolean withFins, boolean sashimi,
			boolean withEndoFins, int fishType) {
		this.searchAll = false;
		if (this.finder.getStepNumber() != this.lastStepNumber) {
			// sudoku is dirty -> cache is useless
			this.cachedSteps.clear();
			this.lastStepNumber = this.finder.getStepNumber();
		} else {
			// try the cache first
			for (int i = 0; i < this.cachedSteps.size(); i++) {
				final SolutionStep step = this.cachedSteps.get(i);
				final SolutionTechnique type = step.getType();
				// for a step to fit it must: be the same size, the same category (BASIC,
				// FRANKEN or MUTANT)
				// and finned/sashimi
				if (type.getFishSize() == size
						&& (fishType == BASIC && type.isBasicFish() || fishType == FRANKEN && type.isFrankenFish()
								|| fishType == MUTANT && type.isMutantFish())
						&& (withFins && (step.getFins().size() > 0 || step.getEndoFins().size() > 0))
						&& sashimi == type.isSashimiFish()) {
					this.cachedSteps.clear();
					return step;
				}
			}
		}
		// the hard way...
		this.steps.clear();
		this.kraken = false;
		SolutionStep step = null;
		for (int cand = 1; cand <= 9; cand++) {
			step = this.getFishes(cand, size, size, withoutFins, withFins, sashimi, withEndoFins, fishType);
			if (!this.searchAll && !this.siamese && step != null) {
				return step;
			}
		}
		if ((this.searchAll || this.siamese) && this.steps.size() > 0) {
			this.findSiameseFish(this.steps);
			Collections.sort(this.steps);
			return this.steps.get(0);
		}
		return step;
	}

	/**
	 * Find all Kraken Fishes. Arguments see
	 * {@link #getAllFishes(int, int, int, int, sudoku.FindAllStepsProgressDialog, int, int)}.
	 *
	 * @param minSize
	 * @param maxSize
	 * @param maxFins
	 * @param maxEndoFins
	 * @param dlg
	 * @param forCandidate
	 * @param type
	 * @return
	 */
	protected List<SolutionStep> getAllKrakenFishes(int minSize, int maxSize, int maxFins, int maxEndoFins,
			FindAllStepsProgressDialog dlg, int forCandidate, int type) {
		this.tablingSolver = this.finder.getTablingSolver();
		synchronized (this.tablingSolver) {
			// System.out.println("getAllKrakenFishes: " + minSize + "/" + maxSize + "/" +
			// forCandidate);
			this.dlg = dlg;
			this.sudoku = this.finder.getSudoku();
			final boolean oldCheckTemplates = Options.getInstance().isCheckTemplates();
			Options.getInstance().setCheckTemplates(false);
			final int oldMaxFins = Options.getInstance().getMaxFins();
			final int oldEndoFins = Options.getInstance().getMaxEndoFins();
			Options.getInstance().setMaxFins(maxFins);
			Options.getInstance().setMaxEndoFins(maxEndoFins);
			final List<SolutionStep> oldSteps = this.steps;
			this.steps = new ArrayList<SolutionStep>();
			this.kraken = true;
			this.searchAll = true;
//        fishType = UNDEFINED;
			this.tablingSolver.initForKrakenSearch();
			long millis1 = System.currentTimeMillis();
			for (int i = 1; i <= 9; i++) {
				if (forCandidate != -1 && forCandidate != i) {
					// not now
					continue;
				}
				long millis = System.currentTimeMillis();
				this.baseGesamt = 0;
				this.baseShowGesamt = 0;
				// getFishes(i, minSize, maxSize, lineUnits, colUnits, true, true, false, true);
				this.getFishes(i, minSize, maxSize, true, true, false, true, type);
				millis = System.currentTimeMillis() - millis;
//            System.out.println("getAllKrakenFishes(" + i + "): " + millis + "ms");
//            System.out.println(steps.size() + " kraken fishes found!");
			}
			millis1 = System.currentTimeMillis() - millis1;
//        System.out.println("getAllKrakenFishes() gesamt: " + millis1 + "ms");
//        System.out.println("baseAnz: " + baseGesamt + "(" + baseShowGesamt + "), coverAnz: " + coverGesamt + ", Fische: " + versucheFisch + ", Fins: " + versucheFins);
//        StringBuffer tmpBuffer = new StringBuffer();
//        for (int i = 0; i < anzFins.length; i++) {
//            tmpBuffer.append(" " + anzFins[i]);
//        }
//        System.out.println(tmpBuffer.toString());
			final List<SolutionStep> result = this.steps;
			if (result != null) {
				// findSiameseFish(result);
				Collections.sort(result);
			}
			this.steps = oldSteps;
			Options.getInstance().setCheckTemplates(oldCheckTemplates);
			Options.getInstance().setMaxFins(oldMaxFins);
			Options.getInstance().setMaxEndoFins(oldEndoFins);
			this.kraken = false;
			this.dlg = null;
			// System.out.println(" " + result.size() + " steps!");
			return result;
		}
	}

	/**
	 * Find a Kraken Fish. All options are taken from {@link Options}.
	 *
	 * @return
	 */
	private SolutionStep getKrakenFish() {
		this.tablingSolver = this.finder.getTablingSolver();
		synchronized (this.tablingSolver) {
			this.baseGesamt = 0;
			this.baseShowGesamt = 0;
			this.steps = new ArrayList<SolutionStep>();
			final boolean oldCheckTemplates = Options.getInstance().isCheckTemplates();
			Options.getInstance().setCheckTemplates(false);
			final int oldMaxFins = Options.getInstance().getMaxFins();
			final int oldEndoFins = Options.getInstance().getMaxEndoFins();
			Options.getInstance().setMaxFins(Options.getInstance().getMaxKrakenFins());
			Options.getInstance().setMaxEndoFins(Options.getInstance().getMaxKrakenEndoFins());
			this.kraken = true;
//        fishType = UNDEFINED;
			this.tablingSolver.initForKrakenSearch();
			// Endo fins are only searched if the fish type is other than basic and if the
			// max endo fin size > 0
			this.withEndoFins = Options.getInstance().getMaxKrakenEndoFins() != 0
					&& Options.getInstance().getKrakenMaxFishType() > 0;
			final int size = Options.getInstance().getKrakenMaxFishSize();
			for (int i = 1; i <= 9; i++) {
				this.getFishes(i, 2, size, false, true, true, this.withEndoFins,
						Options.getInstance().getKrakenMaxFishType());
				if (this.steps.size() > 0) {
					break;
				}
			}
			this.kraken = false;
			Options.getInstance().setCheckTemplates(oldCheckTemplates);
			Options.getInstance().setMaxFins(oldMaxFins);
			Options.getInstance().setMaxEndoFins(oldEndoFins);
			if (this.steps.size() > 0) {
				this.findSiameseFish(this.steps);
				Collections.sort(this.steps);
				return this.steps.get(0);
			}
			return null;
		}
	}

	/**
	 * Searches for fishes, delegates to {@link #getFishes(boolean)}. For BASIC and
	 * FRANKEN FISH lines/cols is tried first, then cols/lines.
	 *
	 * @param candidate    The fish candidate
	 * @param minSize      Minimum number of base units (no smaller fishes are
	 *                     found)
	 * @param maxSize      Maximum number of base units (smaller fishes are found as
	 *                     well)
	 * @param withoutFins  <code>true</code>, if only finless fishes should be found
	 * @param withFins     <code>true</code>, if the search is for Finned/Sashimi
	 *                     Fish
	 * @param sashimi      <code>true</code>, if the search is for Sashimi Fish
	 *                     (<code>withFins</code> must be true as well).
	 * @param withEndoFins <code>true</code>, if fishes may contain endo fins
	 * @param fishType     The type of the fish ({@link #BASIC}, {@link #FRANKEN} or
	 *                     {@link #MUTANT}).
	 * @return
	 */
	private SolutionStep getFishes(int candidate, int minSize, int maxSize, boolean withoutFins, boolean withFins,
			boolean sashimi, boolean withEndoFins, int fishType) {
		// init attributes
		this.deletesMap.clear();
		this.siamese = Options.getInstance().isAllowDualsAndSiamese();
		this.fishType = fishType;
		this.candidate = candidate;
//        this.candidates = finder.getCandidates()[candidate];
		this.candidatesM1 = this.finder.getCandidates()[candidate].getMask1();
		this.candidatesM2 = this.finder.getCandidates()[candidate].getMask2();
		this.doTemplates = Options.getInstance().isCheckTemplates();
		// put some restrictions on templates: they need a lot of time to be computed
		// so only use them for really large fish
		if ((fishType == BASIC && maxSize <= 5) || (fishType == FRANKEN && maxSize <= 4)
				|| (fishType == MUTANT && maxSize <= 3)) {
			this.doTemplates = false;
		}
		this.withoutFins = withoutFins;
		this.withFins = withFins;
		this.withEndoFins = withEndoFins;
		this.sashimi = sashimi;
		this.minSize = minSize;
		this.maxSize = maxSize;
		if (this.doTemplates) {
			this.delCandTemplatesM1 = this.finder.getDelCandTemplates(false)[candidate].getMask1();
			this.delCandTemplatesM2 = this.finder.getDelCandTemplates(false)[candidate].getMask2();
		}

		// search in lines first
		final SolutionStep step = this.getFishes(true);
		if (fishType == MUTANT || (!this.searchAll && !this.siamese && step != null)) {
			return step;
		}
		// then in cols
		SolutionStep step2 = this.getFishes(false);
		if (step2 == null) {
			step2 = step;
		}
		return step2;
	}

	/**
	 * Gets all fishes with size between {@link #minSize} and {@link #maxSize} of
	 * type {@link #fishType}. Most required data are set in attributes to reduce
	 * method overhead.
	 *
	 * @param candidate    Nummer des Kandidaten, für den die Fische gesucht werden
	 *                     sollen
	 * @param minSize      Minimale Anzahl an Base-Units im Base-Set (es werden
	 *                     keine kleineren Fische gefunden)
	 * @param maxSize      Maximale Anzahl an Base-Units im Base-Set (es werden auch
	 *                     kleinere Fische gefunden)
	 * @param baseUnits    Alle möglichen Base-Units (jeweils ein sortiertes Array
	 *                     mit allen Indexen dieser Unit)
	 * @param coverUnits   Alle möglichen Cover-Units (jeweils ein sortiertes Array
	 *                     mit allen Indexen dieser Unit)
	 * @param withoutFins  <code>true</code>, wenn Finnless-Fische gesucht werden
	 *                     sollen
	 * @param withFins     <code>true</code>, if the search is for Finned/Sashimi
	 *                     Fish
	 * @param sashimi      <code>true</code>, if the search is for Sashimi Fish
	 *                     (<code>withFins</code> must be true as well).
	 * @param withEndoFins <code>true</code>, if fishes may contain endo fins
	 * @param lines        <code>true</code> if the search is for line/col,
	 *                     <code>false</code> for col/line
	 * @return A step if one was found or <code>null</code>
	 */
	private SolutionStep getFishes(boolean lines) {
		// die ganze Rechnung braucht nur gemacht werden, wenn es überhaupt ein
		// Ergebnis geben kann!
		if (this.doTemplates) {
			this.templateSet.set(this.finder.getDelCandTemplates(false)[this.candidate]);
			this.templateSet.and(this.finder.getCandidates()[this.candidate]);
			if (this.templateSet.isEmpty()) {
				// vergebliche Liebesmüh...
				return null;
			}
		}

		// get all eligible base and cover units
		this.initForCandidat(this.maxSize, this.withFins, lines);

		// try all combinations of base units
		Arrays.fill(this.baseUnitsUsed, false);
		// start with level one (level zero is a stopper)
		this.baseLevel = 1;
//        baseStack[0].candidates.clear();
		this.baseStack[0].candidatesM1 = 0;
		this.baseStack[0].candidatesM2 = 0;
//        baseStack[0].endoFins.clear();
		this.baseStack[0].endoFinsM1 = 0;
		this.baseStack[0].endoFinsM2 = 0;
		this.baseStack[1].aktIndex = 0;
		this.baseStack[1].lastUnit = -1;
		// the current unit index
		int aktBaseIndex = 0;
		BaseStackEntry bEntry = null;
		while (true) {
			// fall back if no unit is available (only one level because baseUnitsIncluded
			// must be treated correctly
//            System.out.println("while: " + baseStack[baseLevel].aktIndex + " >= " + (numberOfBaseUnits - minSize + baseLevel));
			while (this.baseStack[this.baseLevel].aktIndex >= (this.numberOfBaseUnits)) {
				if (this.baseStack[this.baseLevel].lastUnit != -1) {
					this.baseUnitsUsed[this.baseStack[this.baseLevel].lastUnit] = false;
					this.baseStack[this.baseLevel].lastUnit = -1;
				}
				this.baseLevel--;
				if (this.baseLevel <= 0) {
					// all combinations tried -> done!
					if (this.steps.size() > 0) {
						return this.steps.get(0);
					}
					return null;
				}
			}
			bEntry = this.baseStack[this.baseLevel];
			// get the next base set; there must be one left or we would have fallen back
			aktBaseIndex = bEntry.aktIndex++;
//            System.out.println("try: " + aktBaseIndex + "/" + baseLevel + "/" + baseUnits[aktBaseIndex] + "/" + entry.aktIndex);
			// make all necessary calculations
			this.baseGesamt++; // counter for progress bar
			this.baseShowGesamt++; // counter for progress bar
			if (this.dlg != null && this.baseShowGesamt % 100 == 0) {
				this.dlg.updateFishProgressBar(this.baseShowGesamt);
			}
			// if the new unit has common candidates with the current base set, those
			// candidates
			// have to be treated as fin cells (endo fins).
//            aktEndoFinSet.setAnd(baseStack[baseLevel - 1].candidates, baseCandidates[aktBaseIndex]);
			this.aktEndoFinSetM1 = this.baseStack[this.baseLevel - 1].candidatesM1
					& this.baseCandidatesM1[aktBaseIndex];
			this.aktEndoFinSetM2 = this.baseStack[this.baseLevel - 1].candidatesM2
					& this.baseCandidatesM2[aktBaseIndex];
			if (this.aktEndoFinSetM1 != 0 || this.aktEndoFinSetM2 != 0) {
				// intersects() == true means: there are endoFins!
//                if (!withFins || !withEndoFins || (baseStack[baseLevel - 1].endoFins.size() + aktEndoFinSet.size()) > Options.getInstance().maxEndoFins) {
				if (!this.withFins || !this.withEndoFins
						|| (this.getSize(this.baseStack[this.baseLevel - 1].endoFinsM1,
								this.baseStack[this.baseLevel - 1].endoFinsM2)
								+ this.getSize(this.aktEndoFinSetM1, this.aktEndoFinSetM2)) > Options.getInstance()
										.getMaxEndoFins()) {
					// every invalid combination eliminates a lot of possibilities:
					// (all non-zero baseUnits greater than i) over (maxSize - aktSize)
					if (this.dlg != null) {
						for (int j = 1; j <= this.maxSize - this.baseLevel; j++) {
							this.baseShowGesamt += SudokuUtil.combinations(this.numberOfBaseUnits - aktBaseIndex, j);
						}
					}
					continue;
				}
			}
			// calculate union of existing sets with new base unit
//            entry.candidates.setOr(baseStack[baseLevel - 1].candidates, baseCandidates[aktBaseIndex]);
			bEntry.candidatesM1 = this.baseStack[this.baseLevel - 1].candidatesM1 | this.baseCandidatesM1[aktBaseIndex];
			bEntry.candidatesM2 = this.baseStack[this.baseLevel - 1].candidatesM2 | this.baseCandidatesM2[aktBaseIndex];
//            printSet("baseSet", entry.candidatesM1, entry.candidatesM2);
//            entry.endoFins.setOr(baseStack[baseLevel - 1].endoFins, aktEndoFinSet);
			bEntry.endoFinsM1 = this.baseStack[this.baseLevel - 1].endoFinsM1 | this.aktEndoFinSetM1;
			bEntry.endoFinsM2 = this.baseStack[this.baseLevel - 1].endoFinsM2 | this.aktEndoFinSetM2;
//            printSet("endoFinSet", entry.endoFinsM1, entry.endoFinsM2);
//            System.out.println("baseStack[" + (baseLevel-1) + "].candidates: " + baseStack[baseLevel-1].candidates);
//            System.out.println("baseCandidates[" + aktBaseIndex + "]: " + baseCandidates[aktBaseIndex]);
//            System.out.println("baseStack[" + baseLevel + "].candidates: " + baseStack[baseLevel].candidates);
			if (bEntry.lastUnit != -1) {
				this.baseUnitsUsed[bEntry.lastUnit] = false;
			}
			bEntry.lastUnit = this.baseUnits[aktBaseIndex];
			this.baseUnitsUsed[this.baseUnits[aktBaseIndex]] = true;
//            System.out.println("baseUnitsUsed: " + Arrays.toString(baseUnitsUsed));
			// check if this set of endo fins can even give eliminations (pays off because
			// the whole cover set check can be skipped)
//            finBuddies.setAll();
			this.finBuddiesM1 = SudokuSetBase.MAX_MASK1;
			this.finBuddiesM2 = SudokuSetBase.MAX_MASK2;
//            if (doTemplates && ! entry.endoFins.isEmpty()) {
			if (this.doTemplates && (bEntry.endoFinsM1 != 0 || bEntry.endoFinsM2 != 0)) {
				// all cells that can see all the endo fins
				Sudoku2.getBuddies(bEntry.endoFinsM1, bEntry.endoFinsM2, this.getBuddiesSet);
				this.finBuddiesM1 = this.getBuddiesSet.getMask1() & ~this.candidatesM1 & this.delCandTemplatesM1;
				this.finBuddiesM2 = this.getBuddiesSet.getMask2() & ~this.candidatesM2 & this.delCandTemplatesM2;
				// now only those cells that have the candidate we are searching for
//                finBuddies.andNot(candidates);
				// and from those only the ones that can actually be eliminated
//                finBuddies.and(finder.getDelCandTemplates(false)[candidate]);
			}
			// if baseLevel lies between minSize and maxSize -> check cover units
//            if (baseLevel >= minSize && baseLevel <= maxSize && !finBuddies.isEmpty()) {
			if (this.baseLevel >= this.minSize && this.baseLevel <= this.maxSize
					&& (this.finBuddiesM1 != 0 || this.finBuddiesM2 != 0)) {
				// check for fish: calculate all possible combinations of cover sets
//                SolutionStep step = searchCoverUnits(entry.candidates, entry.endoFins);
//                System.out.println("baseLevel: " + baseLevel);
				final SolutionStep step = this.searchCoverUnits(bEntry.candidatesM1, bEntry.candidatesM2,
						bEntry.endoFinsM1, bEntry.endoFinsM2);
				if (!this.searchAll && !this.siamese && step != null) {
					return step;
				}
			}
			// and on to the next level
			if (this.baseLevel < this.maxSize) {
				this.baseLevel++;
				bEntry = this.baseStack[this.baseLevel];
				bEntry.aktIndex = aktBaseIndex + 1;
				bEntry.lastUnit = -1;
			}
		}
	}

	/**
	 * The complete cover unit search: all possible combinations of cover units that
	 * are not identical with base units are tried.
	 *
	 * @return
	 */
	private SolutionStep searchCoverUnits(long baseSetM1, long baseSetM2, long endoFinSetM1, long endoFinSetM2) {
//        System.out.println("  Cover search:");
//        System.out.println("    baseSet: " + baseSet);
//        System.out.println("    endoFinSet: " + endoFinSet);
//        printSet("baseSet", baseSetM1, baseSetM2);
//        printSet("endoFinSet", endoFinSetM1, endoFinSetM2);
		// calculate all valid cover units for this search
		this.numberOfCoverUnits = 0;
		for (int i = 0; i < this.numberOfAllCoverUnits; i++) {
			if (this.baseUnitsUsed[this.allCoverUnits[i]]) {
				// possible cover unit is base unit -> skip it
				continue;
			}
//            if (SudokuSetBase.andEmpty(baseSet, allCoverCandidates[i])) {
			if ((baseSetM1 & this.allCoverCandidatesM1[i]) == 0 && (baseSetM2 & this.allCoverCandidatesM2[i]) == 0) {
				// no common candidates -> skip it
				continue;
			}
			// valid cover unit
//            System.out.println("  coverUnit: " + allCoverUnits[i]);
//            printSet("  cands", allCoverCandidatesM1[i], allCoverCandidatesM2[i]);
			this.coverUnits[this.numberOfCoverUnits] = this.allCoverUnits[i];
//            coverCandidates[numberOfCoverUnits++] = allCoverCandidates[i];
			this.coverCandidatesM1[this.numberOfCoverUnits] = this.allCoverCandidatesM1[i];
			this.coverCandidatesM2[this.numberOfCoverUnits++] = this.allCoverCandidatesM2[i];
		}
		// try all combinations of cover units
		Arrays.fill(this.coverUnitsUsed, false);
		// start with level one (level zero is a stopper)
		this.coverLevel = 1;
//        coverStack[0].candidates.clear();
		this.coverStack[0].candidatesM1 = 0;
		this.coverStack[0].candidatesM2 = 0;
//        coverStack[0].cannibalistic.clear();
		this.coverStack[0].cannibalisticM1 = 0;
		this.coverStack[0].cannibalisticM2 = 0;
		this.coverStack[1].aktIndex = 0;
		this.coverStack[1].lastUnit = -1;
		// the current unit index
		int aktCoverIndex = 0;
		CoverStackEntry cEntry = null;
		while (true) {
			// fall back if no unit is available (only one level because coverUnitsIncluded
			// must be treated correctly
//            while (coverStack[coverLevel].aktIndex >= (numberOfCoverUnits - minSize + coverLevel)) {
			while (this.coverStack[this.coverLevel].aktIndex >= (this.numberOfCoverUnits - this.baseLevel
					+ this.coverLevel)) {
				if (this.coverStack[this.coverLevel].lastUnit != -1) {
					this.coverUnitsUsed[this.coverStack[this.coverLevel].lastUnit] = false;
					this.coverStack[this.coverLevel].lastUnit = -1;
				}
				this.coverLevel--;
				if (this.coverLevel <= 0) {
					// all combinations tried -> done!
					if (this.steps.size() > 0) {
						return this.steps.get(0);
					}
					return null;
				}
			}
			cEntry = this.coverStack[this.coverLevel];
			// get the next cover set; there must be one left or we would have fallen back
			aktCoverIndex = cEntry.aktIndex++;
//            System.out.println("try cover: " + aktCoverIndex + "/" + coverLevel);
			// if the new unit has common candidates with the current cover set, those
			// candidates
			// have to be treated as possible eliminations (cannibalistic eliminations)
//            aktCannibalismSet.setAnd(coverStack[coverLevel - 1].candidates, coverCandidates[aktCoverIndex]);
			this.aktCannibalismSetM1 = this.coverStack[this.coverLevel - 1].candidatesM1
					& this.coverCandidatesM1[aktCoverIndex];
			this.aktCannibalismSetM2 = this.coverStack[this.coverLevel - 1].candidatesM2
					& this.coverCandidatesM2[aktCoverIndex];
			// calculate union of existing sets with new cover unit
//            entry.candidates.setOr(coverStack[coverLevel - 1].candidates, coverCandidates[aktCoverIndex]);
			cEntry.candidatesM1 = this.coverStack[this.coverLevel - 1].candidatesM1
					| this.coverCandidatesM1[aktCoverIndex];
			cEntry.candidatesM2 = this.coverStack[this.coverLevel - 1].candidatesM2
					| this.coverCandidatesM2[aktCoverIndex];
//            entry.cannibalistic.setOr(coverStack[coverLevel - 1].cannibalistic, aktCannibalismSet);
			cEntry.cannibalisticM1 = this.coverStack[this.coverLevel - 1].cannibalisticM1 | this.aktCannibalismSetM1;
			cEntry.cannibalisticM2 = this.coverStack[this.coverLevel - 1].cannibalisticM2 | this.aktCannibalismSetM2;
//            System.out.println("coverStack[" + coverLevel + "].candidates: " + coverStack[coverLevel].candidates);
//            System.out.println("coverStack[" + coverLevel + "].cannibalistic: " + coverStack[coverLevel].cannibalistic);
			if (cEntry.lastUnit != -1) {
				this.coverUnitsUsed[cEntry.lastUnit] = false;
			}
			cEntry.lastUnit = this.coverUnits[aktCoverIndex];
			this.coverUnitsUsed[this.coverUnits[aktCoverIndex]] = true;
			// statistic
			this.coverGesamt++;
			if (this.coverLevel == this.baseLevel) {
//                System.out.println("try fish!");
				// same number of base and cover units -> possible fish
				this.versucheFisch++;
				// jetzt kann es ein Fisch sein (mit oder ohne Flossen) -> prüfen
//                fins.clear();
				this.finsM1 = this.finsM2 = 0;
//                boolean isCovered = baseSet.isCovered(entry.candidates, fins);
				final long m1 = ~cEntry.candidatesM1 & baseSetM1;
				final long m2 = ~cEntry.candidatesM2 & baseSetM2;
				boolean isCovered = true;
				if (m1 != 0) {
					isCovered = false;
					this.finsM1 = m1;
				}
				if (m2 != 0) {
					isCovered = false;
					this.finsM2 = m2;
				}
//                fins.or(endoFinSet);
				this.finsM1 |= endoFinSetM1;
				this.finsM2 |= endoFinSetM2;
				// for kraken search withoutFins must be false!
				int finSize = 0;
//                if (isCovered && withoutFins && fins.isEmpty()) {
				if (isCovered && this.withoutFins && this.finsM1 == 0 && this.finsM2 == 0) {
					this.anzFins[0]++;
					// ********* FINNLESS FISCH ****************
					// all candidates from the cover set that are not in the base set can
					// be eliminated. If a base candidate is in more than one cover set,
					// it can be eliminated as well (cannibalistic elimination)
//                    tmpSet.set(entry.candidates);
//                    tmpSet.andNot(baseSet);
					this.tmpSetM1 = cEntry.candidatesM1 & ~baseSetM1;
					this.tmpSetM2 = cEntry.candidatesM2 & ~baseSetM2;
//                    printSet("coverSet", tmpSetM1, tmpSetM2);
//                    if (! tmpSet.isEmpty() || ! entry.cannibalistic.isEmpty()) {
					if (this.tmpSetM1 != 0 || this.tmpSetM2 != 0 || cEntry.cannibalisticM1 != 0
							|| cEntry.cannibalisticM2 != 0) {
						// we found ourselves a fish!
//                        System.out.println("fish found!");
						final SolutionStep step = this.createFishStep(this.coverLevel, false, this.finsM1, this.finsM2,
								this.tmpSetM1, this.tmpSetM2, cEntry.cannibalisticM1, cEntry.cannibalisticM2,
								endoFinSetM1, endoFinSetM2, this.tmpSetM1, this.tmpSetM2, false);
//                        System.out.println(step + "/" + searchAll + "/" +siamese);
						if (!this.searchAll && !this.siamese && step != null) {
							return step;
						}
					}
				} else if (this.withFins && (finSize = this.getSize(this.finsM1, this.finsM2)) > 0
						&& finSize <= Options.getInstance().getMaxFins()) {
//                    System.out.println("finned fish");
					/*********** POSSIBLE FINNED/SASHIMI-FISCH **********/
					this.versucheFins++;
					this.anzFins[finSize]++;
					// A candidate is a potential elimination, if it belongs to the cover set, but
					// not to
					// base set, or belongs to more than one cover set. A potential elimination
					// becomes an eventual elimination, if it sees all fins (including endo fins).

					// get all cells that can possibly see all fins
//                    Sudoku2.getBuddies(fins, finBuddies);
					Sudoku2.getBuddies(this.finsM1, this.finsM2, this.getBuddiesSet);
					this.finBuddiesM1 = this.getBuddiesSet.getMask1();
					this.finBuddiesM2 = this.getBuddiesSet.getMask2();
//                    printSet("baseSet", baseSetM1, baseSetM2);
//                    printSet("fins", finsM1, finsM2);
//                    printSet("finBuddies", finBuddiesM1, finBuddiesM2);
					// if finBuddies is empty, eliminations are impossible
//                    if (!finBuddies.isEmpty()) {
					if (this.finBuddiesM1 != 0 || this.finBuddiesM2 != 0) {
//                        tmpSet.set(entry.candidates);
//                        tmpSet.andNot(baseSet);
						this.tmpSetM1 = cEntry.candidatesM1 & ~baseSetM1;
						this.tmpSetM2 = cEntry.candidatesM2 & ~baseSetM2;
//                        printSet("coverSet1", tmpSetM1, tmpSetM2);
						this.tmpSet2M1 = this.tmpSetM1;
						this.tmpSet2M2 = this.tmpSetM2;
//                        tmpSet.and(finBuddies);
						this.tmpSetM1 &= this.finBuddiesM1;
						this.tmpSetM2 &= this.finBuddiesM2;
//                        printSet("coverSet2", tmpSetM1, tmpSetM2);
//                        tmpSet1.set(entry.cannibalistic);
//                        tmpSet1.and(finBuddies);
						this.tmpSet1M1 = cEntry.cannibalisticM1 & this.finBuddiesM1;
						this.tmpSet1M2 = cEntry.cannibalisticM2 & this.finBuddiesM2;
						if (!this.kraken && (this.tmpSetM1 != 0 || this.tmpSetM2 != 0 || this.tmpSet1M1 != 0
								|| this.tmpSet1M2 != 0)) {
							final SolutionStep step = this.createFishStep(this.coverLevel, true, this.finsM1,
									this.finsM2, this.tmpSetM1, this.tmpSetM2, this.tmpSet1M1, this.tmpSet1M2,
									endoFinSetM1, endoFinSetM2, this.tmpSet2M1, this.tmpSet2M2, false);
							if (step != null && !this.searchAll && !this.siamese) {
								return step;
							}
						} else if (this.kraken && this.tmpSetM1 == 0 && this.tmpSetM2 == 0 && this.tmpSet1M1 == 0
								&& this.tmpSet1M2 == 0) {
							// tmpSet2: cover & ~base -> potential eliminations, add cannibalistic
							this.tmpSet2M1 |= cEntry.cannibalisticM1;
							this.tmpSet2M2 |= cEntry.cannibalisticM2;
							final SolutionStep step = this.searchForKraken(this.tmpSet2M1, this.tmpSet2M2, baseSetM1,
									baseSetM2, this.finsM1, this.finsM2, cEntry.cannibalisticM1, cEntry.cannibalisticM2,
									endoFinSetM1, endoFinSetM2);
							if (step != null && !this.searchAll) {
								return step;
							}
						}
					}
				}
			}
			// and on to the next level
			if (this.coverLevel < this.maxSize) {
				this.coverLevel++;
				cEntry = this.coverStack[this.coverLevel];
				cEntry.aktIndex = aktCoverIndex + 1;
				cEntry.lastUnit = -1;
			}
		}
	}

	/**
	 * Search the current base/cover set combination for possible Kraken Fish
	 *
	 * @param deleteSetM1     All potential eliminations, including cannibalistic
	 *                        ones
	 * @param deleteSetM2
	 * @param baseSetM1       All base candidates
	 * @param baseSetM2
	 * @param finsM1          All fins (including endo fins)
	 * @param finsM2
	 * @param cannibalisticM1 All potential cannibalistic eliminations
	 * @param cannibalisticM2
	 * @param endoFinsM1      All endo fins (included in finsM1/finsM2)
	 * @param endoFinsM2
	 * @return
	 */
	private SolutionStep searchForKraken(long deleteSetM1, long deleteSetM2, long baseSetM1, long baseSetM2,
			long finsM1, long finsM2, long cannibalisticM1, long cannibalisticM2, long endoFinsM1, long endoFinsM2) {
		// Type 1: We have fins but nothing to delete -> check all
		// cover candidates that are not base candidates wether they can be linked
		// to every fin (if fin set -> cover candidate is not set)
		// only one candidate at a time!

		// deleteSet holds all potential eliminations, including cannibalistic ones
		if (deleteSetM1 != 0 || deleteSetM2 != 0) {
			// System.out.println("Possible Kraken: " + baseUnitsIncluded + "/" +
			// coverUnitsIncluded);
			this.krakenDeleteCandSet.set(deleteSetM1, deleteSetM2);
			this.krakenFinSet.set(finsM1, finsM2);
			for (int j = 0; j < this.krakenDeleteCandSet.size(); j++) {
				final int endIndex = this.krakenDeleteCandSet.get(j);
				if (this.tablingSolver.checkKrakenTypeOne(this.krakenFinSet, endIndex, this.candidate)) {
					// kraken fish found -> add!
					this.krakenCannibalisticSet.set(cannibalisticM1, cannibalisticM2);
					if (this.krakenCannibalisticSet.contains(endIndex)) {
						this.krakenCannibalisticSet.clear();
						this.krakenCannibalisticSet.add(endIndex);
					} else {
						this.krakenCannibalisticSet.clear();
					}
					// we add a step without candidates to delete -> we get there afterwards
					SolutionStep step = this.createFishStep(this.coverLevel, true, finsM1, finsM2, 0, 0,
							this.krakenCannibalisticSet.getMask1(), this.krakenCannibalisticSet.getMask2(), endoFinsM1,
							endoFinsM2, deleteSetM1, deleteSetM2, true);
					step.setSubType(step.getType());
					step.setType(SolutionTechnique.KRAKEN_FISH_TYPE_1);
					step.addCandidateToDelete(endIndex, this.candidate);
					// now the chains
					for (int k = 0; k < this.krakenFinSet.size(); k++) {
						final Chain tmpChain = this.tablingSolver.getKrakenChain(this.krakenFinSet.get(k),
								this.candidate, endIndex, this.candidate);
						step.addChain((Chain) tmpChain.clone());
					}
					this.tablingSolver.adjustChains(step);
					step = this.addKrakenStep(step);
					if (step != null && !this.searchAll) {
						return step;
					}
				}
			}
		}
		// Type 2: For every cover set find chains from all base candidates and all fins
		// to
		// a single candidate
		// a check is only necessary if the cover unit doesnt only contain base
		// candidates
		// for cannibalistic candidates no chain is needed
		this.krakenCannibalisticSet.clear();
//        System.out.println("================ search for kraken type 2 ========================");
//        printSet("base units", baseSetM1, baseSetM2);
//        printSet("cannibalistic", cannibalisticM1, cannibalisticM2);
//        for (int coverIndex = 0; coverIndex < coverUnits.length; coverIndex++) {
		for (int coverIndex = 0; coverIndex < this.numberOfCoverUnits; coverIndex++) {
			if (!this.coverUnitsUsed[this.coverUnits[coverIndex]]) {
				continue;
			}
//            System.out.println("cover unit: " + coverUnits[coverIndex]);
//            printSet("cover candidates", coverCandidatesM1[coverIndex], coverCandidatesM2[coverIndex]);
			// get all base candidates for that cover unit that are not cannibalistic
//            tmpSet.set(cInt[coverIndex]);
//            tmpSet.and(baseCandSet);
//            tmpSet.andNot(cannibalisticSet);
//            tmpSetM1 = coverCandidatesM1[coverUnits[coverIndex]] & baseSetM1 & ~cannibalisticM1;
			this.tmpSetM1 = this.coverCandidatesM1[coverIndex] & baseSetM1 & ~cannibalisticM1;
//            tmpSetM2 = coverCandidatesM2[coverUnits[coverIndex]] & baseSetM2 & ~cannibalisticM2;
			this.tmpSetM2 = this.coverCandidatesM2[coverIndex] & baseSetM2 & ~cannibalisticM2;
//            if (cInt[coverIndex].equals(tmpSet)) {
//            if (coverCandidatesM1[coverUnits[coverIndex]] == tmpSetM1 &&
//                coverCandidatesM2[coverUnits[coverIndex]] == tmpSetM2) {
//            printSet("tmpSetM1", tmpSetM1, tmpSetM2);
			if (this.coverCandidatesM1[coverIndex] == this.tmpSetM1
					&& this.coverCandidatesM2[coverIndex] == this.tmpSetM2) {
				// would be a normal Forcing Chain -> skip it
				continue;
			}
			// now add the fins and check all candidates
//            tmpSet.or(fins);
			this.tmpSetM1 |= finsM1;
			this.tmpSetM2 |= finsM2;
//            printSet("tmpSetM1 with fins", tmpSetM1, tmpSetM2);
			this.krakenDeleteCandSet.set(this.tmpSetM1, this.tmpSetM2);
			this.krakenFinSet.clear();
			for (int endCandidate = 1; endCandidate <= 9; endCandidate++) {
				if (this.tablingSolver.checkKrakenTypeTwo(this.krakenDeleteCandSet, this.krakenFinSet, this.candidate,
						endCandidate)) {
					// kraken fishes found -> add!
					for (int j = 0; j < this.krakenFinSet.size(); j++) {
						final int endIndex = this.krakenFinSet.get(j);
						// we add a step without candidates to delete -> we get there afterwards
						SolutionStep step = this.createFishStep(this.coverLevel, true, finsM1, finsM2, 0, 0, 0, 0,
								endoFinsM1, endoFinsM2, deleteSetM1, deleteSetM2, true);
						step.setSubType(step.getType());
						step.setType(SolutionTechnique.KRAKEN_FISH_TYPE_2);
						step.addCandidateToDelete(endIndex, endCandidate);
						for (int k = 0; k < this.krakenDeleteCandSet.size(); k++) {
							final Chain tmpChain = this.tablingSolver.getKrakenChain(this.krakenDeleteCandSet.get(k),
									this.candidate, endIndex, endCandidate);
							step.addChain((Chain) tmpChain.clone());
						}
						this.tablingSolver.adjustChains(step);
						step = this.addKrakenStep(step);
//                        if (step != null) {
//                            System.out.println(step.toString(2));
//                            System.out.println("=========================== step found! =============================");
//                        }
						if (step != null && !this.searchAll) {
							return step;
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * If more than one step is collected ({@link #searchAll} or {@link #siamese}
	 * are set), the step is added to {@link #steps}, in all other cases a copy of
	 * {@link #globalStep} is returned.
	 *
	 * @return
	 */
	private SolutionStep addFishStep() {
		if (!this.searchAll && !this.siamese) {
			return (SolutionStep) this.globalStep.clone();
		}
		if (this.fishType != UNDEFINED && !this.searchAll) {
			final SolutionTechnique type = this.globalStep.getType();
			if (this.fishType == BASIC && !type.isBasicFish()) {
				return null;
			}
			if (this.fishType == FRANKEN && !type.isFrankenFish()) {
				return null;
			}
			if (this.fishType == MUTANT && !type.isMutantFish()) {
				return null;
			}
		}
		if (Options.getInstance().isOnlyOneFishPerStep()) {
			// String del = globalStep.getCandidateString() + " " +
			// globalStep.getValues().get(0);
			final String delOrg = this.globalStep.getCandidateString();
			int startIndex = delOrg.indexOf(')');
			startIndex = delOrg.indexOf('(', startIndex);
			final String del = delOrg.substring(0, startIndex);
			final Integer oldIndex = this.deletesMap.get(del);
			SolutionStep tmpStep = null;
			if (oldIndex != null) {
				tmpStep = this.steps.get(oldIndex.intValue());
			}
			if (tmpStep == null || this.globalStep.getType().compare(tmpStep.getType()) < 0) {
				if (oldIndex != null) {
					this.steps.remove(oldIndex.intValue());
					this.steps.add(oldIndex.intValue(), (SolutionStep) this.globalStep.clone());
				} else {
					this.steps.add((SolutionStep) this.globalStep.clone());
					this.deletesMap.put(del, this.steps.size() - 1);
				}
			}
		} else {
			this.steps.add((SolutionStep) this.globalStep.clone());
		}
		return null;
	}

	/**
	 * Adds a Kraken Fish to {@link #steps} if an equivalent smaller fish doesnt
	 * already exist.
	 *
	 * @param step
	 * @return
	 */
	private SolutionStep addKrakenStep(SolutionStep step) {
		final String del = step.getCandidateString() + " " + step.getValues().get(0);
		final Integer oldIndex = this.deletesMap.get(del);
		SolutionStep tmpStep = null;
		if (oldIndex != null) {
			tmpStep = this.steps.get(oldIndex);
		}
		if (tmpStep == null || step.getSubType().compare(tmpStep.getSubType()) < 0
				|| (step.getSubType().compare(tmpStep.getSubType()) == 0
						&& step.getChainLength() < tmpStep.getChainLength())) {
			this.steps.add(step);
			this.deletesMap.put(del, this.steps.size() - 1);
			return step;
		}
		return null;
	}

	/**
	 * Siamese Fish are two fishes that have the same base sets and differ only in
	 * which candidates are fins; they provide different eliminations. only fishes
	 * of the same category are checked
	 *
	 * To find them: Compare all pairs of fishes, if the base sets match create a
	 * new steps, that contains the same base set and both cover sets/fins/
	 * eliminations.
	 *
	 * @param fishes All available fishes
	 */
	private void findSiameseFish(List<SolutionStep> fishes) {
		if (!Options.getInstance().isAllowDualsAndSiamese()) {
			// not allowed!
			return;
		}
		// read current size (list can be changed by Siamese Fishes)
		final int maxIndex = fishes.size();
		for (int i = 0; i < maxIndex - 1; i++) {
			for (int j = i + 1; j < maxIndex; j++) {
				final SolutionStep step1 = fishes.get(i);
				final SolutionStep step2 = fishes.get(j);
				if (step1.getValues().get(0) != step2.getValues().get(0)) {
					// different candidate
					continue;
				}
				if (step1.getBaseEntities().size() != step2.getBaseEntities().size()) {
					// different fish size -> no dual
					continue;
				}
				if (SolutionTechnique.getStepConfig(step1.getType()).getCategory().ordinal() != SolutionTechnique
						.getStepConfig(step2.getType()).getCategory().ordinal()) {
					// not the same type of fish
					continue;
				}
				boolean baseSetEqual = true;
				for (int k = 0; k < step1.getBaseEntities().size(); k++) {
					if (!step1.getBaseEntities().get(k).equals(step2.getBaseEntities().get(k))) {
						baseSetEqual = false;
						break;
					}
				}
				if (!baseSetEqual) {
					// not the same base set -> cant be a siamese fish
					continue;
				}
				// possible siamese fish; different eliminations?
				if (step1.getCandidatesToDelete().get(0).equals(step2.getCandidatesToDelete().get(0))) {
					// same step twice -> no siamese fish
					continue;
				}
				// ok: siamese fish!
				final SolutionStep siameseStep = (SolutionStep) step1.clone();
				siameseStep.setIsSiamese(true);
				for (int k = 0; k < step2.getCoverEntities().size(); k++) {
					siameseStep.addCoverEntity(step2.getCoverEntities().get(k));
				}
				for (int k = 0; k < step2.getFins().size(); k++) {
					siameseStep.addFin(step2.getFins().get(k));
				}
				for (int k = 0; k < step2.getCandidatesToDelete().size(); k++) {
					siameseStep.addCandidateToDelete(step2.getCandidatesToDelete().get(k));
				}
				siameseStep.getPotentialEliminations().or(step2.getPotentialEliminations());
				siameseStep.getPotentialCannibalisticEliminations().or(step2.getPotentialCannibalisticEliminations());
				fishes.add(siameseStep);
			}
		}
	}

	/**
	 * Create a new fish step. The step is added to the step list if
	 * {@link #searchAll} or {@link #siamese} are set.
	 *
	 * @param size
	 * @param withFins
	 * @param finSet
	 * @param deleteSet
	 * @param cannibalisticSet
	 * @param endoFinSet
	 * @param potentialEliminations All potential eliminations without cannibalistic
	 *                              eliminations
	 * @param kraken                add step in any case
	 * @return
	 */
	private SolutionStep createFishStep(int size, boolean withFins, long finSetM1, long finSetM2, long deleteSetM1,
			long deleteSetM2, long cannibalisticSetM1, long cannibalisticSetM2, long endoFinSetM1, long endoFinSetM2,
			long potentialEliminationsM1, long potentialEliminationsM2, boolean kraken) {
		this.globalStep.reset();

		// Get masks for the constraint types included in the base and cover sets
		SolutionTechnique type = SolutionTechnique.X_WING;
		final int baseMask = this.getUnitMask(this.baseUnitsUsed);
		final int coverMask = this.getUnitMask(this.coverUnitsUsed);

		// check for Sashimi (only for BASIC FISH)
		boolean isSashimi = false;
		if ((baseMask == LINE_MASK && coverMask == COL_MASK) || (baseMask == COL_MASK && coverMask == LINE_MASK)) {
			// alle base units durchschauen: wenn eine base unit mindestens eine fin
			// enthält, werden alle
			// fins gelöscht; es müssen dann noch mehr als ein base-Kandidat übrig sein
			for (int i = 0; i < this.numberOfBaseUnits; i++) {
				if (this.baseUnitsUsed[this.baseUnits[i]]) {
//                    checkSashimiSet.set(baseCandidates[i]);
//                    checkSashimiSet.andNot(finSet);
					this.checkSashimiSetM1 = this.baseCandidatesM1[i] & ~finSetM1;
					this.checkSashimiSetM2 = this.baseCandidatesM2[i] & ~finSetM2;
					if (this.getSizeLTE1(this.checkSashimiSetM1, this.checkSashimiSetM2)) {
						isSashimi = true;
						break;
					}
				}
			}
		}

		// determine the type
		if ((baseMask == LINE_MASK && coverMask == COL_MASK) || (baseMask == COL_MASK && coverMask == LINE_MASK)) {
			// Basic Fish
			if (isSashimi) {
				type = SASHIMI_BASIC_TYPES[size - 2];
			} else if (withFins) {
				type = FINNED_BASIC_TYPES[size - 2];
			} else {
				type = BASIC_TYPES[size - 2];
			}
		} else if ((((baseMask == LINE_MASK) || (baseMask == (LINE_MASK | BLOCK_MASK)))
				&& ((coverMask == COL_MASK) || (coverMask == (COL_MASK | BLOCK_MASK))))
				|| (((baseMask == COL_MASK) || (baseMask == (COL_MASK | BLOCK_MASK)))
						&& ((coverMask == LINE_MASK) || (coverMask == (LINE_MASK | BLOCK_MASK))))) {
			// Franken Fish
			if (withFins) {
				type = FINNED_FRANKEN_TYPES[size - 2];
			} else {
				type = FRANKEN_TYPES[size - 2];
			}
		} else {
			// Mutant Fish
			if (withFins) {
				type = FINNED_MUTANT_TYPES[size - 2];
			} else {
				type = MUTANT_TYPES[size - 2];
			}
		}
		this.globalStep.setType(type);
		this.globalStep.addValue(this.candidate);
		long bm1 = this.baseStack[this.baseLevel].candidatesM1 & ~finSetM1;
		long bm2 = this.baseStack[this.baseLevel].candidatesM2 & ~finSetM2;
		this.createFishSet.set(bm1, bm2);
		for (int i = 0; i < this.createFishSet.size(); i++) {
			this.globalStep.addIndex(this.createFishSet.get(i));
		}
		for (int i = 0; i < this.baseUnitsUsed.length; i++) {
			if (this.baseUnitsUsed[i]) {
				this.globalStep.addBaseEntity(Sudoku2.CONSTRAINT_TYPE_FROM_CONSTRAINT[i],
						Sudoku2.CONSTRAINT_NUMBER_FROM_CONSTRAINT[i]);
			}
		}
		for (int i = 0; i < this.coverUnitsUsed.length; i++) {
			if (this.coverUnitsUsed[i]) {
				this.globalStep.addCoverEntity(Sudoku2.CONSTRAINT_TYPE_FROM_CONSTRAINT[i],
						Sudoku2.CONSTRAINT_NUMBER_FROM_CONSTRAINT[i]);
			}
		}
		// zu löschende Kandidaten
		this.createFishSet.set(deleteSetM1, deleteSetM2);
		for (int k = 0; k < this.createFishSet.size(); k++) {
			this.globalStep.addCandidateToDelete(this.createFishSet.get(k), this.candidate);
		}
		// cannibalistic eliminations
		this.createFishSet.set(cannibalisticSetM1, cannibalisticSetM2);
		for (int k = 0; k < this.createFishSet.size(); k++) {
			this.globalStep.addCannibalistic(this.createFishSet.get(k), this.candidate);
			this.globalStep.addCandidateToDelete(this.createFishSet.get(k), this.candidate);
		}
		// Fins hinzufügen
		bm1 = finSetM1 & ~endoFinSetM1;
		bm2 = finSetM2 & ~endoFinSetM2;
		this.createFishSet.set(bm1, bm2);
		for (int i = 0; i < this.createFishSet.size(); i++) {
			this.globalStep.addFin(this.createFishSet.get(i), this.candidate);
		}
		// Endo-Fins hinzufügen
		this.createFishSet.set(endoFinSetM1, endoFinSetM2);
		for (int i = 0; i < this.createFishSet.size(); i++) {
			this.globalStep.addEndoFin(this.createFishSet.get(i), this.candidate);
		}
		// add potential (cannibalistic) eliminations
		this.createFishSet.set(potentialEliminationsM1, potentialEliminationsM2);
		this.globalStep.getPotentialEliminations().set(this.createFishSet);
		this.createFishSet.set(cannibalisticSetM1, cannibalisticSetM2);
		this.globalStep.getPotentialCannibalisticEliminations().set(this.createFishSet);

		// differentiate between finned and sashimi: if the type doesnt fit, cache the
		// step
		// but only if the search is not for all fishes
		if (!this.searchAll && this.fishType == BASIC && withFins && this.sashimi != isSashimi) {
			this.cachedSteps.add((SolutionStep) this.globalStep.clone());
			return null;
		}

		// add it to steps or return it
		if (kraken) {
			return (SolutionStep) this.globalStep.clone();
		} else {
			return this.addFishStep();
		}
	}

	/**
	 * Creates a mask that contains bits for every type of constraint contained in
	 * <code>used</code>.
	 *
	 * @param used
	 * @return
	 */
	private int getUnitMask(boolean[] used) {
		int mask = 0;
		for (int i = 0; i < used.length; i++) {
			if (used[i]) {
				mask |= MASKS[Sudoku2.CONSTRAINT_TYPE_FROM_CONSTRAINT[i]];
			}
		}
		return mask;
	}

	/**
	 * Calculates all base and cover sets for the current search. Which set is
	 * possible depends on the <code>type</code> of the fish, on its
	 * <code>size</code> (a base set in a search without fins cannot have more than
	 * <code>size</code> candidates) and the status of <code>lines</code>: If it is
	 * <code>true</code>, the base sets include the lines and the cover set the cols
	 * for basic and franken fish.<br>
	 * {@link #candidate}, {@link #candidates} and {@link #fishType} must already be
	 * set.
	 *
	 * @param size     The maximum size of the fish
	 * @param withFins If <code>false</code>, no base set can have more than
	 *                 <code>size</code> candidates
	 * @param lines    If <code>true</code>, the lines go into the base set
	 */
	private void initForCandidat(int size, boolean withFins, boolean lines) {
		this.numberOfBaseUnits = this.numberOfAllCoverUnits = 0;

		// go through all sets
		for (int i = 0; i < Sudoku2.ALL_CONSTRAINTS_TEMPLATES.length; i++) {
			if (i >= 18 && this.fishType == BASIC) {
				continue;
			}
//            tmpSet.set(Sudoku2.ALL_CONSTRAINTS_TEMPLATES[i]);
//            tmpSet.and(candidates);
			this.tmpSetM1 = Sudoku2.ALL_CONSTRAINTS_TEMPLATES_M1[i] & this.candidatesM1;
			this.tmpSetM2 = Sudoku2.ALL_CONSTRAINTS_TEMPLATES_M2[i] & this.candidatesM2;
//            if (tmpSet.isEmpty()) {
			if (this.tmpSetM1 == 0 && this.tmpSetM2 == 0) {
				// no candidate in unit -> skip it
				continue;
			}
			// valid unit -> store it
			if (i < 9) {
				// unit is a line
				if (lines || this.fishType == MUTANT) {
					this.addUnit(i, this.tmpSetM1, this.tmpSetM2, true, size, withFins);
					if (this.fishType == MUTANT) {
						this.addUnit(i, this.tmpSetM1, this.tmpSetM2, false, size, withFins);
					}
				} else if (!lines || this.fishType == MUTANT) {
					this.addUnit(i, this.tmpSetM1, this.tmpSetM2, false, size, withFins);
					if (this.fishType == MUTANT) {
						this.addUnit(i, this.tmpSetM1, this.tmpSetM2, true, size, withFins);
					}
				}
			} else if (i < 18) {
				// unit is a column
				if (lines || this.fishType == MUTANT) {
					this.addUnit(i, this.tmpSetM1, this.tmpSetM2, false, size, withFins);
					if (this.fishType == MUTANT) {
						this.addUnit(i, this.tmpSetM1, this.tmpSetM2, true, size, withFins);
					}
				} else if (!lines || this.fishType == MUTANT) {
					this.addUnit(i, this.tmpSetM1, this.tmpSetM2, true, size, withFins);
					if (this.fishType == MUTANT) {
						this.addUnit(i, this.tmpSetM1, this.tmpSetM2, false, size, withFins);
					}
				}
			} else {
				// unit is a block
				if (this.fishType != BASIC) {
					this.addUnit(i, this.tmpSetM1, this.tmpSetM2, false, size, withFins);
					this.addUnit(i, this.tmpSetM1, this.tmpSetM2, true, size, withFins);
				}
			}
		}
		this.maxBaseCombinations = 0;
		// we have only maxSize combinations (the smaller fishes are automatically
		// included
		if (this.dlg != null) {
			for (int i = 1; i <= this.maxSize; i++) {
				this.maxBaseCombinations += SudokuUtil.combinations(this.numberOfBaseUnits, i);
			}
			this.dlg.resetFishProgressBar(this.maxBaseCombinations);
		}
//        System.out.println("possibleBaseUnits: " + Arrays.toString(baseUnits) + ", " + numberOfBaseUnits);
//        System.out.println("allPossibleCoverUnits: " + Arrays.toString(allCoverUnits) + ", " + numberOfAllCoverUnits);
	}

	/**
	 * Adds a unit to the base or cover units. If the search is for finnless fish,
	 * base units are only added if they have atmost <code>size</code> candidates.
	 *
	 * @param unit
	 * @param setM1
	 * @param setM2
	 * @param base
	 * @param size
	 * @param withFins
	 */
	private void addUnit(int unit, long setM1, long setM2, boolean base, int size, boolean withFins) {
		if (base) {
			if (withFins || this.getSize(setM1, setM2) <= size) {
				this.baseUnits[this.numberOfBaseUnits] = unit;
				this.baseCandidatesM1[this.numberOfBaseUnits] = setM1;
				this.baseCandidatesM2[this.numberOfBaseUnits++] = setM2;
			}
		} else {
			this.allCoverUnits[this.numberOfAllCoverUnits] = unit;
			this.allCoverCandidatesM1[this.numberOfAllCoverUnits] = setM1;
			this.allCoverCandidatesM2[this.numberOfAllCoverUnits++] = setM2;
		}
	}

	/**
	 * Determines the number of bits set in <code>mask1</code> and
	 * <code>mask2</code>.
	 *
	 * @param mask1
	 * @param mask2
	 * @return
	 */
	private int getSize(long mask1, long mask2) {
		int anzahl = 0;
		if (mask1 != 0) {
			for (int i = 0; i < 64; i += 8) {
				anzahl += SudokuSet.anzValues[(int) ((mask1 >> i) & 0xFF)];
			}
		}
		if (mask2 != 0) {
			for (int i = 0; i < 24; i += 8) {
				anzahl += SudokuSet.anzValues[(int) ((mask2 >> i) & 0xFF)];
			}
		}
		return anzahl;
	}

	/**
	 * Determines if only 0 or 1 bit is set in <code>mask1</code> and
	 * <code>mask2</code>.
	 *
	 * @param mask1
	 * @param mask2
	 * @return
	 */
	private boolean getSizeLTE1(long mask1, long mask2) {
		int anzahl = 0;
		if (mask1 != 0) {
			for (int i = 0; i < 64; i += 8) {
				anzahl += SudokuSet.anzValues[(int) ((mask1 >> i) & 0xFF)];
				if (anzahl > 1) {
					return false;
				}
			}
		}
		if (mask2 != 0) {
			for (int i = 0; i < 24; i += 8) {
				anzahl += SudokuSet.anzValues[(int) ((mask2 >> i) & 0xFF)];
				if (anzahl > 1) {
					return false;
				}
			}
		}
		return true;
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
	 * Print some statistics to the console
	 */
	protected void printStatistics() {
		System.out.println("baseAnz: " + this.baseGesamt + "(" + this.baseShowGesamt + "), coverAnz: "
				+ this.coverGesamt + ", Fische: " + this.versucheFisch + ", Fins: " + this.versucheFins);
		final StringBuffer tmpBuffer = new StringBuffer();
		for (int i = 0; i < this.anzFins.length; i++) {
			tmpBuffer.append(" ").append(this.anzFins[i]);
		}
		System.out.println(tmpBuffer);
	}

	@SuppressWarnings("CallToThreadDumpStack")
	public static void main(String[] args) {
//        Sudoku2 sudoku = new Sudoku2();
//        // X-Wing: 3 r37 c34 => r1c34,r4c34,r5c34,r6c34,r9c4<>3
//        sudoku.setSudoku(":0300:3:9.....+5+6+1..+6.+1.7+937+1..962486...2+147+9........5....4...642..+586+1+75.71.+2..+4..1....+52::313 314 343 344 353 354 363 364 394::");
////        // X-Wing: 1 c15 r25 => r2c4789,r5c34789<>1
//        sudoku.setSudoku(":0300:1:9+8..627+5+3.+65..3...+3+2+7.+5...67+9..3.+5...+5...9...8+32.45..9+6+735+91+428+24+9.+8+7..5+51+8.+2...+7::124 127 128 129 153 154 157 158 159::");
////        // X-Wing: 7 c28 r47 => r4c1345,r7c1345<>7
//        sudoku.setSudoku(":0300:7:....+827...3...+1+8....8..7.9......+8..58+5...+4.6..6.1.59+84.....+3..8.45+819.3..+834.+651+9::741 743 744 745 771 773 774 775::");
////        // Jellyfish: 8 r1468 c2679 => r235c2,r39c7,r9c9<>8
//        sudoku.setSudoku(":0302:8:+65+4.+2..7+12..+1.+7+5+46..+1456..+2+1+27+63.4+5...65.12.+7+5.9+27.1+6...+5812...9.+2....+15.1...+5.2.:317 331 332 997 999:822 832 837 852 897 899::");
////        // Finned X-Wing: 9 r48 c38 fr8c7 => r7c8<>9
//        sudoku.setSudoku(":0310:9:..3..+67......91..+3.6...3.597+8.+25+43.+1...+3+1+7.8+22+3+16+89+4+75+6.813+2...+32.4+7...6...+9+6.23.::978::");
////        // Finned Swordfish: 4 c168 r247 fr1c6 => r2c4<>4
//        sudoku.setSudoku(":0311:4:.3.2+6..+8.86..3.5.+2.+2487..+3+6.+8.39.+2.72..6.8..31.+3+527.+6...+2.863..+3.8.5+2+691.....3.2.:924 128 448 452 157 458 578 491 591 792 193 793:424::");
////        // Finned Jellyfish: 7 r1569 c4689 fr9c7 => r7c9<>7
//        sudoku.setSudoku(":0312:7:+6.+4.5.9.2.596.+2.+4+8....+94.+56578+2+4+9..3+2+46.1.5..9+1+3.+6.8244..9+8.+2.......648.8.1+42...+5:314 754 756 376 776 178 778 382 783 384 784 789 398:779::");
////        // Franken Swordfish: 8 r236 c24b3 => r7c2<>8
//        sudoku.setSudoku(":0331:8:52...617.174.536..6.9271..5..5.2...6.9.76..1...6.3...4..759236135261.9.79613.7.5.:814 819 928 842 844 944 856 861 964 866 867 868:872::");
////        // Sashimi Jellyfish: 6 r1269 c5678 fr9c4 => r7c56<>6
//        sudoku.setSudoku(":0322:6:..+12.5....254+1....6.....1+5+2+2..1+5..7.5.....9.+1.+138...+25...+5..+21.15.+3..79..82.7+1+5.3:411 412 815 419 619 819 826 629 829 833 447 647 849 458 658 685 686:675 676::");
////        // Finned X-Wing: 3 r24 c15 fr2c6 => r13c5<>3
//        sudoku.setSudoku(":0310:3:1.........5.....61..8+1..2...7...9.......1.3..4...5...6.+6.+398+14+2+9+42+5+71+6..+8+136+4279+5:962:315 335::");
////        // Finned Franken Jellyfish: 3 r348b1 c3459 fr1c1 fr3c8 => r1c9<>3
////        sudoku.setSudoku(":0342:3:..+5+648+1..+4...1.+6.86+1+8..75.+4+5+42..186.7+8+6...4.193+1...+7+521+6....34+5+85....+21+7..4+1..+9+86:218 918 222 923 924 344 955 386 986 295 296:319::");
////        // Finned Franken Jellyfish: 7 r169b3 c2679 fr6c8 efr1c9 => r5c9<>7
////        sudoku.setSudoku(":0342:7:+5.3+9641+8..+1.+5...69.9+6...53+4..7...84515.4........3+5...+1..+5.+4..1...18.54.+66..21..+58::759::");
////        // Finned Mutant Jellyfish: 4 r36c15 r18c6b4 fr3c9 => r1c9<>4
////        sudoku.setSudoku(":0362:4:.7+6+1.95..1..+6.5...+98+5+72.16.+7...1+6.......97.3.6+9.2+5.+7188.7.6..5...9+5..3.....+9...4.:211 319 425 227 228 229 443 449 172 272 376 476 885 286 486 289 895 396:419::");
//        // X-Wing: 1 c15 r25 => r2c4789,r5c34789<>1 (checks recursion)
//        sudoku.setSudoku(":0300:1:9+8..627+5+3.+65..3...+3+2+7.+5...67+9..3.+5...+5...9...8+32.45..9+6+735+91+428+24+9.+8+7..5+51+8.+2...+7::124 127 128 129 153 154 157 158 159:c15 r25");
////        sudoku.setSudoku("");
////        sudoku.setSudoku("");
////        sudoku.setSudoku("");
//        SudokuSolver solver = SudokuSolverFactory.getDefaultSolverInstance();
//        boolean singleHint = true;
//        if (singleHint) {
//            SolutionStep step = solver.getHint(sudoku, false);
//            System.out.println(step);
//        } else {
//            List<SolutionStep> steps = solver.getStepFinder().getAllFishes(sudoku, 2, 2, 0, 0, null, 1, BASIC);
//            solver.getStepFinder().printStatistics();
//            if (steps.size() > 0) {
//                Collections.sort(steps);
//                for (SolutionStep actStep : steps) {
//                    System.out.println(actStep);
//                }
//            }
//        }
		try {
			final XMLDecoder in = new XMLDecoder(new BufferedInputStream(new FileInputStream(
					"C:\\Sudoku\\Sonstiges\\Bug reports\\20111208 Comparison Exception\\fishse1326274402326.dat")));
			@SuppressWarnings("unchecked")
			final List<SolutionStep> steps = (List<SolutionStep>) in.readObject();
			in.close();
			System.out.println("anz: " + steps.size());
			for (int i = 0; i < steps.size(); i++) {
				for (int j = i + 1; j < steps.size(); j++) {
					final int c1 = steps.get(i).compareTo(steps.get(j));
					final int c2 = steps.get(j).compareTo(steps.get(i));
					if (c1 == 0 && c2 == 0) {
						// ok!
						continue;
					}
					if (c1 == 0 && c2 != 0 || c2 == 0 && c1 != 0) {
						System.out.println("error: " + c1 + "/" + c2 + "/" + i + "/" + j);
					}
					if (c1 < 0 && c2 < 0 || c1 > 0 && c2 > 0) {
						System.out.println("error: " + c1 + "/" + c2 + "/" + i + "/" + j);
					}
				}
			}
			// zweiter Versuch:alleKombinationen aus 3steps
			int counter = 0;
			for (int i = 0; i < steps.size(); i++) {
				for (int j = 0; j < steps.size(); j++) {
					if (j == i) {
						continue;
					}
					for (int k = 0; k < steps.size(); k++) {
						if (k == i || k == j) {
							continue;
						}
						counter++;
						final int cij = steps.get(i).compareTo(steps.get(j));
						final int cjk = steps.get(j).compareTo(steps.get(k));
						final int cik = steps.get(i).compareTo(steps.get(k));
						if (cij == 0 && cik == 0 && cjk == 0) {
							// ok!
							continue;
						}
						if (cij <= 0 && cjk <= 0 && cik >= 0) {
							System.out.println("error: " + cij + "/" + cjk + "/" + cik + " - " + i + "/" + j + "/" + k);
						}
						if (cij >= 0 && cjk >= 0 && cik <= 0) {
							System.out.println("error: " + cij + "/" + cjk + "/" + cik + " - " + i + "/" + j + "/" + k);
						}
					}
				}

			}
			System.out.println("Counter = " + counter);
//            int i1 = 0;
//            int i2 = 6;
//            int i3 = 14;
//            SolutionStep step1 = steps.get(i1);
//            SolutionStep step2 = steps.get(i2);
//            SolutionStep step3 = steps.get(i3);
//            System.out.println("step[" + i1 + "]: " + step1.toString(2));
//            System.out.println("step[" + i2 + "]: " + step2.toString(2));
//            System.out.println("step[" + i3 + "]: " + step3.toString(2));
//            System.out.println("type1:" + step1.getType().toString() + "/" + step1.getSubType().toString() + "/"+step1.getType().isKrakenFish());
//            System.out.println("type2:" + step2.getType().toString() + "/" + step2.getSubType().toString() + "/"+step2.getType().isKrakenFish());
//            System.out.println("type3:" + step3.getType().toString() + "/" + step3.getSubType().toString() + "/"+step3.getType().isKrakenFish());
//            System.out.println(i1 + " ct " + i2 + " = " + step1.compareTo(step2));
//            System.out.println(i2 + " ct " + i3 + " = " + step2.compareTo(step3));
//            System.out.println(i1 + " ct " + i3 + " = " + step1.compareTo(step3));
//            for (SolutionStep step : steps) {
//                System.out.println("   " + step.toString(2));
//            }
			Collections.sort(steps);
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
		System.exit(0);
	}
}
