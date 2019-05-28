package sudoku.view.menu;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import sudoku.factories.LayoutFactory;
import sudoku.view.util.LabelConstants;

public class SettingsMenu extends Menu {

	public SettingsMenu() {
		super();
		this.configure();
	}

	private void configure() {
		this.setText(LabelConstants.SETTINGS);
		this.createChildElements();
	}

	private void createChildElements() {
		final MenuItem puzzleGenerationMenuItem = new MenuItem(LabelConstants.PUZZLE_GENERATION);
		final MenuItem difficultyMenuItem = new MenuItem(LabelConstants.DIFFICULTY);
		final MenuItem solverMenuItem = new MenuItem(LabelConstants.SOLVER);
		final MenuItem colorsMenuItem = new MenuItem(LabelConstants.COLORS);
		final MenuItem miscellaneousMenuItem = new MenuItem(LabelConstants.MISCELLANEOUS);
		miscellaneousMenuItem.setOnAction(event -> LayoutFactory.getInstance().showMiscellaneousSettingsView());
		this.getItems().addAll(puzzleGenerationMenuItem, difficultyMenuItem, solverMenuItem, colorsMenuItem,
				new SeparatorMenuItem(), miscellaneousMenuItem);
	}
}
