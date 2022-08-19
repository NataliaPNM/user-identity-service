package com.example.model;

import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@ToString
@Entity
@Table(name = "address")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {
  @Id
  @EqualsAndHashCode.Exclude
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
  private UUID addressId;

  private String country;
  private String city;
  private String street;
  private Integer postalCode;
  private Integer house;
  private Integer flat;
  private Integer block;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    Address address = (Address) o;
    return addressId != null && Objects.equals(addressId, address.addressId);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
