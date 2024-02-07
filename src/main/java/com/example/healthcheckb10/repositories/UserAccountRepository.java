package com.example.healthcheckb10.repositories;

import com.example.healthcheckb10.entities.UserAccount;
import com.example.healthcheckb10.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount,Long> {
    boolean existsUserAccountByEmail(String email);
    boolean existsUserAccountByRole(Role role);
    Optional<UserAccount>findByEmail(String email);
    @Query(value = "select u.sms_code from user_accounts u where u.id=?1 ",nativeQuery = true )
    String findCode(Long id);
    @Modifying
    @Query(value = "UPDATE user_accounts SET sms_code = :smsCode WHERE id = :userId",nativeQuery = true)
    void addSmsCode(String smsCode,Long userId);
    @Query("from UserAccount a where a.resetToken = ?1")
    Optional<UserAccount>findByResetToken(String ResetToken);
}