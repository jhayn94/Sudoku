package sudoku.core;

import java.io.File;
import java.util.List;

import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import sudoku.StepConfig;
import sudoku.state.model.ApplicationModelState;
import sudoku.state.model.DefaultApplicationModelState;
import sudoku.state.model.MouseModeChangedState;
import sudoku.state.model.RedoActionState;
import sudoku.state.model.RestartPuzzleState;
import sudoku.state.model.ShowContextMenuState;
import sudoku.state.model.UndoActionState;
import sudoku.state.model.candidate.ActiveCandidateChangedState;
import sudoku.state.model.candidate.ToggleCandidateVisibleState;
import sudoku.state.model.cell.ArrowKeyboardInputState;
import sudoku.state.model.cell.ClickedCellState;
import sudoku.state.model.cell.RemoveDigitState;
import sudoku.state.model.cell.SetDigitState;
import sudoku.state.model.cell.SetGivenCellsState;
import sudoku.state.model.cell.UnlockGivenCellsState;
import sudoku.state.model.coloring.ActiveColorChangedState;
import sudoku.state.model.coloring.ResetAllColorsState;
import sudoku.state.model.coloring.ToggleCandidateColorState;
import sudoku.state.model.coloring.ToggleCellColorState;
import sudoku.state.model.filter.ApplyFilterState;
import sudoku.state.model.filter.CycleActiveFilterState;
import sudoku.state.model.hint.ApplyHintState;
import sudoku.state.model.hint.HideHintState;
import sudoku.state.model.hint.ShowPartialHintState;
import sudoku.state.model.hint.ShowSpecificHintState;
import sudoku.state.model.hint.ShowVagueHintState;
import sudoku.state.model.puzzle.CopyPuzzleState;
import sudoku.state.model.puzzle.FillInSinglesPuzzleState;
import sudoku.state.model.puzzle.NewEmptyPuzzleState;
import sudoku.state.model.puzzle.NewRandomPuzzleState;
import sudoku.state.model.puzzle.OpenedFileState;
import sudoku.state.model.puzzle.PastePuzzleState;
import sudoku.state.model.puzzle.SavedFileState;
import sudoku.state.model.settings.SaveColorSettingsState;
import sudoku.state.model.settings.SaveDifficultySettingsState;
import sudoku.state.model.settings.SaveMiscellaneousSettingsState;
import sudoku.state.model.settings.SavePuzzleGenerationSettingsState;
import sudoku.state.model.settings.SaveSolverSettingsState;
import sudoku.state.window.ApplicationWindowState;
import sudoku.state.window.ClosedState;
import sudoku.state.window.MaximizedState;
import sudoku.state.window.MinimizedState;
import sudoku.state.window.RestoredState;
import sudoku.state.window.SoftRestoredState;

/** A controller class to facilitate state (model) changes. */
public class ModelController {

	private static ModelController instance;

	public static ModelController getInstance() {
		if (ModelController.instance == null) {
			ModelController.instance = new ModelController();
		}
		return ModelController.instance;
	}

	/**
	 * Contains info about the state of the model (i.e. the data behind the result
	 * view and the input elements).
	 */
	private ApplicationModelState applicationModelState;

	/**
	 * Contains info about the state of the window (i.e. maximized, window bounds,
	 * etc.).
	 */
	private ApplicationWindowState applicationWindowState;

	private ModelController() {
		this.applicationModelState = new DefaultApplicationModelState();
		this.applicationWindowState = new RestoredState();
		this.applicationModelState.onEnter();
		this.applicationWindowState.onEnter();
	}

	public void transitionToMinimizedState() {
		this.applicationWindowState = new MinimizedState(this.applicationWindowState);
		this.applicationWindowState.onEnter();
	}

	public void transitionToMaximizedState() {
		this.applicationWindowState = new MaximizedState(this.applicationWindowState);
		this.applicationWindowState.onEnter();
	}

	public void transitionToRestoredState() {
		this.applicationWindowState = new RestoredState(this.applicationWindowState);
		this.applicationWindowState.onEnter();
	}

	public void transitionToSoftRestoredState() {
		this.applicationWindowState = new SoftRestoredState(this.applicationWindowState);
		this.applicationWindowState.onEnter();
	}

	public void transitionToClosedState() {
		this.applicationWindowState = new ClosedState(this.applicationWindowState);
		this.applicationWindowState.onEnter();
	}

	public void transitionToApplyFilterState(final String filter) {
		this.applicationModelState = new ApplyFilterState(this.applicationModelState, filter);
		this.applicationModelState.onEnter();
	}

	public void transitionToCycleActiveFilterState(final String filter) {
		this.applicationModelState = new CycleActiveFilterState(this.applicationModelState, filter);
		this.applicationModelState.onEnter();
	}

	public void transitionToClickedCellState(final int row, final int col, final MouseEvent event) {
		this.applicationModelState = new ClickedCellState(row, col, event, this.applicationModelState);
		this.applicationModelState.onEnter();
	}

	public void transitionToArrowKeyboardInputState(final KeyCode keyCode) {
		this.applicationModelState = new ArrowKeyboardInputState(keyCode, this.applicationModelState);
		this.applicationModelState.onEnter();
	}

	public void transitionToSetDigitState(final KeyCode keyCode) {
		this.applicationModelState = new SetDigitState(keyCode, this.applicationModelState);
		this.applicationModelState.onEnter();
	}

	public void transitionToRemoveDigitState(final KeyCode keyCode) {
		this.applicationModelState = new RemoveDigitState(keyCode, this.applicationModelState);
		this.applicationModelState.onEnter();
	}

	public void transitionToToggleCandidateVisibleState(final KeyCode keyCode) {
		this.applicationModelState = new ToggleCandidateVisibleState(keyCode, this.applicationModelState);
		this.applicationModelState.onEnter();

	}

	public void transitionToToggleCellColorState(final KeyCode keyCode, final boolean isShiftDown) {
		this.applicationModelState = new ToggleCellColorState(keyCode, isShiftDown, this.applicationModelState);
		this.applicationModelState.onEnter();
	}

	public void transitionToToggleCandidateColorState(final KeyCode keyCode, final boolean isShiftDown) {
		this.applicationModelState = new ToggleCandidateColorState(keyCode, isShiftDown, this.applicationModelState);
		this.applicationModelState.onEnter();
	}

	public void transitionToActiveCandidateChangedState(final KeyCode keyCode) {
		this.applicationModelState = new ActiveCandidateChangedState(keyCode, this.applicationModelState);
		this.applicationModelState.onEnter();
	}

	public void transitionToResetAllColorsState() {
		this.applicationModelState = new ResetAllColorsState(this.applicationModelState);
		this.applicationModelState.onEnter();
	}

	public void transitionToUndoActionState() {
		this.applicationModelState = new UndoActionState(this.applicationModelState);
		this.applicationModelState.onEnter();
	}

	public void transitionToRedoActionState() {
		this.applicationModelState = new RedoActionState(this.applicationModelState);
		this.applicationModelState.onEnter();
	}

	public void transitionToMouseModeChangedState(final String newMouseMode) {
		this.applicationModelState = new MouseModeChangedState(newMouseMode, this.applicationModelState);
		this.applicationModelState.onEnter();
	}

	public void transitionToActiveColorChangedState(final int colorIndex) {
		this.applicationModelState = new ActiveColorChangedState(colorIndex, this.applicationModelState);
		this.applicationModelState.onEnter();
	}

	public void transitionToNewRandomPuzzleState(final String generateSudokuString) {
		this.applicationModelState = new NewRandomPuzzleState(generateSudokuString, this.applicationModelState);
		this.applicationModelState.onEnter();
	}

	public void transitionToNewEmptyPuzzleState() {
		this.applicationModelState = new NewEmptyPuzzleState(this.applicationModelState);
		this.applicationModelState.onEnter();
	}

	public void transitionToOpenedFileState(final File selectedFile) {
		this.applicationModelState = new OpenedFileState(selectedFile, this.applicationModelState);
		this.applicationModelState.onEnter();

	}

	public void transitionToSavedFileState(final File selectedFile) {
		this.applicationModelState = new SavedFileState(selectedFile, this.applicationModelState);
		this.applicationModelState.onEnter();
	}

	public void transitionToRestartPuzzleState() {
		this.applicationModelState = new RestartPuzzleState(this.applicationModelState);
		this.applicationModelState.onEnter();
	}

	public void transitionToShowVagueHintState() {
		this.applicationModelState = new ShowVagueHintState(this.applicationModelState);
		this.applicationModelState.onEnter();
	}

	public void transitionToShowPartialHintState() {
		this.applicationModelState = new ShowPartialHintState(this.applicationModelState);
		this.applicationModelState.onEnter();
	}

	public void transitionToShowSpecificHintState() {
		this.applicationModelState = new ShowSpecificHintState(this.applicationModelState);
		this.applicationModelState.onEnter();
	}

	public void transitionToFillInSinglesPuzzleState() {
		this.applicationModelState = new FillInSinglesPuzzleState(this.applicationModelState);
		this.applicationModelState.onEnter();
	}

	public void transitionToSetGivenCellsState() {
		this.applicationModelState = new SetGivenCellsState(this.applicationModelState);
		this.applicationModelState.onEnter();
	}

	public void transitionToApplyHintState() {
		this.applicationModelState = new ApplyHintState(this.applicationModelState);
		this.applicationModelState.onEnter();
	}

	public void transitionToHideHintState() {
		this.applicationModelState = new HideHintState(this.applicationModelState);
		this.applicationModelState.onEnter();
	}

	public void transitionToShowContextMenuState() {
		this.applicationModelState = new ShowContextMenuState(this.applicationModelState);
		this.applicationModelState.onEnter();

	}

	public void transitionToSaveMiscellaneousSettingsState() {
		this.applicationModelState = new SaveMiscellaneousSettingsState(this.applicationModelState);
		this.applicationModelState.onEnter();
	}

	public void transitionToSaveColorSettingsState() {
		this.applicationModelState = new SaveColorSettingsState(this.applicationModelState);
		this.applicationModelState.onEnter();
	}

	public void transitionToSaveDifficultySettingsState() {
		this.applicationModelState = new SaveDifficultySettingsState(this.applicationModelState);
		this.applicationModelState.onEnter();
	}

	public void transitionToSavePuzzleGenerationSettingsState() {
		this.applicationModelState = new SavePuzzleGenerationSettingsState(this.applicationModelState);
		this.applicationModelState.onEnter();
	}

	public void transitionToSaveSolverSettingsState(final List<StepConfig> stepConfigs) {
		this.applicationModelState = new SaveSolverSettingsState(stepConfigs, this.applicationModelState);
		this.applicationModelState.onEnter();
	}

	public void transitionToCopyPuzzleState(final boolean isGivensOnly) {
		this.applicationModelState = new CopyPuzzleState(isGivensOnly, this.applicationModelState);
		this.applicationModelState.onEnter();
	}

	public void transitionToPastePuzzleState() {
		this.applicationModelState = new PastePuzzleState(this.applicationModelState);
		this.applicationModelState.onEnter();
	}

	public void transitionToUnlockGivenCellsState() {
		this.applicationModelState = new UnlockGivenCellsState(this.applicationModelState);
		this.applicationModelState.onEnter();
	}

}
