import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class SocialNetworkPanel extends JPanel {
    private Map<String, Student> students;
    private Map<String, Point> positions;
    private String selectedStudent = null;
    private String hoveredStudent = null;
    private StudentSelectListener listener;

    private float animTick = 0f;
    private Timer animTimer;
    private Map<String, Float> pulsePhase = new HashMap<>();

    public interface StudentSelectListener {
        void onStudentSelected(String name);
    }

    private static final Color BG_TOP = new Color(8, 8, 20);
    private static final Color BG_BOT = new Color(14, 10, 28);
    private static final Color GOLD = new Color(255, 210, 70);
    private static final Color GREEN = new Color(60, 210, 110);
    private static final Color RED = new Color(230, 70, 70);
    private static final Color ORANGE = new Color(230, 140, 50);
    private static final Color BLUE = new Color(90, 150, 255);

    public SocialNetworkPanel(Map<String, Student> students) {
        this.students = students;
        this.positions = new HashMap<>();
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        Random rng = new Random(42);
        for (String name : students.keySet()) {
            pulsePhase.put(name, rng.nextFloat() * 6.28f);
        }

        addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { 
                handleClick(e.getX(), e.getY()); 
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override public void mouseMoved(MouseEvent e) { 
                handleHover(e.getX(), e.getY()); 
            }
        });

        animTimer = new Timer(30, e -> { animTick += 0.025f; repaint(); });
        animTimer.start();
    }

    public void setStudentSelectListener(StudentSelectListener l) { 
        this.listener = l; 
    }

    public void setSelectedStudent(String name) { 
        this.selectedStudent = name; repaint(); 
    }

    private void handleHover(int mx, int my) {
        String prev = hoveredStudent;
        hoveredStudent = null;
        for (Map.Entry<String, Point> entry : positions.entrySet()) {
            Point p = entry.getValue();
            if (dist(mx, my, p.x, p.y) <= 30) { hoveredStudent = entry.getKey(); break; }
        }
        if (!Objects.equals(prev, hoveredStudent)) repaint();
    }

    private void handleClick(int mx, int my) {
        for (Map.Entry<String, Point> entry : positions.entrySet()) {
            Point p = entry.getValue();
            if (dist(mx, my, p.x, p.y) <= 30) {
                selectedStudent = entry.getKey();
                if (listener != null) listener.onStudentSelected(selectedStudent);
                repaint();
                return;
            }
        }
        selectedStudent = null;
        if (listener != null) listener.onStudentSelected(null);
        repaint();
    }

    private double dist(int ax, int ay, int bx, int by) {
        return Math.sqrt(Math.pow(ax - bx, 2) + Math.pow(ay - by, 2));
    }

    private void calculateNodePositions() {
        if (students == null || students.isEmpty()) return;
        int w = getWidth(), h = getHeight();
        if (w == 0 || h == 0) return;

        int cx = w / 2, cy = h / 2;
        int radius = (int)(Math.min(w, h) * 0.33);
        int n = students.size(), i = 0;

        for (String name : students.keySet()) {
            if (!positions.containsKey(name)) {
                double angle = 2 * Math.PI * i / n - Math.PI / 2;
                positions.put(name, new Point(cx + (int)(radius * Math.cos(angle)), cy + (int)(radius * Math.sin(angle))));
            }
            i++;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        GradientPaint bgGrad = new GradientPaint(0, 0, BG_TOP, getWidth(), getHeight(), BG_BOT);
        g2.setPaint(bgGrad);
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.setColor(new Color(40, 38, 65, 60));
        for (int x = 30; x < getWidth(); x += 38) {
            for (int y = 30; y < getHeight(); y += 38) {
                g2.fillOval(x - 1, y - 1, 2, 2);
            }
        }

        calculateNodePositions();

        Set<String> drawnEdges = new HashSet<>();
        for (Student student : students.values()) {
            Point p1 = positions.get(student.getName());
            if (p1 == null) 
                continue;

            for (String friendName : student.getFriendNames()) {
                String edgeKey = student.getName().compareTo(friendName) < 0 ? student.getName() + "|" + friendName: friendName + "|" + student.getName();
                if (drawnEdges.contains(edgeKey)) continue;
                drawnEdges.add(edgeKey);

                Point p2 = positions.get(friendName);
                if (p2 == null) 
                    continue;

                double weight = student.getFriendshipWeight(friendName);
                boolean isHighlighted = student.getName().equals(selectedStudent) || friendName.equals(selectedStudent) || student.getName().equals(hoveredStudent) || friendName.equals(hoveredStudent);

                Color edgeColor = edgeColorFor(weight);
                float thick = (float)(weight / 4.0) + 0.6f;

                if (isHighlighted) {
                    g2.setColor(new Color(edgeColor.getRed(), edgeColor.getGreen(), edgeColor.getBlue(), 40));
                    g2.setStroke(new BasicStroke(thick + 6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawLine(p1.x, p1.y, p2.x, p2.y);
                    g2.setColor(new Color(edgeColor.getRed(), edgeColor.getGreen(), edgeColor.getBlue(), 180));
                    g2.setStroke(new BasicStroke(thick + 1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                } else {
                    g2.setColor(new Color(edgeColor.getRed(), edgeColor.getGreen(), edgeColor.getBlue(), 130));
                    g2.setStroke(new BasicStroke(thick, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                }
                g2.drawLine(p1.x, p1.y, p2.x, p2.y);

                int mx = (p1.x + p2.x) / 2;
                int my = (p1.y + p2.y) / 2;
                String wLabel = String.format("%.0f", weight);
                g2.setFont(new Font("SansSerif", Font.BOLD, 9));
                FontMetrics fm = g2.getFontMetrics();
                int bw = fm.stringWidth(wLabel) + 6, bh = 13;
                g2.setColor(new Color(14, 12, 28, 200));
                g2.fillRoundRect(mx - bw/2, my - bh/2, bw, bh, 6, 6);
                g2.setColor(new Color(edgeColor.getRed(), edgeColor.getGreen(), edgeColor.getBlue(), 220));
                g2.drawString(wLabel, mx - fm.stringWidth(wLabel)/2, my + 4);
            }
        }

        int nodeR = 28;
        for (Student student : students.values()) {
            Point p = positions.get(student.getName());
            if (p == null) continue;

            boolean isSelected = student.getName().equals(selectedStudent);
            boolean isHovered = student.getName().equals(hoveredStudent);

            double h = student.getHappiness();
            float phase = pulsePhase.getOrDefault(student.getName(), 0f);
            float pulse = 0.5f + 0.5f * (float)Math.sin(animTick + phase);

            Color nodeColor = nodeColorFor(h);

            if (isSelected) {
                int glowR = nodeR + 12 + (int)(pulse * 5);
                g2.setColor(new Color(GOLD.getRed(), GOLD.getGreen(), GOLD.getBlue(), 40));
                g2.fillOval(p.x - glowR, p.y - glowR, glowR*2, glowR*2);
                g2.setColor(new Color(GOLD.getRed(), GOLD.getGreen(), GOLD.getBlue(), 80));
                g2.setStroke(new BasicStroke(2f));
                g2.drawOval(p.x - glowR, p.y - glowR, glowR*2, glowR*2);
            } else if (isHovered) {
                int glowR = nodeR + 8;
                g2.setColor(new Color(150, 170, 255, 35));
                g2.fillOval(p.x - glowR, p.y - glowR, glowR*2, glowR*2);
            }

            g2.setColor(new Color(0, 0, 0, 100));
            g2.fillOval(p.x - nodeR + 3, p.y - nodeR + 4, nodeR*2, nodeR*2);

            GradientPaint nodeGrad = new GradientPaint(
                p.x - nodeR, p.y - nodeR, nodeColor.brighter(),
                p.x + nodeR, p.y + nodeR, nodeColor.darker()
            );
            g2.setPaint(nodeGrad);
            g2.fillOval(p.x - nodeR, p.y - nodeR, nodeR*2, nodeR*2);

            GradientPaint shine = new GradientPaint(p.x - nodeR/2, p.y - nodeR, new Color(255,255,255, 60), p.x - nodeR/2, p.y, new Color(255,255,255, 0));
            g2.setPaint(shine);
            g2.fillOval(p.x - nodeR/2, p.y - nodeR, nodeR, nodeR);

            g2.setPaint(null);
            Color ringColor = isSelected ? GOLD : isHovered ? new Color(180, 200, 255) : new Color(nodeColor.getRed() + 40, nodeColor.getGreen() + 40, nodeColor.getBlue() + 40);
            g2.setColor(ringColor);
            g2.setStroke(new BasicStroke(isSelected ? 2.5f : 1.5f));
            g2.drawOval(p.x - nodeR, p.y - nodeR, nodeR*2, nodeR*2);

            float happyArc = (float)(h * 360);
            g2.setColor(new Color(GREEN.getRed(), GREEN.getGreen(), GREEN.getBlue(), 180));
            g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawArc(p.x - nodeR - 4, p.y - nodeR - 4, (nodeR+4)*2, (nodeR+4)*2, 90, -(int)happyArc);

            String firstName = student.getName().split(" ")[0];
            g2.setFont(new Font("SansSerif", Font.BOLD, 11));
            FontMetrics fm = g2.getFontMetrics();
            g2.setColor(Color.WHITE);
            g2.drawString(firstName, p.x - fm.stringWidth(firstName)/2, p.y + 4);

            String trustBadge = String.format("T%.0f", student.getTrustFactor() * 100);
            g2.setFont(new Font("SansSerif", Font.BOLD, 8));
            fm = g2.getFontMetrics();
            int bx = p.x + nodeR - 2, by = p.y - nodeR + 2;
            g2.setColor(new Color(20, 20, 40, 210));
            g2.fillRoundRect(bx - fm.stringWidth(trustBadge) - 2, by - 9, fm.stringWidth(trustBadge) + 4, 11, 4, 4);
            g2.setColor(new Color(180, 200, 255));
            g2.drawString(trustBadge, bx - fm.stringWidth(trustBadge), by);

            g2.setFont(new Font("SansSerif", Font.BOLD, 11));
            fm = g2.getFontMetrics();
            int lx = p.x - fm.stringWidth(student.getName())/2;
            int ly = p.y + nodeR + 15;
            g2.setColor(new Color(10, 8, 24, 200));
            g2.fillRoundRect(lx - 4, ly - 12, fm.stringWidth(student.getName()) + 8, 15, 6, 6);
            g2.setColor(Color.WHITE);
            g2.drawString(student.getName(), lx, ly);

            String hpct = String.format("H: %.0f%%", h * 100);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
            fm = g2.getFontMetrics();
            g2.setColor(new Color(nodeColor.getRed() + 60, nodeColor.getGreen() + 60, nodeColor.getBlue() + 60));
            g2.drawString(hpct, p.x - fm.stringWidth(hpct)/2, ly + 12);
        }

        drawLegend(g2);
        drawTitle(g2);
    }

    private Color edgeColorFor(double w) {
        if (w >= 8) return GREEN;
        if (w >= 6) return new Color(100, 200, 130);
        if (w >= 4) return BLUE;
        if (w >= 2) return ORANGE;
        return RED;
    }

    private Color nodeColorFor(double h) {
        if (h >= 0.75) return new Color(50, 170, 90);
        if (h >= 0.5)  return new Color(60, 110, 190);
        if (h >= 0.25) return new Color(180, 110, 40);
        return new Color(170, 50, 50);
    }

    private void drawLegend(Graphics2D g2) {
        int lx = 14, ly = getHeight() - 110;
        g2.setColor(new Color(14, 12, 28, 210));
        g2.fillRoundRect(lx - 6, ly - 16, 170, 106, 10, 10);
        g2.setColor(new Color(60, 55, 100));
        g2.setStroke(new BasicStroke(1f));
        g2.drawRoundRect(lx - 6, ly - 16, 170, 106, 10, 10);

        g2.setFont(new Font("SansSerif", Font.BOLD, 10));
        g2.setColor(GOLD);
        g2.drawString("Bond Strength", lx, ly - 2);

        Object[][] rows = {{"Strong  8–10", GREEN}, {"Good    6–7",  new Color(100, 200, 130)}, {"Neutral 4–5",  BLUE}, {"Weak    2–3",  ORANGE}, {"Enemy   0–1",  RED}};
        g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
        int dy = 14;
        for (Object[] row : rows) {
            Color c = (Color) row[1];
            g2.setColor(c);
            g2.fillRoundRect(lx, ly + dy - 8, 16, 8, 3, 3);
            g2.setColor(new Color(190, 200, 220));
            g2.drawString((String) row[0], lx + 22, ly + dy);
            dy += 15;
        }

        g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
        g2.setColor(new Color(100, 110, 140));
        g2.drawString("Node color = happiness", lx, ly + dy + 2);
    }

    private void drawTitle(Graphics2D g2) {
        g2.setFont(new Font("SansSerif", Font.BOLD, 13));
        g2.setColor(new Color(80, 75, 130, 160));
        g2.drawString("SOCIAL NETWORK", 14, 22);
    }

    public void refreshData(Map<String, Student> students) {
        this.students = students;
        repaint();
    }

    public void stopAnimation() {
        if (animTimer != null) animTimer.stop();
    }
}