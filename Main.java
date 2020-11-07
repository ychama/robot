import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.Math;

public class Main {

    static int passTime, ATime, deliveryTime, deliveryPoints;

    static String[][] resMap;

    static Kitchen kitchen;

    public static void main(String[] args) {
        while (true) {
            getInput();
            int[][] shortestPaths = findShortestPaths();

            int[] tablePaths = getTablePaths(shortestPaths);

            Robot r1 = new Robot(tablePaths);
            r1.setKitchen(kitchen);

            Robot r2 = new Robot(tablePaths);
            r2.setKitchen(kitchen);

            Thread t1 = new Thread(r1);
            Thread t2 = new Thread(r2);

            t2.start();
            t1.start();

            try {
                t1.join();
                t2.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            printRobotsOrders(r1, r2);
        }
    }

    public static void printRobotsOrders(Robot r1, Robot r2) {
        System.out.println("               Robot 1                ");
        System.out.println("-----------------------------------------");
        int i = 0;
        for (Order o : r1.completedOrders) {
            i++;
            System.out.println("Order " + i + " is table number " + o.tableNumber);
            System.out.println();
        }

        System.out.println("Robot 1 total battery consumption: " + r1.totalBatteryConsumption + "sec");
        System.out.println("Robot 1 total time waiting: " + r1.timeWaiting + "sec");
        System.out.println("Robot 1 total time: " + r1.currentTime + "sec");

        System.out.println();
        System.out.println();

        System.out.println("               Robot 2                ");
        System.out.println("-----------------------------------------");

        i = 0;
        for (Order o : r2.completedOrders) {
            i++;
            System.out.println("Order " + i + " is table number " + o.tableNumber);
            System.out.println();
        }

        System.out.println("Robot 2 total battery consumption: " + r2.totalBatteryConsumption + "sec");
        System.out.println("Robot 2 total time waiting: " + r2.timeWaiting + "sec");
        System.out.println("Robot 2 total time: " + r2.currentTime + "sec");
        System.out.println();
        System.out.println("-----------------------------------------");

    }

    public static int[] getTablePaths(int[][] shortestPaths) {
        int[] tablePaths = new int[15];
        Arrays.fill(tablePaths, Integer.MAX_VALUE);

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (resMap[i][j].equals("0") || resMap[i][j].equals("A") || resMap[i][j].equals("K"))
                    continue;

                int tableNumber = Integer.parseInt(resMap[i][j]);
                tablePaths[tableNumber - 1] = Math.min(shortestPaths[i][j], tablePaths[tableNumber - 1]);
            }
        }
        return tablePaths;
    }

    public static int[][] findShortestPaths() {
        // initializing paths
        int[][] shortestPaths = new int[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                shortestPaths[i][j] = Integer.MAX_VALUE;
            }
        }
        shortestPaths[0][0] = 0;

        // performing dijkstra's (kinda)
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                int currDistance = shortestPaths[i][j];

                // look to the right neighbour
                if (j != 7) {
                    if (resMap[i][j + 1].equals("A"))
                        shortestPaths[i][j + 1] = Math.min(currDistance + ATime, shortestPaths[i][j + 1]);
                    else if (resMap[i][j + 1].equals("0"))
                        shortestPaths[i][j + 1] = Math.min(currDistance + passTime, shortestPaths[i][j + 1]);
                    else if (resMap[i][j + 1].equals("K"))
                        ;
                    else
                        shortestPaths[i][j + 1] = Math.min(currDistance + deliveryTime, shortestPaths[i][j + 1]);
                }

                // look to the bottom neighbour
                if (i != 7) {
                    if (resMap[i + 1][j].equals("A"))
                        shortestPaths[i + 1][j] = Math.min(ATime + currDistance, shortestPaths[i + 1][j]);
                    else if (resMap[i + 1][j].equals("0"))
                        shortestPaths[i + 1][j] = Math.min(currDistance + passTime, shortestPaths[i + 1][j]);
                    else if (resMap[i + 1][j].equals("K"))
                        ;
                    else
                        shortestPaths[i + 1][j] = Math.min(currDistance + deliveryTime, shortestPaths[i + 1][j]);
                }

                // look to the left neighbour
                if (j != 0) {
                    if (resMap[i][j - 1].equals("A"))
                        shortestPaths[i][j - 1] = Math.min(ATime + currDistance, shortestPaths[i][j - 1]);
                    else if (resMap[i][j - 1].equals("0"))
                        shortestPaths[i][j - 1] = Math.min(currDistance + passTime, shortestPaths[i][j - 1]);
                    else if (resMap[i][j - 1].equals("K"))
                        ;
                    else
                        shortestPaths[i][j - 1] = Math.min(currDistance + deliveryTime, shortestPaths[i][j - 1]);
                }

                // look to the top neighbour
                if (i != 0) {
                    if (resMap[i - 1][j].equals("A"))
                        shortestPaths[i - 1][j] = Math.min(ATime + currDistance, shortestPaths[i - 1][j]);
                    else if (resMap[i - 1][j].equals("0"))
                        shortestPaths[i - 1][j] = Math.min(currDistance + passTime, shortestPaths[i - 1][j]);
                    else if (resMap[i - 1][j].equals("K"))
                        ;
                    else
                        shortestPaths[i - 1][j] = Math.min(currDistance + deliveryTime, shortestPaths[i - 1][j]);
                }
            }
        }
        return shortestPaths;
    }

    public static void getInput() {
        System.out.print("Please Enter the name of the input file or exit if you wish to terminate the program: ");
        resMap = new String[8][8];
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        File myObj = null;
        Scanner scan = null;
        try {
            String line = reader.readLine();
            if (line.toLowerCase().equals("exit"))
                System.exit(1);
            myObj = new File(line);
            scan = new Scanner(myObj);
        } catch (IOException e) {
            System.err.println(e);
        }

        passTime = scan.nextInt();
        ATime = scan.nextInt();
        deliveryTime = scan.nextInt();
        deliveryPoints = scan.nextInt();
        scan.nextLine();

        for (int i = 0; i < 8; i++) {
            String row = scan.nextLine();
            String[] rowValues = row.split(" ");

            for (int j = 0; j < 8; j++) {
                resMap[i][j] = rowValues[j];
            }
        }

        ArrayList<Order> cusOrders = new ArrayList<Order>();
        while (scan.hasNextInt()) {
            Order temp = new Order(scan.nextInt(), scan.nextInt(), scan.nextInt());
            cusOrders.add(temp);
        }
        kitchen = new Kitchen(cusOrders);

        scan.close();
    }
}