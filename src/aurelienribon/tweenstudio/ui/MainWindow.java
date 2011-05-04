package aurelienribon.tweenstudio.ui;

import aurelienribon.tweenstudio.elements.TweenStudioObjectState;
import aurelienribon.libgdx.tween.Tween;
import aurelienribon.libgdx.tween.TweenEquation;
import aurelienribon.libgdx.tween.TweenSequence;
import aurelienribon.libgdx.tween.equations.Cubic;
import aurelienribon.tweenstudio.ExportWindow;
import aurelienribon.tweenstudio.TweenStudio;
import aurelienribon.tweenstudio.TweenStudioObject;
import aurelienribon.tweenstudio.TweenStudioObjectState;
import java.awt.Component;
import java.awt.Container;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.DefaultListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

class MainWindow extends javax.swing.JFrame {
	private final TweenStudio studio;
	private DefaultListModel objectListModel;
	private TimelineTableModel timelineTableModel;
	private ExportWindow exportWindow;
	private TweenSequence playSequence;

    public MainWindow(final TweenStudio studio) {
		Locale.setDefault(Locale.ENGLISH);
        initComponents();

		objectListModel = new DefaultListModel();
		timelineTableModel = new TimelineTableModel();

		new_objects_list.setModel(objectListModel);
		tl_timeline_table.setModel(timelineTableModel);

		tl_timeline_table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				int index = tl_timeline_table.getSelectedRow();
				if (index < 0 || timelineTableModel.getRowCount() == 0) {
					resetObjects();
					enableComponent(editionPanel, false);
				} else {
					enableComponent(editionPanel, true);
					Tween tween = timelineTableModel.get(index);
					updateSelectionPanel(tween);
					updateObjects(index);
				}
			}
		});

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (exportWindow != null)
					exportWindow.dispose();
				studio.close();
			}
		});
		
		this.studio = studio;
		setObjects(studio.getObjectNames());
		setTimeline(studio.getCorrectedTimeline());
		tl_timeline_table.clearSelection();
    }

	public TweenStudioObject getSelectionObject() {
		int index = tl_timeline_table.getSelectedRow();
		if (index < 0)
			return null;
		return (TweenStudioObject) timelineTableModel.get(index).getTarget();
	}

	public int getSelectionTweenType() {
		int index = tl_timeline_table.getSelectedRow();
		if (index < 0)
			return -1;
		return timelineTableModel.get(index).getTweenType();
	}

	public void updateSelectionValues(float[] newValues) {
		int index = tl_timeline_table.getSelectedRow();
		assert index >= 0;
		sel_targetValue1_nud.setValue(newValues[0]);
		sel_targetValue2_nud.setValue(newValues[1]);
		sel_targetValue3_nud.setValue(newValues[2]);
	}

	// -------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        new_actiongroup_btnGrp = new javax.swing.ButtonGroup();
        editionPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        sel_target_field = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        sel_equation_cbox = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        sel_duration_nud = new javax.swing.JSpinner();
        jLabel4 = new javax.swing.JLabel();
        sel_delay_nud = new javax.swing.JSpinner();
        sel_delete_btn = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        sel_action_field = new javax.swing.JTextField();
        sel_update_btn = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        sel_targetValue1_nud = new javax.swing.JSpinner();
        sel_targetValue2_nud = new javax.swing.JSpinner();
        sel_targetValue3_nud = new javax.swing.JSpinner();
        jTextPane1 = new javax.swing.JTextPane();
        creationPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        new_objects_list = new javax.swing.JList();
        jLabel5 = new javax.swing.JLabel();
        new_actions_panel = new javax.swing.JPanel();
        new_actionOrigin_chk = new javax.swing.JRadioButton();
        new_actionPosition_chk = new javax.swing.JRadioButton();
        new_actionRotation_chk = new javax.swing.JRadioButton();
        new_actionScale_chk = new javax.swing.JRadioButton();
        new_actionOpacity_chk = new javax.swing.JRadioButton();
        jLabel9 = new javax.swing.JLabel();
        new_create_btn = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        tl_playAll_btn = new javax.swing.JButton();
        tl_timelineHolder_scroll = new javax.swing.JScrollPane();
        tl_timeline_table = new javax.swing.JTable();
        tl_playFromSelection_btn = new javax.swing.JButton();
        tl_clearSelection_btn = new javax.swing.JButton();
        tl_export_btn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Tween Studio");

        editionPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Selected tween"));

        jLabel1.setText("Target");

        sel_target_field.setEditable(false);

        jLabel2.setText("Equation");

        sel_equation_cbox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Cubic.IN", "Cubic.OUT", "Cubic.INOUT" }));

        jLabel3.setText("Duration");

        sel_duration_nud.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(0), Integer.valueOf(0), null, Integer.valueOf(100)));

        jLabel4.setText("Delay");

        sel_delay_nud.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(0), null, null, Integer.valueOf(100)));

        sel_delete_btn.setText("Delete tween");
        sel_delete_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sel_delete_btnActionPerformed(evt);
            }
        });

        jLabel6.setText("Target values");

        jLabel7.setText("Action");

        sel_action_field.setEditable(false);

        sel_update_btn.setText("Update tween");
        sel_update_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sel_update_btnActionPerformed(evt);
            }
        });

        jPanel1.setLayout(new java.awt.GridLayout(1, 3, 5, 0));

        sel_targetValue1_nud.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), null, null, Float.valueOf(0.1f)));
        jPanel1.add(sel_targetValue1_nud);

        sel_targetValue2_nud.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), null, null, Float.valueOf(0.1f)));
        jPanel1.add(sel_targetValue2_nud);

        sel_targetValue3_nud.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), null, null, Float.valueOf(0.1f)));
        jPanel1.add(sel_targetValue3_nud);

        jTextPane1.setEditable(false);
        jTextPane1.setForeground(new java.awt.Color(102, 102, 102));
        jTextPane1.setText("In order to change the target value(s), either set them in the above fields, or just click on your render area to see how it affects these values.");
        jTextPane1.setFocusable(false);
        jTextPane1.setOpaque(false);

        javax.swing.GroupLayout editionPanelLayout = new javax.swing.GroupLayout(editionPanel);
        editionPanel.setLayout(editionPanelLayout);
        editionPanelLayout.setHorizontalGroup(
            editionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(editionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(editionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel6)
                    .addComponent(jLabel4)
                    .addComponent(jLabel7)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(editionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE)
                    .addComponent(sel_duration_nud, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE)
                    .addComponent(sel_delay_nud, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, editionPanelLayout.createSequentialGroup()
                        .addComponent(sel_update_btn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sel_delete_btn))
                    .addComponent(sel_target_field, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE)
                    .addComponent(sel_action_field, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE)
                    .addComponent(sel_equation_cbox, javax.swing.GroupLayout.Alignment.TRAILING, 0, 298, Short.MAX_VALUE))
                .addContainerGap())
        );

        editionPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {sel_delete_btn, sel_update_btn});

        editionPanelLayout.setVerticalGroup(
            editionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(editionPanelLayout.createSequentialGroup()
                .addGroup(editionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sel_target_field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(editionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sel_action_field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(editionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sel_equation_cbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(26, 26, 26)
                .addGroup(editionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel6)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(editionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(sel_duration_nud, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(editionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(sel_delay_nud, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jTextPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 34, Short.MAX_VALUE)
                .addGroup(editionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sel_delete_btn)
                    .addComponent(sel_update_btn))
                .addContainerGap())
        );

        creationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Creation"));

        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        new_objects_list.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        new_objects_list.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane2.setViewportView(new_objects_list);

        jLabel5.setText("Tweenable objects");

        new_actions_panel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Actions"));

        new_actiongroup_btnGrp.add(new_actionOrigin_chk);
        new_actionOrigin_chk.setText("Tween origin (*)");

        new_actiongroup_btnGrp.add(new_actionPosition_chk);
        new_actionPosition_chk.setSelected(true);
        new_actionPosition_chk.setText("Tween position");

        new_actiongroup_btnGrp.add(new_actionRotation_chk);
        new_actionRotation_chk.setText("Tween rotation");

        new_actiongroup_btnGrp.add(new_actionScale_chk);
        new_actionScale_chk.setText("Tween scale");

        new_actiongroup_btnGrp.add(new_actionOpacity_chk);
        new_actionOpacity_chk.setText("Tween opacity");

        jLabel9.setText("(*) Expert feature, be careful");

        javax.swing.GroupLayout new_actions_panelLayout = new javax.swing.GroupLayout(new_actions_panel);
        new_actions_panel.setLayout(new_actions_panelLayout);
        new_actions_panelLayout.setHorizontalGroup(
            new_actions_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(new_actions_panelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(new_actions_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(new_actions_panelLayout.createSequentialGroup()
                        .addGroup(new_actions_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(new_actionPosition_chk)
                            .addComponent(new_actionRotation_chk))
                        .addGap(18, 18, 18)
                        .addGroup(new_actions_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(new_actionOpacity_chk)
                            .addComponent(new_actionOrigin_chk)))
                    .addComponent(new_actionScale_chk)
                    .addComponent(jLabel9))
                .addContainerGap(29, Short.MAX_VALUE))
        );
        new_actions_panelLayout.setVerticalGroup(
            new_actions_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, new_actions_panelLayout.createSequentialGroup()
                .addGroup(new_actions_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(new_actionPosition_chk)
                    .addComponent(new_actionOpacity_chk))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(new_actions_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(new_actionRotation_chk)
                    .addComponent(new_actionOrigin_chk))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(new_actionScale_chk)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 13, Short.MAX_VALUE)
                .addComponent(jLabel9)
                .addContainerGap())
        );

        new_create_btn.setText("Insert new tween");
        new_create_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                new_create_btnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout creationPanelLayout = new javax.swing.GroupLayout(creationPanel);
        creationPanel.setLayout(creationPanelLayout);
        creationPanelLayout.setHorizontalGroup(
            creationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, creationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(creationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(new_actions_panel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 269, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(new_create_btn))
                .addContainerGap())
        );
        creationPanelLayout.setVerticalGroup(
            creationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(creationPanelLayout.createSequentialGroup()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(new_actions_panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(new_create_btn)
                .addContainerGap())
        );

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/aurelienribon/tweenstudio/gfx/title.png"))); // NOI18N

        tl_playAll_btn.setText("Play all");
        tl_playAll_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tl_playAll_btnActionPerformed(evt);
            }
        });

        tl_timelineHolder_scroll.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        tl_timelineHolder_scroll.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        tl_timeline_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tl_timeline_table.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        tl_timeline_table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tl_timelineHolder_scroll.setViewportView(tl_timeline_table);

        tl_playFromSelection_btn.setText("Play from selection");
        tl_playFromSelection_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tl_playFromSelection_btnActionPerformed(evt);
            }
        });

        tl_clearSelection_btn.setText("Clear selection");
        tl_clearSelection_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tl_clearSelection_btnActionPerformed(evt);
            }
        });

        tl_export_btn.setText("Export !");
        tl_export_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tl_export_btnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(tl_timelineHolder_scroll, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 703, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(creationPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(editionPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 123, Short.MAX_VALUE)
                        .addComponent(tl_clearSelection_btn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tl_playFromSelection_btn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tl_playAll_btn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tl_export_btn)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel8)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tl_playAll_btn)
                        .addComponent(tl_playFromSelection_btn)
                        .addComponent(tl_clearSelection_btn)
                        .addComponent(tl_export_btn)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tl_timelineHolder_scroll, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(creationPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(editionPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

	private void new_create_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_new_create_btnActionPerformed
		String targetName = new_objects_list.getSelectedValue().toString();
		TweenStudioObject target = studio.getObjectFromName(targetName);
		TweenStudioObjectState state = TweenStudioObjectState.fromObject(target);

		// Get the current state of the tweened object (to add a neutral tween)
		int tweenType = getCreationSelectedTweenType();
		float[] targetValues = new float[3];
		target.getTweenValues(tweenType, targetValues);

		// Build the tween
		Tween tween = Tween.to(target, tweenType, Cubic.INOUT, 500, targetValues[0], targetValues[1], targetValues[2]);

		// Add the tween to the table model
		int index = tl_timeline_table.getSelectedRow();
		timelineTableModel.add(tween, index + 1);
		tl_timeline_table.setRowSelectionInterval(index + 1, index + 1);

		// Scroll to the new table row
		Rectangle rect = tl_timeline_table.getCellRect(index + 1, 0, true);
		tl_timeline_table.scrollRectToVisible(rect);
	}//GEN-LAST:event_new_create_btnActionPerformed

	private void sel_delete_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sel_delete_btnActionPerformed
		int index = tl_timeline_table.getSelectedRow();
		timelineTableModel.remove(index);
		if (timelineTableModel.getRowCount() > 0)
			tl_timeline_table.setRowSelectionInterval(
				Math.min(index, timelineTableModel.getRowCount()-1),
				Math.min(index, timelineTableModel.getRowCount()-1));
		else
			tl_timeline_table.clearSelection();
	}//GEN-LAST:event_sel_delete_btnActionPerformed

	private void tl_clearSelection_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tl_clearSelection_btnActionPerformed
		tl_timeline_table.clearSelection();
	}//GEN-LAST:event_tl_clearSelection_btnActionPerformed

	private void sel_update_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sel_update_btnActionPerformed
		int index = tl_timeline_table.getSelectedRow();
		Tween oldTween = timelineTableModel.get(index);
		Tween newTween = Tween.to(
			oldTween.getTarget(),
			oldTween.getTweenType(),
			parseEquation((String)sel_equation_cbox.getSelectedItem()),
			((Number)sel_duration_nud.getValue()).intValue(),
			((Number)sel_targetValue1_nud.getValue()).floatValue(),
			((Number)sel_targetValue2_nud.getValue()).floatValue(),
			((Number)sel_targetValue3_nud.getValue()).floatValue()
		).delay(((Number)sel_delay_nud.getValue()).intValue());

		timelineTableModel.replace(newTween, index);
		tl_timeline_table.setRowSelectionInterval(index, index);
	}//GEN-LAST:event_sel_update_btnActionPerformed

	private void tl_playAll_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tl_playAll_btnActionPerformed
		resetObjects();
		Tween[] tweens = cloneTweens(timelineTableModel.getAll());

		if (playSequence != null)
			playSequence.kill();
		playSequence = TweenSequence.set(tweens);
		playSequence.start();
	}//GEN-LAST:event_tl_playAll_btnActionPerformed

	private void tl_playFromSelection_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tl_playFromSelection_btnActionPerformed
		int index = tl_timeline_table.getSelectedRow();
		if (index > 0) {
			updateObjects(index - 1);
		} else {
			resetObjects();
		}
		Tween[] tweens = cloneTweens(timelineTableModel.getfrom(index));
		TweenSequence.set(tweens).start();
	}//GEN-LAST:event_tl_playFromSelection_btnActionPerformed

	private void tl_export_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tl_export_btnActionPerformed
		TweenSequence sequence = TweenSequence.set(cloneTweens(timelineTableModel.getAll()));
		if (exportWindow != null)
			exportWindow.dispose();
		exportWindow = new ExportWindow();
		exportWindow.setSequence(sequence, studio);
		exportWindow.setVisible(true);
	}//GEN-LAST:event_tl_export_btnActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel creationPanel;
    private javax.swing.JPanel editionPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextPane jTextPane1;
    private javax.swing.JRadioButton new_actionOpacity_chk;
    private javax.swing.JRadioButton new_actionOrigin_chk;
    private javax.swing.JRadioButton new_actionPosition_chk;
    private javax.swing.JRadioButton new_actionRotation_chk;
    private javax.swing.JRadioButton new_actionScale_chk;
    private javax.swing.ButtonGroup new_actiongroup_btnGrp;
    private javax.swing.JPanel new_actions_panel;
    private javax.swing.JButton new_create_btn;
    private javax.swing.JList new_objects_list;
    private javax.swing.JTextField sel_action_field;
    private javax.swing.JSpinner sel_delay_nud;
    private javax.swing.JButton sel_delete_btn;
    private javax.swing.JSpinner sel_duration_nud;
    private javax.swing.JComboBox sel_equation_cbox;
    private javax.swing.JSpinner sel_targetValue1_nud;
    private javax.swing.JSpinner sel_targetValue2_nud;
    private javax.swing.JSpinner sel_targetValue3_nud;
    private javax.swing.JTextField sel_target_field;
    private javax.swing.JButton sel_update_btn;
    private javax.swing.JButton tl_clearSelection_btn;
    private javax.swing.JButton tl_export_btn;
    private javax.swing.JButton tl_playAll_btn;
    private javax.swing.JButton tl_playFromSelection_btn;
    private javax.swing.JScrollPane tl_timelineHolder_scroll;
    private javax.swing.JTable tl_timeline_table;
    // End of variables declaration//GEN-END:variables

	// -------------------------------------------------------------------------
	// Utils
	// -------------------------------------------------------------------------

	private void enableComponent(Container root, boolean enable) {
		root.setEnabled(enable);
		Component children[] = root.getComponents();
		for(int i = 0; i < children.length; i++) {
			if(children[i] instanceof Container)
				enableComponent((Container)children[i], enable);
		}
	}

	private void setObjects(String[] sprites) {
		objectListModel.clear();

		for (String name : sprites)
			objectListModel.addElement(name);

		if (sprites.length == 0) {
			enableComponent(new_create_btn, false);
		} else {
			new_objects_list.setSelectedIndex(0);
		}
	}

	private void setTimeline(Tween[] tweens) {
		timelineTableModel.clear();

		if (tweens.length == 0) {
			enableComponent(editionPanel, false);
		} else {
			for (int i=0; i<tweens.length; i++)
				timelineTableModel.add(tweens[i], i);
			tl_timeline_table.setRowSelectionInterval(tweens.length-1, tweens.length-1);
		}
	}

	private void updateSelectionPanel(Tween tween) {
		sel_target_field.setText(studio.getNameFromObject((TweenStudioObject) tween.getTarget()));
		sel_action_field.setText(TweenStudioObject.getTweenTypeDesc(tween.getTweenType()));
		sel_equation_cbox.setSelectedItem(tween.getEquation() != null ? tween.getEquation().toString() : "???");
		sel_duration_nud.setValue(tween.getDurationMillis());
		sel_delay_nud.setValue(tween.getDelayMillis());

		switch (tween.getCombinedTweenCount()) {
			case 1:
				enableComponent(sel_targetValue1_nud, true);
				enableComponent(sel_targetValue2_nud, false);
				enableComponent(sel_targetValue3_nud, false);
				sel_targetValue1_nud.setValue(tween.getTargetValues()[0]);
				sel_targetValue2_nud.setValue(0);
				sel_targetValue3_nud.setValue(0);
				break;

			case 2:
				enableComponent(sel_targetValue1_nud, true);
				enableComponent(sel_targetValue2_nud, true);
				enableComponent(sel_targetValue3_nud, false);
				sel_targetValue1_nud.setValue(tween.getTargetValues()[0]);
				sel_targetValue2_nud.setValue(tween.getTargetValues()[1]);
				sel_targetValue3_nud.setValue(0);
				break;

			case 3:
				enableComponent(sel_targetValue1_nud, true);
				enableComponent(sel_targetValue2_nud, true);
				enableComponent(sel_targetValue3_nud, true);
				sel_targetValue1_nud.setValue(tween.getTargetValues()[0]);
				sel_targetValue2_nud.setValue(tween.getTargetValues()[1]);
				sel_targetValue3_nud.setValue(tween.getTargetValues()[2]);
				break;

			default: assert false; break;
		}
	}

	private int getCreationSelectedTweenType() {
		if (new_actionOpacity_chk.isSelected())
			return TweenStudioObject.OPACITY;
		if (new_actionOrigin_chk.isSelected())
			return TweenStudioObject.ORIGIN_XY;
		if (new_actionPosition_chk.isSelected())
			return TweenStudioObject.POSITION_XY;
		if (new_actionRotation_chk.isSelected())
			return TweenStudioObject.ROTATION;
		if (new_actionScale_chk.isSelected())
			return TweenStudioObject.SCALE_XY;
		return -1;
	}

	private void updateObjects(int tweenIndex) {
		resetObjects();
		for (int i=0; i<=tweenIndex; i++) {
			Tween tween = timelineTableModel.get(i);
			tween.getTarget().tweenUpdated(tween.getTweenType(), tween.getTargetValues());
		}
	}

	private void resetObjects() {
		for (TweenStudioObject tso : studio.getObjects()) {
			TweenStudioObjectState state = studio.getInitState(tso);
			state.applyToObject(tso);
		}
	}

	private TweenEquation parseEquation(String str) {
		TweenEquation[] eqs = {
			Cubic.IN, Cubic.OUT, Cubic.INOUT
		};
		for (TweenEquation eq : eqs)
			if (eq.toString().equals(str))
				return eq;
		return null;
	}

	private Tween[] cloneTweens(Tween[] tweens) {
		Tween[] copies = new Tween[tweens.length];
		for (int i=0; i<tweens.length; i++)
			copies[i] = tweens[i].copy();
		return copies;
	}

	// -------------------------------------------------------------------------
	// Timeline Table Model
	// -------------------------------------------------------------------------

	private class TimelineTableModel extends AbstractTableModel {
		private final List<Tween> tweens = new ArrayList<Tween>();

		@Override
		public int getRowCount() {
			return tweens.size();
		}

		@Override
		public int getColumnCount() {
			return 6;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			Tween tween = tweens.get(rowIndex);
			String targetValuesStr = "";
			switch (tween.getCombinedTweenCount()) {
				case 1: targetValuesStr = String.format("%.1f", tween.getTargetValues()[0]); break;
				case 2: targetValuesStr = String.format("%.1f / %.1f", tween.getTargetValues()[1], tween.getTargetValues()[0]); break;
				case 3: targetValuesStr = String.format("%.1f / %.1f / %.1f", tween.getTargetValues()[2], tween.getTargetValues()[1], tween.getTargetValues()[0]); break;
			}

			switch (columnIndex) {
				case 0: return studio.getNameFromObject((TweenStudioObject) tween.getTarget());
				case 1: return TweenStudioObject.getTweenTypeDesc(tween.getTweenType());
				case 2: return tween.getEquation() != null ? tween.getEquation().toString() : "???";
				case 3: return targetValuesStr;
				case 4: return tween.getDurationMillis() + "";
				case 5: return tween.getDelayMillis() + "";
				default: return "???";
			}
		}

		@Override
		public String getColumnName(int column) {
			switch (column) {
				case 0: return "Target";
				case 1: return "Action";
				case 2: return "Equation";
				case 3: return "Target values";
				case 4: return "Duration (ms)";
				case 5: return "Delay (ms)";
				default: return "???";
			}
		}

		public void clear() {
			tweens.clear();
		}

		public void add(Tween tween, int index) {
			tweens.add(index, tween);
			fireTableDataChanged();
		}

		public void remove(int index) {
			tweens.remove(index);
			fireTableDataChanged();
		}

		public void replace(Tween tween, int index) {
			tweens.remove(index);
			tweens.add(index, tween);
			fireTableDataChanged();
		}

		public Tween get(int index) {
			return tweens.get(index);
		}

		public Tween[] getAll() {
			return tweens.toArray(new Tween[0]);
		}

		public Tween[] getfrom(int index) {
			Tween[] selection = new Tween[tweens.size() - index];
			for (int i=0; i<selection.length; i++)
				selection[i] = tweens.get(i);
			return selection;
		}
	}
}
