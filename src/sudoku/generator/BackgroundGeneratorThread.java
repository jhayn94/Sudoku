/*
 * Copyright (C) 2008-12  Bernhard Hobiger
 *
 * This file is part of HoDoKu.
 *
 * HoDoKu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * HoDoKu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with HoDoKu. If not, see <http://www.gnu.org/licenses/>.
 */

package sudoku.generator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sudoku.core.PuzzleConfiguration;
import sudoku.core.SolverConfiguration;
import sudoku.view.util.PuzzleDifficulty;
import sudoku.view.util.PuzzleType;
import sudoku.view.util.SolutionTechniqueConfiguration;

/**
 * One instance of this class is used to control the background creation of
 * sudokus. The following puzzles are created automatically and stored in
 * {@link PuzzleConfiguration}:
 * <ul>
 * <li>10 sudokus for every difficulty level</li>
 * <li>10 sudokus for {@link GameMode#LEARNING}</li>
 * <li>10 sudokus for {@link GameMode#PRACTISING}</li>
 * </ul>
 * The puzzle creation is triggered by the following events:
 * <ul>
 * <li>At program startup: missing puzzles are created (starting with the
 * current {@link DifficultyLevel})</li>
 * <li>When the step configuration has been changed: all types of puzzles are
 * created again.</li>
 * <li>When the training steps configuration has changed: <code>LEARNING</code>
 * and <code>PRACTISING</code> puzzles are redone</li>
 * <li>When the {@link DifficultyLevel} is changed in the GUI:
 * <code>PRACTISING</code> puzzles are redone</li>
 * <li>When a configuration is loaded from a file, all puzzles are redone</li>
 * </ul>
 * This class is a singleton.
 *
 */
public final class BackgroundGeneratorThread implements Runnable {

	private static final Logger LOG = LogManager.getLogger(BackgroundGeneratorThread.class);

	private static BackgroundGeneratorThread instance;

	private final BackgroundGenerator generator;

	private final Thread puzzleCreationThread;

	private boolean shouldRunAgainAfterwards = false;

	private boolean isThreadStarted = false;

	private BackgroundGeneratorThread() {
		this.puzzleCreationThread = new Thread(this);
		this.generator = new BackgroundGenerator();
	}

	public static BackgroundGeneratorThread getInstance() {
		if (instance == null) {
			instance = new BackgroundGeneratorThread();
		}
		return instance;
	}

	/**
	 * Checks, if a puzzle matching the requirements is available.
	 *
	 * @param puzzleDifficulty
	 * @param mode
	 * @return
	 */
	public synchronized String getSudoku(PuzzleDifficulty puzzleDifficulty, PuzzleType puzzleType) {
		final String[] cachedPuzzles = this.getPuzzleArray(puzzleDifficulty, puzzleType);

		String newPuzzle = null;
		if (cachedPuzzles[0] != null) {
			newPuzzle = cachedPuzzles[0];
			for (int i = 1; i < cachedPuzzles.length; i++) {
				cachedPuzzles[i - 1] = cachedPuzzles[i];
			}
			cachedPuzzles[cachedPuzzles.length - 1] = null;
		}
		LOG.info("Got puzzle from cache: " + puzzleDifficulty.name() + "/" + newPuzzle);
		this.startCreation();
		return newPuzzle;
	}

	private String[] getPuzzleArray(PuzzleDifficulty puzzleDifficulty, PuzzleType puzzleType) {
		String[] puzzles = null;
		switch (puzzleType) {
		case STANDARD:
			puzzles = PuzzleConfiguration.getInstance().getStandardPuzzles()[puzzleDifficulty.ordinal() - 1];
			break;
		case SOLVED_UP_TO_TACTIC:
			puzzles = PuzzleConfiguration.getInstance().getLearningPuzzles();
			break;
		case CONTAINS_SPECIFIC_TACTIC:
			puzzles = PuzzleConfiguration.getInstance().getPracticeTechniquePuzzles();
			break;
		}
		return puzzles;
	}

	/**
	 * Adds the given puzzle string to the cache, where rawSudoku is an 81 digit
	 * string representing the givens.
	 */
	private synchronized void addPuzzleToCache(PuzzleDifficulty puzzleDifficulty, PuzzleType puzzleType,
			String rawSudoku) {
		// get the correct puzzles from PuzzleCache
		final String[] cachedPuzzles = this.getPuzzleArray(puzzleDifficulty, puzzleType);
		for (int i = 0; i < cachedPuzzles.length; i++) {
			if (cachedPuzzles[i] == null) {
				cachedPuzzles[i] = rawSudoku;
				break;
			}
		}
	}

	/**
	 * The step configuration has been changed: reset everything and start over.
	 */
	public synchronized void resetAll() {
		final String[][] puzzles = PuzzleConfiguration.getInstance().getStandardPuzzles();
		for (int i = 0; i < puzzles.length; i++) {
			for (int j = 0; j < puzzles[i].length; j++) {
				puzzles[i][j] = null;
			}
		}
		this.resetTrainingPractising();
	}

	/**
	 * The training configuration has changed: recreate the LEARNING and PRACTISING
	 * puzzles and start over.
	 */
	public synchronized void resetTrainingPractising() {
		String[] puzzles1 = PuzzleConfiguration.getInstance().getLearningPuzzles();
		for (int i = 0; i < puzzles1.length; i++) {
			puzzles1[i] = null;
		}
		puzzles1 = PuzzleConfiguration.getInstance().getPracticeTechniquePuzzles();
		for (int i = 0; i < puzzles1.length; i++) {
			puzzles1[i] = null;
		}
		this.startCreation();
	}

	/**
	 * The level has been changed, check if the PRACTISING puzzles have to be
	 * recreated.
	 *
	 * @param newLevel
	 */
	public synchronized void setNewLevel(int newLevel) {
		final int maxTrainingLevel = this.getTrainingLevel();
		if (maxTrainingLevel == -1 || newLevel < maxTrainingLevel) {
			// we cant create suitable puzzles -> ignore
			return;
		}
		if (newLevel == PuzzleConfiguration.getInstance().getPractisingPuzzlesLevel()) {
			// nothing to do!
			return;
		}
		final String[] puzzles = PuzzleConfiguration.getInstance().getPracticeTechniquePuzzles();
		for (int i = 0; i < puzzles.length; i++) {
			puzzles[i] = null;
		}
		SolverConfiguration.getInstance().setPracticingPuzzlesLevel(newLevel);
		this.startCreation();
	}

	/**
	 * Schedules a new creation run. If the thread is not yet running, it is started
	 * manually. The runAgainAfterwards flag is set to tell the thread to run again
	 * if is is already running
	 */
	public void startCreation() {
		if (this.puzzleCreationThread == null) {
			return;
		}
		if (!this.isThreadStarted) {
			this.puzzleCreationThread.start();
			this.isThreadStarted = true;
			LOG.info("BackgroundCreationThread started!");
		}
		synchronized (this.puzzleCreationThread) {
			this.shouldRunAgainAfterwards = true;
			LOG.info("new creation request scheduled!");
			// Wake up the thread if it is sleeping
			this.puzzleCreationThread.notify();
		}
	}

	/**
	 * Checks and fills all missing puzzles in the cache.
	 */
	@Override
	public void run() {
		while (!this.puzzleCreationThread.isInterrupted()) {
			try {
				synchronized (this.puzzleCreationThread) {
					if (this.shouldRunAgainAfterwards == false) {
						this.puzzleCreationThread.wait();
					}
					if (this.shouldRunAgainAfterwards) {
						this.shouldRunAgainAfterwards = false;
					} else {
						continue;
					}
				}
				PuzzleDifficulty puzzleDifficulty = null;
				PuzzleType puzzleType = null;
				while (puzzleDifficulty == null && !this.puzzleCreationThread.isInterrupted()) {
					synchronized (this) {
						final String[][] puzzles = PuzzleConfiguration.getInstance().getStandardPuzzles();
						for (int i = 0; i < puzzles.length; i++) {
							for (int j = 0; j < puzzles[i].length; j++) {
								if (puzzles[i][j] == null) {
									puzzleDifficulty = PuzzleDifficulty.fromOrdinal(i + 1);
									puzzleType = PuzzleType.STANDARD;
									break;
								}
							}
							if (puzzleDifficulty != null) {
								break;
							}
						}
						final int trainingLevel = this.getTrainingLevel();
						String[] puzzles1 = PuzzleConfiguration.getInstance().getLearningPuzzles();
						if (puzzleDifficulty == null && trainingLevel != -1) {
							for (int i = 0; i < puzzles1.length; i++) {
								if (puzzles1[i] == null) {
									puzzleDifficulty = PuzzleDifficulty.DIABOLICAL;
									puzzleType = PuzzleType.CONTAINS_SPECIFIC_TACTIC;
									break;
								}
							}
						}
						if (trainingLevel != -1
								&& PuzzleConfiguration.getInstance().getPractisingPuzzlesLevel() == -1) {
							this.setNewLevel(PuzzleConfiguration.getInstance().getActiveDifficultyLevel());
						}
						puzzles1 = PuzzleConfiguration.getInstance().getPracticeTechniquePuzzles();
						if (puzzleDifficulty == null && trainingLevel != -1
								&& PuzzleConfiguration.getInstance().getActiveDifficultyLevel() >= trainingLevel) {
							for (int i = 0; i < puzzles1.length; i++) {
								if (puzzles1[i] == null) {
									puzzleDifficulty = PuzzleDifficulty
											.fromOrdinal(PuzzleConfiguration.getInstance().getPractisingPuzzlesLevel());
									puzzleType = PuzzleType.SOLVED_UP_TO_TACTIC;
									break;
								}
							}
						}
					}
					// new puzzle type found?
					if (puzzleDifficulty == null) {
						break;
					}

					final String puzzle = this.generator.generateNewPuzzle(puzzleDifficulty, puzzleType);
					// If no puzzle got created, try again.
					if (puzzle == null) {
						break;
					}
					this.addPuzzleToCache(puzzleDifficulty, puzzleType, puzzle);

					// Continue the loop.
					puzzleDifficulty = null;
					puzzleType = null;
				}
			} catch (final InterruptedException ex) {
				this.puzzleCreationThread.interrupt();
			}
		}
	}

	/**
	 * Utility method: gets the {@link DifficultyLevel} of the most difficult
	 * training step. If no training step is set, -1 is returned.<br>
	 * This method is used in two ways: To decide, if LEARNING/PRACTISING steps
	 * should be created at all (if no training step is enabled, creation will be
	 * impossible), and to decide, if PRACTISING steps have to be redone after a
	 * change of the games current DifficultyLevel (if the current level is lower
	 * than the level of the hardest training step, no new PRACTISING puzzles have
	 * to be created).
	 *
	 * @return
	 */
	private int getTrainingLevel() {
		final SolutionTechniqueConfiguration[] configurations = PuzzleConfiguration.getInstance().getOrgSolverSteps();
		int level = -1;
		for (final SolutionTechniqueConfiguration configuration : configurations) {
			if (configuration.isEnabledTraining()) {
				final int actLevel = configuration.getLevel();
				if (actLevel > level) {
					level = actLevel;
				}
			}
		}
		return level;
	}
}
