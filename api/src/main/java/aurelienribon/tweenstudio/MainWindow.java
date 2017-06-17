package aurelienribon.tweenstudio;

import aurelienribon.tweenengine.*;
import aurelienribon.tweenstudio.Property.Field;
import aurelienribon.tweenstudio.TweenStudio.AnimationDef;
import aurelienribon.tweenstudio.ui.timeline.Theme;
import aurelienribon.tweenstudio.ui.timeline.TimelineHelper;
import aurelienribon.tweenstudio.ui.timeline.TimelineModel;
import aurelienribon.tweenstudio.ui.timeline.TimelineModel.Element;
import aurelienribon.tweenstudio.ui.timeline.TimelineModel.Node;
import aurelienribon.tweenstudio.ui.timeline.TimelinePanel;
import aurelienribon.tweenstudio.ui.timeline.TimelinePanel.Listener;
import aurelienribon.utils.io.FileUtils;
import aurelienribon.utils.swing.SpinnerNullableFloatEditor;
import aurelienribon.utils.swing.SpinnerNullableFloatModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static aurelienribon.tweenstudio.Editor.MAX_COMBINED_TWEENS;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com
 */
class MainWindow extends javax.swing.JFrame {
    // -------------------------------------------------------------------------
    // Attributes
    // -------------------------------------------------------------------------

    private final Theme theme = new Theme();
    private final Map<Object, InitialState> initialStatesMap = new HashMap<Object, InitialState>();
    private final float[] buffer = new float[MAX_COMBINED_TWEENS];
    private final Callback callback;

    private AnimationDef animationDef;
    private Timeline workingTimeline;
    private int playTime;
    private int playDuration;

    // -------------------------------------------------------------------------
    // Ctor
    // -------------------------------------------------------------------------

    public MainWindow(final Callback callback) {
        this.callback = callback;

        // add component to swing panel
        initComponents();
        // init data model
        end();

        timelinePanel.setTheme(theme);
        timelinePanel.addListener(timelinePanelListener);
        easingCbox.addItemListener(easingCboxItemListener);
        reloadBtn.addActionListener(reloadBtnActionListener);
        saveBtn.addActionListener(saveBtnActionListener);
        nextBtn.addActionListener(nextBtnActionListener);
    }

    // -------------------------------------------------------------------------
    // Callback
    // -------------------------------------------------------------------------

    public interface Callback {
        public void next();
    }

    // -------------------------------------------------------------------------
    // Listeners
    // -------------------------------------------------------------------------

    private final ItemListener easingCboxItemListener = new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent e) {
            String name = (String) easingCbox.getSelectedItem();
            TweenEquation equation = TweenUtils.parseEasing(name);
            if (equation != null) {
                for (Node node : timelinePanel.getSelectedNodes()) {
                    NodeData nodeData = (NodeData) node.getUserData();
                    nodeData.setEquation(equation);
                }
                recreateTimeline();
            }
        }
    };

    private final ActionListener reloadBtnActionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            Timeline timeline = TimelineCreationHelper.buildTimelineFromDummy(
                    TweenStudio.getTimelinesMap().get(animationDef.name),
                    animationDef.targets,
                    animationDef.targetsNamesMap);

            AnimationDef anim = new AnimationDef(
                    animationDef.name,
                    animationDef.file,
                    timeline,
                    animationDef.editor,
                    animationDef.callback,
                    animationDef.targets,
                    animationDef.targetsNamesMap);

            initialize(anim);
        }
    };

    private final ActionListener saveBtnActionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                String str = ImportExportHelper.timelineToString(workingTimeline, animationDef.targetsNamesMap);
                FileUtils.writeStringToFile(str, animationDef.file);
                TweenStudio.preloadAnimation(animationDef.file, animationDef.name);

                JOptionPane.showMessageDialog(MainWindow.this, "Save successfully done.");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(MainWindow.this, "Can't save to registered file.\n\n" + ex.getMessage());
            }
        }
    };

    private final ActionListener nextBtnActionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            TimelineCreationHelper.copy(workingTimeline, animationDef.timeline);
            end();
            animationDef = null;
            callback.next();
        }
    };

    private final TimelinePanel.Listener timelinePanelListener = new Listener() {
        @Override
        public void playRequested() {
            playDuration = (int) workingTimeline.getFullDuration() * 1000;
            playTime = timelinePanel.getCurrentTime();
            timelinePanel.setPlaying(true);
        }

        @Override
        public void pauseRequested() {
            timelinePanel.setPlaying(false);
        }

        @Override
        public void currentTimeChanged(int newTime, int oldTime) {
            workingTimeline.update((newTime - oldTime) / 1000);
            if (objectCard.isVisible()) updateObjectCard();
        }

        @Override
        public void selectedElementsChanged(List<Element> newElems, List<Element> oldElems) {
            CardLayout cl = (CardLayout) propertiesPanel.getLayout();
            if (!newElems.isEmpty()) {
                cl.show(propertiesPanel, "objectCard");
                buildObjectCard();
                updateObjectCard();

                List<Object> selectedObjects = new ArrayList<Object>();
                for (Element elem : newElems) {
                    ElementData elemData = (ElementData) elem.getUserData();
                    selectedObjects.add(elemData.getTarget());
                }
                animationDef.editor.selectedObjectsChanged(selectedObjects);
            } else {
                cl.show(propertiesPanel, "nothingCard");
                animationDef.editor.selectedObjectsChanged(new ArrayList<Object>());
            }
        }

        @Override
        public void mouseOverElementChanged(Element newElem, Element oldElem) {
            if (newElem != null) {
                ElementData elemData = (ElementData) newElem.getUserData();
                animationDef.editor.mouseOverObjectChanged(elemData.getTarget());
            } else {
                animationDef.editor.mouseOverObjectChanged(null);
            }
        }

        @Override
        public void selectedNodesChanged(List<Node> newNodes, List<Node> oldNodes) {
            CardLayout cl = (CardLayout) propertiesPanel.getLayout();
            if (!newNodes.isEmpty()) {
                cl.show(propertiesPanel, "tweenCard");
                buildTweenCard();
                updateTweenCard();
            } else {
                cl.show(propertiesPanel, "nothingCard");
            }
        }
    };

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    public void initialize(AnimationDef animationDef) {
        this.animationDef = animationDef;
        animationNameField.setText(animationDef.name);
        begin();

        createInitialStates();
        TimelineModel model = createModel();
        ImportExportStudioHelper.timelineToModel(animationDef.timeline, model, animationDef.targetsNamesMap, animationDef.editor);
        timelinePanel.setModel(model);
        recreateTimeline();

        model.addListener(new TimelineModel.Listener() {
            @Override
            public void stateChanged() {
                if (objectCard.isVisible()) updateObjectCard();
                else if (tweenCard.isVisible()) updateTweenCard();
            }
        });
    }

    public void update(int deltaMillis) {
        if (animationDef == null) return;
        if (timelinePanel.isPlaying()) {
            playTime += deltaMillis * 1000;
            if (playTime <= playDuration) {
                timelinePanel.setCurrentTime(playTime);
            } else {
                timelinePanel.setCurrentTime(playDuration);
                timelinePanel.setPlaying(false);
            }
        }
    }

    public void selectedObjectsChanged(List objs) {
        List<Element> elems = new ArrayList<Element>();

        for (Element elem : timelinePanel.getModel().getRoot().getChildren()) {
            ElementData elemData = (ElementData) elem.getUserData();
            if (objs.contains(elemData.getTarget()))
                elems.add(elem);
        }

        if (elems.isEmpty()) timelinePanel.clearSelectedElements();
        else timelinePanel.pushSelectedElements(TimelinePanel.PushBehavior.SET, elems.toArray(new Element[0]));
    }

    public void mouseOverObjectChanged(Object obj) {
        for (Element elem : timelinePanel.getModel().getRoot().getChildren()) {
            ElementData elemData = (ElementData) elem.getUserData();
            if (elemData.getTarget() == obj) {
                timelinePanel.setMouseOverElement(elem);
                return;
            }
        }

        timelinePanel.setMouseOverElement(null);
    }

    public void statesChanged(List<State> changedStates) {
        for (State state : changedStates) {
            TweenAccessor accessor = Tween.getRegisteredAccessor(state.targetClass);
            String targetName = animationDef.targetsNamesMap.get(state.target);
            String propertyName = animationDef.editor.getProperty(state.target, accessor, state.tweenType).name;

            Element elem = timelinePanel.getModel().getElement(targetName + "/" + propertyName);
            Node node = TimelineHelper.getNodeOrCreate(elem, timelinePanel.getCurrentTime());
            NodeData nodeData = (NodeData) node.getUserData();

            nodeData.setTargets(state.targets);
        }

        recreateTimeline();
    }

    // -------------------------------------------------------------------------
    // Helpers -- timeline creation
    // -------------------------------------------------------------------------

    private void recreateTimeline() {
        if (workingTimeline != null) workingTimeline.free();

        workingTimeline = TimelineCreationHelper.createTimelineFromModel(
                timelinePanel.getModel(),
                timelinePanel.getCurrentTime(),
                initialStatesMap);

        if (objectCard.isVisible()) updateObjectCard();
    }

    // -------------------------------------------------------------------------
    // Helpers -- initialization
    // -------------------------------------------------------------------------

    private void createInitialStates() {
        initialStatesMap.clear();
        for (Object target : animationDef.targets) {
            InitialState state = new InitialState(animationDef.editor, target);
            initialStatesMap.put(target, state);
        }
    }

    private TimelineModel createModel() {
        TimelineModel model = new TimelineModel();

        model.addListener(new TimelineModel.Listener() {
            @Override
            public void stateChanged() {
                recreateTimeline();
            }
        });

        for (Object target : animationDef.targets) {
            List<Property> properties = animationDef.editor.getProperties(target);
            Element elem = model.addElement(animationDef.targetsNamesMap.get(target));
            elem.setSelectable(false);
            elem.setUserData(new ElementData(target, null));

            for (Property property : properties) {
                elem = model.addElement(animationDef.targetsNamesMap.get(target) + "/" + property.name);
                elem.setUserData(new ElementData(target, property));
            }
        }

        return model;
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private void begin() {
        timelinePanel.setCurrentTime(0);
        timelinePanel.clearSelectedElements();
        timelinePanel.clearSelectedNodes();
        reloadBtn.setEnabled(true);
        saveBtn.setEnabled(true);
        nextBtn.setEnabled(true);
    }

    private void end() {
        timelinePanel.setCurrentTime(0);
        timelinePanel.clearSelectedElements();
        timelinePanel.clearSelectedNodes();
        timelinePanel.setModel(new TimelineModel());
        animationNameField.setText("<nothing loaded>");
        reloadBtn.setEnabled(false);
        saveBtn.setEnabled(false);
        nextBtn.setEnabled(false);
    }

    private Property getCommonProperty(List<Node> nodes) {
        Property prop = null;

        for (Node node : nodes) {
            ElementData elemData = (ElementData) node.getParent().getUserData();
            if (prop == null) prop = elemData.getProperty();
            if (prop != elemData.getProperty()) return null;
        }

        return prop;
    }

    private TweenEquation getCommonEquation(List<Node> nodes) {
        TweenEquation equation = null;

        for (Node node : nodes) {
            NodeData nodeData = (NodeData) node.getUserData();
            if (equation == null) equation = nodeData.getEquation();
            if (equation != nodeData.getEquation()) return null;
        }

        return equation;
    }

    private Float getCommonTarget(List<Node> nodes, int fieldIdx) {
        Float target = null;

        for (Node node : nodes) {
            NodeData nodeData = (NodeData) node.getUserData();
            if (target == null) target = new Float(nodeData.getTargets()[fieldIdx]);
            if (target.floatValue() != nodeData.getTargets()[fieldIdx]) return null;
        }

        return target;
    }

    private Map<Property, List<Element>> getCommonProperties(List<Element> elems) {
        assert !elems.isEmpty();
        Map<Property, List<Element>> propertiesMap = new LinkedHashMap<Property, List<Element>>();


        List<Element> firstChilds = elems.stream()
                .filter(Objects::nonNull)
                .map(Element::getChildren)
                .findFirst()
                .orElse(Collections.emptyList());

        for (Element propertyElem : firstChilds) {
            ElementData elemData = (ElementData) propertyElem.getUserData();
            propertiesMap.put(elemData.getProperty(), new ArrayList<Element>());
            propertiesMap.get(elemData.getProperty()).add(propertyElem);
        }

        List<Element> childs = elems.stream()
                .filter(Objects::nonNull)
                .flatMap(elt -> elt.getChildren().stream())
                .collect(Collectors.toList());

        for (Element propertyElem : childs) {
            ElementData elemData = (ElementData) propertyElem.getUserData();
            if (propertiesMap.containsKey(elemData.getProperty()))
                propertiesMap.get(elemData.getProperty()).add(propertyElem);
        }

        return propertiesMap;
    }

    private Float[] getCommonValues(Property property, List<Element> propertyElems) {
        float[] buf = new float[property.fields.length];
        Float[] values = null;

        for (Element propertyElem : propertyElems) {
            ElementData elemData = (ElementData) propertyElem.getUserData();
            assert property == elemData.getProperty();

            property.accessor.getValues(elemData.getTarget(), property.tweenType, buf);

            if (values == null) {
                values = new Float[property.fields.length];
                for (int i = 0; i < values.length; i++) values[i] = new Float(buf[i]);
            } else {
                for (int i = 0; i < values.length; i++) {
                    if (values[i] != null && values[i].floatValue() != buf[i])
                        values[i] = null;
                }
            }
        }

        return values;
    }

    // -------------------------------------------------------------------------
    // Cards
    // -------------------------------------------------------------------------

    private void buildTweenCard() {
        List<Node> nodes = timelinePanel.getSelectedNodes();

        tweenPanel.removeAll();

        Property commonProp = getCommonProperty(nodes);
        if (commonProp != null) {
            for (int i = 0; i < commonProp.fields.length; i++) {
                Field field = commonProp.fields[i];

                JLabel label = new JLabel(field.name + ": ");
                label.setForeground(Color.WHITE);
                label.setHorizontalAlignment(JLabel.RIGHT);

                SpinnerNullableFloatModel model = new SpinnerNullableFloatModel(field.min, field.max, field.step);

                JSpinner spinner = new JSpinner(model);
                spinner.setEditor(new SpinnerNullableFloatEditor(model));
                spinner.addChangeListener(new NodesTargetChangeListener(nodes, i));
                spinner.setMinimumSize(new Dimension(70, 20));
                spinner.setPreferredSize(new Dimension(70, 20));
                spinner.setMaximumSize(new Dimension(70, 20));

                JPanel panel = new JPanel(new BorderLayout());
                panel.setBorder(new EmptyBorder(0, 0, 2, 0));
                panel.setOpaque(false);
                panel.add(label, BorderLayout.CENTER);
                panel.add(spinner, BorderLayout.EAST);

                tweenPanel.add(panel);
            }
        }

        tweenPanel.revalidate();
    }

    private void updateTweenCard() {
        List<Node> nodes = timelinePanel.getSelectedNodes();

        TweenEquation commonEquation = getCommonEquation(nodes);
        easingCbox.setEditable(true);
        easingCbox.setSelectedItem(commonEquation != null ? commonEquation.toString() : "---");
        easingCbox.setEditable(false);

        Property commonProp = getCommonProperty(nodes);
        if (commonProp != null) {
            for (int i = 0; i < commonProp.fields.length; i++) {
                JPanel panel = (JPanel) tweenPanel.getComponent(i);
                JSpinner spinner = (JSpinner) panel.getComponent(1);
                NodesTargetChangeListener listener = (NodesTargetChangeListener) spinner.getChangeListeners()[0];

                listener.setEnabled(false);
                spinner.setValue(getCommonTarget(nodes, i));
                listener.setEnabled(true);
            }
        }
    }

    private void buildObjectCard() {
        List<Element> elems = timelinePanel.getSelectedElements();

        List<String> name = elems.stream()
                .filter(Objects::nonNull)
                .map(Element::getName)
                .collect(Collectors.toList());

        objectField.setText(name.size() == 1 ? elems.get(0).getName() : "<" + elems.size() + " objects>");
        objectPanel.removeAll();

        Map<Property, List<Element>> commonPropertiesMap = getCommonProperties(elems);
        for (Property property : commonPropertiesMap.keySet()) {
            for (int i = 0; i < property.fields.length; i++) {
                Field field = property.fields[i];

                JLabel label = new JLabel(field.name + ": ");
                label.setForeground(Color.WHITE);
                label.setHorizontalAlignment(JLabel.RIGHT);

                SpinnerNullableFloatModel model = new SpinnerNullableFloatModel(field.min, field.max, field.step);

                JSpinner spinner = new JSpinner(model);
                spinner.setEditor(new SpinnerNullableFloatEditor(model));
                spinner.addChangeListener(new PropertyTargetChangeListener(commonPropertiesMap.get(property), i));
                spinner.setMinimumSize(new Dimension(70, 20));
                spinner.setPreferredSize(new Dimension(70, 20));
                spinner.setMaximumSize(new Dimension(70, 20));

                JPanel panel = new JPanel(new BorderLayout());
                panel.setBorder(new EmptyBorder(0, 0, 2, 0));
                panel.setOpaque(false);
                panel.add(label, BorderLayout.CENTER);
                panel.add(spinner, BorderLayout.EAST);

                objectPanel.add(panel);
            }
        }

        objectPanel.revalidate();
    }

    private void updateObjectCard() {
        List<Element> elems = timelinePanel.getSelectedElements();
        Map<Property, List<Element>> commonPropertiesMap = getCommonProperties(elems);
        int cnt = 0;

        for (Property property : commonPropertiesMap.keySet()) {
            Float[] commonValues = getCommonValues(property, commonPropertiesMap.get(property));

            for (int i = 0; i < property.fields.length; i++) {
                JPanel panel = (JPanel) objectPanel.getComponent(cnt);
                JSpinner spinner = (JSpinner) panel.getComponent(1);
                ChangeListener[] listeners = spinner.getChangeListeners();
                PropertyTargetChangeListener listener = (PropertyTargetChangeListener) Arrays.stream(listeners)
                        .filter(it -> it instanceof PropertyTargetChangeListener)
                        .findFirst()
                        .get();
                listener.setEnabled(false);
                spinner.setValue(commonValues[i]);
                listener.setEnabled(true);
                cnt += 1;
            }
        }
    }

    // -------------------------------------------------------------------------
    // Spinner elements
    // -------------------------------------------------------------------------

    private class NodesTargetChangeListener implements ChangeListener {
        private final List<Node> nodes;
        private final int fieldIdx;
        private boolean isEnabled = true;

        public NodesTargetChangeListener(List<Node> nodes, int fieldIdx) {
            this.nodes = nodes;
            this.fieldIdx = fieldIdx;
        }

        public void setEnabled(boolean isEnabled) {
            this.isEnabled = isEnabled;
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            if (!isEnabled) return;

            JSpinner spinner = (JSpinner) e.getSource();
            float value = ((Number) spinner.getValue()).floatValue();

            for (Node node : nodes) {
                NodeData nodeData = (NodeData) node.getUserData();
                nodeData.getTargets()[fieldIdx] = value;
            }

            recreateTimeline();
        }
    }

    private class PropertyTargetChangeListener implements ChangeListener {
        private final List<Element> propertyElems;
        private final int fieldIdx;
        private boolean isEnabled = true;

        public PropertyTargetChangeListener(List<Element> propertyElems, int fieldIdx) {
            this.propertyElems = propertyElems;
            this.fieldIdx = fieldIdx;
        }

        public void setEnabled(boolean isEnabled) {
            this.isEnabled = isEnabled;
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            if (!isEnabled) return;

            JSpinner spinner = (JSpinner) e.getSource();
            float value = ((Number) spinner.getValue()).floatValue();

            for (Element propertyElem : propertyElems) {
                Node n = TimelineHelper.getNodeOrCreate(propertyElem, timelinePanel.getCurrentTime());
                NodeData nodeData = (NodeData) n.getUserData();
                nodeData.getTargets()[fieldIdx] = value;
            }

            recreateTimeline();
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        timelinePanel = new aurelienribon.tweenstudio.ui.timeline.TimelinePanel();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        animationNameField = new javax.swing.JTextField();
        reloadBtn = new javax.swing.JButton();
        saveBtn = new javax.swing.JButton();
        nextBtn = new javax.swing.JButton();
        propertiesPanel = new javax.swing.JPanel();
        nothingCard = new javax.swing.JPanel();
        tweenCard = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        easingCbox = new javax.swing.JComboBox();
        tweenPanel = new javax.swing.JPanel();
        objectCard = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        objectField = new javax.swing.JTextField();
        objectPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Tween Studio");
        getContentPane().add(timelinePanel, java.awt.BorderLayout.CENTER);

        jPanel1.setBackground(theme.COLOR_GRIDPANEL_BACKGROUND);

        jPanel2.setOpaque(false);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gfx/logo.png"))); // NOI18N

        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("<html> <p align=\"center\">v0.5 - 2012 - Aurelien Ribon<br/><font color=\"#6eccff\">www.aurelienribon.com</font></p>");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                                        .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE))
                                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBackground(theme.COLOR_GRIDPANEL_SECTION);
        jPanel3.setBorder(new aurelienribon.utils.swing.GroupBorder());
        jPanel3.setForeground(new java.awt.Color(255, 255, 255));
        jPanel3.setOpaque(false);

        animationNameField.setEditable(false);
        animationNameField.setText("---");

        reloadBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gfx/ic_reload.png"))); // NOI18N
        reloadBtn.setToolTipText("Reload file");
        reloadBtn.setFocusable(false);
        reloadBtn.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        reloadBtn.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        reloadBtn.setMargin(new java.awt.Insets(2, 3, 2, 3));
        reloadBtn.setOpaque(false);

        saveBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gfx/ic_save.png"))); // NOI18N
        saveBtn.setText("Save");
        saveBtn.setToolTipText("Save to file");
        saveBtn.setFocusable(false);
        saveBtn.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        saveBtn.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        saveBtn.setMargin(new java.awt.Insets(2, 3, 2, 3));
        saveBtn.setOpaque(false);

        nextBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gfx/ic_next.png"))); // NOI18N
        nextBtn.setText("Next");
        nextBtn.setToolTipText("Go to next animation");
        nextBtn.setFocusable(false);
        nextBtn.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        nextBtn.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        nextBtn.setMargin(new java.awt.Insets(2, 3, 2, 3));
        nextBtn.setOpaque(false);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                                .addComponent(reloadBtn)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(saveBtn)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(nextBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 66, Short.MAX_VALUE))
                                        .addComponent(animationNameField, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE))
                                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(animationNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(reloadBtn)
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(nextBtn)
                                                .addComponent(saveBtn)))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        propertiesPanel.setOpaque(false);
        propertiesPanel.setLayout(new java.awt.CardLayout());

        nothingCard.setOpaque(false);

        javax.swing.GroupLayout nothingCardLayout = new javax.swing.GroupLayout(nothingCard);
        nothingCard.setLayout(nothingCardLayout);
        nothingCardLayout.setHorizontalGroup(
                nothingCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 180, Short.MAX_VALUE)
        );
        nothingCardLayout.setVerticalGroup(
                nothingCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 209, Short.MAX_VALUE)
        );

        propertiesPanel.add(nothingCard, "nothingCard");

        tweenCard.setOpaque(false);

        jPanel5.setBackground(theme.COLOR_GRIDPANEL_SECTION);
        aurelienribon.utils.swing.GroupBorder groupBorder1 = new aurelienribon.utils.swing.GroupBorder();
        groupBorder1.setTitle("Tween properties");
        jPanel5.setBorder(groupBorder1);
        jPanel5.setForeground(new java.awt.Color(255, 255, 255));
        jPanel5.setFont(new java.awt.Font("Tahoma", 1, 11));
        jPanel5.setOpaque(false);

        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Easing:");

        easingCbox.setMaximumRowCount(12);
        easingCbox.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Linear.INOUT", "----------", "Quad.IN", "Quad.OUT", "Quad.INOUT", "----------", "Cubic.IN", "Cubic.OUT", "Cubic.INOUT", "----------", "Quart.IN", "Quart.OUT", "Quart.INOUT", "----------", "Quint.IN", "Quint.OUT", "Quint.INOUT", "----------", "Circ.IN", "Circ.OUT", "Circ.INOUT", "----------", "Sine.IN", "Sine.OUT", "Sine.INOUT", "----------", "Expo.IN", "Expo.OUT", "Expo.INOUT", "----------", "Back.IN", "Back.OUT", "Back.INOUT", "----------", "Bounce.IN", "Bounce.OUT", "Bounce.INOUT", "----------", "Elastic.IN", "Elastic.OUT", "Elastic.INOUT"}));

        tweenPanel.setOpaque(false);
        tweenPanel.setLayout(new javax.swing.BoxLayout(tweenPanel, javax.swing.BoxLayout.PAGE_AXIS));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
                jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel5Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(tweenPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                                                .addComponent(jLabel3)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(easingCbox, 0, 121, Short.MAX_VALUE)))
                                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
                jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel5Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel3)
                                        .addComponent(easingCbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tweenPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout tweenCardLayout = new javax.swing.GroupLayout(tweenCard);
        tweenCard.setLayout(tweenCardLayout);
        tweenCardLayout.setHorizontalGroup(
                tweenCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        tweenCardLayout.setVerticalGroup(
                tweenCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(tweenCardLayout.createSequentialGroup()
                                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(161, Short.MAX_VALUE))
        );

        propertiesPanel.add(tweenCard, "tweenCard");

        objectCard.setOpaque(false);

        jPanel6.setBackground(theme.COLOR_GRIDPANEL_SECTION);
        aurelienribon.utils.swing.GroupBorder groupBorder2 = new aurelienribon.utils.swing.GroupBorder();
        groupBorder2.setTitle("Object properties");
        jPanel6.setBorder(groupBorder2);
        jPanel6.setForeground(new java.awt.Color(255, 255, 255));
        jPanel6.setFont(new java.awt.Font("Tahoma", 1, 11));
        jPanel6.setOpaque(false);

        objectField.setEditable(false);
        objectField.setText("---");

        objectPanel.setOpaque(false);
        objectPanel.setLayout(new javax.swing.BoxLayout(objectPanel, javax.swing.BoxLayout.PAGE_AXIS));

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
                jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel6Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(objectField, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
                                        .addComponent(objectPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE))
                                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
                jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel6Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(objectField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(objectPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 29, Short.MAX_VALUE)
                                .addContainerGap())
        );

        javax.swing.GroupLayout objectCardLayout = new javax.swing.GroupLayout(objectCard);
        objectCard.setLayout(objectCardLayout);
        objectCardLayout.setHorizontalGroup(
                objectCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        objectCardLayout.setVerticalGroup(
                objectCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(objectCardLayout.createSequentialGroup()
                                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(132, Short.MAX_VALUE))
        );

        propertiesPanel.add(objectCard, "objectCard");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(propertiesPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(propertiesPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 209, Short.MAX_VALUE)
                                .addContainerGap())
        );

        getContentPane().add(jPanel1, java.awt.BorderLayout.EAST);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField animationNameField;
    private javax.swing.JComboBox easingCbox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JButton nextBtn;
    private javax.swing.JPanel nothingCard;
    private javax.swing.JPanel objectCard;
    private javax.swing.JTextField objectField;
    private javax.swing.JPanel objectPanel;
    private javax.swing.JPanel propertiesPanel;
    private javax.swing.JButton reloadBtn;
    private javax.swing.JButton saveBtn;
    private aurelienribon.tweenstudio.ui.timeline.TimelinePanel timelinePanel;
    private javax.swing.JPanel tweenCard;
    private javax.swing.JPanel tweenPanel;
    // End of variables declaration//GEN-END:variables
}
