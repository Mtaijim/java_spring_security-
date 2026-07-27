package com.example.Authx.services;

import com.example.Authx.entity.LoginEvent;
import com.example.Authx.entity.User;
import com.example.Authx.helper.DeviceParser;
import com.example.Authx.repositories.LoginEventRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoginEventServices {

    private final LoginEventRepository loginEventRepository;
    private final DeviceParser deviceParser;

//     record success
    public void recordSucess(User user, HttpServletRequest request){
        save(user,request, LoginEvent.LoginStatus.SUCCESS,null);
    }
    public void recordFailure(User user, HttpServletRequest request,String reason){
        save(user,request, LoginEvent.LoginStatus.FAILED,reason);
    }
    public List<LoginEvent> getHistory(User user){
        return loginEventRepository.findTop20ByUserOrderByCreatedAtDesc(user);
    }

    private void save(User user , HttpServletRequest request, LoginEvent.LoginStatus status,String failureReason){
        LoginEvent event = LoginEvent.builder()
                .user(user)
                .ipAddress(deviceParser.parseIp(request))
                .device(deviceParser.parseDevice(request))
                .os(deviceParser.parseOs(request))
                .status(status)
                .failureReason(failureReason)
                .build();

        loginEventRepository.save(event);
    }
}
