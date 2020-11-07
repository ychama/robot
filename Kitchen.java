import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Kitchen {
    ArrayList<Order> cusOrders;
    BlockingQueue<Order> orderQueue;

    public Kitchen(ArrayList<Order> orders) {
        this.cusOrders = orders;

        ArrayList<Order> temp = new ArrayList<Order>(cusOrders);
        Collections.sort(temp);
        orderQueue = new LinkedBlockingQueue<Order>(temp);
    }
}
