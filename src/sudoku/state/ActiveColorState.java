package sudoku.state;

public class ActiveColorState extends ApplicationModelState {

	private final boolean increment;

	public ActiveColorState(final boolean increment, final ApplicationModelState lastState) {
		super(lastState, false);
		this.increment = increment;
	}

	@Override
	public void onEnter() {

	}
}
