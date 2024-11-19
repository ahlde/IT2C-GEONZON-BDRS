package Entities;

import Main.config;
import java.util.Scanner;

public class approval {
    config db = new config();
    Scanner sc = new Scanner(System.in);

    public void approve() {
        boolean res = true;

        do {
            System.out.println(".....................................");
            System.out.println("    Approve a Request");
            System.out.println(".....................................");
            System.out.println("1. View Pending Requests");
            System.out.println("2. Approve a Request");
            System.out.println("3. Reject a Request");
            System.out.println("4. Generate General Report");
            System.out.println("5. Generate Individual Report");
            System.out.println("6. Exit");
            System.out.println("-------------------------------------");

            System.out.print("Enter Action: ");
            int choice = sc.nextInt();

            switch (choice) {
                case 1:
                    viewPendingRequests();
                    break;
                case 2:
                    viewPendingRequests();
                    updateRequestStatus("Approved");
                    break;
                case 3:
                    viewPendingRequests();
                    updateRequestStatus("Rejected");
                    break;
                case 4:
                    generateFilteredGeneralReport();
                    break;
                case 5:
                    generateIndividualReport();
                    break;
                case 6:
                    res = false;
                    break;
                default:
                    System.out.println("Invalid Input");
                    break;
            }
        } while (res);
    }

   
    public void viewPendingRequests() {
        String query = "SELECT r.req_id, z.f_name, z.l_name, r.doc_type, r.req_date, r.req_status, r.purpose " +
                       "FROM tbl_request r INNER JOIN tbl_citizen z ON r.ctzn_id = z.ctzn_id WHERE r.req_status = 'Pending'";
        String[] headers = {"Request ID", "First Name", "Last Name", "Document Type", "Request Date", "Status", "Purpose"};
        String[] columns = {"req_id", "f_name", "l_name", "doc_type", "req_date", "req_status", "purpose"};

        db.viewRecords(query, headers, columns);
    }

    
    private void updateRequestStatus(String newStatus) {
        System.out.println("\n" + newStatus.toUpperCase() + " A REQUEST\n");

        String requestId = "";
        boolean idExists = false;

        while (!idExists) {
            System.out.print("Enter Request ID to " + newStatus.toLowerCase() + ": ");
            requestId = sc.next();

            if (reqIdExists(requestId)) {
                idExists = true;
            } else {
                System.out.println("Invalid Request ID or ID not found. Please try again.");
            }
        }
        String query = "UPDATE tbl_request SET req_status = ? WHERE req_id = ?";
        db.updateRecord(query, newStatus, requestId);

        System.out.println("Request ID " + requestId + " has been " + newStatus.toLowerCase() + " successfully.");
    }

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
    public void generateFilteredGeneralReport() {
        System.out.println("\nGENERATE FILTERED GENERAL REPORT\n");
        System.out.println("Select the type of requests to view:");
        System.out.println("1. Approved Requests");
        System.out.println("2. Rejected Requests");
        System.out.println("3. Pending Requests");
        System.out.print("Enter your choice (1-3): ");

        int choice = sc.nextInt();
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

        String query = "SELECT r.req_id, z.f_name, z.l_name, r.doc_type, r.req_date, r.req_status, r.purpose " +
                       "FROM tbl_request r INNER JOIN tbl_citizen z ON r.ctzn_id = z.ctzn_id " +
                       "WHERE r.req_status IN ('Approved', 'Rejected')";

        String[] headers = {"Request ID", "First Name", "Last Name", "Document Type", "Request Date", "Status", "Purpose"};
        String[] columns = {"req_id", "f_name", "l_name", "doc_type", "req_date", "req_status", "purpose"};

        db.viewRecords(query, headers, columns);
    }
}
