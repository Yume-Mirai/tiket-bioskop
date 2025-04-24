package com.uasjava.tiketbioskop.model;


import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity 
@Table(name = "role")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class Role {
    @Id
    @Column(name = "id", unique = true, nullable = false)
    private int id;

    @Column(name = "role_name", length = 100)
    private String roleName;

    @Column(name = "created_date", columnDefinition = "DATE")
    private LocalDate createdDate;

    @Column(name = "update_date", columnDefinition = "DATE")
    private LocalDate updateDate;

}

