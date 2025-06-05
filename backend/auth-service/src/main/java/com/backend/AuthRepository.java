package com.backend;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.backend.User.User;

@Repository
public interface AuthRepository extends JpaRepository<User, String>{

    @Query(value="SELECT * FROM users WHERE email=?1",nativeQuery=true)
    User findUserByEmail(String email);

    @Query(value="DELETE FROM users WHERE email=?1", nativeQuery = true)
    void deleteByEmail(String email);
    
}