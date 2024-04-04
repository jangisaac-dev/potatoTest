package dev.hsu.potatotest;

import dev.hsu.potatotest.utils.JwtTokenProvider;
import org.apache.commons.codec.binary.Hex;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

@SpringBootTest
class PotatoTestApplicationTests {

    @Test
    void contextLoads() {



    }

}
