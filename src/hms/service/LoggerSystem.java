import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class LoggerSystem {
    private static final List<String> systemLogs = new ArrayList<>();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void log(String action, String user, String details){
        String timestamp = LocalDateTime.now().format(formatter);
        String logEntry = String.format("[%s] | Action: %-15s | By: %-12s | Details: %s", timestamp, action, user, details);
        systemLogs.add(logEntry);
    }

    public static List<String> getLogs(){
        return systemLogs;
    }

    public static void loadLogs(List<String> savedLogs){
        systemLogs.clear();
        systemLogs.addAll(savedLogs);
    }
}
