package com.Company;
import java.net.MalformedURLException;
import java.rmi.*;
import java.sql.SQLException;

public interface Services extends Remote {
    // For Passengers
    public char authenticate(String userName, String password) throws RemoteException, SQLException, MalformedURLException;
    public boolean createAccount(String userName, String password) throws RemoteException;
    public boolean enquiry(String userName, String password) throws RemoteException;
    public boolean bookTicket(int passenger_ID, String source, String destination, String plane_name, int num_of_seats) throws RemoteException, SQLException;
    public boolean getOptions(char server) throws RemoteException;
    public void setToken(boolean set, char server) throws RemoteException;
    public boolean cancelTicket(int ticket_ID, int passenger_id) throws RemoteException, SQLException;

    // For Admin
    public boolean admin_authentication(String username, String password) throws RemoteException, SQLException;
    public boolean add_ticket(int ticket_ID, String source, String destination, int num_of_seats, String plane_name) throws RemoteException,SQLException;
    public boolean update_ticket(int ticket_ID, String source, String destination, int num_of_seats, String plane_name) throws RemoteException,SQLException;
    public boolean delete_ticket(int ticket_ID) throws RemoteException,SQLException;
}


