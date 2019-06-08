package sudoku.state.model.puzzle;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;

import sudoku.core.ViewController;
import sudoku.factories.ModelFactory;
import sudoku.state.model.ApplicationModelState;
import sudoku.state.model.ResetFromModelState;
import sudoku.view.hint.HintTextArea;

/**
 * This class updates the state of the application when the user copies the
 * puzzle to the clipboard
 */
public class PastePuzzleState extends ResetFromModelState {

	private static final Logger LOG = LogManager.getLogger(PastePuzzleState.class);

	private static final String SUDOKU_PUZZLE_REGEX = "([0-9]|\\.){81}";

	public PastePuzzleState(final ApplicationModelState lastState) {
		super(lastState, false);
	}

	@Override
	public void onEnter() {

		final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		try {
			final String puzzleString = (String) clipboard.getData(DataFlavor.stringFlavor);
			if (puzzleString.matches(SUDOKU_PUZZLE_REGEX)) {
				this.sudokuPuzzleValues = ModelFactory.getInstance().createSudokuPuzzleValues(puzzleString);

				this.resetApplicationFromPuzzleState();
				this.resetAllColorStates();
				this.updateAllPuzzleStatsForNewPuzzle();
				this.reapplyActiveFilter();

				this.applicationStateHistory.clearRedoStack();
				this.applicationStateHistory.clearUndoStack();
				this.updateUndoRedoButtons();
				final HintTextArea hintTextArea = ViewController.getInstance().getHintTextArea();
				hintTextArea.getHintTextArea().setText(Strings.EMPTY);
			}

		} catch (UnsupportedFlavorException | IOException e) {
			LOG.error("{}", e);
		}
	}

}
