package sudoku.state.model.hint;

import java.util.List;

import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.shape.Line;
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

	private static final int COORDINATE_ERROR_OFFSET = 10;

	public ShowSpecificHintState(final ApplicationModelState lastState) {
		super(lastState, false);
	}

	@Override
	public void onEnter() {
		this.resetColorStates(false, true, ColorUtils.getHintColorStates());
		this.displayedHint = HodokuFacade.getInstance().getHint(this.sudokuPuzzleValues);
		final HintTextArea hintTextArea = ViewController.getInstance().getHintTextArea();
		if (this.displayedHint == null) {
			hintTextArea.getHintTextArea().setText(LabelConstants.PUZZLE_SOLVED);
		} else {
			final String newHintText = SolutionType.GIVE_UP == this.displayedHint.getType() ? LabelConstants.NO_MOVES
					: this.displayedHint.toString();
			hintTextArea.getHintTextArea().setText(newHintText);
			final HintButtonPane hintButtonPane = ViewController.getInstance().getHintButtonPane();
			if (SolutionType.GIVE_UP != this.displayedHint.getType()) {
				hintButtonPane.getApplyHintButton().setDisable(false);
				hintButtonPane.getHideHintButton().setDisable(false);
			}

			this.updateCandidateColorsForHint();
			this.showLinksForHint();
		}
	}

	private void updateCandidateColorsForHint() {
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

	private void showLinksForHint() {
		final ObservableList<Node> annotationPaneChildren = ViewController.getInstance().getRootPane().getChildren();
		final int activeChainIndex = this.displayedHint.getChains().isEmpty() ? -1 : 0;
		for (int chainIndex = 0; chainIndex < this.displayedHint.getChainAnz(); chainIndex++) {
			if ((activeChainIndex == -1 || activeChainIndex == chainIndex)
					&& (!this.displayedHint.getType().isKrakenFish() || activeChainIndex != -1)) {
				final Chain chain = this.displayedHint.getChains().get(chainIndex);
				final int[] linkData = chain.getChain();
				int oldChe = 0;
				System.out.println(chain);
				for (int i = chain.getStart(); i < chain.getEnd(); i++) {
					int che = Math.abs(linkData[i]);
					final int che1 = Math.abs(linkData[i + 1]);
					// TODO - what purpose does this code serve?
					if (linkData[i] > 0 && linkData[i + 1] < 0) {
						oldChe = che;
					}
					if (linkData[i] == Integer.MIN_VALUE && linkData[i + 1] < 0) {
						che = oldChe;
					}
					if (linkData[i] < 0 && linkData[i + 1] > 0) {
						che = oldChe;
					}

					final int startCellIndex = Chain.getSCellIndex(che);
					final int endCellIndex = Chain.getSCellIndex(che1);
					if (che1 != Integer.MIN_VALUE && startCellIndex != endCellIndex) {
						final int startRow = startCellIndex / SudokuPuzzleValues.CELLS_PER_HOUSE;
						final int startCol = startCellIndex % SudokuPuzzleValues.CELLS_PER_HOUSE;
						final int endRow = endCellIndex / SudokuPuzzleValues.CELLS_PER_HOUSE;
						final int endCol = endCellIndex % SudokuPuzzleValues.CELLS_PER_HOUSE;
						final int startCandidate = Chain.getSCandidate(che);
						final int endCandidate = Chain.getSCandidate(che1);
						final SudokuPuzzleCell startCell = ViewController.getInstance().getSudokuPuzzleCell(startRow, startCol);
						final SudokuPuzzleCell endCell = ViewController.getInstance().getSudokuPuzzleCell(endRow, endCol);
						final Label startCandidateLabel = startCell.getCandidateLabelForDigit(startCandidate);
						final Label endCandidateLabel = endCell.getCandidateLabelForDigit(endCandidate);
						final Bounds startBounds = startCandidateLabel.localToScreen(startCandidateLabel.getBoundsInLocal());
						final Bounds endBounds = endCandidateLabel.localToScreen(endCandidateLabel.getBoundsInLocal());
						final double startX = (startBounds.getMinX() + startBounds.getMaxX()) / 2.0;
						final double startY = (startBounds.getMinY() + startBounds.getMaxY()) / 2.0;
						final double endX = (endBounds.getMinX() + endBounds.getMaxX()) / 2.0;
						final double endY = (endBounds.getMinY() + endBounds.getMaxY()) / 2.0;
						// This math was just off by a little bit each time. After awhile of trying to
						// figure out why, I instead adjusted the end result to line up with the center
						// of the label. It most likely has something to do with padding or borders.
						final Line link = new Line(startX - COORDINATE_ERROR_OFFSET, startY - COORDINATE_ERROR_OFFSET,
								endX - COORDINATE_ERROR_OFFSET, endY - COORDINATE_ERROR_OFFSET);
						link.setStrokeWidth(3.0);
						link.getStyleClass().add(ColorUtils.HINT_COLOR_4_CSS_CLASS);
						System.out.println(startRow + " " + startCol + " " + startCandidate);
						System.out.println(endRow + " " + endCol + " " + endCandidate);
						System.out.println(link);
						if (!Chain.isSStrong(che1)) {
							link.getStrokeDashArray().addAll(5.0, 10.0);
						}
						annotationPaneChildren.add(link);
					}
				}
			}
		}
	}

	/**
	 * This is mostly a direct port from HoDoKu. I seriously can't figure out what
	 * is going on here, so I left it untouched. If there are any bugs with it, a
	 * re-port with fewer tweaks might help.
	 */
	private void updateChainHintCandidates() {
		final int activeChainIndex = this.displayedHint.getChains().isEmpty() ? -1 : 0;
		for (int row = 0; row < SudokuPuzzleValues.CELLS_PER_HOUSE; row++) {
			for (int col = 0; col < SudokuPuzzleValues.CELLS_PER_HOUSE; col++) {
				for (int candidate = 1; candidate <= SudokuPuzzleValues.CELLS_PER_HOUSE; candidate++) {
					ColorState colorStateToApply = ColorState.NONE;
					if (this.sudokuPuzzleValues.getCandidateDigitsForCell(row, col).contains(candidate)) {
						final int linearCellIndex = row * SudokuPuzzleValues.CELLS_PER_HOUSE + col;
						for (int k = 0; k < this.displayedHint.getChains().size(); k++) {
							if ((!this.displayedHint.getType().isKrakenFish() || activeChainIndex != -1)
									&& (activeChainIndex == -1 || k == activeChainIndex)) {
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