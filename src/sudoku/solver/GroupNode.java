
package sudoku.solver;

import java.util.ArrayList;
import java.util.List;

import sudoku.SolutionStep;
import sudoku.Sudoku2;
import sudoku.SudokuSet;

/**
 *
 * @author hobiwan
 */
public class GroupNode {
	public SudokuSet indices = new SudokuSet(); // indices as bit mask
	public SudokuSet buddies = new SudokuSet(); // all buddies that can see all cells in the group node
	public int cand; // candidate for grouped link
	public int line = -1; // row (index in Sudoku2.ROWS), -1 if not applicable
	public int col = -1; // col (index in Sudoku2.COLS), -1 if not applicable
	public int block; // block (index in Sudoku2.BLOCKS)
	public int index1; // index of first cell
	public int index2; // index of second cell
	public int index3; // index of third cell or -1, if grouped node consists only of two cells

	private static SudokuSet candInHouse = new SudokuSet(); // all positions for a given candidate in a given house
	private static SudokuSet tmpSet = new SudokuSet(); // for check with blocks

	/**
	 * Creates a new instance of GroupNode
	 * 
	 * @param cand
	 * @param indices
	 */
	public GroupNode(int cand, SudokuSet indices) {
		this.cand = cand;
		this.indices.set(indices);
		this.index1 = indices.get(0);
		this.index2 = indices.get(1);
		this.index3 = -1;
		if (indices.size() > 2) {
			this.index3 = indices.get(2);
		}
		this.block = Sudoku2.getBlock(this.index1);
		if (Sudoku2.getLine(this.index1) == Sudoku2.getLine(this.index2)) {
			this.line = Sudoku2.getLine(this.index1);
		}
		if (Sudoku2.getCol(this.index1) == Sudoku2.getCol(this.index2)) {
			this.col = Sudoku2.getCol(this.index1);
		}
		// calculate the buddies
		this.buddies.set(Sudoku2.buddies[this.index1]);
		this.buddies.and(Sudoku2.buddies[this.index2]);
		if (this.index3 >= 0) {
			this.buddies.and(Sudoku2.buddies[this.index3]);
		}
	}

	@Override
	public String toString() {
		return "GroupNode: " + this.cand + " - "
				+ SolutionStep.getCompactCellPrint(this.index1, this.index2, this.index3) + "  - " + this.index1 + "/"
				+ this.index2 + "/" + this.index3 + " (" + this.line + "/" + this.col + "/" + this.block + ")";
	}

	/**
	 * Gets all group nodes from the given sudoku and puts them in an ArrayList.
	 *
	 * For all candidates in all lines and all cols do: - check if they have a
	 * candidate left - if so, check if an intersection of line/col and a block
	 * contains more than one candidate; if yes -> group node found
	 * 
	 * @param finder
	 * @return
	 */
	public static List<GroupNode> getGroupNodes(SudokuStepFinder finder) {
		final List<GroupNode> groupNodes = new ArrayList<GroupNode>();

		getGroupNodesForHouseType(groupNodes, finder, Sudoku2.LINE_TEMPLATES);
		getGroupNodesForHouseType(groupNodes, finder, Sudoku2.COL_TEMPLATES);

		return groupNodes;
	}

	private static void getGroupNodesForHouseType(List<GroupNode> groupNodes, SudokuStepFinder finder,
			SudokuSet[] houses) {
		for (int i = 0; i < houses.length; i++) {
			for (int cand = 1; cand <= 9; cand++) {
				candInHouse.set(houses[i]);
				candInHouse.and(finder.getCandidates()[cand]);
				if (candInHouse.isEmpty()) {
					// no candidates left in this house -> proceed
					continue;
				}

				// candidates left in house -> check blocks
				for (int j = 0; j < Sudoku2.BLOCK_TEMPLATES.length; j++) {
					tmpSet.set(candInHouse);
					tmpSet.and(Sudoku2.BLOCK_TEMPLATES[j]);
					if (tmpSet.isEmpty()) {
						// no candidates in this house -> proceed with next block
						continue;
					} else {
						// rather complicated for performance reasons (isEmpty() is much faster than
						// size())
						if (tmpSet.size() >= 2) {
							// group node found
							groupNodes.add(new GroupNode(cand, tmpSet));
						}
					}
				}
			}
		}
	}

	public static void main(String[] args) {
		final Sudoku2 sudoku = new Sudoku2();
		sudoku.setSudoku(
				":0000:x:.4..1..........5.6......3.15.38.2...7......2..........6..5.7....2.....1....3.14..:211 213 214 225 235 448 465 366 566 468 469::");
		long ticks = System.currentTimeMillis();
		final List<GroupNode> groupNodes = GroupNode.getGroupNodes(null);
		ticks = System.currentTimeMillis() - ticks;
		System.out.println("getGroupNodes(): " + ticks + "ms, " + groupNodes.size() + " group nodes");
		for (final GroupNode node : groupNodes) {
			System.out.println("  " + node);
		}
	}
}
