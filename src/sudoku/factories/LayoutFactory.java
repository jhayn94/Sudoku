package sudoku.factories;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import sudoku.core.ViewController;
import sudoku.view.ApplicationSideBar;
import sudoku.view.HelpView;
import sudoku.view.MainApplicationView;
import sudoku.view.ModalDialog;
import sudoku.view.ModalStage;
import sudoku.view.RootStackPane;
import sudoku.view.control.LabeledComboBox;
import sudoku.view.control.ToggleButton;
import sudoku.view.hint.HintButtonPane;
import sudoku.view.hint.HintPane;
import sudoku.view.hint.HintTextArea;
import sudoku.view.puzzle.SudokuPuzzleCell;
import sudoku.view.puzzle.SudokuPuzzleView;
import sudoku.view.sidebar.CandidateSelectionPane;
import sudoku.view.sidebar.ColorSelectionPane;
import sudoku.view.sidebar.FilterButtonPane;
import sudoku.view.sidebar.MouseModePane;
import sudoku.view.sidebar.MouseToolsPane;
import sudoku.view.util.LabelConstants;
import sudoku.view.util.ResourceConstants;
import sudoku.view.util.ShadowRectangle;
import sudoku.view.util.WindowHelper;

/**
 * This class contains methods to instantiate all views shown in the
 * application.
 */
public class LayoutFactory {

	private static final int HELP_SCENE_HEIGHT = 600;

	private static final int HELP_SCENE_WIDTH = 700;

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

	public FilterButtonPane createFilterButtonPane() {
		final FilterButtonPane filterButtonPane = new FilterButtonPane();
		ViewController.getInstance().setFilterButtonPane(filterButtonPane);
		return filterButtonPane;
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
		final ColorSelectionPane colorSelectionPane = new ColorSelectionPane();
		ViewController.getInstance().setColorSelectionPane(colorSelectionPane);
		return colorSelectionPane;
	}

	public HintPane createHintPane() {
		return new HintPane();
	}

	public HintButtonPane createHintButtonPane() {
		final HintButtonPane hintButtonPane = new HintButtonPane();
		ViewController.getInstance().setHintButtonPane(hintButtonPane);
		return hintButtonPane;
	}

	public HintTextArea createHintTextArea() {
		final HintTextArea hintTextArea = new HintTextArea();
		ViewController.getInstance().setHintTextArea(hintTextArea);
		return hintTextArea;
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
		final Stage helpStage = new ModalStage();
		final HelpView helpView = new HelpView(helpStage);
		final RootStackPane rootStackPane = this.createRootStackPane(helpView);
		final Scene helpScene = new Scene(rootStackPane, HELP_SCENE_WIDTH, HELP_SCENE_HEIGHT);
		this.configureScene(helpScene);
		helpStage.setScene(helpScene);
		WindowHelper.addResizeAndDragListener(helpStage, helpView);
		helpStage.show();
	}

	public void showNewBlankPuzzleDialog() {
		final Stage modalStage = new ModalStage();
		final Button button = new Button(LabelConstants.OK);
		new ModalDialog(modalStage).withConfirmButton(button);
	}

	/** Offers some standard configuration of a scene for the project. */
	private void configureScene(final Scene scene) {
		scene.getStylesheets().add(ResourceConstants.APPLICATION_CSS);
		scene.setFill(Color.TRANSPARENT);
	}

	private LayoutFactory() {
		// Private constructor to prevent external instantiation.
	}

}
