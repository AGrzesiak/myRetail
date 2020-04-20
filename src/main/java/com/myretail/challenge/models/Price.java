package com.myretail.challenge.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(value = "prices")
public class Price {

    @PrimaryKey
    private UUID id = UUID.randomUUID();

    private BigDecimal value;

    @Column("currency_code")
    private String currencyCode;

    @Column("product_id")
    private int productId;

}
