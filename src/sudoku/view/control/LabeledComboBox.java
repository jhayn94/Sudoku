package sudoku.view.control;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/** This class pairs a text field with a label to the left of it. */
public class LabeledComboBox extends HBox {

	private static final int COMBO_BOX_WIDTH = 200;

	private static final double LABEL_RIGHT_PADDING = 32;

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
		this.setAlignment(Pos.CENTER);
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
		HBox.setMargin(this.label, new Insets(0, LABEL_RIGHT_PADDING, 0, 0));

		final ObservableList<Node> children = this.getChildren();
		children.add(this.label);
		children.add(this.comboBox);
	}
}
