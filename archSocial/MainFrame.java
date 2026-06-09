import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class MainFrame {

    private static final String fiLe = "Students.csv";

    public static void main(String[] args) {
        Map<String, Student> students = StudentLoader.loadStudentsFromCSV(fiLe);

        if (students.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Could not load Students.csv!\nMake sure it is in the same folder as the program.", "Load Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Social Architect");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1180, 720);
            frame.setMinimumSize(new Dimension(960, 620));
            frame.setLocationRelativeTo(null);
            frame.getContentPane().setBackground(new Color(8, 8, 20));

            CardLayout cards = new CardLayout();
            JPanel root = new JPanel(cards) {
                @Override protected void paintComponent(Graphics g) {
                    g.setColor(new Color(8, 8, 20));
                    g.fillRect(0, 0, getWidth(), getHeight());
                    super.paintComponent(g);
                }
            };
            root.setOpaque(false);

            final StartScreen[] startHolder = new StartScreen[1];
            EndScreen endScreen = new EndScreen(result -> {
                if (startHolder[0] != null)
                    startHolder[0].restartAnimation();
                cards.show(root, "START");
            });
            root.add(endScreen, "END");

            final Component[] currentGame = new Component[1];
            StartScreen startScreen = new StartScreen(() -> {
                if (currentGame[0] != null) root.remove(currentGame[0]);

                Map<String, Student> fresh = StudentLoader.loadStudentsFromCSV(fiLe);
                GameState gameState = new GameState(fresh);

                SocialNetworkPanel networkPanel = new SocialNetworkPanel(fresh);
                RightControlPanel rightPanel = new RightControlPanel(gameState, networkPanel, result -> {
                    endScreen.setResultText(result);
                    cards.show(root, "END");
                });
                networkPanel.setStudentSelectListener(name -> rightPanel.onStudentSelected(name));

                JPanel gamePanel = new JPanel(new BorderLayout()) {
                    @Override protected void paintComponent(Graphics g) {
                        g.setColor(new Color(8, 8, 20));
                        g.fillRect(0, 0, getWidth(), getHeight());
                        super.paintComponent(g);
                    }
                };
                gamePanel.setOpaque(false);
                gamePanel.add(networkPanel, BorderLayout.CENTER);
                gamePanel.add(rightPanel,   BorderLayout.EAST);

                root.add(gamePanel, "GAME");
                currentGame[0] = gamePanel;
                cards.show(root, "GAME");
                rightPanel.refresh();
            });
            startHolder[0] = startScreen;

            root.add(startScreen, "START");
            cards.show(root, "START");

            frame.add(root);
            frame.setVisible(true);
        });
    }
}