package sudoku.view.util;

import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import sudoku.core.ModelController;

/**
 * This class provides methods to allow the resizing and dragging of an
 * undecorated window.
 */
public class WindowHelper {

	public static void addResizeAndDragListener(final Stage stage, final Region applicationView) {
		final ResizeListener resizeListener = new ResizeListener(stage);
		applicationView.addEventHandler(MouseEvent.MOUSE_MOVED, resizeListener);
		applicationView.addEventHandler(MouseEvent.MOUSE_PRESSED, resizeListener);
		applicationView.addEventHandler(MouseEvent.MOUSE_DRAGGED, resizeListener);
		applicationView.addEventHandler(MouseEvent.MOUSE_EXITED, resizeListener);
		applicationView.addEventHandler(MouseEvent.MOUSE_EXITED_TARGET, resizeListener);
	}

	private static class ResizeListener implements EventHandler<MouseEvent> {

		private static final int RIGHT_MENU_BUTTON_TO_EDGE_OF_WINDOW_SPACING = 10;

		private static final int LEFT_MENU_BUTTON_TO_EDGE_OF_WINDOW_SPACING = 8;

		private static final double MENU_BUTTON_WIDTH = 65;

		private final Stage stage;

		private Cursor cursorEvent = Cursor.DEFAULT;

		private static final int BORDER = 24;

		private double xOffset;

		private double yOffset;

		private double startX;

		private double startY;

		public ResizeListener(final Stage stage) {
			this.stage = stage;
			this.yOffset = 0;
			this.xOffset = 0;
			this.startX = 0;
			this.startY = 0;
		}

		@Override
		public void handle(final MouseEvent mouseEvent) {
			final EventType<? extends MouseEvent> mouseEventType = mouseEvent.getEventType();
			final Scene scene = this.stage.getScene();

			final double mouseEventX = mouseEvent.getSceneX();
			final double mouseEventY = mouseEvent.getSceneY();
			final double sceneWidth = scene.getWidth();
			final double sceneHeight = scene.getHeight();

			// If the user tries to drag the stage while maximized, the view should
			// now be considered "restored". The sizes are reset, but the location is
			// not.
			if (this.draggingTitleBarWhileMaximized(mouseEventType) || this.resizingWhileMaximized(mouseEventType)) {
				ModelController.getInstance().transitionToSoftRestoredState();
			}

			if (MouseEvent.MOUSE_MOVED.equals(mouseEventType)) {
				this.updateCursorEvent(mouseEventX, mouseEventY, sceneWidth, sceneHeight);
				scene.setCursor(this.cursorEvent);
			} else if (MouseEvent.MOUSE_EXITED.equals(mouseEventType)
					|| MouseEvent.MOUSE_EXITED_TARGET.equals(mouseEventType)) {
				scene.setCursor(Cursor.DEFAULT);
			} else if (MouseEvent.MOUSE_PRESSED.equals(mouseEventType)) {
				this.startX = this.stage.getWidth() - mouseEventX;
				this.startY = this.stage.getHeight() - mouseEventY;
				this.xOffset = mouseEvent.getSceneX();
				this.yOffset = mouseEvent.getSceneY();
			} else if (MouseEvent.MOUSE_DRAGGED.equals(mouseEventType) && !Cursor.DEFAULT.equals(this.cursorEvent)) {
				this.handleMouseDraggedEvent(mouseEvent, mouseEventX, mouseEventY);
			} else if (MouseEvent.MOUSE_DRAGGED.equals(mouseEventType) && Cursor.DEFAULT.equals(this.cursorEvent)
					&& !this.cursorOnMenuButtons()) {
				this.stage.setX(mouseEvent.getScreenX() - this.xOffset);
				this.stage.setY(mouseEvent.getScreenY() - this.yOffset);
			}
		}

		private boolean draggingTitleBarWhileMaximized(final EventType<? extends MouseEvent> mouseEventType) {
			return this.stage.isMaximized() && MouseEvent.MOUSE_DRAGGED.equals(mouseEventType)
					&& Cursor.DEFAULT.equals(this.cursorEvent) && !this.cursorOnMenuButtons();
		}

		private boolean resizingWhileMaximized(final EventType<? extends MouseEvent> mouseEventType) {
			return this.stage.isMaximized() && MouseEvent.MOUSE_DRAGGED.equals(mouseEventType)
					&& !Cursor.DEFAULT.equals(this.cursorEvent) && !Cursor.CLOSED_HAND.equals(this.cursorEvent)
					&& !Cursor.CROSSHAIR.equals(this.cursorEvent);
		}

		/**
		 * Updates the cursor event reference for this class to match the
		 * application state.
		 */
		private void updateCursorEvent(final double mouseEventX, final double mouseEventY, final double sceneWidth,
				final double sceneHeight) {
			if (mouseEventX < BORDER && mouseEventY < BORDER) {
				this.cursorEvent = Cursor.NW_RESIZE;
			} else if (mouseEventX < BORDER && mouseEventY > sceneHeight - BORDER) {
				this.cursorEvent = Cursor.SW_RESIZE;
			} else if (mouseEventX > sceneWidth - BORDER && mouseEventY < BORDER) {
				this.cursorEvent = Cursor.NE_RESIZE;
			} else if (mouseEventX > sceneWidth - BORDER && mouseEventY > sceneHeight - BORDER) {
				this.cursorEvent = Cursor.SE_RESIZE;
			} else if (mouseEventX < BORDER) {
				this.cursorEvent = Cursor.W_RESIZE;
			} else if (mouseEventX > sceneWidth - BORDER) {
				this.cursorEvent = Cursor.E_RESIZE;
			} else if (mouseEventY < BORDER) {
				this.cursorEvent = Cursor.N_RESIZE;
			} else if (mouseEventY > sceneHeight - BORDER) {
				this.cursorEvent = Cursor.S_RESIZE;
			} else {
				this.cursorEvent = Cursor.DEFAULT;
			}
		}

		/** Handles a mouse drag event by the user. */
		private void handleMouseDraggedEvent(final MouseEvent mouseEvent, final double mouseEventX,
				final double mouseEventY) {
			if (!Cursor.W_RESIZE.equals(this.cursorEvent) && !Cursor.E_RESIZE.equals(this.cursorEvent)) {
				this.updateVerticalComponent(mouseEvent, mouseEventY);
			}
			if (!Cursor.N_RESIZE.equals(this.cursorEvent) && !Cursor.S_RESIZE.equals(this.cursorEvent)) {
				this.updateHorizontalComponent(mouseEvent, mouseEventX);
			}
		}

		/** Handles the horizontal component of the event. */
		private void updateHorizontalComponent(final MouseEvent mouseEvent, final double mouseEventX) {
			final double minWidth = this.stage.getMinWidth() > (BORDER * 2) ? this.stage.getMinWidth() : (BORDER * 2);
			if (Cursor.NW_RESIZE.equals(this.cursorEvent) || Cursor.W_RESIZE.equals(this.cursorEvent)
					|| Cursor.SW_RESIZE.equals(this.cursorEvent)) {
				if (this.stage.getWidth() > minWidth || mouseEventX < 0) {
					this.stage.setWidth(this.stage.getX() - mouseEvent.getScreenX() + this.stage.getWidth());
					this.stage.setX(mouseEvent.getScreenX());
				}
			} else {
				if (this.stage.getWidth() > minWidth || mouseEventX + this.startX - this.stage.getWidth() > 0) {
					this.stage.setWidth(mouseEventX + this.startX);
				}
			}
		}

		/** Handles the vertical component of the event. */
		private void updateVerticalComponent(final MouseEvent mouseEvent, final double mouseEventY) {
			final double minHeight = this.stage.getMinHeight() > (BORDER * 2) ? this.stage.getMinHeight() : (BORDER * 2);
			if (Cursor.NW_RESIZE.equals(this.cursorEvent) || Cursor.N_RESIZE.equals(this.cursorEvent)
					|| Cursor.NE_RESIZE.equals(this.cursorEvent)) {
				if (this.stage.getHeight() > minHeight || mouseEventY < 0) {
					this.stage.setHeight(this.stage.getY() - mouseEvent.getScreenY() + this.stage.getHeight());
					this.stage.setY(mouseEvent.getScreenY());
				}
			} else {
				if (this.stage.getHeight() > minHeight || mouseEventY + this.startY - this.stage.getHeight() > 0) {
					this.stage.setHeight(mouseEventY + this.startY);
				}
			}
		}

		private boolean cursorOnMenuButtons() {
			final Scene scene = this.stage.getScene();
			final double sceneWidth = scene.getWidth();
			return (MENU_BUTTON_WIDTH * 3) - RIGHT_MENU_BUTTON_TO_EDGE_OF_WINDOW_SPACING > this.startX
					|| sceneWidth - MENU_BUTTON_WIDTH - LEFT_MENU_BUTTON_TO_EDGE_OF_WINDOW_SPACING < this.startX;
		}

	}

	private WindowHelper() {
		// Private constructor to prevent instantiation.
	}
}