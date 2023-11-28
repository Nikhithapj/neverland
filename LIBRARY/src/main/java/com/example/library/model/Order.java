package com.example.library.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="orders")

public class Order{
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "order_id")

    private Long id;
    private Date orderDate;
    private Date DeliveryDate;
    private double totalPrice;
    private double shippingFee;
    private String orderStatus;
    private String paymentMethod;
    private String paymentStatus;
    private  boolean isAccept;
    private int quantity;
    @Column(nullable=true)
    private Double discountprice;
//this is added for the save method for saving order while create order in controller



    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="address_id",referencedColumnName = "address_id")
    private Address shippingAddress;

    @ManyToOne(fetch=FetchType.EAGER,cascade = {CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH,CascadeType.DETACH})
    @JoinColumn(name="customer_id",referencedColumnName = "customer_id")
    private Customer customer;
//this is added for the save method for saving order while create order in controller

    @OneToMany(mappedBy = "order",cascade=CascadeType.ALL)
    private List<OrderDetail> orderDetailsList;



    @Column(nullable=true)
    private LocalDateTime confirmedDateTime;

    @Column(nullable = true)
    private LocalDateTime shippedDateTime;

    @Column(nullable=true)
    private LocalDateTime deliveredDateTime;







        }