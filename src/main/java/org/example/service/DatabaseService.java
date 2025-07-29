package org.example.service;

import org.example.model.GrantData;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseService {
    private static final String DB_URL = "jdbc:sqlite:grants.db";

    public DatabaseService() {
        createNewTable();
    }

    private Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public void createNewTable() {
        String sql = "CREATE TABLE IF NOT EXISTS grants (\n"
                + "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "    number INTEGER NOT NULL,\n"
                + "    iin TEXT NOT NULL,\n"
                + "    full_name TEXT NOT NULL,\n"
                + "    score INTEGER NOT NULL,\n"
                + "    university_code TEXT,\n"
                + "    quota TEXT,\n"
                + "    specialty_name TEXT\n"
                + ");";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void saveGrants(List<GrantData> grants) {
        // Сначала очищаем старые данные
        String deleteSql = "DELETE FROM grants";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(deleteSql);
        } catch (SQLException e) {
            System.out.println("Ошибка при очистке таблицы: " + e.getMessage());
        }

        // Затем вставляем новые
        String insertSql = "INSERT INTO grants(number, iin, full_name, score, university_code, quota, specialty_name) VALUES(?,?,?,?,?,?,?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            for (GrantData grant : grants) {
                pstmt.setInt(1, grant.getNumber());
                pstmt.setString(2, grant.getIin());
                pstmt.setString(3, grant.getFullName());
                pstmt.setInt(4, grant.getScore());
                pstmt.setString(5, grant.getUniversityCode());
                pstmt.setString(6, grant.getQuota());
                pstmt.setString(7, grant.getSpecialtyName());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        } catch (SQLException e) {
            System.out.println("Ошибка при сохранении данных: " + e.getMessage());
        }
    }

    public List<GrantData> loadGrants(DataService dataService) {
        String sql = "SELECT number, iin, full_name, score, university_code, quota, specialty_name FROM grants";
        List<GrantData> grants = new ArrayList<>();
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String universityCode = rs.getString("university_code");
                String universityName = dataService.getUniversityNameByCode(universityCode);

                GrantData grant = new GrantData(
                        rs.getInt("number"),
                        rs.getString("iin"),
                        rs.getString("full_name"),
                        rs.getInt("score"),
                        universityCode,
                        universityName,
                        rs.getString("quota"),
                        rs.getString("specialty_name")
                );
                grants.add(grant);
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при загрузке данных: " + e.getMessage());
        }
        return grants;
    }

    public boolean hasData() {
        String sql = "SELECT COUNT(*) FROM grants";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }
}
