package dev.hsu.potatotest.service;

import dev.hsu.potatotest.domain.TagModel;
import dev.hsu.potatotest.repo.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TagService {

    @Autowired
    private TagRepository tagRepository;


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
    public List<TagModel> createTagList(final List<TagModel> modelList) {
        List<TagModel> result = tagRepository.saveAllAndFlush(modelList);
        return result;
    }

    public TagModel createTag(final TagModel createTagModel) {
        if(createTagModel == null) throw new IllegalArgumentException("Tag item cannot be null");
        if (tagRepository.findAllByContentId(createTagModel.getId()).size() >= 5) {
            throw new IllegalArgumentException("Tag item cannot be more than 5");
        }
        TagModel result = tagRepository.saveAndFlush(createTagModel);
        return result;
    }

    public List<TagModel> updateTagList(Long contentId, List<String> models) {
        List<TagModel> originList = tagRepository.findAllByContentId(contentId);
        List<TagModel> inputList = new ArrayList<>();

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
            TagModel TagModel;
            if (i >= originList.size()) {
                TagModel = new TagModel(contentId, tagName);
            }
            else {
                TagModel = originList.get(i);
                TagModel.setTagName(tagName);
            }
            inputList.add(TagModel);
        }
        return tagRepository.saveAllAndFlush(inputList);
    }
    public void deleteTagById(final Long id) {
        tagRepository.deleteById(id);
        tagRepository.flush();
    }

    public void deleteTagByContentId(final Long id) {
        List<TagModel> list = tagRepository.findAllByContentId(id);
        for (TagModel TagModel : list) {
            tagRepository.deleteById(TagModel.getId());
        }
        tagRepository.flush();
    }

    public List<TagModel> findAllByContentId(final Long id) {
        return tagRepository.findAllByContentId(id);
    }

    public List<Long> findContentIdByTagName(final String tag) {
        return tagRepository.findContentIdByTagName(tag);
    }
}
