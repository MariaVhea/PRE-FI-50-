package Pepito;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        boolean exit = true;

        do {
            System.out.println("==============================================");
            System.out.println("\tVIOLATION TRACKER");
            System.out.println("==============================================");
            System.out.println("---1. Student List");
            System.out.println("---2. Violation");
            System.out.println("---3. Report");
            System.out.println("---4. Exit");
            System.out.println("==============================================");
            System.out.print("Enter Choice: ");
            int choice = getValidChoice(input, 1, 4);

            switch (choice) {
                case 1:
                    Student_List studentList = new Student_List();
                    studentList.list();
                    break;
                case 2:
                    Violation violationManager = new Violation();
                    violationManager.manageViolations(); 
                    break;
                case 3:
                    Record reportManager = new Record();
                    reportManager.record();
                    break;
                case 4:
                    exit = confirmExit(input);
                    break;
            }
        } while (exit);

        input.close(); 
    }

    private static int getValidChoice(Scanner input, int min, int max) {
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

    private static boolean confirmExit(Scanner input) {
        System.out.print("Do you really want to exit (Yes/No): ");
        String exitResponse;
        while (true) {
            try {
                exitResponse = input.next();
                if (exitResponse.equalsIgnoreCase("yes")) {
                    return false;
                } else {
                    return true; 
                }
            } catch (Exception e) {
                input.next();
                System.out.print("Invalid response. Please enter Yes or No: ");
            }
        }
    }
}