package dev.hsu.potatotest.controller;

import com.google.gson.Gson;
import dev.hsu.potatotest.constants.AuthConstant;
import dev.hsu.potatotest.domain.ContentModel;
import dev.hsu.potatotest.domain.TagModel;
import dev.hsu.potatotest.domain.UserModel;
import dev.hsu.potatotest.dto.ContentDTO;
import dev.hsu.potatotest.service.*;
import dev.hsu.potatotest.utils.JwtTokenProvider;
import dev.hsu.potatotest.utils.VerifyKeyUtil;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class MainController {

    @Autowired
    private ContentDTOService contentDTOService;
    @Autowired
    private ContentService contentService;
    @Autowired
    private TagService tagService;


    @Autowired
    private UserService userService;
    @Autowired
    private VrfKeyService vrfKeyService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private VerifyKeyUtil verifyKeyUtil;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "success", content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = ContentModel.class))
            )),
            @ApiResponse(responseCode = "404", description = "not found data when empty db")
    })
    @GetMapping("/list")
    public ResponseEntity getList() {
        List<ContentDTO> contentModels = contentDTOService.getContents();
        if (!contentModels.isEmpty()) {
            return ResponseEntity.ok(contentModels);
        }
        return ResponseEntity.notFound().build();
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "success", content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = ContentModel.class))
            )),
            @ApiResponse(responseCode = "404", description = "not found data")
    })
    @GetMapping("/{id}")
    public ResponseEntity getContent(@PathVariable("id") Long id) {
        ContentDTO content = contentDTOService.getContentById(id);
        if (content == null) {
            return ResponseEntity.notFound().build();
        }
        content.setTagList(tagService.findAllByContentId(content.getId()));
        return ResponseEntity.ok(content);
    }


    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "success", content = @Content(
                    schema = @Schema(implementation = ContentModel.class)
            )),
            @ApiResponse(responseCode = "503", description = "too many tags failed"),
    })
    @Parameters({
            @Parameter(name = "token", description = "token Value"),
            @Parameter(name = "title", description = "title Value"),
            @Parameter(name = "content", description = "content Value"),
            @Parameter(name = "tags", description = "tag list Value")
    })
    @PostMapping("/")
    public ResponseEntity insert(@NotEmpty @NotNull String token, String title, String content, @RequestParam @Nullable HashSet<String> tags) {
        UserModel user = jwtTokenProvider.getUserWithValidation(token);

        if (user == null) {
            Pair<Integer, String> validChecker = jwtTokenProvider.isValidUserWithMessage(token);
            return ResponseEntity.status(validChecker.a).body(validChecker.b);
        }

        if (user.getUserRole() < AuthConstant.PERMISSION_CREATE) {
            return ResponseEntity.badRequest().body("permission denied");
        }

        if (tags != null && tags.size() > 5) {
            return ResponseEntity.status(504).body("too many tags"); // code 외부 정의 생략
        }

        ContentDTO contentDTO = contentDTOService.createContent(new ContentModel(user.getId(), title, content));

        if (tags != null) {
//            System.out.println("tag : " + tags.size());
            ArrayList<TagModel> tagList = new ArrayList<>();
            for (String tag : tags) {
                tagList.add(new TagModel(contentDTO.getId(), tag));
            }
            List<TagModel> tagResult = tagService.createTagList(tagList);
            contentDTO.setTagList(tagResult);
        }
//        System.out.println("input result " + new Gson().toJson(contentDTO));

        return ResponseEntity.ok(contentDTO);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "success", content = @Content(
                    schema = @Schema(implementation = ContentModel.class)
            )),
            @ApiResponse(responseCode = "404", description = "not found data")
    })
    @Parameters({
            @Parameter(name = "token", description = "token Value"),
            @Parameter(name = "id", description = "content id Value"),
            @Parameter(name = "title", description = "title Value"),
            @Parameter(name = "content", description = "content Value"),
            @Parameter(name = "tags", description = "tag list Value")
    })
    @PutMapping("/{id}")
    public ResponseEntity edit(@NotEmpty @NotNull String token, @PathVariable("id") Long id, String title, String content,
                               @RequestParam @Nullable HashSet<String> tags
    ) {
        UserModel user = jwtTokenProvider.getUserWithValidation(token);

        if (user == null) {
            Pair<Integer, String> validChecker = jwtTokenProvider.isValidUserWithMessage(token);
            return ResponseEntity.status(validChecker.a).body(validChecker.b);
        }

        if (user.getUserRole() < AuthConstant.PERMISSION_PUT) {
            return ResponseEntity.badRequest().body("permission denied");
        }

        if (contentDTOService.getContentById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        ContentDTO contentDTO = contentDTOService.getContentById(id);

        if (contentDTO.getCreateUserId() != user.getId()) {
            return ResponseEntity.status(405).body("U r not a Content creator");
        }

        contentDTO.setTitle(title);
        contentDTO.setContent(content);

        if (tags == null) {
            return ResponseEntity.ok(contentDTOService.updateContent(contentDTO));
        }

        List<String> tagList = List.copyOf(tags);
        ArrayList<TagModel> nList = new ArrayList<>();

        for (int i = 0; i < tagList.size(); i++) {
            TagModel nModel;
            if (i < contentDTO.getTagList().size()) {
                nModel = contentDTO.getTagList().get(i);
            }
            else {
                nModel = new TagModel();
            }

            nModel.setTagName(tagList.get(i));
            nList.add(nModel);
        }
        contentDTO.setTagList(nList);

        ContentModel contentModel = contentDTOService.updateContent(contentDTO);

        return ResponseEntity.ok(contentModel);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "404", description = "not found data")
    })
    @Parameters({
            @Parameter(name = "token", description = "token Value"),
            @Parameter(name = "id", description = "content id Value"),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity deletePath(@NotEmpty @NotNull String token, @PathVariable("id") Long id) {
        UserModel user = jwtTokenProvider.getUserWithValidation(token);

        if (user == null) {
            Pair<Integer, String> validChecker = jwtTokenProvider.isValidUserWithMessage(token);
            return ResponseEntity.status(validChecker.a).body(validChecker.b);
        }

        if (user.getUserRole() < AuthConstant.PERMISSION_DELETE) {
            return ResponseEntity.badRequest().body("permission denied");
        }

        if (contentDTOService.getContentById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        contentDTOService.deleteContentById(id);
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
        List<ContentDTO> contents = contentDTOService.getContentsById(ids);

        if (contents.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(contents);
    }

}
