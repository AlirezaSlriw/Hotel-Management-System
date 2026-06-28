import java.time.LocalDate;

public class Receptionist extends Staff{
    private String shiftType;

    public Receptionist(String fullName, String nationalId, String phone, String username, String password, String employeeId, LocalDate hireDate, String shiftType){
        super(fullName, nationalId, phone, username, password, UserRole.RECEPTIONIST, employeeId, hireDate);
        setShiftType(shiftType);
    }

    public String getShiftType(){
        return this.shiftType;
    }

    public void setShiftType(String shiftType){
        this.shiftType = shiftType;
    }
}
