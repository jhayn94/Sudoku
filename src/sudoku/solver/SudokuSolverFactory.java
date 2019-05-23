package sudoku.solver;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class contains various utility methods for interacting with
 * SudokuSolvers. Only one is used by the UI, but more can be created in
 * background threads if needed.
 */
public class SudokuSolverFactory {

	private static final SudokuSolver defaultSolver = new SudokuSolver();

	private static List<SolverInstance> instances = new ArrayList<SolverInstance>();

	private static final Thread thread = new Thread(() -> {
		doCleanup();
	});

	private static void doCleanup() {
		while (true) {
			synchronized (thread) {
				// cleanup for defaultSolver
				defaultSolver.getStepFinder().cleanUp();

				// now check all other solvers
				final Iterator<SolverInstance> iterator = instances.iterator();
				while (iterator.hasNext()) {
					final SolverInstance act = iterator.next();
					if (act.inUse == false && (System.currentTimeMillis() - act.lastUsedAt) > SOLVER_TIMEOUT) {
						iterator.remove();
					} else {
						act.instance.getStepFinder().cleanUp();
					}
				}
			}
			try {
				Thread.sleep(SOLVER_TIMEOUT);
			} catch (final InterruptedException ex) {
				// do nothing
			}
		}
	}

	/** The default cleanup time for SudokuSolver instances. */
	private static final long SOLVER_TIMEOUT = 5 * 60 * 1000;

	/**
	 * One entry in {@link #instances}.
	 */
	private static class SolverInstance {
		/** The solver held in this entry. */
		SudokuSolver instance = null;
		/** <code>true</code>, if the solver has been handed out by the factory. */
		boolean inUse = true;
		/** Last time the solver was returned to the factory. */
		long lastUsedAt = -1;

		/**
		 * Create a new entry for {@link #instances}.
		 *
		 * @param instance
		 */
		private SolverInstance(SudokuSolver instance) {
			this.instance = instance;
		}
	}

	/** Start the thread */
	static {
		thread.start();
	}

	/**
	 * This class is a utility class that cannot be instantiated.
	 */
	private SudokuSolverFactory() {
		/* class cannot be instantiated! */ }

	/**
	 * Get the {@link #defaultSolver}.
	 *
	 * @return
	 */
	public static SudokuSolver getDefaultSolverInstance() {
		return defaultSolver;
	}

	/**
	 * Hand out an ununsed solver or create a new one if necessary.
	 *
	 * @return
	 */
	public static SudokuSolver getInstance() {
		SudokuSolver ret = null;
		synchronized (thread) {
			for (final SolverInstance act : instances) {
				if (act.inUse == false) {
					act.inUse = true;
					ret = act.instance;
					break;
				}
			}
			if (ret == null) {
				ret = new SudokuSolver();
				instances.add(new SolverInstance(ret));
			}
		}
		return ret;
	}

	/**
	 * Gives a solver back to the factory.
	 *
	 * @param solver
	 */
	public static void giveBack(SudokuSolver solver) {
		synchronized (thread) {
			for (final SolverInstance act : instances) {
				if (act.instance == solver) {
					act.inUse = false;
					act.lastUsedAt = System.currentTimeMillis();
					break;
				}
			}
		}
	}
}
