package com.example.library.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@Entity
@Table(name = "customers", uniqueConstraints = @UniqueConstraint(columnNames = "username"))
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String phoneNumber;
    private  boolean is_blocked = false;
    //forgot pass
    @Column(name="reset_password_token")
    private String resetPasswordToken;
    @Column(name = "update_on")

    private Date updateOn;
    //    @OneToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "name", referencedColumnName = "id")
//    private City city;
//    private String country;

    //Actually evde thazhe cascade type.all ann
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinTable(name = "customer_role", joinColumns = @JoinColumn(name = "customer_id", referencedColumnName = "customer_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "role_id"))
    private Collection<Role> roles;
@ToString.Exclude
@OneToMany(mappedBy = "customer",cascade=CascadeType.ALL)
private  List<Order>orders;


  @ToString.Exclude
  @OneToOne(mappedBy = "customer")
  private Wallet wallet;

    public Customer() {

    }
    private boolean is_activated;

    @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL)
    private ShoppingCart cart;
//@OneToOne(mappedBy="customer",cascade = CascadeType.ALL)
//private ShoppingCart cart;
//    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
//    private List<Order> orders;
//
//    public Customer() {
//        this.country = "VN";
//        this.cart = new ShoppingCart();
//        this.orders = new ArrayList<>();
//    }

    @Override
    public String toString()
    {
        return "Customer{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
//                ", city=" + city.getName() +
//                ", country='" + country + '\'' +
                ", roles=" + roles +
//                ", cart=" + cart.getId() +
//                ", orders=" + orders.size() +
                ", is_activated=" + is_activated +

                '}';
    }


    @ToString.Exclude
    @OneToMany(mappedBy = "customer",cascade=CascadeType.ALL)
    private List<Address>address;


    @ToString.Exclude
private String referralCode;


}