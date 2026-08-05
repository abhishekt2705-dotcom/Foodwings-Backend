package com.foodwings.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A delivery address that belongs to a customer. A customer may have many addresses.
 */
@Entity
@Table(name = "addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address extends BaseEntity {

    @Column(length = 40)
    private String label;

    @Column(nullable = false)
    private String line1;

    private String line2;

    @Column(nullable = false, length = 60)
    private String city;

    @Column(length = 60)
    private String state;

    @Column(nullable = false, length = 10)
    private String pincode;

    @Column(length = 15)
    private String phone;

    @Column(nullable = false)
    @Builder.Default
    private boolean defaultAddress = false;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
