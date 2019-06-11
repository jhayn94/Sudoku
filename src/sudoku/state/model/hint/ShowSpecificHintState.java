package sudoku.state.model.hint;

import java.util.ArrayList;
import java.util.List;

import sudoku.Chain;
import sudoku.SolutionType;
import sudoku.core.HodokuFacade;
import sudoku.core.ViewController;
import sudoku.factories.LayoutFactory;
import sudoku.model.SudokuPuzzleValues;
import sudoku.state.model.ApplicationModelState;
import sudoku.view.ApplicationRootPane;
import sudoku.view.hint.CurvedHintAnnotation;
import sudoku.view.hint.HintAnnotation;
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
		ViewController.getInstance().getRootPane().removeAllAnnotations();
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

	/**
	 * Creates the links (arrows) for a link, if applicable. As with a few other
	 * methods, this code has been ported / modernized somewhat from HoDoKu to use a
	 * more modular OOP approach.
	 */
	private void showLinksForHint() {
		final int activeChainIndex = this.displayedHint.getChains().isEmpty() ? -1 : 0;
		for (int chainIndex = 0; chainIndex < this.displayedHint.getChainAnz(); chainIndex++) {
			if ((activeChainIndex == -1 || activeChainIndex == chainIndex)
					&& (!this.displayedHint.getType().isKrakenFish() || activeChainIndex != -1)) {
				final Chain chain = this.displayedHint.getChains().get(chainIndex);
				final int[] nodeData = chain.getChain();
				int oldNodeData = 0;
				for (int i = chain.getStart(); i < chain.getEnd(); i++) {
					int startNodeData = Math.abs(nodeData[i]);
					final int endNodeData = Math.abs(nodeData[i + 1]);
					// TODO - what purpose does this code serve? Something to do with forcing chains
					// and nets maybe?
					if (nodeData[i] > 0 && nodeData[i + 1] < 0) {
						oldNodeData = startNodeData;
					}
					if (nodeData[i] == Integer.MIN_VALUE && nodeData[i + 1] < 0) {
						startNodeData = oldNodeData;
					}
					if (nodeData[i] < 0 && nodeData[i + 1] > 0) {
						startNodeData = oldNodeData;
					}

					this.createAnnotation(startNodeData, endNodeData);
				}
			}
		}
		this.checkForAnnotationsToRedraw();
	}

	private void createAnnotation(final int startNodeData, final int endNodeData) {
		final HintAnnotation annotation;
		annotation = LayoutFactory.getInstance().createLinearHintAnnotation(startNodeData, endNodeData);
		if (annotation.isValid()) {
			ViewController.getInstance().getRootPane().addAnnotation(annotation);
		}
	}

	/**
	 * Checks for annotations which need to be redrawn because they overlap with
	 * other candidates in the hint. In this case, the link is redrawn with a slight
	 * curve to clean up possible confusion.
	 */
	private void checkForAnnotationsToRedraw() {
		final List<HintAnnotation> hintAnnotations = ViewController.getInstance().getHintAnnotations();
		final List<Integer> hintNodes = this.getAllHintNodes(hintAnnotations);

		final ApplicationRootPane annotationPane = ViewController.getInstance().getRootPane();
		final List<HintAnnotation> invalidAnnotations = this.getInvalidAnnotations(hintAnnotations, hintNodes);
		this.redrawInvalidAnnotations(annotationPane, invalidAnnotations);

	}

	private List<Integer> getAllHintNodes(final List<HintAnnotation> hintAnnotations) {
		final List<Integer> chainNodes = new ArrayList<>();
		hintAnnotations.forEach(annotation -> {
			chainNodes.add(annotation.getStartNodeData());
			chainNodes.add(annotation.getEndNodeData());
		});
		return chainNodes;
	}

	private List<HintAnnotation> getInvalidAnnotations(final List<HintAnnotation> hintAnnotations,
			final List<Integer> hintNodes) {
		final List<HintAnnotation> invalidAnnotations = new ArrayList<>();
		hintAnnotations.forEach(annotation -> {
			hintNodes.forEach(hintNode -> {
				if (annotation.intersectsWith(hintNode)) {
					invalidAnnotations.add(annotation);
				}
			});
		});
		return invalidAnnotations;
	}

	private void redrawInvalidAnnotations(final ApplicationRootPane annotationPane,
			final List<HintAnnotation> invalidAnnotations) {
		invalidAnnotations.forEach(invalidAnnotation -> {
			annotationPane.removeAnnotation(invalidAnnotation);
			final CurvedHintAnnotation curvedHintAnnotation = LayoutFactory.getInstance()
					.createCurvedHintAnnotation(invalidAnnotation.getStartNodeData(), invalidAnnotation.getEndNodeData());
			ViewController.getInstance().registerHintAnnotation(curvedHintAnnotation);
			annotationPane.addAnnotation(curvedHintAnnotation);
		});
	}

}