package com.TakeHomeTest.restApi.service;

import com.TakeHomeTest.restApi.dto.UserTransactionRequest;
import com.TakeHomeTest.restApi.entity.ServiceLayanan;
import com.TakeHomeTest.restApi.repository.ServiceLayananRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


@Service
public class LayananService {

    @Autowired
    private ServiceLayananRepository serviceLayananRepository;

    @Autowired
    private DataSource dataSource;



    public ServiceLayanan checkLayananExist(UserTransactionRequest transactionRequest) {

        ServiceLayanan serviceLayanan = serviceLayananRepository.findById(transactionRequest.getServiceCode())
                .orElseThrow(() -> new IllegalArgumentException("Layanan tidak ditemukan."));

        return serviceLayanan;
    }

    public List<ServiceLayanan> getAllServices() {

        List<ServiceLayanan> services = serviceLayananRepository.findAll();

        return services;
    }

    public BigDecimal getLayananPrice(String email) {
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

}
