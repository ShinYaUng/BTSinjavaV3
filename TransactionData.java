import java.util.*;

public class TransactionData {
    public static class Transaction {
        private String startStation;
        private String endStation;
        private int numTickets;
        private int payment; // เก็บจำนวนเงินที่ผู้ใช้หยอด

        public Transaction(String startStation, String endStation, int numTickets, int payment) {
            this.startStation = startStation;
            this.endStation = endStation;
            this.numTickets = numTickets;
            this.payment = payment;
        }

        public String getStartStation() { return startStation; }
        public String getEndStation()   { return endStation; }
        public int    getNumTickets()   { return numTickets; }
        public int    getPayment()      { return payment; }

        @Override
        public String toString() {
            // (start, end, tickets, payment)
            return "(" + startStation + ", " + endStation + ", " 
                     + numTickets + ", " + payment + ")";
        }
    }

    // เก็บรายการ Transaction
    private List<Transaction> transactionList = new ArrayList<>();
    // เก็บรายรับ (fare) และเงินทอน (change) คู่ขนาน
    private List<Integer> revenueList = new ArrayList<>();
    private List<Integer> changeList  = new ArrayList<>();

    // เก็บ payment แยกก็ได้ (ไม่จำเป็นถ้ามีใน Transaction)
    private List<Integer> paymentList = new ArrayList<>();

    // สถิติสถานีต้นทาง/ปลายทาง
    private Map<String, Integer> startStationStats = new HashMap<>();
    private Map<String, Integer> endStationStats   = new HashMap<>();

    // Getter
    public List<Transaction> getTransactionList()      { return transactionList; }
    public List<Integer> getRevenueList()              { return revenueList; }
    public List<Integer> getChangeList()               { return changeList; }
    public List<Integer> getPaymentList()              { return paymentList; }
    public Map<String, Integer> getStartStationStats() { return startStationStats; }
    public Map<String, Integer> getEndStationStats()   { return endStationStats; }

    public void addStartStationStat(String station) {
        startStationStats.putIfAbsent(station, 0);
        startStationStats.put(station, startStationStats.get(station) + 1);
    }

    public void addEndStationStat(String station) {
        endStationStats.putIfAbsent(station, 0);
        endStationStats.put(station, endStationStats.get(station) + 1);
    }
}