import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;




public class User extends JFrame {
    private JPanel sidebar;
    private JPanel content;
    private CardLayout cardLayout;
    private Map<String, JPanel> contentPanels;
    private Random random;
    private static final Color SIDEBAR_BG = new Color(45, 45, 45);
    private static final Color SIDEBAR_TEXT = Color.WHITE;
    private static final Color CONTENT_BG = new Color(245, 245, 245);
    private static final Color CONTENT_TEXT = new Color(30, 30, 30);

    public User() {
        random = new Random();
        initializeContentPanels();

        setTitle("User Management");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(150, getHeight()));
        sidebar.setBackground(SIDEBAR_BG);
        add(sidebar, BorderLayout.WEST);

        addCollegeName();
        addButtons();
        addLogoutButton();

        content = new JPanel();
        cardLayout = new CardLayout();
        content.setLayout(cardLayout);
        content.setBackground(CONTENT_BG);
        add(content, BorderLayout.CENTER);

        for (Map.Entry<String, JPanel> entry : contentPanels.entrySet()) {
            content.add(entry.getValue(), entry.getKey());
        }

        // Show Dashboard content by default
        displayRandomText("Dashboard");
        cardLayout.show(content, "Dashboard");
    }

    private void showCourseContent() {
        JPanel coursesPanel = contentPanels.get("Courses");
        coursesPanel.removeAll();
        coursesPanel.setLayout(new BorderLayout());

        // Create filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel courseLabel = new JLabel("Course:");
        JComboBox<String> courseFilter = new JComboBox<>();
        JLabel chapterLabel = new JLabel("Chapter:");
        JComboBox<String> chapterFilter = new JComboBox<>();
        JButton applyFilter = new JButton("Apply Filter");

        filterPanel.add(courseLabel);
        filterPanel.add(courseFilter);
        filterPanel.add(chapterLabel);
        filterPanel.add(chapterFilter);
        filterPanel.add(applyFilter);

        coursesPanel.add(filterPanel, BorderLayout.NORTH);

        // Create a table to display course content
        String[] columnNames = {"Course", "Chapter", "Lesson", "Resource Type", "File Path"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        table.removeColumn(table.getColumnModel().getColumn(4)); // Hide the File Path column
        JScrollPane scrollPane = new JScrollPane(table);

        // Fetch course data from the database and populate the table
        ResultSet courses = Database.getAllCourses();
        if (courses != null) {
            try {
                Set<String> uniqueCourses = new HashSet<>();
                Set<String> uniqueChapters = new HashSet<>();
                
                while (courses.next()) {
                    String course = courses.getString("name");
                    String chapter = courses.getString("chapter");
                    
                    model.addRow(new Object[]{
                        course,
                        chapter,
                        courses.getString("lesson"),
                        courses.getString("resource_type"),
                        courses.getString("file_path") // Keep the file path in the model
                    });
                    
                    uniqueCourses.add(course);
                    uniqueChapters.add(chapter);
                }
                
                // Populate filter comboboxes
                courseFilter.addItem("All Courses");
                for (String course : uniqueCourses) {
                    courseFilter.addItem(course);
                }
                
                chapterFilter.addItem("All Chapters");
                for (String chapter : uniqueChapters) {
                    chapterFilter.addItem(chapter);
                }
            } catch (SQLException e) {
                System.out.println("Error reading course data: " + e.getMessage());
            }
        }

        coursesPanel.add(scrollPane, BorderLayout.CENTER);

        // Add a download button
        JButton downloadButton = new JButton("Download Selected Resource");
        downloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    String filePath = (String) model.getValueAt(selectedRow, 4); // Get file path from hidden column
                    String fileName = new File(filePath).getName();

                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setSelectedFile(new File(fileName));
                    int userSelection = fileChooser.showSaveDialog(User.this);

                    if (userSelection == JFileChooser.APPROVE_OPTION) {
                        File fileToSave = fileChooser.getSelectedFile();
                        try {
                            Path source = Paths.get(filePath);
                            Path destination = fileToSave.toPath();
                            Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
                            JOptionPane.showMessageDialog(User.this,
                                "File downloaded successfully: " + destination,
                                "Download Complete", JOptionPane.INFORMATION_MESSAGE);
                        } catch (IOException ex) {
                            JOptionPane.showMessageDialog(User.this,
                                "Error downloading file: " + ex.getMessage(),
                                "Download Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(User.this,
                        "Please select a resource to download",
                        "No Selection", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        coursesPanel.add(downloadButton, BorderLayout.SOUTH);

        // Add filter functionality
        applyFilter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedCourse = (String) courseFilter.getSelectedItem();
                String selectedChapter = (String) chapterFilter.getSelectedItem();
                
                TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
                table.setRowSorter(sorter);
                
                RowFilter<DefaultTableModel, Object> courseFilter = RowFilter.regexFilter(selectedCourse.equals("All Courses") ? "." : selectedCourse, 0);
                RowFilter<DefaultTableModel, Object> chapterFilter = RowFilter.regexFilter(selectedChapter.equals("All Chapters") ? "." : selectedChapter, 1);
                
                List<RowFilter<DefaultTableModel, Object>> filters = new ArrayList<>();
                filters.add(courseFilter);
                filters.add(chapterFilter);
                
                sorter.setRowFilter(RowFilter.andFilter(filters));
            }
        });

        coursesPanel.revalidate();
        coursesPanel.repaint();
    }

    private void showScheduleContent() {
        JPanel schedulePanel = contentPanels.get("Schedule");
        schedulePanel.removeAll();
        schedulePanel.setLayout(new BorderLayout());
    
        String[] columnNames = {"Course", "Day", "Start Time", "End Time", "Room"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
    
        ResultSet schedules = Database.getAllSchedules();
        if (schedules != null) {
            try {
                while (schedules.next()) {
                    model.addRow(new Object[]{
                        schedules.getString("course"),
                        schedules.getString("day"),
                        schedules.getString("start_time"),
                        schedules.getString("end_time"),
                        schedules.getString("room")
                    });
                }
            } catch (SQLException e) {
                System.out.println("Error reading schedule data: " + e.getMessage());
            }
        }
    
        schedulePanel.add(scrollPane, BorderLayout.CENTER);
    
        schedulePanel.revalidate();
        schedulePanel.repaint();
    }
    
    private void showGrades() {
        JPanel gradesPanel = contentPanels.get("Grades");
        gradesPanel.removeAll();
        gradesPanel.setLayout(new BorderLayout());
    
        // Create a table to display grades
        String[] columnNames = {"Course", "Grade"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
    
        // Check if there's a current user ID
        String currentUserId = Database.getCurrentUserId();
        if (currentUserId == null || currentUserId.isEmpty()) {
            // No user ID, display a message instead of grades
            JLabel noUserLabel = new JLabel("Please log in to view your grades.", SwingConstants.CENTER);
            noUserLabel.setFont(new Font("Arial", Font.BOLD, 16));
            gradesPanel.add(noUserLabel, BorderLayout.CENTER);
        } else {
            // Fetch grades data from the database and populate the table
            ResultSet grades = Database.getGradesForCurrentUser();
            if (grades != null) {
                try {
                    while (grades.next()) {
                        String courseId = grades.getString("course_id");
                        String grade = grades.getString("grade");
                        
                        // You might want to fetch the course name based on the course_id
                        // For now, we'll just display the course_id
                        model.addRow(new Object[]{courseId, grade});
                    }
                } catch (SQLException e) {
                    System.out.println("Error reading grades data: " + e.getMessage());
                }
            }
    
            gradesPanel.add(scrollPane, BorderLayout.CENTER);
        }
    
        // Add a label at the top
        JLabel titleLabel = new JLabel("Your Grades", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gradesPanel.add(titleLabel, BorderLayout.NORTH);
    
        gradesPanel.revalidate();
        gradesPanel.repaint();
    }

    private void showNotifications() {
        JPanel notificationPanel = contentPanels.get("Notifications");
        notificationPanel.removeAll();
        notificationPanel.setLayout(new BorderLayout());
    
        String[] columnNames = {"Date", "Title", "Content"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
    
        ResultSet notifications = Database.getAllAnnouncements();
        if (notifications != null) {
            try {
                while (notifications.next()) {
                    model.addRow(new Object[]{
                        notifications.getString("date"),
                        notifications.getString("title"),
                        notifications.getString("content")
                    });
                }
            } catch (SQLException e) {
                System.out.println("Error reading notification data: " + e.getMessage());
            }
        }
    
        notificationPanel.add(scrollPane, BorderLayout.CENTER);
    
        notificationPanel.revalidate();
        notificationPanel.repaint();
    }
    
    
    private void initializeContentPanels() {
        contentPanels = new HashMap<>();
        String[] sections = {"Dashboard", "Courses", "Schedule", "Grades", "Notifications", "Logout"};
        for (String section : sections) {
            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBackground(CONTENT_BG);
            JTextArea textArea = new JTextArea();
            textArea.setEditable(false);
            textArea.setFont(new Font("Arial", Font.PLAIN, 24));
            textArea.setForeground(CONTENT_TEXT);
            textArea.setBackground(null);
            textArea.setOpaque(false);
            panel.add(textArea);
            contentPanels.put(section, panel);
            // contentPanels.put("Grades", showGrades());
        }
    }

    private void addCollegeName() {
        JPanel collegeNamePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        collegeNamePanel.setBackground(SIDEBAR_BG);
        JLabel collegeNameLabel = new JLabel("BITS", SwingConstants.CENTER);
        collegeNameLabel.setFont(new Font("Arial", Font.BOLD, 24));
        collegeNameLabel.setForeground(SIDEBAR_TEXT);
        collegeNamePanel.add(collegeNameLabel);
        collegeNamePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        collegeNamePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, collegeNamePanel.getPreferredSize().height));
        sidebar.add(collegeNamePanel);
       // sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
    }

    private void addButtons() {
        addButton("Dashboard", () -> displayRandomText("Dashboard"));
        addButton("Courses", this::showCourseContent);
        addButton("Schedule", this::showScheduleContent);
        addButton("Grades", this::showGrades);
        addButton("Notifications", this::showNotifications);
    }

    private void addButton(String name, Runnable action) {
        JButton button = new JButton(name);
        button.setFont(new Font("Arial", Font.PLAIN, 16));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setPreferredSize(new Dimension(150, 50));
        button.setMaximumSize(new Dimension(150, 50));
        button.setMinimumSize(new Dimension(150, 50));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMargin(new Insets(0, 10, 0, 0));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setForeground(SIDEBAR_TEXT);
        button.setBackground(SIDEBAR_BG);

        button.addActionListener(e -> {
            cardLayout.show(content, name);
            action.run();
        });
        
        // Add a border to the button
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(60, 60, 60)), // Bottom border
            BorderFactory.createEmptyBorder(5, 10, 5, 10) // Padding
        ));
    
        
    
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(60, 60, 60));
            }
    
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(SIDEBAR_BG);
            }
        });
    
        sidebar.add(button);
    }
    
    private void handleLogout() {
        int result = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?", "Logout Confirmation",
            JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            // Perform logout actions here
            this.dispose(); // Close the current window
            new Login().setVisible(true); // Open the login window
        }
    }
    
    private void addLogoutButton() {
        sidebar.add(Box.createVerticalGlue());
        addButton("Logout", this::handleLogout);
    }

    private void displayRandomText(String buttonName) {
        JPanel panel = contentPanels.get(buttonName);
        if (panel != null) {
            JTextArea textArea = (JTextArea) panel.getComponent(0);
            String[] texts = getTextsForButton(buttonName);
            if (texts != null && texts.length > 0) {
                int index = random.nextInt(texts.length);
                textArea.setText(texts[index]);
            }
        }
    }

    private String[] getTextsForButton(String buttonName) {
        Map<String, String[]> buttonTexts = new HashMap<>();
        buttonTexts.put("Dashboard", new String[]{
            "Welcome to your dashboard!",
        });
        return buttonTexts.get(buttonName);
    }

    public static void main(String[] args) {
        Database.initializeDatabase();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new User().setVisible(true);
            }
        });
    }
}
