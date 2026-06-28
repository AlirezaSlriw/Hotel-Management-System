public class PentHouse extends Room{

    public PentHouse(String roomNumber, RoomStatus status, double basePrice, int floorNumber, int capacity){
        super(roomNumber, RoomType.PENTHOUSE, status, basePrice, floorNumber, capacity);
    }

    @Override
    protected double getTypeMultiplier() {
        return 5.0;
    }
}
