package hms.model.reservation;
import hms.interfaces.Billable;
import hms.interfaces.Exportable;
import hms.exceptions.InsufficientPaymentException;
import java.io.Serializable;

public class Invoice implements Billable, Exportable, Serializable {
    private String invoiceId;
    private Reservation reservation;
    private double totalAmount;
    private double amountPaid;
    private boolean paymentCompleted;
    private static double municipalTaxRate = 0.01;
    private static double vatRate = 0.09;
    private static int idCounter = 1000;


    public Invoice(Reservation reservation){
        this.invoiceId = "I-" + (++idCounter);
        this.reservation = reservation;
        this.paymentCompleted = false;
        this.totalAmount = calculateTotal();

        this.amountPaid = reservation.getDepositPaid();
        if(this.amountPaid >= this.totalAmount){
            this.paymentCompleted = true;
        }
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

        double discountPercent = reservation.getGuest().getMembershipLevel().getDiscountRate();


        double discountAmount = tempTotal * discountPercent;
        double priceWithDiscount = tempTotal - discountAmount;

        double municipalCharges = priceWithDiscount * municipalTaxRate;
        double valueAddedTax = priceWithDiscount * vatRate;

        return priceWithDiscount + municipalCharges + valueAddedTax;
    }

    public void recalculate(){
        this.totalAmount = calculateTotal();
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
        double baseStayCost = reservation.getTotalPrice();

        double servicesCost = 0;
        if(reservation.getServices() != null){
            for(Service service : reservation.getServices()){
                servicesCost += service.getPrice();
            }
        }

        double subtotal = baseStayCost + servicesCost;
        double discountRate = reservation.getGuest().getMembershipLevel().getDiscountRate();
        double discountAmount = subtotal * discountRate;
        double afterDiscount = subtotal - discountAmount;
        double municipalAmount = afterDiscount * municipalTaxRate;
        double vatAmount = afterDiscount * vatRate;

        long nights = reservation.getCheckOutDate().toEpochDay() - reservation.getCheckInDate().toEpochDay();

        return String.format(
                "================================================================\n" +
                        "                   GRAND PERSIA HOTEL                          \n" +
                        "                      FINAL INVOICE                            \n" +
                        "================================================================\n" +
                        "Invoice ID    : %s\n" +
                        "Guest Name    : %s\n" +
                        "Membership    : %s\n" +
                        "Room          : %s (%s)\n" +
                        "Check-In      : %s\n" +
                        "Check-Out     : %s\n" +
                        "Nights        : %d\n" +
                        "----------------------------------------------------------------\n" +
                        "ITEMIZED CHARGES\n" +
                        "----------------------------------------------------------------\n" +
                        "Stay Cost     : $%,12.2f\n" +
                        "Services      : $%,12.2f\n" +
                        "Subtotal      : $%,12.2f\n" +
                        "Discount (%s) : -$%,11.2f\n" +
                        "After Discount: $%,12.2f\n" +
                        "----------------------------------------------------------------\n" +
                        "Municipal Tax : $%,12.2f  (%.0f%%)\n" +
                        "VAT           : $%,12.2f  (%.0f%%)\n" +
                        "----------------------------------------------------------------\n" +
                        "TOTAL AMOUNT  : $%,12.2f\n" +
                        "Amount Paid   : $%,12.2f\n" +
                        "Balance Due   : $%,12.2f\n" +
                        "Status        : %s\n" +
                        "================================================================\n",
                invoiceId,
                reservation.getGuest().getFullName(),
                reservation.getGuest().getMembershipLevel(),
                reservation.getRoom().getRoomNumber(),
                reservation.getRoom().getType(),
                reservation.getCheckInDate(),
                reservation.getCheckOutDate(),
                nights,
                baseStayCost,
                servicesCost,
                subtotal,
                String.format("%.0f%%", discountRate * 100),
                discountAmount,
                afterDiscount,
                municipalAmount, municipalTaxRate * 100,
                vatAmount, vatRate * 100,
                totalAmount,
                amountPaid,
                Math.max(0, totalAmount - amountPaid),
                paymentCompleted ? "PAID IN FULL" : "PAYMENT PENDING"
        );
    }

    public static void setTaxRates(double municipalRate, double vatRate){
        if(municipalRate < 0 || vatRate < 0){
            throw new IllegalArgumentException("Tax rates cannot be negative!");
        }
        Invoice.municipalTaxRate = municipalRate;
        Invoice.vatRate = vatRate;
    }

    public static double getMunicipalTaxRate(){ return municipalTaxRate; }
    public static double getVatRate(){ return vatRate; }
    public double getTotalAmount(){
        return totalAmount;
    }
    public double getPaidAmount(){
        return amountPaid;
    }
    public boolean isPaymentCompleted(){
        return paymentCompleted;
    }
    public static int getIdCounter(){ return idCounter; }
    public static void setIdCounter(int counter){ idCounter = counter; }
}
