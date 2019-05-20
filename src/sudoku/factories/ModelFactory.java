package sudoku.factories;

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

	private ModelFactory() {
		// Private constructor to prevent external instantiation.
	}

}
