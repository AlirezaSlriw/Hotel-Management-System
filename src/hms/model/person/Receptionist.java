package hms.model.person;
import hms.enums.UserRole;
import java.io.Serializable;
import java.time.LocalDate;

public class Receptionist extends Staff implements Serializable {
    private String shiftType;

    public Receptionist(String fullName, String nationalId, String phone, String username, String password, String employeeId, LocalDate hireDate, String shiftType){
        super(fullName, nationalId, phone, username, password, UserRole.RECEPTIONIST, employeeId, hireDate);
        setShiftType(shiftType);
    }

    public String getShiftType(){
        return this.shiftType;
    }

    public void setShiftType(String shiftType){
        if(shiftType == null || shiftType.trim().isEmpty()){
            throw new IllegalArgumentException("Shift type cannot be empty!");
        }
        this.shiftType = shiftType;
    }
}
