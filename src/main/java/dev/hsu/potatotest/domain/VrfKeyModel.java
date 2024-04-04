package dev.hsu.potatotest.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

//foreign key remove version
@Entity(name = "vrf_key_model")
@Schema(description = "Verified key temp save model")
public class VrfKeyModel {

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull
    @Column(unique = true)
    private String userEmail;

    @NotNull
    private String verifiedKey;

    @CreationTimestamp
    private LocalDateTime createdDate;


    public VrfKeyModel(String userEmail, String verifiedKey) {
        this.userEmail = userEmail;
        this.verifiedKey = verifiedKey;
    }

    public VrfKeyModel() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getVerifiedKey() {
        return verifiedKey;
    }

    public void setVerifiedKey(String verifiedKey) {
        this.verifiedKey = verifiedKey;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
}
