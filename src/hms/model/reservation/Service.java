import java.io.Serializable;

public class Service implements Serializable {
    private double price;
    private ServiceType type;
    private String serviceId;

    public Service(String serviceId, ServiceType type, double price){
        setServiceId(serviceId);
        this.type = type;
        setPrice(price);
    }

    public double getPrice(){
        return this.price;
    }

    private void setPrice(double price){
        if(price < 0){
            throw new IllegalArgumentException("Price cannot be negative!");
        }
        this.price = price;
    }

    public ServiceType getType(){
        return this.type;
    }

    public String getServiceId(){
        return this.serviceId;
    }

    private void setServiceId(String serviceId){
        if(serviceId == null || serviceId.trim().isEmpty()){
            throw new IllegalArgumentException("Service ID cannot be empty!");
        }
        this.serviceId = serviceId;
    }
}
