package dev.hsu.potatotest.service;

import dev.hsu.potatotest.domain.ContentSingleModel;
import dev.hsu.potatotest.domain.TagSingleModel;
import dev.hsu.potatotest.repo.TagRepository;
import dev.hsu.potatotest.repo.TagSingleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class TagService {

    @Autowired
    private TagSingleRepository tagRepository;


//
//
//    public List<TagSingleModel> getTags(boolean query) {
//        List<TagSingleModel> Tags;
//        if (query) {
//            Tags = tagRepository.findAllQuery();
//        }
//        else {
//            Tags = tagRepository.findAll();
//        }
//        return Tags;
//    }
//
//    public Optional<TagSingleModel> getTagById(final Long id, final boolean isQuery) {
//        Optional<TagSingleModel> result;
//        if (isQuery) {
//            result = tagRepository.findByIdQuery(id);
//        }
//        else {
//            result = tagRepository.findById(id);
//        }
//        return result;
//    }
//
    public List<TagSingleModel> createTagList(final List<TagSingleModel> modelList) {
        List<TagSingleModel> result = tagRepository.saveAll(modelList);
        tagRepository.flush();
        return result;
    }

    public TagSingleModel createTag(final TagSingleModel createTagSingleModel) {
        if(createTagSingleModel == null) throw new IllegalArgumentException("Tag item cannot be null");
        if (tagRepository.findAllByContentId(createTagSingleModel.getId()).size() >= 5) {
            throw new IllegalArgumentException("Tag item cannot be more than 5");
        }
        TagSingleModel result = tagRepository.save(createTagSingleModel);
        tagRepository.flush();
        return result;
    }

    public List<TagSingleModel> updateTagList(Long contentId, List<String> models) {
        List<TagSingleModel> originList = tagRepository.findAllByContentId(contentId);
        List<TagSingleModel> inputList = new ArrayList<>();

        int lostCnt = originList.size() - models.size();
        if (lostCnt > 0) {
            for (int i = 0; i < lostCnt; i++) {
                tagRepository.delete(originList.get(i));
            }
        }
        tagRepository.flush();
        originList = tagRepository.findAllByContentId(contentId);

        for (int i = 0; i < models.size(); i++) {
            String tagName = models.get(i);
            TagSingleModel TagSingleModel;
            if (i >= originList.size()) {
                TagSingleModel = new TagSingleModel(contentId, tagName);
            }
            else {
                TagSingleModel = originList.get(i);
                TagSingleModel.setTagName(tagName);
            }
            inputList.add(TagSingleModel);
        }
        List<TagSingleModel> result = tagRepository.saveAll(inputList);
        tagRepository.flush();
        return result;
    }
    public void deleteTagById(final Long id) {
        tagRepository.deleteById(id);
        tagRepository.flush();
    }

    public void deleteTagByContentId(final Long id) {
        List<TagSingleModel> list = tagRepository.findAllByContentId(id);
        for (TagSingleModel TagSingleModel : list) {
            tagRepository.deleteById(TagSingleModel.getId());
        }
        tagRepository.flush();
    }

    public List<TagSingleModel> findAllByContentId(final Long id) {
        return tagRepository.findAllByContentId(id);
    }

    public List<Long> findContentIdByTagName(final String tag) {
        return tagRepository.findContentIdByTagName(tag);
    }
}
