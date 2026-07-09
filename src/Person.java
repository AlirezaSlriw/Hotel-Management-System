public abstract class Person {
    private String fullName;
    private String nationalId;
    private String phone;

    public Person(String name, String nationalId, String phone){
        setFullName(name);
        setNationalId(nationalId);
        setPhone(phone);
    }

    public String getFullName(){
        return this.fullName;
    }

    public void setFullName(String fullName){
        if(fullName == null || fullName.trim().isEmpty()){
            throw new IllegalArgumentException("Name must not be empty!");
        }
        this.fullName = fullName;
    }

    public String getNationalId(){
        return this.nationalId;
    }

    private void setNationalId(String nationalId){
        if(nationalId == null || !nationalId.matches("\\d{10}")){
            throw new IllegalArgumentException("National ID must be exactly 10 numeric digits!");
        }
        this.nationalId = nationalId;
    }

    public String getPhone(){
        return this.phone;
    }

    public void setPhone(String phone){
        if(phone == null || phone.trim().isEmpty()){
            throw new IllegalArgumentException("Phone number must not be empty!");
        }
        this.phone = phone;
    }
}
