import java.util.Scanner;

class Process {
    public int id;
    public boolean isActive;

    public Process(int id) {
        this.id = id;
        isActive = true;
    }
}

public class Ring {
    int num_of_process;
    Process[] P;
    Scanner sc;

    public Ring() {
        sc = new Scanner(System.in);
    }

    public void initialise() {
        System.out.print("Enter number of processes : ");
        num_of_process = sc.nextInt();
        P = new Process[num_of_process];
        for (int i = 0; i < P.length; i++) {
            P[i] = new Process(i);
        }
    }

    public int get_Max() {
        int max_Id = -99;
        int max_Id_index = 0;
        for (int i = 0; i < P.length; i++) {
            if (P[i].isActive && P[i].id > max_Id) {
                max_Id = P[i].id;
                max_Id_index = i;
            }
        }
        return max_Id_index;
    }

    public void elect() {
        System.out.println("Process No. " + P[get_Max()].id + " fails");
        P[get_Max()].isActive = false;
        System.out.print("Election Initiated by : ");
        int initiatorProcesss = sc.nextInt();
        int prev = initiatorProcesss;
        int next = prev + 1;
        while (true) {
            if (P[next].isActive) {
                System.out.println("\nProcess " + P[prev].id + " pass Election(" + P[prev].id + ") to "
                        + P[next].id);
                prev = next;
            }
            next = (next + 1) % num_of_process;
            if (next == initiatorProcesss) {
                break;
            }
        }
        System.out.println("\n***********Process " + P[get_Max()].id + " becomes coordinator***********");
        int coordinator = P[get_Max()].id;
        prev = coordinator;
        next = (prev + 1) % num_of_process;
        while (true) {
            if (P[next].isActive) {
                System.out.println("\nProcess " + P[prev].id + " pass Coordinator(" + coordinator + ") message to process " + P[next].id);
                prev = next;
            }
            next = (next + 1) % num_of_process;
            if (next == coordinator) {
                System.out.println("\nEnd Of Election ");
                break;
            }
        }
    }

    public static void main(String arg[]) {
        Ring r = new Ring();
        r.initialise();
        r.elect();
    }
}