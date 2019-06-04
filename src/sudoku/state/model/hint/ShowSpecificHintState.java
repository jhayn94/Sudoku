package sudoku.state.model.hint;

import java.util.ArrayList;
import java.util.List;

import sudoku.Candidate;
import sudoku.Chain;
import sudoku.SolutionType;
import sudoku.core.HodokuFacade;
import sudoku.core.ViewController;
import sudoku.model.SudokuPuzzleValues;
import sudoku.state.model.ApplicationModelState;
import sudoku.view.hint.HintButtonPane;
import sudoku.view.hint.HintTextArea;
import sudoku.view.puzzle.SudokuPuzzleCell;
import sudoku.view.util.ColorUtils.ColorState;
import sudoku.view.util.LabelConstants;

/**
 * This class updates the state of the application when the user requests a
 * specific hint (i.e. the exact next displayedHint).
 */
public class ShowSpecificHintState extends ApplicationModelState {

	private static final int DELTA = 5;

	public ShowSpecificHintState(final ApplicationModelState lastState) {
		super(lastState, false);
	}

	@Override
	public void onEnter() {
		this.displayedHint = HodokuFacade.getInstance().getHint(this.sudokuPuzzleValues);
		final HintTextArea hintTextArea = ViewController.getInstance().getHintTextArea();
		final String newHintText = SolutionType.GIVE_UP == this.displayedHint.getType() ? LabelConstants.NO_MOVES
				: this.displayedHint.toString();
		hintTextArea.getHintTextArea().setText(newHintText);
		final HintButtonPane hintButtonPane = ViewController.getInstance().getHintButtonPane();
		hintButtonPane.getApplyHintButton().setDisable(false);
		hintButtonPane.getHideHintButton().setDisable(false);
		final int chainIndex = this.displayedHint.getChains().isEmpty() ? -1 : 0;

		// if chainIndex is != -1, alsToShow contains the indices of the ALS, that are
		// part of the chain
		final List<Integer> alsToShow = new ArrayList<Integer>();

		for (int row = 0; row < SudokuPuzzleValues.CELLS_PER_HOUSE; row++) {
			for (int col = 0; col < SudokuPuzzleValues.CELLS_PER_HOUSE; col++) {
				for (int candidate = 1; candidate <= SudokuPuzzleValues.CELLS_PER_HOUSE; candidate++) {
					ColorState candColor = ColorState.NONE;
					if (this.sudokuPuzzleValues.getCandidateDigitsForCell(row, col).contains(candidate)) {
						final int linearCellIndex = row * SudokuPuzzleValues.CELLS_PER_HOUSE + col;
						if (this.displayedHint.getIndices().indexOf(linearCellIndex) >= 0
								&& this.displayedHint.getValues().indexOf(candidate) >= 0) {
							candColor = ColorState.COLORSTATE4A;
						}
						final int alsIndex = this.displayedHint.getAlsIndex(linearCellIndex, chainIndex);
						if (alsIndex != -1 && ((chainIndex == -1 && !this.displayedHint.getType().isKrakenFish())
								|| alsToShow.contains(alsIndex))) {
							candColor = ColorState.COLORSTATE4A;
						}
						for (int k = 0; k < this.displayedHint.getChains().size(); k++) {
							if (this.displayedHint.getType().isKrakenFish() && chainIndex == -1) {
								continue;
							}
							if (chainIndex != -1 && k != chainIndex) {
								continue;
							}
							final Chain chain = this.displayedHint.getChains().get(k);
							for (int j = chain.getStart(); j <= chain.getEnd(); j++) {
								if (chain.getChain()[j] == Integer.MIN_VALUE) {
									continue;
								}
								final int chainEntry = Math.abs(chain.getChain()[j]);
								int index1 = -1, index2 = -1, index3 = -1;
								if (Chain.getSNodeType(chainEntry) == Chain.NORMAL_NODE) {
									index1 = Chain.getSCellIndex(chainEntry);
								}
								if (Chain.getSNodeType(chainEntry) == Chain.GROUP_NODE) {
									index1 = Chain.getSCellIndex(chainEntry);
									index2 = Chain.getSCellIndex2(chainEntry);
									index3 = Chain.getSCellIndex3(chainEntry);
								}
								if ((linearCellIndex == index1 || linearCellIndex == index2 || linearCellIndex == index3)
										&& Chain.getSCandidate(chainEntry) == candidate) {
									if (Chain.isSStrong(chainEntry)) {
										// strong link
										candColor = ColorState.COLORSTATE4A;
									} else {
										candColor = ColorState.COLORSTATE2A;
									}
								}
							}
						}
						for (final Candidate cand : this.displayedHint.getFins()) {
							if (cand.getIndex() == linearCellIndex && cand.getValue() == candidate) {
								candColor = ColorState.COLORSTATE2A;
							}
						}
						for (final Candidate cand : this.displayedHint.getEndoFins()) {
							if (cand.getIndex() == linearCellIndex && cand.getValue() == candidate) {
								candColor = ColorState.COLORSTATE3A;
							}
						}
						if (this.displayedHint.getValues().contains(candidate)
								&& this.displayedHint.getColorCandidates().containsKey(linearCellIndex)) {
							candColor = ColorState.COLORSTATE5A;
						}
						for (final Candidate cand : this.displayedHint.getCandidatesToDelete()) {
							if (cand.getIndex() == linearCellIndex && cand.getValue() == candidate) {
								candColor = ColorState.COLORSTATE1A;
							}
						}
						for (final Candidate cand : this.displayedHint.getCannibalistic()) {
							if (cand.getIndex() == linearCellIndex && cand.getValue() == candidate) {
								candColor = ColorState.COLORSTATE5A;
							}
						}
					}
					final SudokuPuzzleCell cell = ViewController.getInstance().getSudokuPuzzleCell(row, col);
					this.setCandidateColorForCell(cell, candColor, candidate);
				}
			}
		}

	}
}