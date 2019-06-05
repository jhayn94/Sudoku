package sudoku.view.settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import sudoku.view.dialog.ModalDialog;
import sudoku.view.util.LabelConstants;

/**
 * This class contains methods to allow the user to view or change the color
 * settings of the application.
 */
public class ColorSettingsView extends ModalDialog {

	private static final int HINT_DELETE_COLOR_INDEX = 5;

	private static final int SMALL_PADDING = 15;

	private static final int LABEL_WIDTH = 120;

	private static final int BUTTON_PANE_PADDING = 5;

	private static final int MEDIUM_PADDING = 20;

	private static final int LARGE_PADDING = 30;

	private static final String COLOR_PAIR_A = "A";

	private static final String COLOR_PAIR_B = "B";

	private ColorPicker filterColorPicker;

	/**
	 * These are the colors used when the user applies color to a cell. There are 5
	 * pairs of colors.
	 */
	private final ColorPicker[] coloringColorPickers;

	/**
	 * These are the colors used when the for hints. These colors are not paired;
	 * only arranged in a grid because it uses the space better.
	 */
	private final ColorPicker[] hintColorPickers;

	public ColorSettingsView(final Stage stage) {
		super(stage);
		this.coloringColorPickers = new ColorPicker[ApplicationSettings.NUM_COLORS_USED_IN_COLORING];
		this.hintColorPickers = new ColorPicker[ApplicationSettings.NUM_COLORS_USED_IN_HINTS
				+ ApplicationSettings.NUM_COLORS_USED_IN_ALSES + 1];
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
		contentPane.setPadding(new Insets(MEDIUM_PADDING));
		final HBox selectedColorPane = this.createFilterColorPane();
		selectedColorPane.setAlignment(Pos.CENTER);
		VBox.setMargin(selectedColorPane, new Insets(0, 0, MEDIUM_PADDING, 0));
		final HBox coloringColorsPane = this.createColoringColorsPane();
		VBox.setMargin(coloringColorsPane, new Insets(0, MEDIUM_PADDING, 0, 0));
		final HBox hintColorsPane = this.createHintColorsPane();
		VBox.setMargin(hintColorsPane, new Insets(0, MEDIUM_PADDING, 0, 0));
		contentPane.getChildren().addAll(selectedColorPane, coloringColorsPane, hintColorsPane);
		this.createButtonPane();
		this.setCenter(contentPane);
	}

	private HBox createFilterColorPane() {
		final HBox selectedColorPane = new HBox();
		final Label filterColorLabel = new Label(LabelConstants.FILTERED_CELL_COLOR);
		HBox.setMargin(filterColorLabel, new Insets(0, MEDIUM_PADDING, 0, 0));
		final String colorForFiltering = ApplicationSettings.getInstance().getColorForFiltering();
		this.filterColorPicker = new ColorPicker(Color.valueOf(colorForFiltering));
		selectedColorPane.getChildren().addAll(filterColorLabel, this.filterColorPicker);
		return selectedColorPane;
	}

	private HBox createColoringColorsPane() {
		final HBox parentPane = new HBox();
		final GridPane baseColorsGridPane = new GridPane();
		baseColorsGridPane.setHgap(SMALL_PADDING);
		baseColorsGridPane.setVgap(SMALL_PADDING);
		final GridPane alternateColorsGridPane = new GridPane();
		alternateColorsGridPane.setHgap(SMALL_PADDING);
		alternateColorsGridPane.setVgap(SMALL_PADDING);
		final String[] coloringColors = ApplicationSettings.getInstance().getColorsUsedInColoring();
		for (int index = 0; index < ApplicationSettings.NUM_COLORS_USED_IN_COLORING; index++) {
			final int colorPair = index / 2 + 1;
			final String colorPairIndicator = index % 2 == 0 ? COLOR_PAIR_A : COLOR_PAIR_B;
			final Label coloringColorLabel = new Label(LabelConstants.COLOR_PAIR + colorPair + colorPairIndicator + ":");
			coloringColorLabel.setMinWidth(LABEL_WIDTH);
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

	private HBox createHintColorsPane() {
		final HBox parentPane = new HBox();
		final GridPane leftGridPane = new GridPane();
		leftGridPane.setHgap(SMALL_PADDING);
		leftGridPane.setVgap(SMALL_PADDING);
		final GridPane rightGridPane = new GridPane();
		rightGridPane.setHgap(SMALL_PADDING);
		rightGridPane.setVgap(SMALL_PADDING);
		final List<String> allHintColors = this.getHintColors();
		for (int index = 0; index < allHintColors.size(); index++) {
			final String colorPickerLabel = this.getLabelForHintColorPicker(index);
			final Label coloringColorLabel = new Label(colorPickerLabel);
			coloringColorLabel.setMinWidth(LABEL_WIDTH);
			final String colorForColoring = allHintColors.get(index);
			this.hintColorPickers[index] = new ColorPicker(Color.valueOf(colorForColoring));
			final int row = index / 2;
			if (index % 2 == 0) {
				leftGridPane.add(coloringColorLabel, 0, row);
				leftGridPane.add(this.hintColorPickers[index], 1, row);
			} else {
				rightGridPane.add(coloringColorLabel, 0, row);
				rightGridPane.add(this.hintColorPickers[index], 1, row);
			}
		}
		HBox.setMargin(leftGridPane, new Insets(SMALL_PADDING, LARGE_PADDING, 0, 0));
		HBox.setMargin(rightGridPane, new Insets(SMALL_PADDING, 0, 0, 0));
		parentPane.getChildren().addAll(leftGridPane, rightGridPane);
		return parentPane;
	}

	private List<String> getHintColors() {
		final String[] hintColors = ApplicationSettings.getInstance().getHintColors();
		final String hintDeleteColor = ApplicationSettings.getInstance().getHintDeleteColor();
		final String[] hintAlsColors = ApplicationSettings.getInstance().getAlsColors();
		final List<String> allHintColors = new ArrayList<>();
		allHintColors.addAll(Arrays.asList(hintColors));
		allHintColors.add(hintDeleteColor);
		allHintColors.addAll(Arrays.asList(hintAlsColors));
		return allHintColors;
	}

	private void createButtonPane() {
		final Button saveAndApplyButton = new Button(LabelConstants.SAVE_AND_APPLY);
		saveAndApplyButton.setOnAction(event -> {
			ModelController.getInstance().transitionToSaveColorSettingsState();
			this.getStage().close();
		});
		final Button restoreDefaultsButton = new Button(LabelConstants.RESTORE_DEFAULTS);
		restoreDefaultsButton.setOnAction(event -> this.resetViewToDefaults());
		HBox.setMargin(restoreDefaultsButton, new Insets(0, 0, 0, MEDIUM_PADDING));
		final HBox buttonPane = new HBox();
		buttonPane.setPadding(new Insets(0, 0, BUTTON_PANE_PADDING, BUTTON_PANE_PADDING));
		buttonPane.getChildren().addAll(saveAndApplyButton, restoreDefaultsButton);
		this.setBottom(buttonPane);
	}

	private void resetViewToDefaults() {
		final String colorForFiltering = DefaultApplicationSettings.getInstance().getColorForFiltering();
		final String[] colorsUsedInColoring = DefaultApplicationSettings.getInstance().getColorsUsedInColoring();
		final String[] hintColors = DefaultApplicationSettings.getInstance().getHintColors();
		final String[] alsColors = DefaultApplicationSettings.getInstance().getAlsColors();
		final String hintDeleteColor = DefaultApplicationSettings.getInstance().getHintDeleteColor();
		this.filterColorPicker.setValue(Color.valueOf(colorForFiltering));
		for (int index = 0; index < ApplicationSettings.NUM_COLORS_USED_IN_COLORING; index++) {
			this.coloringColorPickers[index].setValue(Color.valueOf(colorsUsedInColoring[index]));
		}
		for (int index = 0; index < ApplicationSettings.NUM_COLORS_USED_IN_HINTS; index++) {
			this.hintColorPickers[index].setValue(Color.valueOf(hintColors[index]));
		}
		this.hintColorPickers[HINT_DELETE_COLOR_INDEX].setValue(Color.valueOf(hintDeleteColor));
		for (int index = 0; index < ApplicationSettings.NUM_COLORS_USED_IN_ALSES; index++) {
			final int adjustedIndex = index + HINT_DELETE_COLOR_INDEX + 1;
			this.hintColorPickers[adjustedIndex].setValue(Color.valueOf(alsColors[index]));
		}
	}

	/** Gets the label for the given index of hint color picker. */
	private String getLabelForHintColorPicker(final int index) {
		if (index < HINT_DELETE_COLOR_INDEX) {
			return LabelConstants.HINT_COLOR + (index + 1) + ":";
		} else if (index > HINT_DELETE_COLOR_INDEX) {
			return LabelConstants.ALS_COLOR + (index - HINT_DELETE_COLOR_INDEX) + ":";
		} else {
			return LabelConstants.HINT_DELETE_COLOR;
		}
	}

	public ColorPicker getFilterColorPicker() {
		return this.filterColorPicker;
	}

	public ColorPicker getColoringColorPicker(final int index) {
		return this.coloringColorPickers[index];
	}

	public ColorPicker getHintColorPicker(final int index) {
		return this.hintColorPickers[index];
	}

}
