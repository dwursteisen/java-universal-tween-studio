package aurelienribon.tweenstudio.ui.timeline;

import java.awt.Color;
import java.awt.Font;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class Theme {
	public final Font FONT = new Font("Tahoma", Font.PLAIN, 12);

	public Color COLOR_FOREGROUND;
	public Color COLOR_SEPARATOR;
		
	public Color COLOR_MENUBAR_BACKGROUND;

	public Color COLOR_NAMESPANEL_BACKGROUND;
	public Color COLOR_NAMESPANEL_SECTION;
	public Color COLOR_NAMESPANEL_SECTION_SELECTED;
	public Color COLOR_NAMESPANEL_SECTION_MOUSEOVER;
	public Color COLOR_NAMESPANEL_SECTION_UNUSABLE;

	public Color COLOR_GRIDPANEL_BACKGROUND;
	public Color COLOR_GRIDPANEL_SECTION;
	public Color COLOR_GRIDPANEL_SECTION_UNUSABLE;
	public Color COLOR_GRIDPANEL_TIMELINE;
    public Color COLOR_GRIDPANEL_NODE_TRACK;
    public Color COLOR_GRIDPANEL_NODE_FILL;
    public Color COLOR_GRIDPANEL_NODE_STROKE;
    public Color COLOR_GRIDPANEL_NODE_TRACK_SELECTED;
    public Color COLOR_GRIDPANEL_NODE_FILL_SELECTED;
    public Color COLOR_GRIDPANEL_NODE_STROKE_SELECTED;
    public Color COLOR_GRIDPANEL_NODE_TRACK_MOUSEOVER;
    public Color COLOR_GRIDPANEL_NODE_FILL_MOUSEOVER;
    public Color COLOR_GRIDPANEL_NODE_STROKE_MOUSEOVER;
	public Color COLOR_GRIDPANEL_CURSOR;

	public Color COLOR_SCROLLBAR_CONTAINER_FILL;
	public Color COLOR_SCROLLBAR_SELECTOR;

	public Theme(String def) {
		load(def);
	}

	private void load(String def) {
		if (def == null) def = "";

		COLOR_FOREGROUND = getColor(def, "COLOR_FOREGROUND", 0xDCDCDC);
		COLOR_SEPARATOR = getColor(def, "COLOR_SEPARATOR", 0xB4B4B4);

		COLOR_MENUBAR_BACKGROUND = getColor(def, "COLOR_MENUBAR_BACKGROUND", 0x323232);

		COLOR_NAMESPANEL_BACKGROUND = getColor(def, "COLOR_NAMESPANEL_BACKGROUND", 0x323232);
		COLOR_NAMESPANEL_SECTION = getColor(def, "COLOR_NAMESPANEL_SECTION", 0x464646);
		COLOR_NAMESPANEL_SECTION_SELECTED = getColor(def, "COLOR_NAMESPANEL_SECTION_SELECTED", 0x646464);
		COLOR_NAMESPANEL_SECTION_MOUSEOVER = getColor(def, "COLOR_NAMESPANEL_SECTION_MOUSEOVER", 0x505050);
		COLOR_NAMESPANEL_SECTION_UNUSABLE = getColor(def, "COLOR_NAMESPANEL_SECTION_UNUSABLE", 0x323232);

		COLOR_GRIDPANEL_BACKGROUND = getColor(def, "COLOR_GRIDPANEL_BACKGROUND", 0x323232);
		COLOR_GRIDPANEL_SECTION = getColor(def, "COLOR_GRIDPANEL_SECTION", 0x505050);
		COLOR_GRIDPANEL_SECTION_UNUSABLE = getColor(def, "COLOR_GRIDPANEL_SECTION_UNUSABLE", 0x323232);
		COLOR_GRIDPANEL_TIMELINE = getColor(def, "COLOR_GRIDPANEL_TIMELINE", 0xB4B4B4);
		COLOR_GRIDPANEL_NODE_TRACK = getColor(def, "COLOR_GRIDPANEL_NODE_TRACK", 0x787878);
		COLOR_GRIDPANEL_NODE_FILL = getColor(def, "COLOR_GRIDPANEL_NODE_FILL", 0xE6E6E6);
		COLOR_GRIDPANEL_NODE_STROKE = getColor(def, "COLOR_GRIDPANEL_NODE_STROKE", 0x323232);
		COLOR_GRIDPANEL_NODE_TRACK_SELECTED = getColor(def, "COLOR_GRIDPANEL_NODE_TRACK_SELECTED", 0xB4B4B4);
		COLOR_GRIDPANEL_NODE_FILL_SELECTED = getColor(def, "COLOR_GRIDPANEL_NODE_FILL_SELECTED", 0xFFFFFF);
		COLOR_GRIDPANEL_NODE_STROKE_SELECTED = getColor(def, "COLOR_GRIDPANEL_NODE_STROKE_SELECTED", 0x646464);
		COLOR_GRIDPANEL_NODE_TRACK_MOUSEOVER = getColor(def, "COLOR_GRIDPANEL_NODE_TRACK_MOUSEOVER", 0x8C8C8C);
		COLOR_GRIDPANEL_NODE_FILL_MOUSEOVER = getColor(def, "COLOR_GRIDPANEL_NODE_FILL_MOUSEOVER", 0xE6E6E6);
		COLOR_GRIDPANEL_NODE_STROKE_MOUSEOVER = getColor(def, "COLOR_GRIDPANEL_NODE_STROKE_MOUSEOVER", 0x646464);
		COLOR_GRIDPANEL_CURSOR = getColor(def, "COLOR_GRIDPANEL_CURSOR", 0xFFEE00);

		COLOR_SCROLLBAR_CONTAINER_FILL = getColor(def, "COLOR_SCROLLBAR_CONTAINER_FILL", 0xC8C8C8);
		COLOR_SCROLLBAR_SELECTOR = getColor(def, "COLOR_SCROLLBAR_SELECTOR", 0x646464);
	}

	private Color getColor(String input, String name, int defaultColor) {
		Matcher m = Pattern.compile(name + "=(.+)\\s*").matcher(input);
		if (m.matches()) return Color.decode(m.group(1));
		return new Color(defaultColor);
	}
}