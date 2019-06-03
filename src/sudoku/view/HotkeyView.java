package sudoku.view;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import sudoku.view.dialog.ModalDialog;
import sudoku.view.util.LabelConstants;
import sudoku.view.util.ResourceConstants;

public class HotkeyView extends ModalDialog {

	private static final int PADDING = 20;

	private static final int HOTKEY_LABEL_WIDTH = 150;

	private static final int ACTION_LABEL_WIDTH = 500;

	private static final int ACTION_LABEL_HEIGHT = 40;

	private static final Logger LOG = LogManager.getLogger(HotkeyView.class);

	private static final String PIPE_REGEX = "\\|";

	private final List<String> hotkeys;

	public HotkeyView(final Stage stage) {
		super(stage);
		this.hotkeys = new ArrayList<>();
		this.configure();
	}

	@Override
	protected void configure() {
		this.setTitle(LabelConstants.HOTKEYS);
		this.readHotkeysFromFile(ResourceConstants.HOTKEYS);
		this.createChildElements();
	}

	@Override
	protected void createChildElements() {
		super.createChildElements();
		final ScrollPane scrollPane = new ScrollPane();
		final GridPane hotKeyGridPane = new GridPane();
		scrollPane.setContent(hotKeyGridPane);
		hotKeyGridPane.setPadding(new Insets(PADDING));
		hotKeyGridPane.setVgap(10);
		hotKeyGridPane.setHgap(10);
		for (int index = 0; index < this.hotkeys.size(); index++) {
			// Hotkey is index 0; action is index 1.
			final String hotkey = this.hotkeys.get(index);
			final String[] hotkeyAndAction = hotkey.split(PIPE_REGEX);
			final Label hotkeyLabel = new Label(hotkeyAndAction[0].trim());
			hotkeyLabel.setMinWidth(HOTKEY_LABEL_WIDTH);
			hotkeyLabel.setMaxWidth(70);
			final Label actionLabel = new Label(hotkeyAndAction[1].trim());
			actionLabel.setWrapText(true);
			actionLabel.setMinWidth(ACTION_LABEL_WIDTH);
			actionLabel.setMaxWidth(ACTION_LABEL_WIDTH);
			actionLabel.setMinHeight(ACTION_LABEL_HEIGHT);
			actionLabel.setMaxHeight(ACTION_LABEL_HEIGHT);
			hotKeyGridPane.add(hotkeyLabel, 0, index);
			hotKeyGridPane.add(actionLabel, 1, index);
		}
		this.setCenter(scrollPane);
	}

	/**
	 * To avoid having a file that is mostly constants, the hotkeys are read in from
	 * a file.
	 */
	private void readHotkeysFromFile(final String filePath) {
		final Path settingsFilePath = Paths.get(new File(filePath).toURI());
		try {
			final List<String> allLines = Files.readAllLines(settingsFilePath);
			allLines.stream().forEach(this.hotkeys::add);
		} catch (final IOException e) {
			LOG.error("{}", e);
		}
	}

}
