package Entities;

import Main.config;
import java.util.Scanner;

public class request {
    config db = new config();
    Scanner sc = new Scanner(System.in);
    citizen ct = new citizen();

    public void manageRequests() {
        boolean isRunning = true;

        while (isRunning) {
            System.out.println("\n========== Document Request Management ==========");
            System.out.println("1. Add Request");
            System.out.println("2. View Requests"); // No specific 'pending' filter
            System.out.println("3. Update Request");
            System.out.println("4. Delete Request");
            System.out.println("5. Exit");
            System.out.print("Select an action (1-5): ");

            int choice = sc.nextInt();
            switch (choice) {
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
                    isRunning = false;
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid input. Please try again.");
            }
        }
    }

    public void addRequest() {
        System.out.println("\n--- Add New Document Request ---");
        ct.viewCitizen();

        String citizenId;
        while (true) {
            System.out.print("Enter Citizen ID: ");
            citizenId = sc.next();
            if (citIdExists(citizenId)) break;
            System.out.println("Invalid ID. Please try again.");
        }

        System.out.println("\nSelect Document Type:");
        System.out.println("1. Barangay Clearance");
        System.out.println("2. Certificate of Residency");
        System.out.println("3. Certificate of Indigency");
        System.out.print("Enter choice (1-3): ");

        String[] docTypes = {"Barangay Clearance", "Certificate of Residency", "Certificate of Indigency"};
        int docTypeChoice = sc.nextInt();

        if (docTypeChoice < 1 || docTypeChoice > 3) {
            System.out.println("Invalid choice. Request cancelled.");
            return;
        }
        String documentType = docTypes[docTypeChoice - 1];

        sc.nextLine(); // Consume the newline character
        System.out.print("Enter the purpose: ");
        String purpose = sc.nextLine();

        // Display officials
        System.out.println("\nSelect the Barangay Official:");
        db.viewRecords(
            "SELECT official_id, first_name, last_name, position FROM brgy_official",
            new String[]{"Official ID", "First Name", "Last Name", "Position"},
            new String[]{"official_id", "first_name", "last_name", "position"}
        );

        System.out.print("Enter Official ID: ");
        int officialId = sc.nextInt();

        if (!officialIdExists(officialId)) {
            System.out.println("Invalid official ID. Request cancelled.");
            return;
        }

        // Insert request
        String sql = "INSERT INTO tbl_request (ctzn_id, doc_type, req_date, req_status, purpose, issued_by) " +
                     "VALUES (?, ?, current_timestamp, 'Pending', ?, ?)";
        db.addRecord(sql, citizenId, documentType, purpose, officialId);
        System.out.println("Request added successfully.");
    }

        public void viewRequests() {
            String query = "SELECT r.req_id, z.f_name, z.l_name, r.doc_type, r.req_date, r.req_status, r.purpose, " +
                           "o.first_name || ' ' || o.last_name AS official_name " +
                           "FROM tbl_request r " +
                           "INNER JOIN tbl_citizen z ON r.ctzn_id = z.ctzn_id " +
                           "INNER JOIN brgy_official o ON r.issued_by = o.official_id " +
                           "WHERE r.req_status = 'Pending'"; // Filter for Pending requests only
            db.viewRecords(query,
                new String[]{"ID", "First Name", "Last Name", "Document Type", "Date", "Status", "Purpose", "Issued By"},
                new String[]{"req_id", "f_name", "l_name", "doc_type", "req_date", "req_status", "purpose", "official_name"}
            );
        }


    public void updateRequest() {
        System.out.println("\n--- Update Document Request ---");
        String requestId = getRequestId();
        if (requestId == null) return;

        System.out.println("\nSelect the field to update:");
        System.out.println("1. Document Type");
        System.out.println("2. Purpose");
        System.out.println("3. Official Issuer");
        System.out.print("Choice: ");

        int choice = sc.nextInt();
        switch (choice) {
            case 1:
                updateField("doc_type", requestId);
                break;
            case 2:
                updateField("purpose", requestId);
                break;
            case 3:
                updateField("issued_by", requestId);
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    public void deleteRequest() {
        System.out.println("\n--- Delete Document Request ---");
        String requestId = getRequestId();
        if (requestId == null) return;

        System.out.print("Are you sure? (yes/no): ");
        if (sc.next().equalsIgnoreCase("yes")) {
            db.deleteRecord("DELETE FROM tbl_request WHERE req_id = ?", requestId);
            System.out.println("Request deleted successfully.");
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    private void updateField(String field, String requestId) {
        sc.nextLine(); // Consume newline
        System.out.print("Enter new value: ");
        String newValue = sc.nextLine();
        db.updateRecord("UPDATE tbl_request SET " + field + " = ? WHERE req_id = ?", newValue, requestId);
        System.out.println("Update successful.");
    }

    private String getRequestId() {
        System.out.print("Enter Request ID: ");
        String requestId = sc.next();
        if (!reqIdExists(requestId)) {
            System.out.println("Request ID not found.");
            return null;
        }
        return requestId;
    }

    // Validation methods
    private boolean citIdExists(String citizenId) {
        return db.getCount("SELECT COUNT(*) FROM tbl_citizen WHERE ctzn_id = ?", citizenId) > 0;
    }

    private boolean reqIdExists(String requestId) {
        return db.getCount("SELECT COUNT(*) FROM tbl_request WHERE req_id = ?", requestId) > 0;
    }

    private boolean officialIdExists(int officialId) {
        return db.getCount("SELECT COUNT(*) FROM brgy_official WHERE official_id = ?", officialId) > 0;
    }
}
