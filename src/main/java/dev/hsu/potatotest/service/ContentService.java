package dev.hsu.potatotest.service;

import dev.hsu.potatotest.domain.ContentModel;
import dev.hsu.potatotest.repo.ContentRepository;
import dev.hsu.potatotest.repo.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ContentService {

    @Autowired
    private ContentRepository contentRepository;


    public List<ContentModel> getContents(boolean query) {
        List<ContentModel> contents;
        if (query) {
            contents = contentRepository.findAllQuery();
        }
        else {
            contents = contentRepository.findAll();
        }
        return contents;
    }

    public Optional<ContentModel> getContentById(final Long id, final boolean isQuery) {
        Optional<ContentModel> result;
        if (isQuery) {
            result = contentRepository.findByIdQuery(id);
        }
        else {
            result = contentRepository.findById(id);
        }
        return result;
    }

    public List<ContentModel> getContentsById(final List<Long> ids) {
        return contentRepository.findAllById(ids);
    }

    public ContentModel createContent(final ContentModel createContentModel) {
        if(createContentModel == null) throw new IllegalArgumentException("content item cannot be null");

        return contentRepository.saveAndFlush(createContentModel);
    }

    public ContentModel updateContent(final long id, final ContentModel updateContentModel, boolean query) {
        Optional<ContentModel> opContent = getContentById(id, query);
        if (opContent.isEmpty()) {
            return null;
        }
        ContentModel ContentModel = opContent.get();

        if (updateContentModel.getTitle() == null) {
            updateContentModel.setTitle(ContentModel.getTitle());
        }
        ContentModel.setTitle(updateContentModel.getTitle());

        if (updateContentModel.getContent() == null) {
            updateContentModel.setContent(ContentModel.getContent());
        }
        ContentModel.setContent(updateContentModel.getContent());


        return contentRepository.save(ContentModel);
    }

    public void deleteContentById(final Long id) {
        contentRepository.deleteById(id);
        contentRepository.flush();
    }
}
