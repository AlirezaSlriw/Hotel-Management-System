public class Invoice implements Billable, Exportable{
    private String invoiceId;
    private Reservation reservation;
    private double totalAmount;
    private double amountPaid;
    private boolean paymentCompleted;
    private static int idCounter = 1000;

    public Invoice(Reservation reservation){
        this.invoiceId = "I-" + (++idCounter);
        this.reservation = reservation;
        this.amountPaid = 0.0;
        this.paymentCompleted = false;
        this.totalAmount = calculateTotal();
    }


    @Override
    public double calculateTotal() {
        double basePrice = reservation.getTotalPrice();

        double servicePrice = 0;
        if(reservation.getServices() != null){
            for(Service service : reservation.getServices()){
                servicePrice += service.getPrice();
            }
        }

        double tempTotal = basePrice + servicePrice;

        double discountPercent = 0.0;
        switch (reservation.getGuest().getMembershipLevel()){
            case SILVER -> discountPercent = 0.05;
            case GOLD -> discountPercent = 0.10;
            case PLATINUM -> discountPercent = 0.15;
            default -> discountPercent = 0.0;
        }

        double discountAmount = tempTotal * discountPercent;
        double priceWithDiscount = tempTotal - discountAmount;

        double municipalCharges = priceWithDiscount * 0.05;
        double valueAddedTax = priceWithDiscount * 0.09;

        return priceWithDiscount + municipalCharges + valueAddedTax;
    }

    @Override
    public void applyDiscount(double percentage) {
        if(percentage > 0 && percentage <= 100){
            this.totalAmount -= (this.totalAmount * (percentage / 100.0));
        }
    }

    public void processPayment(Double amount) throws InsufficientPaymentException{
        if(amount <= 0){
            throw new IllegalArgumentException("Payment amount must be greater than 0.");
        }

        this.amountPaid += amount;

        if(this.amountPaid >= this.totalAmount){
            this.paymentCompleted = true;
            System.out.println("[FINANCIAL SYSTEM] Payment completed successfully.");
        }
        else{
            System.out.printf("[FINANCIAL SYSTEM] Partial payment received. Outstanding balance: %.2f\n", (totalAmount - amountPaid));
        }
    }

    public void ensurePaymentCompleted() throws InsufficientPaymentException{
        if(!paymentCompleted){
            throw new InsufficientPaymentException("[FINANCIAL SYSTEM] Invoice is not fully paid. Outstanding balance: " + (totalAmount - amountPaid));
        }
    }

    @Override
    public String exportToText() {
        String template =
                "=========================================\n" +
                        "             GRAND PERSIA HOTEL          \n" +
                        "               FINAL INVOICE             \n" +
                        "=========================================\n" +
                        "Invoice ID:      %s\n" +
                        "Guest Name:      %s\n" +
                        "Membership:      %s\n" +
                        "Room Number:     %s\n" +
                        "-----------------------------------------\n" +
                        "Total Amount:    %.2f\n" +
                        "Amount Paid:     %.2f\n" +
                        "Payment Status:  %s\n" +
                        "=========================================\n";

        return String.format(template,
                invoiceId,
                reservation.getGuest().getFullName(),
                reservation.getGuest().getMembershipLevel().toString(),
                reservation.getRoom().getRoomNumber(),
                totalAmount,
                amountPaid,
                paymentCompleted ? "PAID IN FULL" : "PAYMENT PENDING"
        );
    }

    public double getTotalAmount(){
        return totalAmount;
    }
    public double getPaidAmount(){
        return amountPaid;
    }
    public boolean isPaymentCompleted(){
        return paymentCompleted;
    }
}
