import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Robot implements Runnable {
    Kitchen kit;
    int currBattery;
    int[] tablePaths;

    int totalBatteryConsumption = 0;
    int timeWaiting = 0;
    int currentTime = 0;
    ArrayList<Orderinfo> completedOrders;

    public Robot(int[] paths) {
        this.currBattery = 300;
        tablePaths = paths;
        completedOrders = new ArrayList<Orderinfo>();
    }

    public void setKitchen(Kitchen k) {
        kit = k;
    }

    public PathInfo getTablePath(Order o) {
        PathInfo info = new PathInfo();

        info.timeToDeliver = tablePaths[o.tableNumber - 1];

        return info;
    }

    public void startDelivering() throws InterruptedException {
        while (!kit.orderQueue.isEmpty()) {
            Order candidateOrder = kit.orderQueue.poll(100, TimeUnit.MILLISECONDS);

            if (candidateOrder == null)
                break;

            PathInfo pathInfo = getTablePath(candidateOrder);
            Thread.sleep(pathInfo.timeToDeliver);
            if (currBattery < pathInfo.timeToDeliver * 2) {
                // charging up
                currentTime += 60 * currBattery / 300.0;
                timeWaiting += 60 * currBattery / 300.0;
                currBattery = 300;
            }
            currentTime += pathInfo.timeToDeliver * 2;
            currBattery -= pathInfo.timeToDeliver * 2;
            totalBatteryConsumption += pathInfo.timeToDeliver * 2;
            completedOrders.add(new Orderinfo(candidateOrder, pathInfo));
        }
    }

    @Override
    public void run() {
        try {
            startDelivering();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
