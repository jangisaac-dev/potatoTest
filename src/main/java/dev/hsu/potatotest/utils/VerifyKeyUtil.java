package dev.hsu.potatotest.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class VerifyKeyUtil {
    private static VerifyKeyUtil instance;

    public static VerifyKeyUtil getInstance() {
        if (instance == null) {
            instance = new VerifyKeyUtil();
            instance.random.setSeed(Long.parseLong(instance.projectThingPath));
        }
        return instance;
    }


    @Value("${hsu.verify.random_seed}")
    private String projectThingPath;
    @Value("${hsu.verify.time_second}")
    public Long verifyTime;

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
}
