package Pepito;

import java.util.Scanner;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Record {
    Scanner input = new Scanner(System.in);
    config conf = new config();
    Student_List sl = new Student_List();
    Violation v = new Violation();
    
    public void record(){
        boolean exit = true;
        do{
            System.out.println("\tReport");
            System.out.println("---1. General Report");
            System.out.println("---2. Individual Report");
            System.out.println("---3. Exit");
            System.out.print("Enter Choice: ");
            int choice;
            while(true){
                try{
                    choice = input.nextInt();
                    if(choice>0 && choice<4){
                        break;
                    }else{
                        System.out.print("Again: ");
                    }
                }catch(Exception e){
                    input.next();
                    System.out.print("Again: ");
                }
            }
            switch(choice){
                case 1:
                    general();
                    break;
                case 2:
                   sl.view();
                    individual();
                    break;
                default:
                    exit = false;
                    break;
            }
        }while(exit);
    }
    public void general(){
        sl.view();
        Violation.viewViolations(conf);
    }
    public void individual(){
        boolean exit = true;
        System.out.println("\t--Select Student--");
        System.out.print("Enter ID to View: ");
        int id;
        while(true){
            try{
                id = input.nextInt();
                if(doesIDexists(id, conf)){
                    break;
                }else if(id == 0){
                    exit = false;
                    break;
                }else{
                    System.out.print("Enter ID to View Again: ");
                }
            }catch(Exception e){
                input.next();
                System.out.print("Enter ID to View Again: ");
            }
        }
        while(exit){
            Rview(id);
            exit = false;
        }
    }
    
public void Rview(int id) {
    String tbl_view = "SELECT s.S_Id, s.S_fname, s.S_mname, s.S_lname, s.S_gender, s.S_bdate, " +
                     "v.v_Id, v.v_vname, v.v_pname, v.v_date, v.v_status " +
                     "FROM Student_List s " +
                     "LEFT JOIN violation_history v ON s.S_Id = v.S_Id " +
                     "WHERE s.S_Id = ?";
    String[] tbl_Headers = {"ID", "First Name", "Middle Name", "Last Name", "Gender", "Birth Date",
                           "Violation ID", "Violation Name", "Violation Punishment", "Violation Date", "Violation Status"};
    String[] tbl_Columns = {"S_Id", "S_fname", "S_mname", "S_lname", "S_gender", "S_bdate",
                           "v_Id", "v_vname", "v_pname", "v_date", "v_status"};

    try (Connection conn = conf.connectDB();
         PreparedStatement pstmt = conn.prepareStatement(tbl_view)) {

        pstmt.setInt(1, id);
        ResultSet rs = pstmt.executeQuery();

        List<List<String>> records = new ArrayList<>();

        
        List<String> headers = new ArrayList<>();
        for (String header : tbl_Headers) {
            headers.add(header);
        }
        records.add(headers);

        
        boolean hasRecords = false;
        while (rs.next()) {
            hasRecords = true;
            List<String> row = new ArrayList<>();
            for (String colName : tbl_Columns) {
                row.add(rs.getString(colName) != null ? rs.getString(colName) : "No records yet");
            }
            records.add(row);
        }

        if (!hasRecords) {
            System.out.println("No violation records found for the selected student.");
            return;
        }

        
        int[] columnWidths = new int[tbl_Headers.length];
        for (List<String> record : records) {
            for (int i = 0; i < record.size(); i++) {
                columnWidths[i] = Math.max(columnWidths[i], record.get(i).length());
            }
        }

       
        for (int i = 0; i < tbl_Headers.length; i++) {
            System.out.print("| " + String.format("%-" + (columnWidths[i] + 3) + "s", tbl_Headers[i])); 
        }
        System.out.println("|");

       
        for (int i = 1; i < records.size(); i++) { 
            List<String> record = records.get(i);
            for (int j = 0; j < record.size(); j++) {
                System.out.print("| " + String.format("%-" + (columnWidths[j] + 3) + "s", record.get(j))); 
            }
            System.out.println("|");
        }

    } catch (SQLException e) {
        System.out.println("|\tError retrieving student and violation records: " + e.getMessage());
    }
}
    
    private boolean doesIDexists(int id, config conf) {
        String query = "SELECT COUNT(*) FROM Student_List Where S_Id = ?";
        try (Connection conn = conf.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println("|\tError checking Report ID: " + e.getMessage());
        }
        return false;
    }
   
}
