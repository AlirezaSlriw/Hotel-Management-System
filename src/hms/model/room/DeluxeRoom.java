package hms.model.room;
import hms.enums.RoomStatus;
import hms.enums.RoomType;
import java.io.Serializable;

public class DeluxeRoom extends Room implements Serializable {
    private static double typeMultiplier = 1.5;

    public DeluxeRoom(String roomNumber, RoomStatus status, double basePrice, int floorNumber, int capacity){
        super(roomNumber, RoomType.DELUXE, status, basePrice, floorNumber, capacity);
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
