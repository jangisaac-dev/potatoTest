package dev.hsu.potatotest.service;

import com.google.gson.Gson;
import dev.hsu.potatotest.domain.ContentModel;
import dev.hsu.potatotest.domain.TagModel;
import dev.hsu.potatotest.dto.ContentDTO;
import dev.hsu.potatotest.repo.ContentRepository;
import dev.hsu.potatotest.repo.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ContentDTOService {

    @Autowired
    private ContentRepository contentRepository;
    @Autowired
    private TagRepository tagRepository;

    public List<ContentDTO> getContents() {
        List<ContentDTO> result = new ArrayList<>();
        List<ContentModel> contents = contentRepository.findAllQuery();
        for (ContentModel content : contents) {
            result.add(joinTag(content));
        }
        return result;
    }

    private ContentDTO joinTag(ContentModel content) {
        List<TagModel> tags = tagRepository.findAllByContentId(content.getId());

        ContentDTO result = new ContentDTO(content);
        result.setTagList(tags);
        return result;
    }

    public ContentDTO getContentById(final Long id) {
        Optional<ContentModel> result = contentRepository.findById(id);
        if (result.isEmpty()) {
            return null;
        }

        return joinTag(result.get());
    }
    public List<ContentDTO> getContentsById(final List<Long> ids) {
        ArrayList<ContentDTO> nList = new ArrayList<>();
        for (Long id : ids) {
            nList.add(getContentById(id));
        }
        return nList;
    }

    public ContentDTO createContent(final ContentDTO createContentModel) {
        if(createContentModel == null) throw new IllegalArgumentException("content item cannot be null");

        ContentModel result = contentRepository.saveAndFlush(createContentModel);
        tagRepository.saveAllAndFlush(createContentModel.getTagList());
        return getContentById(result.getId());
    }
    public ContentDTO createContent(final ContentModel createContentModel) {
        if(createContentModel == null) throw new IllegalArgumentException("content item cannot be null");

        return new ContentDTO(contentRepository.saveAndFlush(createContentModel));
    }

    public ContentDTO updateContent(final ContentDTO updatedModel) {
        List<TagModel> upTags = updatedModel.getTagList();
        tagRepository.saveAllAndFlush(upTags);
        contentRepository.saveAndFlush(updatedModel.getParentModel());

        return getContentById(updatedModel.getId());
    }

    public void deleteContentById(final Long id) {
        contentRepository.deleteById(id);
        contentRepository.flush();

        tagRepository.deleteAll(tagRepository.findAllByContentId(id));
        tagRepository.flush();
    }
}
