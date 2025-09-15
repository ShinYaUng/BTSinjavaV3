import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Map;

public class AdminGUI extends JFrame {

    private TicketMachine ticketMachine;
    private UserGUI userGUI;

    private JTable transactionTable;
    private JLabel summaryLabel;

    private JComboBox<String> afterStationBox;
    private JComboBox<String> removeStationBox;

    public AdminGUI(TicketMachine ticketMachine, UserGUI userGUI) {
        this.ticketMachine = ticketMachine;
        this.userGUI = userGUI;
        initUI();
    }

    private void initUI() {
        setTitle("Admin Panel");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Manage Stations", createManagePanel());
        tabbedPane.addTab("Transaction Logs", createTransactionLogPanel());
        tabbedPane.addTab("Station Info", createStationInfoPanel()); // อาจพิจารณาลบถ้าไม่ใช้
        tabbedPane.addTab("Manage Fares", createFareManagementPanel());

        add(tabbedPane);
        setVisible(true);
    }

    private JPanel createManagePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill=GridBagConstraints.HORIZONTAL;

        // เพิ่มสถานีเป็นสถานีแรก
        gbc.gridx=0; gbc.gridy=0;
        panel.add(new JLabel("เพิ่มสถานีเป็นสถานีแรก:"), gbc);

        JTextField firstField = new JTextField(10);
        gbc.gridx=1; gbc.gridy=0;
        panel.add(firstField, gbc);

        JButton addFirstBtn = new JButton("Add First");
        gbc.gridx=2; gbc.gridy=0;
        panel.add(addFirstBtn, gbc);

        addFirstBtn.addActionListener(e->{
            String st= firstField.getText().trim();
            if(!st.isEmpty()){
                ticketMachine.addStationFirst(st);
                JOptionPane.showMessageDialog(this, 
                    "เพิ่มสถานี '"+st+"' เป็นสถานีแรกเรียบร้อย");
                firstField.setText("");
                reloadStationData();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "กรุณากรอกชื่อสถานีก่อนทำการเพิ่ม", 
                    "ข้อผิดพลาด", JOptionPane.ERROR_MESSAGE);
            }
        });

        // เพิ่มสถานีเป็นสถานีสุดท้าย
        gbc.gridx=0; gbc.gridy=1;
        panel.add(new JLabel("เพิ่มสถานีเป็นสถานีสุดท้าย:"), gbc);

        JTextField lastField = new JTextField(10);
        gbc.gridx=1; gbc.gridy=1;
        panel.add(lastField, gbc);

        JButton addLastBtn = new JButton("Add Last");
        gbc.gridx=2; gbc.gridy=1;
        panel.add(addLastBtn, gbc);

        addLastBtn.addActionListener(e->{
            String st = lastField.getText().trim();
            if(!st.isEmpty()){
                ticketMachine.addStationLast(st);
                JOptionPane.showMessageDialog(this, 
                    "เพิ่มสถานี '"+st+"' เป็นสถานีสุดท้ายเรียบร้อย");
                lastField.setText("");
                reloadStationData();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "กรุณากรอกชื่อสถานีก่อนทำการเพิ่ม", 
                    "ข้อผิดพลาด", JOptionPane.ERROR_MESSAGE);
            }
        });

        // เพิ่มสถานีต่อจากสถานีที่เลือก
        gbc.gridx=0; gbc.gridy=2;
        panel.add(new JLabel("เพิ่มสถานีต่อจาก:"), gbc);

        afterStationBox = new JComboBox<>(ticketMachine.getStationList().toArray(new String[0]));
        afterStationBox.setSelectedIndex(-1);
        gbc.gridx=1; gbc.gridy=2;
        panel.add(afterStationBox, gbc);

        JTextField afterField = new JTextField(10);
        gbc.gridx=2; gbc.gridy=2;
        panel.add(afterField, gbc);

        JButton addAfterBtn = new JButton("Add After");
        gbc.gridx=3; gbc.gridy=2;
        panel.add(addAfterBtn, gbc);

        addAfterBtn.addActionListener(e->{
            String st = afterField.getText().trim();
            String aft = (String) afterStationBox.getSelectedItem();
            if(!st.isEmpty() && aft != null){
                ticketMachine.addStationAfter(st, aft);
                JOptionPane.showMessageDialog(this, 
                    "เพิ่มสถานี '"+st+"' ต่อจาก: "+aft+" เรียบร้อย");
                afterField.setText("");
                reloadStationData();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "กรุณากรอกชื่อสถานีและเลือกสถานีที่ต้องการเพิ่มต่อจาก", 
                    "ข้อผิดพลาด", JOptionPane.ERROR_MESSAGE);
            }
        });

        // ลบสถานี
        gbc.gridx=0; gbc.gridy=3;
        panel.add(new JLabel("ลบสถานี:"), gbc);

        removeStationBox = new JComboBox<>(ticketMachine.getStationList().toArray(new String[0]));
        removeStationBox.setSelectedIndex(-1);
        gbc.gridx=1; gbc.gridy=3;
        panel.add(removeStationBox, gbc);

        JButton removeBtn = new JButton("Remove Station");
        gbc.gridx=2; gbc.gridy=3;
        panel.add(removeBtn, gbc);

        removeBtn.addActionListener(e->{
            String st = (String) removeStationBox.getSelectedItem();
            if(st != null){
                int confirm = JOptionPane.showConfirmDialog(this, 
                    "ต้องการลบสถานี: "+st+" ใช่หรือไม่?", 
                    "ยืนยันการลบ", JOptionPane.YES_NO_OPTION);
                if(confirm == JOptionPane.YES_OPTION){
                    ticketMachine.removeStation(st);
                    JOptionPane.showMessageDialog(this, 
                        "ลบสถานี: "+st+" เรียบร้อย");
                    reloadStationData();
                }
            } else {
                JOptionPane.showMessageDialog(this, 
                    "กรุณาเลือกสถานีที่ต้องการลบ", 
                    "ข้อผิดพลาด", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    private JPanel createTransactionLogPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        transactionTable = new JTable();
        panel.add(new JScrollPane(transactionTable), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());

        JButton reloadBtn = new JButton("Reload Logs");
        reloadBtn.addActionListener(e-> loadTransactionTable());
        bottomPanel.add(reloadBtn, BorderLayout.NORTH);

        summaryLabel = new JLabel("สรุปข้อมูล: ");
        bottomPanel.add(summaryLabel, BorderLayout.SOUTH);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        loadTransactionTable();
        return panel;
    }

    private void loadTransactionTable() {
        String[] cols = {"ลำดับ", "Start Station (สถานีแรก)", "End Station (สถานีสุดท้าย)", "Tickets (จำนวนตั๋ว)", "Payment (฿)", "Fare (฿)", "Change (฿)"};

        var txs = ticketMachine.getTransactionData().getTransactionList();
        var rev = ticketMachine.getTransactionData().getRevenueList();
        var chg = ticketMachine.getTransactionData().getChangeList();
        var pay = ticketMachine.getTransactionData().getPaymentList();

        int rowCount = txs.size();
        Object[][] data = new Object[rowCount][7];
        for(int i=0; i<rowCount; i++){
            var t = txs.get(i);
            data[i][0] = i + 1; // ลำดับเริ่มต้นที่ 1
            data[i][1] = t.getStartStation();
            data[i][2] = t.getEndStation();
            data[i][3] = t.getNumTickets();
            data[i][4] = t.getPayment();
            data[i][5] = rev.get(i);
            data[i][6] = chg.get(i);
        }

        DefaultTableModel model = new DefaultTableModel(data, cols){
            @Override public boolean isCellEditable(int r,int c){return false;}
        };
        transactionTable.setModel(model);

        // อัปเดตสรุปข้อมูลที่ Transaction Logs
        int totalTx = ticketMachine.getTotalTransactions();
        int totalTk = ticketMachine.getTotalTickets();
        int totalPay= ticketMachine.getTotalPayment();
        int totalIn = ticketMachine.getTotalIncome();
        int totalOut= ticketMachine.getTotalChange();

        summaryLabel.setText(
            "สรุปข้อมูล: " +
            "Total Transactions: " + totalTx +
            " | Total Tickets: " + totalTk +
            " | Payment: " + totalPay + " ฿" +
            " | Revenue: " + totalIn + " ฿" +
            " | Change: " + totalOut + " ฿"
        );
    }

    private JPanel createStationInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // แถบบนสุดมีชื่อและปุ่มรีโหลด
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel infoLabel = new JLabel("ข้อมูลสถานี:");
        topPanel.add(infoLabel, BorderLayout.WEST);
        
        JButton reloadButton = new JButton("รีโหลดข้อมูล");
        topPanel.add(reloadButton, BorderLayout.EAST);
        
        panel.add(topPanel, BorderLayout.NORTH);
        
        JTable stationTable = new JTable();
        reloadStationInfoTable(stationTable);
        panel.add(new JScrollPane(stationTable), BorderLayout.CENTER);
        
        // เพิ่ม ActionListener ให้กับปุ่มรีโหลด
        reloadButton.addActionListener(e -> {
            try {
                ticketMachine.loadBackupData(); // โหลดข้อมูลจากแบ็คอัพ
                reloadStationInfoTable(stationTable); // รีเฟรชตารางข้อมูลสถานี
                reloadStationData(); // รีเฟรชข้อมูลสถานีใน ComboBox
                JOptionPane.showMessageDialog(this, "รีโหลดข้อมูลสถานีจากแบ็คอัพเรียบร้อยแล้ว");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "ไม่สามารถรีโหลดข้อมูลได้: " + ex.getMessage(), 
                    "ข้อผิดพลาด", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        return panel;
    }

    private void reloadStationInfoTable(JTable stationTable) {
        String[] cols = {"ลำดับ", "ชื่อสถานี"};
        List<String> sList = ticketMachine.getStationList();
        Object[][] data = new Object[sList.size()][2];
        for(int i=0; i<sList.size(); i++){
            data[i][0] = i + 1; // ลำดับเริ่มต้นที่ 1
            data[i][1] = sList.get(i);
        }
        DefaultTableModel model = new DefaultTableModel(data,cols){
            @Override
            public boolean isCellEditable(int r, int c){return false;}
        };
        stationTable.setModel(model);
    }

    private void reloadStationData() {
        afterStationBox.removeAllItems();
        removeStationBox.removeAllItems();

        List<String> sList = ticketMachine.getStationList();
        for(String s : sList){
            afterStationBox.addItem(s);
            removeStationBox.addItem(s);
        }
        afterStationBox.setSelectedIndex(-1);
        removeStationBox.setSelectedIndex(-1);

        if(userGUI != null){
            userGUI.reloadStationCombo();
        }

        // อัปเดตข้อมูลใน "Station Info" หากยังคงใช้งาน
    }

    private JPanel createFareManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JTextArea fareDisplay = new JTextArea(15, 30);
        fareDisplay.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(fareDisplay);

        JPanel inputPanel = new JPanel(new GridLayout(3, 3, 5, 5));

        JLabel distanceLabel = new JLabel("ระยะทาง (สถานี):");
        JLabel fareLabel = new JLabel("ค่าโดยสาร (บาท):");
        JLabel flatRateLabel = new JLabel("ค่าเหมาจ่าย (บาท):");

        // ใช้ JSpinner เพื่อจำกัดเฉพาะตัวเลข
        JSpinner distanceSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 8, 1));
        distanceSpinner.setEditor(new JSpinner.NumberEditor(distanceSpinner, "#"));
        
        JSpinner fareSpinner = new JSpinner(new SpinnerNumberModel(15, 1, 500, 1));
        fareSpinner.setEditor(new JSpinner.NumberEditor(fareSpinner, "#"));
        
        JButton updateFareButton = new JButton("อัปเดตราคาโดยสาร");

        JSpinner flatRateSpinner = new JSpinner(new SpinnerNumberModel(ticketMachine.getFlatRateFare(), 1, 1000, 1));
        flatRateSpinner.setEditor(new JSpinner.NumberEditor(flatRateSpinner, "#"));
        
        JButton updateFlatRateButton = new JButton("อัปเดตราคาเหมาจ่าย");

        inputPanel.add(distanceLabel);
        inputPanel.add(distanceSpinner);
        inputPanel.add(updateFareButton);

        inputPanel.add(fareLabel);
        inputPanel.add(fareSpinner);
        inputPanel.add(new JLabel()); // ช่องว่าง

        inputPanel.add(flatRateLabel);
        inputPanel.add(flatRateSpinner);
        inputPanel.add(updateFlatRateButton);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(inputPanel, BorderLayout.SOUTH);

        updateFareDisplay(fareDisplay);

        updateFareButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int distance = (int) distanceSpinner.getValue();
                    int fare = (int) fareSpinner.getValue();
                    ticketMachine.updateFare(distance, fare);
                    JOptionPane.showMessageDialog(panel, 
                        "อัปเดตราคาโดยสารสำหรับระยะทาง " + distance + " สถานีเป็น " + fare + " บาท");
                    updateFareDisplay(fareDisplay);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panel, 
                        "กรุณากรอกข้อมูลให้ถูกต้อง", 
                        "ข้อผิดพลาด", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        updateFlatRateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int flatRate = (int) flatRateSpinner.getValue();
                    ticketMachine.setFlatRateFare(flatRate);
                    JOptionPane.showMessageDialog(panel, 
                        "อัปเดตราคาเหมาจ่ายเป็น " + flatRate + " บาท");
                    updateFareDisplay(fareDisplay);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panel, 
                        "กรุณากรอกข้อมูลให้ถูกต้อง", 
                        "ข้อผิดพลาด", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        return panel;
    }

    private void updateFareDisplay(JTextArea fareDisplay) {
        StringBuilder sb = new StringBuilder("=== ค่าโดยสารตามระยะทาง ===\n");
        for (Map.Entry<Integer, Integer> entry : ticketMachine.getFareMap().entrySet()) {
            sb.append("ระยะทาง ").append(entry.getKey()).append(" สถานี: ").append(entry.getValue()).append(" บาท\n");
        }
        sb.append("\n=== ค่าเหมาจ่าย ===\n");
        sb.append("มากกว่า 8 สถานี: ").append(ticketMachine.getFlatRateFare()).append(" บาท\n");
        fareDisplay.setText(sb.toString());
    }


}