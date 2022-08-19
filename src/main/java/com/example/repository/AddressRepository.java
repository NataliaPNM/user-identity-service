package com.example.repository;

import com.example.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AddressRepository extends JpaRepository<Address, UUID> {

  Optional<Address> findAddressByCountryAndCityAndStreetAndPostalCodeAndHouseAndFlatAndBlock(
      String country,
      String city,
      String street,
      Integer postalCode,
      Integer house,
      Integer flat,
      Integer block);
}
