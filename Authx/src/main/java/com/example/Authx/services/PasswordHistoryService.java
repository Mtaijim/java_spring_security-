package com.example.Authx.services;

import com.example.Authx.entity.PasswordHistory;
import com.example.Authx.entity.User;
import com.example.Authx.repositories.PasswordHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PasswordHistoryService {

    private final PasswordEncoder passwordEncoder;
    private final PasswordHistoryRepository passwordHistoryRepository;

    private static final int HISTORY_LIMIT = 5;

    public boolean ispasswordReused(User user, String password){
        List<PasswordHistory> history = passwordHistoryRepository
                .findTop5ByUserOrderByCreatedAtDesc(user);

        return  history.stream().anyMatch(
                h-> passwordEncoder
                        .matches(password, h.getPasswordHash())
        );

    }

    public void saveToHistory(User user , String HashedPassword){
        PasswordHistory history = PasswordHistory.builder()
                .user(user)
                .passwordHash(HashedPassword)
                .build();
        passwordHistoryRepository.save(history);
    }


}
