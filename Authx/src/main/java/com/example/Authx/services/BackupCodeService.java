package com.example.Authx.services;

import com.example.Authx.entity.MfaBackUpcode;
import com.example.Authx.entity.User;
import com.example.Authx.repositories.MfaBackupCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BackupCodeService {

    private static final int CODE_COUNT = 10 ;
    private static final SecureRandom RANDOM = new SecureRandom();
    private final MfaBackupCodeRepository mfaBackupCodeRepository;
    private final PasswordEncoder passwordEncoder;


    public List<String> generateBackUpCodes(User user ){
        mfaBackupCodeRepository.deleteByUser(user);
        mfaBackupCodeRepository.flush();

        List<String> plainCodes = new ArrayList<>();
        for(int i = 0  ; i< CODE_COUNT ; i++){
            String code = generateSingleCode();
            plainCodes.add(code);

            MfaBackUpcode entity = new MfaBackUpcode();
            entity.setUser(user);
            entity.setCodeHash(passwordEncoder.encode(code));
            entity.setUsed(false);
            entity.setCreatedAt(LocalDateTime.now());
            mfaBackupCodeRepository.save(entity);
        }
        return plainCodes;
    }

    public boolean verifyAndConsume(User user, String inputCode){
        List<MfaBackUpcode> codes = mfaBackupCodeRepository.findByUserAndUsedFalse(user);
        for (MfaBackUpcode storedCode : codes){
            if(passwordEncoder.matches(inputCode , storedCode.getCodeHash())){
                storedCode.setUsed(true);
                mfaBackupCodeRepository.save(storedCode);
                return true;
            }

        }
        return false;
    }
    public int countRemaining(User user) {
        return mfaBackupCodeRepository.findByUserAndUsedFalse(user).size();
    }
    private String generateSingleCode() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            if (i == 4) sb.append('-');
            sb.append(chars.charAt(RANDOM.nextInt(chars.length())));
        }
        return sb.toString();
    }


}
