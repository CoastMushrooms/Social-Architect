import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

public class RightControlPanel extends JPanel {
    private GameState gameState;
    private SocialNetworkPanel networkPanel;
    private Consumer<String> onGameEnd;

    private JLabel dayLabel, apLabel, happinessLabel, statusLabel, goodwillLabel;
    private JTextArea eventLogArea;
    private JPanel studentInfoPanel;
    private JPanel suggestionPanel;
    private JLabel selectedStudentLabel;
    private JLabel centralLabel, weakBondsLabel;
    private JProgressBar happinessBar;
    private String selectedStudentName = null;
    private boolean endScreenShown = false;

    private static final Color BG = new Color(22, 22, 36);
    private static final Color PANEL_BG = new Color(30, 30, 50);
    private static final Color ACCENT = new Color(100, 160, 255);
    private static final Color GREEN = new Color(80, 200, 120);
    private static final Color RED = new Color(220, 80, 80);
    private static final Color GOLD = new Color(255, 200, 60);
    private static final Color TEXT = new Color(200, 210, 230);
    private static final Color DIM = new Color(120, 130, 150);

    public RightControlPanel(GameState gameState, SocialNetworkPanel networkPanel, Consumer<String> onGameEnd) {
        this.gameState = gameState;
        this.networkPanel = networkPanel;
        this.onGameEnd = onGameEnd;
        setBackground(BG);
        setLayout(new BorderLayout(0, 8));
        setBorder(new MatteBorder(0, 2, 0, 0, new Color(50, 55, 80)));
        setPreferredSize(new Dimension(320, 0));

        add(buildTopBar(),    BorderLayout.NORTH);
        add(buildCenter(),    BorderLayout.CENTER);
        add(buildBottomLog(), BorderLayout.SOUTH);
    }

    private JPanel buildTopBar() {
        JPanel top = new JPanel(new GridLayout(3, 2, 4, 4));
        top.setBackground(PANEL_BG);
        top.setBorder(new CompoundBorder(new MatteBorder(0, 0, 1, 0, new Color(50, 55, 80)), new EmptyBorder(10, 10, 10, 10)));

        dayLabel = makeLabel("Day 1 / Week 1", GOLD,   13, Font.BOLD);
        apLabel = makeLabel("AP: 5",          GREEN,  13, Font.BOLD);
        happinessLabel= makeLabel("Avg Happy: -",   ACCENT, 11, Font.PLAIN);
        statusLabel = makeLabel("Status: -",      TEXT,   11, Font.PLAIN);
        goodwillLabel = makeLabel("Goodwill: 0",    DIM,    11, Font.PLAIN);
        happinessBar = new JProgressBar(0, 100);
        happinessBar.setStringPainted(true);
        happinessBar.setFont(new Font("SansSerif", Font.BOLD, 9));
        happinessBar.setBackground(new Color(20, 20, 36));

        top.add(dayLabel);      
        top.add(apLabel);
        top.add(happinessLabel);
        top.add(statusLabel);
        top.add(goodwillLabel); 
        top.add(happinessBar);
        return top;
    }

    private JScrollPane buildCenter() {
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(BG);
        center.setBorder(new EmptyBorder(8, 8, 8, 8));

        center.add(buildStudentInfoPanel());
        center.add(Box.createVerticalStrut(8));
        center.add(buildSuggestionsPanel());
        center.add(Box.createVerticalStrut(8));
        center.add(buildAnalyticsPanel());
        center.add(Box.createVerticalStrut(8));
        center.add(buildActionsPanel());
        center.add(Box.createVerticalStrut(8));
        center.add(buildEndDayButton());

        JScrollPane scroll = new JScrollPane(center);
        scroll.setBackground(BG);
        scroll.getViewport().setBackground(BG);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        return scroll;
    }

    private JPanel buildStudentInfoPanel() {
        JPanel p = styledPanel("Selected Student");
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        selectedStudentLabel = makeLabel("Click a node to select", DIM, 11, Font.ITALIC);
        selectedStudentLabel.setAlignmentX(LEFT_ALIGNMENT);
        p.add(selectedStudentLabel);

        studentInfoPanel = new JPanel();
        studentInfoPanel.setLayout(new BoxLayout(studentInfoPanel, BoxLayout.Y_AXIS));
        studentInfoPanel.setBackground(PANEL_BG);
        studentInfoPanel.setVisible(false);
        p.add(studentInfoPanel);
        return p;
    }

    private JPanel buildSuggestionsPanel() {
        suggestionPanel = styledPanel("Friendship Suggestions");
        suggestionPanel.setLayout(new BoxLayout(suggestionPanel, BoxLayout.Y_AXIS));
        JLabel placeholder = makeLabel("End a day to see suggestions.", DIM, 10, Font.ITALIC);
        placeholder.setAlignmentX(LEFT_ALIGNMENT);
        suggestionPanel.add(Box.createVerticalStrut(4));
        suggestionPanel.add(placeholder);
        return suggestionPanel;
    }

    private void updateSuggestionsPanel() {
        suggestionPanel.removeAll();
        List<String> suggestions = gameState.getFriendshipSuggestions();
        suggestionPanel.add(Box.createVerticalStrut(4));
        if (suggestions.isEmpty()) {
            JLabel none = makeLabel("No urgent suggestions right now.", DIM, 10, Font.ITALIC);
            none.setAlignmentX(LEFT_ALIGNMENT);
            suggestionPanel.add(none);
        } else {
            for (String s : suggestions) {
                Color c = s.contains("[Dijkstra") ? new Color(120, 180, 255): s.contains("[BFS") ? new Color(100, 220, 160): GOLD;
                JLabel lbl = makeLabel("<html><body style='width:240px'>" + s + "</body></html>", c, 9, Font.PLAIN);
                lbl.setAlignmentX(LEFT_ALIGNMENT);
                lbl.setBorder(BorderFactory.createEmptyBorder(1, 0, 4, 0));
                suggestionPanel.add(lbl);
            }
        }
        suggestionPanel.revalidate();
        suggestionPanel.repaint();
    }

    private JPanel buildAnalyticsPanel() {
        JPanel p = styledPanel("Analytics");
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        centralLabel = makeLabel("Most Connected: -", TEXT, 10, Font.PLAIN);
        weakBondsLabel = makeLabel("Weak Bonds: -",     TEXT, 10, Font.PLAIN);
        centralLabel.setAlignmentX(LEFT_ALIGNMENT);
        weakBondsLabel.setAlignmentX(LEFT_ALIGNMENT);

        p.add(Box.createVerticalStrut(6));
        p.add(centralLabel);
        p.add(Box.createVerticalStrut(3));
        p.add(weakBondsLabel);
        return p;
    }

    private JPanel buildActionsPanel() {
        JPanel p = styledPanel("Actions");
        p.setLayout(new GridLayout(0, 1, 4, 4));
        p.add(makeActionButton("Host Event (2 AP)",       new Color(70, 140, 200), e -> doHostEvent()));
        p.add(makeActionButton("Send Gift (1 AP)",        new Color(70, 160, 100), e -> doSendGift()));
        p.add(makeActionButton("Reinforce Bond (2 AP)",   new Color(100, 120, 200),e -> doReinforce()));
        p.add(makeActionButton("Break Friendship (1 AP)", new Color(180, 100, 100),e -> doBreakFriendship()));
        return p;
    }

    private JPanel buildEndDayButton() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG);
        JButton btn = new JButton("⏭ End Day");
        btn.setBackground(new Color(60, 80, 140));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 12, 10, 12));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { 
                btn.setBackground(new Color(80, 110, 180)); 
            }
            public void mouseExited (MouseEvent e) { 
                btn.setBackground(new Color(60, 80, 140));  
            }
        });
        btn.addActionListener(e -> { 
            gameState.endDay(); refresh(); checkWinCondition(); 
        });
        p.add(btn, BorderLayout.CENTER);
        return p;
    }

    private JPanel buildBottomLog() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(PANEL_BG);
        p.setBorder(new CompoundBorder(new MatteBorder(1, 0, 0, 0, new Color(50, 55, 80)), new EmptyBorder(6, 8, 6, 8)));
        p.setPreferredSize(new Dimension(0, 180));

        p.add(makeLabel("Event Log", GOLD, 11, Font.BOLD), BorderLayout.NORTH);

        eventLogArea = new JTextArea();
        eventLogArea.setEditable(false);
        eventLogArea.setBackground(new Color(18, 18, 28));
        eventLogArea.setForeground(TEXT);
        eventLogArea.setFont(new Font("Monospaced", Font.PLAIN, 10));
        eventLogArea.setWrapStyleWord(true);
        eventLogArea.setLineWrap(true);
        eventLogArea.setBorder(new EmptyBorder(4, 4, 4, 4));

        JScrollPane scroll = new JScrollPane(eventLogArea);
        scroll.setBorder(null);
        scroll.setBackground(new Color(18, 18, 28));
        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    private void doHostEvent() {
        if (selectedStudentName == null) { 
            showMsg("Select a student first!"); 
            return; 
        }
        List<String> others = new ArrayList<>(gameState.getStudents().keySet());
        others.remove(selectedStudentName);
        if (others.isEmpty()) 
            return;
        String b = (String) JOptionPane.showInputDialog(this, "Host event between " + selectedStudentName + " and...", "Host Event", JOptionPane.PLAIN_MESSAGE, null, others.toArray(), others.get(0));
        if (b != null) { 
            gameState.hostEvent(selectedStudentName, b); 
            refresh(); 
        }
    }

    private void doSendGift() {
        if (selectedStudentName == null) { 
            showMsg("Select a student first!"); return; 
        }
        gameState.sendGift(selectedStudentName);
        refresh();
    }

    private void doReinforce() {
        if (selectedStudentName == null) { 
            showMsg("Select a student first!"); 
            return; 
        }
        Student s = gameState.getStudents().get(selectedStudentName);
        List<String> friends = new ArrayList<>(s.getFriendNames());
        if (friends.isEmpty()) { 
            showMsg(selectedStudentName + " has no bonds to reinforce."); 
            return; 
        }
        String b = (String) JOptionPane.showInputDialog(this, "Reinforce " + selectedStudentName + "'s bond with...", "Reinforce Bond", JOptionPane.PLAIN_MESSAGE, null, friends.toArray(), friends.get(0));
        if (b != null) { 
            gameState.reinforceBond(selectedStudentName, b); 
            refresh(); 
        }
    }

    private void doBreakFriendship() {
        if (selectedStudentName == null) { 
            showMsg("Select a student first!"); 
            return; 
        }
        Student s = gameState.getStudents().get(selectedStudentName);
        List<String> friends = new ArrayList<>(s.getFriendNames());
        if (friends.isEmpty()) { 
            showMsg(selectedStudentName + " has no bonds to break."); 
            return; 
        }
        
        String msg = "Break " + selectedStudentName + "'s bond with...\n\n(Warning, this is risky";
        String b = (String) JOptionPane.showInputDialog(this, msg, "Break Friendship", JOptionPane.WARNING_MESSAGE, null, friends.toArray(), friends.get(0));
        if (b != null) { 
            gameState.breakFriendship(selectedStudentName, b); 
            refresh(); 
        }
    }

    public void onStudentSelected(String name) {
        selectedStudentName = name;
        updateStudentInfoPanel();
        networkPanel.setSelectedStudent(name);
    }

    private void updateStudentInfoPanel() {
        studentInfoPanel.removeAll();
        if (selectedStudentName == null) {
            selectedStudentLabel.setText("Click a node");
            selectedStudentLabel.setForeground(DIM);
            studentInfoPanel.setVisible(false);
        } else {
            Student s = gameState.getStudents().get(selectedStudentName);
            if (s == null) 
                return;
            selectedStudentLabel.setText(s.getName());
            selectedStudentLabel.setForeground(GOLD);
            studentInfoPanel.setVisible(true);
            studentInfoPanel.setBackground(PANEL_BG);

            addInfo(String.format("Happiness: %.0f%%", s.getHappiness() * 100), GREEN);
            addInfo(String.format("Trust: %.0f%%",     s.getTrustFactor() * 100), ACCENT);
            addInfo("Friends: " + String.join(", ", s.getFriendNames()), TEXT);
            for (String fn : s.getFriendNames()) {
                double w = s.getFriendshipWeight(fn);
                addInfo(String.format("  → %s: %.1f", fn.split(" ")[0], w), w >= 7 ? GREEN : w <= 2 ? RED : TEXT);
            }
        }
        studentInfoPanel.revalidate();
        studentInfoPanel.repaint();
    }

    private void addInfo(String text, Color color) {
        JLabel l = makeLabel(text, color, 9, Font.PLAIN);
        l.setAlignmentX(LEFT_ALIGNMENT);
        studentInfoPanel.add(l);
    }

    public void refresh() {
        dayLabel.setText("Day " + gameState.getDay() + " / Week " + gameState.getWeek());

        int ap = gameState.getActionPoints();
        apLabel.setText("AP: " + ap);
        apLabel.setForeground(ap >= 3 ? GREEN : ap >= 1 ? GOLD : RED);

        int hPct = (int)(gameState.getAverageHappiness() * 100);
        happinessLabel.setText("Avg Happy: " + hPct + "%");
        happinessBar.setValue(hPct);
        happinessBar.setString(hPct + "%");
        happinessBar.setForeground(hPct >= 70 ? GREEN : hPct >= 40 ? GOLD : RED);

        String status = gameState.getWinStatus();
        statusLabel.setText("Status: " + status);
        statusLabel.setForeground(status.equals("WINNING") ? GREEN : status.equals("PROGRESSING") ? GOLD : RED);

        goodwillLabel.setText("Goodwill: " + gameState.getGoodwillAP());
        centralLabel.setText("Most Connected: " + gameState.getMostCentral());

        int weak = gameState.countWeakBonds();
        weakBondsLabel.setText("Weak Bonds: " + weak);
        weakBondsLabel.setForeground(weak > 2 ? RED : weak > 0 ? GOLD : GREEN);

        updateStudentInfoPanel();
        updateSuggestionsPanel();
        updateEventLog();
        networkPanel.refreshData(gameState.getStudents());
    }

    private void updateEventLog() {
        List<String> log = gameState.getEventLog();
        StringBuilder sb = new StringBuilder();
        int start = Math.max(0, log.size() - 30);
        for (int i = start; i < log.size(); i++) {
            sb.append(log.get(i)).append("\n");
        }
        eventLogArea.setText(sb.toString());
        eventLogArea.setCaretPosition(eventLogArea.getDocument().getLength());
    }

    private void checkWinCondition() {
        if (gameState.isGameOver() && !endScreenShown) {
            endScreenShown = true;
            String result = gameState.getEnding() + "\nFinal Happiness: " + String.format("%.0f%%", gameState.getAverageHappiness() * 100) + "\nWeak bonds remaining: " + gameState.countWeakBonds();
            if (onGameEnd != null) 
                onGameEnd.accept(result);
        }
    }

    private JPanel styledPanel(String title) {
        JPanel p = new JPanel();
        p.setBackground(PANEL_BG);
        p.setAlignmentX(LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        p.setBorder(new CompoundBorder(new LineBorder(new Color(50, 55, 80), 1, true), new TitledBorder(BorderFactory.createEmptyBorder(), title, TitledBorder.LEFT, TitledBorder.TOP, new Font("SansSerif", Font.BOLD, 10), GOLD)));
        return p;
    }

    private JButton makeActionButton(String text, Color bg, ActionListener al) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 11));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(7, 10, 7, 10));
        Color hover = bg.brighter();
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { 
                btn.setBackground(hover); 
            }
            public void mouseExited (MouseEvent e) { 
                btn.setBackground(bg); 
            }
        });
        btn.addActionListener(al);
        return btn;
    }

    private JLabel makeLabel(String text, Color color, int size, int style) {
        JLabel l = new JLabel(text);
        l.setForeground(color);
        l.setFont(new Font("SansSerif", style, size));
        return l;
    }

    private void showMsg(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Notice", JOptionPane.INFORMATION_MESSAGE);
    }
}