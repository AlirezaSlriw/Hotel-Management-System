import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class HotelService implements Searchable<Room>{
    public static final List<Room> Rooms = new ArrayList<>();
    public static final List<User> Users = new ArrayList<>();
    public static final List<Reservation> Reservations = new ArrayList<>();

    public static final WaitlistManager waitlistManager = new WaitlistManager();
    public static final List<MaintenanceRequest> MaintenanceRequests = new ArrayList<>();

    static {
        try {
            SuperAdmin defaultAdmin = new SuperAdmin(
                    "Super Admin",
                    "1111111111",
                    "09000000000",
                    "admin",
                    "admin123",
                    "ADMIN001",
                    LocalDate.of(1970, 1, 1)
            );
            Users.add(defaultAdmin);
            waitlistManager.registerObserver(waitlistManager);
        }
        catch (Exception e){
            System.out.println("[SYSTEM ERROR]: Default admin creation failed:" + e.getMessage());
        }


        //(floor = 5 && roomNum = 5) -> I'm going back to 505...If it's a seven hour flight or a forty-five minute drive...
        for (int floor = 1; floor <= 5; floor++){
            for (int roomNum = 1; roomNum <= 10; roomNum++){
                String fullRoomNumber = floor + String.format("%02d", roomNum);
                if (floor == 5){
                    Rooms.add(new PentHouse(fullRoomNumber, RoomStatus.AVAILABLE, 800, floor, 6));
                }
                else if (floor == 4 || floor == 3){
                    Rooms.add(new Suite(fullRoomNumber, RoomStatus.AVAILABLE, 400, floor, 4));
                }
                else if (floor == 2){
                    Rooms.add(new DeluxeRoom(fullRoomNumber, RoomStatus.AVAILABLE, 200, floor, 3));
                }
                else{
                    Rooms.add(new StandardRoom(fullRoomNumber, RoomStatus.AVAILABLE, 100, floor, 2));
                }
            }
        }
    }

    public static User login(String username, String password){
        for(User user : Users){
            if(user.getUsername().equals(username) && user.getPassword().equals(password)){
                return user;
            }
        }

        return null;
    }

    @Override
    public List<Room> search(String query) {
        List<Room> targetRooms = new ArrayList<>();

        for(Room room : Rooms){
            if(room.getRoomNumber().contains(query) || room.getType().name().equalsIgnoreCase(query)){
                targetRooms.add(room);
            }
        }
        return targetRooms;
    }

    @Override
    public List<Room> filter(Predicate<Room> predicate) {
        List<Room> filteredRooms = new ArrayList<>();

        for(Room room : Rooms){
            if(predicate.test(room)){
                filteredRooms.add(room);
            }
        }
        return filteredRooms;
    }


}
