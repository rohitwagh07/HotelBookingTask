import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Date;

//Driver class (main method)
public class HotelBookingSystem {
    static List<Guest> bookings = new ArrayList<>();
    static final String FILE_NAME = "bookings.txt";
    static final int RATE_PER_DAY = 1000;

    public static void main(String[] args) {
        loadBookingsFromFile();
        Scanner sc = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\n--- Hotel Booking System ---");
            System.out.println("1. Book Room");
            System.out.println("2. View All Bookings");
            System.out.println("3. Checkout Guest");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");
            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    bookRoom(sc);
                    break;
                case 2:
                    viewBookings();
                    break;
                case 3:
                    checkoutGuest(sc);
                    break;
                case 4:
                    saveBookingsToFile();
                    System.out.println("Exiting program.");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } while (choice != 4);
    }

    static void bookRoom(Scanner sc) {
        try {
            System.out.print("Enter guest name: ");
            String name = sc.nextLine();
            System.out.print("Enter contact number: ");
            String contact = sc.nextLine();
            System.out.print("Enter ID proof: ");
            String idProof = sc.nextLine();
            int roomNumber = getAvailableRoom();

            if (roomNumber == -1) {
                System.out.println("No rooms available.");
                return;
            }

            System.out.print("Enter check-in date (dd-MM-yyyy): ");
            Date checkIn = new SimpleDateFormat("dd-MM-yyyy").parse(sc.nextLine());
            System.out.print("Enter expected check-out date (dd-MM-yyyy): ");
            Date checkOut = new SimpleDateFormat("dd-MM-yyyy").parse(sc.nextLine());

            bookings.add(new Guest(name, contact, idProof, roomNumber, checkIn, checkOut));
            System.out.println("Room " + roomNumber + " booked successfully!");

        } catch (Exception e) {
            System.out.println("Error booking room: " + e.getMessage());
        }
    }

    static void viewBookings() {
        if (bookings.isEmpty()) {
            System.out.println("No current bookings.");
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        for (Guest g : bookings) {
            System.out.println("Room " + g.roomNumber + ": " + g.name + ", Contact: " + g.contact +
                               ", ID: " + g.idProof + ", Check-in: " + sdf.format(g.checkIn) +
                               ", Check-out: " + sdf.format(g.checkOut));
        }
    }

    static void checkoutGuest(Scanner sc) {
        System.out.print("Enter room number to checkout: ");
        int roomNumber = sc.nextInt();
        sc.nextLine();

        Guest guestToCheckout = null;
        for (Guest g : bookings) {
            if (g.roomNumber == roomNumber) {
                guestToCheckout = g;
                break;
            }
        }

        if (guestToCheckout != null) {
            long diff = (guestToCheckout.checkOut.getTime() - guestToCheckout.checkIn.getTime()) / (1000 * 60 * 60 * 24);
            diff = diff == 0 ? 1 : diff;
            int bill = (int) diff * RATE_PER_DAY;
            System.out.println("Guest checked out. Total bill: ₹" + bill);
            bookings.remove(guestToCheckout);
        } else {
            System.out.println("Room not found.");
        }
    }

    static int getAvailableRoom() {
        for (int i = 101; i <= 110; i++) {
            boolean taken = false;
            for (Guest g : bookings) {
                if (g.roomNumber == i) {
                    taken = true;
                    break;
                }
            }
            if (!taken) return i;
        }
        return -1;
    }

    static void saveBookingsToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Guest g : bookings) {
                pw.println(g.toFileString());
            }
        } catch (IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
    }

    static void loadBookingsFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                bookings.add(Guest.fromFileString(line));
            }
        } catch (FileNotFoundException e) {
        } catch (Exception e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }
}
