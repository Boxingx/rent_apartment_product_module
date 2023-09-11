package com.example.rent_apartment_product_module.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "apartment_info")
@Data
public class ApartmentEntity {

    @Id
    @SequenceGenerator(name = "apartment_infoSequence", sequenceName = "apartment_info_sequence", allocationSize = 1, initialValue = 3)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "apartment_infoSequence")
    @Column(name = "id")
    private Long id;

    @Column(name = "rooms_count")
    private String roomsCount;

    @Column(name = "average_rating")
    private String averageRating;

    @Column(name = "price")
    private String price;

    @Column(name = "status")
    private String status;

    @Column(name = "registration_date")
    private String registrationDate;

    @OneToOne(mappedBy = "apartmentEntity")
    private AddressEntity addressEntity;


}
