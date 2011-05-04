package aurelienribon.tweenstudio;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class TweenStudioEditorMap {
	private final Map<TweenStudioObject, MapTuple> map;

	public TweenStudioEditorMap() {
		map = new HashMap<TweenStudioObject, MapTuple>();
	}

	public void put(TweenStudioObject obj, String fieldName, String friendlyName) {
		map.put(obj, new MapTuple(fieldName, friendlyName));
	}

	public String getFieldName(TweenStudioObject obj) {
		assert map.containsKey(obj);
		return map.get(obj).fieldName;
	}

	public String getFriendlyName(TweenStudioObject obj) {
		assert map.containsKey(obj);
		return map.get(obj).friendlyName;
	}

	public TweenStudioObject getObjectFromFieldName(String fieldName) {
		for (Entry<TweenStudioObject, MapTuple> entry : map.entrySet())
			if (entry.getValue().fieldName.equals(fieldName))
				return entry.getKey();
		assert false;
		return null;
	}

	private class MapTuple {
		private String fieldName;
		private String friendlyName;

		public MapTuple(String fieldName, String friendlyName) {
			this.fieldName = fieldName;
			this.friendlyName = friendlyName;
		}

		public String getFieldName() {
			return fieldName;
		}

		public String getFriendlyName() {
			return friendlyName;
		}
	}
}
