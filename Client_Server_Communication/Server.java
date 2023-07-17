import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

// unicast remote object is a remote object that can only be accessed by one client at a time
public class Server extends UnicastRemoteObject implements Services {
    public Server() throws RemoteException {
        super();
    }

    //can use hashmaps,
    public static ArrayList<Passenger> P = new ArrayList<Passenger>();

    @Override
    public boolean authenticate(String username, String password) throws RemoteException {
        System.out.println("Authenticating...");
        boolean isValidUser = false;

        for(int i = 0 ; i < P.size() ; i++){
            if(P.get(i).authenticate(username, password)){
                System.out.println("Checking....");
                System.out.println(P.get(i));
                isValidUser = true;
                break;
            }
        }
        return isValidUser;
    }

    @Override
    public boolean createAccount(String username, String password) throws RemoteException {
        boolean success = false;
        try{
            Passenger p = new Passenger(username, password);
            P.add(p);
            success = true;
        }catch (Exception e){
            System.out.println(e);
        }
        return success;
    }

    @Override
    public void enquiry(String userName, String password){
        displayList();
    }

    @Override
    public boolean bookTicket(String userName, String password, String source, String destination) throws RemoteException {
        System.out.println("Service will be provided soon....");
        return false;
    }

    @Override
    public boolean cancelTicket(String userName, String password) throws RemoteException {
        System.out.println("Service will be provided soon....");
        return false;
    }

    @Override
    public boolean checkStatus(String userName, String password, int num) throws RemoteException {
        System.out.println("Service will be provided soon....");
        return false;
    }

    public void displayList(){
        System.out.println("List will be updated soon...");
    }


    public static void main(String[] args) {
        String serverName = "Airline Reservation System";

        try {
            Registry reg = LocateRegistry.createRegistry(8000);
            reg.rebind(serverName, new Server());
            System.out.println("Server is running...");

            Passenger p1 = new Passenger("Admin", "Admin@123");
            P.add(p1);

        } catch (Exception e) {

            e.printStackTrace();
        }
    }


}

class Passenger{
    private String userName;
    private String password;


    Passenger(String userName, String password){
        this.password = password;
        this.userName = userName;
    }

    public boolean authenticate(String userName, String password){
        return this.userName.equals(userName) && this.password.equals(password);
    }
}