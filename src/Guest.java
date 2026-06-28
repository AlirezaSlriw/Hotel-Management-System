import java.util.ArrayList;
import java.util.List;

public class Guest extends User{
    private MembershipLevel membershipLevel;
    private int totalStays;
    private List<Object> reservationHistory;

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

    public List<Object> getReservationHistory(){
        return this.reservationHistory;
    }
}
