package Entities;

import Main.config;
import java.util.Scanner;

public class approval {
    config db = new config();
    Scanner sc = new Scanner(System.in);
    citizen ct = new citizen();
    request rq = new request();
    public void approve() {
        boolean res = true;

        do {
            System.out.println(".....................................");
            System.out.println("    Approve a Request");
            System.out.println(".....................................");
            System.out.println("1. Approve a Request");
            System.out.println("2. Reject a Request");
            System.out.println("3. Generate General Report");
            System.out.println("4. Generate Individual Report");
            System.out.println("5. Exit");
            System.out.println("-------------------------------------");

            System.out.print("Enter Action: ");
            int choice = getIntInput();

            switch (choice) {
                case 1:
                    updateRequestStatus("Approved");
                    break;
                case 2:
                    updateRequestStatus("Rejected");
                    break;
                case 3:
                    generateFilteredGeneralReport();
                    break;
                case 4:
                    generateIndividualReport();
                    break;
                case 5:
                    res = false;
                    break;
                
                default:
                    System.out.println("Invalid Input. Please choose a valid option.");
                    break;
            }
        } while (res);
    }

        private void updateRequestStatus(String newStatus) {
            System.out.println("\n" + newStatus.toUpperCase() + " REQUEST(S)\n");
            rq.viewRequests();
            String requestIdsInput = "";
            boolean validIds = false;

            while (!validIds) {
                System.out.print("Enter Request IDs to " + newStatus.toLowerCase() + " (separate by commas or spaces): ");
                requestIdsInput = sc.nextLine().trim();

                // Split the input by spaces or commas to get individual IDs
                String[] requestIds = requestIdsInput.split("[,\\s]+");

                // Check if all entered IDs are valid
                validIds = true;
                for (String reqId : requestIds) {
                    if (!reqIdExists(reqId)) {
                        System.out.println("Invalid Request ID: " + reqId + ". Please try again.");
                        validIds = false;
                        break;
                    }

                    // Check if the request is already approved or rejected
                    String currentStatus = getRequestStatus(reqId);
                    if (currentStatus.equals("Approved") || currentStatus.equals("Rejected")) {
                        System.out.println("Request ID " + reqId + " is already " + currentStatus + ". Cannot modify.");
                        validIds = false;
                        break;
                    }
                }

                if (!validIds) {
                    System.out.println("One or more invalid Request IDs or already approved/rejected. Please enter again.");
                }
            }
            String query = "UPDATE tbl_request SET req_status = ? WHERE req_id = ?";
            for (String requestId : requestIdsInput.split("[,\\s]+")) {
                db.updateRecord(query, newStatus, requestId);  // Execute the update for each request ID
                System.out.println("Request ID " + requestId + " has been " + newStatus.toLowerCase() + " successfully.");
            }
        }



    public void generateFilteredGeneralReport() {
        System.out.println("\nGENERATE FILTERED GENERAL REPORT\n");
        System.out.println("Select the type of requests to view:");
        System.out.println("1. Approved Requests");
        System.out.println("2. Rejected Requests");
        System.out.println("3. Pending Requests");
        System.out.println("4. Go Back");
        System.out.print("Enter your choice (1-4): ");

        int choice = getIntInput();
        String statusFilter = "";

        switch (choice) {
            case 1:
                statusFilter = "Approved";
                break;
            case 2:
                statusFilter = "Rejected";
                break;
            case 3:
                statusFilter = "Pending";
                break;
            case 4:
                return;  // Go back to the previous menu
            default:
                System.out.println("Invalid choice. Returning to menu.");
                return;
        }

        String query = "SELECT r.req_id, z.f_name, z.l_name, r.doc_type, r.req_date, r.req_status, r.purpose " +
                       "FROM tbl_request r INNER JOIN tbl_citizen z ON r.ctzn_id = z.ctzn_id WHERE r.req_status = '" + statusFilter + "'";
        String[] headers = {"Request ID", "First Name", "Last Name", "Document Type", "Request Date", "Status", "Purpose"};
        String[] columns = {"req_id", "f_name", "l_name", "doc_type", "req_date", "req_status", "purpose"};

        db.viewRecords(query, headers, columns);
    }

         public void generateIndividualReport() {
                System.out.println("\nGENERATING INDIVIDUAL REPORT\n");
                ct.viewCitizen();
                // Ask the user for the Citizen ID to filter the requests
                System.out.print("Enter Citizen ID to view their requests: ");
                String citizenId = sc.nextLine();

                // Check if the citizen ID exists
                if (!citizenIdExists(citizenId)) {
                    System.out.println("Citizen ID not found. Returning to menu.");
                    return;
                }

                // Query to get the citizen's name
                String citizenQuery = "SELECT f_name, l_name FROM tbl_citizen WHERE ctzn_id = ?";

                // Fetch the citizen's name
                Object[] citizenInfo = db.getSingleRecord(citizenQuery, citizenId);
                if (citizenInfo != null) {
                    // Citizen name found
                    String firstName = (String) citizenInfo[0];
                    String lastName = (String) citizenInfo[1];
                    System.out.println("Requests for Citizen: " + firstName + " " + lastName);

                    // Query to get the citizen's requests
                    String requestsQuery = "SELECT r.req_id, r.doc_type, r.req_date, r.req_status, r.purpose " +
                                           "FROM tbl_request r " +
                                           "WHERE r.ctzn_id = ? AND r.req_status IN ('Approved', 'Rejected')";

                    // Headers and columns for the report
                    String[] headers = {"Request ID", "Document Type", "Request Date", "Status", "Purpose"};
                    String[] columns = {"req_id", "doc_type", "req_date", "req_status", "purpose"};

                    // Fetch and display the citizen's requests, passing the citizenId parameter
                    db.viewRecords(requestsQuery, headers, columns, citizenId);  // Pass citizenId here
                } else {
                    System.out.println("No records found for Citizen ID: " + citizenId);
                }
            }


// Validation method to check if a citizen ID exists in the database
private boolean citizenIdExists(String citizenId) {
    String query = "SELECT COUNT(*) FROM tbl_citizen WHERE ctzn_id = ?";
    try {
        double count = db.getSingleValue(query, citizenId);
        return count > 0;
    } catch (Exception e) {
        System.out.println("Error checking citizen ID existence: " + e.getMessage());
        return false;
    }
}


    // Validation method
    private boolean reqIdExists(String reqId) {
        String query = "SELECT COUNT(*) FROM tbl_request WHERE req_id = ?";
        try {
            double count = db.getSingleValue(query, reqId); 
            return count > 0; 
        } catch (Exception e) {
            System.out.println("Error checking request ID existence: " + e.getMessage());
            return false; 
        }
    }

    // Method to handle integer input with exception handling
    private int getIntInput() {
        while (true) {
            try {
                return Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }
    
    // Method to retrieve the current status of a request
        private String getRequestStatus(String reqId) {
            String query = "SELECT req_status FROM tbl_request WHERE req_id = ?";
            try {
                Object[] result = db.getSingleRecord(query, reqId);
                if (result != null && result.length > 0) {
                    return (String) result[0];
                }
            } catch (Exception e) {
                System.out.println("Error retrieving request status: " + e.getMessage());
            }
            return "";
        }

}
