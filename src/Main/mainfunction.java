package Main;

import Entities.approval;
import Entities.citizen;
import Entities.officials;
import Entities.request;
import java.util.InputMismatchException;
import java.util.Scanner;

public class mainfunction {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String response;

        do {
            System.out.println("-----------------------------------------------");
            System.out.println("Welcome to Barangay Documents Request System");
            System.out.println("-----------------------------------------------");
            System.out.println("1. CITIZEN");
            System.out.println("2. BARANGGAY OFFICIALS");
            System.out.println("3. REQUEST A DOCUMENT");
            System.out.println("4. APPROVE REQUESTS");
            System.out.println("5. EXIT");
            System.out.println("-----------------------------------------------");

            int act = 0;
            boolean validInput = false;
            while (!validInput) {
                System.out.print("Enter Action (1-5): ");
                try {
                    act = sc.nextInt();
                    if (act >= 1 && act <= 5) {
                        validInput = true;
                    } else {
                        System.out.println("Please enter a number between 1 and 5.");
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a number.");
                    sc.next(); 
                }
            }

            switch (act) {
                case 1:
                    citizen ap = new citizen();
                    ap.citizenTrans();
                    break;
                case 2:
                    officials offi = new officials();
                    offi.officialTrans();
                    break;
                case 3:
                    request req = new request();
                    req.req();
                    break;
                case 4:
                    approval apv = new approval();
                    apv.approve();
                    break;
                case 5:
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid Action"); 
            }

            if (act == 5) {
                break; 
            }

            System.out.print("Do you want to continue? (yes/no): ");
            response = sc.next().trim().toLowerCase();
            while (!response.equals("yes") && !response.equals("no")) {
                System.out.print("Invalid input. Please enter 'yes' or 'no': ");
                response = sc.next().trim().toLowerCase();
            }

        } while (response.equals("yes"));

        System.out.println("Thank you, See you!");
        sc.close();
    }
}
