package com.TakeHomeTest.restApi.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "service_layanan")
public class ServiceLayanan {
    @Id
    private String serviceCode;

    private String serviceName;
    private String serviceIcon;
    private BigDecimal serviceTariff;
}
