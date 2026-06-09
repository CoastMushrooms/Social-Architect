import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class EndScreen extends JPanel {
    private JTextArea resultArea;
    private JButton restartButton;

    private static final Color BG_TOP = new Color(8, 8, 20);
    private static final Color BG_BOT = new Color(18, 10, 35);
    private static final Color TEXT = new Color(210, 220, 230);
    private static final Color ACCENT = new Color(100, 170, 240);
    private static final Color BUTTON_BG = new Color(70, 120, 200);

    public EndScreen(java.util.function.Consumer<String> onRestart) {
        setLayout(new GridBagLayout());
        setOpaque(false);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(new CompoundBorder(new LineBorder(new Color(80, 70, 130), 1, true), new EmptyBorder(24, 28, 24, 28)));

        JLabel title = new JLabel("Game Over");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(ACCENT);
        title.setAlignmentX(CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Review the final result and return to the start.");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitle.setForeground(TEXT);
        subtitle.setAlignmentX(CENTER_ALIGNMENT);

        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setWrapStyleWord(true);
        resultArea.setLineWrap(true);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        resultArea.setForeground(TEXT);
        resultArea.setBackground(new Color(14, 16, 28));
        resultArea.setBorder(new EmptyBorder(12, 12, 12, 12));
        resultArea.setPreferredSize(new Dimension(420, 220));
        resultArea.setOpaque(true);

        restartButton = new JButton("Restart");
        restartButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        restartButton.setForeground(Color.WHITE);
        restartButton.setBackground(BUTTON_BG);
        restartButton.setFocusPainted(false);
        restartButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        restartButton.setBorder(new EmptyBorder(10, 18, 10, 18));
        restartButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { 
                restartButton.setBackground(BUTTON_BG.brighter()); 
            }
            public void mouseExited(java.awt.event.MouseEvent e) { 
                restartButton.setBackground(BUTTON_BG); 
            }
        });
        restartButton.addActionListener(e -> onRestart.accept("restart"));
        restartButton.setAlignmentX(CENTER_ALIGNMENT);

        card.add(title);
        card.add(Box.createVerticalStrut(10));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(18));
        card.add(resultArea);
        card.add(Box.createVerticalStrut(18));
        card.add(restartButton);

        add(card);
    }

    public void setResultText(String result) {
        resultArea.setText(result);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        GradientPaint gp = new GradientPaint(0, 0, BG_TOP, 0, getHeight(), BG_BOT);
        g2.setPaint(gp);
        g2.fillRect(0, 0, getWidth(), getHeight());
    }
}