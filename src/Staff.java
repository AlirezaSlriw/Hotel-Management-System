import java.time.LocalDate;

public abstract class Staff extends User{
    private String employeeId;
    private LocalDate hireDate;

    public Staff(String fullName, String nationalId, String phone, String username, String password, UserRole role, String employeeId, LocalDate hireDate) {
        super(fullName, nationalId, phone, username, password, role);
        setEmployeeId(employeeId);
        setHireDate(hireDate);
    }

    public String getEmployeeId(){
        return this.employeeId;
    }

    private void setEmployeeId(String employeeId){
        if(employeeId == null || employeeId.trim().isEmpty()){
            throw new IllegalArgumentException("Employee ID cannot be empty!");
        }
        this.employeeId = employeeId;
    }

    public LocalDate getHireDate(){
        return this.hireDate;
    }

    private void setHireDate(LocalDate hireDate){
        if(hireDate == null || hireDate.isAfter(LocalDate.now())){
            throw new IllegalArgumentException("Hire date cannot be in the future!");
        }
        this.hireDate = hireDate;
    }

}
