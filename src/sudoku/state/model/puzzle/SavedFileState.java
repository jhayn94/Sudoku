package sudoku.state.model.puzzle;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import sudoku.model.SudokuPuzzleValues;
import sudoku.state.ApplicationModelState;

/**
 * This class updates the state of the application when the user invokes a
 * "redo", either through the keyboard or a button press in the UI.
 */
public class SavedFileState extends ApplicationModelState {

	private String puzzleString;

	private String[][] candidatesForCellsFromFile;

	private String givens;

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
			ioe.printStackTrace();
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
		bufferedWriter.write(givens.toString() + "\n");
		bufferedWriter.write(setValues.toString() + "\n");
	}

	private void writeCandidates(final BufferedWriter bufferedWriter) throws IOException {
		for (int row = 0; row < SudokuPuzzleValues.CELLS_PER_HOUSE; row++) {
			for (int col = 0; col < SudokuPuzzleValues.CELLS_PER_HOUSE; col++) {
				final List<Integer> candidateDigitsForCell = this.sudokuPuzzleValues.getCandidateDigitsForCell(row, col);
				for (final int candidate : candidateDigitsForCell) {
					bufferedWriter.write(String.valueOf(candidate));
				}
				bufferedWriter.write("\n");
			}
		}

	}

}
