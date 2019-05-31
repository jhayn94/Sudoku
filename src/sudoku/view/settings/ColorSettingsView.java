package sudoku.view.settings;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import sudoku.core.ModelController;
import sudoku.model.ApplicationSettings;
import sudoku.model.DefaultApplicationSettings;
import sudoku.view.ModalDialog;
import sudoku.view.util.LabelConstants;

/**
 * This class contains methods to allow the user to view or change the color
 * settings of the application.
 */
public class ColorSettingsView extends ModalDialog {

	private static final int BUTTON_PANE_PADDING = 5;

	private static final int SMALL_PADDING = 20;

	private static final int LARGE_PADDING = 30;

	private static final String COLOR_PAIR_A = "A";

	private static final String COLOR_PAIR_B = "B";

	private ColorPicker filterColorPicker;

	/**
	 * These are the colors used when the user applies color to a cell. There are 5
	 * pairs of colors.
	 */
	private final ColorPicker[] coloringColorPickers;

	public ColorSettingsView(final Stage stage) {
		super(stage);
		this.coloringColorPickers = new ColorPicker[ApplicationSettings.NUM_COLORS_USED_IN_COLORING];
		this.configure();
	}

	@Override
	protected void configure() {
		this.setTitle(LabelConstants.COLOR_SETTINGS);
		this.createChildElements();
	}

	@Override
	protected void createChildElements() {
		final VBox contentPane = new VBox();
		contentPane.setAlignment(Pos.TOP_LEFT);
		contentPane.setPadding(new Insets(SMALL_PADDING));
		final HBox selectedColorPane = this.createFilterColorPane();
		selectedColorPane.setAlignment(Pos.CENTER);
		VBox.setMargin(selectedColorPane, new Insets(0, 0, LARGE_PADDING, 0));
		final HBox coloringColorsPane = this.createColoringColorsPane();
		VBox.setMargin(coloringColorsPane, new Insets(0, SMALL_PADDING, 0, 0));
		contentPane.getChildren().addAll(selectedColorPane, coloringColorsPane);
		this.createButtonPane();
		this.setCenter(contentPane);
	}

	private HBox createFilterColorPane() {
		final HBox selectedColorPane = new HBox();
		final Label filterColorLabel = new Label(LabelConstants.FILTERED_CELL_COLOR);
		HBox.setMargin(filterColorLabel, new Insets(0, SMALL_PADDING, 0, 0));
		final String colorForFiltering = ApplicationSettings.getInstance().getColorForFiltering();
		this.filterColorPicker = new ColorPicker(Color.valueOf(colorForFiltering));
		selectedColorPane.getChildren().addAll(filterColorLabel, this.filterColorPicker);
		return selectedColorPane;
	}

	private HBox createColoringColorsPane() {
		final HBox parentPane = new HBox();
		final GridPane baseColorsGridPane = new GridPane();
		baseColorsGridPane.setHgap(15);
		baseColorsGridPane.setVgap(20);
		final GridPane alternateColorsGridPane = new GridPane();
		alternateColorsGridPane.setHgap(15);
		alternateColorsGridPane.setVgap(20);
		final String[] coloringColors = ApplicationSettings.getInstance().getColorsUsedInColoring();
		for (int index = 0; index < ApplicationSettings.NUM_COLORS_USED_IN_COLORING; index++) {
			final int colorPair = index / 2 + 1;
			final String colorPairIndicator = index % 2 == 0 ? COLOR_PAIR_A : COLOR_PAIR_B;
			final Label coloringColorLabel = new Label(LabelConstants.COLOR_PAIR + colorPair + colorPairIndicator + ":");
			coloringColorLabel.setMinWidth(120);
			final String colorForColoring = coloringColors[index];
			this.coloringColorPickers[index] = new ColorPicker(Color.valueOf(colorForColoring));
			if (index % 2 == 0) {
				baseColorsGridPane.add(coloringColorLabel, 0, colorPair - 1);
				baseColorsGridPane.add(this.coloringColorPickers[index], 1, colorPair - 1);
			} else {
				alternateColorsGridPane.add(coloringColorLabel, 0, colorPair - 1);
				alternateColorsGridPane.add(this.coloringColorPickers[index], 1, colorPair - 1);
			}
		}
		HBox.setMargin(baseColorsGridPane, new Insets(0, LARGE_PADDING, 0, 0));
		parentPane.getChildren().addAll(baseColorsGridPane, alternateColorsGridPane);
		return parentPane;
	}

	private void createButtonPane() {
		final Button saveAndApplyButton = new Button(LabelConstants.SAVE_AND_APPLY);
		saveAndApplyButton.setOnAction(event -> {
			ModelController.getInstance().transitionToSaveColorSettingsState();
			this.stage.close();
		});
		final Button restoreDefaultsButton = new Button(LabelConstants.RESTORE_DEFAULTS);
		restoreDefaultsButton.setOnAction(event -> this.resetViewToDefaults());
		HBox.setMargin(restoreDefaultsButton, new Insets(0, 0, 0, SMALL_PADDING));
		final HBox buttonPane = new HBox();
		buttonPane.setPadding(new Insets(0, 0, BUTTON_PANE_PADDING, BUTTON_PANE_PADDING));
		buttonPane.getChildren().addAll(saveAndApplyButton, restoreDefaultsButton);
		this.setBottom(buttonPane);
	}

	private void resetViewToDefaults() {
		final String colorForFiltering = DefaultApplicationSettings.getInstance().getColorForFiltering();
		final String[] colorsUsedInColoring = DefaultApplicationSettings.getInstance().getColorsUsedInColoring();
		this.filterColorPicker.setValue(Color.valueOf(colorForFiltering));
		for (int index = 0; index < ApplicationSettings.NUM_COLORS_USED_IN_COLORING; index++) {
			this.coloringColorPickers[index].setValue(Color.valueOf(colorsUsedInColoring[index]));
		}
	}

	public ColorPicker getFilterColorPicker() {
		return this.filterColorPicker;
	}

	public ColorPicker getColoringColorPicker(final int index) {
		return this.coloringColorPickers[index];
	}

}
