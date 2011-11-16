package aurelienribon.tweenstudio;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com
 */
public class Property {
	private final int id;
	private final String name;
	private int combinedTweensCount = -1;

	public Property(int tweenType, String name) {
		this.id = tweenType;
		this.name = name;
	}

	public int getTweenType() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getCombinedTweensCount() {
		return combinedTweensCount;
	}

	public void setCombinedTweensCount(int combinedTweensCount) {
		this.combinedTweensCount = combinedTweensCount;
	}
}
