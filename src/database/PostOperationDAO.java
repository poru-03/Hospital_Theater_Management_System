package database;

import model.PostOperation;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PostOperationDAO {

    public PostOperation getPostOpByBookingId(String bookingId) {
        String sql = "SELECT * FROM post_operations WHERE booking_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, bookingId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new PostOperation(
                    rs.getString("pop_id"),
                    rs.getString("booking_id"),
                    rs.getString("prescription_events"),
                    rs.getString("side_effects"),
                    rs.getString("medicine"),
                    rs.getDate("next_date")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
