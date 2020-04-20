package com.myretail.challenge.repositories;

import com.myretail.challenge.models.Price;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.Set;
import java.util.UUID;

public interface PriceRepo extends CassandraRepository<Price, UUID> {

    @AllowFiltering
    Set<Price> findByProductId(int productId);
}
