package com.example.library.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Collection;

@Getter@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "admin")
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="admin_id")
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    //    @Lob
//    @Column(columnDefinition = "MEDIUMBLOB")
//    private String image;
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "admins_roles",joinColumns = @JoinColumn(name = "admin_id", referencedColumnName = "admin_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id",referencedColumnName = "role_id"))
    private Collection<Role> roles;
}
