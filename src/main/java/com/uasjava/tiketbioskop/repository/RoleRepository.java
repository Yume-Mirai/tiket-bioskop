package com.uasjava.tiketbioskop.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uasjava.tiketbioskop.model.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    // Optional<Role> findByName(String name);
    // List<Role> findAllByNameIn(List<String> names);
    // List<Role> findAllByIdIn(List<Long> ids);
    
}
