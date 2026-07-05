public interface Billable {
    double calculateTotal();
    void applyDiscount(double percentage);
}
