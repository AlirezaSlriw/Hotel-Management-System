package hms.model.room;
import hms.enums.RoomStatus;
import hms.enums.RoomType;
import java.io.Serializable;

public class PentHouse extends Room implements Serializable {
    private static double typeMultiplier = 5.0;

    public PentHouse(String roomNumber, RoomStatus status, double basePrice, int floorNumber, int capacity){
        super(roomNumber, RoomType.PENTHOUSE, status, basePrice, floorNumber, capacity);
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
