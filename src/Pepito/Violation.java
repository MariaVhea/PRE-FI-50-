package Pepito;

import java.util.Scanner;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;

public class Violation {
    private Scanner input = new Scanner(System.in);
    private config conf = new config();
    private DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private LocalDate currentDate = LocalDate.now();
    private Student_List studentList = new Student_List();

   public void manageViolations() {
    boolean exit = false;
    do {
        System.out.println("\tREGISTER STUDENT VIOLATION");
        System.out.println("---1. Add");
        System.out.println("---2. Edit");
        System.out.println("---3. Delete");
        System.out.println("---4. View");
        System.out.println("---5. Exit");
        System.out.print("Enter Choice: ");

        int choice = getValidChoice(1, 5);
        switch (choice) {
            case 1:
                studentList.view();
                addViolation();
                break;
            case 2:
                studentList.view();
                editViolation();
                break;
            case 3:
                Violation.viewViolations(conf); 
                deleteViolation();
                break;
            case 4:
                Violation.viewViolations(conf); 
                break;
            case 5:
                exit = true;
                break;
        }
    } while (!exit);
}

    private void addViolation() {
        int studentId = getValidStudentId();
        input.nextLine(); 

        System.out.print("Enter Violation Name: ");
        String violationName = input.nextLine();
        System.out.print("Enter Punishment: ");
        String punishment = input.nextLine();
        System.out.print("Enter Violation Status: ");
        String status = input.nextLine(); 

        System.out.print("Enter Violation Date (YYYY-MM-DD): ");
        String dateInput = input.nextLine();
        LocalDate violationDate = LocalDate.parse(dateInput, dateFormat); 

        String sql = "INSERT INTO violation_history (S_Id, v_vname, v_pname, v_status, v_date) VALUES (?, ?, ?, ?, ?)";
        conf.addRecord(sql, studentId, violationName, punishment, status, violationDate); 
    }

    private void editViolation() {
        Violation.viewViolations(conf);
        int violationId = getValidViolationId();

        System.out.print("Enter new Violation Name: ");
        String violationName = input.next();
        System.out.print("Enter new Punishment: ");
        String punishment = input.next();
        System.out.print("Enter new Violation Status: ");
        String status = input.next(); 

        System.out.print("Enter new Violation Date (YYYY-MM-DD): ");
        String dateInput = input.next();
        LocalDate violationDate = LocalDate.parse(dateInput, dateFormat); 

        String sql = "UPDATE violation_history SET v_vname = ?, v_pname = ?, v_status = ?, v_date = ? WHERE v_Id = ?";
        conf.updateRecord(sql, violationName, punishment, status, violationDate, violationId);
    }

    private void deleteViolation() {
        int violationId = getValidViolationId();
        String sql = "DELETE FROM violation_history WHERE v_Id = ?";
        conf.deleteRecord(sql, violationId);
    }

    public static void viewViolations(config conf) {
        String sql = "SELECT * FROM violation_history";
        String[] headers = {"ID", "Violation Name", "Punishment", "Violation Date", "Violation Status"};
        String[] columns = {"v_Id", "v_vname", "v_pname", "v_date", "v_status"};

        try (Connection conn = conf.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            // Display header
            System.out.println("\n=============================================================================================");
            System.out.printf("%-10s %-25s %-25s %-15s %-15s%n", headers[0], headers[1], headers[2], headers[3], headers[4]);
            System.out.println("===============================================================================================");

            boolean hasRecords = false;
            while (rs.next()) {
                hasRecords = true;
                int id = rs.getInt(columns[0]);
                String violationName = rs.getString(columns[1]);
                String punishment = rs.getString(columns[2]);
                String violationDate = rs.getString(columns[3]);
                String violationStatus = rs.getString(columns[4]);

                System.out.printf("%-10d %-25s %-25s %-15s %-15s%n", id, violationName, punishment, violationDate, violationStatus);
            }

            if (!hasRecords) {
                System.out.println("No violation records found.");
            }

            System.out.println("==============================================================================================\n");

        } catch (SQLException e) {
            System.out.println("|\tError retrieving violation records: " + e.getMessage());
        }
    }

    public void violationView(int studentId) {
        String sql = "SELECT * FROM violation_history WHERE S_Id = ?";
        String[] headers = {"ID", "Violation Name", "Punishment", "Violation Date", "Violation Status"};
        String[] columns = {"v_Id", "v_vname", "v_pname", "v_date", "v_status"};

        try (Connection conn = conf.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();

           
            System.out.println("\n=======================================================================================");
            System.out.printf("%-10s %-25s %-25s %-15s %-15s%n", headers[0], headers[1], headers[2], headers[3], headers[4]);
            System.out.println("=========================================================================================");

            boolean hasRecords = false;
            while (rs.next()) {
                hasRecords = true;
                int id = rs.getInt(columns[0]);
                String violationName = rs.getString(columns[1]);
                String punishment = rs.getString(columns[2]);
                String violationDate = rs.getString(columns[3]);
                String violationStatus = rs.getString(columns[4]);

                System.out.printf("%-10d %-25s %-25s %-15s %-15s%n", id, violationName, punishment, violationDate, violationStatus);
            }

            if (!hasRecords) {
                System.out.println("No violation records found for this student.");
            }

            System.out.println("==========================================================================================\n");

        } catch (SQLException e) {
            System.out.println("|\tError retrieving violation records: " + e.getMessage());
        }
    }

    

    private int getValidStudentId() {
        int id = -1;
        while (true) {
            System.out.print("Enter Student ID: ");
            try {
                id = input.nextInt();
                if (doesStudentIdExist(id)) {
                    return id;
                } else if (id == 0) {
                    return 0; 
                }
            } catch (Exception e) {
                input.next(); 
            }
        }
    }

    private int getValidViolationId() {
        int id = -1;
        while (true) {
            System.out.print("Enter Violation ID: ");
            try {
                id = input.nextInt();
                if (doesViolationIdExist(id)) {
                    return id;
                } else if (id == 0) {
                    return 0; 
                }
            } catch (Exception e) {
                input.next(); 
            }
        }
    }

    private boolean doesStudentIdExist(int id) {
        String query = "SELECT COUNT(*) FROM Student_List WHERE S_Id = ?";
        try (Connection conn = conf.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.out.println("|\tError checking Student ID: " + e.getMessage());
            return false;
        }
    }

    private boolean doesViolationIdExist(int id) {
        String query = "SELECT COUNT(*) FROM violation_history WHERE v_Id = ?";
        try (Connection conn = conf.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.out.println("|\tError checking Violation ID: " + e.getMessage());
            return false;
        }
    }

    private int getValidChoice(int min, int max) {
        int choice = -1;
        while (true) {
            try {
                choice = input.nextInt();
                if (choice >= min && choice <= max) {
                    return choice;
                } else {
                    System.out.print("Invalid choice. Please enter again: ");
                }
            } catch (Exception e) {
                input.next(); 
                System.out.print("Invalid input. Please enter again: ");
            }
        }
    }
}