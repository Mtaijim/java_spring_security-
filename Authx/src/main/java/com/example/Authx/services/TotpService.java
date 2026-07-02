package com.example.Authx.services;

import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import org.springframework.stereotype.Service;

import static dev.samstevens.totp.util.Utils.getDataUriForImage;

@Service
public class TotpService {
    private static final String ISSUER = "Authx";
//    random secret
    public String generateSecret(){
        SecretGenerator secretGenerator = new DefaultSecretGenerator();
        return secretGenerator.generate();
    }

//    qr code base64 data uri
    public String generateQrCode(String secret, String email) throws QrGenerationException {
        QrData data = new QrData.Builder()
                .label(email)
                .secret(secret)
                .issuer(ISSUER)
                .algorithm(HashingAlgorithm.SHA1)
                .digits(6)
                .period(30)
                .build();
        QrGenerator generator = new ZxingPngQrGenerator();
        byte [] imageData = generator.generate(data);
        return getDataUriForImage(imageData,generator.getImageMimeType());
    }
//    code verify against stored secret
public boolean verifyCode(String secret , String code){
        if(secret==null || code ==null)return false;
    TimeProvider timeProvider = new SystemTimeProvider();
    CodeGenerator codeGenerator = new DefaultCodeGenerator();
    DefaultCodeVerifier verifier = new DefaultCodeVerifier(codeGenerator,timeProvider);

    verifier.setTimePeriod(30);
    verifier.setAllowedTimePeriodDiscrepancy(1);
    return verifier.isValidCode(secret,code);


    }
}
