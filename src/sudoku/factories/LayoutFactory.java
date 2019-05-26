package sudoku.factories;

import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sudoku.core.ViewController;
import sudoku.view.ApplicationSideBar;
import sudoku.view.HelpView;
import sudoku.view.MainApplicationView;
import sudoku.view.RootStackPane;
import sudoku.view.control.LabeledComboBox;
import sudoku.view.control.ToggleButton;
import sudoku.view.puzzle.SudokuPuzzleCell;
import sudoku.view.puzzle.SudokuPuzzleView;
import sudoku.view.sidebar.CandidateSelectionPane;
import sudoku.view.sidebar.ColorSelectionPane;
import sudoku.view.sidebar.MouseModePane;
import sudoku.view.sidebar.MouseToolsPane;
import sudoku.view.sidebar.NumericButtonPane;
import sudoku.view.util.ResourceConstants;
import sudoku.view.util.ShadowRectangle;
import sudoku.view.util.WindowHelper;

/**
 * This class contains methods to instantiate all views shown in the
 * application.
 */
public class LayoutFactory {

	private static final int HELP_STAGE_HEIGHT = 600;

	private static final int HELP_STAGE_WIDTH = 700;

	private static LayoutFactory layoutFactoryInstance;

	public static LayoutFactory getInstance() {
		if (layoutFactoryInstance == null) {
			layoutFactoryInstance = new LayoutFactory();
		}
		return layoutFactoryInstance;
	}

	public RootStackPane createRootStackPane(final Region applicationView) {
		return new RootStackPane(applicationView);
	}

	public MainApplicationView createMainApplicationView() {
		return new MainApplicationView();
	}

	public ApplicationSideBar createApplicationSideBar() {
		return new ApplicationSideBar();
	}

	public SudokuPuzzleView createSudokuPuzzleView() {
		final SudokuPuzzleView sudokuPuzzleView = new SudokuPuzzleView();
		ViewController.getInstance().setSudokuPuzzleView(sudokuPuzzleView);
		return sudokuPuzzleView;
	}

	public SudokuPuzzleCell createSudokuPuzzleCell(final int col, final int row) {
		final SudokuPuzzleCell sudokuPuzzleCell = new SudokuPuzzleCell(row, col);
		ViewController.getInstance().registerSudokuPuzzleCell(sudokuPuzzleCell, col, row);
		return sudokuPuzzleCell;
	}

	public NumericButtonPane createNumericButtonPane() {
		final NumericButtonPane numericButtonPane = new NumericButtonPane();
		ViewController.getInstance().setNumericButtonPane(numericButtonPane);
		return numericButtonPane;
	}

	public MouseModePane createMouseModePane() {
		return new MouseModePane();
	}

	public MouseToolsPane createMouseToolsPane() {
		return new MouseToolsPane();
	}

	public CandidateSelectionPane createColorCandidateSelectionPane() {
		return new CandidateSelectionPane();
	}

	public ColorSelectionPane createColorSelectionPane() {
		return new ColorSelectionPane();
	}

	public ShadowRectangle createShadowRectangle() {
		return new ShadowRectangle();
	}

	public LabeledComboBox createLabeledComboBox() {
		return new LabeledComboBox();
	}

	public ToggleButton createToggleButton(final String label) {
		return new ToggleButton(label);
	}

	public void showHelpView() {
		final Stage helpStage = ViewController.getInstance().getHelpStage();
		// Don't create a second instance if one is already available.
		if (helpStage != null) {
			helpStage.toFront();
		} else {
			this.createNewHelpView();
		}
	}

	/** Creates a new stage, scene, and then a HelpView, which is nested inside. */
	private void createNewHelpView() {
		final Stage helpStage = this.createHelpStage();
		final HelpView helpView = new HelpView(helpStage);
		final RootStackPane rootStackPane = LayoutFactory.getInstance().createRootStackPane(helpView);
		final Scene helpScene = new Scene(rootStackPane, HELP_STAGE_WIDTH, HELP_STAGE_HEIGHT);
		helpScene.getStylesheets().add(ResourceConstants.APPLICATION_CSS);
		helpScene.setFill(Color.TRANSPARENT);
		helpStage.setScene(helpScene);
		WindowHelper.addResizeAndDragListener(helpStage, helpView);
		helpStage.show();
	}

	private Stage createHelpStage() {
		final Stage helpStage = this.createBasicStage();
		ViewController.getInstance().setHelpStage(helpStage);
		return helpStage;
	}

	/** Creates a stage with some common project variables set. */
	private Stage createBasicStage() {
		final Stage helpStage = new Stage();
		helpStage.initStyle(StageStyle.TRANSPARENT);
		helpStage.centerOnScreen();
		helpStage.setMinHeight(600);
		return helpStage;
	}

	private LayoutFactory() {
		// Private constructor to prevent external instantiation.
	}

}
