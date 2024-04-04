package dev.hsu.potatotest.utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.hsu.potatotest.domain.UserModel;
import dev.hsu.potatotest.dto.TokenDTO;
import dev.hsu.potatotest.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.runtime.misc.Triple;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.math.NumberUtils;
import org.hibernate.mapping.Any;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.Map;


@Component
public class JwtTokenProvider {

//    private final Logger LOGGER = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.access_valid_time}")
    private String accessTokenValidSecond;
    @Value("${jwt.refresh_valid_time}")
    private String refreshTokenValidSecond;
    @Value("${hsu.aes.key}")
    private String aesKey;

    @Autowired
    private UserService userService;

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(this.secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

//    public JwtTokenProvider() {
//        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes(StandardCharsets.UTF_8));
//    }

    public TokenDTO createToken(Long userId) {
        return new TokenDTO(
                createAccessToken(userId),
                createRefreshToken(userId)
        );
    }
    public String createAccessToken(Long userId) {
        try {
            return createToken(String.valueOf(userId), accessTokenValidSecond);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
//            throw new RuntimeException(e);
        }
    }
    public String createRefreshToken(Long userId) {
        try {
            return createToken(String.valueOf(userId), refreshTokenValidSecond);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
//            throw new RuntimeException(e);
        }
    }

    public String createToken(String userId, String validTime) throws Exception {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userId", userId);

        Claims claims = Jwts.claims().subject(encrypt(jsonObject.toString())).build();
        Date now = new Date();

        System.out.println("validTime : " + validTime);

        return Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(new Date(Long.parseLong(validTime) * 1000 + now.getTime()))
                .signWith(getSigningKey())
                .compact();
    }


    public Claims extractAllClaims(String token){
        return Jwts.parser()
                .verifyWith(this.getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getUserId(String token) {
        JsonElement userId = extraValue(token).get("userId");
        if (userId.isJsonNull()) {
            return null;
        }

        return userId.getAsString();
    }

    private JsonObject extraValue(String token) {
        String subject = extractAllClaims(token).getSubject();
        try {
            String decrypted = decrypt(subject);
            JsonObject json = new Gson().fromJson(decrypted, JsonObject.class);
            System.out.println("keyset : " + json.keySet());
            return json;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String encrypt(String plainText) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(aesKey.getBytes(StandardCharsets.UTF_8), "AES");
        IvParameterSpec IV = new IvParameterSpec(aesKey.substring(0,16).getBytes());

        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");

        c.init(Cipher.ENCRYPT_MODE, secretKey, IV);

        byte[] encrpytionByte = c.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

        return Hex.encodeHexString(encrpytionByte);
    }

    private String decrypt(String encodeText) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(aesKey.getBytes(StandardCharsets.UTF_8), "AES");
        IvParameterSpec IV = new IvParameterSpec(aesKey.substring(0,16).getBytes());

        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");

        c.init(Cipher.DECRYPT_MODE, secretKey, IV);

        byte[] decodeByte = Hex.decodeHex(encodeText.toCharArray());

        return new String(c.doFinal(decodeByte), StandardCharsets.UTF_8);
    }

    public UserModel getUserWithValidation(String token) {
        if (!isValidUser(token)) {
            return null;
        }

        String userId = getUserId(token);
        return userService.findById(Long.valueOf(userId));
    }

    public boolean isValidUser(String token) {
        return isValidUserWithMessage(token).a == 200;
    }

    public Pair<Integer, String> isValidUserWithMessage(String token) {
        String userId = getUserId(token);
        if (!NumberUtils.isParsable(userId)) {
            return new Pair<>(400, "token not valid");
        }

        UserModel user = userService.findById(Long.valueOf(userId));
        if (user == null) {
            return new Pair<>(404, "not found user");
        }
        return new Pair<>(200, "success");
    }

}

