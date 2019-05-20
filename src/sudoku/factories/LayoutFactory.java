package sudoku.factories;

import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sudoku.core.ViewController;
import sudoku.view.HelpView;
import sudoku.view.MainApplicationView;
import sudoku.view.RootStackPane;
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

	public RootStackPane createRootStackPane(Region applicationView) {
		return new RootStackPane(applicationView);
	}

	public MainApplicationView createMainApplicationView() {
		return new MainApplicationView();
	}

	public ShadowRectangle createShadowRectangle() {
		return new ShadowRectangle();
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
		final Stage helpStage = new Stage();
		helpStage.initStyle(StageStyle.TRANSPARENT);
		helpStage.centerOnScreen();
		helpStage.setMinHeight(600);
		ViewController.getInstance().setHelpStage(helpStage);
		return helpStage;
	}

	private LayoutFactory() {
		// Private constructor to prevent external instantiation.
	}

}
