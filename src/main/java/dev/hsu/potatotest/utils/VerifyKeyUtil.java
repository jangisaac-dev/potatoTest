package dev.hsu.potatotest.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Random;
import org.apache.commons.codec.binary.Hex;

@Component
public class VerifyKeyUtil {


//    @Value("${hsu.verify.random_seed}")
//    private String randomSeed;
    @Value("${hsu.verify.time_second}")
    private String verifyTime;
    @Value("${hsu.verify.admin_password}")
    private String adminPassword;

    public Long getVerifyTime() {
        System.out.println("verifyTime : " + verifyTime);
//        return Long.parseLong(verifyTime);
        return 10000L;
    }

    private final Random random = new Random();

    public String generateKey() {
        return generateKey(4);
    }

    public String generateKey(int size) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
//            sb.append(random.nextInt());
            sb.append(i+1); // 검증 코드 단순화
        }
        return sb.toString();
    }

    public Boolean checkAdminPassword(String pw) {
        return pw.equals(adminPassword);
    }

}
