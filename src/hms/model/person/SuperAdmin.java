package hms.model.person;
import hms.enums.UserRole;
import java.io.Serializable;
import java.time.LocalDate;

public class SuperAdmin extends Staff implements Serializable {
    private final boolean systemConfigAccess = true;

    public SuperAdmin(String fullName, String nationalId, String phone, String username,
                      String password, String employeeId, LocalDate hireDate){
        super(fullName, nationalId, phone, username, password, UserRole.SUPER_ADMIN, employeeId, hireDate);
    }

    public boolean hasSystemConfigAccess(){
        return systemConfigAccess;
    }
}
