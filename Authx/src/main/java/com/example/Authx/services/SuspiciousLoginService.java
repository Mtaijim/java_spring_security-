package com.example.Authx.services;


import com.example.Authx.entity.LoginEvent;
import com.example.Authx.entity.User;
import com.example.Authx.repositories.LoginEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.regex.Pattern.matches;

@Service
@RequiredArgsConstructor
@Slf4j
public class SuspiciousLoginService {

    private final LoginEventRepository loginEventRepository;
    private final EmailService emailService;


    @Async
    public void checkAndAlert(
            User user,
            String currentDevice,
            String currentOs,
            String currentIp
    ){

        try{
            List<LoginEvent> recentLogins = loginEventRepository
                    .findTop10ByUserAndStatusOrderByCreatedAtDesc(
                            user,
                            LoginEvent.LoginStatus.SUCCESS
                    );

            if(recentLogins.isEmpty()){
                log.info("first login for : {}",user.getEmail());
                sendAlert(user, currentDevice,currentOs ,currentIp);
                return;
            }


            boolean knownDevice = recentLogins.stream()
                    .anyMatch(event ->
                            matches(event.getDevice(), currentDevice) &&
                                    matches(event.getOs(), currentOs)
                    );
boolean KnownIp = recentLogins.stream()
        .anyMatch(event->
                event.getIpAddress() != null
                && event.getIpAddress().equals(currentIp)
                );

            if (!knownDevice || !KnownIp) {
                log.info("New device/location login for: {}",
                        user.getEmail());
                sendAlert(user, currentDevice, currentOs, currentIp);
            }

        } catch (Exception e) {
            log.error(
                    "Failed to check suspicious login: {}",
                    e.getMessage()
            );
        }
    }

    private void sendAlert(
            User user,
            String device,
            String os,
            String ip) {

        emailService.sendSuspiciousLoginAlert(
                user.getEmail(),
                user.getName(),
                device,
                os,
                ip,
                LocalDateTime.now()
        );
    }
    private boolean matches(String a, String b) {
        if (a == null || b == null) return false;
        return a.equalsIgnoreCase(b);
    }

}
