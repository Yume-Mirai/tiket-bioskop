package com.uasjava.tiketbioskop.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uasjava.tiketbioskop.model.UserRole;
import com.uasjava.tiketbioskop.model.Users;

public interface UserRoleRepository extends JpaRepository <UserRole, Integer> {
    // @Query("SELECT ur FROM UserRole ur WHERE ur.user.id = :userId")
    // List<UserRole> findByUserId(@Param("userId") Long userId);
    // @Query("SELECT ur FROM UserRole ur WHERE ur.user.id = :userId")
    // List<UserRole> findByUserId(@Param("userId") Long userId);
    
        List<UserRole> findByUsers(Users users);

}
