package sudoku.state.model.puzzle;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sudoku.model.SudokuPuzzleValues;
import sudoku.state.model.ApplicationModelState;

/**
 * This class updates the state of the application when the user saves an
 * existing puzzle file.
 */
public class SavedFileState extends ApplicationModelState {

	private static final Logger LOG = LogManager.getLogger(SavedFileState.class);

	private static final String NEW_LINE = "\n";

	private final File selectedFile;

	public SavedFileState(final File selectedFile, final ApplicationModelState lastState) {
		super(lastState, false);
		this.selectedFile = selectedFile;
	}

	@Override
	public void onEnter() {
		try {
			final BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(this.selectedFile));
			this.writeGivensAndSetValues(bufferedWriter);
			this.writeCandidates(bufferedWriter);
			bufferedWriter.close();
		} catch (final IOException ioe) {
			LOG.error("{}", ioe);
		}
	}

	private void writeGivensAndSetValues(final BufferedWriter bufferedWriter) throws IOException {
		final StringBuilder givens = new StringBuilder();
		final StringBuilder setValues = new StringBuilder();
		for (int row = 0; row < SudokuPuzzleValues.CELLS_PER_HOUSE; row++) {
			for (int col = 0; col < SudokuPuzzleValues.CELLS_PER_HOUSE; col++) {
				givens.append(this.sudokuPuzzleValues.getGivenCellDigit(row, col));
				setValues.append(this.sudokuPuzzleValues.getFixedCellDigit(row, col));
			}
		}
		bufferedWriter.write(givens.toString() + NEW_LINE);
		bufferedWriter.write(setValues.toString() + NEW_LINE);
	}

	private void writeCandidates(final BufferedWriter bufferedWriter) throws IOException {
		for (int row = 0; row < SudokuPuzzleValues.CELLS_PER_HOUSE; row++) {
			for (int col = 0; col < SudokuPuzzleValues.CELLS_PER_HOUSE; col++) {
				final List<Integer> candidateDigitsForCell = this.sudokuPuzzleValues.getCandidateDigitsForCell(row, col);
				for (final int candidate : candidateDigitsForCell) {
					bufferedWriter.write(String.valueOf(candidate));
				}
				bufferedWriter.write(NEW_LINE);
			}
		}

	}

}
