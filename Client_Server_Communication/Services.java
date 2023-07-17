import java.rmi.*;

public interface Services extends Remote {
    public boolean authenticate(String userName, String password) throws RemoteException;
    public boolean createAccount(String userName, String password) throws RemoteException;
    public void enquiry(String userName, String password) throws RemoteException;
    public boolean bookTicket(String userName, String password, String source, String destination) throws  RemoteException;
    public boolean cancelTicket(String userName , String password) throws RemoteException;
    public boolean checkStatus(String userName , String password, int num) throws  RemoteException;
}


