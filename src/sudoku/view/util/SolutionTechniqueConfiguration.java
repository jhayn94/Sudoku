package sudoku.view.util;

/**
 * This class contains methods to configure a solution step in the solver.
 */
public final class SolutionTechniqueConfiguration implements Cloneable, Comparable<SolutionTechniqueConfiguration> {
	private int stepNumberInSolution;
	private SolutionTechnique solutionTechnique;
	private int level; // Index in Options.difficultyLevels
	private SolutionCategory solutionCategory;
	private int baseScore; // score for every instance of step in solution
	private boolean enabled; // used in solution?
	private boolean allStepsEnabled; // searched for when all steps are found?
	private int indexProgress; // search order when rating the efficiency of steps
	private boolean enabledProgress; // enabled when rating the efficiency of steps
	private boolean enabledTraining; // enabled for training/practicing mode

	public SolutionTechniqueConfiguration(int index, SolutionTechnique type, int level, SolutionCategory category,
			int baseScore, boolean enabled, boolean allStepsEnabled, int indexProgress, boolean enabledProgress,
			boolean enabledTraining) {
		this.setIndex(index);
		this.setTechnique(type);
		this.setLevel(level);
		this.setCategory(category);
		this.setBaseScore(baseScore);
		this.setEnabled(enabled);
		this.setAllStepsEnabled(allStepsEnabled);
		this.setIndexProgress(indexProgress);
		this.setEnabledProgress(enabledProgress);
		this.setEnabledTraining(enabledTraining);
	}

	@Override
	public String toString() {
		return this.solutionTechnique.getStepName();
	}

	public SolutionTechnique getTechnique() {
		return this.solutionTechnique;
	}

	public void setTechnique(SolutionTechnique solutionTechnique) {
		this.solutionTechnique = solutionTechnique;
	}

	public int getLevel() {
		return this.level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getBaseScore() {
		return this.baseScore;
	}

	public void setBaseScore(int baseScore) {
		this.baseScore = baseScore;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public SolutionCategory getCategory() {
		return this.solutionCategory;
	}

	public void setCategory(SolutionCategory category) {
		this.solutionCategory = category;
	}

	public String getCategoryName() {
		return this.solutionCategory.getCategoryName();
	}

	public int getIndex() {
		return this.stepNumberInSolution;
	}

	public void setIndex(int index) {
		this.stepNumberInSolution = index;
	}

	@Override
	public int compareTo(SolutionTechniqueConfiguration other) {
		return this.stepNumberInSolution - other.getIndex();
	}

	public boolean isAllStepsEnabled() {
		return this.allStepsEnabled;
	}

	public void setAllStepsEnabled(boolean allStepsEnabled) {
		this.allStepsEnabled = allStepsEnabled;
	}

	public int getIndexProgress() {
		return this.indexProgress;
	}

	public void setIndexProgress(int indexProgress) {
		this.indexProgress = indexProgress;
	}

	public boolean isEnabledProgress() {
		return this.enabledProgress;
	}

	public void setEnabledProgress(boolean enabledProgress) {
		this.enabledProgress = enabledProgress;
	}

	/**
	 * @return the enabledTraining
	 */
	public boolean isEnabledTraining() {
		return this.enabledTraining;
	}

	/**
	 * @param enabledTraining the enabledTraining to set
	 */
	public void setEnabledTraining(boolean enabledTraining) {
		this.enabledTraining = enabledTraining;
	}
}
