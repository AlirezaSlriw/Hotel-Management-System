import java.util.ArrayList;
import java.util.List;

public class Guest extends User{
    private MembershipLevel membershipLevel;
    private int totalStays;
    private List<Reservation> reservationHistory;

    public Guest(String fullName, String nationalId, String phone, String username, String password){
        super(fullName, nationalId, phone, username, password, UserRole.GUEST);
        this.reservationHistory = new ArrayList<>();
        this.totalStays = 0;
        this.membershipLevel = MembershipLevel.BRONZE;
    }

    public MembershipLevel getMembershipLevel(){
        return this.membershipLevel;
    }

    public void setMembershipLevel(MembershipLevel membershipLevel){
        this.membershipLevel = membershipLevel;
    }

    public void increaseTotalStays(){
        this.totalStays++;
    }

    public int getTotalStays(){
        return this.totalStays;
    }

    public List<Reservation> getReservationHistory(){
        return this.reservationHistory;
    }

    public void addReservation(Reservation reservation){
        this.reservationHistory.add(reservation);
    }

    public void checkMembershipUpgrade(){
        if(totalStays >= 30){
            this.membershipLevel = MembershipLevel.PLATINUM;
        }
        else if(totalStays >= 15){
            this.membershipLevel = MembershipLevel.GOLD;
        }
        else if(totalStays >= 5){
            this.membershipLevel = MembershipLevel.SILVER;
        }
        else{
            this.membershipLevel = MembershipLevel.BRONZE;
        }
    }
}
