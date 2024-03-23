package dev.hsu.potatotest.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.hsu.potatotest.domain.ContentSingleModel;
import dev.hsu.potatotest.domain.ContentSingleModel;
import dev.hsu.potatotest.domain.TagModel;
import dev.hsu.potatotest.domain.TagSingleModel;
import dev.hsu.potatotest.dtos.ContentDTO;
import dev.hsu.potatotest.service.ContentService;
import dev.hsu.potatotest.service.TagService;
import dev.hsu.potatotest.utils.LocalDateTimeSerializer;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class MainController {

    @Autowired
    private ContentService contentService;
    @Autowired
    private TagService tagService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "success", content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = ContentSingleModel.class))
            )),
            @ApiResponse(responseCode = "404", description = "not found data")
    })
    @GetMapping("/list")
    public ResponseEntity getList(@RequestParam(value="query", required = false, defaultValue="false") boolean query) {
        List<ContentSingleModel> ContentSingleModels = contentService.getContents(query);
        if (!ContentSingleModels.isEmpty()) {
            return ResponseEntity.ok(ContentSingleModels);
        }
        return ResponseEntity.notFound().build();
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "success", content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = ContentSingleModel.class))
            )),
            @ApiResponse(responseCode = "404", description = "not found data")
    })
    @GetMapping("/getContent/{id}")
    public ResponseEntity getContent(@PathVariable("id") Long id,
                                     @RequestParam(value="query", required = false, defaultValue="false") boolean query) {
        Optional<ContentSingleModel> result = contentService.getContentById(id, query);
        if (result.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new ContentDTO(result.get(), tagService.findAllByContentId(result.get().getId())));
    }


    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "success", content = @Content(
                    schema = @Schema(implementation = ContentSingleModel.class)
            )),
            @ApiResponse(responseCode = "503", description = "too many tags failed"),
    })
    @Parameters({
            @Parameter(name = "title", description = "title Value"),
            @Parameter(name = "content", description = "content Value"),
            @Parameter(name = "tags", description = "tag list Value")
    })
    @PostMapping("/insert")
    public ResponseEntity insert(String title, String content, @RequestParam @Nullable HashSet<String> tags) {
        if (tags != null && tags.size() > 5) {
            return ResponseEntity.status(504).body("too many tags"); // code 외부 정의 생략
        }

        ContentSingleModel result = contentService.createContent(new ContentSingleModel(title, content));
        ContentDTO contentDTO = new ContentDTO(result);

        if (tags != null) {
            System.out.println("tag : " + tags.size());
            ArrayList<TagSingleModel> tagList = new ArrayList<>();
            for (String tag : tags) {
                tagList.add(new TagSingleModel(result.getId(), tag));
            }
            List<TagSingleModel> tagResult = tagService.createTagList(tagList);
            contentDTO.setTagList(tagResult);
        }


        System.out.println("input result " + new Gson().toJson(contentDTO));

        return ResponseEntity.ok(contentDTO);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "success", content = @Content(
                    schema = @Schema(implementation = ContentSingleModel.class)
            )),
            @ApiResponse(responseCode = "404", description = "not found data")
    })
    @Parameters({
            @Parameter(name = "title", description = "title Value"),
            @Parameter(name = "content", description = "content Value"),
            @Parameter(name = "tags", description = "tag list Value")
    })
    @PutMapping("/edit")
    public ResponseEntity edit(Long id, String title, String content,
                               @RequestParam(value="query", required = false, defaultValue="false") boolean query,
                               @RequestParam @Nullable HashSet<String> tags
    ) {
        ContentSingleModel result = contentService.updateContent(id, new ContentSingleModel(title, content), query);
        if (tags != null) {
            tagService.updateTagList(result.getId(), tags.stream().toList());
        }
        else {
            tagService.deleteTagByContentId(result.getId());
        }
        return ResponseEntity.ok(new ContentDTO(result, tagService.findAllByContentId(result.getId())));
    }
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "success", content = @Content(
                    schema = @Schema(implementation = ContentSingleModel.class)
            )),
            @ApiResponse(responseCode = "404", description = "not found data")
    })
    @Parameters({
            @Parameter(name = "title", description = "title Value"),
            @Parameter(name = "content", description = "content Value"),
            @Parameter(name = "tags", description = "tag list Value")
    })
    @PutMapping("/edit/{id}")
    public ResponseEntity editPath(@PathVariable("id") Long id, @Nullable String title, @Nullable String content, @RequestParam(value="query", required = false, defaultValue="false") boolean query, @RequestParam @Nullable HashSet<String> tags) {
        if (contentService.getContentById(id, query).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        if (tags != null) {
            tagService.updateTagList(id, tags.stream().toList());
        }
        else {
            tagService.deleteTagByContentId(id);
        }
        ContentSingleModel result = contentService.updateContent(id, new ContentSingleModel(title, content), query);
        return ResponseEntity.ok(new ContentDTO(result, tagService.findAllByContentId(result.getId())));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity deletePath(@PathVariable("id") Long id) {
        if (contentService.getContentById(id, false).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        contentService.deleteContentById(id);
        tagService.deleteTagByContentId(id);
        return ResponseEntity.ok().build();
    }


    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "success", content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = ContentDTO.class))
            )),
            @ApiResponse(responseCode = "404", description = "not found data")
    })
    @GetMapping("/searchByTag")
    public ResponseEntity searchByTag(String title) {
        List<Long> ids = tagService.findContentIdByTagName(title);
        List<ContentSingleModel> contents = contentService.getContentsById(ids);

        List<ContentDTO> result = new ArrayList<>();
        for (ContentSingleModel model : contents) {
            result.add(new ContentDTO(model, tagService.findAllByContentId(model.getId())));
        }

        if (!result.isEmpty()) {
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.notFound().build();
    }

}
