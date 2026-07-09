import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.time.LocalDate;

public class Main {
    private static final String LINE      = "----------------------------------------";
    private static final String LINE_WIDE = "----------------------------------------------------------------";
    private static final Scanner sc = new Scanner(System.in);
    private static User currentUser = null;

    private static void printHeader(String title){
        int width = LINE.length();
        String paddedTitle = "  " + title + "  ";

        if (paddedTitle.length() >= width){
            System.out.println("==== " + title + " ====");
        }
        else{
            int barLen = (width - paddedTitle.length()) / 2;
            String bar = "=".repeat(barLen);
            String header = bar + paddedTitle + bar;
            while (header.length() < width) header += "=";
            System.out.println(header);
        }
        System.out.println(LINE);
    }

    private static void printHeaderWide(String title){
        int width = LINE_WIDE.length();
        String paddedTitle = "  " + title + "  ";
        int barLen = (width - paddedTitle.length()) / 2;
        barLen = Math.max(barLen, 3);
        String bar = "=".repeat(barLen);
        String header = bar + paddedTitle + bar;
        while (header.length() < width) header += "=";
        System.out.println(header);
        System.out.println(LINE_WIDE);
    }


    public static void main(String[] args) {
        showLoadingBar();
        while (true){
            if (currentUser == null){
                showWelcomeMenu();
            }
            else{
                switch (currentUser.getRole()){
                    case SUPER_ADMIN -> showSuperAdminMenu();
                    case HOTEL_MANAGER -> showHotelManagerMenu();
                    case RECEPTIONIST -> showReceptionistMenu();
                    case GUEST -> showGuestMenu();
                }
            }
        }
    }

    private static void clearScreen(){
        System.out.print("\033[H\033[2J");
        System.out.flush();
        for (int i = 0; i < 25; i++){
            System.out.println();
        }
    }

    private static void showLoadingBar(){
        clearScreen();
        System.out.print("  Loading: [");
        for (int i = 0; i < 30; i++){
            try { Thread.sleep(60); } catch (InterruptedException ignored) {}
            System.out.print("=");
            System.out.flush();
        }
        System.out.print("]");
        try { Thread.sleep(400); } catch (InterruptedException ignored) {}
        clearScreen();
    }

    private static void waitForEnter(){
        System.out.println(LINE);
        System.out.print("Press Enter to return... ");
        sc.nextLine();
        clearScreen();
    }

    private static void waitForEnterWide(){
        System.out.println(LINE_WIDE);
        System.out.print("Press Enter to return... ");
        sc.nextLine();
        clearScreen();
    }

    private static void showWelcomeMenu(){
        printHeader("GRAND PERSIA HOTEL");
        System.out.println("1. Login to Account");
        System.out.println("2. Guest Sign-Up (Create New Account)");
        System.out.println("3. Exit System");
        System.out.println(LINE);
        System.out.print("Choice > ");
        String choice = sc.nextLine().trim();
        clearScreen();

        switch (choice){
            case "1" -> showLoginMenu();
            case "2" -> registerGuestDirectly();
            case "3" -> {
                System.out.println("Thank you for using Grand Persia Hotel System. Goodbye!");
                System.exit(0);
            }
            default -> System.out.println("[ERROR] Invalid choice. Please try again.");
        }
    }

    private static void showLoginMenu(){
       printHeader("LOGIN");
        System.out.printf("%-12s > ", "Username");
        String username = sc.nextLine().trim();
        System.out.printf("%-12s > ", "Password");
        String password = sc.nextLine().trim();
        clearScreen();

        User user = HotelService.login(username, password);
        if (user != null){
            currentUser = user;
            LoggerSystem.log("LOGIN", currentUser.getUsername(), "Logged in successfully.");
            System.out.println("[SUCCESS] Welcome back, " + user.getFullName() + " (" + user.getRole() + ")");
        }
        else{
            System.out.println("[ERROR] Invalid username or password.");
        }
    }

    private static void registerGuestDirectly(){
        printHeader("GUEST SIGN-UP");
        try {
            System.out.print("Your Full Name > ");
            String name = sc.nextLine();
            System.out.print("National ID (10 digits) > ");
            String nationalId = sc.nextLine();
            System.out.print("Phone Number > ");
            String phone = sc.nextLine();
            System.out.print("Choose Username > ");
            String username = sc.nextLine();
            System.out.print("Choose Password (min 8 chars) > ");
            String password = sc.nextLine();

            if (isUsernameTaken(username)){
                System.out.println("[ERROR] Username already exists. Please choose another.");
                return;
            }
            if (isNationalIdTaken(nationalId)){
                System.out.println("[ERROR] An account with this National ID already exists.");
                return;
            }

            Guest newGuest = new Guest(name, nationalId, phone, username, password);
            HotelService.Users.add(newGuest);
            LoggerSystem.log("GUEST_SIGNUP", username, "Registered a new guest account via public form.");
            System.out.println("[SUCCESS] Account created successfully! You can now login.");
            try { Thread.sleep(1000); clearScreen(); } catch (InterruptedException ignored) {}
            return;
        }
        catch (IllegalArgumentException e){
            System.out.println("[ERROR] Sign-up failed: " + e.getMessage());
        }
        waitForEnter();
    }

    private static void showSuperAdminMenu(){
        boolean inMenu = true;
        while (inMenu) {
            printHeader("SUPER-ADMIN DASHBOARD");
            System.out.println("1. Create New User Account (Manager/Receptionist/Guest)");
            System.out.println("2. System Settings (Base Prices & Multipliers)");
            System.out.println("3. View Reports");
            System.out.println("4. Delete / Disable User Accounts");
            System.out.println("5. View System Audit Security Logs");
            System.out.println("6. Logout");
            System.out.println(LINE);
            System.out.print("Choice > ");
            String choice = sc.nextLine().trim();
            clearScreen();

            switch (choice) {
                case "1" -> {
                    createAnyUserMenu();                    
                }
                case "2" -> {
                    systemSettingsMenu();
                }
                case "3" -> {
                    showReportsMenu();
                }
                case "4" -> {
                    deleteUserAccount();
                    waitForEnter();
                }
                case "5" -> {
                    showSecurityLogs();
                    waitForEnterWide();
                }
                case "6" -> {
                    LoggerSystem.log("LOGOUT", currentUser.getUsername(), "Logged out.");
                    currentUser = null;
                    inMenu = false;
                }
                default -> {
                    System.out.println("[ERROR] Invalid choice. Please try again.");
                    try {
                        Thread.sleep(1000);
                    }
                    catch(Exception ignored){

                    }
                    clearScreen();
                }
            }
        }
    }

    private static void systemSettingsMenu(){
        boolean inSettings = true;
        while (inSettings){
            printHeader("SYSTEM SETTINGS");
            System.out.println("1. View / Change Room Base Prices");
            System.out.println("2. View / Change Room Type Multipliers");
            System.out.println("3. View / Change Seasonal Multipliers");
            System.out.println("4. View / Change Guest Count Multipliers");
            System.out.println("5. View / Change Membership Discounts");
            System.out.println("6. View / Change Tax Rates (Municipal & VAT)");
            System.out.println("0. Back to Main Menu");
            System.out.println(LINE);
            System.out.print("Choice > ");
            String choice = sc.nextLine().trim();
            clearScreen();

            switch (choice){
                case "1" -> changeRoomBasePrice();
                case "2" -> changeRoomTypeMultipliers();
                case "3" -> changeSeasonalMultipliers();
                case "4" -> changeGuestMultipliers();
                case "5" -> changeMembershipDiscounts();
                case "6" -> changeTaxRates();
                case "0" -> inSettings = false;
                default -> System.out.println("[ERROR] Invalid choice. Please try again.");
            }
        }
    }

    private static void changeRoomBasePrice(){
        System.out.println("--- ROOM BASE PRICES ---");
        for (Room r : HotelService.Rooms){
            System.out.printf("Room %-4s | Type: %-10s | Base Price: $%.2f\n", r.getRoomNumber(), r.getType(), r.getBasePrice());
        }
        System.out.print("\nRoom Number to change (Enter to skip) > ");
        String roomNum = sc.nextLine().trim();
        if (roomNum.isEmpty()) return;

        for (Room r : HotelService.Rooms){
            if (r.getRoomNumber().equals(roomNum)){
                System.out.print("New Base Price > ");
                try {
                    double newPrice = Double.parseDouble(sc.nextLine().trim());
                    r.setBasePrice(newPrice);
                    LoggerSystem.log("SETTINGS_CHANGE", currentUser.getUsername(), "Base price of room " + roomNum + " -> " + newPrice);
                    System.out.println("[SUCCESS] Base price updated.");
                }
                catch (NumberFormatException e){
                    System.out.println("[ERROR] Invalid number.");
                }
                catch (IllegalArgumentException e){
                    System.out.println("[ERROR] " + e.getMessage());
                }
                return;
            }
        }
        System.out.println("[ERROR] Room not found.");
    }

    private static void changeRoomTypeMultipliers(){
        System.out.println("--- ROOM TYPE MULTIPLIERS ---");
        System.out.printf("1. STANDARD  (current: %.2f)\n", StandardRoom.getStaticTypeMultiplier());
        System.out.printf("2. DELUXE    (current: %.2f)\n", DeluxeRoom.getStaticTypeMultiplier());
        System.out.printf("3. SUITE     (current: %.2f)\n", Suite.getStaticTypeMultiplier());
        System.out.printf("4. PENTHOUSE (current: %.2f)\n", PentHouse.getStaticTypeMultiplier());
        System.out.print("Select type to change (Enter to skip) > ");
        String choice = sc.nextLine().trim();
        if (choice.isEmpty()) return;

        try {
            System.out.print("New Multiplier > ");
            double newValue = Double.parseDouble(sc.nextLine().trim());
            switch (choice){
                case "1" -> StandardRoom.setTypeMultiplier(newValue);
                case "2" -> DeluxeRoom.setTypeMultiplier(newValue);
                case "3" -> Suite.setTypeMultiplier(newValue);
                case "4" -> PentHouse.setTypeMultiplier(newValue);
                default -> { System.out.println("[ERROR] Invalid selection."); return; }
            }
            LoggerSystem.log("SETTINGS_CHANGE", currentUser.getUsername(), "Room type multiplier changed to " + newValue);
            System.out.println("[SUCCESS] Multiplier updated.");
        }
        catch (NumberFormatException e){
            System.out.println("[ERROR] Invalid number.");
        }
        catch (IllegalArgumentException e){
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private static void changeSeasonalMultipliers(){
        System.out.println("--- SEASONAL MULTIPLIERS ---");
        for (Season s : Season.values()){
            System.out.printf("%-8s (current: %.2f)\n", s.name(), s.getMultiplier());
        }
        System.out.print("Select season to change (OFF/NORMAL/PEAK, Enter to skip) > ");
        String name = sc.nextLine().trim().toUpperCase();
        if (name.isEmpty()) return;

        try {
            Season season = Season.valueOf(name);
            System.out.print("New Multiplier > ");
            double newValue = Double.parseDouble(sc.nextLine().trim());
            season.setMultiplier(newValue);
            LoggerSystem.log("SETTINGS_CHANGE", currentUser.getUsername(), "Season " + name + " multiplier -> " + newValue);
            System.out.println("[SUCCESS] Multiplier updated.");
        }
        catch (IllegalArgumentException e){
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private static void changeGuestMultipliers(){
        System.out.println("--- GUEST COUNT MULTIPLIERS ---");
        System.out.print("New multiplier for 1 guest > ");
        try {
            double single = Double.parseDouble(sc.nextLine().trim());
            System.out.print("New multiplier for 2 guests > ");
            double doubleG = Double.parseDouble(sc.nextLine().trim());
            System.out.print("New multiplier for 3+ guests > ");
            double extra = Double.parseDouble(sc.nextLine().trim());

            Room.setGuestMultipliers(single, doubleG, extra);
            LoggerSystem.log("SETTINGS_CHANGE", currentUser.getUsername(), "Guest multipliers updated.");
            System.out.println("[SUCCESS] Guest multipliers updated.");
        }
        catch (NumberFormatException e){
            System.out.println("[ERROR] Invalid number.");
        }
        catch (IllegalArgumentException e){
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private static void changeMembershipDiscounts(){
        System.out.println("--- MEMBERSHIP DISCOUNTS ---");
        for (MembershipLevel level : MembershipLevel.values()){
            System.out.printf("%-10s (current: %.0f%%)\n", level.name(), level.getDiscountRate() * 100);
        }
        System.out.print("Select level to change (BRONZE/SILVER/GOLD/PLATINUM, Enter to skip) > ");
        String name = sc.nextLine().trim().toUpperCase();
        if (name.isEmpty()) return;

        try {
            MembershipLevel level = MembershipLevel.valueOf(name);
            System.out.print("New Discount Percentage (e.g., 10 for 10%) > ");
            double percent = Double.parseDouble(sc.nextLine().trim());
            level.setDiscountRate(percent / 100.0);
            LoggerSystem.log("SETTINGS_CHANGE", currentUser.getUsername(), "Discount for " + name + " -> " + percent + "%");
            System.out.println("[SUCCESS] Discount updated.");
        }
        catch (IllegalArgumentException e){
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private static void changeTaxRates(){
        System.out.println("--- TAX RATES ---");
        System.out.printf("Municipal Tax (current: %.0f%%)\n", Invoice.getMunicipalTaxRate() * 100);
        System.out.printf("VAT           (current: %.0f%%)\n", Invoice.getVatRate() * 100);
        try {
            System.out.print("New Municipal Tax % > ");
            double municipal = Double.parseDouble(sc.nextLine().trim());
            System.out.print("New VAT % > ");
            double vat = Double.parseDouble(sc.nextLine().trim());

            Invoice.setTaxRates(municipal / 100.0, vat / 100.0);
            LoggerSystem.log("SETTINGS_CHANGE", currentUser.getUsername(), "Tax rates updated.");
            System.out.println("[SUCCESS] Tax rates updated.");
        }
        catch (NumberFormatException e){
            System.out.println("[ERROR] Invalid number.");
        }
        catch (IllegalArgumentException e){
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private static void createAnyUserMenu(){
        boolean inMenu = true;
        while (inMenu){
            printHeader("CREATE NEW USER ACCOUNT");
            System.out.println("1. Create Hotel Manager");
            System.out.println("2. Create Receptionist");
            System.out.println("3. Create Guest");
            System.out.println("0. Back to Main Menu");
            System.out.println(LINE);
            System.out.print("Choice > ");
            String type = sc.nextLine().trim();
            clearScreen();

            switch (type){
                case "1" -> { createManagerAccount(); inMenu = false; }
                case "2" -> { createReceptionistAccount(); inMenu = false; }
                case "3" -> { createGuestAccountBySuperAdmin(); inMenu = false; }
                case "0" -> inMenu = false;
                default -> System.out.println("[ERROR] Invalid selection. Please try again.");
            }
        }
    }

    private static void createGuestAccountBySuperAdmin(){
        System.out.println("--- REGISTER NEW GUEST (by Admin) ---");
        try {
            System.out.print("Full Name > "); String name = sc.nextLine();
            System.out.print("National ID (10 digits) > "); String nationalId = sc.nextLine();
            System.out.print("Phone Number > "); String phone = sc.nextLine();
            System.out.print("Username > "); String username = sc.nextLine();
            System.out.print("Password (min 8 chars) > "); String password = sc.nextLine();

            if (isUsernameTaken(username)){
                System.out.println("[ERROR] Username already exists.");
                return;
            }
            if (isNationalIdTaken(nationalId)){
                System.out.println("[ERROR] An account with this National ID already exists.");
                return;
            }

            Guest guest = new Guest(name, nationalId, phone, username, password);
            HotelService.Users.add(guest);
            LoggerSystem.log("GUEST_REG_BY_ADMIN", currentUser.getUsername(), "Registered Guest: " + username);
            System.out.println("[SUCCESS] Guest account created successfully.");
            try { Thread.sleep(1000); clearScreen(); } catch (InterruptedException ignored) {}
        }
        catch (IllegalArgumentException e){
            System.out.println("[ERROR] Creation failed: " + e.getMessage());
        }
    }

    private static void showHotelManagerMenu(){
        boolean inMenu = true;
        while (inMenu) {
            printHeader("HOTEL MANAGER DASHBOARD");
            System.out.println("1. Add / Remove Rooms");
            System.out.println("2. Create Receptionist Account");
            System.out.println("3. View Reports");
            System.out.println("4. Change Room Status (e.g., MAINTENANCE)");
            System.out.println("5. Maintenance Request Approvals");
            System.out.println("6. Logout");
            System.out.println(LINE);
            System.out.print("Choice > ");
            String choice = sc.nextLine().trim();
            clearScreen();

            switch (choice){
                case "1" -> {
                    printHeader("ADD NEW ROOM");
                    try {
                        System.out.print("Room Number (e.g., 601) > "); String rNum = sc.nextLine();
                        System.out.print("Floor Number > "); int floor = Integer.parseInt(sc.nextLine());
                        System.out.print("Base Price > "); double price = Double.parseDouble(sc.nextLine());
                        System.out.print("Capacity > "); int cap = Integer.parseInt(sc.nextLine());
                        System.out.print("Room Type (1: Standard / 2: Deluxe / 3: Suite / 4: Penthouse) > ");
                        String typeChoice = sc.nextLine().trim();

                        Room newRoom = switch(typeChoice){
                            case "1" -> new StandardRoom(rNum, RoomStatus.AVAILABLE, price, floor, cap);
                            case "2" -> new DeluxeRoom(rNum, RoomStatus.AVAILABLE, price, floor, cap);
                            case "3" -> new Suite(rNum, RoomStatus.AVAILABLE, price, floor, cap);
                            case "4" -> new PentHouse(rNum, RoomStatus.AVAILABLE, price, floor, cap);
                            default -> throw new IllegalArgumentException("Invalid type");
                        };
                        HotelService.Rooms.add(newRoom);
                        LoggerSystem.log("ROOM_ADD", currentUser.getUsername(), "Added room " + rNum);
                        System.out.println("[SUCCESS] Room added successfully.");
                        try { Thread.sleep(1000); clearScreen(); } catch (InterruptedException ignored) {}
                    }
                    catch(Exception e){
                        System.out.println("[ERROR] " + e.getMessage());
                    }
                }
                case "2" -> {
                    createReceptionistAccount();
                }
                case "3" -> {
                    showReportsMenu();
                }
                case "4" -> {
                    changeRoomStatus();
                }
                case "5" -> {
                    manageMaintenanceRequests();
                }
                case "6" -> {
                    LoggerSystem.log("LOGOUT", currentUser.getUsername(), "Logged out.");
                    currentUser = null;
                    inMenu = false;
                }
                default -> {
                    System.out.println("[ERROR] Invalid choice. Please try again.");
                    clearScreen();
                }
            }
        }
    }

    private static void showReportsMenu(){
        boolean isGuest = (currentUser.getRole() == UserRole.GUEST);
        boolean isReceptionist = (currentUser.getRole() == UserRole.RECEPTIONIST);
        boolean isManager = (currentUser.getRole() == UserRole.HOTEL_MANAGER);
        boolean isSuperAdmin = (currentUser.getRole() == UserRole.SUPER_ADMIN);

        boolean inMenu = true;
        while (inMenu){
            clearScreen();
            printHeader("REPORTS CENTER");

            List<String> labels  = new ArrayList<>();
            List<String> actions = new ArrayList<>();

            labels.add("Room Status Overview");
            actions.add("R01");

            if (isManager || isSuperAdmin){
                labels.add("Daily Occupancy Report");
                actions.add("R02");
            }

            if (isManager || isSuperAdmin){
                labels.add("Monthly Revenue by Room Type");
                actions.add("R03");
            }

            if (isManager){
                labels.add("Most Popular Rooms");
                actions.add("R04");
            }

            if (isManager || isSuperAdmin){
                labels.add("Guests with Outstanding Balance");
                actions.add("R05");
            }

            if (isManager || isReceptionist){
                labels.add("Guest Stay History (Search by ID)");
                actions.add("R06");
            }

            for (int i = 0; i < labels.size(); i++){
                System.out.println((i + 1) + ". " + labels.get(i));
            }
            System.out.println("0. Back to Main Menu");
            System.out.println(LINE);
            System.out.print("Choice > ");
            String choice = sc.nextLine().trim();
            clearScreen();

            if (choice.equals("0")){
                inMenu = false;
                continue;
            }

            int idx;
            try {
                idx = Integer.parseInt(choice) - 1;
            }
            catch (NumberFormatException e){
                System.out.println("[ERROR] Invalid choice. Please try again.");
                continue;
            }

            if (idx < 0 || idx >= actions.size()){
                System.out.println("[ERROR] Invalid choice. Please try again.");
                continue;
            }

            switch (actions.get(idx)){
                case "R01" -> { reportRoomStatusOverview();  waitForEnterWide(); }
                case "R02" -> { reportDailyOccupancy();      waitForEnterWide(); }
                case "R03" -> { reportMonthlyRevenue();      waitForEnterWide(); }
                case "R04" -> { reportMostPopularRooms();    waitForEnterWide(); }
                case "R05" -> { reportOutstandingBalances(); waitForEnterWide(); }
                case "R06" -> { reportGuestHistory();        waitForEnterWide(); }
            }
        }
    }

    private static void reportRoomStatusOverview(){
        printHeaderWide("R-01: ROOM STATUS OVERVIEW");
        int available = 0, reserved = 0, occupied = 0, maintenance = 0, outOfService = 0;

        System.out.printf("%-6s | %-10s | %-15s | %s\n", "Room", "Type", "Status", "Base Price");
        System.out.println(LINE_WIDE);

        for (Room r : HotelService.Rooms){
            System.out.printf("%-6s | %-10s | %-15s | $%.2f\n",
                    r.getRoomNumber(), r.getType(), r.getStatus(), r.getBasePrice());
            switch (r.getStatus()){
                case AVAILABLE    -> available++;
                case RESERVED     -> reserved++;
                case OCCUPIED     -> occupied++;
                case MAINTENANCE  -> maintenance++;
                case OUT_OF_SERVICE -> outOfService++;
            }
        }

        System.out.println(LINE_WIDE);
        System.out.println("SUMMARY");
        System.out.println(LINE_WIDE);
        System.out.printf("Available     : %d\n", available);
        System.out.printf("Reserved      : %d\n", reserved);
        System.out.printf("Occupied      : %d\n", occupied);
        System.out.printf("Maintenance   : %d\n", maintenance);
        System.out.printf("Out of Service: %d\n", outOfService);
        System.out.printf("Total Rooms   : %d\n", HotelService.Rooms.size());
    }

    private static void reportDailyOccupancy(){
        printHeaderWide("R-02: DAILY OCCUPANCY REPORT");
        int total = HotelService.Rooms.size();
        int occupied = 0, reserved = 0, maintenance = 0;

        for (Room r : HotelService.Rooms){
            if (r.getStatus() == RoomStatus.OCCUPIED)    occupied++;
            else if (r.getStatus() == RoomStatus.RESERVED)    reserved++;
            else if (r.getStatus() == RoomStatus.MAINTENANCE) maintenance++;
        }

        int available = total - occupied - reserved - maintenance;
        double occupancyRate = total > 0 ? (occupied * 100.0 / total) : 0;
        double utilizationRate = total > 0 ? ((occupied + reserved) * 100.0 / total) : 0;

        System.out.printf("Report Date         : %s\n", LocalDate.now());
        System.out.println(LINE_WIDE);
        System.out.printf("Total Rooms         : %d\n", total);
        System.out.printf("Occupied            : %d  (%.1f%%)\n", occupied, occupancyRate);
        System.out.printf("Reserved            : %d  (%.1f%%)\n", reserved, reserved * 100.0 / total);
        System.out.printf("Under Maintenance   : %d  (%.1f%%)\n", maintenance, maintenance * 100.0 / total);
        System.out.printf("Available           : %d  (%.1f%%)\n", available, available * 100.0 / total);
        System.out.println(LINE_WIDE);
        System.out.printf("Occupancy Rate      : %.1f%%\n", occupancyRate);
        System.out.printf("Utilization Rate    : %.1f%%\n", utilizationRate);
    }

    private static void reportMonthlyRevenue(){
        try {
            checkAccess(UserRole.HOTEL_MANAGER, UserRole.SUPER_ADMIN);
        }
        catch (AccessDeniedException e){
            System.out.println("[ERROR] " + e.getMessage());
            return;
        }
        printHeaderWide("R-03: MONTHLY REVENUE BY ROOM TYPE");
        System.out.print("Enter Period (YYYY-MM) > ");
        int month;
        int year;
        try {
            String input = sc.nextLine().trim();
            if (!input.matches("\\d{4}-\\d{2}")){
                System.out.println("[ERROR] Invalid format. Please use YYYY-MM (e.g., 2026-07).");
                return;
            }
            year = Integer.parseInt(input.substring(0, 4));
            month = Integer.parseInt(input.substring(5, 7));
            if (month < 1 || month > 12){
                System.out.println("[ERROR] Month must be between 01 and 12.");
                return;
            }
        }
        catch (NumberFormatException e){
            System.out.println("[ERROR] Invalid input.");
            return;
        }

        double totalStandard = 0, totalDeluxe = 0, totalSuite = 0, totalPenthouse = 0;
        int countStandard = 0, countDeluxe = 0, countSuite = 0, countPenthouse = 0;

        for (Reservation res : HotelService.Reservations){
            if (res.getStatus() == ReservationStatus.COMPLETED &&
                    res.getCheckOutDate().getMonthValue() == month &&
                    res.getCheckOutDate().getYear() == year){

                Invoice inv = res.getInvoice() != null ? res.getInvoice() : new Invoice(res);
                double amount = inv.getTotalAmount();

                switch (res.getRoom().getType()){
                    case STANDARD  -> { totalStandard   += amount; countStandard++;   }
                    case DELUXE    -> { totalDeluxe      += amount; countDeluxe++;     }
                    case SUITE     -> { totalSuite        += amount; countSuite++;       }
                    case PENTHOUSE -> { totalPenthouse   += amount; countPenthouse++;  }
                }
            }
        }

        double grandTotal = totalStandard + totalDeluxe + totalSuite + totalPenthouse;

        System.out.printf("Period: %02d/%d\n", month, year);
        System.out.println(LINE_WIDE);
        System.out.printf("%-12s | %5s reservations | Revenue\n", "Room Type", "Count");
        System.out.println(LINE_WIDE);
        System.out.printf("%-12s | %5d             | $%.2f\n", "STANDARD",   countStandard,   totalStandard);
        System.out.printf("%-12s | %5d             | $%.2f\n", "DELUXE",     countDeluxe,     totalDeluxe);
        System.out.printf("%-12s | %5d             | $%.2f\n", "SUITE",      countSuite,      totalSuite);
        System.out.printf("%-12s | %5d             | $%.2f\n", "PENTHOUSE",  countPenthouse,  totalPenthouse);
        System.out.println(LINE_WIDE);
        System.out.printf("%-12s | %5d             | $%.2f\n", "TOTAL",
                countStandard + countDeluxe + countSuite + countPenthouse, grandTotal);
    }

    private static void reportMostPopularRooms(){
        printHeaderWide("R-04: MOST POPULAR ROOMS");

        java.util.Map<String, Integer> roomCount = new java.util.HashMap<>();
        for (Reservation res : HotelService.Reservations){
            String roomNum = res.getRoom().getRoomNumber();
            roomCount.put(roomNum, roomCount.getOrDefault(roomNum, 0) + 1);
        }

        if (roomCount.isEmpty()){
            System.out.println("[INFO] No reservation data available yet.");
            return;
        }

        List<java.util.Map.Entry<String, Integer>> sorted = new ArrayList<>(roomCount.entrySet());
        sorted.sort((a, b) -> b.getValue() - a.getValue());

        System.out.printf("%-6s | %-10s | %-15s | %s\n", "Room", "Type", "Status", "Total Reservations");
        System.out.println(LINE_WIDE);

        int rank = 1;
        for (java.util.Map.Entry<String, Integer> entry : sorted){
            String roomNum = entry.getKey();
            Room room = null;
            for (Room r : HotelService.Rooms){
                if (r.getRoomNumber().equals(roomNum)){ room = r; break; }
            }
            if (room != null){
                System.out.printf("#%-5d | %-10s | %-15s | %d\n",
                        rank++, room.getType(), room.getStatus(), entry.getValue());
            }
        }
    }

    private static void reportOutstandingBalances(){
        try {
            checkAccess(UserRole.HOTEL_MANAGER, UserRole.SUPER_ADMIN);
        }
        catch (AccessDeniedException e){
            System.out.println("[ERROR] " + e.getMessage());
            return;
        }
        printHeaderWide("R-05: GUESTS WITH OUTSTANDING BALANCE");

        boolean found = false;
        double totalOutstanding = 0;

        System.out.printf("%-15s | %-20s | %-10s | %-10s | %s\n",
                "Reservation", "Guest", "Total", "Paid", "Balance");
        System.out.println(LINE_WIDE);

        for (Reservation res : HotelService.Reservations){
            Invoice inv = res.getInvoice();
            if (inv != null && !inv.isPaymentCompleted()){
                double balance = inv.getTotalAmount() - inv.getPaidAmount();
                System.out.printf("%-15s | %-20s | %-10.2f | %-10.2f | $%.2f\n",
                        res.getReservationId(),
                        res.getGuest().getFullName(),
                        inv.getTotalAmount(),
                        inv.getPaidAmount(),
                        balance);
                totalOutstanding += balance;
                found = true;
            }
        }

        if (!found){
            System.out.println("[INFO] No outstanding balances found.");
            return;
        }
        System.out.println(LINE_WIDE);
        System.out.printf("Total Outstanding Balance: $%.2f\n", totalOutstanding);
    }

    private static void reportGuestHistory(){
        printHeaderWide("R-06: GUEST STAY HISTORY");
        System.out.print("Guest National ID > ");
        String nationalId = sc.nextLine().trim();

        try {
            Guest guest = findGuestByNationalId(nationalId);

            System.out.printf("Guest Name    : %s\n", guest.getFullName());
            System.out.printf("Membership    : %s\n", guest.getMembershipLevel());
            System.out.printf("Total Stays   : %d\n", guest.getTotalStays());
            System.out.println(LINE_WIDE);

            List<Reservation> history = guest.getReservationHistory();
            if (history.isEmpty()){
                System.out.println("[INFO] No reservation history found for this guest.");
                return;
            }

            System.out.printf("%-12s | %-6s | %-10s | %-12s | %-12s | %s\n",
                    "Res ID", "Room", "Type", "Check-In", "Check-Out", "Status");
            System.out.println(LINE_WIDE);

            for (Reservation res : history){
                System.out.printf("%-12s | %-6s | %-10s | %-12s | %-12s | %s\n",
                        res.getReservationId(),
                        res.getRoom().getRoomNumber(),
                        res.getRoom().getType(),
                        res.getCheckInDate(),
                        res.getCheckOutDate(),
                        res.getStatus());
            }
        }
        catch (GuestNotFoundException e){
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private static void manageMaintenanceRequests(){
        boolean inMenu = true;
        while (inMenu){
            clearScreen();
            printHeader("MAINTENANCE REQUEST APPROVALS");

            List<MaintenanceRequest> pending = new ArrayList<>();
            for (MaintenanceRequest req : HotelService.MaintenanceRequests){
                if (req.getStatus() == MaintenanceStatus.PENDING){
                    pending.add(req);
                }
            }

            if (pending.isEmpty()){
                System.out.println("[INFO] No pending maintenance requests.");
                System.out.println(LINE);
                System.out.println("0. Back to Main Menu");
                System.out.print("Choice > ");
                sc.nextLine();
                clearScreen();
                break;
            }

            System.out.println("--- PENDING REQUESTS ---");
            for (MaintenanceRequest req : pending) {
                System.out.println(req.getSummary());
                System.out.println(LINE);
            }

            System.out.println("1. Review a Request");
            System.out.println("0. Back to Main Menu");
            System.out.print("Choice > ");
            String choice = sc.nextLine().trim();

            if (choice.equals("0")){
                clearScreen();
                break;
            }

            if (!choice.equals("1")){
                System.out.println("[ERROR] Invalid choice.");
                continue;
            }

            System.out.print("Enter Request ID to review > ");
            String reqId = sc.nextLine().trim();
            if (reqId.isEmpty()) continue;

            MaintenanceRequest selected = null;
            for (MaintenanceRequest req : HotelService.MaintenanceRequests){
                if (req.getRequestId().equalsIgnoreCase(reqId)){
                    selected = req;
                    break;
                }
            }

            if (selected == null){
                System.out.println("[ERROR] Request ID not found.");
                waitForEnter();
                continue;
            }

            if (selected.getStatus() != MaintenanceStatus.PENDING){
                System.out.println("[ERROR] This request has already been resolved.");
                waitForEnter();
                continue;
            }

            clearScreen();
            printHeader("REQUEST DETAILS");
            System.out.println(selected.getSummary());
            System.out.println(LINE);
            System.out.print("Decision (1: Approve / 2: Reject) > ");
            String decision = sc.nextLine().trim();

            if (decision.equals("1")) {
                selected.approve();

                for (Room r : HotelService.Rooms){
                    if (r.getRoomNumber().equals(selected.getRoomNumber())) {
                        r.setStatus(RoomStatus.MAINTENANCE);
                        break;
                    }
                }

                LoggerSystem.log("MAINTENANCE_APPROVED", currentUser.getUsername(),
                        "Approved request " + reqId + " for room " + selected.getRoomNumber());
                System.out.println("[SUCCESS] Request approved. Room is now in MAINTENANCE.");
            }
            else if (decision.equals("2")){
                selected.reject();
                LoggerSystem.log("MAINTENANCE_REJECTED", currentUser.getUsername(),
                        "Rejected request " + reqId + " for room " + selected.getRoomNumber());
                System.out.println("[SUCCESS] Request rejected.");
            }
            else{
                System.out.println("[ERROR] Invalid decision. No changes made.");
            }
            waitForEnter();
        }
    }

    private static void showReceptionistMenu() {
        boolean inMenu = true;
        while (inMenu) {
            printHeader("RECEPTIONIST DASHBOARD");
            System.out.println("1. Register New Reservation (Walk-in)");
            System.out.println("2. Process Guest Check-In");
            System.out.println("3. Process Guest Check-Out & Invoice");
            System.out.println("4. Search Rooms (by Number or Type)");
            System.out.println("5. Add Extra Service / Maintenance Request");
            System.out.println("6. Process Payment");
            System.out.println("7. Apply Manual Discount to Invoice");
            System.out.println("8. View Reports");
            System.out.println("9. Logout");
            System.out.println(LINE);
            System.out.print("Choice > ");
            String choice = sc.nextLine().trim();
            clearScreen();

            switch (choice){
                case "1" -> {
                    createNewReservation();
                    waitForEnter();
                }
                case "2" -> {
                    processCheckIn();
                    waitForEnter();
                }
                case "3" -> {
                    processCheckOut();
                    waitForEnter();
                }
                case "4" -> {
                    searchRooms();
                    waitForEnter();
                }
                case "5" -> {
                    addService();
                    waitForEnter();
                }
                case "6" -> {
                    processPaymentMenu();
                    waitForEnter();
                }
                case "7" -> {
                    applyManualDiscount();
                    waitForEnter();
                }
                case "8" -> {
                    showReportsMenu();
                }
                case "9" -> {
                    LoggerSystem.log("LOGOUT", currentUser.getUsername(), "Logged out.");
                    currentUser = null;
                    inMenu = false;
                }
                default -> {
                    System.out.println("[ERROR] Invalid choice. Please try again.");
                    clearScreen();
                }
            }
        }
    }

    private static void applyManualDiscount(){
        printHeader("APPLY MANUAL DISCOUNT");
        System.out.print("Reservation ID > ");
        String resId = sc.nextLine().trim();

        for (Reservation res : HotelService.Reservations){
            if (res.getReservationId().equalsIgnoreCase(resId)){
                Invoice invoice = res.getInvoice();
                if (invoice == null){
                    System.out.println("[ERROR] Guest must be checked-in first.");
                    return;
                }
                System.out.printf("Current Total: $%.2f\n", invoice.getTotalAmount());
                System.out.print("Discount Percentage (e.g., 10 for 10%) > ");
                try {
                    double percent = Double.parseDouble(sc.nextLine().trim());
                    if (percent <= 0 || percent > 100){
                        System.out.println("[ERROR] Discount must be between 1 and 100.");
                        return;
                    }
                    invoice.applyDiscount(percent);
                    LoggerSystem.log("MANUAL_DISCOUNT", currentUser.getUsername(),
                            "Applied " + percent + "% discount to " + resId);
                    System.out.printf("[SUCCESS] %.0f%% discount applied. New Total: $%.2f\n", percent, invoice.getTotalAmount());
                    try { Thread.sleep(1000); clearScreen(); } catch (InterruptedException ignored) {}
                }
                catch (NumberFormatException e){
                    System.out.println("[ERROR] Invalid percentage.");
                }
                return;
            }
        }
        System.out.println("[ERROR] Reservation ID not found.");
    }

    private static void showGuestMenu(){
        boolean inMenu = true;
        while (inMenu) {
            Guest guest = (Guest) currentUser;
            printHeader("GUEST DASHBOARD");
            System.out.println("Welcome: " + guest.getFullName());
            System.out.println("Current Membership Level: " + guest.getMembershipLevel());
            System.out.println("Total Stays Calculated  : " + guest.getTotalStays());
            System.out.println(LINE);
            System.out.println("1. Search & Browse Available Rooms");
            System.out.println("2. View My Current Active Reservations");
            System.out.println("3. Request Reservation Cancellation (With Penalty)");
            System.out.println("4. View My Complete Stay History");
            System.out.println("5. Request a Service / Maintenance");
            System.out.println("6. View Room Status Overview");
            System.out.println("7. Logout");
            System.out.println(LINE);
            System.out.print("Choice > ");
            String choice = sc.nextLine().trim();
            clearScreen();

            switch (choice){
                case "1" -> {
                    showAvailableRoomsOnly();
                    waitForEnter();
                }
                case "2" -> {
                    viewGuestReservations(guest);
                    waitForEnter();
                }
                case "3" -> {
                    printHeader("CANCEL RESERVATION");
                    System.out.print("Reservation ID to Cancel > ");
                    String cancelId = sc.nextLine().trim();
                    for (Reservation r : HotelService.Reservations){
                        if (r.getReservationId().equalsIgnoreCase(cancelId) && r.getGuest().getUsername().equals(currentUser.getUsername())){
                            double penalty = r.calculatePenalty();
                            if (penalty > 0){
                                long days = r.getCheckInDate().toEpochDay() - LocalDate.now().toEpochDay();
                                String reason = days < 1 ? "less than 24 hours to check-in (100% penalty)" :
                                        "1-3 days to check-in (50% penalty)";
                                System.out.printf("[WARNING] Cancellation penalty: $%.2f (%s)\n", penalty, reason);
                                System.out.print("Are you sure? (yes/no) > ");
                                if (!sc.nextLine().trim().equalsIgnoreCase("yes")){
                                    System.out.println("[INFO] Cancellation aborted.");
                                    break;
                                }
                            }
                            r.cancelReservation();
                            System.out.printf("[SUCCESS] Reservation cancelled. Penalty applied: $%.2f\n", penalty);
                            HotelService.waitlistManager.notifyObservers(r.getRoom());
                            LoggerSystem.log("CANCEL_RES", currentUser.getUsername(), "Cancelled " + cancelId + " with penalty " + penalty);
                            break;
                        }
                    }
                    waitForEnter();
                }
                case "4" -> {
                    printHeader("STAY HISTORY");
                    if (guest.getReservationHistory().isEmpty()) {
                        System.out.println("[INFO] No previous stays found under your account.");
                    }
                    else{
                        for (Reservation r : guest.getReservationHistory()){
                            System.out.printf("Res ID: %s | Room: %s | Dates: %s to %s\n", r.getReservationId(), r.getRoom().getRoomNumber(), r.getCheckInDate(), r.getCheckOutDate());
                        }
                    }
                    waitForEnter();
                }
                case "5" -> {
                    addService();
                    waitForEnter();
                }
                case "6" -> {
                    reportRoomStatusOverview();
                    waitForEnterWide();
                }
                case "7" -> {
                    LoggerSystem.log("LOGOUT", currentUser.getUsername(), "Logged out.");
                    currentUser = null;
                    inMenu = false;
                }
                default -> {
                    System.out.println("[ERROR] Invalid choice. Please try again.");
                    clearScreen();
                }
            }
        }
    }

    private static void createManagerAccount(){
        System.out.println("--- REGISTER NEW HOTEL MANAGER ---");
        try {
            System.out.print("Manager Full Name > "); String name = sc.nextLine();
            System.out.print("National ID (10 digits) > "); String nationalId = sc.nextLine();
            System.out.print("Phone Number > "); String phone = sc.nextLine();
            System.out.print("Username > "); String user = sc.nextLine();
            System.out.print("Password > "); String pass = sc.nextLine();
            System.out.print("Employee ID > "); String empId = sc.nextLine();
            System.out.print("Department Name > "); String dept = sc.nextLine();

            if (isUsernameTaken(user)){
                System.out.println("[ERROR] Username already exists.");
                return;
            }
            if (isNationalIdTaken(nationalId)){
                System.out.println("[ERROR] An account with this National ID already exists.");
                return;
            }

            HotelManager hm = new HotelManager(name, nationalId, phone, user, pass, empId, LocalDate.now(), dept);
            HotelService.Users.add(hm);
            LoggerSystem.log("MANAGER_REG", currentUser.getUsername(), "Registered Manager: " + user);
            System.out.println("[SUCCESS] Hotel Manager account created successfully.");
            try { Thread.sleep(1000); clearScreen(); } catch (InterruptedException ignored) {}
        }
        catch(Exception e) {
            System.out.println("[ERROR] Creation failed: " + e.getMessage());
        }
    }

    private static void createReceptionistAccount(){
        System.out.println("--- REGISTER NEW RECEPTIONIST ---");
        try {
            System.out.print("Full Name > "); String name = sc.nextLine();
            System.out.print("National ID > "); String nationalId = sc.nextLine();
            System.out.print("Phone Number > "); String phone = sc.nextLine();
            System.out.print("Username > "); String user = sc.nextLine();
            System.out.print("Password > "); String pass = sc.nextLine();
            System.out.print("Employee ID > "); String empId = sc.nextLine();
            System.out.print("Shift Type (Morning/Night) > "); String shift = sc.nextLine();

            if (isUsernameTaken(user)){
                System.out.println("[ERROR] Username already exists.");
                return;
            }
            if (isNationalIdTaken(nationalId)){
                System.out.println("[ERROR] An account with this National ID already exists.");
                return;
            }

            Receptionist rec = new Receptionist(name, nationalId, phone, user, pass, empId, LocalDate.now(), shift);
            HotelService.Users.add(rec);
            LoggerSystem.log("RECEPTIONIST_REG", currentUser.getUsername(), "Registered Receptionist: " + user);
            System.out.println("[SUCCESS] Receptionist account created successfully.");
            try { Thread.sleep(1000); clearScreen(); } catch (InterruptedException ignored) {}
        }
        catch(Exception e) {
            System.out.println("[ERROR] Creation failed: " + e.getMessage());
        }
    }

    private static void changeRoomStatus(){
        System.out.print("Enter Room Number to change > ");
        String roomNum = sc.nextLine().trim();
        try {
            Room r = findRoomByNumber(roomNum);
            System.out.println("Current Status: " + r.getStatus());
            System.out.print("New Status (1: Available / 2: Maintenance / 3: Out of Service) > ");
            String choice = sc.nextLine().trim();
            if (choice.equals("1")) r.setStatus(RoomStatus.AVAILABLE);
            else if (choice.equals("2")) r.setStatus(RoomStatus.MAINTENANCE);
            else if (choice.equals("3")) r.setStatus(RoomStatus.OUT_OF_SERVICE);
            LoggerSystem.log("ROOM_STATUS", currentUser.getUsername(), "Changed Room " + roomNum + " status to " + r.getStatus());
            System.out.println("[SUCCESS] Status changed.");
            try { Thread.sleep(1000); clearScreen(); } catch (InterruptedException ignored) {}
        }
        catch (RoomNotFoundException e){
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private static void deleteUserAccount(){
        try {
            checkAccess(UserRole.SUPER_ADMIN);
        }
        catch (AccessDeniedException e){
            System.out.println("[ERROR] " + e.getMessage());
            return;
        }
        System.out.print("Enter username to remove > ");
        String uname = sc.nextLine().trim();
        if(uname.equals("admin")){
            System.out.println("[ERROR] Cannot delete the SuperAdmin account.");
            return;
        }
        boolean removed = HotelService.Users.removeIf(u -> u.getUsername().equalsIgnoreCase(uname));
        if(removed){
            LoggerSystem.log("USER_DELETE", currentUser.getUsername(), "Deleted user account: " + uname);
            System.out.println("[SUCCESS] User account deleted.");
        }
        else{
            System.out.println("[ERROR] Username not found.");
        }
    }

    private static void createNewReservation(){
        printHeader("CREATE RESERVATION");
        try {
            System.out.print("Guest National ID (10 digits) > ");
            String nationalId = sc.nextLine().trim();

            Guest guest;
            try {
                guest = findGuestByNationalId(nationalId);
                System.out.println("[INFO] Existing guest found: " + guest.getFullName() +
                        " (Membership: " + guest.getMembershipLevel() + ")");
            }
            catch (GuestNotFoundException e){
                System.out.println("[INFO] No existing account found. Registering as new guest...");
                System.out.print("Guest Full Name > "); String name = sc.nextLine();
                System.out.print("Guest Phone > "); String phone = sc.nextLine();
                System.out.print("Guest Username > "); String gUser = sc.nextLine();
                System.out.print("Guest Password > "); String gPass = sc.nextLine();

                guest = new Guest(name, nationalId, phone, gUser, gPass);
                HotelService.Users.add(guest);
                LoggerSystem.log("GUEST_REG", currentUser.getUsername(), "Registered new guest via reservation: " + gUser);
            }

            System.out.print("Enter Room Number > ");
            String roomNum = sc.nextLine().trim();

            Room targetRoom;
            try {
                targetRoom = findRoomByNumber(roomNum);
            }
            catch (RoomNotFoundException e){
                System.out.println("[ERROR] " + e.getMessage());
                return;
            }

            if (targetRoom.getStatus() != RoomStatus.AVAILABLE){
                try {
                    throw new RoomNotAvailableException("Room " + roomNum + " is not available for reservation (Status: " + targetRoom.getStatus() + ")");
                }
                catch (RoomNotAvailableException e){
                    System.out.println("[ERROR] " + e.getMessage());
                    HotelService.waitlistManager.addToList(roomNum, guest);
                    LoggerSystem.log("WAITLIST_ADD", currentUser.getUsername(), "Added " + guest.getFullName() + " to waitlist.");
                    return;
                }
            }

            System.out.print("Check-in Date (YYYY-MM-DD) > "); LocalDate checkIn = LocalDate.parse(sc.nextLine());
            System.out.print("Check-out Date (YYYY-MM-DD) > "); LocalDate checkOut = LocalDate.parse(sc.nextLine());
            System.out.print("Number of Guests > "); int guestsCount = Integer.parseInt(sc.nextLine());

            String resId = "R-" + (HotelService.Reservations.size() + 1001);
            Reservation reservation = new Reservation(resId, guest, targetRoom, checkIn, checkOut, guestsCount);

            HotelService.Reservations.add(reservation);
            guest.addReservation(reservation);

            System.out.printf("\n[INFO] Estimated total cost: $%.2f\n", reservation.getTotalPrice());
            System.out.print("Register deposit now? (yes/no) > ");

            if (sc.nextLine().trim().equalsIgnoreCase("yes")){
                System.out.print("Deposit Amount > ");
                try {
                    double deposit = Double.parseDouble(sc.nextLine().trim());
                    if (deposit <= 0){
                        System.out.println("[WARNING] Invalid deposit. Reservation saved as PENDING.");
                    }
                    else{
                        reservation.confirmReservation(deposit);
                        System.out.printf("[SUCCESS] Deposit of $%.2f registered. Status: CONFIRMED\n", deposit);
                    }
                }
                catch (NumberFormatException e){
                    System.out.println("[WARNING] Invalid amount. Reservation saved as PENDING.");
                }
            }
            else {
                System.out.println("[INFO] No deposit registered. Reservation saved as PENDING.");
            }

            System.out.println("[SUCCESS] Reservation ID: " + resId);
            LoggerSystem.log("RESERVATION_NEW", currentUser.getUsername(), "Created reservation " + resId + " | Status: " + reservation.getStatus());

        }
        catch (ReservationConflictException e){
            System.out.println("[ERROR] Date conflict: " + e.getMessage());
            LoggerSystem.log("CONFLICT_ERROR", currentUser.getUsername(), e.getMessage());
        }
        catch (DateTimeParseException e){
            System.out.println("[ERROR] Invalid date format. Use YYYY-MM-DD.");
        }
        catch (Exception e){
            System.out.println("[ERROR] Failed: " + e.getMessage());
        }
    }

    private static Guest findGuestByNationalId(String nationalId) throws GuestNotFoundException{
        for (User u : HotelService.Users){
            if (u instanceof Guest g && g.getNationalId().equals(nationalId)){
                return g;
            }
        }
        throw new GuestNotFoundException("No guest found with National ID: " + nationalId);
    }

    private static boolean isUsernameTaken(String username){
        for (User u : HotelService.Users){
            if (u.getUsername().equalsIgnoreCase(username)) return true;
        }
        return false;
    }

    private static boolean isNationalIdTaken(String nationalId){
        for (User u : HotelService.Users){
            if (u.getNationalId().equals(nationalId)) return true;
        }
        return false;
    }

    private static Room findRoomByNumber(String roomNum) throws RoomNotFoundException {
        for (Room r : HotelService.Rooms){
            if (r.getRoomNumber().equals(roomNum)){
                return r;
            }
        }
        throw new RoomNotFoundException("Room with number '" + roomNum + "' does not exist in the system.");
    }

    private static void processCheckIn(){
        System.out.print("Enter Reservation ID for Check-In > ");
        String resId = sc.nextLine().trim();
        for (Reservation res : HotelService.Reservations){
            if (res.getReservationId().equalsIgnoreCase(resId)){

                if (res.getStatus() == ReservationStatus.CANCELLED ||
                        res.getStatus() == ReservationStatus.COMPLETED){
                    System.out.println("[ERROR] This reservation is " + res.getStatus() + " and cannot be checked in.");
                    return;
                }
                if (res.getStatus() == ReservationStatus.ACTIVE){
                    System.out.println("[ERROR] Guest is already checked in.");
                    return;
                }
                if (res.getStatus() == ReservationStatus.PENDING){
                    System.out.println("[WARNING] Reservation is PENDING — deposit not registered yet.");
                    System.out.print("Register deposit now to confirm? (yes/no) > ");
                    if (sc.nextLine().trim().equalsIgnoreCase("yes")){
                        System.out.print("Deposit Amount > ");
                        try {
                            double deposit = Double.parseDouble(sc.nextLine().trim());
                            if (deposit <= 0){
                                System.out.println("[ERROR] Invalid deposit. Check-In cancelled.");
                                return;
                            }
                            res.confirmReservation(deposit);
                            System.out.printf("[SUCCESS] Deposit of $%.2f registered. Status: CONFIRMED\n", deposit);
                        }
                        catch (NumberFormatException e){
                            System.out.println("[ERROR] Invalid amount. Check-In cancelled.");
                            return;
                        }
                    }
                    else {
                        System.out.println("[INFO] Check-In cancelled. Please register deposit first.");
                        return;
                    }
                }

                res.checkIn();
                System.out.println("[SUCCESS] Check-In complete. Room is now OCCUPIED.");
                LoggerSystem.log("CHECK_IN", currentUser.getUsername(), "Checked-in reservation " + resId);
                return;
            }
        }
        System.out.println("[ERROR] Reservation ID not found.");
    }

    private static void processCheckOut(){
        System.out.print("Enter Reservation ID for Check-Out > ");
        String resId = sc.nextLine().trim();
        for (Reservation res : HotelService.Reservations){
            if (res.getReservationId().equalsIgnoreCase(resId)){

                Invoice invoice = res.getInvoice();
                if (invoice == null){
                    System.out.println("[ERROR] This reservation has not been checked in yet.");
                    return;
                }

                if (!invoice.isPaymentCompleted()){
                    System.out.printf("[ERROR] Outstanding balance: %.2f\n", invoice.getTotalAmount() - invoice.getPaidAmount());
                    System.out.print("Do you want to pay the remaining balance now? (yes/no) > ");

                    if (!sc.nextLine().trim().equalsIgnoreCase("yes")){
                        return;
                    }

                    System.out.print("Enter payment amount > ");
                    try {
                        double amount = Double.parseDouble(sc.nextLine().trim());
                        invoice.processPayment(amount);
                    }
                    catch (NumberFormatException e){
                        System.out.println("[ERROR] Invalid amount entered. Check-out cancelled.");
                        return;
                    }
                    catch (InsufficientPaymentException e){
                        System.out.println("[ERROR] " + e.getMessage());
                        return;
                    }

                    if (!invoice.isPaymentCompleted()){
                        System.out.println("[ERROR] Payment still incomplete. Cannot check-out yet.");
                        return;
                    }
                }

                completeCheckOut(res, invoice, resId);
                return;
            }
        }
        System.out.println("[ERROR] Reservation ID not found.");
    }

    private static void completeCheckOut(Reservation res, Invoice invoice, String resId){
        res.checkOut();
        System.out.println("[SUCCESS] Check-Out completed. Room freed.");
        System.out.println(invoice.exportToText());

        HotelService.waitlistManager.notifyObservers(res.getRoom());
        LoggerSystem.log("CHECK_OUT", currentUser.getUsername(), "Checked-out reservation " + resId);
    }

    private static void addService() {
        printHeader("ADD SERVICE TO RESERVATION");
        System.out.print("Reservation ID > ");
        String resId = sc.nextLine().trim();

        Reservation targetRes = null;
        for (Reservation res : HotelService.Reservations){
            if (res.getReservationId().equalsIgnoreCase(resId)){
                targetRes = res;
                break;
            }
        }

        if (targetRes == null){
            System.out.println("[ERROR] Reservation not found.");
            return;
        }

        if (targetRes.getStatus() != ReservationStatus.ACTIVE){
            System.out.println("[ERROR] Services can only be added to active reservations.");
            return;
        }

        System.out.println("--- SELECT SERVICE ---");
        System.out.println("1. Minibar         ($50)");
        System.out.println("2. Extra Cleaning  ($30)");
        System.out.println("3. Parking         ($20)");
        System.out.println("4. Room Service    ($45)");
        System.out.println("5. Maintenance Request (Free)");
        System.out.print("Choice > ");
        String ch = sc.nextLine().trim();

        if (ch.equals("5")){
            System.out.print("Describe the Issue > ");
            String description = sc.nextLine().trim();
            if (description.isEmpty()){
                System.out.println("[ERROR] Description cannot be empty.");
                return;
            }

            if (currentUser.getRole() == UserRole.GUEST){
                boolean isOwnRoom = targetRes.getGuest().getUsername()
                        .equals(currentUser.getUsername());
                if (!isOwnRoom){
                    System.out.println("[ERROR] You can only request maintenance for your own reservation.");
                    return;
                }
            }

            MaintenanceRequest request = new MaintenanceRequest(
                    targetRes.getRoom().getRoomNumber(),
                    currentUser.getUsername(),
                    description
            );
            HotelService.MaintenanceRequests.add(request);
            targetRes.addService(new Service(
                    request.getRequestId(),
                    ServiceType.MAINTENANCE,
                    0.0
            ));
            LoggerSystem.log("MAINTENANCE_REQ", currentUser.getUsername(),
                    "Submitted " + request.getRequestId() + " for room " + targetRes.getRoom().getRoomNumber());
            System.out.println("[SUCCESS] Maintenance request submitted. ID: " + request.getRequestId());
            System.out.println("[INFO] Hotel management will review your request shortly.");
            try { Thread.sleep(1200); clearScreen(); } catch (InterruptedException ignored) {}
            return;
        }

        ServiceType st = switch(ch) {
            case "1" -> ServiceType.MINIBAR;
            case "2" -> ServiceType.EXTRA_CLEANING;
            case "3" -> ServiceType.PARKING;
            case "4" -> ServiceType.ROOM_SERVICE;
            default -> null;
        };

        if (st == null){
            System.out.println("[ERROR] Invalid selection.");
            return;
        }

        double price = switch(ch) {
            case "1" -> 50.0;
            case "2" -> 30.0;
            case "3" -> 20.0;
            default  -> 45.0;
        };

        targetRes.addService(new Service("SRV-" + System.currentTimeMillis() % 1000, st, price));
        System.out.println("[SUCCESS] Service added to reservation. Invoice updated.");
    }

    private static void viewGuestReservations(Guest g){
        printHeader("ACTIVE & PENDING RESERVATIONS");
        int count = 0;
        for (Reservation r : HotelService.Reservations){
            if (r.getGuest().getUsername().equals(g.getUsername())){
                System.out.printf("Res ID: %s | Room: %s | Type: %s | Dates: %s to %s\n", r.getReservationId(), r.getRoom().getRoomNumber(), r.getRoom().getType(), r.getCheckInDate(), r.getCheckOutDate());
                count++;
            }
        }
        if(count == 0){
            System.out.println("[INFO] You don't have any active reservations.");
        }
    }

    private static void showAllRooms(){
        printHeaderWide("ALL HOTEL ROOMS");
        for (Room room : HotelService.Rooms){
            System.out.printf("Room %-4s | %-10s | %-15s | $%.2f\n", room.getRoomNumber(), room.getType(), room.getStatus(), room.getBasePrice());
        }
    }

    private static void showAvailableRoomsOnly(){
        printHeaderWide("AVAILABLE ROOMS");
        for (Room room : HotelService.Rooms){
            if (room.getStatus() == RoomStatus.AVAILABLE){
                System.out.printf("Room %-4s | Type: %-10s | Price: $%.2f\n", room.getRoomNumber(), room.getType(), room.getBasePrice());
            }
        }
    }

    private static void showSecurityLogs() {
        printHeaderWide("SECURITY AUDIT LOGS");
        List<String> logs = LoggerSystem.getLogs();
        if(logs.isEmpty()) System.out.println("[INFO] No system log records yet.");
        else logs.forEach(System.out::println);
    }

    private static void checkAccess(UserRole... allowedRoles) throws AccessDeniedException {
        for (UserRole role : allowedRoles){
            if (currentUser.getRole() == role) return;
        }
        String attempted = currentUser.getRole() + " attempted restricted action";
        LoggerSystem.log("ACCESS_DENIED", currentUser.getUsername(), attempted);
        throw new AccessDeniedException(
                "Access denied for role: " + currentUser.getRole() +
                        ". Required: " + java.util.Arrays.toString(allowedRoles)
        );
    }

    private static void searchRooms(){
        printHeader("SEARCH ROOM");
        System.out.print("Enter room number or type > ");
        String query = sc.nextLine();

        HotelService service = new HotelService();
        List<Room> results = service.search(query);

        System.out.println("--- RESULTS ---");
        if (results.isEmpty()) {
            System.out.println("[INFO] No rooms found matching your search query.");
        }
        else{
            for (Room room : results){
                System.out.printf("Room %-4s | %-10s | %-15s%n", room.getRoomNumber(), room.getType(), room.getStatus());
            }
        }
    }

    private static void processPaymentMenu(){
        System.out.print("Enter Reservation ID for Payment > ");
        String resId = sc.nextLine().trim();
        for (Reservation res : HotelService.Reservations){
            if (res.getReservationId().equalsIgnoreCase(resId)){
                Invoice invoice = res.getInvoice();
                if (invoice == null){
                    System.out.println("[ERROR] Guest must be checked-in before payment.");
                    return;
                }
                System.out.printf("Total: %.2f | Paid: %.2f | Remaining: %.2f\n",
                        invoice.getTotalAmount(), invoice.getPaidAmount(),
                        invoice.getTotalAmount() - invoice.getPaidAmount());
                System.out.print("Payment amount > ");
                try {
                    double amount = Double.parseDouble(sc.nextLine().trim());
                    invoice.processPayment(amount);
                    LoggerSystem.log("PAYMENT", currentUser.getUsername(), "Payment of " + amount + " for " + resId);
                } catch (NumberFormatException e){
                    System.out.println("[ERROR] Invalid amount.");
                } catch (InsufficientPaymentException e){
                    System.out.println("[ERROR] " + e.getMessage());
                }
                return;
            }
        }
        System.out.println("[ERROR] Reservation ID not found.");
    }
}