import java.util.*;
import java.io.*;

public class TicketMachine {

    private ArrayList<String> stationList;
    private Map<Integer, Integer> fareMap; // แผนที่ระยะ -> ค่าโดยสาร
    private TransactionData transactionData;

    private int flatRateFare = 67; // ค่าโดยสารเหมาจ่ายสำหรับระยะทาง > 8 สถานี

    private final String BACKUP_FILE = "backup_data.txt";
    private final String TRANSACTION_FILE = "transaction_data.txt";
    private final String STATIONS_FILE = "stations_data.txt"; // เพิ่มไฟล์สำหรับสถานี

    public TicketMachine() {
        stationList = new ArrayList<>();

        // โหลดสถานีจากไฟล์ ถ้าไม่มีไฟล์จะใช้สถานีเริ่มต้นและบันทึกลงไฟล์
        if (new File(STATIONS_FILE).exists()) {
            loadStations();
        } else {
            initializeDefaultStations();
            saveStations();
        }

        // ค่าโดยสารระยะทางเริ่มต้น
        fareMap = new HashMap<>();
        fareMap.put(1, 15);
        fareMap.put(2, 32);
        fareMap.put(3, 40);
        fareMap.put(4, 43);
        fareMap.put(5, 50);
        fareMap.put(6, 55);
        fareMap.put(7, 58);
        fareMap.put(8, 62);

        transactionData = new TransactionData();
        initializeTransactionFile();

        // โหลดข้อมูลธุรกรรมปัจจุบันจาก transaction_data.txt
        loadTransactionData();

        // โหลดข้อมูลสำรอง (Backup) เมื่อเริ่มต้นโปรแกรม (ถ้าต้องการ)
        // loadBackupData(); // ถ้าไม่ต้องการโหลดข้อมูลสำรองเข้ามาใน transaction_data
    }

    // ฟังก์ชันสำหรับกำหนดสถานีเริ่มต้น
    private void initializeDefaultStations() {
        stationList.addAll(Arrays.asList(
            "N24 คูคต", "N23 แยก คปอ.", "N22 พิพิธภัณฑ์กองทัพอากาศ", "N21 โรงพยาบาลภูมิพลฯ",
            "N20 สะพานใหม่", "N19 สายหยุด", "N18 พหลโยธิน 59", "N17 วัดพระศรีมหาธาตุ",
            "N16 กรมทหารราบที่11", "N15 บางบัว", "N14 กรมป่าไม้", "N13 ม.เกษตร",
            "N12 เสนานิคม", "N11 รัชโยธิน", "N10 พหลโยธิน24", "N9 ห้าแยกลาดพร้าว",
            "N8 หมอชิต", "N7 สะพานควาย", "N6 เสนาร่วม", "N5 อารีย์", "N4 สนามเป้า",
            "N3 อนุสาวรีย์ชัยฯ", "N2 พญาไท", "N1 ราชเทวี", "CEN สยาม", "E1 ชิดลม",
            "E2 เพลินจิต", "E3 นานา", "E4 อโศก", "E5 พร้อมพงษ์", "E6 ทองหล่อ",
            "E7 เอกมัย", "E8 พระโขนง", "E9 อ่อนนุช", "E10 บางจาก", "E11 ปุณณวิถี",
            "E12 อุดมสุข", "E13 บางนา", "E14 แบริ่ง", "E15 สำโรง", "E16 ปู่เจ้า",
            "E17 ช้างเอราวัณ", "E18 โรงเรียนนายเรือ", "E19 ปากน้ำ", "E20 ศรีนครินทร์",
            "E21 แพรกษา", "E22 สายลวด", "E23 เคหะฯ"
        ));
    }

    // ฟังก์ชันสำหรับโหลดสถานีจากไฟล์
    private void loadStations() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(STATIONS_FILE), "UTF-8"))) {
            String line;
            stationList.clear();
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    stationList.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            // หากเกิดข้อผิดพลาดในการโหลด ให้ใช้สถานีเริ่มต้น
            initializeDefaultStations();
        }
    }

    // ฟังก์ชันสำหรับบันทึกสถานีลงไฟล์
    private void saveStations() {
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(STATIONS_FILE), "UTF-8"))) {
            for (String station : stationList) {
                bw.write(station);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getStationList() {
        return stationList;
    }

    public Map<Integer, Integer> getFareMap() {
        return fareMap;
    }

    public int getFlatRateFare() {
        return flatRateFare;
    }

    public void setFlatRateFare(int flatRate) {
        this.flatRateFare = flatRate;
    }

    public void updateFare(int distance, int fare) {
        if (distance > 0) {
            fareMap.put(distance, fare);
        }
    }

    public void addStationFirst(String stationName) {
        if (stationName != null && !stationName.trim().isEmpty()) {
            stationList.add(0, stationName.trim());
            saveStations(); // บันทึกหลังจากเพิ่มสถานี
        }
    }

    public void addStationLast(String stationName) {
        if (stationName != null && !stationName.trim().isEmpty()) {
            stationList.add(stationName.trim());
            saveStations(); // บันทึกหลังจากเพิ่มสถานี
        }
    }

    public void addStationAfter(String stationName, String afterStation) {
        if (stationName == null || stationName.trim().isEmpty()) return;
        stationName = stationName.trim();
        int idx = stationList.indexOf(afterStation);
        if (idx >= 0) {
            stationList.add(idx + 1, stationName);
        } else {
            stationList.add(stationName);
        }
        saveStations(); // บันทึกหลังจากเพิ่มสถานี
    }

    public void removeStation(String stationName) {
        stationList.remove(stationName);
        saveStations(); // บันทึกหลังจากลบสถานี
    }

    public int calculateFare(int startIndex, int endIndex, int numTickets) {
        int distance = Math.abs(endIndex - startIndex);
        int farePerTicket;

        if (distance > 8) {
            farePerTicket = flatRateFare; // เหมาจ่าย
        } else {
            farePerTicket = fareMap.getOrDefault(distance, 15); // ค่าเริ่มต้นคือ 15
        }
        return farePerTicket * numTickets;
    }

    public TransactionData getTransactionData() {
        return transactionData;
    }

    public Map<Integer, Integer> calculateChangeCoins(int amount) {
        Map<Integer, Integer> coins = new LinkedHashMap<>();
        int[] denominations = {100, 50, 20, 10, 5, 2, 1};
        for (int denom : denominations) {
            if (amount >= denom) {
                coins.put(denom, amount / denom);
                amount %= denom;
            }
        }
        return coins;
    }

    // ปรับปรุงเมธอดนี้เพื่อบันทึก transaction_data ลงใน transaction_data.txt
    public void saveTransactionData() {
        try (BufferedWriter w = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(TRANSACTION_FILE), "UTF-8"))) {

            w.write("ธุรกรรม (ต้นทาง, ปลายทาง, จำนวนตั๋ว, payment)\n");
            for (var t : transactionData.getTransactionList()) {
                w.write(t.toString() + "\n");
            }

            w.write("\nรายการรายรับ:\n");
            for (var r : transactionData.getRevenueList()) {
                w.write(r + "\n");
            }
            w.write("\nรายการเงินทอน:\n");
            for (var c : transactionData.getChangeList()) {
                w.write(c + "\n");
            }
            w.write("\nสถิติการเลือกสถานีต้นทาง:\n");
            for (var en : transactionData.getStartStationStats().entrySet()) {
                w.write(en.getKey() + ": " + en.getValue() + "\n");
            }
            w.write("\nสถิติการเลือกสถานีปลายทาง:\n");
            for (var en : transactionData.getEndStationStats().entrySet()) {
                w.write(en.getKey() + ": " + en.getValue() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeTransactionFile() {
        File f = new File(TRANSACTION_FILE);
        if (!f.exists()) {
            try (BufferedWriter w = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(TRANSACTION_FILE), "UTF-8"))) {
                w.write("ธุรกรรม (ต้นทาง, ปลายทาง, จำนวนตั๋ว, payment)\n\n");
                w.write("รายการรายรับ:\n\n");
                w.write("รายการเงินทอน:\n\n");
                w.write("สถิติการเลือกสถานีต้นทาง:\n\n");
                w.write("สถิติการเลือกสถานีปลายทาง:\n\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // ปรับปรุงเมธอดนี้เพื่อเพิ่มข้อมูลจาก transaction_data ลงใน backup_data
    public void saveBackupData() {
        try (BufferedWriter w = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(BACKUP_FILE, true), "UTF-8"))) { // เปิดในโหมด append

            // ตรวจสอบว่า backup_data มีข้อมูลอยู่แล้วหรือไม่ เพื่อเพิ่มหัวข้อเฉพาะครั้งแรก
            File backupFile = new File(BACKUP_FILE);
            boolean isNewFile = backupFile.length() == 0;

            if (!isNewFile) {
                w.write("\n"); // เพิ่มบรรทัดว่างเพื่อแยกข้อมูลชุดใหม่
            }

            // บันทึกธุรกรรม
            w.write("ธุรกรรม (ต้นทาง, ปลายทาง, จำนวนตั๋ว, payment)\n");
            for (var t : transactionData.getTransactionList()) {
                w.write(t.toString() + "\n");
            }

            // บันทึกรายการรายรับ
            w.write("\nรายการรายรับ:\n");
            for (var r : transactionData.getRevenueList()) {
                w.write(r + "\n");
            }

            // บันทึกรายการเงินทอน
            w.write("\nรายการเงินทอน:\n");
            for (var c : transactionData.getChangeList()) {
                w.write(c + "\n");
            }

            // บันทึกสถิติการเลือกสถานีต้นทาง
            w.write("\nสถิติการเลือกสถานีต้นทาง:\n");
            for (var en : transactionData.getStartStationStats().entrySet()) {
                w.write(en.getKey() + ": " + en.getValue() + "\n");
            }

            // บันทึกสถิติการเลือกสถานีปลายทาง
            w.write("\nสถิติการเลือกสถานีปลายทาง:\n");
            for (var en : transactionData.getEndStationStats().entrySet()) {
                w.write(en.getKey() + ": " + en.getValue() + "\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // เมธอดนี้ใช้สำหรับโหลดข้อมูลธุรกรรมจาก transaction_data.txt หากต้องการ
    private void loadTransactionData() {
        File f = new File(TRANSACTION_FILE);
        if (!f.exists()) return;

        try (BufferedReader r = new BufferedReader(new InputStreamReader(
                new FileInputStream(f), "UTF-8"))) {
            String line;
            String section = "";

            while ((line = r.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("ธุรกรรม (ต้นทาง, ปลายทาง, จำนวนตั๋ว, payment)")) {
                    section = "TRANSACTION";
                    continue;
                } else if (line.contains("รายการรายรับ:")) {
                    section = "REVENUE";
                    continue;
                } else if (line.contains("รายการเงินทอน:")) {
                    section = "CHANGE";
                    continue;
                } else if (line.contains("สถิติการเลือกสถานีต้นทาง:")) {
                    section = "START";
                    continue;
                } else if (line.contains("สถิติการเลือกสถานีปลายทาง:")) {
                    section = "END";
                    continue;
                }
                if (line.isEmpty()) continue;

                switch (section) {
                    case "TRANSACTION":
                        if (line.startsWith("(") && line.endsWith(")")) {
                            String data = line.substring(1, line.length() - 1);
                            String[] parts = data.split(",");
                            if (parts.length == 4) {
                                String s = parts[0].trim();
                                String e = parts[1].trim();
                                int n = Integer.parseInt(parts[2].trim());
                                int pay = Integer.parseInt(parts[3].trim());
                                transactionData.getTransactionList()
                                        .add(new TransactionData.Transaction(s, e, n, pay));
                            }
                        }
                        break;
                    case "REVENUE":
                        transactionData.getRevenueList().add(Integer.parseInt(line));
                        break;
                    case "CHANGE":
                        transactionData.getChangeList().add(Integer.parseInt(line));
                        break;
                    case "START":
                        String[] sp = line.split(":");
                        if (sp.length == 2) {
                            transactionData.getStartStationStats()
                                    .put(sp[0].trim(), Integer.parseInt(sp[1].trim()));
                        }
                        break;
                    case "END":
                        String[] ep = line.split(":");
                        if (ep.length == 2) {
                            transactionData.getEndStationStats()
                                    .put(ep[0].trim(), Integer.parseInt(ep[1].trim()));
                        }
                        break;
                }
            }

            // หลังจากโหลด transaction_data แล้ว สามารถเลือกที่จะล้างไฟล์ transaction_data.txt ได้
            // เพื่อเตรียมพร้อมสำหรับเซสชั่นใหม่
            // clearTransactionDataFile();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // เมธอดนี้ใช้สำหรับล้างข้อมูลใน transaction_data.txt หลังจากบันทึกลง backup_data แล้ว
    private void clearTransactionDataFile() {
        try (BufferedWriter w = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(TRANSACTION_FILE), "UTF-8"))) {
            w.write("ธุรกรรม (ต้นทาง, ปลายทาง, จำนวนตั๋ว, payment)\n\n");
            w.write("รายการรายรับ:\n\n");
            w.write("รายการเงินทอน:\n\n");
            w.write("สถิติการเลือกสถานีต้นทาง:\n\n");
            w.write("สถิติการเลือกสถานีปลายทาง:\n\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveBackupAndClearTransaction() {
        saveBackupData();
        clearTransactionDataFile();
    }

    public void loadBackupData() {
        File f = new File(BACKUP_FILE);
        if (!f.exists()) return;

        try (BufferedReader r = new BufferedReader(new InputStreamReader(
                new FileInputStream(f), "UTF-8"))) {
            String line;
            String section = "";

            while ((line = r.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("ธุรกรรม (ต้นทาง, ปลายทาง, จำนวนตั๋ว, payment)")) {
                    section = "TRANSACTION";
                    continue;
                } else if (line.contains("รายการรายรับ:")) {
                    section = "REVENUE";
                    continue;
                } else if (line.contains("รายการเงินทอน:")) {
                    section = "CHANGE";
                    continue;
                } else if (line.contains("สถิติการเลือกสถานีต้นทาง:")) {
                    section = "START";
                    continue;
                } else if (line.contains("สถิติการเลือกสถานีปลายทาง:")) {
                    section = "END";
                    continue;
                }
                if (line.isEmpty()) continue;

                switch (section) {
                    case "TRANSACTION":
                        if (line.startsWith("(") && line.endsWith(")")) {
                            String data = line.substring(1, line.length() - 1);
                            String[] parts = data.split(",");
                            if (parts.length == 4) {
                                String s = parts[0].trim();
                                String e = parts[1].trim();
                                int n = Integer.parseInt(parts[2].trim());
                                int pay = Integer.parseInt(parts[3].trim());
                                transactionData.getTransactionList()
                                        .add(new TransactionData.Transaction(s, e, n, pay));
                            }
                        }
                        break;
                    case "REVENUE":
                        transactionData.getRevenueList().add(Integer.parseInt(line));
                        break;
                    case "CHANGE":
                        transactionData.getChangeList().add(Integer.parseInt(line));
                        break;
                    case "START":
                        String[] sp = line.split(":");
                        if (sp.length == 2) {
                            transactionData.getStartStationStats()
                                    .put(sp[0].trim(), Integer.parseInt(sp[1].trim()));
                        }
                        break;
                    case "END":
                        String[] ep = line.split(":");
                        if (ep.length == 2) {
                            transactionData.getEndStationStats()
                                    .put(ep[0].trim(), Integer.parseInt(ep[1].trim()));
                        }
                        break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getTotalIncome() {
        int sum = 0;
        for (int r : transactionData.getRevenueList()) {
            sum += r;
        }
        return sum;
    }

    public int getTotalChange() {
        int sum = 0;
        for (int c : transactionData.getChangeList()) {
            sum += c;
        }
        return sum;
    }

    public int getTotalPayment() {
        int sum = 0;
        for (TransactionData.Transaction t : transactionData.getTransactionList()) {
            sum += t.getPayment();
        }
        return sum;
    }



    public int getTotalTransactions() {
        return transactionData.getTransactionList().size();
    }

    public int getTotalTickets() {
        int total = 0;
        for (var t : transactionData.getTransactionList()) {
            total += t.getNumTickets();
        }
        return total;
    }
}