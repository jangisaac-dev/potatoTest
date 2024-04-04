package dev.hsu.potatotest.dto;

import dev.hsu.potatotest.domain.ContentModel;
import dev.hsu.potatotest.domain.TagModel;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "single Content DTO")
public class TokenDTO {


    String accessToken;

    String refreshToken;

    public TokenDTO() {
    }

    public TokenDTO(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
