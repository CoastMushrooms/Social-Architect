import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class StartScreen extends JPanel {

    public interface StartListener { void onStart(); }

    private StartListener listener;
    private float animTick = 0f;
    private Timer animTimer;

    private static final Color BG_TOP = new Color(8, 8, 20);
    private static final Color BG_BOT = new Color(18, 10, 35);
    private static final Color GOLD = new Color(255, 200, 60);
    private static final Color GOLD_DIM = new Color(180, 130, 30);
    private static final Color GREEN = new Color(60, 200, 110);
    private static final Color DIM = new Color(100, 110, 140);
    private static final Color BULLET = new Color(170, 180, 205);
    private static final Color PANEL_BG = new Color(22, 18, 42, 230);

    private static final float[][] NODES = {{0.12f,0.2f}, {0.3f,0.75f}, {0.55f,0.15f}, {0.8f,0.3f}, {0.7f,0.8f}, {0.92f,0.55f}, {0.05f,0.6f}, {0.45f,0.88f}};

    private static final String[] BULLETS = {"Build strong friendships","Raise average happiness","Earn goodwill for actions","Connect isolated students"};

    public StartScreen(StartListener listener) {
        this.listener = listener;
        setLayout(new GridBagLayout());
        setOpaque(false);
        animTimer = new Timer(40, e -> { animTick += 0.01f; repaint(); });
        animTimer.start();
        buildUI();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setPaint(new GradientPaint(0, 0, BG_TOP, 0, getHeight(), BG_BOT));
        g2.fillRect(0, 0, getWidth(), getHeight());
        int w = getWidth(), h = getHeight();
        for (int i = 0; i < NODES.length; i++) {
            float cx = NODES[i][0] * w + (float)(Math.sin(animTick + i * 1.2f) * 18);
            float cy = NODES[i][1] * h + (float)(Math.cos(animTick * 0.7f + i * 0.9f) * 14);
            g2.setColor(new Color(80, 70, 130, 60));
            g2.fillOval((int)cx - 6, (int)cy - 6, 12, 12);
        }
    }

    private void buildUI() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(PANEL_BG);
        panel.setBorder(new CompoundBorder(new LineBorder(new Color(80, 70, 130), 1, true), new EmptyBorder(44, 64, 44, 64)));

        JLabel title = new JLabel("SOCIAL ARCHITECT");
        title.setFont(new Font("SansSerif", Font.BOLD, 32));
        title.setForeground(GOLD);
        title.setAlignmentX(CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Connect all to get some peace");
        sub.setFont(new Font("SansSerif", Font.ITALIC, 13));
        sub.setForeground(DIM);
        sub.setAlignmentX(CENTER_ALIGNMENT);

        JPanel divider = new JPanel() {
            protected void paintComponent(Graphics g) {
                if (getWidth() == 0) return;
                Graphics2D g2 = (Graphics2D) g;
                float[] fracs = {0f, 0.5f, 1f};
                Color[] cols = {new Color(0,0,0,0), GOLD_DIM, new Color(0,0,0,0)};
                g2.setPaint(new java.awt.LinearGradientPaint(0, 0, getWidth(), 0, fracs, cols));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        divider.setOpaque(false);
        divider.setPreferredSize(new Dimension(340, 1));
        divider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        divider.setAlignmentX(CENTER_ALIGNMENT);

        JPanel bullets = new JPanel();
        bullets.setLayout(new BoxLayout(bullets, BoxLayout.Y_AXIS));
        bullets.setOpaque(false);
        bullets.setAlignmentX(CENTER_ALIGNMENT);
        for (String text : BULLETS) {
            JLabel l = new JLabel("▸  " + text);
            l.setFont(new Font("SansSerif", Font.PLAIN, 12));
            l.setForeground(BULLET);
            l.setAlignmentX(LEFT_ALIGNMENT);
            bullets.add(l);
            bullets.add(Box.createVerticalStrut(6));
        }

        JButton playBtn = new JButton("Play") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isPressed()  ? GREEN.darker(): getModel().isRollover() ? GREEN: new Color(45, 160, 90);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth()  - fm.stringWidth(getText())) / 2, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
            }
        };
        playBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        playBtn.setForeground(Color.WHITE);
        playBtn.setContentAreaFilled(false);
        playBtn.setBorderPainted(false);
        playBtn.setFocusPainted(false);
        playBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        playBtn.setAlignmentX(CENTER_ALIGNMENT);
        playBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        playBtn.setPreferredSize(new Dimension(200, 38));
        playBtn.addActionListener(e -> { if (animTimer != null) animTimer.stop(); listener.onStart(); });

        panel.add(title);
        panel.add(Box.createVerticalStrut(6));
        panel.add(sub);
        panel.add(Box.createVerticalStrut(24));
        panel.add(divider);
        panel.add(Box.createVerticalStrut(24));
        panel.add(bullets);
        panel.add(Box.createVerticalStrut(28));
        panel.add(playBtn);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER;
        add(panel, gbc);
    }

    public void restartAnimation() {
        if (animTimer == null)
            animTimer = new Timer(40, e -> { animTick += 0.01f; repaint(); });
        if (!animTimer.isRunning()) animTimer.start();
    }
}