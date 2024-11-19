package Entities;

import Main.config;
import java.util.Scanner;

public class request {
    config db = new config();
    Scanner sc = new Scanner(System.in);
    citizen ct = new citizen();

    public void req() {
        boolean res = true;

        do {
            System.out.println(".....................................");
            System.out.println("    Request a Document");
            System.out.println(".....................................");
            System.out.println("1. ADD REQUEST");
            System.out.println("2. VIEW REQUESTS");
            System.out.println("3. UPDATE REQUEST");
            System.out.println("4. DELETE REQUEST");
            System.out.println("5. EXIT");
            System.out.println("-------------------------------------");

            System.out.print("Enter Action: ");
            int action = sc.nextInt();

            switch (action) {
                case 1:
                    addRequest();
                    break;
                case 2:
                    viewRequests();
                    break;
                case 3:
                    updateRequest();
                    break;
                case 4:
                    deleteRequest();
                    break;
                case 5:
                    res = false;
                    break;
                default:
                    System.out.println("Invalid Input");
                    break;
            }

        } while (res);
    }

    public void addRequest() {
        System.out.println("\nADD A NEW DOCUMENT REQUEST\n");
        ct.viewCitizen();

        String citizenId = "";
        boolean idExists = false;
        while (!idExists) {
            System.out.print("Enter Citizen ID: ");
            citizenId = sc.next();

            if (citIdExists(citizenId)) {
                idExists = true;
            } else {
                System.out.println("Invalid ID or ID not found. Please try again.");
            }
        }

        System.out.println("\nSelect the Document Type:");
        System.out.println("1. Barangay Clearance");
        System.out.println("2. Certificate of Residency");
        System.out.println("3. Certificate of Indigency");
        System.out.print("Enter your choice (1-3): ");
        int docTypeChoice = sc.nextInt();

        String documentType = "";
        switch (docTypeChoice) {
            case 1:
                documentType = "Barangay Clearance";
                break;
            case 2:
                documentType = "Certificate of Residency";
                break;
            case 3:
                documentType = "Certificate of Indigency";
                break;
            default:
                System.out.println("Invalid choice. Request cancelled.");
                return;
        }

        sc.nextLine(); // Consume the newline character
        System.out.print("Enter the purpose of the request: ");
        String purpose = sc.nextLine();

        // Fetch the list of barangay officials from the brgy_official table
        System.out.println("\nSelect the Barangay Official Issuing the Document:");
        String query = "SELECT official_id, first_name, last_name, position FROM brgy_official";
        db.viewRecords(query, new String[]{"Official ID", "First Name", "Last Name", "Position"}, new String[]{"official_id", "first_name", "last_name", "position"});

        System.out.print("Enter the official ID: ");
        int officialId = sc.nextInt();

        // Validate that the official_id exists in the brgy_official table (primary key check)
        if (!officialIdExists(officialId)) {
            System.out.println("Invalid official ID. Request cancelled.");
            return;
        }

        boolean requirementsMet = validateRequirements(documentType);
        if (!requirementsMet) {
            System.out.println("Request cannot proceed. Requirements not met.");
            return;
        }

        // Insert the request with the valid official_id
        String sql = "INSERT INTO tbl_request (ctzn_id, doc_type, req_date, req_status, purpose, issued_by) " +
                     "VALUES (?, ?, current_timestamp, 'Pending', ?, ?)";
        db.addRecord(sql, citizenId, documentType, purpose, officialId);  // Pass the officialId to the query
        System.out.println("DOCUMENT REQUEST ADDED SUCCESSFULLY.");
    }



    public void viewRequests() {
        String query = "SELECT r.req_id, z.f_name, z.l_name, r.doc_type, r.req_date, r.req_status, r.purpose, o.first_name || ' ' || o.last_name AS official_name " +
                       "FROM tbl_request r INNER JOIN tbl_citizen z ON r.ctzn_id = z.ctzn_id " +
                       "INNER JOIN brgy_official o ON r.issued_by = o.official_id";
        String[] headers = {"ID", "First Name", "Last Name", "Document Type", "Date and Time", "Status", "Purpose", "Issued By"};
        String[] columns = {"req_id", "f_name", "l_name", "doc_type", "req_date", "req_status", "purpose", "official_name"};

        db.viewRecords(query, headers, columns);
    }

    private void updateRequest() {
        System.out.println("\nUPDATE AN EXISTING DOCUMENT REQUEST\n");

        String requestId = "";
        boolean idExists = false;

        while (!idExists) {
            System.out.print("Enter Request ID: ");
            requestId = sc.next();

            if (reqIdExists(requestId)) {
                idExists = true;
            } else {
                System.out.println("Invalid Request ID or ID not found. Please try again.");
            }
        }

        boolean continueUpdating = true;

        while (continueUpdating) {
            System.out.println("\nWhat would you like to update?");
            System.out.println("1. Document Type");
            System.out.println("2. Purpose");
            System.out.println("3. Issued By");
            System.out.println("4. Exit Update");
            System.out.print("Enter your choice (1-4): ");

            int choice = 0;
            boolean validChoice = false;

            while (!validChoice) {
                try {
                    choice = sc.nextInt();
                    if (choice >= 1 && choice <= 4) {
                        validChoice = true;
                    } else {
                        System.out.println("Invalid option. Please enter a number between 1 and 4.");
                    }
                } catch (Exception e) {
                    System.out.println("Invalid input. Please enter a number.");
                    sc.next();
                }
            }

            sc.nextLine();

            switch (choice) {
                case 1:
                    System.out.println("\nSelect the New Document Type:");
                    System.out.println("1. Barangay Clearance");
                    System.out.println("2. Certificate of Residency");
                    System.out.println("3. Certificate of Indigency");
                    System.out.print("Enter your choice (1-3): ");
                    int docTypeChoice = sc.nextInt();

                    String newDocType = "";
                    switch (docTypeChoice) {
                        case 1:
                            newDocType = "Barangay Clearance";
                            break;
                        case 2:
                            newDocType = "Certificate of Residency";
                            break;
                        case 3:
                            newDocType = "Certificate of Indigency";
                            break;
                        default:
                            System.out.println("Invalid choice. Skipping update for Document Type.");
                            continue;
                    }

                    String sqlUpdateDocType = "UPDATE tbl_request SET doc_type = ? WHERE req_id = ?";
                    db.updateRecord(sqlUpdateDocType, newDocType, requestId);
                    System.out.println("Document Type updated successfully.");
                    break;

                case 2:
                    System.out.print("Enter New Purpose: ");
                    String newPurpose = sc.nextLine();

                    String sqlUpdatePurpose = "UPDATE tbl_request SET purpose = ? WHERE req_id = ?";
                    db.updateRecord(sqlUpdatePurpose, newPurpose, requestId);
                    System.out.println("Purpose updated successfully.");
                    break;

                case 3:
                    System.out.print("Enter New Issued By: ");
                    String newIssuedBy = sc.nextLine();

                    String sqlUpdateIssuedBy = "UPDATE tbl_request SET issued_by = ? WHERE req_id = ?";
                    db.updateRecord(sqlUpdateIssuedBy, newIssuedBy, requestId);
                    System.out.println("Issued By updated successfully.");
                    break;

                case 4:
                    continueUpdating = false;
                    System.out.println("Exiting update process.");
                    break;

                default:
                    System.out.println("Invalid option.");
                    break;
            }

            if (continueUpdating) {
                System.out.print("Do you want to update another field? (yes/no): ");
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
    }

    private void deleteRequest() {
        System.out.println("\nDELETE A DOCUMENT REQUEST\n");

        String requestId = "";
        boolean idExists = false;

        while (!idExists) {
            System.out.print("Enter Request ID to delete: ");
            requestId = sc.next();

            if (reqIdExists(requestId)) {
                idExists = true;
            } else {
                System.out.println("Invalid Request ID or ID not found. Please try again.");
            }
        }

        System.out.print("Are you sure you want to delete this request? (yes/no): ");
        sc.nextLine();
        String confirmation = sc.nextLine().trim().toLowerCase();

        if (confirmation.equals("yes")) {
            String deleteQuery = "DELETE FROM tbl_request WHERE req_id = ?";
            db.deleteRecord(deleteQuery, requestId);
            System.out.println("DOCUMENT REQUEST DELETED SUCCESSFULLY.");
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    private boolean citIdExists(String citizenId) {
        String query = "SELECT COUNT(*) FROM tbl_citizen WHERE ctzn_id = ?";
        return db.getCount(query, citizenId) > 0;
    }

    private boolean reqIdExists(String requestId) {
        String query = "SELECT COUNT(*) FROM tbl_request WHERE req_id = ?";
        return db.getCount(query, requestId) > 0;
    }

    private boolean officialIdExists(int officialId) {
        String query = "SELECT COUNT(*) FROM brgy_official WHERE official_id = ?";
        return db.getCount(query, officialId) > 0;
    }

    private boolean validateRequirements(String documentType) {
        return true;
    }
}
