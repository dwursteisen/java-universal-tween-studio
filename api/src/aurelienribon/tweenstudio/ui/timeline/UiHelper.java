package aurelienribon.tweenstudio.ui.timeline;

import aurelienribon.tweenstudio.ui.timeline.TimelineModel.Element;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
class UiHelper {
	public static int getLinesCount(TimelineModel model) {
		return _getLinesCount(model.getRoot())-1;
	}

	private static int _getLinesCount(Element elem) {
		int cnt = 1;
		for (Element child : elem.getChildren())
			cnt += _getLinesCount(child);
		return cnt;
	}
}
