import java.util.*;

public class WaitlistManager implements RoomObserver, Notifiable {
    private final Map<String, Queue<Guest>> waitlists = new HashMap<>();
    private final List<RoomObserver> observers = new ArrayList<>();

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
}
