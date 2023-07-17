package com.Company;

import java.net.MalformedURLException;
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
    private Connection connect_01,connect_02;
    private Statement st_01,st_02;
    private ResultSet result_01,result_02;
    private PreparedStatement prepared_01,prepared_02;
    private static Token tokenA;
    private static Token tokenB;
    int num_of_users_to_connect = 0;


    public Server() throws RemoteException {
        // super();
        DefaultTime time = new DefaultTime();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connect_01 = DriverManager.getConnection("jdbc:mysql://localhost:3306/ars", "root", "Eshant@c18");
            st_01 = connect_01.createStatement();
            connect_02 = DriverManager.getConnection("jdbc:mysql://localhost:3306/ars_backup", "root", "Eshant@c18");
            st_02 = connect_02.createStatement();
            String path = "//localhost:1099/SystemTime";
            SystemTime stub = (SystemTime) UnicastRemoteObject.exportObject(time, 0);
            Naming.bind(path, time);

        } catch (Exception e) {
            System.out.println(e);
        }
        startTime = time.getTime();
        endTime = startTime + 30000;

    }

    public char server_to_connect(int passenger_ID) throws RemoteException {

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
    public boolean add_ticket(int ticket_ID, String source, String destination, int num_of_seats, String plane_name) throws RemoteException,SQLException {
        // Main database
        st_01.executeUpdate("insert into ticket (ticket_ID,source,destination,num_of_seats,plane_name) value('" + ticket_ID + "','" + source + "','" + destination + "','" + num_of_seats + "','" + plane_name + "') ");

        // Backup database
        st_02.executeUpdate("insert into ticket_backup (ticket_ID,source,destination,num_of_seats,plane_name) value('" + ticket_ID + "','" + source + "','" + destination + "','" + num_of_seats + "','" + plane_name + "') ");

        return true;
    }

    @Override
    public boolean update_ticket(int ticket_ID, String source, String destination, int num_of_seats, String plane_name) throws RemoteException,SQLException {
        // Updating main database
        prepared_01 = connect_01.prepareStatement("update ticket set num_of_seats = ?, source = ?, destination = ?, plane_name = ? where ticket_ID = ?");
        prepared_01.setInt(1, num_of_seats);
        prepared_01.setString(2, source);
        prepared_01.setString(3, destination);
        prepared_01.setString(4, plane_name);
        prepared_01.setInt(5,ticket_ID);

        prepared_01.executeUpdate();

        // Updating backup database
        prepared_02 = connect_02.prepareStatement("update ticket set num_of_seats = ?, source = ?, destination = ?, plane_name = ? where ticket_ID = ?");
        prepared_02.setInt(1, num_of_seats);
        prepared_02.setString(2, source);
        prepared_02.setString(3, destination);
        prepared_02.setString(4, plane_name);
        prepared_02.setInt(5,ticket_ID);

        prepared_02.executeUpdate();

        return true;
    }

    @Override
    public boolean delete_ticket(int ticket_ID) throws RemoteException,SQLException {
        prepared_01 = connect_01.prepareStatement("delete from ticket where ticket_ID = ?");
        prepared_01.setInt(1,ticket_ID);

        prepared_01.executeUpdate();
        return true;
    }

    @Override
    public boolean admin_authentication(String username, String password) throws RemoteException, SQLException {
        System.out.println("Authenticating...");
        DefaultTime time = new DefaultTime();
        long authenticationTime = time.getTime();
        System.out.println("Authentication Time : " + authenticationTime);

        System.out.println("Checking....");
        if(connect_01!=null && !connect_01.isClosed()){
            result_01 = st_01.executeQuery("select admin_ID from admin where admin_name = '" + username + "' and admin_password = '" + password + "'");
            while(result_01.next()){
                return true;
            }
        }
        else{
            System.out.println("Connecting to ARS backUp !!!");
            result_02 = st_02.executeQuery("select admin_ID from admin_backup where admin_name = '" + username + "' and admin_password = '" + password + "'");
            while(result_02.next()){
                return true;
            }
        }
        return false;
    }

    @Override
    public char authenticate(String username, String password) throws RemoteException, SQLException, MalformedURLException {
        System.out.println("Authenticating...");
        DefaultTime time = new DefaultTime();
        long authenticationTime = time.getTime();
        System.out.println("Authentication Time : " + authenticationTime);

        System.out.println("Checking....");
        if(connect_01 != null && !connect_01.isClosed()){
            result_01 = st_01.executeQuery("select passenger_ID from passenger where passenger_username = '" + username + "' and passenger_password = '" + password + "'");
            while(result_01.next()){
                int id = result_01.getInt(1);
                return server_to_connect(id);
            }
        }
        else{
            System.out.println("Connecting to ARS backUp !!!");
            result_02 = st_02.executeQuery("select passenger_ID from passenger where passenger_username = '" + username + "' and passenger_password = '" + password + "'");
            while(result_02.next()){
                int id = result_02.getInt(1);
                return server_to_connect(id);
            }
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
            // Storing to main database
            st_01.executeUpdate("insert into passenger(passenger_username,passenger_password) value('"+ username + "','" + password + "')" );

            // Updating to backup database
            st_02.executeUpdate("insert into passenger_backup(passenger_username,passenger_password) value('"+ username + "','" + password + "')" );
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
    public boolean bookTicket(int passenger_ID, String source, String destination, String plane_name, int num_of_seats) throws RemoteException, SQLException {
        try {
            int num_of_seats_available = 0;
            ResultSet result = st_01.executeQuery("select num_of_seats from ticket where source = '" + source + "' and destination = '" + destination + "' and plane_name = '" + plane_name + "'");
            while(result.next()){
                num_of_seats_available = result.getInt(1);
                if(num_of_seats_available < num_of_seats)
                    return false;
            }

            num_of_seats_available -= num_of_seats;

            // Adding account to main database
            prepared_01 = connect_01.prepareStatement("update ticket set num_of_seats = ? where source = ? and destination = ? and plane_name = ?");
            prepared_01.setInt(1, num_of_seats_available);
            prepared_01.setString(2, source);
            prepared_01.setString(3, destination);
            prepared_01.setString(4, plane_name);

            prepared_01.executeUpdate();

            int ticket_ID = -1;
            result = st_01.executeQuery("select ticket_ID from ticket where source = '" + source + "' and destination = '" + destination + "' and plane_name = '" + plane_name + "'");
            while(result.next()){
                ticket_ID = result.getInt(1);
            }

            prepared_01 = connect_01.prepareStatement("update passenger set ticket_ID = ? where passenger_ID = ?");
            prepared_01.setInt(1,ticket_ID);
            prepared_01.setInt(2,passenger_ID);

            prepared_01.executeUpdate();

            // Updating backup server
            prepared_02 = connect_02.prepareStatement("update ticket_backup set num_of_seats = ? where source = ? and destination = ? and plane_name = ?");
            prepared_02.setInt(1, num_of_seats_available);
            prepared_02.setString(2, source);
            prepared_02.setString(3, destination);
            prepared_02.setString(4, plane_name);

            prepared_02.executeUpdate();

            ticket_ID = -1;
            result = st_02.executeQuery("select ticket_ID from ticket_backup where source = '" + source + "' and destination = '" + destination + "' and plane_name = '" + plane_name + "'");
            while(result.next()){
                ticket_ID = result.getInt(1);
            }

            prepared_02 = connect_02.prepareStatement("update passenger_backup set ticket_ID = ? where passenger_ID = ?");
            prepared_02.setInt(1,ticket_ID);
            prepared_02.setInt(2,passenger_ID);

            prepared_02.executeUpdate();

        } catch (Exception e){
            System.out.println("e");
        }

        return true;
    }

    @Override
    public boolean cancelTicket(int ticket_ID, int passenger_id) throws RemoteException, SQLException {
        int num_of_seats = 0;
        result_01 = st_01.executeQuery("select num_of_seats from ticket where ticket_ID = " + ticket_ID);
        while(result_01.next()){
                num_of_seats = result_01.getInt(1);
        }

        if(num_of_seats == 0){
                System.out.println("You have entered either incorrect ticket_ID or incorrect passenger_ID");
                return false;
            }

        num_of_seats += 1;
        prepared_01 = connect_01.prepareStatement("update ticket set num_of_seats = ? where ticket_ID = ?");
        prepared_01.setInt(1, num_of_seats);
        prepared_01.setInt(2, ticket_ID);
        prepared_01.executeUpdate();

        int ticket_ID_new = -1;
        prepared_01 = connect_01.prepareStatement("update passenger set ticket_ID = ? where passenger_id = ?");
        prepared_01.setInt(1,ticket_ID_new);
        prepared_01.setInt(2,passenger_id);
        prepared_01.executeUpdate();

        // Updating Backup server
        result_02 = st_02.executeQuery("select num_of_seats from ticket_backup where ticket_ID = " + ticket_ID);
        while(result_02.next()){
            num_of_seats = result_01.getInt(1);
        }

        num_of_seats += 1;
        prepared_02 = connect_02.prepareStatement("update ticket_backup set num_of_seats = ? where ticket_ID = ?");
        prepared_02.setInt(1, num_of_seats);
        prepared_02.setInt(2, ticket_ID);
        prepared_02.executeUpdate();

        ticket_ID_new = -1;
        prepared_02 = connect_02.prepareStatement("update passenger_backup set ticket_ID = ? where passenger_id = ?");
        prepared_02.setInt(1,ticket_ID_new);
        prepared_02.setInt(2,passenger_id);
        prepared_02.executeUpdate();

        return true;
    }

    public static void main(String[] args) throws SQLException{
        try {
            String serverName = "ARS";
            Registry reg = LocateRegistry.createRegistry(1099);
            Server s = new Server();
            Naming.rebind(serverName,s);

            tokenA = new Token();
            tokenB = new Token();

            System.out.println("Server is running...");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}