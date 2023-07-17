package com.Company;

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
             Connection connect_01 = DriverManager.getConnection("jdbc:mysql://localhost:3306/ars", "root", "Eshant@c18");
             Connection connect_02 = DriverManager.getConnection("jdbc:mysql://localhost:3306/ars_backup", "root", "Eshant@c18");
             Scanner sc = new Scanner(System.in);
             Statement st_01 = connect_01.createStatement();
             Statement st_02 = connect_02.createStatement();

            ResultSet result_01 = null,result_02 = null;
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

                boolean run = true;
                while(run) {
                    choose_User();
                    System.out.print("Enter your choice : ");
                    int user = sc.nextInt();
                    sc.nextLine();

                    if (user == 1) {
                        boolean move = true;
                        boolean IsadminAunthenticated = false;

                        while(move){

                            if(!IsadminAunthenticated){
                                System.out.print("\nEnter Admin Name:");
                                username = sc.nextLine();

                                System.out.print("Enter Password: ");
                                password = sc.nextLine();

                                if(s.admin_authentication(username,password))
                                    IsadminAunthenticated = true;

                                else{
                                    System.out.println("Enter valid credentials...");
                                }
                            }

                            if(IsadminAunthenticated){
                                displayAdminOptions();
                                System.out.print("Enter your choice : ");
                                choice = sc.nextInt();

                                if(choice == 1){

                                    System.out.print("\nEnter ticket_ID:");
                                    int ticket_ID = sc.nextInt();
                                    sc.nextLine();

                                    System.out.print("\nEnter Source:");
                                    String source = sc.nextLine();

                                    System.out.print("\nEnter Destnation:");
                                    String destination = sc.nextLine();

                                    System.out.print("\nEnter No. of seats :");
                                    int num_of_seats = sc.nextInt();
                                    sc.nextLine();

                                    System.out.print("\nEnter plane name:");
                                    String plane_name = sc.nextLine();

                                    if(s.add_ticket(ticket_ID,source,destination,num_of_seats,plane_name)){
                                        System.out.println("Added successfully...");
                                    }
                                }

                                if(choice == 2){
                                    System.out.print("\nEnter ticket ID:");
                                    int ticket_ID = sc.nextInt();
                                    sc.nextLine();

                                    System.out.print("\nEnter Source:");
                                    String source = sc.nextLine();

                                    System.out.print("\nEnter Destnation:");
                                    String destination = sc.nextLine();

                                    System.out.print("\nEnter No. of seats :");
                                    int num_of_seats = sc.nextInt();
                                    sc.nextLine();

                                    System.out.print("\nEnter plane name:");
                                    String plane_name = sc.nextLine();

                                    if(s.update_ticket(ticket_ID,source,destination,num_of_seats,plane_name)){
                                        System.out.println("Updated successfully....");
                                    }
                                }

                                if(choice == 3){
                                    System.out.print("\nEnter ticket ID:");
                                    int ticket_ID = sc.nextInt();

                                    if(s.delete_ticket(ticket_ID)){
                                        System.out.println("Deleted successfully...");
                                    }
                                }

                                if(choice == 4){
                                    connect_01.isClosed();
                                   if(connect_01!=null && !connect_01.isClosed()){
                                       result_01 = st_01.executeQuery("select * from ticket");
                                       displayTickets(result_01);
                                   }

                                   else{
                                       result_02 = st_02.executeQuery("select * from ticket_backup");
                                       displayTickets(result_02);
                                   }
                                }

                                if(choice == 5){
                                    System.out.println("\nThanks for visiting us. Welcome again....");
                                    move = run = false;
                                }

                                else{
                                    System.out.println("Invalid Option !! \n");
                                }
                            }
                        }
                    }
                    else if (user == 2) {
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
                                    if (move)
                                        System.out.println("Token is free..");
                                    else
                                        System.out.println("Token in use..");

                                    showPortal(server, move, flag, sc, s, username, password, connect_01, connect_02, result_01, st_01, result_02, st_02);
                                    run = flag = move = false;
                                } else {
                                    System.out.println("\nInvalid login Credentials.");
                                }
                            } else if (choice == 2) {
                                System.out.print("\nEnter Username:");
                                username = sc.nextLine();

                                System.out.print("Enter Password: ");
                                password = sc.nextLine();

                                boolean isUserCreated = s.createAccount(username, password);

                                if (!isUserCreated) {
                                    System.out.println("\nUnable to create new user..");
                                } else {
                                    System.out.println("\nNew user created successfully....");
                                    char server = s.authenticate(username, password);
                                    showPortal(server, move, flag, sc, s, username, password, connect_01, connect_02, result_01, st_01, result_02, st_02);
                                }
                            } else {
                                System.out.println("Invalid choice");
                            }
                        }

                    }

                    else if (user == 3) {
                        System.out.println("\nThanks for visiting us. Welcome again....");
                        run = false;
                    }

                    else {
                        System.out.println("Invalid Option !! \n");
                    }
                }

                sc.close();
                connect_01.close();
                connect_02.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static  void showPortal(char server, boolean move,boolean flag, Scanner sc ,Services s, String username, String password, Connection connect_01, Connection connect_02, ResultSet result_01, Statement st_01, ResultSet result_02, Statement st_02) throws SQLException, RemoteException {
        while (move) {
            displayServices();

            System.out.print("Enter your option : ");
            int option = sc.nextInt();

            switch (option) {

                case 1:
                    System.out.println("Journey Details..........");
                    if(connect_01 != null && !connect_01.isClosed()){
                        if(s.enquiry(username,password))
                            result_01 = st_01.executeQuery("select * from ticket");
                        displayList(result_01);
                    }

                    else{
                        if(s.enquiry(username,password))
                            result_02 = st_02.executeQuery("select * from ticket_backup");
                        displayList(result_02);
                    }
                    break;

                case 2:
                    sc.nextLine();
                    System.out.print("\nEnter source:");
                    String source = sc.nextLine();

                    System.out.print("Enter destination: ");
                    String destination = sc.nextLine();

                    result_01 = st_01.executeQuery("select * from ticket where source = '" + source + "' and destination = '" + destination + "'");
                    if(result_01.next()) {
                        displayList(st_01.executeQuery("select * from ticket where source = '" + source + "' and destination = '" + destination + "'"));

                        System.out.println("Enter the plane name you want to have journey with ?");
                        String plane_name = sc.nextLine();
                        System.out.println("How many seats do you want to book ?");
                        int num_of_seats = sc.nextInt();
                        System.out.print("Enter your passenger_ID : ");
                        int passenger_ID = sc.nextInt();
                        s.bookTicket(passenger_ID,source, destination, plane_name, num_of_seats);
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
                    if(s.cancelTicket(ticket_ID, passenger_ID)){
                        System.out.println("Cancelled your ticket ");
                        System.out.println("Amount will be refunded by 24 hrs");
                    }
                    break;

                case 4:
                    showTicket(connect_01, connect_02, result_01, result_02, st_01, st_02, username, password);
                    break;

                case 5:
                    System.out.println("\nThanks for visiting us. Welcome again....");
                    s.setToken(true, server);
                    flag = move = false;
            }
        }
    }

    public static void displayTickets(ResultSet result) throws SQLException {
        System.out.println("-----------------------------------------------------------------------------");
        System.out.println("| - - - - - - - - - - - Airline Reservation System Ticket - - - - - - - - - |");

        System.out.println("+-------+---------------+-----------------+-------------+-------------------+");
        System.out.println("|  ID   |    Source     |   Destination   |  Plane Name |  Available Seats  |");
        while(result.next()){

            if(result.getInt(1)<0)
                continue;

            System.out.println("+-------+---------------+-----------------+-------------+-------------------+");
            System.out.println("|\t" + result.getInt(1) + "\t|\t\t"+ result.getString(2) + "\t\t|\t\t" + result.getString(3) + "\t\t  | " + result.getString(5) + ((result.getString(5).length()<9) ? "\t\t|\t\t" : "\t|\t\t") + result.getInt(4) + "\t\t\t|");
        }
        System.out.println("+-------+---------------+-----------------+-------------+-------------------+");

    }

    public static void showTicket(Connection connect_01, Connection connect_02, ResultSet result_01, ResultSet result_02, Statement st_01, Statement st_02, String username, String password) throws SQLException {
        int ticket_ID = 0;
        if(connect_01 != null && !connect_01.isClosed()){
            result_01 = st_01.executeQuery("select * from passenger where passenger_username = '" + username + "' and passenger_password = '" + password + "'");
            while(result_01.next()){
                ticket_ID = result_01.getInt(4);
                if(ticket_ID < 0){
                    System.out.println("You have no upcoming journey....");
                    return;
                }
                System.out.println("-----------------------------------------------------");
                System.out.println("|         Airline Reservation System Ticket ✈✈✈    |");
                System.out.println("-----------------------------------------------------");
                System.out.println("| " + "Passenger ID : " + result_01.getInt(1) + "\t\t\t" + "\t\t\t\t\t\t|");
                System.out.println("| " + "Passenger Name : " + result_01.getString(2) + "\t\t\t" + "Ticket ID : " + result_01.getInt(4) + "\t\t|");
            }

            result_01 = st_01.executeQuery("select * from ticket where ticket_id = " + ticket_ID);
            while(result_01.next()){
                System.out.println("| " + "Source : " + result_01.getString(2) + "\t\t\t" + "Destination : " + result_01.getString(3) + "\t\t\t|");
                System.out.println("| " + "Plane Name : " +  "✈ "+ result_01.getString(5) + ((result_01.getString(5).length()<9) ? "\t\t\t\t\t\t\t|" : "\t\t\t\t\t|"));
            }
            System.out.println("-----------------------------------------------------");
        }

        else{
            result_02 = st_02.executeQuery("select * from passenger_backup where passenger_username = '" + username + "' and passenger_password = '" + password + "'");
            while(result_02.next()){
                ticket_ID = result_02.getInt(4);
                if(ticket_ID < 0){
                    System.out.println("You have no upcoming journey....");
                    return;
                }
                System.out.println("-----------------------------------------------------");
                System.out.println("|         Airline Reservation System Ticket ✈✈✈    |");
                System.out.println("-----------------------------------------------------");
                System.out.println("| " + "Passenger ID : " + result_02.getInt(1) + "\t\t\t" + "\t\t\t\t\t\t|");
                System.out.println("| " + "Passenger Name : " + result_02.getString(2) + "\t\t\t" + "Ticket ID : " + result_02.getInt(4) + "\t\t|");
            }

            result_02 = st_02.executeQuery("select * from ticket_backup where ticket_id = " + ticket_ID);
            while(result_02.next()){
                System.out.println("| " + "Source : " + result_02.getString(2) + "\t\t\t" + "Destination : " + result_02.getString(3) + "\t\t\t|");
                System.out.println("| " + "Plane Name : " +  "✈ "+ result_02.getString(5) + ((result_02.getString(5).length()<9) ? "\t\t\t\t\t\t\t|" : "\t\t\t\t\t|"));
            }
            System.out.println("-----------------------------------------------------");

        }
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

    public static void displayAdminOptions(){
        System.out.println("\nOptions :");
        System.out.println("Enter 1 to add ticket");
        System.out.println("Enter 2 to update ticket");
        System.out.println("Enter 3 to delete ticket");
        System.out.println("Enter 4 to view all tickets");
        System.out.println("Enter 5 to exit");
    }

    public static void displayServices() {
        System.out.println("\nOptions :");
        System.out.println("Enter 1 to enquiry");
        System.out.println("Enter 2 to book Ticket");
        System.out.println("Enter 3 to cancel Ticket");
        System.out.println("Enter 4 to view Ticket of upcoming journey");
        System.out.println("Enter 5 to exit");
    }

    public static void choose_User(){
        System.out.println("Enter 1 to use ARS as Admin");
        System.out.println("Enter 2 to use ARS as User");
    }
}

