package dev.hsu.potatotest.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity(name = "tag_model")
@Schema(description = "Tag Model For Content Model")
public class TagModel {

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @JsonIgnore
    private ContentModel contentModel;

    @NotNull
    private String tagName;

    public TagModel() {
    }

    public TagModel(String tagName) {
        this.tagName = tagName;
    }

    public TagModel(ContentModel contentModel, String tagName) {
        this.contentModel = contentModel;
        this.tagName = tagName;
    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ContentModel getContentModel() {
        return contentModel;
    }

    public void setContentModel(ContentModel contentModel) {
        this.contentModel = contentModel;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }
}
