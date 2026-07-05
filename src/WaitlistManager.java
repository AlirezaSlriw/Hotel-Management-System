import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class WaitlistManager implements RoomObserver{
    private final Map<String, Queue<Guest>> waitlists = new HashMap<>();

    public void addToList(String roomNum, Guest guest){
        waitlists.putIfAbsent(roomNum, new LinkedList<>());
        waitlists.get(roomNum).add(guest);
        System.out.printf("%s added to room %s Waitlist.\n", guest.getFullName(), roomNum);
    }

    @Override
    public void whenRoomAvailable(Room room) {
        Queue<Guest> queue = waitlists.get(room.getRoomNumber());
        if(queue != null && !queue.isEmpty()){
            Guest nextGuest = queue.poll();
            System.out.printf("[SYSTEM]: ROOM %s IS AVAILABLE, GUEST NOTIFIED.\n", room.getRoomNumber());
        }
    }
}
