import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class Client {
    public static Scanner sc = new Scanner(System.in);

    public static void main(String args[]) throws RemoteException {
        try {

            Registry reg = LocateRegistry.getRegistry("localhost", 8000);

            Services authenticator = (Services) reg.lookup("Airline Reservation System");
            
            boolean move = true;
            int choice;
            String username, password;

            System.out.println("\n      Welcome to Airline Reservation System -> ");

            while (move){

                displayOptions();

                System.out.print("\nEnter your choice : ");
                choice = sc.nextInt();
                sc.nextLine();

                if(choice == 1){

                    System.out.print("\nEnter Username:");
                    username = sc.nextLine();

                    System.out.print("Enter Password: ");
                    password = sc.nextLine();

                    boolean isValidStudent = authenticator.authenticate(username, password);

                    if (isValidStudent) {
                        System.out.println("\nLogged In Successfully.....");

                        while (move){
                            displayServices();

                            System.out.print("Enter your option : ");
                            int option = sc.nextInt();

                            switch (option){

                                case 1:
                                    System.out.println("Service will be provided soon....");
                                    authenticator.enquiry(username,password);
                                    break;

                                case 2:
                                    sc.nextLine();
                                    System.out.print("\nEnter source:");
                                    String source = sc.nextLine();

                                    System.out.print("Enter destination: ");
                                    String destination = sc.nextLine();

                                    System.out.println("Service will be provided soon....");
                                    authenticator.bookTicket(username,password,source,destination);
                                    break;

                                case 3:
                                    System.out.println("Service will be provided soon....");
                                    authenticator.cancelTicket(username,password);
                                    break;

                                case 4:
                                    System.out.println("\nThanks for visiting us. Welcome again....");
                                    move = false;
                            }
                        }

                    }

                    else {
                        System.out.println("\nInvalid login Credentials.");
                    }
                }

                else if(choice == 2){
                    System.out.print("\nEnter Username:");
                    username = sc.nextLine();

                    System.out.print("Enter Password: ");
                    password = sc.nextLine();

                    boolean isUserCreated = authenticator.createAccount(username, password);

                    if (!isUserCreated) {
                        System.out.println("\nUnable to create new user..");
                    }

                    else {
                        System.out.println("\nNew user created successfully....");
                    }
                }

                else{
                    System.out.println("Invalid choice");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void displayOptions(){
        System.out.println("Enter 1 to Login");
        System.out.println("Enter 2 to create a new account.");
    }

    public static void displayServices(){
        System.out.println("\nOptions :");
        System.out.println("Enter 1 to enquiry");
        System.out.println("Enter 2 to book Ticket");
        System.out.println("Enter 3 to cancel Ticket");
        System.out.println("Enter 4 to exit");
    }
}


