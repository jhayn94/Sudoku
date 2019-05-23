
package sudoku.generator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class contains various utility methods for interacting with
 * SudokuGenerators. Only one is used by the UI, but more can be created in
 * background threads if needed.
 */
public final class SudokuGeneratorFactory {

	// The cleanup thread waits this long between executions.
	private static final long DEFAULT_CLEANUP_TIME = 5 * 60 * 1000;

	private static final SudokuGenerator defaultGenerator = new SudokuGenerator();

	private static List<GeneratorInstance> instances = new ArrayList<GeneratorInstance>();

	private static final Thread cleanupThread = new Thread(SudokuGeneratorFactory::doCleanup);

	private static void doCleanup() {
		while (true) {
			synchronized (cleanupThread) {
				final Iterator<GeneratorInstance> iterator = instances.iterator();
				while (iterator.hasNext()) {
					final GeneratorInstance act = iterator.next();
					if (act.inUse == false && (System.currentTimeMillis() - act.lastUsedAt) > DEFAULT_CLEANUP_TIME) {
						iterator.remove();
					}
				}
			}
			try {
				Thread.sleep(DEFAULT_CLEANUP_TIME);
			} catch (final InterruptedException ex) {
				// Nothing to do.
			}
		}
	}

	private static class GeneratorInstance {

		SudokuGenerator instance = null;

		boolean inUse = true;

		long lastUsedAt = -1;

		private GeneratorInstance(SudokuGenerator instance) {
			this.instance = instance;
		}
	}

	static {
		cleanupThread.start();
	}

	private SudokuGeneratorFactory() {
		// Private constructor to prevent instantiation.
	}

	public static SudokuGenerator getDefaultGeneratorInstance() {
		return defaultGenerator;
	}

	public static SudokuGenerator getGeneratorInstance() {
		SudokuGenerator generatorInstance = null;
		synchronized (cleanupThread) {
			for (final GeneratorInstance instance : instances) {
				if (!instance.inUse) {
					instance.inUse = true;
					generatorInstance = instance.instance;
					break;
				}
			}
			if (generatorInstance == null) {
				generatorInstance = new SudokuGenerator();
				instances.add(new GeneratorInstance(generatorInstance));
			}
		}
		return generatorInstance;
	}

	/**
	 * Gives a generator back to the factory, so it can be reassigned.
	 */
	public static void yield(SudokuGenerator generator) {
		synchronized (cleanupThread) {
			for (final GeneratorInstance act : instances) {
				if (act.instance == generator) {
					act.inUse = false;
					act.lastUsedAt = System.currentTimeMillis();
					break;
				}
			}
		}
	}
}
