package sudoku.solver;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import sudoku.FindAllStepsProgressDialog;
import sudoku.StepConfig;
import sudoku.SudokuSet;
import sudoku.SudokuSetBase;
import sudoku.model.SudokuPuzzle;
import sudoku.view.util.PuzzleSolutionStep;
import sudoku.view.util.SolutionTechnique;
import sudoku.view.util.SolutionTechniqueConfiguration;

/**
 * This class has two purposes:
 * <ol>
 * <li>It holds all configuration data for the specializes solvers and handles
 * lazy initialization</li>
 * <li>It caches data needed by more than one solver (e.g. ALS and RCs)</li>
 * <li>It exposes the public API of the specialized solvers to the rest of the
 * program.</li>
 * </ol>
 *
 * @author hobiwan
 */
public class SudokuStepFinder {
	/** The specialized solver for Singles, Intersections and Subsets. */
	private SimpleSolver simpleSolver;
	/** The specialized solver for all kinds of Fish. */
	private FishSolver fishSolver;
	/** The specialized solver for single digit patterns. */
	private SingleDigitPatternSolver singleDigitPatternSolver;
	/** The specialized solver for all kinds of Uniqueness techniques. */
	private UniquenessSolver uniquenessSolver;
	/** The specialized solver for Wings. */
	private WingSolver wingSolver;
	/** The specialized solver for Coloring. */
	private ColoringSolver coloringSolver;
	/** The specialized solver for simple chains. */
	private ChainSolver chainSolver;
	/** The specialized solver for ALS moves. */
	private AlsSolver alsSolver;
	/** The specialized solver for SDC. */
	private MiscellaneousSolver miscellaneousSolver;
	/** The specialized solver for complicated chains. */
	private TablingSolver tablingSolver;
	/** The specialized solver for Templates. */
	private TemplateSolver templateSolver;
	/** The specialized solver for guessing. */
	private BruteForceSolver bruteForceSolver;
	/** The specialized solver for Incomplete Solutions. */
	private IncompleteSolver incompleteSolver;
	/** The specialized solver for giving up. */
	private GiveUpSolver giveUpSolver;
	/** An array for all specialized solvers. Makes finding steps easier. */
	private AbstractSolver[] solvers;

	private SudokuPuzzle sudoku;

	private SolutionTechniqueConfiguration[] stepConfigs;
	/**
	 * A status counter that changes every time a new step has been found.
	 * Specialized solvers can use this counter to use cached steps instead of
	 * searching for them if no step was found since the last search.
	 */
	private int stepNumber = 0;
	/** for timing */
	private long templateNanos;
	/** for timing */
	private int templateAnz;
	/** Lazy initialization: The solvers are only created when they are used. */
	private boolean initialized = false;
	/**
	 * If set to <code>true</code>, the StepFinder contains only one
	 * {@link SimpleSolver} instance.
	 */
	private boolean simpleOnly = false;

	// Data that is used by more than one specialized solver
	/** One set with all positions left for each candidate. */
	private final SudokuSet[] candidates = new SudokuSet[10];
	/** Dirty flag for candidates. */
	private boolean candidatesDirty = true;
	/** One set with all set cells for each candidate. */
	private final SudokuSet[] positions = new SudokuSet[10];
	/** Dirty flag for positions. */
	private boolean positionsDirty = true;
	/** One set with all cells where a candidate is still possible */
	private final SudokuSet[] candidatesAllowed = new SudokuSet[10];
	/** Dirty flag for candidatesAllowed. */
	private boolean candidatesAllowedDirty = true;
	/** A set for all cells that are not set yet */
	private final SudokuSet emptyCells = new SudokuSet();
	/**
	 * One template per candidate with all positions that can be set immediately.
	 */
	private final SudokuSet[] setValueTemplates = new SudokuSet[10];
	/**
	 * One template per candidate with all positions from which the candidate can be
	 * eliminated immediately.
	 */
	private final SudokuSet[] delCandTemplates = new SudokuSet[10];
	/** The lists with all valid templates for each candidate. */
	private List<List<SudokuSetBase>> candTemplates;
	/** Dirty flag for templates (without refinements). */
	private boolean templatesDirty = true;
	/** Dirty flag for templates (with refinements). */
	private boolean templatesListDirty = true;
	/** Cache for ALS entries (only ALS with more than one cell). */
	private List<Als> alsesOnlyLargerThanOne = null;
	/** Step number for which {@link #alsesOnlyLargerThanOne} was computed. */
	private int alsesOnlyLargerThanOneStepNumber = -1;
	/** Cache for ALS entries (ALS with one cell allowed). */
	private List<Als> alsesWithOne = null;
	/** Step number for which {@link #alsesWithOne} was computed. */
	private int alsesWithOneStepNumber = -1;
	/** Cache for RC entries. */
	private List<RestrictedCommon> restrictedCommons = null;
	/** start indices into {@link #restrictedCommons} for all ALS. */
	private int[] startIndices = null;
	/** end indices into {@link #restrictedCommons} for all ALS. */
	private int[] endIndices = null;
	/** Overlap status at last RC search. */
	private boolean lastRcAllowOverlap;
	/** Step number for which {@link #restrictedCommons} was computed. */
	private int lastRcStepNumber = -1;
	/** ALS list for which RCs were calculated. */
	private List<Als> lastRcAlsList = null;
	/** Was last RC search only for forward references? */
	private boolean lastRcOnlyForward = true;
	/** Collect RCs for forward search only */
	private boolean rcOnlyForward = true;

	// temporary varibles for calculating ALS and RC
	/** Temporary set for recursion: all cells of each try */
	private final SudokuSet indexSet = new SudokuSet();
	/** Temporary set for recursion: all numbers contained in {@link #indexSet}. */
	private final short[] candSets = new short[10];
	/** statistics: total time for all calls */
	private long alsNanos;
	/** statistics: number of calls */
	private int anzAlsCalls;
	/** statistics: number of ALS found */
	private int anzAls;
	/** statistics: number of ALS found more than once */
	private int doubleAls;

	/** All candidates common to two ALS. */
	private short possibleRestrictedCommonsSet = 0;
	/**
	 * Holds all buddies of all candidate cells for one RC (including the candidate
	 * cells themselves).
	 */
	private final SudokuSet restrictedCommonBuddiesSet = new SudokuSet();
	/** All cells containing a specific candidate in two ALS. */
	private final SudokuSet restrictedCommonIndexSet = new SudokuSet();
	/** Contains the indices of all overlapping cells in two ALS. */
	private final SudokuSet intersectionSet = new SudokuSet();
	/** statistics: total time for all calls */
	private long rcNanos;
	/** statistics: number of calls */
	private int rcAnzCalls;
	/** statistics: number of RCs found */
	private int anzRcs;

	/**
	 * Creates an instance of the class.
	 */
	public SudokuStepFinder() {
		this(false);
	}

	/**
	 * Creates an instance of the class.
	 *
	 * @param simpleOnly If set, the StepFinder contains only an instance of
	 *                   SimpleSolver
	 */
	public SudokuStepFinder(boolean simpleOnly) {
		this.simpleOnly = simpleOnly;
		this.initialized = false;
	}

	private void initialize() {
		if (this.initialized) {
			return;
		}
		// Create all Sets
		for (int i = 0; i < this.candidates.length; i++) {
			this.candidates[i] = new SudokuSet();
			this.positions[i] = new SudokuSet();
			this.candidatesAllowed[i] = new SudokuSet();
		}
		// Create all templates
		this.candTemplates = new ArrayList<List<SudokuSetBase>>(10);
		for (int i = 0; i < this.setValueTemplates.length; i++) {
			this.setValueTemplates[i] = new SudokuSet();
			this.delCandTemplates[i] = new SudokuSet();
			this.candTemplates.add(i, new LinkedList<SudokuSetBase>());
		}
		// Create the solvers
		this.simpleSolver = new SimpleSolver(this);
		if (!this.simpleOnly) {
			this.fishSolver = new FishSolver(this);
			this.singleDigitPatternSolver = new SingleDigitPatternSolver(this);
			this.uniquenessSolver = new UniquenessSolver(this);
			this.wingSolver = new WingSolver(this);
			this.coloringSolver = new ColoringSolver(this);
			this.chainSolver = new ChainSolver(this);
			this.alsSolver = new AlsSolver(this);
			this.miscellaneousSolver = new MiscellaneousSolver(this);
			this.tablingSolver = new TablingSolver(this);
			this.templateSolver = new TemplateSolver(this);
			this.bruteForceSolver = new BruteForceSolver(this);
			this.incompleteSolver = new IncompleteSolver(this);
			this.giveUpSolver = new GiveUpSolver(this);
			this.solvers = new AbstractSolver[] { this.simpleSolver, this.fishSolver, this.singleDigitPatternSolver,
					this.uniquenessSolver, this.wingSolver, this.coloringSolver, this.chainSolver, this.alsSolver,
					this.miscellaneousSolver, this.tablingSolver, this.templateSolver, this.bruteForceSolver,
					this.incompleteSolver, this.giveUpSolver };
		} else {
			this.solvers = new AbstractSolver[] { this.simpleSolver };
		}
		this.initialized = true;
	}

	/**
	 * Calls the {@link AbstractSolver#cleanUp() } method for every specialized
	 * solver. This method is called from an extra thread from within
	 * {@link SudokuSolverFactory}. No synchronization is done here to speed things
	 * up, if the functionality is not used.<br>
	 *
	 * Specialized solvers, that use cleanup, have to implement synchronization
	 * themselves.
	 */
	public void cleanUp() {
		if (this.solvers == null) {
			return;
		}
		for (final AbstractSolver solver : this.solvers) {
			solver.cleanUp();
		}
	}

	/**
	 * Gets the next step of type <code>type</code>.
	 *
	 * @param type
	 * @return
	 */
	public PuzzleSolutionStep getStep(SolutionTechnique type) {
		this.initialize();
		PuzzleSolutionStep result = null;
		for (int i = 0; i < this.solvers.length; i++) {
			if ((result = this.solvers[i].getStep(type)) != null) {
				// step has been found!
				this.stepNumber++;
				return result;
			}
		}
		return result;
	}

	public void doStep(PuzzleSolutionStep step) {
		this.initialize();
		for (int i = 0; i < this.solvers.length; i++) {
			if (this.solvers[i].doStep(step)) {
				this.setSudokuDirty();
				return;
			}
		}
		throw new RuntimeException("Invalid solution step in doStep() (" + step.getTechnique() + ")");
	}

	/**
	 * The sudoku has been changed, all precalculated data is now invalid.
	 */
	public void setSudokuDirty() {
		this.candidatesDirty = true;
		this.candidatesAllowedDirty = true;
		this.positionsDirty = true;
		this.templatesDirty = true;
		this.templatesListDirty = true;
		this.stepNumber++;
	}

	/**
	 * Stes a new sudoku.
	 *
	 * @param sudoku
	 */
	public void setSudoku(SudokuPuzzle sudoku) {
		if (sudoku != null && this.sudoku != sudoku) {
			this.sudoku = sudoku;
		}
		// even if the reference is the same, the content could have been changed
		this.setSudokuDirty();
	}

	/**
	 * Gets the sudoku.
	 *
	 * @return
	 */
	public SudokuPuzzle getSudoku() {
		return this.sudoku;
	}

	/**
	 * Sets the stepConfigs.
	 *
	 * @param stepConfigs
	 */
	public void setStepConfigs(StepConfig[] stepConfigs) {
		this.stepConfigs = stepConfigs;
	}

	/**
	 * Get the {@link TablingSolver}.
	 *
	 * @return
	 */
	protected TablingSolver getTablingSolver() {
		return this.tablingSolver;
	}

	/******************************************************************************************************************/
	/* EXPOSE PUBLIC APIs */
	/******************************************************************************************************************/

	/**
	 * Finds all Full Houses for a given sudoku.
	 *
	 * @param newSudoku
	 * @return
	 */
	public List<PuzzleSolutionStep> findAllFullHouses(SudokuPuzzle newSudoku) {
		this.initialize();
		final SudokuPuzzle oldSudoku = this.getSudoku();
		this.setSudoku(newSudoku);
		final List<PuzzleSolutionStep> steps = this.simpleSolver.findAllFullHouses();
		this.setSudoku(oldSudoku);
		return steps;
	}

	/**
	 * Finds all Naked Singles for a given sudoku.
	 *
	 * @param newSudoku
	 * @return
	 */
	public List<PuzzleSolutionStep> findAllNakedSingles(SudokuPuzzle newSudoku) {
		this.initialize();
		final SudokuPuzzle oldSudoku = this.getSudoku();
		this.setSudoku(newSudoku);
		final List<PuzzleSolutionStep> steps = this.simpleSolver.findAllNakedSingles();
		this.setSudoku(oldSudoku);
		return steps;
	}

	/**
	 * Finds all Naked Subsets for a given sudoku.
	 *
	 * @param newSudoku
	 * @return
	 */
	public List<PuzzleSolutionStep> findAllNakedXle(SudokuPuzzle newSudoku) {
		this.initialize();
		final SudokuPuzzle oldSudoku = this.getSudoku();
		this.setSudoku(newSudoku);
		final List<PuzzleSolutionStep> steps = this.simpleSolver.findAllNakedXle();
		this.setSudoku(oldSudoku);
		return steps;
	}

	/**
	 * Finds all Hidden Singles for a given sudoku.
	 *
	 * @param newSudoku
	 * @return
	 */
	public List<PuzzleSolutionStep> findAllHiddenSingles(SudokuPuzzle newSudoku) {
		this.initialize();
		final SudokuPuzzle oldSudoku = this.getSudoku();
		this.setSudoku(newSudoku);
		final List<PuzzleSolutionStep> steps = this.simpleSolver.findAllHiddenSingles();
		this.setSudoku(oldSudoku);
		return steps;
	}

	/**
	 * Find all hidden Subsets.
	 *
	 * @param newSudoku
	 * @return
	 */
	public List<PuzzleSolutionStep> findAllHiddenXle(SudokuPuzzle newSudoku) {
		this.initialize();
		final SudokuPuzzle oldSudoku = this.getSudoku();
		this.setSudoku(newSudoku);
		final List<PuzzleSolutionStep> steps = this.simpleSolver.findAllHiddenXle();
		this.setSudoku(oldSudoku);
		return steps;
	}

	/**
	 * Finds all Locked Candidates for a given sudoku.
	 *
	 * @param newSudoku
	 * @return
	 */
	public List<PuzzleSolutionStep> findAllLockedCandidates(SudokuPuzzle newSudoku) {
		this.initialize();
		final SudokuPuzzle oldSudoku = this.getSudoku();
		this.setSudoku(newSudoku);
		final List<PuzzleSolutionStep> steps = this.simpleSolver.findAllLockedCandidates();
		this.setSudoku(oldSudoku);
		return steps;
	}

	/**
	 * Finds all Locked Candidates Type 1 for a given sudoku.
	 *
	 * @param newSudoku
	 * @return
	 */
	public List<PuzzleSolutionStep> findAllLockedCandidates1(SudokuPuzzle newSudoku) {
		this.initialize();
		final SudokuPuzzle oldSudoku = this.getSudoku();
		this.setSudoku(newSudoku);
		final List<PuzzleSolutionStep> steps = this.simpleSolver.findAllLockedCandidates();
		this.setSudoku(oldSudoku);
		// filter the steps
		final List<PuzzleSolutionStep> resultList = new ArrayList<PuzzleSolutionStep>();
		for (final PuzzleSolutionStep step : steps) {
			if (step.getType().equals(SolutionTechnique.LOCKED_CANDIDATES_1)) {
				resultList.add(step);
			}
		}
		return resultList;
	}

	/**
	 * Finds all Locked Candidates Type 2 for a given sudoku.
	 *
	 * @param newSudoku
	 * @return
	 */
	public List<PuzzleSolutionStep> findAllLockedCandidates2(SudokuPuzzle newSudoku) {
		this.initialize();
		final SudokuPuzzle oldSudoku = this.getSudoku();
		this.setSudoku(newSudoku);
		final List<PuzzleSolutionStep> steps = this.simpleSolver.findAllLockedCandidates();
		this.setSudoku(oldSudoku);
		// filter the steps
		final List<PuzzleSolutionStep> resultList = new ArrayList<PuzzleSolutionStep>();
		for (final PuzzleSolutionStep step : steps) {
			if (step.getType().equals(SolutionTechnique.LOCKED_CANDIDATES_2)) {
				resultList.add(step);
			}
		}
		return resultList;
	}

	/**
	 * Finds all fishes of a given size and shape.
	 *
	 * @param newSudoku
	 * @param minSize
	 * @param maxSize
	 * @param maxFins
	 * @param maxEndoFins
	 * @param dlg
	 * @param forCandidate
	 * @param type
	 * @return
	 */
	public List<PuzzleSolutionStep> getAllFishes(SudokuPuzzle newSudoku, int minSize, int maxSize, int maxFins,
			int maxEndoFins, FindAllStepsProgressDialog dlg, int forCandidate, int type) {
		this.initialize();
		final SudokuPuzzle oldSudoku = this.getSudoku();
		this.setSudoku(newSudoku);
		final List<PuzzleSolutionStep> steps = this.fishSolver.getAllFishes(minSize, maxSize, maxFins, maxEndoFins, dlg,
				forCandidate, type);
		this.setSudoku(oldSudoku);
		return steps;
	}

	/**
	 * Finds all kraken fishes of a given size and shape.
	 *
	 * @param newSudoku
	 * @param minSize
	 * @param maxSize
	 * @param maxFins
	 * @param maxEndoFins
	 * @param dlg
	 * @param forCandidate
	 * @param type
	 * @return
	 */
	public List<PuzzleSolutionStep> getAllKrakenFishes(SudokuPuzzle newSudoku, int minSize, int maxSize, int maxFins,
			int maxEndoFins, FindAllStepsProgressDialog dlg, int forCandidate, int type) {
		this.initialize();
		final SudokuPuzzle oldSudoku = this.getSudoku();
		this.setSudoku(newSudoku);
		final List<PuzzleSolutionStep> steps = this.fishSolver.getAllKrakenFishes(minSize, maxSize, maxFins,
				maxEndoFins, dlg, forCandidate, type);
		this.setSudoku(oldSudoku);
		return steps;
	}

	/**
	 * Finds all Empty Rectangles
	 *
	 * @param newSudoku
	 * @return
	 */
	public List<PuzzleSolutionStep> findAllEmptyRectangles(SudokuPuzzle newSudoku) {
		this.initialize();
		final SudokuPuzzle oldSudoku = this.getSudoku();
		this.setSudoku(newSudoku);
		final List<PuzzleSolutionStep> steps = this.singleDigitPatternSolver.findAllEmptyRectangles();
		this.setSudoku(oldSudoku);
		return steps;
	}

	/**
	 * Finds all Skyscrapers
	 *
	 * @param newSudoku
	 * @return
	 */
	public List<PuzzleSolutionStep> findAllSkyScrapers(SudokuPuzzle newSudoku) {
		this.initialize();
		final SudokuPuzzle oldSudoku = this.getSudoku();
		this.setSudoku(newSudoku);
		final List<PuzzleSolutionStep> steps = this.singleDigitPatternSolver.findAllSkyscrapers();
		this.setSudoku(oldSudoku);
		return steps;
	}

	/**
	 * Finds all Two String Kites
	 *
	 * @param newSudoku
	 * @return
	 */
	public List<PuzzleSolutionStep> findAllTwoStringKites(SudokuPuzzle newSudoku) {
		this.initialize();
		final SudokuPuzzle oldSudoku = this.getSudoku();
		this.setSudoku(newSudoku);
		final List<PuzzleSolutionStep> steps = this.singleDigitPatternSolver.findAllTwoStringKites();
		this.setSudoku(oldSudoku);
		return steps;
	}

	/**
	 * Finds all instances of all types of Uniqueness techniques
	 *
	 * @param newSudoku
	 * @return
	 */
	public List<PuzzleSolutionStep> getAllUniqueness(SudokuPuzzle newSudoku) {
		this.initialize();
		final SudokuPuzzle oldSudoku = this.getSudoku();
		this.setSudoku(newSudoku);
		final List<PuzzleSolutionStep> steps = this.uniquenessSolver.getAllUniqueness();
		this.setSudoku(oldSudoku);
		return steps;
	}

	/**
	 * Find all kinds of Wings
	 *
	 * @param newSudoku
	 * @return
	 */
	public List<PuzzleSolutionStep> getAllWings(SudokuPuzzle newSudoku) {
		this.initialize();
		final SudokuPuzzle oldSudoku = this.getSudoku();
		this.setSudoku(newSudoku);
		final List<PuzzleSolutionStep> steps = this.wingSolver.getAllWings();
		this.setSudoku(oldSudoku);
		return steps;
	}

	/**
	 * Find all Simple Colors
	 *
	 * @param newSudoku
	 * @return
	 */
	public List<PuzzleSolutionStep> findAllSimpleColors(SudokuPuzzle newSudoku) {
		this.initialize();
		final SudokuPuzzle oldSudoku = this.getSudoku();
		this.setSudoku(newSudoku);
		final List<PuzzleSolutionStep> steps = this.coloringSolver.findAllSimpleColors();
		this.setSudoku(oldSudoku);
		return steps;
	}

	/**
	 * Find all Multi Colors
	 *
	 * @param newSudoku
	 * @return
	 */
	public List<PuzzleSolutionStep> findAllMultiColors(SudokuPuzzle newSudoku) {
		this.initialize();
		final SudokuPuzzle oldSudoku = this.getSudoku();
		this.setSudoku(newSudoku);
		final List<PuzzleSolutionStep> steps = this.coloringSolver.findAllMultiColors();
		this.setSudoku(oldSudoku);
		return steps;
	}

	/**
	 * Find all simple chains (X-Chain, XY-Chain, Remote Pairs, Turbot Fish).
	 *
	 * @param newSudoku
	 * @return
	 */
	public List<PuzzleSolutionStep> getAllChains(SudokuPuzzle newSudoku) {
		this.initialize();
		final SudokuPuzzle oldSudoku = this.getSudoku();
		this.setSudoku(newSudoku);
		final List<PuzzleSolutionStep> steps = this.chainSolver.getAllChains();
		this.setSudoku(oldSudoku);
		return steps;
	}

	/**
	 * Finds all ALS-XZ, ALS-XY and ALS-Chains.
	 *
	 * @param newSudoku
	 * @param doXz
	 * @param doXy
	 * @param doChain
	 * @return
	 */
	public List<PuzzleSolutionStep> getAllAlses(SudokuPuzzle newSudoku, boolean doXz, boolean doXy, boolean doChain) {
		this.initialize();
		final SudokuPuzzle oldSudoku = this.getSudoku();
		this.setSudoku(newSudoku);
		final List<PuzzleSolutionStep> steps = this.alsSolver.getAllAlses(doXz, doXy, doChain);
		this.setSudoku(oldSudoku);
		return steps;
	}

	/**
	 * Get all Death Blossoms
	 *
	 * @param newSudoku
	 * @return
	 */
	public List<PuzzleSolutionStep> getAllDeathBlossoms(SudokuPuzzle newSudoku) {
		this.initialize();
		final SudokuPuzzle oldSudoku = this.getSudoku();
		this.setSudoku(newSudoku);
		final List<PuzzleSolutionStep> steps = this.alsSolver.getAllDeathBlossoms();
		this.setSudoku(oldSudoku);
		return steps;
	}

	/**
	 * Finds all Sue de Coqs
	 *
	 * @param newSudoku
	 * @return
	 */
	public List<PuzzleSolutionStep> getAllSueDeCoqs(SudokuPuzzle newSudoku) {
		this.initialize();
		final SudokuPuzzle oldSudoku = this.getSudoku();
		this.setSudoku(newSudoku);
		final List<PuzzleSolutionStep> steps = this.miscellaneousSolver.getAllSueDeCoqs();
		this.setSudoku(oldSudoku);
		return steps;
	}

	/**
	 * Finds all normal Nice Loops/AICs
	 *
	 * @param newSudoku
	 * @return
	 */
	public List<PuzzleSolutionStep> getAllNiceLoops(SudokuPuzzle newSudoku) {
		this.initialize();
		final SudokuPuzzle oldSudoku = this.getSudoku();
		this.setSudoku(newSudoku);
		final List<PuzzleSolutionStep> steps = this.tablingSolver.getAllNiceLoops();
		this.setSudoku(oldSudoku);
		return steps;
	}

	/**
	 * Find all Grouped Nice Loops/AICs
	 *
	 * @param newSudoku
	 * @return
	 */
	public List<PuzzleSolutionStep> getAllGroupedNiceLoops(SudokuPuzzle newSudoku) {
		this.initialize();
		final SudokuPuzzle oldSudoku = this.getSudoku();
		this.setSudoku(newSudoku);
		final List<PuzzleSolutionStep> steps = this.tablingSolver.getAllGroupedNiceLoops();
		this.setSudoku(oldSudoku);
		return steps;
	}

	/**
	 * Finds all Forcing Chains
	 *
	 * @param newSudoku
	 * @return
	 */
	public List<PuzzleSolutionStep> getAllForcingChains(SudokuPuzzle newSudoku) {
		this.initialize();
		final SudokuPuzzle oldSudoku = this.getSudoku();
		this.setSudoku(newSudoku);
		final List<PuzzleSolutionStep> steps = this.tablingSolver.getAllForcingChains();
		this.setSudoku(oldSudoku);
		return steps;
	}

	/**
	 * Finds all Forcing Nets
	 *
	 * @param newSudoku
	 * @return
	 */
	public List<PuzzleSolutionStep> getAllForcingNets(SudokuPuzzle newSudoku) {
		this.initialize();
		final SudokuPuzzle oldSudoku = this.getSudoku();
		this.setSudoku(newSudoku);
		final List<PuzzleSolutionStep> steps = this.tablingSolver.getAllForcingNets();
		this.setSudoku(oldSudoku);
		return steps;
	}

	/**
	 * Finds all Templates steps
	 *
	 * @param newSudoku
	 * @return
	 */
	public List<PuzzleSolutionStep> getAllTemplates(SudokuPuzzle newSudoku) {
		this.initialize();
		final SudokuPuzzle oldSudoku = this.getSudoku();
		this.setSudoku(newSudoku);
		final List<PuzzleSolutionStep> steps = this.templateSolver.getAllTemplates();
		this.setSudoku(oldSudoku);
		return steps;
	}

	/******************************************************************************************************************/
	/* END EXPOSE PUBLIC APIs */
	/******************************************************************************************************************/

	/******************************************************************************************************************/
	/* SETS */
	/******************************************************************************************************************/

	/**
	 * Returns the {@link #candidates}. Recalculates them if they are dirty.
	 *
	 * @return
	 */
	public SudokuSet[] getCandidates() {
		if (this.candidatesDirty) {
			this.initCandidates();
		}
		return this.candidates;
	}

	/**
	 * Returns the {@link #positions}. Recalculates them if they are dirty.
	 *
	 * @return
	 */
	public SudokuSet[] getPositions() {
		if (this.positionsDirty) {
			this.initPositions();
		}
		return this.positions;
	}

	/**
	 * Create the sets that contain all cells, in which a specific candidate is
	 * still present.
	 */
	private void initCandidates() {
		if (this.candidatesDirty) {
			for (int i = 1; i < this.candidates.length; i++) {
				this.candidates[i].clear();
			}
			final short[] cells = this.sudoku.getCells();
			for (int i = 0; i < cells.length; i++) {
				final int[] cands = SudokuPuzzle.POSSIBLE_VALUES[cells[i]];
				for (int j = 0; j < cands.length; j++) {
					this.candidates[cands[j]].add(i);
				}
			}
			this.candidatesDirty = false;
		}
	}

	/**
	 * Create the sets that contain all cells, in which a specific candidate is
	 * already set.
	 */
	private void initPositions() {
		if (this.positionsDirty) {
			for (int i = 1; i < this.positions.length; i++) {
				this.positions[i].clear();
			}
			final int[] values = this.sudoku.getValues();
			for (int i = 0; i < values.length; i++) {
				if (values[i] != 0) {
					this.positions[values[i]].add(i);
				}
			}
			this.positionsDirty = false;
		}
	}

	/**
	 * Returns the {@link #candidatesAllowed}. Recalculates them if they are dirty.
	 *
	 * @return
	 */
	public SudokuSet[] getCandidatesAllowed() {
		if (this.candidatesAllowedDirty) {
			this.initCandidatesAllowed();
		}
		return this.candidatesAllowed;
	}

	/**
	 * Returns the {@link #emptyCells}. Recalculates them if they are dirty.
	 *
	 * @return
	 */
	public SudokuSet getEmptyCells() {
		if (this.candidatesAllowedDirty) {
			this.initCandidatesAllowed();
		}
		return this.emptyCells;
	}

	/**
	 * Create the sets that contain all cells, in which a specific candidate is
	 * still valid.
	 */
	private void initCandidatesAllowed() {
		if (this.candidatesAllowedDirty) {
			this.emptyCells.setAll();
			for (int i = 1; i < this.candidatesAllowed.length; i++) {
				this.candidatesAllowed[i].setAll();
			}
			final int[] values = this.sudoku.getValues();
			for (int i = 0; i < values.length; i++) {
				if (values[i] != 0) {
					this.candidatesAllowed[values[i]].andNot(SudokuPuzzle.buddies[i]);
					this.emptyCells.remove(i);
				}
			}
			for (int i = 1; i < this.candidatesAllowed.length; i++) {
				this.candidatesAllowed[i].and(this.emptyCells);
			}
			this.candidatesAllowedDirty = false;
		}
	}

	/******************************************************************************************************************/
	/* END SETS */
	/******************************************************************************************************************/

	/******************************************************************************************************************/
	/* TEMPLATES */
	/******************************************************************************************************************/

	/**
	 * Returns delCandTemplates.
	 *
	 * @param initLists
	 * @return
	 */
	protected SudokuSet[] getDelCandTemplates(boolean initLists) {
		if ((initLists && this.templatesListDirty) || (!initLists && this.templatesDirty)) {
			this.initCandTemplates(initLists);
		}
		return this.delCandTemplates;
	}

	/**
	 * Returns setValueTemplates.
	 *
	 * @param initLists
	 * @return
	 */
	protected SudokuSet[] getSetValueTemplates(boolean initLists) {
		if ((initLists && this.templatesListDirty) || (!initLists && this.templatesDirty)) {
			this.initCandTemplates(initLists);
		}
		return this.setValueTemplates;
	}

	/**
	 * Initializiation of templates:
	 *
	 * The following templates are forbidden and will be ignored: All templates
	 * which have no 1 at at least one already set position (positions & template)
	 * != positions All templats which have at least one 1 at a position thats
	 * already forbidden (~(positions | allowedPositions) & template) != 0
	 *
	 * When the valid templates are known: All valid templates OR: Candidate can be
	 * eliminated from all positions that are 0 All templates AND: Candidate can be
	 * set in all cells that have a 1 left Calculate all valid combinations of
	 * templates for two different candidates (OR), AND all results: Gives Hidden
	 * Pairs (eliminate all candidates from the result, that dont belong to the two
	 * start candidates). - not implemented yet
	 *
	 * If <code>initLists</code> is set make the following additions (for
	 * {@link TemplateSolver}): All templates, that have a one at the result of an
	 * AND of all templates of another candidate, are forbidden All templates, that
	 * dont have at least one non overlapping combination with at least one template
	 * of another candidate, are forbidden.
	 *
	 * @param initLists
	 */
	private void initCandTemplates(boolean initLists) {
///*K*/ Not here!!!
//        if (! Options.getInstance().checkTemplates) {
//            return;
//        }
		this.templateAnz++;
		final long nanos = System.nanoTime();
		if ((initLists && this.templatesListDirty) || (!initLists && this.templatesDirty)) {
			final SudokuSetBase[] allowedPositions = this.getCandidates();
			final SudokuSet[] setPositions = this.getPositions();
			final SudokuSetBase[] templates = SudokuPuzzle.templates;
			final SudokuSetBase[] forbiddenPositions = new SudokuSetBase[10]; // eine 1 an jeder Position, an der Wert
																				// nicht mehr sein darf

//        SudokuSetBase setMask = new SudokuSetBase();
//        SudokuSetBase delMask = new SudokuSetBase();
//        SudokuSetBase temp = new SudokuSetBase();
			for (int i = 1; i <= 9; i++) {
				this.setValueTemplates[i].setAll();
				this.delCandTemplates[i].clear();
				this.candTemplates.get(i).clear();

				// eine 1 an jeder verbotenen Position ~(positions | allowedPositions)
				forbiddenPositions[i] = new SudokuSetBase();
				forbiddenPositions[i].set(setPositions[i]);
				forbiddenPositions[i].or(allowedPositions[i]);
				forbiddenPositions[i].not();
			}
			for (int i = 0; i < templates.length; i++) {
				for (int j = 1; j <= 9; j++) {
					if (!setPositions[j].andEquals(templates[i])) {
						// Template hat keine 1 an einer bereits gesetzten Position
						continue;
					}
					if (!forbiddenPositions[j].andEmpty(templates[i])) {
						// Template hat eine 1 an einer verbotenen Position
						continue;
					}
					// Template ist für Kandidaten erlaubt!
					this.setValueTemplates[j].and(templates[i]);
					this.delCandTemplates[j].or(templates[i]);
					if (initLists) {
						this.candTemplates.get(j).add(templates[i]);
					}
				}
			}

			// verfeinern
			if (initLists) {
				int removals = 0;
				do {
					removals = 0;
					for (int j = 1; j <= 9; j++) {
						this.setValueTemplates[j].setAll();
						this.delCandTemplates[j].clear();
						final ListIterator<SudokuSetBase> it = this.candTemplates.get(j).listIterator();
						while (it.hasNext()) {
							final SudokuSetBase template = it.next();
							boolean removed = false;
							for (int k = 1; k <= 9; k++) {
								if (k != j && !template.andEmpty(this.setValueTemplates[k])) {
									it.remove();
									removed = true;
									removals++;
									break;
								}
							}
							if (!removed) {
								this.setValueTemplates[j].and(template);
								this.delCandTemplates[j].or(template);
							}
						}
					}
				} while (removals > 0);
			}

			for (int i = 1; i <= 9; i++) {
				this.delCandTemplates[i].not();
			}
			this.templatesDirty = false;
			if (initLists) {
				this.templatesListDirty = false;
			}
		}
		this.templateNanos += System.nanoTime() - nanos;
	}

	/**
	 * @return the stepNumber
	 */
	public int getStepNumber() {
		return this.stepNumber;
	}
	/******************************************************************************************************************/
	/* END TEMPLATES */
	/******************************************************************************************************************/

	/******************************************************************************************************************/
	/* ALS AND RC CACHE */
	/******************************************************************************************************************/

	/**
	 * Convenience method for {@link #getAlses(boolean) }.
	 *
	 * @return
	 */
	public List<Als> getAlses() {
		return this.getAlses(false);
	}

	/**
	 * Gets all ALS from {@link #sudoku}. If <code>onlyLargerThanOne</code> is set,
	 * ALS of size 1 (cells containing two candidates) are ignored.<br>
	 * The work is delegated to
	 * {@link #collectAllAlsesForHouse(int[][], sudoku.SudokuPuzzle, java.util.List, boolean)}.<br>
	 * <br>
	 * The list is cached in {@link #alsesOnlyLargerThanOne} or
	 * {@link #alsesWithOne} respectively and only recomputed if necessary.
	 *
	 * @param onlyLargerThanOne
	 * @return
	 */
	public List<Als> getAlses(boolean onlyLargerThanOne) {
		if (onlyLargerThanOne) {
			if (this.alsesOnlyLargerThanOneStepNumber == this.stepNumber) {
				return this.alsesOnlyLargerThanOne;
			} else {
				this.alsesOnlyLargerThanOne = this.doGetAlses(onlyLargerThanOne);
				this.alsesOnlyLargerThanOneStepNumber = this.stepNumber;
				return this.alsesOnlyLargerThanOne;
			}
		} else {
			if (this.alsesWithOneStepNumber == this.stepNumber) {
				return this.alsesWithOne;
			} else {
				this.alsesWithOne = this.doGetAlses(onlyLargerThanOne);
				this.alsesWithOneStepNumber = this.stepNumber;
				return this.alsesWithOne;
			}
		}
	}

	/**
	 * Does some statistics and starts the recursive search for every house.
	 *
	 * @param onlyLargerThanOne
	 * @return
	 */
	private List<Als> doGetAlses(boolean onlyLargerThanOne) {
		final long actNanos = System.nanoTime();

		// this is the list we will be working with
		final List<Als> alses = new ArrayList<Als>(300);
		alses.clear();

		// recursion is started once for every cell in every house
		for (int i = 0; i < SudokuPuzzle.ALL_UNITS.length; i++) {
			for (int j = 0; j < SudokuPuzzle.ALL_UNITS[i].length; j++) {
				this.indexSet.clear();
				this.candSets[0] = 0;
				this.checkAlsRecursive(0, j, SudokuPuzzle.ALL_UNITS[i], alses, onlyLargerThanOne);
			}
		}

		// compute fields
		for (final Als als : alses) {
			als.computeFields(this);
		}

		this.alsNanos += (System.nanoTime() - actNanos);
		this.anzAlsCalls++;

		return alses;
	}

	/**
	 * Does a recursive ALS search over one house (<code>indexe</code>).
	 *
	 * @param anzahl            Number of cells already contained in
	 *                          {@link #indexSet}.
	 * @param startIndex        First index in <code>indexe</code> to check.
	 * @param indexe            Array with all the cells of the current house.
	 * @param alses             List for all newly found ALS
	 * @param onlyLargerThanOne Allow ALS with only one cell (bivalue cells)
	 */
	private void checkAlsRecursive(int anzahl, int startIndex, int[] indexe, List<Als> alses,
			boolean onlyLargerThanOne) {
		anzahl++;
		if (anzahl > indexe.length - 1) {
			// end recursion (no more than 8 cells in an ALS possible)
			return;
		}
		for (int i = startIndex; i < indexe.length; i++) {
			final int houseIndex = indexe[i];
			if (this.sudoku.getValue(houseIndex) != 0) {
				// cell already set -> ignore
				continue;
			}
			this.indexSet.add(houseIndex);
			this.candSets[anzahl] = (short) (this.candSets[anzahl - 1] | this.sudoku.getCell(houseIndex));

			// if the number of candidates is excatly one larger than the number
			// of cells, an ALS was found
			if (SudokuPuzzle.ANZ_VALUES[this.candSets[anzahl]] - anzahl == 1) {
				if (!onlyLargerThanOne || this.indexSet.size() > 1) {
					// found one -> save it if it doesnt exist already
					this.anzAls++;
					final Als newAls = new Als(this.indexSet, this.candSets[anzahl]);
					if (!alses.contains(newAls)) {
						alses.add(newAls);
					} else {
						this.doubleAls++;
					}
				}
			}

			// continue recursion
			this.checkAlsRecursive(anzahl, i + 1, indexe, alses, onlyLargerThanOne);

			// remove current cell
			this.indexSet.remove(houseIndex);
		}
	}

	/**
	 * Do some statistics.
	 *
	 * @return
	 */
	public String getAlsStatistics() {
		return "Statistic for getAls(): number of calls: " + this.anzAlsCalls + ", total time: "
				+ (this.alsNanos / 1000) + "us, average: " + (this.alsNanos / this.anzAlsCalls / 1000) + "us\r\n"
				+ "    anz: " + this.anzAls + "/" + (this.anzAls / this.anzAlsCalls) + ", double: " + this.doubleAls
				+ "/" + (this.doubleAls / this.anzAlsCalls) + " res: " + (this.anzAls - this.doubleAls) + "/"
				+ ((this.anzAls - this.doubleAls) / this.anzAlsCalls);
	}

	/**
	 * Lists of all RCs of the current sudoku are needed by more than one solver,
	 * but caching them can greatly increase performance.
	 *
	 * @param alses
	 * @param allowOverlap
	 * @return
	 */
	public List<RestrictedCommon> getRestrictedCommons(List<Als> alses, boolean allowOverlap) {
		if (this.lastRcStepNumber != this.stepNumber || this.lastRcAllowOverlap != allowOverlap
				|| this.lastRcAlsList != alses || this.lastRcOnlyForward != this.rcOnlyForward) {
			// recompute
			if (this.startIndices == null || this.startIndices.length < alses.size()) {
				this.startIndices = new int[(int) (alses.size() * 1.5)];
				this.endIndices = new int[(int) (alses.size() * 1.5)];
			}
			this.restrictedCommons = this.doGetRestrictedCommons(alses, allowOverlap);
			// store caching flags
			this.lastRcStepNumber = this.stepNumber;
			this.lastRcAllowOverlap = allowOverlap;
			this.lastRcOnlyForward = this.rcOnlyForward;
			this.lastRcAlsList = alses;
		}
		return this.restrictedCommons;
	}

	/**
	 * Getter for {@link #startIndices}.
	 *
	 * @return
	 */
	public int[] getStartIndices() {
		return this.startIndices;
	}

	/**
	 * Getter for {@link #endIndices}.
	 *
	 * @return
	 */
	public int[] getEndIndices() {
		return this.endIndices;
	}

	/**
	 * Setter for {@link #rcOnlyForward}.
	 *
	 * @param rof
	 */
	public void setRcOnlyForward(boolean rof) {
		this.rcOnlyForward = rof;
	}

	/**
	 * Getter for {@link #rcOnlyForward}.
	 *
	 * @return
	 */
	public boolean isRcOnlyForward() {
		return this.rcOnlyForward;
	}

	/**
	 * For all combinations of two ALS check whether they have one or two RC(s). An
	 * RC is a candidate that is common to both ALS and where all instances of that
	 * candidate in both ALS see each other.<br>
	 * ALS with RC(s) may overlap as long as the overlapping area doesnt contain an
	 * RC.<br>
	 * Two ALS can have a maximum of two RCs.<br>
	 * The index of the first RC for {@link #alses}[i] is written to
	 * {@link #startIndices}[i], the index of the last RC + 1 is written to
	 * {@link #endIndices}[i] (needed for chain search).<br>
	 * <br>
	 *
	 * If {@link #rcOnlyForward} is set to <code>true</code>, only RCs with
	 * references to ALS with a greater index are collected. For ALS-XZ und
	 * ALS-XY-Wing this is irrelevant. For ALS-Chains it greatly improves
	 * performance, but not all chains are found. This is the default when solving
	 * puzzles, {@link #rcOnlyForward} <code>false</code> is the default for search
	 * for all steps.
	 *
	 * @param withOverlap If <code>false</code> overlapping ALS are not allowed
	 */
	private List<RestrictedCommon> doGetRestrictedCommons(List<Als> alses, boolean withOverlap) {
		this.rcAnzCalls++;
		long actNanos = 0;
		actNanos = System.nanoTime();
		// store the calculation mode
		this.lastRcOnlyForward = this.rcOnlyForward;
		// delete all RCs from the last run
		final List<RestrictedCommon> rcs = new ArrayList<RestrictedCommon>(2000);
		// Try all combinations of alses
		for (int i = 0; i < alses.size(); i++) {
			final Als als1 = alses.get(i);
			this.startIndices[i] = rcs.size();
			// if (DEBUG) System.out.println("als1: " + PuzzleSolutionStep.getAls(als1));
			int start = 0;
			if (this.rcOnlyForward) {
				start = i + 1;
			}
			for (int j = start; j < alses.size(); j++) {
				if (i == j) {
					continue;
				}
				final Als als2 = alses.get(j);
				// check whether the ALS overlap (intersectionSet is needed later on anyway)
				this.intersectionSet.set(als1.indices);
				this.intersectionSet.and(als2.indices);
				if (!withOverlap && !this.intersectionSet.isEmpty()) {
					// overlap is not allowed!
					continue;
				}
				// if (DEBUG) System.out.println("als2: " + PuzzleSolutionStep.getAls(als2));
				// restricted common: all buddies + the positions of the candidates themselves
				// ANDed
				// check whether als1 and als2 have common candidates
				this.possibleRestrictedCommonsSet = als1.candidates;
				this.possibleRestrictedCommonsSet &= als2.candidates;
				// possibleRestrictedCommons now contains all candidates common to both ALS
				if (this.possibleRestrictedCommonsSet == 0) {
					// nothing to do!
					continue;
				}
				// number of RC candidates found for this ALS combination
				int rcAnz = 0;
				RestrictedCommon newRC = null;
				final int[] prcs = SudokuPuzzle.POSSIBLE_VALUES[this.possibleRestrictedCommonsSet];
				for (int k = 0; k < prcs.length; k++) {
					final int cand = prcs[k];
					// Get all positions of cand in both ALS
					this.restrictedCommonIndexSet.set(als1.indicesPerCandidat[cand]);
					this.restrictedCommonIndexSet.or(als2.indicesPerCandidat[cand]);
					// non of these positions may be in the overlapping area of the two ALS
					if (!this.restrictedCommonIndexSet.andEmpty(this.intersectionSet)) {
						// at least on occurence of cand is in overlap -> forbidden
						continue;
					}
					// now check if all those candidates see each other
					this.restrictedCommonBuddiesSet.setAnd(als1.buddiesAlsPerCandidat[cand],
							als2.buddiesAlsPerCandidat[cand]);
					// we now know all common buddies, all common candidates must be in that set
					if (this.restrictedCommonIndexSet.andEquals(this.restrictedCommonBuddiesSet)) {
						// found -> cand is RC
						if (rcAnz == 0) {
							newRC = new RestrictedCommon(i, j, cand);
							rcs.add(newRC);
							this.anzRcs++;
						} else {
							newRC.setCand2(cand);
						}
						rcAnz++;
					}
				}
			}
			this.endIndices[i] = rcs.size();
		}
		actNanos = System.nanoTime() - actNanos;
		this.rcNanos += actNanos;
		return rcs;
	}

	/**
	 * Do some statistics.
	 *
	 * @return
	 */
	public String getRCStatistics() {
		return "Statistic for getRestrictedCommons(): number of calls: " + this.rcAnzCalls + ", total time: "
				+ (this.rcNanos / 1000) + "us, average: " + (this.rcNanos / this.rcAnzCalls / 1000) + "us\r\n"
				+ "    anz: " + this.anzRcs + "/" + (this.anzRcs / this.rcAnzCalls);
	}

	/******************************************************************************************************************/
	/* END ALS AND RC CACHE */
	/******************************************************************************************************************/

}
