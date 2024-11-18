package com.TakeHomeTest.restApi.service;

import com.TakeHomeTest.restApi.dto.GeneralResponse;
import com.TakeHomeTest.restApi.dto.UserRegistrationRequest;
import com.TakeHomeTest.restApi.entity.ServiceLayanan;
import com.TakeHomeTest.restApi.entity.Transaction;
import com.TakeHomeTest.restApi.entity.User;
import com.TakeHomeTest.restApi.repository.ServiceLayananRepository;
import com.TakeHomeTest.restApi.repository.TransactionRepository;
import com.TakeHomeTest.restApi.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private ServiceLayananRepository serviceLayananRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final DataSource dataSource;


    @Autowired
    public UserService(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    public void checkDuplicateEmail(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, email);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next() && resultSet.getInt(1) > 0) {
                throw new IllegalArgumentException("Email sudah terdaftar");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error", e);
        }
    }



public User register(UserRegistrationRequest request) {
    String sql = "INSERT INTO users (email, password, first_name, last_name, balance) VALUES (?, ?, ?, ?, ?)";
    try (Connection connection = dataSource.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        preparedStatement.setString(1, request.getEmail());
        preparedStatement.setString(2, encodedPassword);
        preparedStatement.setString(3, request.getFirstName());
        preparedStatement.setString(4, request.getLastName());
        preparedStatement.setBigDecimal(5, BigDecimal.ZERO);

        preparedStatement.executeUpdate();

        return null;
    } catch (SQLException e) {
        throw new RuntimeException("Database error", e);
    }
}



    public BigDecimal getBalance(String email) {
        String sql = "SELECT balance FROM users WHERE email = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, email);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBigDecimal("balance");
            } else {
                throw new IllegalArgumentException("User tidak ditemukan");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error", e);
        }
    }



    @Transactional
    public BigDecimal topUpBalance(String email, BigDecimal amount) {
        String sqlUser = "SELECT id, balance FROM users WHERE email = ?";
        String sqlUpdateBalance = "UPDATE users SET balance = ? WHERE id = ?";
        String sqlInsertTransaction = "INSERT INTO transactions (user_id, amount, transaction_type, created_at) VALUES (?, ?, ?, ?)";

        try (Connection connection = dataSource.getConnection()) {

            try (PreparedStatement preparedStatement = connection.prepareStatement(sqlUser)) {
                preparedStatement.setString(1, email);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    Long userId = resultSet.getLong("id");
                    BigDecimal balance = resultSet.getBigDecimal("balance");
                    BigDecimal newBalance = balance.add(amount);


                    try (PreparedStatement updateBalanceStmt = connection.prepareStatement(sqlUpdateBalance)) {
                        updateBalanceStmt.setBigDecimal(1, newBalance);
                        updateBalanceStmt.setLong(2, userId);
                        updateBalanceStmt.executeUpdate();
                    }


                    try (PreparedStatement insertTransactionStmt = connection.prepareStatement(sqlInsertTransaction)) {
                        insertTransactionStmt.setLong(1, userId);
                        insertTransactionStmt.setBigDecimal(2, amount);
                        insertTransactionStmt.setString(3, "TOPUP");
                        insertTransactionStmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
                        insertTransactionStmt.executeUpdate();
                    }

                    return newBalance;
                } else {
                    throw new IllegalArgumentException("User tidak ditemukan");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error", e);
        }
    }



    @Transactional
    public ResponseEntity<GeneralResponse<Map<String, Object>>> createTransaction(String email,  String serviceId) {

        String sqlUser = "SELECT id, balance FROM users WHERE email = ?";
        String sqlService = "SELECT service_code, service_name, service_tariff  FROM service_layanan WHERE service_code = ?";
        String sqlInsertTransaction = "INSERT INTO transactions (user_id, amount, transaction_type, service_id, invoice, created_at) VALUES (?, ?, ?, ?, ?, ?)";

        String invoiceNumber = "INV-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-" + UUID.randomUUID().toString().substring(0, 3).toUpperCase();

        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sqlUser)) {
                preparedStatement.setString(1, email);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    Long userId = resultSet.getLong("id");
                    BigDecimal balance = resultSet.getBigDecimal("balance");

                    String serviceCode = "";
                    String serviceName = "";
                    BigDecimal amount = BigDecimal.ZERO;

                    try (PreparedStatement serviceStmt = connection.prepareStatement(sqlService)) {
                        serviceStmt.setString(1, serviceId);
                        ResultSet serviceResultSet = serviceStmt.executeQuery();
                        if (serviceResultSet.next()) {
                            serviceCode = serviceResultSet.getString("service_code");
                            serviceName = serviceResultSet.getString("service_name");
                            amount = serviceResultSet.getBigDecimal("service_tariff");
                        } else {
                            throw new IllegalArgumentException("Layanan tidak ditemukan.");
                        }
                    }

                    if (balance.compareTo(amount) < 0) {
                        throw new IllegalArgumentException("Saldo tidak cukup untuk melakukan transaksi.");
                    }

                    try (PreparedStatement insertTransactionStmt = connection.prepareStatement(sqlInsertTransaction)) {
                        insertTransactionStmt.setLong(1, userId);
                        insertTransactionStmt.setBigDecimal(2, amount);
                        insertTransactionStmt.setString(3, "PAYMENT");
                        insertTransactionStmt.setString(4, serviceId);
                        insertTransactionStmt.setString(5, invoiceNumber);
                        insertTransactionStmt.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
                        insertTransactionStmt.executeUpdate();
                    }

                    BigDecimal newBalance = balance.subtract(amount);
                    String sqlUpdateBalance = "UPDATE users SET balance = ? WHERE id = ?";
                    try (PreparedStatement updateBalanceStmt = connection.prepareStatement(sqlUpdateBalance)) {
                        updateBalanceStmt.setBigDecimal(1, newBalance);
                        updateBalanceStmt.setLong(2, userId);
                        updateBalanceStmt.executeUpdate();
                    }

                    Map<String, Object> responseData = new HashMap<>();
                    responseData.put("invoice_number", invoiceNumber);
                    responseData.put("service_code", serviceCode);
                    responseData.put("service_name", serviceName);
                    responseData.put("transaction_type", "PAYMENT");
                    responseData.put("total_amount", amount);
                    responseData.put("created_on", LocalDateTime.now());

                    return ResponseEntity.ok(new GeneralResponse<>(0, "Transaksi berhasil", responseData));

                } else {
                    throw new IllegalArgumentException("Pengguna tidak ditemukan");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error", e);
        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            return ResponseEntity.badRequest().body(new GeneralResponse<>(102, e.getMessage(), errorResponse));
        }
    }



}
