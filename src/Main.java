import java.util.List;
import java.util.Scanner;
import java.time.LocalDate;

public class Main {
    private static final String LINE = "----------------------------------------";
    private static final Scanner sc = new Scanner(System.in);
    private static User currentUser = null;

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("         GRAND PERSIA HOTEL");
        System.out.println("========================================");

        while (true){
            if(currentUser == null){
                showLoginMenu();
            }
            else{
                showMainMenu();
            }
        }
    }

    private static void showLoginMenu(){
        System.out.println();
        System.out.println("========== LOGIN ==========");
        System.out.println(LINE);

        System.out.print("Username > ");
        String username = sc.nextLine();

        System.out.print("Password > ");
        String password = sc.nextLine();

        User user = HotelService.login(username, password);

        if(user != null){
            currentUser = user;

            System.out.println();
            System.out.println("[SUCCESS] Login successful!");
            System.out.println("Welcome, " + user.getFullName());
            System.out.println("Role : " + user.getRole());
        }
        else{
            System.out.println("[ERROR] Invalid username or password.");
        }
    }

    private static void showMainMenu(){
        System.out.println();
        System.out.println("======== MAIN MENU ========");
        System.out.println(LINE);
        System.out.println("1. View All Rooms");
        System.out.println("2. Search Room");
        System.out.println("3. Create Reservation");
        System.out.println("4. Add Extra Service");
        System.out.println("5. Logout");
        System.out.println(LINE);

        System.out.print("Your choice > ");
        String choice = sc.nextLine();

        switch (choice){
            case "1" -> showAllRooms();
            case "2" -> searchRooms();
            case "3" -> createNewReservation();
            case "4" -> addService();
            case "5" -> {
                currentUser = null;
                System.out.println("[INFO] Logged out successfully.");
            }

            default -> System.out.println("[ERROR] Invalid choice! Try again.");
        }
    }

    private static void showAllRooms(){
        System.out.println();
        System.out.println("======== ROOM LIST ========");
        System.out.println(LINE);

        for(Room room : HotelService.Rooms){
            System.out.printf("Room %-4s | %-10s | %-10s | $%.2f\n",room.getRoomNumber(), room.getType(),room.getStatus(), room.getBasePrice());
        }
    }

    private static void searchRooms(){
        System.out.println();
        System.out.println("======= SEARCH ROOM =======");
        System.out.println(LINE);

        System.out.print("Search > ");
        String query = sc.nextLine();

        HotelService service = new HotelService();
        List<Room> results = service.search(query);

        System.out.println();
        System.out.println("====== SEARCH RESULT ======");
        if(results.isEmpty()){
            System.out.println("[INFO] No rooms found.");
        }
        else{
            for (Room room : results){
                System.out.printf("Room %-4s | %-10s | %-10s%n", room.getRoomNumber(), room.getType(), room.getStatus());
            }
        }
    }

    private static void createNewReservation(){
        System.out.println();
        System.out.println("==== CREATE RESERVATION ====");
        System.out.println(LINE);

        try {
            System.out.print("Guest Full Name > ");
            String name = sc.nextLine();
            System.out.print("Guest National ID (10 digits) > ");
            String nationalId = sc.nextLine();
            System.out.print("Guest Phone > ");
            String phone = sc.nextLine();
            System.out.print("Create Guest Username > ");
            String guestUser = sc.nextLine();
            System.out.print("Create Guest Password (min 8 chars) > ");
            String guestPass = sc.nextLine();

            Guest guest = new Guest(name, nationalId, phone, guestUser, guestPass);

            System.out.print("Enter Room Number > ");
            String roomNum = sc.nextLine();

            Room targetRoom = null;
            for (Room room : HotelService.Rooms){
                if (room.getRoomNumber().equals(roomNum)){
                    targetRoom = room;
                    break;
                }
            }

            if (targetRoom == null){
                System.out.println("[ERROR] Room not found!");
                return;
            }

            if (targetRoom.getStatus() != RoomStatus.AVAILABLE){
                System.out.println("[INFO] Room is not available. Adding guest to waitlist...");
                HotelService.waitlistManager.addToList(roomNum, guest);
                return;
            }

            System.out.print("Check-in Date (YYYY-MM-DD) > ");
            LocalDate checkIn = LocalDate.parse(sc.nextLine());
            System.out.print("Check-out Date (YYYY-MM-DD) > ");
            LocalDate checkOut = LocalDate.parse(sc.nextLine());
            System.out.print("Number of Guests > ");
            int guestsCount = Integer.parseInt(sc.nextLine());

            String reservationId = "R-" + (HotelService.Reservations.size() + 1000);
            Reservation reservation = new Reservation(reservationId, guest, targetRoom, checkIn, checkOut, guestsCount);

            HotelService.Reservations.add(reservation);
            guest.addReservation(reservation);

            Invoice invoice = new Invoice(reservation);
            System.out.println();
            System.out.println("[SUCCESS] Reservation Created Successfully!");
            System.out.println(invoice.exportToText());
        }
        catch (java.time.format.DateTimeParseException e){
            System.out.println("[ERROR] Invalid date format! Please use YYYY-MM-DD.");
        }
        catch (IllegalArgumentException e){
            System.out.println("[ERROR] Validation failed: " + e.getMessage());
        }
        catch (Exception e){
            System.out.println("[ERROR] Something went wrong: " + e.getMessage());
        }
    }

    private static void addService(){
        System.out.println();
        System.out.println("====== EXTRA SERVICES ======");
        System.out.println(LINE);

        if (HotelService.Reservations.isEmpty()){
            System.out.println("[INFO] No active reservations found to add services.");
            return;
        }

        System.out.print("Enter Reservation ID > ");
        String resId = sc.nextLine();

        Reservation targetRes = null;
        for (Reservation reservation : HotelService.Reservations){
            if (reservation.getReservationId().equalsIgnoreCase(resId)){
                reservation.getReservationId();
                targetRes = reservation;
                break;
            }
        }

        if (targetRes == null){
            System.out.println("[ERROR] Reservation not found!");
            return;
        }

        System.out.println("Select Service Type:");
        System.out.println("1. Minibar ($50)");
        System.out.println("2. Extra Cleaning ($30)");
        System.out.println("3. Parking ($20)");
        System.out.println("4. Room Service ($45)");
        System.out.print("Choice > ");
        String serviceChoice = sc.nextLine();

        ServiceType serviceType;
        double servicePrice;

        switch (serviceChoice){
            case "1" -> {
                serviceType = ServiceType.MINIBAR;
                servicePrice = 50.0;
            }
            case "2" -> {
                serviceType = ServiceType.EXTRA_CLEANING;
                servicePrice = 30.0;
            }
            case "3" -> {
                serviceType = ServiceType.PARKING;
                servicePrice = 20.0;
            }
            case "4" -> {
                serviceType = ServiceType.ROOM_SERVICE;
                servicePrice = 45.0;
            }
            default -> {
                System.out.println("[ERROR] Invalid service selection.");
                return;
            }
        }

        String serviceId = "S-" + (targetRes.getServices().size() + 100);
        Service extraService = new Service(serviceId, serviceType, servicePrice);
        targetRes.getServices().add(extraService);

    }
}