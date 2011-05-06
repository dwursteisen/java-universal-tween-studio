package aurelienribon.tweenstudio.ui.timeline.components;

public interface Scrollable {
	public void requestHorizontalScroll(float speed);
	public void requestVerticalScroll(float position);
	public int getPreferredHeight();
	public int getVerticalOffset();
}
