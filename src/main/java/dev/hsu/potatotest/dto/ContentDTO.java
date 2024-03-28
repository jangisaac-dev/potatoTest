package dev.hsu.potatotest.dto;

import dev.hsu.potatotest.domain.ContentModel;
import dev.hsu.potatotest.domain.TagModel;
import io.swagger.v3.oas.annotations.media.Schema;

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
}
