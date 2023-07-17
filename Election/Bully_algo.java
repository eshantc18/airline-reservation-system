import java.io.*;
import java.util.Scanner;

class Bully_algo {
    static int n;
    static int priority[] = new int[10];
    static int status[] = new int[10];
    static int coordinator;

    public static void main(String args[]) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter the number of process : ");
        n = sc.nextInt();
        for (int i = 0; i < n; i++) {
            System.out.println("\nFor process " + (i + 1) + ":");
            System.out.println("Status:");
            status[i] = sc.nextInt();
            System.out.println("Priority");
            priority[i] = sc.nextInt();
        }
        System.out.print("Which process will initiate election ? : ");
        int isElected = sc.nextInt();
        elect(isElected);
        System.out.println("Final coordinator is " + coordinator);
        sc.close();
    }

    static void elect(int isElected) {
        isElected = isElected - 1;
        coordinator = isElected + 1;
        for (int i = 0; i < n; i++) {
            if (priority[isElected] < priority[i]) {
                System.out.println("Election message is sent from " + (isElected + 1) + " to " + (i + 1));
                if (status[i] == 1)
                    elect(i + 1);
            }
        }
    }
}