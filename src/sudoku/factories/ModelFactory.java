package sudoku.factories;

import sudoku.model.ApplicationStateHistory;
import sudoku.model.SudokuPuzzleStyle;
import sudoku.model.SudokuPuzzleValues;

/**
 * This class contains methods to instantiation all models or entities shown in
 * the application.
 */
public class ModelFactory {

	private static ModelFactory modelFactoryInstance;

	public static ModelFactory getInstance() {
		if (modelFactoryInstance == null) {
			modelFactoryInstance = new ModelFactory();
		}
		return modelFactoryInstance;
	}

	public SudokuPuzzleValues createSudokuPuzzleValues(final String initialValues) {
		return new SudokuPuzzleValues(initialValues);
	}

	public SudokuPuzzleValues createSudokuPuzzleValues() {
		return new SudokuPuzzleValues();
	}

	public SudokuPuzzleStyle createSudokuPuzzleStyle() {
		return new SudokuPuzzleStyle();
	}

	public ApplicationStateHistory createApplicationStateHistory() {
		return new ApplicationStateHistory();
	}

	private ModelFactory() {
		// Private constructor to prevent external instantiation.
	}

}
