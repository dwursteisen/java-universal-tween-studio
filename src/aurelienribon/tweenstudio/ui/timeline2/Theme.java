package aurelienribon.tweenstudio.ui.timeline2;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;

class Theme {
    public static final Color COLOR_BACKGROUND = new Color(51, 51, 51);
	public static final Color COLOR_FOREGROUND = new Color(180, 180, 180);

    public static final Color COLOR_GRID_BACKGROUND = new Color(40, 40, 40);
    public static final Color COLOR_GRID_LINES_BACKGROUND = new Color(67, 67, 67);
    public static final Color COLOR_GRID_NODES_BACKGROUND = new Color(120, 120, 120);
    public static final Color COLOR_GRID_NODES_FILL = new Color(230, 230, 230);


    public static final Color COLOR_HIGHLIGHT = new Color(100, 100, 100);
    public static final Color COLOR_HIGHLIGHT_ALT = new Color(80, 80, 80);

	public static final Color COLOR_CURSOR = new Color(255, 238, 0);

	public static final Font FONT = new Font("Tahoma", Font.PLAIN, 12);

	public static final Stroke STROKE_SMALL = new BasicStroke(1);
	public static final Stroke STROKE_LARGE = new BasicStroke(2);
}
