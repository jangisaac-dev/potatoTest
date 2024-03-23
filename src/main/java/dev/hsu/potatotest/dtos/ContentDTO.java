package dev.hsu.potatotest.dtos;

import dev.hsu.potatotest.domain.ContentSingleModel;
import dev.hsu.potatotest.domain.TagSingleModel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "single Content DTO")
public class ContentDTO {


    private Long id;
    private String title;
    private String content;

    private List<TagSingleModel> tagList;

    public ContentDTO(ContentSingleModel contentSingleModel) {
        this.id = contentSingleModel.getId();
        this.title = contentSingleModel.getTitle();
        this.content = contentSingleModel.getContent();
    }

    public ContentDTO(ContentSingleModel contentSingleModel, List<TagSingleModel> tagList) {
        this.id = contentSingleModel.getId();
        this.title = contentSingleModel.getTitle();
        this.content = contentSingleModel.getContent();
        this.tagList = tagList;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public List<TagSingleModel> getTagList() {
        return tagList;
    }

    public void setTagList(List<TagSingleModel> tagList) {
        this.tagList = tagList;
    }

}
