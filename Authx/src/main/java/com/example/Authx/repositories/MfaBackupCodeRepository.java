package com.example.Authx.repositories;

import com.example.Authx.entity.MfaBackUpcode;
import com.example.Authx.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MfaBackupCodeRepository extends JpaRepository<MfaBackUpcode,Long> {

    // get all codes for a user
    List<MfaBackUpcode> findByUser(User user);

    // get only unused codes
    List<MfaBackUpcode> findByUserAndUsedFalse(User user);

    // wipe all old codes before generating new ones
    void deleteByUser(User user);
}
