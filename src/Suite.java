public class Suite extends Room{

    public Suite(String roomNumber, RoomStatus status, double basePrice, int floorNumber, int capacity){
        super(roomNumber, RoomType.SUITE, status, basePrice, floorNumber, capacity);
    }

    @Override
    protected double getTypeMultiplier() {
        return 2.5;
    }
}
