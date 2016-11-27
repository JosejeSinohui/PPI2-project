package ppi2project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

// this class handles the interactions with the database
public class SQLiteConn {

    //connection
    private Connection connect() {
        String url = "jdbc:sqlite:database.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    //insert into the database
    public void insert(String name, String team, double money) {
        String sql = "INSERT INTO betters(name, team, money) VALUES(?,?,?)";

        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, team);
            pstmt.setDouble(3, money);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // return all elements from the database in a resultset
    public ResultSet selectAll() throws SQLException {
        String sql = "SELECT * FROM betters";
        Connection conn = this.connect();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        return rs;
    }

    // trae solo los elementos de 
    public String getWinners(String winner) throws SQLException {
        String sql = "SELECT * "
                + "FROM betters WHERE team LIKE '" + winner + "'";
        Connection conn = this.connect();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();

        // global total money
        double totalMoney = this.totalAmount();

        // team total money
        double teamMoney = this.teamAmount(winner);

          
        String winners = "";
        while (rs.next()) {
            winners += rs.getInt("id") + "    "
                    + rs.getString("name") + "    "
                    + rs.getDouble("money") * totalMoney / teamMoney + "\n";
        }

        return winners;

    }

    // Team total bettings used to calculate individual contribution
    public double teamAmount(String winner) throws SQLException {
        String sql = "SELECT * "
                + "FROM betters WHERE team LIKE '" + winner + "'";
        Connection conn = this.connect();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();

        // team total money
        double teamTotal = 0;
        while (rs.next()) {
            teamTotal += rs.getDouble("money");
        }

        return teamTotal;
    }

    // Total amount of money from the betters
    public double totalAmount() throws SQLException {

        ResultSet rs = this.selectAll();
        double total = 0;
        while (rs.next()) {
            total += rs.getDouble("money");
        }
        return total;
    }

    //updates a value in the database
    public void update(int id, String name, String team, double money) {
        String sql = "UPDATE betters SET name = ? , "
                + "team = ? , "
                + "money = ?"
                + "WHERE id = ?";

        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setString(2, team);
            pstmt.setDouble(3, money);
            pstmt.setInt(4, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    //deletes an element in the databases
    public void delete(int id) {
        String sql = "DELETE FROM betters WHERE id = ?";

        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void deleteAll() {
        String sql = "delete from betters";
        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // returns a Table model based on the elements of the database
    public static DefaultTableModel buildTableModel(ResultSet rs)
            throws SQLException {

        ResultSetMetaData metaData = rs.getMetaData();

        // names of columns
        Vector<String> columnNames = new Vector<String>();
        int columnCount = metaData.getColumnCount();
        for (int column = 1; column <= columnCount; column++) {
            columnNames.add(metaData.getColumnName(column));
        }

        // data of the table
        Vector<Vector<Object>> data = new Vector<Vector<Object>>();
        while (rs.next()) {
            Vector<Object> vector = new Vector<Object>();
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                vector.add(rs.getObject(columnIndex));
            }
            data.add(vector);
        }

        // make elements non editable
        return new DefaultTableModel(data, columnNames) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

    }

}
