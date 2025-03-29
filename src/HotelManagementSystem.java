import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class HotelManagementSystem {
    private static final String url = "jdbc:mysql://localhost:3306/hotel_db";
    private  static final String username = "root";
    private static final String password = "Nope@1234";

    public static void main(String[] args) throws  ClassNotFoundException , SQLException{
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch (ClassNotFoundException e){
            System.out.println(e.getMessage());
        }

        try{
            Connection connection = DriverManager.getConnection(url, username , password);
            while(true){
                System.out.println();
                System.out.println("Hotel Management System ");
                Scanner scan = new Scanner(System.in);
                System.out.println("1. Reserve Room ");
                System.out.println("2. View Reservation");
                System.out.println("3. Get Room Number");
                System.out.println("4. Update Reservation");
                System.out.println("5. Delete Reservation");
                System.out.println("0. Exit");
                System.out.println("Choose an option :");
                int choice = scan.nextInt();
                switch (choice){
                    case 1:
                        reserveRoom(connection, scan);
                        break;
                    case 2:
                        viewReservation(connection);
                        break;
                    case  3:
                        getRoomNumber(connection, scan);
                        break;
                    case 4:
                        updateReservation(connection, scan);
                        break;
                    case 5:
                        deleteReservation(connection, scan);
                        break;
                    case 0:
                        exit();
                        scan.close();
                        return;

                    default:
                        System.out.println("Try again !! , Invalid choice.");
                }


            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
        catch (InterruptedException e){
            throw new  RuntimeException(e);
        }
    }

    private static void updateReservation(Connection connection, Scanner scan) {
        try {
            System.out.print("Enter reservation ID to update: ");
            int reservationId = scan.nextInt();
            scan.nextLine(); // Consume the newline character

            if (!reservationExists(connection, reservationId)) {
                System.out.println("Reservation not found for the given ID.");
                return;
            }

            System.out.print("Enter new guest name: ");
            String newGuestName = scan.nextLine();
            System.out.print("Enter new room number: ");
            int newRoomNumber = scan.nextInt();
            System.out.print("Enter new contact number: ");
            String newContactNumber = scan.next();

            String sql = "UPDATE reservations SET guest_name = '" + newGuestName + "', " +
                    "room_number = " + newRoomNumber + ", " +
                    "contact_number = '" + newContactNumber + "' " +
                    "WHERE reservation_id = " + reservationId;

            try (Statement statement = connection.createStatement()) {
                int affectedRows = statement.executeUpdate(sql);

                if (affectedRows > 0) {
                    System.out.println("Reservation updated successfully!");
                } else {
                    System.out.println("Reservation update failed.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static boolean reservationExists(Connection connection, int reservationId) {
        try {
            String sql = "SELECT reservation_id FROM reservations WHERE reservation_id = " + reservationId;

            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(sql)) {

                return resultSet.next(); // If there's a result, the reservation exists
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Handle database errors as needed
        }
    }

    private static void getRoomNumber(Connection connection, Scanner scan) {
        try {
            System.out.print("Enter reservation ID: ");
            int reservationId = scan.nextInt();
            System.out.print("Enter guest name: ");
            String guestName = scan.next();

            String sql = "SELECT room_number FROM reservations " +
                    "WHERE reservation_id = " + reservationId +
                    " AND guest_name = '" + guestName + "'";

            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(sql)) {

                if (resultSet.next()) {
                    int roomNumber = resultSet.getInt("room_number");
                    System.out.println("Room number for Reservation ID " + reservationId +
                            " and Guest " + guestName + " is: " + roomNumber);
                } else {
                    System.out.println("Reservation not found for the given ID and guest name.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void viewReservation(Connection connection)  throws SQLException{
        String query = "SELECT reservation_id, guest_name, room_number, contact_number, reservation_date FROM reservations";
        try(Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query)){
            System.out.println("Current Reservations:");
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
            System.out.println("| Reservation ID | Guest           | Room Number   | Contact Number      | Reservation Date        |");
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
            while(rs.next()){
                int id = rs.getInt("reservation_id");
                String name = rs.getString("guest_name");
                int room = rs.getInt("room_number");
                String contact = rs.getString("contact_number");
                String reservationTime = rs.getString("reservation_date");
                System.out.printf("| %-14d | %-15s | %-13d | %-20s | %-19s   |\n", + id, name, room, contact, reservationTime);
            }
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
        }
    }

    private static void deleteReservation(Connection connection, Scanner scan) {
        try {
            System.out.print("Enter reservation ID to delete: ");
            int reservationId = scan.nextInt();

            if (!reservationExists(connection, reservationId)) {
                System.out.println("Reservation not found for the given ID.");
                return;
            }

            String sql = "DELETE FROM reservations WHERE reservation_id = " + reservationId;

            try (Statement statement = connection.createStatement()) {
                int affectedRows = statement.executeUpdate(sql);

                if (affectedRows > 0) {
                    System.out.println("Reservation deleted successfully!");
                } else {
                    System.out.println("Reservation deletion failed.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void reserveRoom(Connection connection, Scanner scan) {
        try {
            System.out.println("Enter guest name :");
            String name = scan.next();
            scan.nextLine();
            System.out.println("Enter room number :");
            int roomNumber = scan.nextInt();
            System.out.println("Enter contact number :");
            String contactNumber = scan.next();

            String query = "INSERT INTO reservations(guest_name, room_number, contact_number)" +
                    " VALUES(' " + name + " ', " + roomNumber + ", ' " + contactNumber + " '  )";

            try (Statement statement = connection.createStatement()){
                int rows = statement.executeUpdate(query);
                if (rows > 0) {
                    System.out.println("Reservation Successful");
                } else {
                    System.out.println("Reservation Failed");
                }
            }
            } catch (SQLException e) {
                e.printStackTrace();
            }
    }
    public static void exit() throws InterruptedException {
        System.out.print("Exiting System");
        int i = 5;
        while(i!=0){
            System.out.print(".");
            Thread.sleep(100);
            i--;
        }
        System.out.println();
        System.out.println("ThankYou For Using Hotel Reservation System!!!");
    }

}
