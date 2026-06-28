import java.time.LocalDate;

public class HotelManager extends Staff{
    private String departmentName;

    public HotelManager(String fullName, String nationalId, String phone, String username, String password, String employeeId, LocalDate hireDate, String departmentName){
        super(fullName, nationalId, phone, username, password, UserRole.HOTEL_MANAGER, employeeId, hireDate);
        setDepartmentName(departmentName);
    }

    public String getDepartmentName(){
        return this.departmentName;
    }

    public void setDepartmentName(String departmentName){
        this.departmentName = departmentName;
    }
}
