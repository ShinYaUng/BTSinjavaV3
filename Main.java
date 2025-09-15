import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        // ตั้งค่าฟอนต์สำหรับ Swing (รองรับภาษาไทย)
        // หากเครื่องไม่มี "TH Sarabun New" อาจใช้ "Tahoma" หรือ "Angsana New" แทน
        Font thaiFont = new Font("TH Sarabun New", Font.PLAIN, 18);

        UIManager.put("Label.font", thaiFont);
        UIManager.put("Button.font", thaiFont);
        UIManager.put("ComboBox.font", thaiFont);
        UIManager.put("TextField.font", thaiFont);
        UIManager.put("Table.font", thaiFont);
        UIManager.put("TableHeader.font", thaiFont);
        UIManager.put("TextArea.font", thaiFont);
        UIManager.put("OptionPane.messageFont", thaiFont);
        UIManager.put("OptionPane.buttonFont", thaiFont);
        // สำคัญ: เมนูและเมนูไอเท็ม
        UIManager.put("Menu.font", thaiFont);
        UIManager.put("MenuItem.font", thaiFont);

        // สร้าง TicketMachine และโหลดข้อมูลสำรอง
        TicketMachine ticketMachine = new TicketMachine();
        ticketMachine.loadBackupData();

        // เปิดหน้า UserGUI
        SwingUtilities.invokeLater(() -> {
            new UserGUI(ticketMachine);
        });
    }
}