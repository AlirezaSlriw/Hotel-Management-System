public abstract class User extends Person{
    private String username;
    private String password;
    private UserRole role;

    public User(String fullName, String nationalId, String phone, String username, String password, UserRole role){
        super(fullName, nationalId, phone);
        setUsername(username);
        setPassword(password);
        this.role = role;
    }

    public String getUsername(){
        return this.username;
    }

    public void setUsername(String username){
        if(username == null || username.trim().isEmpty()){
            throw new IllegalArgumentException("Username cannot be empty!");
        }
        this.username = username;
    }

    public String getPassword(){
        return this.password;
    }

    public void setPassword(String password){
        if(password == null || password.length() < 8){
            throw new IllegalArgumentException("Password must be at least 8 characters long!");
        }
        this.password = password;
    }

    public UserRole getRole(){
        return this.role;
    }
}
