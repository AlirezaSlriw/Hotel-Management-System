package hms.interfaces;
import hms.model.room.Room;

public interface Notifiable {
    void registerObserver(RoomObserver observer);
    void removeObserver(RoomObserver observer);
    void notifyObservers(Room room);
}