package dev.hsu.potatotest.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity(name = "content_model")
@Schema(description = "single Content DAO")
public class ContentModel {

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private Long createUserId;
    private String title;
    private String content;

    @CreationTimestamp
    private LocalDateTime createdDate;

    @UpdateTimestamp
    private LocalDateTime updateDate;

    public ContentModel() {
    }

    public ContentModel(Long createUserId, String title, String content) {
        this.createUserId = createUserId;
        this.title = title;
        this.content = content;
    }

    public ContentModel(Long id, Long createUserId, String title, String content, LocalDateTime createdDate, LocalDateTime updateDate) {
        this.id = id;
        this.createUserId = createUserId;
        this.title = title;
        this.content = content;
        this.createdDate = createdDate;
        this.updateDate = updateDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(LocalDateTime updateDate) {
        this.updateDate = updateDate;
    }

    @Override
    public String toString() {
        return "ContentModel{" +
                "id=" + id +
                ", createUserId=" + createUserId +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", createdDate=" + createdDate +
                ", updateDate=" + updateDate +
                '}';
    }
}
