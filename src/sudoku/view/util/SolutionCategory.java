package sudoku.view.util;

public enum SolutionCategory {
	SINGLES("Singles"), INTERSECTIONS("Intersections"), SUBSETS("Subsets"), BASIC_FISH("Basic Fish"),
	FINNED_BASIC_FISH("(Sashimi) Finned Fish"), FRANKEN_FISH("Franken Fish"),
	FINNED_FRANKEN_FISH("Finned Franken Fish"), MUTANT_FISH("Mutant Fish"), FINNED_MUTANT_FISH("Finned Mutant Fish"),
	SINGLE_DIGIT_PATTERNS("Single Digit Patterns"), COLORING("Coloring"), UNIQUENESS("Uniqueness"),
	CHAINS_AND_LOOPS("Chains and Loops"), WINGS("Wings"), ALMOST_LOCKED_SETS("Almost Locked Sets"),
	ENUMERATIONS("Enumerations"), MISCELLANEOUS("Miscellaneous"), LAST_RESORT("Last Resort");

	private String categoryName;

	SolutionCategory(String catName) {
		this.categoryName = catName;
	}

	@Override
	public String toString() {
		return "SolutionCategory[" + this.categoryName + "]";
	}

	public String getCategoryName() {
		return this.categoryName;
	}

	public void setCategoryName(String name) {
		this.categoryName = name;
	}

	public boolean isFish() {
		if (this == BASIC_FISH || this == FINNED_BASIC_FISH || this == FRANKEN_FISH || this == FINNED_FRANKEN_FISH
				|| this == MUTANT_FISH || this == FINNED_MUTANT_FISH) {
			return true;
		}
		return false;
	}
}
