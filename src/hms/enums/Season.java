package hms.enums;

public enum Season {
    OFF(0.8),
    NORMAL(1.2),
    PEAK(1.5);

    private double multiplier;

    Season(double multiplier){
        this.multiplier = multiplier;
    }

    public double getMultiplier(){
        return this.multiplier;
    }

    public void setMultiplier(double multiplier){
        if(multiplier <= 0){
            throw new IllegalArgumentException("Multiplier must be greater than zero!");
        }
        this.multiplier = multiplier;
    }
}
