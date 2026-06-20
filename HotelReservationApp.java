import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HotelReservationApp extends JFrame {
    private ArrayList<Room> roomsList;
    private ArrayList<Booking> bookingsList;
    private final String BOOKINGS_FILE = "bookings.csv";
    
    // UI components
    private JTabbedPane tabbedPane;
    private DefaultTableModel catalogTableModel;
    private DefaultTableModel bookingsTableModel;
    private JComboBox<String> categoryFilterBox;
    
    // Booking Form components
    private JTextField guestNameField;
    private JTextField contactField;
    private JTextField roomNoField;
    private JTextField nightsField;
    
    public HotelReservationApp() {
        roomsList = new ArrayList<>();
        bookingsList = new ArrayList<>();
        
        initializeRooms();
        initUI();
        loadBookingsFromCSV();
        updateTables();
    }

    private void initializeRooms() {
        // Standard rooms (101 - 105)
        for (int i = 101; i <= 105; i++) {
            roomsList.add(new Room(i, "Standard", 100.00));
        }
        // Deluxe rooms (201 - 205)
        for (int i = 201; i <= 205; i++) {
            roomsList.add(new Room(i, "Deluxe", 180.00));
        }
        // Suite rooms (301 - 303)
        for (int i = 301; i <= 303; i++) {
            roomsList.add(new Room(i, "Suite", 350.00));
        }
    }

    private void initUI() {
        setTitle("CodeAlpha - Hotel Reservation System");
        setSize(850, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Styling
        Color primaryColor = new Color(32, 56, 100);
        Color bgColor = new Color(245, 245, 245);
        getContentPane().setBackground(bgColor);

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(primaryColor);
        JLabel titleLabel = new JLabel("HOTEL RESERVATION & BOOKING SYSTEM");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 12));

        // Tab 1: Room Catalog & Booking
        JPanel tab1Panel = new JPanel(new BorderLayout(10, 10));
        tab1Panel.setBackground(bgColor);
        tab1Panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Catalog List (Left)
        JPanel catalogPanel = new JPanel(new BorderLayout());
        catalogPanel.setBorder(BorderFactory.createTitledBorder("Rooms Directory"));
        catalogPanel.setBackground(Color.WHITE);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.add(new JLabel("Filter Category:"));
        categoryFilterBox = new JComboBox<>(new String[]{"All", "Standard", "Deluxe", "Suite"});
        filterPanel.add(categoryFilterBox);
        catalogPanel.add(filterPanel, BorderLayout.NORTH);

        String[] catalogColumns = {"Room No", "Category", "Price / Night", "Status"};
        catalogTableModel = new DefaultTableModel(catalogColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable catalogTable = new JTable(catalogTableModel);
        catalogTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane catalogScroll = new JScrollPane(catalogTable);
        catalogPanel.add(catalogScroll, BorderLayout.CENTER);

        tab1Panel.add(catalogPanel, BorderLayout.CENTER);

        // Booking Form (Right)
        JPanel bookingFormPanel = new JPanel(new GridBagLayout());
        bookingFormPanel.setPreferredSize(new Dimension(320, 400));
        bookingFormPanel.setBorder(BorderFactory.createTitledBorder("Make a Reservation"));
        bookingFormPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 0;
        bookingFormPanel.add(new JLabel("Guest Name:"), gbc);
        guestNameField = new JTextField(15);
        gbc.gridx = 1;
        bookingFormPanel.add(guestNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        bookingFormPanel.add(new JLabel("Contact No:"), gbc);
        contactField = new JTextField(15);
        gbc.gridx = 1;
        bookingFormPanel.add(contactField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        bookingFormPanel.add(new JLabel("Selected Room:"), gbc);
        roomNoField = new JTextField(15);
        roomNoField.setEditable(false);
        roomNoField.setBackground(new Color(240, 240, 240));
        gbc.gridx = 1;
        bookingFormPanel.add(roomNoField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        bookingFormPanel.add(new JLabel("Duration (Nights):"), gbc);
        nightsField = new JTextField("1");
        gbc.gridx = 1;
        bookingFormPanel.add(nightsField, gbc);

        JButton bookButton = new JButton("Book & Pay");
        bookButton.setBackground(primaryColor);
        bookButton.setForeground(Color.WHITE);
        bookButton.setFocusPainted(false);
        bookButton.setFont(new Font("Arial", Font.BOLD, 12));
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        bookingFormPanel.add(bookButton, gbc);

        tab1Panel.add(bookingFormPanel, BorderLayout.EAST);
        tabbedPane.addTab("Search & Book Rooms", tab1Panel);

        // Tab 2: Active Reservations
        JPanel tab2Panel = new JPanel(new BorderLayout(10, 10));
        tab2Panel.setBackground(bgColor);
        tab2Panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] bookingColumns = {"Booking ID", "Guest Name", "Contact", "Room No", "Nights", "Total Cost ($)", "Payment Status"};
        bookingsTableModel = new DefaultTableModel(bookingColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable bookingsTable = new JTable(bookingsTableModel);
        bookingsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane bookingsScroll = new JScrollPane(bookingsTable);
        tab2Panel.add(bookingsScroll, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setBackground(bgColor);
        JButton cancelButton = new JButton("Cancel Reservation");
        cancelButton.setBackground(new Color(150, 40, 40));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        actionPanel.add(cancelButton);
        tab2Panel.add(actionPanel, BorderLayout.SOUTH);

        tabbedPane.addTab("Manage Reservations", tab2Panel);
        add(tabbedPane, BorderLayout.CENTER);

        // Room Catalog Filter Listener
        categoryFilterBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateTables();
            }
        });

        // Catalog Selection Listener
        catalogTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = catalogTable.getSelectedRow();
            if (selectedRow != -1) {
                int roomNo = (Integer) catalogTableModel.getValueAt(selectedRow, 0);
                String status = (String) catalogTableModel.getValueAt(selectedRow, 3);
                if (status.equals("Available")) {
                    roomNoField.setText(String.valueOf(roomNo));
                } else {
                    roomNoField.setText("");
                }
            }
        });

        // Book Room Button Listener
        bookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processBooking();
            }
        });

        // Cancel Reservation Listener
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelBooking(bookingsTable);
            }
        });
    }

    private void updateTables() {
        String filter = (String) categoryFilterBox.getSelectedItem();
        
        // Redraw Room Catalog Table
        catalogTableModel.setRowCount(0);
        for (Room r : roomsList) {
            if (filter.equals("All") || r.category.equals(filter)) {
                boolean isBooked = isRoomBooked(r.roomNumber);
                catalogTableModel.addRow(new Object[]{
                    r.roomNumber,
                    r.category,
                    String.format("%.2f", r.pricePerNight),
                    isBooked ? "Booked" : "Available"
                });
            }
        }

        // Redraw Bookings Table
        bookingsTableModel.setRowCount(0);
        for (Booking b : bookingsList) {
            bookingsTableModel.addRow(new Object[]{
                b.bookingId,
                b.guestName,
                b.contact,
                b.roomNumber,
                b.nights,
                String.format("%.2f", b.totalCost),
                b.paymentStatus
            });
        }
    }

    private boolean isRoomBooked(int roomNo) {
        for (Booking b : bookingsList) {
            if (b.roomNumber == roomNo) {
                return true;
            }
        }
        return false;
    }

    private Room getRoomByNo(int roomNo) {
        for (Room r : roomsList) {
            if (r.roomNumber == roomNo) {
                return r;
            }
        }
        return null;
    }

    private void processBooking() {
        String guestName = guestNameField.getText().trim();
        String contact = contactField.getText().trim();
        String roomStr = roomNoField.getText().trim();
        String nightsStr = nightsField.getText().trim();

        if (guestName.isEmpty() || contact.isEmpty() || roomStr.isEmpty() || nightsStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select an available room and fill in all fields.", "Booking Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int roomNo = Integer.parseInt(roomStr);
            int nights = Integer.parseInt(nightsStr);

            if (nights <= 0) {
                JOptionPane.showMessageDialog(this, "Stay duration must be at least 1 night.", "Booking Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Room room = getRoomByNo(roomNo);
            double totalCost = room.pricePerNight * nights;

            // Simulate Payment Gateway
            int confirm = JOptionPane.showConfirmDialog(this,
                    String.format("Invoice Details:\nRoom: %d (%s)\nRate: $%.2f/night\nNights: %d\nTotal Cost: $%.2f\n\nProceed to payment simulation?",
                            roomNo, room.category, room.pricePerNight, nights, totalCost),
                    "Payment Processing", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                // Generate Unique Booking ID
                String bookingId = "BK" + (1000 + bookingsList.size() + 1);
                Booking newBooking = new Booking(bookingId, guestName, contact, roomNo, nights, totalCost, "PAID");
                bookingsList.add(newBooking);
                
                saveBookingsToCSV();
                updateTables();
                
                JOptionPane.showMessageDialog(this,
                        String.format("Payment Successful!\nBooking Reference: %s\nThank you for choosing CodeAlpha Hotel.", bookingId),
                        "Booking Confirmed", JOptionPane.INFORMATION_MESSAGE);

                // Clear fields
                guestNameField.setText("");
                contactField.setText("");
                roomNoField.setText("");
                nightsField.setText("1");
                tabbedPane.setSelectedIndex(1); // Switch to list view
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Nights must be a valid integer.", "Booking Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelBooking(JTable bookingsTable) {
        int selectedRow = bookingsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a reservation record to cancel.", "Cancel Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String bookingId = (String) bookingsTableModel.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to cancel booking: " + bookingId + "?\nA refund will be simulated.",
                "Cancel Reservation", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // Find and remove booking
            bookingsList.removeIf(b -> b.bookingId.equals(bookingId));
            saveBookingsToCSV();
            updateTables();
            JOptionPane.showMessageDialog(this, "Booking cancelled successfully. Refund processed.", "Reservation Cancelled", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void saveBookingsToCSV() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(BOOKINGS_FILE))) {
            for (Booking b : bookingsList) {
                String name = b.guestName.replace(",", "\\,");
                writer.println(b.bookingId + "," + name + "," + b.contact + "," + b.roomNumber + "," + b.nights + "," + b.totalCost + "," + b.paymentStatus);
            }
        } catch (IOException e) {
            System.err.println("Error saving bookings: " + e.getMessage());
        }
    }

    private void loadBookingsFromCSV() {
        File file = new File(BOOKINGS_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 7) {
                    String id = parts[0];
                    String name = parts[1].replace("\\,", ",");
                    String contact = parts[2];
                    int roomNo = Integer.parseInt(parts[3]);
                    int nights = Integer.parseInt(parts[4]);
                    double cost = Double.parseDouble(parts[5]);
                    String payment = parts[6];
                    
                    bookingsList.add(new Booking(id, name, contact, roomNo, nights, cost, payment));
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading bookings: " + e.getMessage());
        }
    }

    // Room Class model
    private static class Room {
        int roomNumber;
        String category;
        double pricePerNight;

        public Room(int roomNumber, String category, double pricePerNight) {
            this.roomNumber = roomNumber;
            this.category = category;
            this.pricePerNight = pricePerNight;
        }
    }

    // Booking Class model
    private static class Booking {
        String bookingId;
        String guestName;
        String contact;
        int roomNumber;
        int nights;
        double totalCost;
        String paymentStatus;

        public Booking(String bookingId, String guestName, String contact, int roomNumber, int nights, double totalCost, String paymentStatus) {
            this.bookingId = bookingId;
            this.guestName = guestName;
            this.contact = contact;
            this.roomNumber = roomNumber;
            this.nights = nights;
            this.totalCost = totalCost;
            this.paymentStatus = paymentStatus;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new HotelReservationApp().setVisible(true);
            }
        });
    }
}
