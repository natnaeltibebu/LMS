import java.sql.*;

public class Database {
    private static final String DB_URL = "jdbc:sqlite:db.db";
    private static String currentUserId;

    public static void setCurrentUserId(String userId) {
        currentUserId = userId;
    }

    public static String getCurrentUserId() {
        return currentUserId;
    }

    public static void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            
            // Create users table
            String sql = "CREATE TABLE IF NOT EXISTS users (" +
                         "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                         "username TEXT NOT NULL UNIQUE," +
                         "password TEXT NOT NULL," +
                         "user_type TEXT NOT NULL)";
            stmt.execute(sql);
    
            // Create courses table
            sql = "CREATE TABLE IF NOT EXISTS courses (" +
                  "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                  "name TEXT NOT NULL," +
                  "chapter TEXT NOT NULL," +
                  "lesson TEXT NOT NULL," +
                  "resource_type TEXT NOT NULL," +
                  "file_path TEXT NOT NULL)";
            stmt.execute(sql);
    
            // Create schedule table
            sql = "CREATE TABLE IF NOT EXISTS schedule (" +
                  "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                  "course TEXT NOT NULL," +
                  "day TEXT NOT NULL," +
                  "start_time TEXT NOT NULL," +
                  "end_time TEXT NOT NULL," +
                  "room TEXT NOT NULL)";
            stmt.execute(sql);

            // Create announcements table
            sql = "CREATE TABLE IF NOT EXISTS announcements (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title TEXT NOT NULL," +
                "content TEXT NOT NULL," +
                "date TEXT NOT NULL)";
            stmt.execute(sql);

            sql = "CREATE TABLE IF NOT EXISTS grades (" +
                 "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                 "student_id INTEGER," +
                 "course_id INTEGER," +
                 "grade REAL," +
                 "FOREIGN KEY (student_id) REFERENCES users(id)," +
                 "FOREIGN KEY (course_id) REFERENCES courses(id))";
            stmt.executeUpdate(sql);
            
            System.out.println("Database initialized successfully.");
        } catch (SQLException e) {
            System.out.println("Error initializing database: " + e.getMessage());
        }
    }

    public static boolean addUser(String username, String password, String userType) {
        if (userExists(username)) {
            System.out.println("User " + username + " already exists.");
            return false;
        }
    
        String sql = "INSERT INTO users (username, password, user_type) VALUES (?, ?, ?)";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, userType);
            pstmt.executeUpdate();
            
            System.out.println("User " + username + " added successfully.");
            return true;
        } catch (SQLException e) {
            System.out.println("Error adding user: " + e.getMessage());
            return false;
        }
    }

    public static ResultSet getUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        
        try {
            Connection conn = DriverManager.getConnection(DB_URL);
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            return pstmt.executeQuery();
        } catch (SQLException e) {
            System.out.println("Error getting user: " + e.getMessage());
            return null;
        }
    }

    public static boolean authenticateUser(String username, String password) {
        String sql = "SELECT id FROM users WHERE username = ? AND password = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String userId = rs.getString("id");
                setCurrentUserId(userId);
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Error authenticating user: " + e.getMessage());
            return false;
        }
    }

    public static boolean userExists(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            return rs.next(); // If there's a matching user, return true
        } catch (SQLException e) {
            System.out.println("Error checking if user exists: " + e.getMessage());
            return false;
        }
    }

    // Add this method to initialize default users
    public static void initializeDefaultUsers() {
        if (!userExists("admin")) {
            addUser("admin", "admin123", "admin");
        }
        if (!userExists("user")) {
            addUser("user", "user123", "user");
        }
        if (!userExists("user2")) {
            addUser("user2", "user321", "user");
        }
    }

    // In Database.java
    public static void addCourse(String name, String chapter, String lesson, String resourceType, String filePath) {
        String sql = "INSERT INTO courses (name, chapter, lesson, resource_type, file_path) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, name);
            pstmt.setString(2, chapter);
            pstmt.setString(3, lesson);
            pstmt.setString(4, resourceType);
            pstmt.setString(5, filePath);
            pstmt.executeUpdate();
            
            System.out.println("Course added successfully.");
        } catch (SQLException e) {
            System.out.println("Error adding course: " + e.getMessage());
        }
    }

    public static ResultSet getAllCourses() {
        String sql = "SELECT * FROM courses";
        
        try {
            Connection conn = DriverManager.getConnection(DB_URL);
            Statement stmt = conn.createStatement();
            return stmt.executeQuery(sql);
        } catch (SQLException e) {
            System.out.println("Error getting courses: " + e.getMessage());
            return null;
        }
    }

    public static void updateCourse(int id, String name, String chapter, String lesson, String resourceType, String filePath) {
        String sql = "UPDATE courses SET name = ?, chapter = ?, lesson = ?, resource_type = ?, file_path = ? WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, name);
            pstmt.setString(2, chapter);
            pstmt.setString(3, lesson);
            pstmt.setString(4, resourceType);
            pstmt.setString(5, filePath);
            pstmt.setInt(6, id);
            pstmt.executeUpdate();
            
            System.out.println("Course updated successfully.");
        } catch (SQLException e) {
            System.out.println("Error updating course: " + e.getMessage());
        }
    }

    public static void deleteCourse(int id) {
        String sql = "DELETE FROM courses WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            
            System.out.println("Course deleted successfully.");
        } catch (SQLException e) {
            System.out.println("Error deleting course: " + e.getMessage());
        }
    }

    public static void addSchedule(String course, String day, String startTime, String endTime, String room) {
        String sql = "INSERT INTO schedule(course, day, start_time, end_time, room) VALUES(?,?,?,?,?)";
    
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
    
            pstmt.setString(1, course);
            pstmt.setString(2, day);
            pstmt.setString(3, startTime);
            pstmt.setString(4, endTime);
            pstmt.setString(5, room);
            pstmt.executeUpdate();
    
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static ResultSet getAllSchedules() {
        String sql = "SELECT * FROM schedule";
        
        try {
            Connection conn = DriverManager.getConnection(DB_URL);
            Statement stmt = conn.createStatement();
            return stmt.executeQuery(sql);
        } catch (SQLException e) {
            System.out.println("Error getting schedules: " + e.getMessage());
            return null;
        }
    }

    public static void updateSchedule(int id, String course, String day, String startTime, String endTime, String room) {
        String sql = "UPDATE schedule SET course = ?, day = ?, start_time = ?, end_time = ?, room = ? WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, course);
            pstmt.setString(2, day);
            pstmt.setString(3, startTime);
            pstmt.setString(4, endTime);
            pstmt.setString(5, room);
            pstmt.setInt(6, id);
            pstmt.executeUpdate();
            
            System.out.println("Schedule updated successfully.");
        } catch (SQLException e) {
            System.out.println("Error updating schedule: " + e.getMessage());
        }
    }
    
    public static void deleteSchedule(int id) {
        String sql = "DELETE FROM schedule WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            
            System.out.println("Schedule deleted successfully.");
        } catch (SQLException e) {
            System.out.println("Error deleting schedule: " + e.getMessage());
        }
    }
    
    public static void addGrade(String studentId, String courseId, String grade) {
        String sql = "INSERT INTO grades (student_id, course_id, grade) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            pstmt.setString(2, courseId);
            pstmt.setString(3, grade);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static void updateGrade(int id, String studentId, String courseId, String grade) {
        String sql = "UPDATE grades SET student_id = ?, course_id = ?, grade = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            pstmt.setString(2, courseId);
            pstmt.setString(3, grade);
            pstmt.setInt(4, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static void deleteGrade(int id) {
        String sql = "DELETE FROM grades WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static ResultSet getAllGrades() {
        String sql = "SELECT * FROM grades";
        try {
            Connection conn = DriverManager.getConnection(DB_URL);
            PreparedStatement pstmt = conn.prepareStatement(sql);
            return pstmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    

    public static ResultSet getGradesForCurrentUser() {
        if (currentUserId == null) {
            throw new IllegalStateException("Current user ID is not set");
        }
        String sql = "SELECT * FROM grades WHERE student_id = ?";
        try {
            Connection conn = DriverManager.getConnection(DB_URL);
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, currentUserId);
            return pstmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void addAnnouncement(String title, String content, String date) {
        String sql = "INSERT INTO announcements (title, content, date) VALUES (?, ?, ?)";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, title);
            pstmt.setString(2, content);
            pstmt.setString(3, date);
            pstmt.executeUpdate();
            
            System.out.println("Announcement added successfully.");
        } catch (SQLException e) {
            System.out.println("Error adding announcement: " + e.getMessage());
        }
    }

    public static ResultSet getAllAnnouncements() {
        String sql = "SELECT * FROM announcements ORDER BY date DESC";
        
        try {
            Connection conn = DriverManager.getConnection(DB_URL);
            Statement stmt = conn.createStatement();
            return stmt.executeQuery(sql);
        } catch (SQLException e) {
            System.out.println("Error getting announcements: " + e.getMessage());
            return null;
        }
    }
    public static void updateAnnouncement(int id, String title, String content, String date) {
        String sql = "UPDATE announcements SET title = ?, content = ?, date = ? WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, title);
            pstmt.setString(2, content);
            pstmt.setString(3, date);
            pstmt.setInt(4, id);
            pstmt.executeUpdate();
            
            System.out.println("Announcement updated successfully.");
        } catch (SQLException e) {
            System.out.println("Error updating announcement: " + e.getMessage());
        }
    }
    
    public static void deleteAnnouncement(int id) {
        String sql = "DELETE FROM announcements WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            
            System.out.println("Announcement deleted successfully.");
        } catch (SQLException e) {
            System.out.println("Error deleting announcement: " + e.getMessage());
        }
    }
}