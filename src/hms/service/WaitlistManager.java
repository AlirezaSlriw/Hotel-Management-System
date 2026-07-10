package hms.service;
import hms.interfaces.Notifiable;
import hms.interfaces.RoomObserver;
import hms.model.person.Guest;
import hms.model.room.Room;
import java.util.*;

public class WaitlistManager implements RoomObserver, Notifiable {
    private final Map<String, Queue<Guest>> waitlists = new HashMap<>();
    private transient List<RoomObserver> observers = new ArrayList<>();


    private void readObject(java.io.ObjectInputStream in)
            throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.observers = new ArrayList<>();
    }

    @Override
    public void registerObserver(RoomObserver observer){
        if (!observers.contains(observer)){
            observers.add(observer);
        }
    }

    @Override
    public void removeObserver(RoomObserver observer){
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(Room room){
        for (RoomObserver observer : observers){
            observer.whenRoomAvailable(room);
        }
    }

    public void initObservers(){
        if (this.observers == null){
            this.observers = new ArrayList<>();
        }
        this.registerObserver(this);
    }

    public void addToList(String roomNum, Guest guest){
        waitlists.putIfAbsent(roomNum, new LinkedList<>());
        waitlists.get(roomNum).add(guest);
        System.out.printf("[WAITLIST] %s added to waitlist for room %s.\n",
                guest.getFullName(), roomNum);
    }

    @Override
    public void whenRoomAvailable(Room room){
        Queue<Guest> queue = waitlists.get(room.getRoomNumber());
        if(queue != null && !queue.isEmpty()){
            Guest nextGuest = queue.poll();
            System.out.printf("[NOTIFICATION] Room %s is now available. Notifying: %s\n",
                    room.getRoomNumber(), nextGuest.getFullName());
        }
    }

    public Map<String, Queue<Guest>> getWaitlists(){
        return waitlists;
    }

    public void loadWaitlists(Map<String, Queue<Guest>> saved){
        this.waitlists.clear();
        this.waitlists.putAll(saved);
    }
}
