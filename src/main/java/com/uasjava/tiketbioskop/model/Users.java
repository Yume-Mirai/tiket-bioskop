package com.uasjava.tiketbioskop.model;
import java.time.LocalDate;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, length = 100)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 12)
    private String nomor;

    @Column(nullable = false)
    private LocalDate tanggal_lahir;

    @Column(name = "status")
    private Boolean status;

    @Column(name = "created_date", columnDefinition = "DATE")
    private Date createdDate;

    @Column(name = "update_date", columnDefinition = "DATE")
    private LocalDate updateDate;

    // @Enumerated(EnumType.STRING)
    // @Column(nullable = false)
    // private Role role = Role.USER;

    // public enum Role {
    //     USER, ADMIN
    // }
}

