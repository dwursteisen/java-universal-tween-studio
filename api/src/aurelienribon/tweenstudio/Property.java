package aurelienribon.tweenstudio;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com
 */
public class Property {
	private final int id;
	private final String name;
	private int combinedTweensCount = -1;

	public Property(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public int getId() {
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
