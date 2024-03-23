package dev.hsu.potatotest.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.hsu.potatotest.domain.ContentSingleModel;
import dev.hsu.potatotest.domain.TagModel;
import dev.hsu.potatotest.repo.ContentRepository;
import dev.hsu.potatotest.repo.ContentSingleRepository;
import dev.hsu.potatotest.repo.TagRepository;
import dev.hsu.potatotest.repo.TagSingleRepository;
import dev.hsu.potatotest.utils.LocalDateTimeSerializer;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ContentService {

    @Autowired
    private ContentSingleRepository contentRepository;
    @Autowired
    private TagSingleRepository tagRepository;




    public List<ContentSingleModel> getContents(boolean query) {
        List<ContentSingleModel> contents;
        if (query) {
            contents = contentRepository.findAllQuery();
        }
        else {
            contents = contentRepository.findAll();
        }
        return contents;
    }

    public Optional<ContentSingleModel> getContentById(final Long id, final boolean isQuery) {
        Optional<ContentSingleModel> result;
        if (isQuery) {
            result = contentRepository.findByIdQuery(id);
        }
        else {
            result = contentRepository.findById(id);
        }
        return result;
    }

    public List<ContentSingleModel> getContentsById(final List<Long> ids) {
        return contentRepository.findAllById(ids);
    }

    public ContentSingleModel createContent(final ContentSingleModel createContentSingleModel) {
        if(createContentSingleModel == null) throw new IllegalArgumentException("content item cannot be null");

        ContentSingleModel result = contentRepository.save(createContentSingleModel);
        contentRepository.flush();
        return result;
    }

    public ContentSingleModel updateContent(final long id, final ContentSingleModel updateContentSingleModel, boolean query) {
        Optional<ContentSingleModel> opContent = getContentById(id, query);
        if (opContent.isEmpty()) {
            return null;
        }
        ContentSingleModel ContentSingleModel = opContent.get();

        if (updateContentSingleModel.getTitle() == null) {
            updateContentSingleModel.setTitle(ContentSingleModel.getTitle());
        }
        ContentSingleModel.setTitle(updateContentSingleModel.getTitle());

        if (updateContentSingleModel.getContent() == null) {
            updateContentSingleModel.setContent(ContentSingleModel.getContent());
        }
        ContentSingleModel.setContent(updateContentSingleModel.getContent());


        return contentRepository.save(ContentSingleModel);
    }

    public void deleteContentById(final Long id) {
        contentRepository.deleteById(id);
        contentRepository.flush();
    }
}
