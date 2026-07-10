package hms.model.room;
import hms.enums.RoomStatus;
import hms.enums.RoomType;
import java.io.Serializable;

public class Suite extends Room implements Serializable {
    private static double typeMultiplier = 2.5;

    public Suite(String roomNumber, RoomStatus status, double basePrice, int floorNumber, int capacity){
        super(roomNumber, RoomType.SUITE, status, basePrice, floorNumber, capacity);
    }

    @Override
    protected double getTypeMultiplier() {
        return typeMultiplier;
    }

    public static void setTypeMultiplier(double multiplier){
        if(multiplier <= 0){
            throw new IllegalArgumentException("Multiplier must be greater than zero!");
        }
        typeMultiplier = multiplier;
    }

    public static double getStaticTypeMultiplier(){
        return typeMultiplier;
    }
}
