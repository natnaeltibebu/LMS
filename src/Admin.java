import javax.swing.*;              // GUI components
import java.awt.*;                 // Layout 
import java.awt.event.*;           // Event handling
import javax.swing.table.*;        // Table components
import java.sql.*;                 // Database operations
import java.util.*;                // Collections and utilities
import java.util.Date;
import java.text.SimpleDateFormat;


public class Admin extends JFrame { 
    private JPanel sidebar;          // Left panel for navigation
    private JPanel content;          // Main content area
    private CardLayout cardLayout;   // Manages multiple panels in content area
    private Map<String, JPanel> contentPanels; // Stores different content panels
    

    // Color constants for UI
    private static final Color SIDEBAR_BG = new Color(45, 45, 45);
    private static final Color SIDEBAR_TEXT = Color.WHITE;
    private static final Color CONTENT_BG = new Color(245, 245, 245);
    private static final Color CONTENT_TEXT = new Color(30, 30, 30);
     

    public Admin() {
        // Initialize components
        initializeContentPanels();
    
        // Set up main window properties
        setTitle("Classroom Management");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
    
        // Create and configure sidebar
        sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(150, getHeight()));
        sidebar.setBackground(SIDEBAR_BG);
        add(sidebar, BorderLayout.WEST);
    
        // Add elements to sidebar
        addCollegeName();
        addButtons();
        addLogoutButton();
    
        // Create and configure main content area
        content = new JPanel();
        cardLayout = new CardLayout();
        content.setLayout(cardLayout);
        content.setBackground(CONTENT_BG);
        add(content, BorderLayout.CENTER);
    
        // Add content panels to main area
        for (Map.Entry<String, JPanel> entry : contentPanels.entrySet()) {
            content.add(entry.getValue(), entry.getKey());
        }
    
        // Set default view to Dashboard
        showWelcomeMessage();
        cardLayout.show(content, "Dashboard");
    }


    private void initializeContentPanels() {
        contentPanels = new HashMap<>();
        String[] sections = {"Dashboard", "Courses", "Schedule", "Gradebook", "Announcement", "Logout"};
        for (String section : sections) {
            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBackground(CONTENT_BG);
            contentPanels.put(section, panel);
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
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
    }

    private void addButtons() {
        addButton("Dashboard");
        addButton("Courses");
        addButton("Schedule");
        addButton("Gradebook");
        addButton("Announcement");
    }

    private void addButton(String name) {
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
        
        // Add a border to the button
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(60, 60, 60)), // Bottom border
            BorderFactory.createEmptyBorder(5, 10, 5, 10) // Padding
        ));
    
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(content, name);
                if (name.equals("Announcement")) {
                    showAnnouncementForm();
                } else if (name.equals("Gradebook")) {
                    showGradeEntryForm();
                } else if (name.equals("Schedule")) {
                    showScheduleEntryForm();
                } else if (name.equals("Courses")) {
                    showCourseUploadForm();
                } else if (name.equals("Logout")){
                    logout();
                } else{
                    showWelcomeMessage();
                }
            }
        });
    
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

    private void addLogoutButton() {
        sidebar.add(Box.createVerticalGlue());
        addButton("Logout");
    }

    private void showWelcomeMessage() {
        JPanel dashboardPanel = contentPanels.get("Dashboard");
        dashboardPanel.removeAll();

        JLabel welcomeLabel = new JLabel("Welcome to your dashboard!");
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        welcomeLabel.setForeground(CONTENT_TEXT);
        
        dashboardPanel.add(welcomeLabel);
        dashboardPanel.revalidate();
        dashboardPanel.repaint();
    }

    
    private void showCourseUploadForm() {
        JPanel coursesPanel = contentPanels.get("Courses");
        coursesPanel.removeAll();
        coursesPanel.setLayout(new BorderLayout());
        coursesPanel.add(createCourseUploadForm(), BorderLayout.CENTER);
        coursesPanel.revalidate();
        coursesPanel.repaint();
    }

    private void showScheduleEntryForm() {
        JPanel schedulePanel = contentPanels.get("Schedule");
        schedulePanel.removeAll();
        schedulePanel.setLayout(new BorderLayout());
        schedulePanel.add(createScheduleEntryForm(), BorderLayout.CENTER);
        schedulePanel.revalidate();
        schedulePanel.repaint();
    }

    private void showGradeEntryForm() {
        JPanel gradebookPanel = contentPanels.get("Gradebook");
        gradebookPanel.removeAll();
        gradebookPanel.setLayout(new BorderLayout());
        gradebookPanel.add(createGradeEntryForm(), BorderLayout.CENTER);
        gradebookPanel.revalidate();
        gradebookPanel.repaint();
    }

    private void showAnnouncementForm() {
        JPanel announcementPanel = contentPanels.get("Announcement");
        announcementPanel.removeAll();
        announcementPanel.setLayout(new BorderLayout());
        announcementPanel.add(createAnnouncementForm(), BorderLayout.CENTER);
        announcementPanel.revalidate();
        announcementPanel.repaint();
    }

    
    public static DefaultTableModel buildTableModel(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        Vector<String> columnNames = new Vector<>();
        int columnCount = metaData.getColumnCount();
        for (int column = 1; column <= columnCount; column++) {
            columnNames.add(metaData.getColumnName(column));
        }

        Vector<Vector<Object>> data = new Vector<>();
        while (rs.next()) {
            Vector<Object> vector = new Vector<>();
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                vector.add(rs.getObject(columnIndex));
            }
            data.add(vector);
        }

        return new DefaultTableModel(data, columnNames);
    }

    
    private JPanel createCourseUploadForm() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(CONTENT_BG);
    
        // Create the table
        JTable courseTable = new JTable();
        courseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        courseTable.setAutoCreateRowSorter(true);
        JScrollPane scrollPane = new JScrollPane(courseTable);
        refreshCourseTable(courseTable);
        
    
        // Create the top panel with buttons
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        topPanel.setBackground(CONTENT_BG);
    
        // Create the "Add/Edit Course" button
        JButton addCourseButton = new JButton("Add Course");
        addCourseButton.setPreferredSize(new Dimension(110, 30));
        
        // Add the buttons panel to the top panel
        topPanel.add(addCourseButton);

        // Add search field to the right of the table
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setBackground(CONTENT_BG);
        
        // Create the search field and button
        JTextField searchField = new JTextField(15);
        searchField.setToolTipText("Search courses");
        JButton searchButton = new JButton("Search");
    
        // Make all components equal height
        int buttonHeight = addCourseButton.getPreferredSize().height;
        
        searchField.setPreferredSize(new Dimension(searchField.getPreferredSize().width, buttonHeight));
        searchButton.setPreferredSize(new Dimension(searchButton.getPreferredSize().width, buttonHeight));
        
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
    
        // Create the form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(CONTENT_BG);
        formPanel.setBorder(BorderFactory.createTitledBorder("Course Details"));
    
        // Create form components
        String[] courses = {"Maths", "Physics", "Chemistry", "Biology"};
        JComboBox<String> courseComboBox = new JComboBox<>(courses);
        String[] chapters = {"Chapter 1", "Chapter 2", "Chapter 3", "Chapter 4"};
        JComboBox<String> chapterComboBox = new JComboBox<>(chapters);
        String[] lessons = {"Lesson 1", "Lesson 2", "Lesson 3", "Lesson 4"};
        JComboBox<String> lessonComboBox = new JComboBox<>(lessons);
        String[] resourceTypes = {"Assignment", "Lecture Notes", "Syllabus", "Quiz", "Other"};
        JComboBox<String> resourceTypeComboBox = new JComboBox<>(resourceTypes);
        JTextField filePathField = new JTextField(20);
        JButton browseButton = new JButton("Browse");
        JButton submitButton = new JButton("Submit");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
    
        // Style the buttons
        submitButton.setBackground(Color.LIGHT_GRAY);
        updateButton.setBackground(Color.LIGHT_GRAY);
        deleteButton.setBackground(Color.RED);
        browseButton.setBackground(Color.LIGHT_GRAY);
    
        submitButton.setForeground(Color.BLACK);
        updateButton.setForeground(Color.BLACK);
        deleteButton.setForeground(Color.BLACK);
        browseButton.setForeground(Color.BLACK);
    
        // Set preferred size for all components to ensure consistent width
        Dimension fieldSize = new Dimension(250, 30);
        courseComboBox.setPreferredSize(fieldSize);
        chapterComboBox.setPreferredSize(fieldSize);
        lessonComboBox.setPreferredSize(fieldSize);
        resourceTypeComboBox.setPreferredSize(fieldSize);
        filePathField.setPreferredSize(new Dimension(190, 30));
        browseButton.setPreferredSize(new Dimension(80, 30));
    
        // Add components to the form panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
    
        formPanel.add(new JLabel("Course:"), gbc);
        gbc.gridy++;
        formPanel.add(courseComboBox, gbc);
        gbc.gridy++;
        formPanel.add(new JLabel("Chapter:"), gbc);
        gbc.gridy++;
        formPanel.add(chapterComboBox, gbc);
        gbc.gridy++;
        formPanel.add(new JLabel("Lesson:"), gbc);
        gbc.gridy++;
        formPanel.add(lessonComboBox, gbc);
        gbc.gridy++;
        formPanel.add(new JLabel("Resource Type:"), gbc);
        gbc.gridy++;
        formPanel.add(resourceTypeComboBox, gbc);
        gbc.gridy++;
        formPanel.add(new JLabel("File:"), gbc);
        gbc.gridy++;
    
        // Create a panel for file path and browse button
        JPanel filePanel = new JPanel(new BorderLayout(5, 0));
        filePanel.setBackground(CONTENT_BG);
        filePanel.add(filePathField, BorderLayout.CENTER);
        filePanel.add(browseButton, BorderLayout.EAST);
        formPanel.add(filePanel, gbc);
    
        gbc.gridy++;
        
        // Create a panel for the buttons
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBackground(CONTENT_BG);
        
        GridBagConstraints buttonGbc = new GridBagConstraints();
        buttonGbc.fill = GridBagConstraints.HORIZONTAL;
        buttonGbc.insets = new Insets(5, 0, 5, 0);
        buttonGbc.weightx = 1.0;
        
        buttonPanel.add(submitButton, buttonGbc);
        
        buttonGbc.gridx = 1;
        buttonGbc.insets = new Insets(5, 5, 5, 5);
        buttonPanel.add(updateButton, buttonGbc);
        
        buttonGbc.gridx = 2;
        buttonGbc.insets = new Insets(5, 0, 5, 0);
        buttonPanel.add(deleteButton, buttonGbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);
    
        // Create a panel for the table and search field
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        tablePanel.add(searchPanel, BorderLayout.SOUTH);
    
        // Create a split pane to hold the table panel and form
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tablePanel, formPanel);
        splitPane.setResizeWeight(0.7); // 70% of space to the table, 30% to the form
    
        // Add components to the main panel
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(splitPane, BorderLayout.CENTER);
    
        //Add action listeners
        addCourseButton.addActionListener(e -> {
            formPanel.setVisible(!formPanel.isVisible());
            splitPane.resetToPreferredSizes();
            mainPanel.revalidate();
            mainPanel.repaint();
        });
    
        browseButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(Admin.this);
            if (result == JFileChooser.APPROVE_OPTION) {
                filePathField.setText(fileChooser.getSelectedFile().getAbsolutePath());
            }
        });
    
        submitButton.addActionListener(e -> {
            String course = (String) courseComboBox.getSelectedItem();
            String chapter = (String) chapterComboBox.getSelectedItem();
            String lesson = (String) lessonComboBox.getSelectedItem();
            String resourceType = (String) resourceTypeComboBox.getSelectedItem();
            String filePath = filePathField.getText();
    
            if (course != null && chapter != null && lesson != null && !filePath.isEmpty()) {
                Database.addCourse(course, chapter, lesson, resourceType, filePath);
                JOptionPane.showMessageDialog(Admin.this, "Course added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshCourseTable(courseTable);
                clearFields(courseComboBox, chapterComboBox, lessonComboBox, resourceTypeComboBox, filePathField);
            } else {
                JOptionPane.showMessageDialog(Admin.this, "Please fill in all fields.", "Incomplete Form", JOptionPane.WARNING_MESSAGE);
            }
        });
    
        updateButton.addActionListener(e -> {
            int selectedRow = courseTable.getSelectedRow();
            if (selectedRow != -1) {
                int id = (int) courseTable.getValueAt(selectedRow, 0);
                String course = (String) courseComboBox.getSelectedItem();
                String chapter = (String) chapterComboBox.getSelectedItem();
                String lesson = (String) lessonComboBox.getSelectedItem();
                String resourceType = (String) resourceTypeComboBox.getSelectedItem();
                String filePath = filePathField.getText();
    
                if (course != null && chapter != null && lesson != null && !filePath.isEmpty()) {
                    Database.updateCourse(id, course, chapter, lesson, resourceType, filePath);
                    JOptionPane.showMessageDialog(Admin.this, "Course updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    refreshCourseTable(courseTable);
                    clearFields(courseComboBox, chapterComboBox, lessonComboBox, resourceTypeComboBox, filePathField);
                } else {
                    JOptionPane.showMessageDialog(Admin.this, "Please fill in all fields.", "Incomplete Form", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(Admin.this, "Please select a course to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });
    
        deleteButton.addActionListener(e -> {
            int selectedRow = courseTable.getSelectedRow();
            if (selectedRow != -1) {
                int id = (int) courseTable.getValueAt(selectedRow, 0);
                int confirm = JOptionPane.showConfirmDialog(Admin.this, "Are you sure you want to delete this course?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    Database.deleteCourse(id);
                    JOptionPane.showMessageDialog(Admin.this, "Course deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    refreshCourseTable(courseTable);
                    clearFields(courseComboBox, chapterComboBox, lessonComboBox, resourceTypeComboBox, filePathField);
                }
            } else {
                JOptionPane.showMessageDialog(Admin.this, "Please select a course to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });
    
        courseTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = courseTable.getSelectedRow();
                if (selectedRow != -1) {
                    courseComboBox.setSelectedItem(courseTable.getValueAt(selectedRow, 1));
                    chapterComboBox.setSelectedItem(courseTable.getValueAt(selectedRow, 2));
                    lessonComboBox.setSelectedItem(courseTable.getValueAt(selectedRow, 3));
                    resourceTypeComboBox.setSelectedItem(courseTable.getValueAt(selectedRow, 4));
                    filePathField.setText((String) courseTable.getValueAt(selectedRow, 5));
                }
            }
        });
    
        searchButton.addActionListener(e -> {
            String searchTerm = searchField.getText().toLowerCase();
            TableRowSorter<TableModel> sorter = new TableRowSorter<>(courseTable.getModel());
            courseTable.setRowSorter(sorter);
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchTerm));
        });

    
        return mainPanel;
    }
    
    private void clearFields(JComboBox<String> courseComboBox, JComboBox<String> chapterComboBox, JComboBox<String> lessonComboBox, JComboBox<String> resourceTypeComboBox, JTextField filePathField) {
        courseComboBox.setSelectedIndex(0);
        chapterComboBox.setSelectedIndex(0);
        lessonComboBox.setSelectedIndex(0);
        resourceTypeComboBox.setSelectedIndex(0);
        filePathField.setText(""); 
    }

    private void refreshCourseTable(JTable courseTable) {
        ResultSet rs = Database.getAllCourses();
        try {
            courseTable.setModel(buildTableModel(rs));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }


    private JPanel createScheduleEntryForm() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(CONTENT_BG);
    
        // Create the table
        JTable scheduleTable = new JTable();
        scheduleTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scheduleTable.setAutoCreateRowSorter(true);
        JScrollPane scrollPane = new JScrollPane(scheduleTable);
        refreshScheduleTable(scheduleTable);
    
        // Create the top panel with buttons
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(CONTENT_BG);
    
        // Create the buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        buttonsPanel.setBackground(CONTENT_BG);
    
        // Create the "Add Schedule" button
        JButton addScheduleButton = new JButton("Add Schedule");
    
        // Create the search field and button
        JTextField searchField = new JTextField(15);
        searchField.setToolTipText("Search schedules");
        JButton searchButton = new JButton("Search");
    
        // Make all components equal height
        int buttonHeight = addScheduleButton.getPreferredSize().height;
        Dimension commonSize = new Dimension(searchField.getPreferredSize().width, buttonHeight);
        searchField.setPreferredSize(commonSize);
        addScheduleButton.setPreferredSize(new Dimension(110, 30));
        searchButton.setPreferredSize(new Dimension(searchButton.getPreferredSize().width, buttonHeight));
    
        // Add components to the buttons panel
        buttonsPanel.add(addScheduleButton);
    
        // Add the buttons panel to the top panel
        topPanel.add(buttonsPanel, BorderLayout.WEST);
    
        // Create the form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(CONTENT_BG);
        formPanel.setBorder(BorderFactory.createTitledBorder("Schedule Details"));
    
        // Create form components
        String[] courses = {"Maths", "Physics", "Chemistry", "Biology"}; 
        JComboBox<String> courseComboBox = new JComboBox<>(courses);
        
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        JComboBox<String> dayComboBox = new JComboBox<>(days);
        
        // Create time spinners
        SpinnerDateModel startModel = new SpinnerDateModel();
        JSpinner startTimeSpinner = new JSpinner(startModel);
        JSpinner.DateEditor startTimeEditor = new JSpinner.DateEditor(startTimeSpinner, "HH:mm");
        startTimeSpinner.setEditor(startTimeEditor);
    
        SpinnerDateModel endModel = new SpinnerDateModel();
        JSpinner endTimeSpinner = new JSpinner(endModel);
        JSpinner.DateEditor endTimeEditor = new JSpinner.DateEditor(endTimeSpinner, "HH:mm");
        endTimeSpinner.setEditor(endTimeEditor);
        
        String[] rooms = {"201", "202", "203", "204", "205"};
        JComboBox<String> roomComboBox = new JComboBox<>(rooms);
    
        JButton submitButton = new JButton("Submit");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
    
        // Style the buttons
        submitButton.setBackground(Color.LIGHT_GRAY);
        updateButton.setBackground(Color.LIGHT_GRAY);
        deleteButton.setBackground(Color.RED);
    
        // Set preferred size for all components to ensure consistent width
        Dimension fieldSize = new Dimension(250, 30);
        courseComboBox.setPreferredSize(fieldSize);
        dayComboBox.setPreferredSize(fieldSize);
        startTimeSpinner.setPreferredSize(fieldSize);
        endTimeSpinner.setPreferredSize(fieldSize);
        roomComboBox.setPreferredSize(fieldSize);
    
        // Add components to the form panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
    
        formPanel.add(new JLabel("Course:"), gbc);
        gbc.gridy++;
        formPanel.add(courseComboBox, gbc);
        gbc.gridy++;
        formPanel.add(new JLabel("Day:"), gbc);
        gbc.gridy++;
        formPanel.add(dayComboBox, gbc);
        gbc.gridy++;
        formPanel.add(new JLabel("Start Time:"), gbc);
        gbc.gridy++;
        formPanel.add(startTimeSpinner, gbc);
        gbc.gridy++;
        formPanel.add(new JLabel("End Time:"), gbc);
        gbc.gridy++;
        formPanel.add(endTimeSpinner, gbc);
        gbc.gridy++;
        formPanel.add(new JLabel("Room:"), gbc);
        gbc.gridy++;
        formPanel.add(roomComboBox, gbc);
        gbc.gridy++;
    
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBackground(CONTENT_BG);
        
        GridBagConstraints buttonGbc = new GridBagConstraints();
        buttonGbc.fill = GridBagConstraints.HORIZONTAL;
        buttonGbc.insets = new Insets(5, 0, 5, 0);
        buttonGbc.weightx = 1.0;
        
        buttonPanel.add(submitButton, buttonGbc);
        
        buttonGbc.gridx = 1;
        buttonGbc.insets = new Insets(5, 5, 5, 5);
        buttonPanel.add(updateButton, buttonGbc);
        
        buttonGbc.gridx = 2;
        buttonGbc.insets = new Insets(5, 0, 5, 0);
        buttonPanel.add(deleteButton, buttonGbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);
    
        // Create a panel for the table and search field
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.add(scrollPane, BorderLayout.CENTER);
    
        // Add search field to the right of the table
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setBackground(CONTENT_BG);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        tablePanel.add(searchPanel, BorderLayout.SOUTH);
    
        // Create a split pane to hold the table panel and form
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tablePanel, formPanel);
        splitPane.setResizeWeight(0.7); // 70% of space to the table, 30% to the form
    
        // Add components to the main panel
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(splitPane, BorderLayout.CENTER);
    
        // Add action listeners
        addScheduleButton.addActionListener(e -> {
            formPanel.setVisible(!formPanel.isVisible());
            splitPane.resetToPreferredSizes();
            mainPanel.revalidate();
            mainPanel.repaint();
        });
    
        submitButton.addActionListener(e -> {
            String course = (String) courseComboBox.getSelectedItem();
            String day = (String) dayComboBox.getSelectedItem();
            String startTime = new SimpleDateFormat("HH:mm").format((Date) startTimeSpinner.getValue());
            String endTime = new SimpleDateFormat("HH:mm").format((Date) endTimeSpinner.getValue());
            String room = (String) roomComboBox.getSelectedItem();
    
            if (course != null && day != null && !startTime.isEmpty() && !endTime.isEmpty() && room != null) {
                Database.addSchedule(course, day, startTime, endTime, room);
                JOptionPane.showMessageDialog(Admin.this, "Schedule added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshScheduleTable(scheduleTable);
                clearScheduleFields(courseComboBox, dayComboBox, startTimeSpinner, endTimeSpinner, roomComboBox);
            } else {
                JOptionPane.showMessageDialog(Admin.this, "Please fill in all fields.", "Incomplete Form", JOptionPane.WARNING_MESSAGE);
            }
        });
    
        updateButton.addActionListener(e -> {
            int selectedRow = scheduleTable.getSelectedRow();
            if (selectedRow != -1) {
                int id = (int) scheduleTable.getValueAt(selectedRow, 0);
                String course = (String) courseComboBox.getSelectedItem();
                String day = (String) dayComboBox.getSelectedItem();
                String startTime = new SimpleDateFormat("HH:mm").format((Date) startTimeSpinner.getValue());
                String endTime = new SimpleDateFormat("HH:mm").format((Date) endTimeSpinner.getValue());
                String room = (String) roomComboBox.getSelectedItem();
    
                if (course != null && day != null && !startTime.isEmpty() && !endTime.isEmpty() && room != null) {
                    Database.updateSchedule(id, course, day, startTime, endTime, room);
                    JOptionPane.showMessageDialog(Admin.this, "Schedule updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    refreshScheduleTable(scheduleTable);
                    clearScheduleFields(courseComboBox, dayComboBox, startTimeSpinner, endTimeSpinner, roomComboBox);
                } else {
                    JOptionPane.showMessageDialog(Admin.this, "Please fill in all fields.", "Incomplete Form", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(Admin.this, "Please select a schedule to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });
    
        deleteButton.addActionListener(e -> {
            int selectedRow = scheduleTable.getSelectedRow();
            if (selectedRow != -1) {
                int id = (int) scheduleTable.getValueAt(selectedRow, 0);
                int confirm = JOptionPane.showConfirmDialog(Admin.this, "Are you sure you want to delete this schedule?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    Database.deleteSchedule(id);
                    JOptionPane.showMessageDialog(Admin.this, "Schedule deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    refreshScheduleTable(scheduleTable);
                }
            } else {
                JOptionPane.showMessageDialog(Admin.this, "Please select a schedule to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });
    
        searchButton.addActionListener(e -> {
            String searchTerm = searchField.getText().toLowerCase();
            TableRowSorter<TableModel> sorter = new TableRowSorter<>(scheduleTable.getModel());
            scheduleTable.setRowSorter(sorter);
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchTerm));
        });
    
        return mainPanel;
    }
  
    private void clearScheduleFields(JComboBox<String> courseComboBox, JComboBox<String> dayComboBox, JSpinner startTimeSpinner, JSpinner endTimeSpinner, JComboBox<String> roomComboBox) {
        courseComboBox.setSelectedIndex(0);
        dayComboBox.setSelectedIndex(0);
        startTimeSpinner.setValue(new Date());
        endTimeSpinner.setValue(new Date());
        roomComboBox.setSelectedIndex(0);
    }    

    private void refreshScheduleTable(JTable scheduleTable) {
        ResultSet rs = Database.getAllSchedules();
        try {
            scheduleTable.setModel(buildTableModel(rs));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
   

    private JPanel createGradeEntryForm() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(CONTENT_BG);
    
        // Create the table
        JTable gradeTable = new JTable();
        gradeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        gradeTable.setAutoCreateRowSorter(true);
        JScrollPane scrollPane = new JScrollPane(gradeTable);
        refreshGradeTable(gradeTable);
    
        // Create the top panel with buttons
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(CONTENT_BG);
    
        // Create the buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        buttonsPanel.setBackground(CONTENT_BG);
    
        // Create the "Add Grade" button
        JButton addGradeButton = new JButton("Add Grade");
    
        // Create the search field and button
        JTextField searchField = new JTextField(15);
        searchField.setToolTipText("Search grades");
        JButton searchButton = new JButton("Search");
    
        // Make all components equal height
        int buttonHeight = addGradeButton.getPreferredSize().height;
        Dimension commonSize = new Dimension(searchField.getPreferredSize().width, buttonHeight);
        searchField.setPreferredSize(commonSize);
        addGradeButton.setPreferredSize(new Dimension(110, 30));
        searchButton.setPreferredSize(new Dimension(searchButton.getPreferredSize().width, buttonHeight));
    
        // Add components to the buttons panel
        buttonsPanel.add(addGradeButton);
    
        // Add the buttons panel to the top panel
        topPanel.add(buttonsPanel, BorderLayout.WEST);
    
        // Create the form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(CONTENT_BG);
        formPanel.setBorder(BorderFactory.createTitledBorder("Grade Details"));
    
        // Create form components
        JTextField studentIdField = new JTextField(20);
        JTextField courseIdField = new JTextField(20);
        JTextField gradeField = new JTextField(20);
        JButton submitButton = new JButton("Submit");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
    
        // Style the buttons
        submitButton.setBackground(Color.LIGHT_GRAY);
        updateButton.setBackground(Color.LIGHT_GRAY);
        deleteButton.setBackground(Color.RED);
    
        submitButton.setForeground(Color.BLACK);
        updateButton.setForeground(Color.BLACK);
        deleteButton.setForeground(Color.BLACK);
    
        // Set preferred size for all components to ensure consistent width
        Dimension fieldSize = new Dimension(250, 30);
        studentIdField.setPreferredSize(fieldSize);
        courseIdField.setPreferredSize(fieldSize);
        gradeField.setPreferredSize(fieldSize);
    
        // Add components to the form panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
    
        formPanel.add(new JLabel("Student ID:"), gbc);
        gbc.gridy++;
        formPanel.add(studentIdField, gbc);
        gbc.gridy++;
        formPanel.add(new JLabel("Course ID:"), gbc);
        gbc.gridy++;
        formPanel.add(courseIdField, gbc);
        gbc.gridy++;
        formPanel.add(new JLabel("Grade:"), gbc);
        gbc.gridy++;
        formPanel.add(gradeField, gbc);
        gbc.gridy++;
    
        // Add buttons in a single row
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(CONTENT_BG);
        buttonPanel.add(submitButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
    
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);
    
        // Create a panel for the table and search field
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.add(scrollPane, BorderLayout.CENTER);
    
        // Add search field to the right of the table
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setBackground(CONTENT_BG);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        tablePanel.add(searchPanel, BorderLayout.SOUTH);
    
        // Create a split pane to hold the table panel and form
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tablePanel, formPanel);
        splitPane.setResizeWeight(0.7); // 70% of space to the table, 30% to the form
    
        // Add components to the main panel
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(splitPane, BorderLayout.CENTER);
    
        // Add action listeners
        addGradeButton.addActionListener(e -> {
            formPanel.setVisible(!formPanel.isVisible());
            splitPane.resetToPreferredSizes();
            mainPanel.revalidate();
            mainPanel.repaint();
        });
    
        submitButton.addActionListener(e -> {
            String studentId = studentIdField.getText();
            String courseId = courseIdField.getText();
            String grade = gradeField.getText();
    
            if (!studentId.isEmpty() && !courseId.isEmpty() && !grade.isEmpty()) {
                Database.addGrade(studentId, courseId, grade);
                JOptionPane.showMessageDialog(Admin.this, "Grade added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshGradeTable(gradeTable);
                clearGradeFields(studentIdField, courseIdField, gradeField);
            } else {
                JOptionPane.showMessageDialog(Admin.this, "Please fill in all fields.", "Incomplete Form", JOptionPane.WARNING_MESSAGE);
            }
        });
    
        updateButton.addActionListener(e -> {
            int selectedRow = gradeTable.getSelectedRow();
            if (selectedRow != -1) {
                int id = (int) gradeTable.getValueAt(selectedRow, 0);
                String studentId = studentIdField.getText();
                String courseId = courseIdField.getText();
                String grade = gradeField.getText();
    
                if (!studentId.isEmpty() && !courseId.isEmpty() && !grade.isEmpty()) {
                    Database.updateGrade(id, studentId, courseId, grade);
                    JOptionPane.showMessageDialog(Admin.this, "Grade updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    refreshGradeTable(gradeTable);
                    clearGradeFields(studentIdField, courseIdField, gradeField);
                } else {
                    JOptionPane.showMessageDialog(Admin.this, "Please fill in all fields.", "Incomplete Form", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(Admin.this, "Please select a grade to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });
    
        deleteButton.addActionListener(e -> {
            int selectedRow = gradeTable.getSelectedRow();
            if (selectedRow != -1) {
                int id = (int) gradeTable.getValueAt(selectedRow, 0);
                int confirm = JOptionPane.showConfirmDialog(Admin.this, "Are you sure you want to delete this grade?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    Database.deleteGrade(id);
                    JOptionPane.showMessageDialog(Admin.this, "Grade deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    refreshGradeTable(gradeTable);
                    clearGradeFields(studentIdField, courseIdField, gradeField);
                }
            } else {
                JOptionPane.showMessageDialog(Admin.this, "Please select a grade to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });
    
        gradeTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = gradeTable.getSelectedRow();
                if (selectedRow != -1) {
                    studentIdField.setText(gradeTable.getValueAt(selectedRow, 1).toString());
                    courseIdField.setText(gradeTable.getValueAt(selectedRow, 2).toString());
                    gradeField.setText(gradeTable.getValueAt(selectedRow, 3).toString());
                }
            }
        });
    
        searchButton.addActionListener(e -> {
            String searchTerm = searchField.getText().toLowerCase();
            TableRowSorter<TableModel> sorter = new TableRowSorter<>(gradeTable.getModel());
            gradeTable.setRowSorter(sorter);
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchTerm));
        });
    
        return mainPanel;
    }
        
    private void clearGradeFields(JTextField studentIdField, JTextField courseIdField, JTextField gradeField) {
        studentIdField.setText("");
        courseIdField.setText("");
        gradeField.setText("");
    }
    
    private void refreshGradeTable(JTable gradeTable) {
        ResultSet rs = Database.getAllGrades();
        try {
            gradeTable.setModel(buildTableModel(rs));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    
    private JPanel createAnnouncementForm() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(CONTENT_BG);
    
        // Create the table
        JTable announcementTable = new JTable();
        announcementTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        announcementTable.setAutoCreateRowSorter(true);
        JScrollPane scrollPane = new JScrollPane(announcementTable);
        refreshAnnouncementTable(announcementTable);
    
        // Create the top panel with buttons
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(CONTENT_BG);
    
        // Create the buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        buttonsPanel.setBackground(CONTENT_BG);
    
        // Create the "Add Announcement" button
        JButton addAnnouncementButton = new JButton("Add Announcement");
    
        // Create the search field and button
        JTextField searchField = new JTextField(15);
        searchField.setToolTipText("Search announcements");
        JButton searchButton = new JButton("Search");
    
        // Make all components equal height
        int buttonHeight = addAnnouncementButton.getPreferredSize().height;
        Dimension commonSize = new Dimension(searchField.getPreferredSize().width, buttonHeight);
        searchField.setPreferredSize(commonSize);
        addAnnouncementButton.setPreferredSize(new Dimension(150, 30));
        searchButton.setPreferredSize(new Dimension(searchButton.getPreferredSize().width, buttonHeight));
    
        // Add components to the buttons panel
        buttonsPanel.add(addAnnouncementButton);
    
        // Add the buttons panel to the top panel
        topPanel.add(buttonsPanel, BorderLayout.WEST);
    
        // Create the form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(CONTENT_BG);
        formPanel.setBorder(BorderFactory.createTitledBorder("Announcement Details"));
    
        // Create form components
        JTextField titleField = new JTextField(20);
        JTextArea contentArea = new JTextArea(5, 20);
        JScrollPane contentScrollPane = new JScrollPane(contentArea);
        JTextField dateField = new JTextField(20);
        JButton submitButton = new JButton("Submit");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
    
        // Style the buttons
        submitButton.setBackground(Color.LIGHT_GRAY);
        updateButton.setBackground(Color.LIGHT_GRAY);
        deleteButton.setBackground(Color.RED);
    
        submitButton.setForeground(Color.BLACK);
        updateButton.setForeground(Color.BLACK);
        deleteButton.setForeground(Color.BLACK);
    
        // Set preferred size for all components to ensure consistent width
        Dimension fieldSize = new Dimension(250, 30);
        titleField.setPreferredSize(fieldSize);
        contentScrollPane.setPreferredSize(new Dimension(250, 100));
        dateField.setPreferredSize(fieldSize);
    
        // Add components to the form panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
    
        formPanel.add(new JLabel("Title:"), gbc);
        gbc.gridy++;
        formPanel.add(titleField, gbc);
        gbc.gridy++;
        formPanel.add(new JLabel("Content:"), gbc);
        gbc.gridy++;
        formPanel.add(contentScrollPane, gbc);
        gbc.gridy++;
        formPanel.add(new JLabel("Date:"), gbc);
        gbc.gridy++;
        formPanel.add(dateField, gbc);
        gbc.gridy++;
    
        // Create a panel for the buttons, aligning them below the input fields
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBackground(CONTENT_BG);
    
        GridBagConstraints buttonGbc = new GridBagConstraints();
        buttonGbc.fill = GridBagConstraints.HORIZONTAL;
        buttonGbc.insets = new Insets(5, 0, 5, 0);
        buttonGbc.weightx = 1.0;
    
        buttonPanel.add(submitButton, buttonGbc);
    
        buttonGbc.gridx = 1;
        buttonGbc.insets = new Insets(5, 5, 5, 5);
        buttonPanel.add(updateButton, buttonGbc);
    
        buttonGbc.gridx = 2;
        buttonGbc.insets = new Insets(5, 0, 5, 0);
        buttonPanel.add(deleteButton, buttonGbc);
    
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);
    
        // Create a panel for the table and search field
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.add(scrollPane, BorderLayout.CENTER);
    
        // Add search field to the right of the table
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setBackground(CONTENT_BG);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        tablePanel.add(searchPanel, BorderLayout.SOUTH);
    
        // Create a split pane to hold the table panel and form
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tablePanel, formPanel);
        splitPane.setResizeWeight(0.7); // 70% of space to the table, 30% to the form
    
        // Add components to the main panel
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(splitPane, BorderLayout.CENTER);
    
        // Add action listeners
        addAnnouncementButton.addActionListener(e -> {
            formPanel.setVisible(!formPanel.isVisible());
            splitPane.resetToPreferredSizes();
            mainPanel.revalidate();
            mainPanel.repaint();
        });
    
        submitButton.addActionListener(e -> {
            String title = titleField.getText();
            String content = contentArea.getText();
            String date = dateField.getText();
    
            if (!title.isEmpty() && !content.isEmpty() && !date.isEmpty()) {
                Database.addAnnouncement(title, content, date);
                JOptionPane.showMessageDialog(Admin.this, "Announcement added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshAnnouncementTable(announcementTable);
                clearAnnouncementFields(titleField, contentArea, dateField);
            } else {
                JOptionPane.showMessageDialog(Admin.this, "Please fill in all fields.", "Incomplete Form", JOptionPane.WARNING_MESSAGE);
            }
        });
    
        updateButton.addActionListener(e -> {
            int selectedRow = announcementTable.getSelectedRow();
            if (selectedRow != -1) {
                int id = (int) announcementTable.getValueAt(selectedRow, 0);
                String title = titleField.getText();
                String content = contentArea.getText();
                String date = dateField.getText();
    
                if (!title.isEmpty() && !content.isEmpty() && !date.isEmpty()) {
                    Database.updateAnnouncement(id, title, content, date);
                    JOptionPane.showMessageDialog(Admin.this, "Announcement updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    refreshAnnouncementTable(announcementTable);
                    clearAnnouncementFields(titleField, contentArea, dateField);
                } else {
                    JOptionPane.showMessageDialog(Admin.this, "Please fill in all fields.", "Incomplete Form", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(Admin.this, "Please select an announcement to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });
    
        deleteButton.addActionListener(e -> {
            int selectedRow = announcementTable.getSelectedRow();
            if (selectedRow != -1) {
                int id = (int) announcementTable.getValueAt(selectedRow, 0);
                int confirm = JOptionPane.showConfirmDialog(Admin.this, "Are you sure you want to delete this announcement?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    Database.deleteAnnouncement(id);
                    JOptionPane.showMessageDialog(Admin.this, "Announcement deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    refreshAnnouncementTable(announcementTable);
                    clearAnnouncementFields(titleField, contentArea, dateField);
                }
            } else {
                JOptionPane.showMessageDialog(Admin.this, "Please select an announcement to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });
    
        announcementTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = announcementTable.getSelectedRow();
                if (selectedRow != -1) {
                    titleField.setText((String) announcementTable.getValueAt(selectedRow, 1));
                    contentArea.setText((String) announcementTable.getValueAt(selectedRow, 2));
                    dateField.setText((String) announcementTable.getValueAt(selectedRow, 3));
                }
            }
        });
    
        searchButton.addActionListener(e -> {
            String searchTerm = searchField.getText().toLowerCase();
            TableRowSorter<TableModel> sorter = new TableRowSorter<>(announcementTable.getModel());
            announcementTable.setRowSorter(sorter);
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchTerm));
        });
    
        return mainPanel;
    }
    
    private void clearAnnouncementFields(JTextField titleField, JTextArea contentArea, JTextField dateField) {
        titleField.setText("");
        contentArea.setText("");
        dateField.setText("");
    }
    
    private void refreshAnnouncementTable(JTable announcementTable) {
        ResultSet rs = Database.getAllAnnouncements();
        try {
            announcementTable.setModel(buildTableModel(rs));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }


    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Close the current Admin window
            this.dispose();
            
            // Open the login window
            SwingUtilities.invokeLater(() -> {
                Login loginFrame = new Login();
                loginFrame.setVisible(true);
            });
        }
    }


    public static void main(String[] args) {
        Database.initializeDatabase();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Admin().setVisible(true);
            }
        });
    }
}