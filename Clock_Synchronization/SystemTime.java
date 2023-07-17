import java.rmi.*;

public interface SystemTime extends Remote{
    long getTime() throws RemoteException;
}