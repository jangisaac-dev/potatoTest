package dev.hsu.potatotest.dto;

import dev.hsu.potatotest.domain.ContentModel;
import dev.hsu.potatotest.domain.TagModel;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "single Content DTO")
public class ContentDTO extends ContentModel {

    private List<TagModel> tagList;

    public List<TagModel> getTagList() {
        return tagList;
    }

    public void setTagList(List<TagModel> tagList) {
        this.tagList = tagList;
    }

    public ContentDTO() {
    }

    public ContentDTO(Long id, Long createUserId, String title, String content, LocalDateTime createdDate, LocalDateTime updateDate, List<TagModel> tagList) {
        super(id, createUserId, title, content, createdDate, updateDate);
        this.tagList = tagList;
    }

    public ContentDTO(ContentModel parent, List<TagModel> tagList) {
        super(parent.getId(), parent.getCreateUserId(), parent.getTitle(), parent.getContent(), parent.getCreatedDate(), parent.getUpdateDate());
        this.tagList = tagList;
    }
    public ContentDTO(ContentModel parent) {
        super(parent.getId(), parent.getCreateUserId(), parent.getTitle(), parent.getContent(), parent.getCreatedDate(), parent.getUpdateDate());
    }

    @Override
    public String toString() {
        return "ContentDTO{" +
                super.toString() +
                "tagList=" + tagList +
                '}';
    }

    public ContentModel getParentModel() {
        return new ContentModel(getId(), getCreateUserId(), getTitle(), getContent(), getCreatedDate(), getUpdateDate());
    }
}
