package aurelienribon.tweenstudio.ui;

import aurelienribon.tweenstudio.ui.timeline.TimelineModel;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com
 */
public class MainWindow extends javax.swing.JFrame {
	public MainWindow() {
		initComponents();
	}

	public void setTimelineModel(TimelineModel model) {
		timelinePanel.setModel(model);
	}

	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        timelinePanel = new aurelienribon.tweenstudio.ui.timeline.TimelinePanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().add(timelinePanel, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private aurelienribon.tweenstudio.ui.timeline.TimelinePanel timelinePanel;
    // End of variables declaration//GEN-END:variables
}
