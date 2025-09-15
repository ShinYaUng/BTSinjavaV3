import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.table.DefaultTableModel;

public class UserGUI extends JFrame {
    private TicketMachine ticketMachine;

    private JComboBox<String> startStationBox;
    private JComboBox<String> endStationBox;
    private JSpinner ticketSpinner; 
    private JLabel fareLabel;  
    private JTextField paymentField;

    private boolean paymentMade = false; 

    private List<SessionPurchase> sessionPurchases = new ArrayList<>();
    private int purchaseCount = 0;

    private static class SessionPurchase {
        int orderNo;
        String start;
        String end;
        int tickets;
        int fare;
        int payment;
        int change;
        public SessionPurchase(int orderNo, String start, String end, 
                               int tickets, int fare, int payment, int change){
            this.orderNo = orderNo; 
            this.start = start; 
            this.end = end;
            this.tickets = tickets; 
            this.fare = fare;
            this.payment = payment; 
            this.change = change;
        }
    }

    public UserGUI(TicketMachine tm) {
        this.ticketMachine = tm;
        initUI();
    }

    private void initUI() {
        try {
            setTitle("BTS Ticket Machine (User)");
            setSize(700, 500);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(EXIT_ON_CLOSE);

            JMenuBar menuBar = new JMenuBar();

            JMenu adminMenu = new JMenu("Admin");
            JMenuItem openAdminItem = new JMenuItem("Open Admin Panel");
            openAdminItem.addActionListener(e -> openAdminPanel());
            adminMenu.add(openAdminItem);
            menuBar.add(adminMenu);

            JMenu userMenu = new JMenu("User");
            JMenuItem showStationsItem = new JMenuItem("ดูสถานีทั้งหมด");
            showStationsItem.addActionListener(e -> showAllStations());
            userMenu.add(showStationsItem);

            JMenuItem sessionSummaryItem = new JMenuItem("View Session Summary");
            sessionSummaryItem.addActionListener(e -> showSessionSummary());
            userMenu.add(sessionSummaryItem);

            JMenuItem helpItem = new JMenuItem("Help (คู่มือใช้งาน)");
            helpItem.addActionListener(e -> showHelpDialog());
            userMenu.add(helpItem);

            menuBar.add(userMenu);
            setJMenuBar(menuBar);

            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
            add(panel);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;

            gbc.gridx = 0; 
            gbc.gridy = 0;
            panel.add(new JLabel("สถานีต้นทาง:"), gbc);

            startStationBox = new JComboBox<>(ticketMachine.getStationList().toArray(new String[0]));
            startStationBox.setSelectedIndex(-1);
            gbc.gridx = 1; 
            gbc.gridy = 0;
            panel.add(startStationBox, gbc);

            gbc.gridx = 0; 
            gbc.gridy = 1;
            panel.add(new JLabel("สถานีปลายทาง:"), gbc);

            endStationBox = new JComboBox<>(ticketMachine.getStationList().toArray(new String[0]));
            endStationBox.setSelectedIndex(-1);
            gbc.gridx = 1; 
            gbc.gridy = 1;
            panel.add(endStationBox, gbc);

            gbc.gridx = 0; 
            gbc.gridy = 2;
            panel.add(new JLabel("จำนวนตั๋ว (1–10):"), gbc);

            ticketSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
            ((JSpinner.DefaultEditor) ticketSpinner.getEditor()).getTextField().setEditable(false);
            gbc.gridx = 1; 
            gbc.gridy = 2;
            panel.add(ticketSpinner, gbc);

            gbc.gridx = 0; 
            gbc.gridy = 3;
            panel.add(new JLabel("ค่าโดยสาร (บาท):"), gbc);

            fareLabel = new JLabel("-");
            gbc.gridx = 1; 
            gbc.gridy = 3;
            panel.add(fareLabel, gbc);

            gbc.gridx = 0; 
            gbc.gridy = 4;
            panel.add(new JLabel("ใส่จำนวนเงิน (บาท):"), gbc);

            paymentField = new JTextField("0",10);
            paymentField.setEditable(false);
            gbc.gridx = 1; 
            gbc.gridy = 4;
            panel.add(paymentField, gbc);

            JPanel moneyPanel = new JPanel(new FlowLayout());
            String[] moneyBtns = {"100","50","20","10","5","2","1"};
            for(String m : moneyBtns){
                JButton b = new JButton(m);
                b.addActionListener(e -> addMoney(Integer.parseInt(m)));
                moneyPanel.add(b);
            }
            gbc.gridx = 1; 
            gbc.gridy = 5;
            panel.add(moneyPanel, gbc);

            JButton payBtn = new JButton("ชำระเงิน");
            gbc.gridx = 0; 
            gbc.gridy = 6;
            panel.add(payBtn, gbc);

            JButton resetBtn = new JButton("เลือกใหม่");
            gbc.gridx = 1; 
            gbc.gridy = 6;
            panel.add(resetBtn, gbc);

            payBtn.addActionListener(e -> processPayment());
            resetBtn.addActionListener(e -> resetAll());

            startStationBox.addActionListener(evt -> updateFare());
            endStationBox.addActionListener(evt -> updateFare());
            ticketSpinner.addChangeListener((ChangeEvent e) -> updateFare());

            setVisible(true);
            System.out.println("UserGUI ถูกแสดงผลเรียบร้อยแล้ว");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "เกิดข้อผิดพลาดในการสร้าง UI: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showHelpDialog() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== วิธีใช้งานตู้จำหน่ายตั๋ว ===\n\n")
          .append("1) เลือกสถานีต้นทางและปลายทาง\n")
          .append("2) เลือกจำนวนตั๋ว (1–10)\n")
          .append("3) ใส่เงินให้เพียงพอ\n")
          .append("4) กด 'ชำระเงิน' เพื่อรับตั๋วและเงินทอน\n\n")
          .append("=== ตารางราคา ===\n");
    
        // เพิ่มข้อมูลราคาตามระยะทาง
        Map<Integer, Integer> fareMap = ticketMachine.getFareMap();
        for (Map.Entry<Integer, Integer> entry : fareMap.entrySet()) {
            sb.append("ระยะทาง ").append(entry.getKey()).append(" สถานี: ")
              .append(entry.getValue()).append(" บาท\n");
        }
    
        // แสดงค่าเหมาจ่าย
        sb.append("\n=== ค่าเหมาจ่าย ===\n")
          .append("มากกว่า 8 สถานี: ").append(ticketMachine.getFlatRateFare()).append(" บาท\n");
    
        sb.append("\n=== ส่วนลด ===\n")
          .append("- ซื้อ 5 ใบขึ้นไป ลด 10%\n");
    
        JTextArea textArea = new JTextArea(sb.toString(), 15, 40);
        textArea.setEditable(false);
    
        JScrollPane sp = new JScrollPane(textArea);
        JDialog dlg = new JDialog(this, "คู่มือการใช้งาน", true);
        dlg.setSize(500, 400);
        dlg.setLocationRelativeTo(this);
        dlg.add(sp);
        dlg.setVisible(true);
    }

    private void updateFare() {
        int si = startStationBox.getSelectedIndex();
        int ei = endStationBox.getSelectedIndex();
        int numTickets = (int) ticketSpinner.getValue();

        if(si < 0 || ei < 0) {
            fareLabel.setText("-");
            return;
        }
        if(si == ei) {
            fareLabel.setText("0");
            return;
        }
        int fare = ticketMachine.calculateFare(si, ei, numTickets);

        if(numTickets >= 5) {
            int discount = (int)(fare * 0.1);
            fare -= discount;
        }

        fareLabel.setText(String.valueOf(fare));
    }

    private void addMoney(int amt) {
        try {
            int cur = Integer.parseInt(paymentField.getText());
            paymentField.setText(String.valueOf(cur + amt));
        } catch(Exception e){
            paymentField.setText(String.valueOf(amt));
        }
    }

    private void processPayment() {
        try {
            int amountInserted = Integer.parseInt(paymentField.getText());
            String fareText = fareLabel.getText();
            if(fareText.equals("-")) {
                JOptionPane.showMessageDialog(this, "กรุณาเลือกสถานีให้ถูกต้อง");
                return;
            }
            int fare = Integer.parseInt(fareText);
            if(fare == 0) {
                JOptionPane.showMessageDialog(this, "ไม่สามารถซื้อได้ (ต้นทาง=ปลายทาง หรือยังไม่เลือก)");
                return;
            }
            if(amountInserted < fare) {
                JOptionPane.showMessageDialog(this, "เงินไม่พอ กรุณาเติมเงินเพิ่ม!");
                return;
            }

            int cf = JOptionPane.showConfirmDialog(this, 
                "ยืนยันการชำระเงิน "+fare+" บาท?", 
                "Confirm", JOptionPane.YES_NO_OPTION
            );
            if(cf != JOptionPane.YES_OPTION) return;

            int change = amountInserted - fare;

            String sSt = (String) startStationBox.getSelectedItem();
            String eSt = (String) endStationBox.getSelectedItem();
            int numTickets = (int) ticketSpinner.getValue();

            TransactionData txData = ticketMachine.getTransactionData();
            TransactionData.Transaction newTx = new TransactionData.Transaction(sSt, eSt, numTickets, amountInserted);
            txData.getTransactionList().add(newTx);
            txData.getPaymentList().add(amountInserted);
            txData.getRevenueList().add(fare);
            txData.getChangeList().add(change);
            txData.addStartStationStat(sSt);
            txData.addEndStationStat(eSt);

            ticketMachine.saveTransactionData();

            purchaseCount++;
            SessionPurchase sp = new SessionPurchase(
                purchaseCount, sSt, eSt, numTickets, fare, amountInserted, change
            );
            sessionPurchases.add(sp);

            Map<Integer,Integer> coins = ticketMachine.calculateChangeCoins(change);
            StringBuilder sb = new StringBuilder();
            sb.append("ต้นทาง: ").append(sSt).append("\n");
            sb.append("ปลายทาง: ").append(eSt).append("\n");
            sb.append("จำนวนตั๋ว: ").append(numTickets).append("\n");
            sb.append("ค่าโดยสาร: ").append(fare).append(" บาท\n");
            sb.append("ชำระ: ").append(amountInserted).append(" บาท\n");
            sb.append("เงินทอน: ").append(change).append(" บาท\n\n");
            sb.append("รายละเอียดเหรียญทอน:\n");
            for(Map.Entry<Integer,Integer> entry : coins.entrySet()){
                sb.append("เหรียญ ").append(entry.getKey())
                  .append(": ").append(entry.getValue()).append(" เหรียญ\n");
            }
            JOptionPane.showMessageDialog(this, sb.toString(), 
                    "ชำระเงินสำเร็จ", JOptionPane.INFORMATION_MESSAGE);

            paymentMade = true;

        } catch(Exception ex){
            JOptionPane.showMessageDialog(this, "เกิดข้อผิดพลาดในการชำระเงิน");
            ex.printStackTrace();
        }
    }

    private void resetAll() {
        if(!paymentMade) {
            try {
                int amt = Integer.parseInt(paymentField.getText());
                if(amt > 0) {
                    Map<Integer, Integer> coins = ticketMachine.calculateChangeCoins(amt);
                    StringBuilder sb = new StringBuilder();
                    sb.append("คืนเงิน: ").append(amt).append(" บาท\n");
                    sb.append("รายละเอียดเหรียญ:\n");
                    for(Map.Entry<Integer, Integer> en : coins.entrySet()) {
                        sb.append("เหรียญ ").append(en.getKey())
                          .append(": ").append(en.getValue()).append("\n");
                    }
                    JOptionPane.showMessageDialog(this, sb.toString());
                }
            } catch(Exception ex){
                // ignore
            }
        }
        startStationBox.setSelectedIndex(-1);
        endStationBox.setSelectedIndex(-1);
        ticketSpinner.setValue(1);
        fareLabel.setText("-");
        paymentField.setText("0");
        paymentMade = false;
    }

    private void showSessionSummary() {
        JDialog dlg = new JDialog(this, "Session Summary", true);
        dlg.setSize(800, 400);
        dlg.setLocationRelativeTo(this);

        String[] columnNames = {
            "ลำดับ", "ต้นทาง", "ปลายทาง", "จำนวนตั๋ว", 
            "ค่าโดยสาร (บาท)", "ชำระ(บาท)", "เงินทอน(บาท)"
        };

        Object[][] data = new Object[sessionPurchases.size()][7];
        int totalTickets = 0;
        int totalFare    = 0;
        int totalPay     = 0;
        int totalChange  = 0;

        for(int i = 0; i < sessionPurchases.size(); i++){
            SessionPurchase x = sessionPurchases.get(i);
            data[i][0] = x.orderNo;
            data[i][1] = x.start;
            data[i][2] = x.end;
            data[i][3] = x.tickets;
            data[i][4] = x.fare;
            data[i][5] = x.payment;
            data[i][6] = x.change;

            totalTickets += x.tickets;
            totalFare    += x.fare;
            totalPay     += x.payment;
            totalChange  += x.change;
        }

        DefaultTableModel model = new DefaultTableModel(data, columnNames){
            @Override public boolean isCellEditable(int row, int col){return false;}
        };
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        JLabel bottomLabel = new JLabel(
            "รวมธุรกรรม: " + sessionPurchases.size()
          + " | รวมตั๋ว: " + totalTickets + " ใบ"
          + " | รวมค่าโดยสาร: " + totalFare + " บาท"
          + " | จำนวนเงินที่หยอด: " + totalPay + " บาท"
          + " | รวมเงินทอน: " + totalChange + " บาท"
        );

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(bottomLabel, BorderLayout.SOUTH);

        dlg.add(mainPanel);
        dlg.setVisible(true);
    }

    private void openAdminPanel() {
        JPanel p = new JPanel(new GridLayout(2,2));
        p.add(new JLabel("Username:"));
        JTextField userField = new JTextField();
        p.add(userField);

        p.add(new JLabel("Password:"));
        JPasswordField passField = new JPasswordField();
        p.add(passField);

        int res = JOptionPane.showConfirmDialog(this, p, "Admin Login",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if(res == JOptionPane.OK_OPTION){
            String u = userField.getText().trim();
            String ps = new String(passField.getPassword()).trim();
            if(u.equals("admin") && ps.equals("1234")) {
                try {
                    new AdminGUI(ticketMachine, this);
                } catch(Exception e){
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "เกิดข้อผิดพลาดในการเปิด Admin Panel: " + e.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(this, "Username/Password ไม่ถูกต้อง!");
            }
        }
    }

    private void showAllStations() {
        List<String> stations = ticketMachine.getStationList();
        if (stations == null || stations.isEmpty()) {
            JOptionPane.showMessageDialog(this, "ไม่มีข้อมูลสถานี กรุณาตรวจสอบการตั้งค่า");
            return;
        }
        StringBuilder sb = new StringBuilder("=== สถานีทั้งหมด ===\n\n");
        for (String station : stations) {
            sb.append(station).append("\n");
        }
        JTextArea textArea = new JTextArea(sb.toString(), 20, 30);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        JOptionPane.showMessageDialog(this, scrollPane, "สถานีทั้งหมด", JOptionPane.INFORMATION_MESSAGE);
    }

    public void reloadStationCombo() {
        startStationBox.removeAllItems();
        endStationBox.removeAllItems();
        List<String> stations = ticketMachine.getStationList();
        for(String s : stations){
            startStationBox.addItem(s);
            endStationBox.addItem(s);
        }
        startStationBox.setSelectedIndex(-1);
        endStationBox.setSelectedIndex(-1);
        fareLabel.setText("-");
    }

    @Override
    public void dispose() {
        ticketMachine.saveBackupData();
        super.dispose();
    }
}

