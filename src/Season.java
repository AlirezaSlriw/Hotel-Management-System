public enum Season {
    OFF(0.8),
    NORMAL(1.2),
    PEAK(1.5);

    private final double multiplier;

    Season(double multiplier){
        this.multiplier = multiplier;
    }

    public double getMultiplier(){
        return this.multiplier;
    }
}
