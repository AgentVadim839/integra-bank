package com.bank.integra.dao;

import com.bank.integra.entities.details.UserDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDetailsRepository extends JpaRepository<UserDetails, Integer> {
    UserDetails findUserDetailsByUserId(Integer id);
    Boolean existsByUserId(Integer id);
    Boolean existsByEmail(String email);
}
