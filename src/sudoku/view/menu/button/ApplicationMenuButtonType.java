package sudoku.view.menu.button;

public enum ApplicationMenuButtonType {

	CLOSE(CloseMenuButton.class), MINIMIZE(MinimizeMenuButton.class), MAXIMIZE(MaximizeMenuButton.class), CONTEXT(
			ContextMenuButton.class);

	private Class<?> clazz;

	private ApplicationMenuButtonType(Class<?> clazz) {
		this.clazz = clazz;
	}

	public Class<?> getClassType() {
		return clazz;
	}

}
