import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HotelBookingGUI {
    private JFrame frame;
    private HotelBookingSystem system;

    public HotelBookingGUI() {
        system = new HotelBookingSystem(); // Loads bookings from file
        frame = new JFrame("🏨 Hotel Booking System");
        frame.setSize(700, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(2, 2, 20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        panel.setBackground(new Color(245, 245, 245));

        JButton bookRoomBtn = createStyledButton("📝 Book Room");
        JButton viewBookingsBtn = createStyledButton("📋 View All Bookings");
        JButton checkoutBtn = createStyledButton("✅ Checkout Guest");
        JButton exitBtn = createStyledButton("🚪 Exit");

        panel.add(bookRoomBtn);
        panel.add(viewBookingsBtn);
        panel.add(checkoutBtn);
        panel.add(exitBtn);

        frame.add(panel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Action listeners
        bookRoomBtn.addActionListener(e -> showBookingForm());
        viewBookingsBtn.addActionListener(e -> showAllBookings());
        checkoutBtn.addActionListener(e -> showCheckoutDialog());
        exitBtn.addActionListener(e -> {
            system.saveBookingsToFile();
            frame.dispose();
        });
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setBackground(new Color(66, 133, 244));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return btn;
    }

    private void showBookingForm() {
        JDialog dialog = new JDialog(frame, "📝 Book a Room", true);
        dialog.setSize(400, 350);
        dialog.setLayout(new GridLayout(6, 2, 10, 10));
        dialog.getContentPane().setBackground(Color.WHITE);
        dialog.setResizable(false);

        JTextField nameField = new JTextField();
        JTextField contactField = new JTextField();
        JTextField idField = new JTextField();
        JTextField checkInField = new JTextField("dd-MM-yyyy");
        JTextField checkOutField = new JTextField("dd-MM-yyyy");

        dialog.add(new JLabel("Name:")); dialog.add(nameField);
        dialog.add(new JLabel("Contact:")); dialog.add(contactField);
        dialog.add(new JLabel("ID Proof:")); dialog.add(idField);
        dialog.add(new JLabel("Check-in Date:")); dialog.add(checkInField);
        dialog.add(new JLabel("Check-out Date:")); dialog.add(checkOutField);

        JButton submitBtn = new JButton("Book");
        submitBtn.setBackground(new Color(52, 168, 83));
        submitBtn.setForeground(Color.WHITE);
        submitBtn.setFont(new Font("Arial", Font.BOLD, 14));
        dialog.add(new JLabel()); dialog.add(submitBtn);

        submitBtn.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String contact = contactField.getText().trim();
                String id = idField.getText().trim();
                Date checkIn = new SimpleDateFormat("dd-MM-yyyy").parse(checkInField.getText().trim());
                Date checkOut = new SimpleDateFormat("dd-MM-yyyy").parse(checkOutField.getText().trim());

                if (name.isEmpty() || contact.isEmpty() || id.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please fill all fields.");
                    return;
                }

                if (!checkOut.after(checkIn)) {
                    JOptionPane.showMessageDialog(dialog, "Check-out must be after check-in.");
                    return;
                }

                int room = system.getAvailableRoom();
                if (room == -1) {
                    JOptionPane.showMessageDialog(dialog, "No rooms available.");
                } else {
                    system.bookings.add(new Guest(name, contact, id, room, checkIn, checkOut));
                    system.saveBookingsToFile();  // Save after booking
                    JOptionPane.showMessageDialog(dialog, "Room " + room + " booked successfully!");
                    dialog.dispose();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });

        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
    }

    private void showAllBookings() {
        JDialog dialog = new JDialog(frame, "📋 All Bookings", true);
        dialog.setSize(800, 300);

        String[] cols = {"Room", "Name", "Contact", "ID", "Check-in", "Check-out"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        for (Guest g : system.bookings) {
            model.addRow(new Object[]{g.roomNumber, g.name, g.contact, g.idProof,
                    sdf.format(g.checkIn), sdf.format(g.checkOut)});
        }

        JTable table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(24);
        dialog.add(new JScrollPane(table));
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
    }

    private void showCheckoutDialog() {
        String input = JOptionPane.showInputDialog(frame, "Enter room number to checkout:");
        try {
            int roomNumber = Integer.parseInt(input);
            Guest guest = system.bookings.stream()
                    .filter(g -> g.roomNumber == roomNumber)
                    .findFirst().orElse(null);

            if (guest != null) {
                long diff = (guest.checkOut.getTime() - guest.checkIn.getTime()) / (1000 * 60 * 60 * 24);
                diff = diff == 0 ? 1 : diff;
                int bill = (int) diff * HotelBookingSystem.RATE_PER_DAY;
                system.bookings.remove(guest);
                system.saveBookingsToFile(); // Save after checkout
                JOptionPane.showMessageDialog(frame, "Checkout successful.\nTotal bill: ₹" + bill);
            } else {
                JOptionPane.showMessageDialog(frame, "Room not found.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Invalid room number.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(HotelBookingGUI::new);
    }
}
