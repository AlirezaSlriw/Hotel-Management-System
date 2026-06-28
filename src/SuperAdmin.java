import java.time.LocalDate;

public class SuperAdmin extends Staff{
    public SuperAdmin(String fullName, String nationalId, String phone, String username, String password, String employeeId, LocalDate hireDate){
        super(fullName, nationalId, phone, username, password, UserRole.SUPER_ADMIN, employeeId, hireDate);
    }
}
