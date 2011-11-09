package aurelienribon.tweenstudio.ui.timeline;

import aurelienribon.tweenstudio.ui.timeline.TimelineModel.Element;
import java.awt.BorderLayout;
import javax.swing.JFrame;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class TimelinePanel extends javax.swing.JPanel {
	public static void main(String[] args) {
		TimelineModel model = new TimelineModel();
		model.addElement("Sprite 1/Position");
		model.addElement("Sprite 1/Rotation");
		model.addElement("Sprite 1/Opacity");
		model.addElement("Sprite 2/Position");
		model.addElement("Sprite 2/Rotation");
		model.addElement("Sprite 2/Opacity");
		model.addElement("Sprite 3/Position");
		model.addElement("Sprite 3/Rotation");
		model.addElement("Sprite 3/Opacity");

		model.getElement("Sprite 1").setSelectable(false);
		model.getElement("Sprite 2").setSelectable(false);
		model.getElement("Sprite 3").setSelectable(false);
		model.getElement("Sprite 1/Position").addNode(600, 1000);
		model.getElement("Sprite 2/Rotation").addNode(200, 800);
		model.getElement("Sprite 2/Rotation").addNode(700, 500);

		TimelinePanel panel = new TimelinePanel();
		panel.setModel(model);

		JFrame frame = new JFrame();
		frame.setLayout(new BorderLayout());
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		frame.setSize(1000, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

    public TimelinePanel() {
        initComponents();

		gridPanel.addListener(new GridPanel.EventListener() {
			@Override public void timeCursorMoved(int newTime) {
				menuBarPanel.setTime(newTime);
			}

			@Override public void selectedElementChanged(Element selectedElement) {
				namesPanel.setSelectedElement(selectedElement);
			}
		});

		menuBarPanel.addListener(new MenuBarPanel.EventListener() {
			@Override public void addNodeRequested() {
				gridPanel.requestAddNode();
			}

			@Override public void delNodeRequested() {
				gridPanel.requestDelNode();
			}
		});

		namesPanel.addListener(new NamesPanel.EventListener() {
			@Override public void selectedElementChanged(Element selectedElem) {
				gridPanel.setSelectedElement(selectedElem);
			}
		});
    }

	public void setModel(TimelineModel model) {
		gridPanel.setModel(model);
		namesPanel.setModel(model);
	}
	
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        menuBarPanel = new aurelienribon.tweenstudio.ui.timeline.MenuBarPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel2 = new javax.swing.JPanel();
        namesPanel = new aurelienribon.tweenstudio.ui.timeline.NamesPanel();
        jPanel1 = new javax.swing.JPanel();
        gridPanel = new aurelienribon.tweenstudio.ui.timeline.GridPanel();

        setLayout(new java.awt.BorderLayout());

        menuBarPanel.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.COLOR_SEPARATOR));
        add(menuBarPanel, java.awt.BorderLayout.PAGE_START);

        jSplitPane1.setBorder(null);
        jSplitPane1.setDividerLocation(200);
        jSplitPane1.setDividerSize(3);
        jSplitPane1.setContinuousLayout(true);

        jPanel2.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout namesPanelLayout = new javax.swing.GroupLayout(namesPanel);
        namesPanel.setLayout(namesPanelLayout);
        namesPanelLayout.setHorizontalGroup(
            namesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 200, Short.MAX_VALUE)
        );
        namesPanelLayout.setVerticalGroup(
            namesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 437, Short.MAX_VALUE)
        );

        jPanel2.add(namesPanel, java.awt.BorderLayout.CENTER);

        jSplitPane1.setLeftComponent(jPanel2);

        jPanel1.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout gridPanelLayout = new javax.swing.GroupLayout(gridPanel);
        gridPanel.setLayout(gridPanelLayout);
        gridPanelLayout.setHorizontalGroup(
            gridPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 418, Short.MAX_VALUE)
        );
        gridPanelLayout.setVerticalGroup(
            gridPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 437, Short.MAX_VALUE)
        );

        jPanel1.add(gridPanel, java.awt.BorderLayout.CENTER);

        jSplitPane1.setRightComponent(jPanel1);

        add(jSplitPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private aurelienribon.tweenstudio.ui.timeline.GridPanel gridPanel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JSplitPane jSplitPane1;
    private aurelienribon.tweenstudio.ui.timeline.MenuBarPanel menuBarPanel;
    private aurelienribon.tweenstudio.ui.timeline.NamesPanel namesPanel;
    // End of variables declaration//GEN-END:variables
}
