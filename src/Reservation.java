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
    private double depositPaid = 0.0;
    private List<Service> services;
    private int numberOfGuests;
    private Invoice invoice;


    public Reservation(String reservationId, Guest guest, Room room, LocalDate checkInDate, LocalDate checkOutDate, int numberOfGuests) throws InvalidDateRangeException, ReservationConflictException {
        validateDates(checkInDate, checkOutDate);
        for (Reservation existing : HotelService.Reservations){
            if (existing.getRoom().getRoomNumber().equals(room.getRoomNumber()) &&
                    existing.getStatus() != ReservationStatus.CANCELLED &&
                    existing.getStatus() != ReservationStatus.COMPLETED){

                boolean overlaps = checkInDate.isBefore(existing.getCheckOutDate()) &&
                        checkOutDate.isAfter(existing.getCheckInDate());
                if (overlaps){
                    throw new ReservationConflictException(
                            "Room " + room.getRoomNumber() + " already has a reservation from " +
                                    existing.getCheckInDate() + " to " + existing.getCheckOutDate() +
                                    " (ID: " + existing.getReservationId() + ")"
                    );
                }
            }
        }

        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;

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

    public double getDepositPaid(){
        return depositPaid;
    }

    public LocalDateTime getCreatedAt(){
        return this.createdAt;
    }

    public List<Service> getServices(){
        return this.services;
    }

    public Invoice getInvoice(){
        return this.invoice;
    }

    public void addService(Service service){
        if(this.services == null){
            this.services = new ArrayList<>();
        }
        this.services.add(service);
        if(this.invoice != null){
            this.invoice.recalculate();
        }
    }

    private void validateDates(LocalDate checkInDate, LocalDate checkOutDate) throws InvalidDateRangeException {
        if(checkInDate == null || checkOutDate == null || !checkInDate.isBefore(checkOutDate)){
            throw new InvalidDateRangeException("Check-Out date must be after the Check-In date!");
        }
    }

    public void confirmReservation(double depositAmount){
        if(this.status == ReservationStatus.PENDING){
            this.status = ReservationStatus.CONFIRMED;
            this.depositPaid = depositAmount;
        }
    }

    public void checkIn(){
        if(this.status == ReservationStatus.CONFIRMED){
            this.room.setStatus(RoomStatus.OCCUPIED);
            this.status = ReservationStatus.ACTIVE;
            this.invoice = new Invoice(this);
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

        double taxRate = 1 + Invoice.getMunicipalTaxRate() + Invoice.getVatRate();
        double totalWithTax = this.totalPrice * taxRate;

        if (daysTillCheckIn < 1){
            return totalWithTax;
        }
        else if (daysTillCheckIn <= 3){
            return totalWithTax * 0.5;
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
