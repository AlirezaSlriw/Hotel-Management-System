import java.security.PublicKey;
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

    public Reservation(String reservationId, Guest guest, Room room, LocalDate checkInDate, LocalDate checkOutDate) throws InvalidDateRangeException {
        this.reservationId = reservationId;
        this.guest = guest;
        this.room = room;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.status = ReservationStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.services = new ArrayList<>();
        if (numberOfGuests <= 0 || numberOfGuests > room.getCapacity()) {
            throw new IllegalArgumentException("number of guests doesn't fit in this room!");
        }
        this.numberOfGuests = numberOfGuests;
        validateDates(checkInDate, checkOutDate);
        calculateTotalPrice();
    }

    public String getReservationId(){
        return this.reservationId;
    }

    public Guest getGuest(){
        return this.guest;
    }

    public Room getRoom(){
        return this.room;
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

    private boolean validateDates(LocalDate checkInDate, LocalDate checkOutDate) throws InvalidDateRangeException {
        if(checkInDate == null || checkOutDate == null || checkInDate.isAfter(checkOutDate)){
            throw new InvalidDateRangeException("Check-Out date must be after the Check-In date!");
        }
        return true;
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
            this.guest.checkMembershipUpgrade;
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
            return pricePerNight;
        }
        else if(daysTillCheckIn <= 3){
            return 0.5 * pricePerNight;
        }
        else{
            return 0.0;
        }
    }

    private void calculateTotalPrice(){
        long nights = checkInDate.toEpochDay() - LocalDate.now().toEpochDay();
        double price = 0;

        for(int i = 0; i < nights; i++){
            LocalDate currentDate = checkInDate.plusDays(i);
            price += room.calculatePrice(currentDate, numberOfGuests);
        }

        this.totalPrice = price;
    }

    private void calculateCancellationPenalty(){

    }
}
