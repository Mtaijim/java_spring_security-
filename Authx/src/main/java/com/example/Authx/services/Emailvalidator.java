package com.example.Authx.services;

import com.example.Authx.exceptions.InvalidEmailException;
import org.springframework.stereotype.Component;

import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.regex.Pattern;

@Component
public class Emailvalidator {
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    private static final Set<String> BLOCKED_DOMAINS = new HashSet<>(Arrays.asList(
            "mailinator.com", "guerrillamail.com", "guerrillamail.net",
            "guerrillamail.org", "tempmail.com", "temp-mail.org",
            "throwam.com", "throwaway.email", "yopmail.com",
            "sharklasers.com", "trashmail.com", "trashmail.net",
            "trashmail.me", "dispostable.com", "maildrop.cc",
            "spamgourmet.com", "fakeinbox.com", "mailnull.com",
            "spambox.us", "mytemp.email", "tempinbox.com",
            "getnada.com", "discard.email", "mailnesia.com",
            "notmailinator.com", "spamherelots.com", "binkmail.com",
            "bobmail.info", "letthemeatspam.com", "spam4.me",
            "spamfree24.org"
    ));
    public void Validate(String email){
        if(email == null || email.isBlank()){
            throw new InvalidEmailException("Email cannot be Empty");
        }

        // layer 1 - format check
        if(!EMAIL_PATTERN.matcher(email).matches()){
            throw new InvalidEmailException("Invalid Email format");
        }
        String domain = email.substring(email.indexOf('@') + 1).toLowerCase();

//      layer 2 - to check domain

        if(BLOCKED_DOMAINS.contains(domain)){
            throw new InvalidEmailException("Disposable email addresses are not allowed");
        }
//    layer 3 Mx record

        if(!hasMxRecord(domain)){
            throw new InvalidEmailException("email domain does not exists or cannot receive emails");
        }
    }

    private boolean hasMxRecord(String domain) {
        try{
            Hashtable<String,String> env = new Hashtable<>();
            env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
            env.put("java.naming.provider.url", "dns://8.8.8.8");
            env.put("com.sun.jndi.dns.timeout.initial", "2000");
            env.put("com.sun.jndi.dns.timeout.retries", "1");
            InitialDirContext ctx = new InitialDirContext(env);
            Attributes attrs = ctx.getAttributes(domain, new String[]{"MX"});
            return attrs.get("MX") != null;


        } catch (Exception e) {
          return false;
        }
    }

}
