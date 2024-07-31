import javax.swing.*;
import java.awt.*;


public class Login extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JTextField userNameField;
    private JPasswordField passwordField;

    public Login() {
        setTitle("Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        JPanel loginPanel = createLoginPanel();
        mainPanel.add(loginPanel, "LOGIN");
        
        setContentPane(mainPanel);

        // Initialize the database and default users
        Database.initializeDatabase();
        Database.initializeDefaultUsers();
    }

        private JPanel createLoginPanel() {
            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            panel.setBackground(new Color(240, 240, 240)); // Light gray background

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            JLabel titleLabel = new JLabel("Login");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
            titleLabel.setHorizontalAlignment(JLabel.CENTER);
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 2;
            panel.add(titleLabel, gbc);

            gbc.gridwidth = 1;
            gbc.gridy++;
            JLabel userNameLabel = new JLabel("User Name:");
            userNameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            panel.add(userNameLabel, gbc);

            gbc.gridx = 1;
            userNameField = new JTextField(15);
            userNameField.setFont(new Font("Arial", Font.PLAIN, 14));
            panel.add(userNameField, gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            JLabel passwordLabel = new JLabel("Password:");
            passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            panel.add(passwordLabel, gbc);

            gbc.gridx = 1;
            passwordField = new JPasswordField(15);
            passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
            panel.add(passwordField, gbc);

            gbc.gridx = 1;
            gbc.gridy++;
            gbc.insets = new Insets(20, 10, 10, 10);
            JButton loginButton = new JButton("Login");
            loginButton.setFont(new Font("Arial", Font.BOLD, 14));
            loginButton.addActionListener(e -> attemptLogin());
            panel.add(loginButton, gbc);

            return panel;
    }

    private void attemptLogin() {
        String username = userNameField.getText();
        String password = new String(passwordField.getPassword());
        
        if (Database.authenticateUser(username, password)) {
            JOptionPane.showMessageDialog(this, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            
            // Open the appropriate window based on user type
            if (username.equals("admin")) {
                openAdminWindow();
            } else {
                openUserWindow();
            }
            
            // Close the login window
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openAdminWindow() {
        SwingUtilities.invokeLater(() -> {
            new Admin().setVisible(true);
        });
    }

    private void openUserWindow() {
        SwingUtilities.invokeLater(() -> {
            new User().setVisible(true);
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new Login().setVisible(true);
        });
    }
}

