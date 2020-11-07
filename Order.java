public class Order implements Comparable<Order> {
    int orderTime;
    int tableNumber;
    int prepTime;

    @Override
    public int compareTo(Order other) {
        int readyTime = orderTime + prepTime;
        int otherReadyTime = other.orderTime + other.prepTime;

        return readyTime - otherReadyTime;
    }

    public Order(int orderTime, int tableNumber, int prepTime) {
        this.orderTime = orderTime;
        this.tableNumber = tableNumber;
        this.prepTime = prepTime;
    }
}
