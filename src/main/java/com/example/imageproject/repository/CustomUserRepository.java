package com.example.imageproject.repository;


import com.example.imageproject.domain.CustomUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface CustomUserRepository extends JpaRepository<CustomUser, Long> {

    CustomUser findByEmail(String email);
    CustomUser findByActivation(String confirmationToken);
    CustomUser findByUsername(String username);

}
