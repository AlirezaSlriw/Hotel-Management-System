package hms.data;
import hms.model.maintenance.MaintenanceRequest;
import hms.model.person.Guest;
import hms.model.person.User;
import hms.model.reservation.Reservation;
import hms.model.room.Room;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class SaveData implements Serializable {
    public List<User> users;
    public List<Room> rooms;
    public List<Reservation> reservations;
    public List<MaintenanceRequest> maintenanceRequests;

    public Map<String, Queue<Guest>> waitlists;

    public int invoiceIdCounter;
    public int maintenanceIdCounter;

    public double seasonOff;
    public double seasonNormal;
    public double seasonPeak;
    public double memberBronzeDiscount;
    public double memberSilverDiscount;
    public double memberGoldDiscount;
    public double memberPlatinumDiscount;
    public double standardMultiplier;
    public double deluxeMultiplier;
    public double suiteMultiplier;
    public double pentHouseMultiplier;
    public double singleGuestMultiplier;
    public double doubleGuestMultiplier;
    public double extraGuestMultiplier;
    public double municipalTaxRate;
    public double vatRate;

    public List<String> logs;
}