package aurelienribon.tweenstudio;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com
 */
public class Property {
	private final int id;
	private final String name;
	private int attrsCnt = -1;

	public Property(int tweenType, String name) {
		this.id = tweenType;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getAttributesCount() {
		return attrsCnt;
	}

	public void setAttributesCount(int count) {
		this.attrsCnt = count;
	}
}
