package sudoku.view.sidebar;

import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import sudoku.core.ModelController;
import sudoku.factories.LayoutFactory;
import sudoku.view.control.LabeledComboBox;
import sudoku.view.util.LabelConstants;
import sudoku.view.util.TooltipConstants;

/**
 * This class corresponds to the combo box in the bottom left of the view. It
 * allows the user to change the mode of the mouse: select cells, color cells,
 * color candidates.
 */
public class MouseModePane extends GridPane {

	private static final int PADDING_FOR_PANE = 15;

	private static final String CSS_CLASS = "sudoku-transparent-pane";

	private static final int DEFAULT_WIDTH = 320;

	public MouseModePane() {
		this.configure();
	}

	private void configure() {
		this.getStyleClass().add(CSS_CLASS);
		this.setPadding(new Insets(PADDING_FOR_PANE, PADDING_FOR_PANE, PADDING_FOR_PANE, PADDING_FOR_PANE - 1));
		this.setMinWidth(DEFAULT_WIDTH);
		this.setMaxWidth(DEFAULT_WIDTH);
		this.createChildElements();
	}

	private void createChildElements() {
		final LabeledComboBox mouseModeComboBox = LayoutFactory.getInstance().createLabeledComboBox();
		mouseModeComboBox.setFocusTraversable(false);
		mouseModeComboBox.getLabel().setText(LabelConstants.MOUSE_MODE);
		final ComboBox<String> comboBox = mouseModeComboBox.getComboBox();
		comboBox.getItems().addAll(LabelConstants.SELECT_CELLS, LabelConstants.COLOR_CELLS,
				LabelConstants.COLOR_CANDIDATES);
		comboBox.getSelectionModel().select(0);
		comboBox.setTooltip(new Tooltip(TooltipConstants.MOUSE_MODE));
		this.getChildren().add(mouseModeComboBox);
		comboBox.setFocusTraversable(false);
		comboBox.getEditor().textProperty().addListener(this.onValueChanged(comboBox));
	}

	private ChangeListener<? super String> onValueChanged(final ComboBox<String> comboBox) {
		return (obs, oldValue, newValue) -> {
			// When the change listener is added, setting the initial value via code
			// triggers the listener. This causes some stuff to be initialized in the wrong
			// order. Requiring that this isn't the initial setting of the value avoids that
			// problem.
			if (!oldValue.isEmpty()) {
				final String selected = comboBox.getSelectionModel().getSelectedItem();
				ModelController.getInstance().transitionToMouseModeChangedState(selected.toUpperCase().replace(" ", "_"));
			}
		};
	}

}
