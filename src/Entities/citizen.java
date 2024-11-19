package Entities;

import Main.config;
import java.util.InputMismatchException;
import java.util.Scanner;
import Main.mainfunction;

public class citizen {
    Scanner sc = new Scanner(System.in);
    config conf = new config();
    mainfunction main = new mainfunction();
    public void citizenTrans() {
        boolean res = true;

        do {
            System.out.println(".....................................");
            System.out.println("    Citizen");
            System.out.println(".....................................");
            System.out.println("1. ADD CITIZEN");
            System.out.println("2. VIEW CITIZEN");
            System.out.println("3. UPDATE CITIZEN");
            System.out.println("4. DELETE CITIZEN");
            System.out.println("5. BACK TO MAIN MENU");
            System.out.println("-------------------------------------");

            int act = 0;
            boolean validInput = false;
            while (!validInput) {
                System.out.print("Enter Action (1-5): ");
                try {
                    act = sc.nextInt();
                    if (act >= 1 && act <= 5) {
                        validInput = true;
                    } else {
                        System.out.println("Invalid option. Please enter a number between 1 and 5.");
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a number.");
                    sc.next();
                }
            }

            switch (act) {
                case 1: addCitizen();
                        goBackToMenu();
                    break;
                case 2: viewCitizen();
                        goBackToMenu();
                    break;
                case 3 :
                    viewCitizen();
                    updateCitizen();
                    goBackToMenu();
                    break;
                case 4:
                    viewCitizen();
                    deleteCitizen();
                    viewCitizen();
                    goBackToMenu();
                    break;
                case 5: res = false;
                    main.main(new String[]{});
                    break;
                default: System.out.println("INVALID OPTION");
               break;
            }

        } while (res);
    }

    public void addCitizen() {
        System.out.println("ADD");
        sc.nextLine(); 

        System.out.print("First Name: ");
        String fname = sc.nextLine().trim();

        System.out.print("Last Name: ");
        String lname = sc.nextLine().trim();

        String contnum;
        while (true) {
            System.out.print("Citizen Contact Num (11 digits): ");
            contnum = sc.next();
            if (contnum.matches("\\d{11}")) {
                break;
            } else {
                System.out.println("Invalid contact number. Must be 11 digits and numeric.");
            }
        }

        String dob;
        while (true) {
            System.out.print("Date of Birth (MM/DD/YYYY): ");
            dob = sc.next();
            if (dob.matches("\\d{2}/\\d{2}/\\d{4}")) {
                break;
            } else {
                System.out.println("Invalid date format. Please use MM/DD/YYYY.");
            }
        }

        String sex;
        while (true) {
            System.out.print("Sex (M/F): ");
            sex = sc.next().toUpperCase();
            if (sex.equals("M") || sex.equals("F")) {
                break;
            } else {
                System.out.println("Invalid input. Please enter 'M' for male or 'F' for female.");
            }
        }

        sc.nextLine(); 
        System.out.print("Purok: ");
        String purok = sc.nextLine().trim();

        String sql = "INSERT INTO tbl_citizen (f_name, l_name, contact, date_of_birth, sex, purok) VALUES (?, ?, ?, ?, ?, ?)";
        conf.addRecord(sql, fname, lname, contnum, dob, sex, purok);
        System.out.println("Citizen added successfully.");
    }

    public void viewCitizen() {
        if (hasCitizens()) {
            String query = "SELECT * FROM tbl_citizen";
            String[] headers = {"ID", "First_Name", "Last_Name", "Contact Num", "Date of Birth", "Sex", "Purok"};
            String[] columns = {"ctzn_id", "f_name", "l_name", "contact", "date_of_birth", "sex", "purok"};

            conf.viewRecords(query, headers, columns);
        } else {
            System.out.println("No citizens found in the database.");
        }
    }

    private void updateCitizen() {
        int id = getCitizenIdFromUser("UPDATE");

        if (isCitizenIdValid(id)) {
            boolean continueUpdating = true;

            while (continueUpdating) {
                System.out.println("\nWhat do you want to update?");
                System.out.println("1. First Name");
                System.out.println("2. Last Name");
                System.out.println("3. Contact Number");
                System.out.println("4. Purok");
                System.out.println("5. Exit Update");
                System.out.print("Enter choice (1-5): ");

                int choice = 0;
                boolean validChoice = false;

                while (!validChoice) {
                    try {
                        choice = sc.nextInt();
                        if (choice >= 1 && choice <= 5) {
                            validChoice = true;
                        } else {
                            System.out.println("Invalid option. Please enter a number between 1 and 5.");
                        }
                    } catch (InputMismatchException e) {
                        System.out.println("Invalid input. Please enter a number.");
                        sc.next(); 
                    }
                }

                sc.nextLine(); 

                switch (choice) {
                    case 1: 
                        System.out.print("Enter New First Name: ");
                        String firstName = sc.nextLine().trim();
                        String sqlUpdateFirstName = "UPDATE tbl_citizen SET f_name = ? WHERE ctzn_id = ?";
                        conf.updateRecord(sqlUpdateFirstName, firstName, id);
                       
                        break;

                    case 2:
                        System.out.print("Enter New Last Name: ");
                        String lastName = sc.nextLine().trim();
                        String sqlUpdateLastName = "UPDATE tbl_citizen SET l_name = ? WHERE ctzn_id = ?";
                        conf.updateRecord(sqlUpdateLastName, lastName, id);
                       
                        break;

                    case 3: 
                        String contactNumber;
                        while (true) {
                            System.out.print("Enter New Contact Number (11 digits): ");
                            contactNumber = sc.next();
                            if (contactNumber.matches("\\d{11}")) {
                                break;
                            } else {
                                System.out.println("Invalid contact number. Must be 11 digits and numeric.");
                            }
                        }
                        String sqlUpdateContact = "UPDATE tbl_citizen SET contact = ? WHERE ctzn_id = ?";
                        conf.updateRecord(sqlUpdateContact, contactNumber, id);
                       
                        break;

                    case 4: 
                        System.out.print("Enter New Purok: ");
                        String purok = sc.nextLine().trim();
                        String sqlUpdatePurok = "UPDATE tbl_citizen SET purok = ? WHERE ctzn_id = ?";
                        conf.updateRecord(sqlUpdatePurok, purok, id);
                       
                        break;

                    case 5: 
                        continueUpdating = false;
                        System.out.println("Exiting update process.");
                        break;

                    default:
                        System.out.println("Invalid option.");
                        break;
                }

               
                if (continueUpdating) {
                    System.out.print("Do you want to update another? (yes/no): ");
                    String response = sc.nextLine().trim().toLowerCase();

                    while (!response.equals("yes") && !response.equals("no")) {
                        System.out.print("Invalid input. Please enter 'yes' or 'no': ");
                        response = sc.nextLine().trim().toLowerCase();
                    }

                    if (response.equals("no")) {
                        continueUpdating = false;
                        System.out.println("Exiting update process.");
                    }
                }
            }
        } else {
            System.out.println("Citizen ID not found.");
        }
    }


    private void deleteCitizen() {
        if (hasCitizens()) { 
            int id = getCitizenIdFromUser("DELETE");

            if (isCitizenIdValid(id)) { 
                System.out.print("Are you sure you want to delete this citizen? (yes/no): ");
                String confirmation = sc.nextLine().trim().toLowerCase();

                while (!confirmation.equals("yes") && !confirmation.equals("no")) {
                    System.out.print("Invalid input. Please enter 'yes' or 'no': ");
                    confirmation = sc.nextLine().trim().toLowerCase();
                }

                if (confirmation.equals("yes")) {
                    String deleteQuery = "DELETE FROM tbl_citizen WHERE ctzn_id = ?";
                    conf.deleteRecord(deleteQuery, id);
                    System.out.println("Citizen deleted successfully.");
                } else {
                    System.out.println("Deletion cancelled.");
                }
            } else {
                System.out.println("Citizen ID not found. No records were deleted.");
            }
        } else {
          
        }
    }


    private int getCitizenIdFromUser(String action) {
        int id = -1;
        while (true) {
            System.out.print("Enter Citizen ID to " + action + ": ");
            try {
                id = sc.nextInt();
                break;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a numeric ID.");
                sc.next(); 
            }
        }
        return id;
    }

    private boolean isCitizenIdValid(int id) {
        String sql = "SELECT COUNT(*) FROM tbl_citizen WHERE ctzn_id = ?";
        return conf.getSingleValue(sql, id) > 0;
    }

   
    private boolean hasCitizens() {
        String sql = "SELECT COUNT(*) FROM tbl_citizen";
        return conf.getSingleValue(sql) > 0;
    }

    private boolean goBackToMenu() {
        sc.nextLine(); 
        System.out.print("Do you want to go back to the menu? (yes/no): ");
        String response = sc.nextLine().trim().toLowerCase();

        while (!response.equals("yes") && !response.equals("no")) {
            System.out.print("Invalid input. Please enter 'yes' or 'no': ");
            response = sc.nextLine().trim().toLowerCase();
        }
        return response.equals("yes");
    }
}
