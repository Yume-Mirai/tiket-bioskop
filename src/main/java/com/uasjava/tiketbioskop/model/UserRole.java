package com.uasjava.tiketbioskop.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity 
@Table(name = "user_role")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder


public class UserRole {
    @Id
    @GeneratedValue
    @Column(name = "id", unique = true, nullable = false)
    private int id;


    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    Users users;

    @ManyToOne
    @JoinColumn(name = "role_id", referencedColumnName = "id")
    Role role;
}
