package hms.service;
import hms.enums.ReservationStatus;
import hms.enums.RoomStatus;
import hms.exceptions.ReservationConflictException;
import hms.interfaces.Searchable;
import hms.model.maintenance.MaintenanceRequest;
import hms.model.person.SuperAdmin;
import hms.model.person.User;
import hms.model.reservation.Reservation;
import hms.model.room.*;
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

    public static void checkForConflict(Room room, LocalDate checkIn, LocalDate checkOut)
            throws ReservationConflictException {
        for (Reservation existing : Reservations){
            if (existing.getRoom().getRoomNumber().equals(room.getRoomNumber()) &&
                    existing.getStatus() != ReservationStatus.CANCELLED &&
                    existing.getStatus() != ReservationStatus.COMPLETED){

                boolean overlaps = checkIn.isBefore(existing.getCheckOutDate()) &&
                        checkOut.isAfter(existing.getCheckInDate());
                if (overlaps){
                    throw new ReservationConflictException(
                            "Room " + room.getRoomNumber() +
                                    " already has a reservation from " +
                                    existing.getCheckInDate() + " to " +
                                    existing.getCheckOutDate() +
                                    " (ID: " + existing.getReservationId() + ")"
                    );
                }
            }
        }
    }
}
