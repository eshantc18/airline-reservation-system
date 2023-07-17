package com.Company;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.ArrayList;

// unicast remote object is a remote object that can only be accessed by one client at a time
public class Server extends UnicastRemoteObject implements Services {
    long startTime;
    long endTime;
    private Connection connect;
    private Statement st;
    private ResultSet result;
    private PreparedStatement prepared;
    private static Token tokenA;
    private static Token tokenB;
    int num_of_users_to_connect = 0;


    public Server() throws RemoteException {
        // super();
        DefaultTime time = new DefaultTime();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/ars", "root", "Eshant@c18");
            st = connect.createStatement();
            String path = "//localhost:1099/SystemTime";
            SystemTime stub = (SystemTime) UnicastRemoteObject.exportObject(time, 0);
            Naming.bind(path, time);

        } catch (Exception e) {
            System.out.println("Error");
        }
        startTime = time.getTime();
        endTime = startTime + 30000;

    }

    // can use hashmaps,
//    public static ArrayList<Passenger> P = new ArrayList<Passenger>();
//    public static ArrayList<Ticket> T = new ArrayList<Ticket>();

    public char server_to_connect(int passenger_ID){
        if(num_of_users_to_connect%2 == 0 && !tokenA.InUse){
            System.out.println("Connecting passenger with ID :" + passenger_ID + " to Server A");
            num_of_users_to_connect++;
            tokenA.InUse = true;
            return 'A';
        }

        if(num_of_users_to_connect%2 == 0 && tokenA.InUse){
            System.out.println("Waiting for passenger with ID :" + passenger_ID + " to connect to Server A");
            num_of_users_to_connect++;
            tokenA.InUse = false;
            return 'A';
        }

        if(num_of_users_to_connect%2 != 0 && !tokenB.InUse){
            System.out.println("Connecting passenger with ID :" + passenger_ID + " to Server B");
            num_of_users_to_connect++;
            tokenB.InUse = true;
            return 'B';
        }

        if(num_of_users_to_connect%2 != 0 && tokenB.InUse){
            System.out.println("Waiting for passenger with ID :" + passenger_ID + " to connect to Server B");
            num_of_users_to_connect++;
            tokenB.InUse = false;
            tokenB.InUse = true;
            return 'B';
        }

        return 'C';
    }

    @Override
    public char authenticate(String username, String password) throws RemoteException, SQLException {
        System.out.println("Authenticating...");
        DefaultTime time = new DefaultTime();
        long authenticationTime = time.getTime();
        System.out.println("Authentication Time : " + authenticationTime);

        System.out.println("Checking....");
        result = st.executeQuery("select passenger_ID from passenger where passenger_username = '" + username + "' and passenger_password = '" + password + "'");
        while(result.next()){
            int id = result.getInt(1);
            return server_to_connect(id);
        }

        return 'F';
    }

    public boolean getOptions(char server){
        if(server == 'A'){
            while(!tokenA.InUse){
                System.out.println("Token in use....");
                return false;
            }
        }

        if(server == 'B'){
            while(!tokenB.InUse){
                System.out.println("Token in use....");
                return false;
            }
        }

        System.out.println("Token is free...");
        return true;
    }

    public void setToken(boolean set, char server){
        if(server == 'A')
            tokenA.InUse = set;

        if(server == 'B')
            tokenB.InUse = set;

    }


    @Override
    public boolean createAccount(String username, String password) throws RemoteException {
        boolean success = false;
        try {
            st.executeUpdate("insert into passenger(passenger_username,passenger_password) value('"+ username + "','" + password + "')" );
            success = true;
        } catch (Exception e) {
            System.out.println(e);
        }
        return success;
    }

    @Override
    public boolean enquiry(String userName, String password) {
        return true;
    }

    @Override
    public boolean bookTicket(String source, String destination, String plane_name, int num_of_seats) throws RemoteException, SQLException {
        try {
            int num_of_seats_available = 0;
            ResultSet result = st.executeQuery("select num_of_seats from ticket where source = '" + source + "' and destination = '" + destination + "' and plane_name = '" + plane_name + "'");
            while(result.next()){
                num_of_seats_available = result.getInt(1);
                if(num_of_seats_available < num_of_seats)
                    return false;
            }

            num_of_seats_available -= num_of_seats;
            prepared = connect.prepareStatement("update ticket set num_of_seats = ? where source = ? and destination = ? and plane_name = ?");
            prepared.setInt(1, num_of_seats_available);
            prepared.setString(2, source);
            prepared.setString(3, destination);
            prepared.setString(4, plane_name);

            prepared.executeUpdate();

        } catch (Exception e){
            System.out.println("e");
        }

        return true;
    }

    @Override
    public boolean cancelTicket(int ticket_ID, int passenger_id) throws RemoteException, SQLException {
            int num_of_seats = 0;
            result = st.executeQuery("select num_of_seats from ticket where ticket_ID = " + ticket_ID);
            while(result.next()){
                num_of_seats = result.getInt(1);
            }

        if(num_of_seats == 0){
                System.out.println("You have entered either incorrect ticket_ID or incorrect passenger_ID");
                return false;
            }

            num_of_seats += 1;
        prepared = connect.prepareStatement("update ticket set num_of_seats = ? where ticket_ID = ?");
        prepared.setInt(1, num_of_seats);
        prepared.setInt(2, ticket_ID);
        prepared.executeUpdate();

        int ticket_ID_new = -1;
        prepared = connect.prepareStatement("update passenger set ticket_ID = ? where passenger_id = ?");
        prepared.setInt(1,ticket_ID_new);
        prepared.setInt(2,passenger_id);
        prepared.executeUpdate();

        return true;
    }

    @Override
    public boolean checkStatus(String userName, String password, int num) throws RemoteException {
        System.out.println("Service will be provided soon....");
        return false;
    }

    public static void main(String[] args) {
        String serverName = "ARS";
        try {
            Registry reg = LocateRegistry.createRegistry(1099);
            // reg.rebind(serverName, new Server());
            Server s = new Server();
            Naming.rebind(serverName, s);
            tokenA = new Token();
            tokenB = new Token();

            System.out.println("Server is running...");

//            Passenger p1 = new Passenger("Admin", "Admin@123");
//            P.add(p1);
//            Passenger p2 = new Passenger("Eshantc18", "Eshant@18");
//            P.add(p2);
//            Passenger p3 = new Passenger("Eshantc123", "Eshant@123");
//            P.add(p3);
//
//            Ticket t1 = new Ticket("MUM", "DEL", "AIR INDIA", 54);
//            T.add(t1);
//            Ticket t2 = new Ticket("MUM", "DEL", "VISTARA", 54);
//            T.add(t2);
//            Ticket t3 = new Ticket("MUM", "DEL", "AIR INDIGO", 54);
//            T.add(t3);
//            Ticket t4 = new Ticket("NGP", "MUM", "GOAIR", 42);
//            T.add(t4);
//            Ticket t5 = new Ticket("NGP", "MUM", "VISTARA", 42);
//            T.add(t5);
//            Ticket t6 = new Ticket("NGP", "MUM", "AIR INDIA", 42);
//            T.add(t6);
//            Ticket t7 = new Ticket("DEL", "AMH", "VISTARA", 78);
//            T.add(t7);
//            Ticket t8 = new Ticket("DEL", "AMH", "AIR INDIGO", 78);
//            T.add(t8);
//            Ticket t9 = new Ticket("DEL", "AMH", "INDIGO", 78);
//            T.add(t9);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}