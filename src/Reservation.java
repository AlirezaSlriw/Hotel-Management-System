import java.security.Provider;
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

    public Reservation(){

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

    public void addService(List<Service> services){
        this.services = services;
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
        }
    }

    private void calculateTotalPrice(){
        int nights = this.checkOutDate.getDayOfMonth() - this.checkInDate.getDayOfMonth();
        int guestCount = getGues;
        this.totalPrice = room.calculatePrice(checkInDate, guestCount) * nights;
    }
}
