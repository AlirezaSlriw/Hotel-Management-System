import java.time.LocalDate;

public abstract class Room {

    private String roomNumber;
    private RoomType type;
    private RoomStatus status;
    private double basePrice;
    private int floorNumber;
    private int capacity;

    public Room(String roomNumber, RoomType type, RoomStatus status, double basePrice, int floorNumber, int capacity){
        this.roomNumber = roomNumber;
        this.type = type;
        setStatus(status);
        setBasePrice(basePrice);
        setFloorNumber(floorNumber);
        setCapacity(capacity);
    }

    public String getRoomNumber(){
        return this.roomNumber;
    }

    private void setRoomNumber(String roomNumber){
        if(roomNumber == null || roomNumber.trim().isEmpty()){
            throw new IllegalArgumentException("Room number cannot be empty!");
        }
        this.roomNumber = roomNumber;
    }

    public RoomType getType(){
        return this.type;
    }

    public RoomStatus getStatus(){
        return this.status;
    }

    public void setStatus(RoomStatus status){
        this.status = status;
    }

    public double getBasePrice(){
        return this.basePrice;
    }

    public void setBasePrice(double basePrice){
        if(basePrice <= 0){
            throw new IllegalArgumentException("Base Price must be greater than zero!");
        }
        this.basePrice = basePrice;
    }

    public int getFloorNumber(){
        return this.floorNumber;
    }

    public void setFloorNumber(int floorNumber){
        if(floorNumber < 0){
            throw new IllegalArgumentException("Floor number cannot be negative!");
        }
        this.floorNumber = floorNumber;
    }

    public int getCapacity(){
        return this.capacity;
    }

    public void setCapacity(int capacity){
        if(capacity <= 0){
            throw new IllegalArgumentException("Room capacity must be at least one");
        }
        this.capacity = capacity;
    }

    protected abstract double getTypeMultiplier();

    protected double getSeasonMultiplier(LocalDate date){
        switch (date.getMonthValue()){
            case 1, 2, 12 ->{
                return Season.OFF.getMultiplier();
            }

            case 3, 4, 5, 9, 10, 11 ->{
                return Season.NORMAL.getMultiplier();
            }

            case 6, 7, 8 ->{
                return Season.PEAK.getMultiplier();
            }
        }
        return 1.0;
    }

    protected double getGuestMultiplier(int guestNumbers){
        double percentageIncrease = ((guestNumbers - 1) * 2)/10.0;
        return (1.0 + percentageIncrease);
    }



    public double calculatePrice(LocalDate date, int guestNumbers){
        return getBasePrice() * getTypeMultiplier() * getSeasonMultiplier(date) *
                getGuestMultiplier(guestNumbers);
    }

    public boolean isAvailable(){
        return this.status == RoomStatus.AVAILABLE;
    }
}
