package com.Company;
// Lampord Algo
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.sql.DriverManager;
import java.util.ArrayList;
// import java.rmi.registry.LocateRegistry;
// import java.rmi.registry.Registry;
import java.util.Scanner;
import java.time.*;
import java.sql.*;

public class Client {

    public static void main(String args[]) throws RemoteException {
        Clock clientClock = Clock.systemUTC();
        try {
             Class.forName("com.mysql.cj.jdbc.Driver");
             Connection connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/ars", "root", "Eshant@c18");
             Scanner sc = new Scanner(System.in);
             Statement st = connect.createStatement();
             ResultSet result = st.executeQuery("select * from passenger");
            // Registry reg = LocateRegistry.getRegistry("localhost", 8000);

            Services s = (Services) Naming.lookup("ARS");
            boolean flag = true;
            while (flag) {

                int choice;
                String username, password;

                System.out.println("\n      Welcome to Airline Reservation System -> ");
                SystemTime stubTime = (SystemTime) Naming.lookup("SystemTime");
                long start = Instant.now().toEpochMilli(); // t1
                long serverTime = stubTime.getTime(); // t2
                System.out.println("Server time " + serverTime);

                long end = Instant.now().toEpochMilli();
                long rtt = (end - start) / 2;

                long updatedTime = serverTime + rtt;
                Duration diff = Duration.ofMillis(updatedTime - clientClock.instant().toEpochMilli());

                clientClock = Clock.offset(clientClock, diff);
                System.out.println("New Client time " + clientClock.instant().toEpochMilli());

                boolean move = true;
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

                        char server = s.authenticate(username, password);

                        if (server != 'F') {
                            System.out.println("\nLogged In Successfully.....");

                            move = s.getOptions(server);
                            if(move)
                                System.out.println("Token is free..");
                            else
                                System.out.println("Token in use..");

                            showPortal( server, move, flag,  sc , s, username, password, result, st);
                            flag = move = false;
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
                            char server = s.authenticate(username,password);
                            showPortal( server, move, flag,  sc , s, username, password, result, st);
                        }
                    }

                    else {
                        System.out.println("Invalid choice");
                    }
                }
                sc.close();
                connect.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static  void showPortal(char server, boolean move,boolean flag, Scanner sc ,Services s, String username, String password, ResultSet result, Statement st) throws SQLException, RemoteException {
        while (move) {
            displayServices();

            System.out.print("Enter your option : ");
            int option = sc.nextInt();

            switch (option) {

                case 1:
                    System.out.println("Journey Details..........");
                    if(s.enquiry(username,password))
                        result = st.executeQuery("select * from ticket");
                    displayList(result);
                    break;

                case 2:
                    sc.nextLine();
                    System.out.print("\nEnter source:");
                    String source = sc.nextLine();

                    System.out.print("Enter destination: ");
                    String destination = sc.nextLine();

                    result = st.executeQuery("select * from ticket where source = '" + source + "' and destination = '" + destination + "'");
                    if(result.next()) {
                        displayList(st.executeQuery("select * from ticket where source = '" + source + "' and destination = '" + destination + "'"));

                        System.out.println("Enter the plane name you want to have journey with ?");
                        String plane_name = sc.nextLine();
                        System.out.println("How many seats do you want to book ?");
                        int num_of_seats = sc.nextInt();

                        s.bookTicket(source, destination, plane_name, num_of_seats);
                    }
                    else{
                        System.out.println("\nNo planes are available.....");
                    }
                    break;

                case 3:

                    System.out.print("Enter your passneger_ID : ");
                    int passenger_ID = sc.nextInt();
                    System.out.print("Enter your ticket_ID : ");
                    int ticket_ID = sc.nextInt();
                    if(s.cancelTicket(ticket_ID, passenger_ID) == true){
                        System.out.println("Cancelled your ticket ");
                        System.out.println("Amount will be refunded by 24 hrs");
                    }
                    break;

                case 4:
                    showTicket(result,st, username, password);
                    break;

                case 5:
                    System.out.println("\nThanks for visiting us. Welcome again....");
                    s.setToken(true, server);
                    flag = move = false;
            }
        }
    }
    public static void showTicket(ResultSet result, Statement st, String username, String password) throws SQLException {
        int ticket_ID = 0;

        result = st.executeQuery("select * from passenger where passenger_username = '" + username + "' and passenger_password = '" + password + "'");
        while(result.next()){
            ticket_ID = result.getInt(4);
            if(ticket_ID < 0){
                System.out.println("You have no upcoming journey....");
                return;
            }
            System.out.println("-----------------------------------------------------");
            System.out.println("|         Airline Reservation System Ticket ✈✈✈    |");
            System.out.println("-----------------------------------------------------");
            System.out.println("| " + "Passenger ID : " + result.getInt(1) + "\t\t\t" + "\t\t\t\t\t\t|");
            System.out.println("| " + "Passenger Name : " + result.getString(2) + "\t\t\t" + "Ticket ID : " + result.getInt(4) + "\t\t|");
        }

        result = st.executeQuery("select * from ticket where ticket_id = " + ticket_ID);
        while(result.next()){
            System.out.println("| " + "Source : " + result.getString(2) + "\t\t\t" + "Destination : " + result.getString(3) + "\t\t\t|");
            System.out.println("| " + "Plane Name : " +  "✈ "+ result.getString(5) + ((result.getString(5).length()<9) ? "\t\t\t\t\t\t\t|" : "\t\t\t\t\t|"));
        }
        System.out.println("-----------------------------------------------------");
    }

    public static void displayList(ResultSet result) throws SQLException {

        System.out.println("+---------------+-----------------+-------------+-------------------+");
        System.out.println("|    Source     |   Destination   |  Plane Name |  Available Seats  |");
        while(result.next()){
            System.out.println("+---------------+-----------------+-------------+-------------------+");
            System.out.println("|\t\t"+ result.getString(2) + "\t\t|\t\t" + result.getString(3) + "\t\t  | " + result.getString(5) + ((result.getString(5).length()<9) ? "\t\t|\t\t" : "\t|\t\t") + result.getInt(4) + "\t\t\t|");
        }
        System.out.println("+---------------+-----------------+-------------+-------------------+");
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
        System.out.println("Enter 4 to view Ticket of upcoming journey");
        System.out.println("Enter 5 to exit");
    }
}
