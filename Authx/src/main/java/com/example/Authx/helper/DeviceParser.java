package com.example.Authx.helper;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class DeviceParser {

    public String parseDevice(HttpServletRequest request ){
        String userAgent = getUserAgent(request);
        if(userAgent.contains("Mobile") || userAgent.contains("Android")){
            if(userAgent.contains("iPhone") || userAgent.contains("iPad")){
                return "iPhone/iPad";
            }
            return "Mobile";
        }
        if(userAgent.contains("Tablet")) return "Tablet";
        if(userAgent.contains("Chrome"))  return "Chrome";
        if(userAgent.contains("Firefox")) return "Firefox";
        if(userAgent.contains("Safari"))  return "Safari";
        if(userAgent.contains("MSIE") || userAgent.contains("Trident")) {
            return "Internet Explorer";
        }
      return "Unknown Browser";
    }
    //  Parse OS from User-Agent
    public String parseOs(HttpServletRequest request){
        String userAgent = getUserAgent(request);

        if (userAgent.contains("Windows NT 10")) return "Windows 10/11";
        if (userAgent.contains("Windows NT"))    return "Windows";
        if (userAgent.contains("Mac OS X"))      return "macOS";
        if (userAgent.contains("Android"))       return "Android";
        if (userAgent.contains("iPhone") ||
                userAgent.contains("iPad"))          return "iOS";
        if (userAgent.contains("Linux"))         return "Linux";
        return "Unknown OS";
    }

    //  Extract real IP (handles proxies)
public String parseIp(HttpServletRequest request){

//        check for proxy header
    String ip = request.getHeader("X-Forwarded-For");
    if(ip !=null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)){
        return ip.split(",")[0].trim();
    }
    ip = request.getHeader("X-Real-Ip");
    if (ip !=null && !ip.isEmpty())return ip;
    return request.getRemoteAddr();
}

    private String getUserAgent(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        return userAgent != null ?userAgent :"";
    }
}
