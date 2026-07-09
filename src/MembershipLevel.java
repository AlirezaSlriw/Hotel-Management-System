public enum MembershipLevel {
    BRONZE(0.0),
    SILVER(0.05),
    GOLD(0.10),
    PLATINUM(0.15);

    private double discountRate;

    MembershipLevel(double discountRate){
        this.discountRate = discountRate;
    }

    public double getDiscountRate(){
        return this.discountRate;
    }

    public void setDiscountRate(double discountRate){
        if(discountRate < 0 || discountRate > 1){
            throw new IllegalArgumentException("Discount rate must be between 0 and 1!");
        }
        this.discountRate = discountRate;
    }
}
