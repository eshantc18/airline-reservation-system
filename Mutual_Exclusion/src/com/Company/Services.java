package com.Company;
import java.rmi.*;
import java.sql.SQLException;

public interface Services extends Remote {
    public char authenticate(String userName, String password) throws RemoteException, SQLException;
    public boolean createAccount(String userName, String password) throws RemoteException;
    public boolean enquiry(String userName, String password) throws RemoteException;
    public boolean bookTicket(String source, String destination, String plane_name, int num_of_seats) throws RemoteException, SQLException;
    public boolean getOptions(char server) throws RemoteException;
    public void setToken(boolean set, char server) throws RemoteException;
    public boolean cancelTicket(int ticket_ID, int passenger_id) throws RemoteException, SQLException;
    public boolean checkStatus(String userName , String password, int num) throws  RemoteException;
}


