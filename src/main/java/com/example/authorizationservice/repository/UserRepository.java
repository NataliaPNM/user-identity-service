package com.example.authorizationservice.repository;

import com.example.authorizationservice.model.Secure;
import com.example.authorizationservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User,Long> {
   // Optional<User> findByEmail(String email);
  // @Query("select u from users u  where u.login =:login")
   // Optional<User> findByLogin(String login);

}
