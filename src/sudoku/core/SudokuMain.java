package sudoku.core;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sudoku.factories.LayoutFactory;
import sudoku.factories.MenuFactory;
import sudoku.view.ApplicationRootPane;
import sudoku.view.MainApplicationView;
import sudoku.view.RootStackPane;
import sudoku.view.menu.ApplicationTitleBar;
import sudoku.view.util.ResourceConstants;
import sudoku.view.util.WindowHelper;

/** Entry point for the execution of the project. */
public class SudokuMain extends Application {

	private static final double DEFAULT_STAGE_WIDTH = 1292.5;

	private static final int DEFAULT_STAGE_HEIGHT = 690;

	private static final Logger LOG = LogManager.getLogger(SudokuMain.class);

	@Override
	public void start(final Stage stage) throws IOException {
		final BorderPane root = this.createRootPane(stage);
		final RootStackPane rootStackPane = LayoutFactory.getInstance().createRootStackPane(root);
		final Scene scene = this.createScene(rootStackPane);
		this.configureStage(stage, scene, root);
		// These messages are just to separate executions if a log file gets
		// re-used.
		LOG.info("==============================================");
		LOG.info("Application started successfully.");
	}

	/**
	 * Creates and returns a Scene, using the given Parent object as a root element.
	 */
	private Scene createScene(final Region root) {
		final Scene scene = new Scene(root);
		final String applicationCSS = this.getClass().getResource(ResourceConstants.APPLICATION_CSS).toExternalForm();
		scene.getStylesheets().add(applicationCSS);
		scene.setFill(Color.TRANSPARENT);
		return scene;
	}

	/**
	 * Creates and returns a BorderPane which acts as the root element for the
	 * scene.
	 */
	private BorderPane createRootPane(final Stage stage) {
		final ApplicationRootPane root = LayoutFactory.getInstance().createApplicationRootPane();
		final ApplicationTitleBar menuContainer = MenuFactory.getInstance().createApplicationTitleBar(stage);
		root.setTop(menuContainer);
		final MainApplicationView topLevelSplitPane = LayoutFactory.getInstance().createMainApplicationView();
		root.setCenter(topLevelSplitPane);
		return root;
	}

	private void configureStage(final Stage stage, final Scene scene, final Region root) {
		ViewController.getInstance().setStage(stage);
		stage.getIcons().add(new Image(this.getClass().getResourceAsStream(ResourceConstants.APPLICATION_ICON)));
		stage.initStyle(StageStyle.TRANSPARENT);
		stage.setScene(scene);
		stage.setMinHeight(DEFAULT_STAGE_HEIGHT);
		stage.setHeight(DEFAULT_STAGE_HEIGHT);
		stage.setMinWidth(DEFAULT_STAGE_WIDTH);
		stage.setWidth(DEFAULT_STAGE_WIDTH);
		stage.setMaximized(false);
		stage.show();
		WindowHelper.addResizeAndDragListener(stage, root);
		// Initializes the model controller with default states + behaviors.
		ModelController.getInstance();
	}

	public static void main(final String[] args) {
		Application.launch(args);
	}

}
