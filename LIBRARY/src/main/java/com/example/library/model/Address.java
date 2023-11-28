package com.example.library.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name="addresses")
public class Address implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="address_id")
    private Long id;

    private String addressLine1;
    private String addressLine2;
    private String city;
    private String pincode;
    private String district;
    private String state;
    private String Country;
    private boolean is_default;

    @OneToMany(mappedBy="shippingAddress")
    private List<Order> order;

    @ManyToOne(fetch=FetchType.EAGER,cascade={CascadeType.MERGE,CascadeType.PERSIST,CascadeType.DETACH,CascadeType.REFRESH})
    @JoinColumn(name="customer_id",referencedColumnName="customer_id")
    private Customer customer;

    public void setAddressLine2(String addressLine2) {
    }
}
