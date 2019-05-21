package sudoku.view;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import sudoku.factories.LayoutFactory;
import sudoku.view.util.LabelConstants;

/**
 * This class corresponds to the view on the left side of the screen. It
 * contains all other view elements on this side of the application.
 */
public class NumericButtonPane extends GridPane {

	private final List<Button> filterButtons;

	private static final int NUM_DIGIT_BUTTONS = 9;

	public NumericButtonPane() {
		this.filterButtons = new ArrayList<>(10);
		this.configure();
	}

	public List<Button> getFilterButtons() {
		return filterButtons;
	}

	private void configure() {
		this.setPadding(new Insets(10, 10, 10, 10));
		this.setHgap(20);
		this.setVgap(20);
		this.setMinWidth(300);
		this.setMaxWidth(300);
		this.createChildElements();
	}

	private void createChildElements() {
		for (int index = 1; index <= NUM_DIGIT_BUTTONS; index++) {
			final Button digitFilterButton = LayoutFactory.getInstance().createToggleButton(String.valueOf(index));
			// Integer division intentional!
			this.add(digitFilterButton, (index - 1) % 3, (index - 1) / 3);
			this.filterButtons.add(digitFilterButton);
		}
		final Button bivalueCellFilterButton = LayoutFactory.getInstance()
				.createToggleButton(LabelConstants.BIVALUE_CELL_BUTTON);
		this.filterButtons.add(bivalueCellFilterButton);
		this.add(bivalueCellFilterButton, 1, 3);
	}

}
