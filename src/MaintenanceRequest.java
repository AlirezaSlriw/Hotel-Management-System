import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MaintenanceRequest {
    private static int idCounter = 1;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private String requestId;
    private String roomNumber;
    private String requestedBy;
    private String description;
    private MaintenanceStatus status;
    private LocalDateTime requestedAt;
    private LocalDateTime resolvedAt;

    public MaintenanceRequest(String roomNumber, String requestedBy, String description){
        this.requestId = "MNT-" + (idCounter++);
        this.roomNumber = roomNumber;
        this.requestedBy = requestedBy;
        this.description = description;
        this.status = MaintenanceStatus.PENDING;
        this.requestedAt = LocalDateTime.now();
        this.resolvedAt = null;
    }

    public void approve(){
        this.status = MaintenanceStatus.APPROVED;
        this.resolvedAt = LocalDateTime.now();
    }

    public void reject(){
        this.status = MaintenanceStatus.REJECTED;
        this.resolvedAt = LocalDateTime.now();
    }

    public String getRequestId(){ return requestId; }
    public String getRoomNumber(){ return roomNumber; }
    public String getRequestedBy(){ return requestedBy; }
    public String getDescription(){ return description; }
    public MaintenanceStatus getStatus(){ return status; }

    public String getSummary(){
        String resolved = (resolvedAt != null) ? resolvedAt.format(formatter) : "Pending";
        return String.format(
                "ID: %-8s | Room: %-4s | By: %-12s | Status: %-8s | Requested: %s | Resolved: %s\nDesc: %s",
                requestId, roomNumber, requestedBy, status,
                requestedAt.format(formatter), resolved, description
        );
    }
}
