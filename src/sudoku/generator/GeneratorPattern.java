/*
 * Copyright (C) 2008-12  Bernhard Hobiger
 *
 * This file is part of HoDoKu.
 *
 * HoDoKu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * HoDoKu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with HoDoKu. If not, see <http://www.gnu.org/licenses/>.
 */

package sudoku.generator;

import java.util.Arrays;

import sudoku.model.SudokuPuzzle;

/**
 * A pattern, that indicates, which cells should contain givens when generating
 * new puzzles.<br>
 * <br>
 *
 * <b>Caution:</b> The setter for {@link #pattern} only sets the reference, the
 * constructor actually makes a copy of the pattern, that has been passed in.
 * When working with new patterns, only the constructore should be used, the
 * setter is used internally by <code>XmlDecoder</code>.
 *
 * @author hobiwan
 */
public class GeneratorPattern implements Cloneable {

	// One entry per cell; if it is <code>true</code>, the cell must be a given.
	private final boolean[] isGiven;

	private String name = "";

	// Indicates if the puzzle is valid.
	private boolean valid;

	private GeneratorPattern() {
		this.isGiven = new boolean[SudokuPuzzle.NUMBER_OF_CELLS_PER_DIMENSION
				* SudokuPuzzle.NUMBER_OF_CELLS_PER_DIMENSION];
	}

	public GeneratorPattern(String name) {
		this.name = name;
		this.valid = false;
		this.isGiven = new boolean[SudokuPuzzle.NUMBER_OF_CELLS_PER_DIMENSION
				* SudokuPuzzle.NUMBER_OF_CELLS_PER_DIMENSION];
	}

	public GeneratorPattern(String name, boolean[] pattern) {
		this.name = name;
		this.isGiven = Arrays.copyOf(pattern, pattern.length);
	}

	@Override
	public GeneratorPattern clone() {
		GeneratorPattern newPattern = null;
		newPattern = new GeneratorPattern();
		newPattern.setName(this.name);
		newPattern.setValid(this.valid);
		System.arraycopy(this.isGiven, 0, newPattern.isGiven, 0, this.isGiven.length);
		return newPattern;
	}

	@Override
	public String toString() {
		return this.name + ": " + Arrays.toString(this.isGiven);
	}

	public int getNumberOfGivens() {
		int anz = 0;
		for (int i = 0; i < this.isGiven.length; i++) {
			if (this.isGiven[i]) {
				anz++;
			}
		}
		return anz;
	}

	public boolean[] getIsGiven() {
		return this.isGiven;
	}

	public void setIsGiven(boolean[] pattern) {
		// TODO - needed?
//		this.isGiven = pattern;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isValid() {
		return this.valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

}
