package Main;

import java.sql.*;
import java.util.*;

public class config {

    // Connection Method to SQLite
    public static Connection connectDB() {
        Connection con = null;
        try {
            Class.forName("org.sqlite.JDBC"); // Load the SQLite JDBC driver
            con = DriverManager.getConnection("jdbc:sqlite:bcdis_db.db"); // Establish connection
        } catch (Exception e) {
            System.out.println("Connection Failed: " + e);
        }
        return con;
    }

    // Method to add a record dynamically
    public void addRecord(String sql, Object... values) {
        try (Connection conn = this.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            setPreparedStatementValues(pstmt, values); // Set dynamic values
            pstmt.executeUpdate();
            System.out.println("Record added successfully!");
        } catch (SQLException e) {
            System.out.println("Error adding record: " + e.getMessage());
        }
    }

    // Dynamic view method to display records from any table
   // Dynamic view method to display records from any table
public void viewRecords(String sqlQuery, String[] columnHeaders, String[] columnNames, Object... params) {
    // Check that columnHeaders and columnNames arrays are the same length
    if (columnHeaders.length != columnNames.length) {
        System.out.println("Error: Mismatch between column headers and column names.");
        return;
    }

    try (Connection conn = this.connectDB();
         PreparedStatement pstmt = conn.prepareStatement(sqlQuery)) {

        // Set dynamic parameters if any
        setPreparedStatementValues(pstmt, params); // Bind values dynamically

        // Execute the query
        ResultSet rs = pstmt.executeQuery();

        // Print the headers dynamically
        StringBuilder headerLine = new StringBuilder();
        headerLine.append("---------------------------------------------------------------\n| ");
        for (String header : columnHeaders) {
            headerLine.append(String.format("%-30s | ", header)); // Adjust formatting
        }
        headerLine.append("\n---------------------------------------------------------------");

        System.out.println(headerLine.toString());

        // Print the rows dynamically based on the provided column names
        while (rs.next()) {
            StringBuilder row = new StringBuilder("| ");
            for (String colName : columnNames) {
                String value = rs.getString(colName);
                row.append(String.format("%-30s | ", value != null ? value : "")); // Adjust formatting
            }
            System.out.println(row.toString());
        }
        System.out.println("---------------------------------------------------------------");

    } catch (SQLException e) {
        System.out.println("Error retrieving records: " + e.getMessage());
    }
}

    // Method to update a record dynamically
    public void updateRecord(String sql, Object... values) {
        try (Connection conn = this.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            setPreparedStatementValues(pstmt, values); // Set dynamic values
            pstmt.executeUpdate();
            System.out.println("Record updated successfully!");
        } catch (SQLException e) {
            System.out.println("Error updating record: " + e.getMessage());
        }
    }

    // Method to delete a record dynamically
    public void deleteRecord(String sql, Object... values) {
        try (Connection conn = this.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            setPreparedStatementValues(pstmt, values); // Set dynamic values
            pstmt.executeUpdate();
            System.out.println("Record deleted successfully!");
        } catch (SQLException e) {
            System.out.println("Error deleting record: " + e.getMessage());
        }
    }

    // Helper Method for Setting PreparedStatement Values
    private void setPreparedStatementValues(PreparedStatement pstmt, Object... values) throws SQLException {
        for (int i = 0; i < values.length; i++) {
            if (values[i] instanceof Integer) {
                pstmt.setInt(i + 1, (Integer) values[i]);
            } else if (values[i] instanceof Double) {
                pstmt.setDouble(i + 1, (Double) values[i]);
            } else if (values[i] instanceof Float) {
                pstmt.setFloat(i + 1, (Float) values[i]);
            } else if (values[i] instanceof Long) {
                pstmt.setLong(i + 1, (Long) values[i]);
            } else if (values[i] instanceof Boolean) {
                pstmt.setBoolean(i + 1, (Boolean) values[i]);
            } else if (values[i] instanceof java.util.Date) {
                pstmt.setDate(i + 1, new java.sql.Date(((java.util.Date) values[i]).getTime()));
            } else if (values[i] instanceof java.sql.Date) {
                pstmt.setDate(i + 1, (java.sql.Date) values[i]);
            } else if (values[i] instanceof java.sql.Timestamp) {
                pstmt.setTimestamp(i + 1, (java.sql.Timestamp) values[i]);
            } else {
                pstmt.setString(i + 1, values[i].toString()); // Default to String
            }
        }
    }

    // Method to get a single value (e.g., COUNT) dynamically
    public double getSingleValue(String sql, Object... params) {
        double result = 0.0;
        try (Connection conn = connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            setPreparedStatementValues(pstmt, params); // Set dynamic values
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                result = rs.getDouble(1); // Get the first column value
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving single value: " + e.getMessage());
        }
        return result;
    }

    // Method to get a count value dynamically
    public int getCount(String sql, Object... params) {
        int count = 0;
        try (Connection conn = connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            setPreparedStatementValues(pstmt, params); // Set dynamic values
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1); // Get the count value
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving count: " + e.getMessage());
        }
        return count;
    }

    
    // Method to get a single record dynamically (e.g., citizen's name)
public Object[] getSingleRecord(String sql, Object... params) {
    Object[] result = null;
    try (Connection conn = connectDB();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        setPreparedStatementValues(pstmt, params); // Set dynamic values
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            result = new Object[rs.getMetaData().getColumnCount()];
            for (int i = 0; i < result.length; i++) {
                result[i] = rs.getObject(i + 1);
            }
        }

    } catch (SQLException e) {
        System.out.println("Error retrieving record: " + e.getMessage());
    }
    return result;
}

   
}
