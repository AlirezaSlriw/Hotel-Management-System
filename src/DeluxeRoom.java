public class DeluxeRoom extends Room{

    public DeluxeRoom(String roomNumber, RoomStatus status, double basePrice, int floorNumber, int capacity){
        super(roomNumber, RoomType.DELUXE, status, basePrice, floorNumber, capacity);
    }

    @Override
    protected double getTypeMultiplier() {
        return 1.5;
    }

}
