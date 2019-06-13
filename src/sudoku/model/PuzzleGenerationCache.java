package sudoku.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sudoku.core.HodokuFacade;

public class PuzzleGenerationCache {

	private static final Logger LOG = LogManager.getLogger(PuzzleGenerationCache.class);

	private static PuzzleGenerationCache instance;

	public static PuzzleGenerationCache getInstance() {
		if (PuzzleGenerationCache.instance == null) {
			PuzzleGenerationCache.instance = new PuzzleGenerationCache();
		}
		return PuzzleGenerationCache.instance;
	}

	private static final int MAX_CACHE_SIZE = 10;

	private final List<String> cachedPuzzles;

	private Thread puzzleGenerationThread;

	private boolean stopped;

	private PuzzleGenerationCache() {
		this.stopped = true;
		this.cachedPuzzles = new ArrayList<>();
		this.startThread();
	}

	public void onSettingsChanged() {
		LOG.info("Cleared puzzle cache.");
		this.stopThread();
		this.cachedPuzzles.clear();
		this.startThread();
	}

	public synchronized String getNextPuzzleString() {
		while (this.cachedPuzzles.isEmpty()) {
			try {
				LOG.info("Waiting for puzzle to be available...");
				Thread.sleep(3000);
			} catch (final InterruptedException e) {
				LOG.error("{}", e);
			}
		}
		this.startThread();
		return this.cachedPuzzles.remove(0);
	}

	private void startThread() {
		if (this.stopped) {
			this.stopped = false;
			this.puzzleGenerationThread = new Thread(this::generatePuzzles);
			this.puzzleGenerationThread.start();
		}
	}

	private void stopThread() {
		this.stopped = true;
		while (this.puzzleGenerationThread.isAlive()) {
			try {
				Thread.sleep(500);
			} catch (final InterruptedException e) {
				LOG.error("{}", e);
			}
		}
	}

	private void generatePuzzles() {
		LOG.info("Puzzle generation thread started, initial size = {}", this.cachedPuzzles.size());
		while (!this.stopped && this.cachedPuzzles.size() < MAX_CACHE_SIZE) {
			final String sudokuString = HodokuFacade.getInstance().generateSudokuString();
			if (!sudokuString.isEmpty()) {
				synchronized (this.cachedPuzzles) {
					this.cachedPuzzles.add(sudokuString);
					LOG.info("Puzzle Added, size now {}: {}", this.cachedPuzzles.size(), sudokuString);
				}
			}
		}
		this.stopped = true;
	}
}
