package sudoku.state.model.hint;

import java.util.List;

import sudoku.Chain;
import sudoku.SolutionType;
import sudoku.core.HodokuFacade;
import sudoku.core.ViewController;
import sudoku.model.SudokuPuzzleValues;
import sudoku.state.model.ApplicationModelState;
import sudoku.view.hint.HintButtonPane;
import sudoku.view.hint.HintTextArea;
import sudoku.view.puzzle.SudokuPuzzleCell;
import sudoku.view.util.ColorUtils;
import sudoku.view.util.ColorUtils.ColorState;
import sudoku.view.util.LabelConstants;

/**
 * This class updates the state of the application when the user requests a
 * specific hint (i.e. the exact next displayedHint).
 */
public class ShowSpecificHintState extends ApplicationModelState {

	public ShowSpecificHintState(final ApplicationModelState lastState) {
		super(lastState, false);
	}

	@Override
	public void onEnter() {
		// TODO - decide if candidate color states should be cleared first to avoid
		// confusion.
//		this.resetColorStates(false, true, Arrays.asList(ColorState.values()));
		this.displayedHint = HodokuFacade.getInstance().getHint(this.sudokuPuzzleValues);
		final HintTextArea hintTextArea = ViewController.getInstance().getHintTextArea();
		if (this.displayedHint == null) {
			hintTextArea.getHintTextArea().setText(LabelConstants.PUZZLE_SOLVED);

		} else {
			final String newHintText = SolutionType.GIVE_UP == this.displayedHint.getType() ? LabelConstants.NO_MOVES
					: this.displayedHint.toString();
			hintTextArea.getHintTextArea().setText(newHintText);
			final HintButtonPane hintButtonPane = ViewController.getInstance().getHintButtonPane();
			hintButtonPane.getApplyHintButton().setDisable(false);
			hintButtonPane.getHideHintButton().setDisable(false);

			// The order of these cannot change! Otherwise the ALS candidate colors
			// overwrite the other candidates.
			this.updateColorForAlmostLockedSetCandidates();
			this.updateChainHintCandidates();
			this.updateColorForColorHintCandidates();
			this.updateColorForPrimaryHintCandidates();
			this.updateColorForSecondaryHintCandidates();
			this.updateColorForTertiaryHintCandidates();
			this.updateColorForDeletableCandidates();
			this.updateColorForCannibalCandidates();
		}
	}

	/**
	 * This is mostly a direct port from HoDoKu. I seriously can't figure out what
	 * is going on here, so I left it untouched. If there are any bugs with it, a
	 * re-port with fewer tweaks might help.
	 */
	private void updateChainHintCandidates() {
		final int chainIndex = this.displayedHint.getChains().isEmpty() ? -1 : 0;
		for (int row = 0; row < SudokuPuzzleValues.CELLS_PER_HOUSE; row++) {
			for (int col = 0; col < SudokuPuzzleValues.CELLS_PER_HOUSE; col++) {
				for (int candidate = 1; candidate <= SudokuPuzzleValues.CELLS_PER_HOUSE; candidate++) {
					ColorState colorStateToApply = ColorState.NONE;
					if (this.sudokuPuzzleValues.getCandidateDigitsForCell(row, col).contains(candidate)) {
						final int linearCellIndex = row * SudokuPuzzleValues.CELLS_PER_HOUSE + col;
						for (int k = 0; k < this.displayedHint.getChains().size(); k++) {
							if ((!this.displayedHint.getType().isKrakenFish() || chainIndex != -1)
									&& (chainIndex == -1 || k == chainIndex)) {
								final Chain chain = this.displayedHint.getChains().get(k);
								for (int chainSegmentIndex = chain.getStart(); chainSegmentIndex <= chain
										.getEnd(); chainSegmentIndex++) {
									if (chain.getChain()[chainSegmentIndex] != Integer.MIN_VALUE) {
										final int chainEntry = Math.abs(chain.getChain()[chainSegmentIndex]);
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
												colorStateToApply = ColorState.PRIMARY_HINT_CANDIDATE;
											} else {
												colorStateToApply = ColorState.SECONDARY_HINT_CANDIDATE;
											}
										}
									}
								}
							}
						}
					}
					final SudokuPuzzleCell cell = ViewController.getInstance().getSudokuPuzzleCell(row, col);
					if (ColorState.NONE != colorStateToApply) {
						this.setCandidateColorForCell(cell.getRow(), cell.getCol(), colorStateToApply, candidate);
					}
				}
			}
		}
	}

	private void updateColorForColorHintCandidates() {
		// Technically, I think there can only be one, but I am porting the code from
		// HoDoKu, so I didn't change it.
		this.displayedHint.getValues().forEach(candidate -> {
			this.displayedHint.getColorCandidates().forEach((linearCellIndex, colorIndex) -> {
				final int row = linearCellIndex / SudokuPuzzleValues.CELLS_PER_HOUSE;
				final int col = linearCellIndex % SudokuPuzzleValues.CELLS_PER_HOUSE;
				final ColorState colorStateToApply = ColorUtils.getColorStateForColoringIndex(colorIndex);
				this.setCandidateColorForCell(row, col, colorStateToApply, candidate);
			});
		});

	}

	private void updateColorForPrimaryHintCandidates() {
		// Color primary candidates.
		final List<Integer> cellIndiciesForHint = this.displayedHint.getIndices();
		final List<Integer> candidatesForHint = this.displayedHint.getValues();
		cellIndiciesForHint.forEach(linearCellIndex -> {
			final int row = linearCellIndex / SudokuPuzzleValues.CELLS_PER_HOUSE;
			final int col = linearCellIndex % SudokuPuzzleValues.CELLS_PER_HOUSE;
			candidatesForHint.forEach(candidate -> {
				this.setCandidateColorForCell(row, col, ColorState.PRIMARY_HINT_CANDIDATE, candidate);
			});
		});
	}

	private void updateColorForSecondaryHintCandidates() {
		this.displayedHint.getFins().forEach(finCandidate -> {
			final int linearCellIndex = finCandidate.getIndex();
			final int row = linearCellIndex / SudokuPuzzleValues.CELLS_PER_HOUSE;
			final int col = linearCellIndex % SudokuPuzzleValues.CELLS_PER_HOUSE;
			final int candidate = finCandidate.getValue();
			this.setCandidateColorForCell(row, col, ColorState.SECONDARY_HINT_CANDIDATE, candidate);
		});
	}

	private void updateColorForTertiaryHintCandidates() {
		this.displayedHint.getEndoFins().forEach(endoFinCandidate -> {
			final int linearCellIndex = endoFinCandidate.getIndex();
			final int row = linearCellIndex / SudokuPuzzleValues.CELLS_PER_HOUSE;
			final int col = linearCellIndex % SudokuPuzzleValues.CELLS_PER_HOUSE;
			final int candidate = endoFinCandidate.getValue();
			this.setCandidateColorForCell(row, col, ColorState.TERTIARY_HINT_CANDIDATE, candidate);
		});
	}

	private void updateColorForDeletableCandidates() {
		this.displayedHint.getCandidatesToDelete().forEach(candidateToDelete -> {
			final int linearCellIndex = candidateToDelete.getIndex();
			final int row = linearCellIndex / SudokuPuzzleValues.CELLS_PER_HOUSE;
			final int col = linearCellIndex % SudokuPuzzleValues.CELLS_PER_HOUSE;
			final int candidate = candidateToDelete.getValue();
			this.setCandidateColorForCell(row, col, ColorState.DELETABLE_HINT_CANDIDATE, candidate);
		});
	}

	private void updateColorForCannibalCandidates() {
		this.displayedHint.getCannibalistic().forEach(cannibalisticCandidate -> {
			final int linearCellIndex = cannibalisticCandidate.getIndex();
			final int row = linearCellIndex / SudokuPuzzleValues.CELLS_PER_HOUSE;
			final int col = linearCellIndex % SudokuPuzzleValues.CELLS_PER_HOUSE;
			final int candidate = cannibalisticCandidate.getValue();
			this.setCandidateColorForCell(row, col, ColorState.QUINARY_HINT_CANDIDATE, candidate);
		});
	}

	private void updateColorForAlmostLockedSetCandidates() {
		final int chainIndex = this.displayedHint.getChains().isEmpty() ? -1 : 0;
		this.displayedHint.getAlses().forEach(almostLockedSet -> {
			almostLockedSet.getIndices().forEach(linearCellIndex -> {
				final int row = linearCellIndex / SudokuPuzzleValues.CELLS_PER_HOUSE;
				final int col = linearCellIndex % SudokuPuzzleValues.CELLS_PER_HOUSE;
				almostLockedSet.getCandidates().forEach(candidate -> {
					final int alsIndex = this.displayedHint.getAlsIndex(linearCellIndex, chainIndex);
					if (!this.displayedHint.getType().isKrakenFish()) {
						final ColorState colorStateToApply = ColorUtils.getColorStateForAlmostLockedSetIndex(alsIndex);
						this.setCandidateColorForCell(row, col, colorStateToApply, candidate);
					}
				});
			});
		});
	}

}