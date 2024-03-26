package dev.hsu.potatotest.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

//foreign key remove version
@Entity(name = "tag_model")
@Schema(description = "Tag Model For removed fk Content Model DAO")
public class TagModel {

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private Long contentId;

    @NotNull
    private String tagName;

    public TagModel() {
    }

    public TagModel(String tagName) {
        this.tagName = tagName;
    }

    public TagModel(Long contentId, String tagName) {
        this.contentId = contentId;
        this.tagName = tagName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getContentId() {
        return contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }
}
