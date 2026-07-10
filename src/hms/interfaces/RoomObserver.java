package hms.interfaces;
import hms.model.room.Room;

public interface RoomObserver {
    void whenRoomAvailable(Room room);
}
