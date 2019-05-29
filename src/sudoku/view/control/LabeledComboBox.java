package sudoku.view.control;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/** This class pairs a text field paired with a label to the left. */
public class LabeledComboBox extends HBox {

	private static final int COMBO_BOX_WIDTH = 210;

	private static final double LABEL_RIGHT_PADDING = 26;

	private static final double LABEL_TOP_PADDING = 8;

	private ComboBox<String> comboBox;

	private Label label;

	public LabeledComboBox() {
		super();
		this.configure();
	}

	public ComboBox<String> getComboBox() {
		return this.comboBox;
	}

	public Label getLabel() {
		return this.label;
	}

	private void configure() {
		this.addChildElements();
	}

	private void addChildElements() {
		this.comboBox = new ComboBox<>();
		this.comboBox.setEditable(true);
		this.comboBox.setMinWidth(COMBO_BOX_WIDTH);
		this.comboBox.setMaxWidth(COMBO_BOX_WIDTH);
		this.comboBox.getEditor().setEditable(false);

		this.label = new Label();
		this.label.setAlignment(Pos.BASELINE_LEFT);
		// Space the text field and label out a little bit.

		final ObservableList<Node> children = this.getChildren();

		HBox.setMargin(this.label, new Insets(LABEL_TOP_PADDING, LABEL_RIGHT_PADDING, 0, 0));
		children.add(this.label);
		children.add(this.comboBox);
	}
}
