import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class HotelService implements Searchable<Room>{
    public static final List<Room> Rooms = new ArrayList<>();
    public static final List<User> Users = new ArrayList<>();
    public static final List<Reservation> Reservations = new ArrayList<>();

    public static final WaitlistManager waitlistManager = new WaitlistManager();

    static {
        try {
            SuperAdmin defaultAdmin = new SuperAdmin(
                    "Default",
                    "1111111111",
                    "09000000000",
                    "admin",
                    "GoodLuckCrackingThisOne",
                    "IDK123",
                    LocalDate.of(1970, 1, 1)
            );
            Users.add(defaultAdmin);
        }
        catch (Exception e){
            System.out.println("[SYSTEM]: SOMETHING WENT WRONG WHILE CREATOMG THE DEFAULT USER: " + e.getMessage());
        }

        Rooms.add(new StandardRoom("101", RoomStatus.AVAILABLE, 250, 1, 2));
        Rooms.add(new StandardRoom("102", RoomStatus.AVAILABLE, 250, 1, 2));
        Rooms.add(new DeluxeRoom("201", RoomStatus.AVAILABLE, 325, 2, 3));
        Rooms.add(new Suite("301", RoomStatus.AVAILABLE, 375, 3, 4));
        Rooms.add(new PentHouse("501", RoomStatus.MAINTENANCE, 450, 5, 6));
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
