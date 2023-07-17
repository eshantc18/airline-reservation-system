// Lampord Algo 
import java.rmi.Naming;
import java.rmi.RemoteException;
// import java.rmi.registry.LocateRegistry;
// import java.rmi.registry.Registry;
import java.util.Scanner;
import java.time.*;

public class Client {

    public static void main(String args[]) throws RemoteException {
        Clock clientClock = Clock.systemUTC();
        try {
            Scanner sc = new Scanner(System.in);

            // Registry reg = LocateRegistry.getRegistry("localhost", 8000);

            Services s = (Services) Naming.lookup("ARS");
            boolean flag = true;
            while (flag) {
                boolean move = true;
                int choice;
                String username, password;

                System.out.println("\n      Welcome to Airline Reservation System -> ");
                SystemTime stubTime = (SystemTime) Naming.lookup("SystemTime");
                long start = Instant.now().toEpochMilli(); // t1
                long serverTime = stubTime.getTime(); // t2
                System.out.println("Server time " + serverTime);

                long end = Instant.now().toEpochMilli();
                long rtt = (end - start) / 2;
                System.out.println("RTT " + rtt);
                
                long updatedTime = serverTime + rtt;
                Duration diff = Duration.ofMillis(updatedTime - clientClock.instant().toEpochMilli());

                clientClock = clientClock.offset(clientClock, diff);
                System.out.println("New Client time " + clientClock.instant().toEpochMilli());

                while (move) {

                    displayOptions();

                    System.out.print("\nEnter your choice : ");
                    choice = sc.nextInt();
                    sc.nextLine();

                    if (choice == 1) {

                        System.out.print("\nEnter Username:");
                        username = sc.nextLine();

                        System.out.print("Enter Password: ");
                        password = sc.nextLine();

                        boolean isValidStudent = s.authenticate(username, password);

                        if (isValidStudent) {
                            System.out.println("\nLogged In Successfully.....");

                            while (move) {
                                displayServices();

                                System.out.print("Enter your option : ");
                                int option = sc.nextInt();

                                switch (option) {

                                    case 1:
                                        System.out.println("Service will be provided soon....");
                                        s.enquiry(username, password);
                                        break;

                                    case 2:
                                        sc.nextLine();
                                        System.out.print("\nEnter source:");
                                        String source = sc.nextLine();

                                        System.out.print("Enter destination: ");
                                        String destination = sc.nextLine();

                                        System.out.println("Service will be provided soon....");
                                        s.bookTicket(username, password, source, destination);
                                        break;

                                    case 3:
                                        System.out.println("Service will be provided soon....");
                                        s.cancelTicket(username, password);
                                        break;

                                    case 4:
                                        System.out.println("\nThanks for visiting us. Welcome again....");
                                        flag = move = false;
                                }
                            }

                        }

                        else {
                            System.out.println("\nInvalid login Credentials.");
                        }
                    }

                    else if (choice == 2) {
                        System.out.print("\nEnter Username:");
                        username = sc.nextLine();

                        System.out.print("Enter Password: ");
                        password = sc.nextLine();

                        boolean isUserCreated = s.createAccount(username, password);

                        if (!isUserCreated) {
                            System.out.println("\nUnable to create new user..");
                        }

                        else {
                            System.out.println("\nNew user created successfully....");
                        }
                    }

                    else {
                        System.out.println("Invalid choice");
                    }
                }
            sc.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void displayOptions() {
        System.out.println("Enter 1 to Login");
        System.out.println("Enter 2 to create a new account.");
    }

    public static void displayServices() {
        System.out.println("\nOptions :");
        System.out.println("Enter 1 to enquiry");
        System.out.println("Enter 2 to book Ticket");
        System.out.println("Enter 3 to cancel Ticket");
        System.out.println("Enter 4 to exit");
    }
}
