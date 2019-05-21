package sudoku.factories;

import sudoku.model.SudokuPuzzle;

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

	public SudokuPuzzle createSudokuPuzzle() {
		return new SudokuPuzzle();
	}

	private ModelFactory() {
		// Private constructor to prevent external instantiation.
	}

}
