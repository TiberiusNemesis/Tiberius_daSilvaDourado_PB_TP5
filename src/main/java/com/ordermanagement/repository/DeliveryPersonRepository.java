package com.ordermanagement.repository;

import com.ordermanagement.model.DeliveryPerson;
import java.util.List;

public interface DeliveryPersonRepository extends UserRepository<DeliveryPerson> {
    List<DeliveryPerson> findByAvailable(boolean available);
}