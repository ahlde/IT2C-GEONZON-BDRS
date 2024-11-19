
package Entities;
import Main.config;
import Main.mainfunction;
import java.util.*;

public class officials {
     Scanner sc = new Scanner(System.in);
    config conf = new config();
    mainfunction main = new mainfunction();

    public void officialTrans() {
        boolean res = true;

        do {
            System.out.println("-------------------------------------");
            System.out.println("Barangay Official Management");
            System.out.println("-------------------------------------");
            System.out.println("1. ADD OFFICIAL");
            System.out.println("2. VIEW OFFICIALS");
            System.out.println("3. UPDATE OFFICIAL");
            System.out.println("4. DELETE OFFICIAL");
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
                    sc.next(); // Clear invalid input
                }
            }

            switch (act) {
                case 1: addOfficial();
                        goBackToMenu();
                    break;
                case 2: viewOfficials();
                        goBackToMenu();
                    break;
                case 3 :
                    viewOfficials();
                    updateOfficial();
                    goBackToMenu();
                    break;
                case 4:
                    viewOfficials();
                    deleteOfficial();
                    viewOfficials();
                    goBackToMenu();
                    break;
                case 5: res = false;
                    main.main(new String[]{}); // Back to main menu
                    break;
                default: System.out.println("INVALID OPTION");
               break;
            }

        } while (res);
    }

    public void addOfficial() {
        System.out.println("ADD OFFICIAL");
        sc.nextLine(); // Clear buffer

        System.out.print("First Name: ");
        String firstName = sc.nextLine().trim();

        System.out.print("Last Name: ");
        String lastName = sc.nextLine().trim();

        System.out.print("Position: ");
        String position = sc.nextLine().trim();

        String contactNumber;
        while (true) {
            System.out.print("Contact Number (11 digits): ");
            contactNumber = sc.next();
            if (contactNumber.matches("\\d{11}")) {
                break;
            } else {
                System.out.println("Invalid contact number. Must be 11 digits and numeric.");
            }
        }

        String email;
        while (true) {
            System.out.print("Email: ");
            email = sc.next();
            if (email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                break;
            } else {
                System.out.println("Invalid email format. Please enter a valid email address.");
            }
        }

        String sql = "INSERT INTO brgy_official (first_name, last_name, position, contact_number, email) VALUES (?, ?, ?, ?, ?)";
        conf.addRecord(sql, firstName, lastName, position, contactNumber, email);
        System.out.println("Official added successfully.");
    }

    public void viewOfficials() {
        if (hasOfficials()) {
            String query = "SELECT * FROM brgy_official";
            String[] headers = {"ID", "First Name", "Last Name", "Position", "Contact Number", "Email"};
            String[] columns = {"official_id", "first_name", "last_name", "position", "contact_number", "email"};

            conf.viewRecords(query, headers, columns);
        } else {
            System.out.println("No officials found in the database.");
        }
    }

    private void updateOfficial() {
        int id = getOfficialIdFromUser("UPDATE");

        if (isOfficialIdValid(id)) {
            boolean continueUpdating = true;

            while (continueUpdating) {
                System.out.println("\nWhat do you want to update?");
                System.out.println("1. First Name");
                System.out.println("2. Last Name");
                System.out.println("3. Position");
                System.out.println("4. Contact Number");
                System.out.println("5. Email");
                System.out.println("6. Exit Update");
                System.out.print("Enter choice (1-6): ");

                int choice = 0;
                boolean validChoice = false;

                while (!validChoice) {
                    try {
                        choice = sc.nextInt();
                        if (choice >= 1 && choice <= 6) {
                            validChoice = true;
                        } else {
                            System.out.println("Invalid option. Please enter a number between 1 and 6.");
                        }
                    } catch (InputMismatchException e) {
                        System.out.println("Invalid input. Please enter a number.");
                        sc.next(); // Clear invalid input
                    }
                }

                sc.nextLine(); // Clear buffer

                switch (choice) {
                    case 1: // Update First Name
                        System.out.print("Enter New First Name: ");
                        String firstName = sc.nextLine().trim();
                        String sqlUpdateFirstName = "UPDATE brgy_official SET first_name = ? WHERE official_id = ?";
                        conf.updateRecord(sqlUpdateFirstName, firstName, id);
                      
                        break;

                    case 2: // Update Last Name
                        System.out.print("Enter New Last Name: ");
                        String lastName = sc.nextLine().trim();
                        String sqlUpdateLastName = "UPDATE brgy_official SET last_name = ? WHERE official_id = ?";
                        conf.updateRecord(sqlUpdateLastName, lastName, id);
                        System.out.println("Last Name updated successfully.");
                        break;

                    case 3: // Update Position
                        System.out.print("Enter New Position: ");
                        String position = sc.nextLine().trim();
                        String sqlUpdatePosition = "UPDATE brgy_official SET position = ? WHERE official_id = ?";
                        conf.updateRecord(sqlUpdatePosition, position, id);
                      
                        break;

                    case 4: // Update Contact Number
                        String contactNumber;
                        while (true) {
                            System.out.print("Enter New Contact Number (11 digits): ");
                            contactNumber = sc.nextLine().trim();
                            if (contactNumber.matches("\\d{11}")) {
                                break;
                            } else {
                                System.out.println("Invalid contact number. Must be 11 digits and numeric.");
                            }
                        }
                        String sqlUpdateContact = "UPDATE brgy_official SET contact_number = ? WHERE official_id = ?";
                        conf.updateRecord(sqlUpdateContact, contactNumber, id);
                       
                        break;

                    case 5: // Update Email
                        String email;
                        while (true) {
                            System.out.print("Enter New Email: ");
                            email = sc.nextLine().trim();
                            if (email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                                break;
                            } else {
                                System.out.println("Invalid email format. Please enter a valid email address.");
                            }
                        }
                        String sqlUpdateEmail = "UPDATE brgy_official SET email = ? WHERE official_id = ?";
                        conf.updateRecord(sqlUpdateEmail, email, id);
                       
                        break;

                    case 6: // Exit Update
                        continueUpdating = false;
                        System.out.println("Exiting update process.");
                        break;

                    default:
                        System.out.println("Invalid option.");
                        break;
                }

                // Ask if they want to update another column
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
            System.out.println("Official ID not found.");
        }
    }


    private void deleteOfficial() {
        if (hasOfficials()) { // Check if there are any officials in the database
            int id = getOfficialIdFromUser("DELETE");

            if (isOfficialIdValid(id)) { // Validate if the provided ID exists
                System.out.print("Are you sure you want to delete this official? (yes/no): ");
                String confirmation = sc.nextLine().trim().toLowerCase();

                while (!confirmation.equals("yes") && !confirmation.equals("no")) {
                    System.out.print("Invalid input. Please enter 'yes' or 'no': ");
                    confirmation = sc.nextLine().trim().toLowerCase();
                }

                if (confirmation.equals("yes")) {
                    String deleteQuery = "DELETE FROM brgy_official WHERE official_id = ?";
                    conf.deleteRecord(deleteQuery, id);
                    System.out.println("Official deleted successfully.");
                } else {
                    System.out.println("Deletion cancelled.");
                }
            } else {
                System.out.println("Official ID not found. No records were deleted.");
            }
        } else {
           
        }
    }


    private int getOfficialIdFromUser(String action) {
        int id = -1;
        while (true) {
            System.out.print("Enter Official ID to " + action + ": ");
            try {
                id = sc.nextInt();
                break;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a numeric ID.");
                sc.next(); // Clear invalid input
            }
        }
        return id;
    }

    private boolean isOfficialIdValid(int id) {
        String sql = "SELECT COUNT(*) FROM brgy_official WHERE official_id = ?";
        return conf.getSingleValue(sql, id) > 0;
    }

    private boolean hasOfficials() {
        String sql = "SELECT COUNT(*) FROM brgy_official";
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

