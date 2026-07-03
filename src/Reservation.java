import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Reservation {
    private String reservationId;
    private Guest guest;
    private Room room;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private ReservationStatus status;
    private LocalDateTime createdAt;
    private double totalPrice;
    private List<Service> services;
    private int numberOfGuests;

    public Reservation(String reservationId, Guest guest, Room room, LocalDate checkInDate, LocalDate checkOutDate, int numberOfGuests) throws InvalidDateRangeException {
        validateDates(checkInDate, checkOutDate);

        setReservationId(reservationId);
        setGuest(guest);
        setRoom(room);

        if (numberOfGuests <= 0){
            throw new IllegalArgumentException("Number of guests must be greater than zero!");
        }
        if (room.getCapacity() < numberOfGuests){
            throw new IllegalArgumentException("There is not enough space for these number of guests in this room!");
        }
        this.numberOfGuests = numberOfGuests;

        this.status = ReservationStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.services = new ArrayList<>();

        calculateTotalPrice();
        this.room.setStatus(RoomStatus.RESERVED);
    }

    public String getReservationId(){
        return this.reservationId;
    }

    public void setReservationId(String reservationId){
        if(reservationId == null || reservationId.trim().isEmpty()){
            throw new IllegalArgumentException("Reservation ID cannot be empty!");
        }
        this.reservationId = reservationId;
    }

    public Guest getGuest(){
        return this.guest;
    }

    public void setGuest(Guest guest){
        if(guest == null){
            throw new IllegalArgumentException("Guest cannot be null!");
        }
        this.guest = guest;
    }

    public Room getRoom(){
        return this.room;
    }

    public void setRoom(Room room){
        if(room == null){
            throw new IllegalArgumentException("Room cannot be null!");
        }
        this.room = room;
    }

    public LocalDate getCheckInDate(){
        return this.checkInDate;
    }

    public LocalDate getCheckOutDate(){
        return this.checkOutDate;
    }

    public ReservationStatus getStatus(){
        return this.status;
    }

    private void setStatus(ReservationStatus status){
        this.status = status;
    }

    public double getTotalPrice(){
        return this.totalPrice;
    }

    public LocalDateTime getCreatedAt(){
        return this.createdAt;
    }

    public List<Service> getServices(){
        return this.services;
    }

    public void addService(Service service){
        if(this.services == null){
            this.services = new ArrayList<>();
        }
        this.services.add(service);
    }

    private void validateDates(LocalDate checkInDate, LocalDate checkOutDate) throws InvalidDateRangeException {
        if(checkInDate == null || checkOutDate == null || checkInDate.isAfter(checkOutDate)){
            throw new InvalidDateRangeException("Check-Out date must be after the Check-In date!");
        }
    }

    public void confirmReservation(){
        if(this.status == ReservationStatus.PENDING){
            this.status = ReservationStatus.CONFIRMED;
        }
    }

    public void checkIn(){
        if(this.status == ReservationStatus.CONFIRMED){
            this.room.setStatus(RoomStatus.OCCUPIED);
            this.status = ReservationStatus.ACTIVE;
        }
    }

    public void checkOut(){
        if(this.status == ReservationStatus.ACTIVE){
            this.room.setStatus(RoomStatus.AVAILABLE);
            this.status = ReservationStatus.COMPLETED;
            this.guest.increaseTotalStays();
            this.guest.checkMembershipUpgrade();
        }
    }

    public void cancelReservation(){
        if(this.status == ReservationStatus.PENDING || this.status == ReservationStatus.CONFIRMED){
            this.status = ReservationStatus.CANCELLED;
            this.room.setStatus(RoomStatus.AVAILABLE);
        }
    }

    public double calculatePenalty(){
        long daysTillCheckIn = checkInDate.toEpochDay() - LocalDate.now().toEpochDay();
        double pricePerNight = room.calculatePrice(checkInDate, numberOfGuests);

        if(daysTillCheckIn < 1){
            return 1.0 * pricePerNight;
        }
        else if(daysTillCheckIn <= 3){
            return 0.5 * pricePerNight;
        }
        else{
            return 0.0;
        }
    }

    private void calculateTotalPrice(){
        long nights = checkOutDate.toEpochDay() - checkInDate.toEpochDay();
        double price = 0;

        for(int i = 0; i < nights; i++){
            LocalDate currentDate = checkInDate.plusDays(i);
            price += room.calculatePrice(currentDate, numberOfGuests);
        }

        this.totalPrice = price;
    }
}
