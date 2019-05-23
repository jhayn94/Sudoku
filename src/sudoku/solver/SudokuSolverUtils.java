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
package sudoku.solver;

import java.math.BigInteger;
import java.util.List;

import sudoku.view.util.PuzzleSolutionStep;

public class SudokuSolverUtils {

	/**
	 * Clears the list. To avoid memory leaks all steps in the list are explicitly
	 * nullified.
	 *
	 */
	public static void clearStepListWithNullify(List<PuzzleSolutionStep> steps) {
		if (steps != null) {
			for (int i = 0; i < steps.size(); i++) {
				steps.get(i).reset();
				steps.set(i, null);
			}
			steps.clear();
		}
	}

	/**
	 * Clears the list. The steps are not nullfied, but the list items are.
	 *
	 */
	public static void clearStepList(List<PuzzleSolutionStep> steps) {
		if (steps != null) {
			for (int i = 0; i < steps.size(); i++) {
				steps.set(i, null);
			}
			steps.clear();
		}
	}

	/**
	 * Calculates n over k
	 */
	public static int combinations(int n, int k) {
		if (n <= 167) {
			double fakN = 1;
			for (int i = 2; i <= n; i++) {
				fakN *= i;
			}
			double fakNMinusK = 1;
			for (int i = 2; i <= n - k; i++) {
				fakNMinusK *= i;
			}
			double fakK = 1;
			for (int i = 2; i <= k; i++) {
				fakK *= i;
			}
			return (int) (fakN / (fakNMinusK * fakK));
		} else {
			BigInteger fakN = BigInteger.ONE;
			for (int i = 2; i <= n; i++) {
				fakN = fakN.multiply(new BigInteger(i + ""));
			}
			BigInteger fakNMinusK = BigInteger.ONE;
			for (int i = 2; i <= n - k; i++) {
				fakNMinusK = fakNMinusK.multiply(new BigInteger(i + ""));
			}
			BigInteger fakK = BigInteger.ONE;
			for (int i = 2; i <= k; i++) {
				fakK = fakK.multiply(new BigInteger(i + ""));
			}
			fakNMinusK = fakNMinusK.multiply(fakK);
			fakN = fakN.divide(fakNMinusK);
			return fakN.intValue();
		}
	}

	public static String getCandString(int candidate) {
		return String.valueOf(candidate);
	}

}
