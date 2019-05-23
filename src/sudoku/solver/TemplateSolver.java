package sudoku.solver;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import sudoku.Candidate;
import sudoku.SolutionStep;
import sudoku.SolutionType;
import sudoku.Sudoku2;
import sudoku.SudokuSet;

/**
 * Verboten sind alle Templates, die keine 1 an einer der bereits gesetzten
 * Positionen haben: (positions & template) != positions Verboten sind alle
 * Templates, die eine 1 an einer nicht mehr erlaubten Position haben:
 * (~(positions | allowedPositions) & template) != 0 Verboten sind alle
 * Templates, die eine 1 an einer Position eines Templates haben, das aus allen
 * verundeten Templates eines anderen Kandidaten gebildet wurde Verboten sind
 * alle Templates, die keine einzige überlappungsfreie Kombination mit
 * wenigstens einem Template einer anderen Ziffer haben
 *
 * Wenn die Templates bekannt sind: alle Templates OR: Alle Kandidaten, die
 * nicht enthalten sind, können gelöscht werden alle Templates AND: Alle
 * Positionen, die übrig bleiben, können gesetzt werden alle gültigen
 * Kombinationen aus Templates zweier Ziffern bilden (OR), alle Ergebnisse AND:
 * An allen verbliebenen Positionen können alle Kandidaten, die nicht zu einer
 * dieser Ziffern gehören, eliminiert werden.
 *
 * @author hobiwan
 */
public class TemplateSolver extends AbstractSolver {

	private List<SolutionStep> steps; // gefundene Lösungsschritte
	private final SolutionStep globalStep = new SolutionStep(SolutionTechnique.HIDDEN_SINGLE);

	/**
	 * Creates a new instance of TemplateSolver
	 *
	 * @param finder
	 */
	public TemplateSolver(SudokuStepFinder finder) {
		super(finder);
	}

	@Override
	protected SolutionStep getStep(SolutionTechnique type) {
		SolutionStep result = null;
		this.sudoku = this.finder.getSudoku();
		switch (type) {
		case TEMPLATE_SET:
			this.getTemplateSet(true);
			if (this.steps.size() > 0) {
				result = this.steps.get(0);
			}
			break;
		case TEMPLATE_DEL:
			this.getTemplateDel(true);
			if (this.steps.size() > 0) {
				result = this.steps.get(0);
			}
			break;
		}
		return result;
	}

	@Override
	protected boolean doStep(SolutionStep step) {
		boolean handled = true;
		this.sudoku = this.finder.getSudoku();
		switch (step.getType()) {
		case TEMPLATE_SET:
			final int value = step.getValues().get(0);
			for (final int index : step.getIndices()) {
				this.sudoku.setCell(index, value);
			}
			break;
		case TEMPLATE_DEL:
			for (final Candidate cand : step.getCandidatesToDelete()) {
				this.sudoku.delCandidate(cand.getIndex(), cand.getValue());
			}
			break;
		default:
			handled = false;
		}
		return handled;
	}

	protected List<SolutionStep> getAllTemplates() {
		this.sudoku = this.finder.getSudoku();
		final List<SolutionStep> oldSteps = this.steps;
		this.steps = new ArrayList<SolutionStep>();
		long millis1 = System.currentTimeMillis();
		this.getTemplateSet(false);
		this.getTemplateDel(false);
		millis1 = System.currentTimeMillis() - millis1;
		Logger.getLogger(this.getClass().getName()).log(Level.FINE, "getAllTemplates() gesamt: {0}ms", millis1);
		final List<SolutionStep> result = this.steps;
		this.steps = oldSteps;
		return result;
	}

	private void getTemplateSet(boolean initSteps) {
		if (initSteps) {
			this.steps = new ArrayList<SolutionStep>();
		}

		// können Zellen gesetzt werden?
		final SudokuSet setSet = new SudokuSet();
		for (int i = 1; i <= 9; i++) {
			setSet.set(this.finder.getSetValueTemplates(true)[i]);
			setSet.andNot(this.finder.getPositions()[i]);
			if (!setSet.isEmpty()) {
				// Zellen können gesetzt werden
				this.globalStep.reset();
				this.globalStep.setType(SolutionTechnique.TEMPLATE_SET);
				this.globalStep.addValue(i);
				for (int j = 0; j < setSet.size(); j++) {
					this.globalStep.addIndex(setSet.get(j));
				}
				this.steps.add((SolutionStep) this.globalStep.clone());
			}
		}
	}

	private void getTemplateDel(boolean initSteps) {
		if (initSteps) {
			this.steps = new ArrayList<SolutionStep>();
		}

		// können Kandidaten gelöscht werden?
		final SudokuSet setSet = new SudokuSet();
		for (int i = 1; i <= 9; i++) {
			setSet.set(this.finder.getDelCandTemplates(true)[i]);
			setSet.and(this.finder.getCandidates()[i]);
			if (!setSet.isEmpty()) {
				// Kandidaten können gelöscht werden
				this.globalStep.reset();
				this.globalStep.setType(SolutionTechnique.TEMPLATE_DEL);
				this.globalStep.addValue(i);
				for (int j = 0; j < setSet.size(); j++) {
					this.globalStep.addCandidateToDelete(setSet.get(j), i);
				}
				this.steps.add((SolutionStep) this.globalStep.clone());
			}
		}
	}

	public static void main(String[] args) {
		// Sudoku2 sudoku = new Sudoku2(true);
		final Sudoku2 sudoku = new Sudoku2();
		// sudoku.setSudoku(":0361:4:..5.132673268..14917...2835..8..1.262.1.96758.6..283...12....83693184572..723.6..:434
		// 441 442 461 961 464 974:411:r7c39 r6c1b9 fr3c3");
		sudoku.setSudoku(
				":0000:x:7.2.34.8.........2.8..51.74.......51..63.27..29.......14.76..2.8.........2.51.8.7:::");
//        for (int i = 1; i <= 9; i++) {
//            System.out.println("allowedPositions[" + i + "]: " + sudoku.getCandidates()[i]);
//            System.out.println("positions[" + i + "]: " + sudoku.getPositions()[i]);
//        }
		final TemplateSolver ts = new TemplateSolver(null);
		long millis = System.currentTimeMillis();
		for (int i = 0; i < 1; i++) {
			final List<SolutionStep> steps = ts.getAllTemplates();
		}
		millis = System.currentTimeMillis() - millis;
		System.out.println("Zeit: " + (millis / 100) + "ms");
	}
}
