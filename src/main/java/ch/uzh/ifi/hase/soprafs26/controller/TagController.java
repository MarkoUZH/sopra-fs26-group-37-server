package ch.uzh.ifi.hase.soprafs26.controller;

import ch.uzh.ifi.hase.soprafs26.rest.dto.TagGetDTO;
import ch.uzh.ifi.hase.soprafs26.rest.dto.TagPostDTO;
import ch.uzh.ifi.hase.soprafs26.rest.mapper.TagDTOMapper;
import ch.uzh.ifi.hase.soprafs26.service.UserService;


import ch.uzh.ifi.hase.soprafs26.entity.Tag;
import ch.uzh.ifi.hase.soprafs26.service.TagService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TagController {

    private final TagService tagService;
    private final UserService userService;

    TagController(TagService tagService, UserService userService) {
        this.tagService = tagService;
        this.userService = userService;
    }

    @GetMapping("/tags")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<TagGetDTO> getAllTags(
            @RequestHeader(value = "Authorization", required = false) String token) {

        

        return tagService.getTags().stream()
                .map(TagDTOMapper.INSTANCE::convertEntityToTagGetDTO)
                .toList();
    }

    @GetMapping("/tags/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public TagGetDTO getTagById(
            @RequestHeader(value = "Authorization", required = false) String token,
            @PathVariable Long id) {

        

        Tag tag = tagService.getTagById(id);
        return TagDTOMapper.INSTANCE.convertEntityToTagGetDTO(tag);
    }

    @PostMapping("/tags")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public TagGetDTO createTag(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody TagPostDTO tagPostDTO) {

        

        Tag tag = TagDTOMapper.INSTANCE.convertTagPostDTOtoEntity(tagPostDTO);
        Tag createdTag = tagService.createTag(tag);
        return TagDTOMapper.INSTANCE.convertEntityToTagGetDTO(createdTag);
    }

    @PutMapping("/tags/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public TagGetDTO updateTag(
            @RequestHeader(value = "Authorization", required = false) String token,
            @PathVariable Long id,
            @RequestBody TagPostDTO tagPostDTO) {

        

        Tag tag = TagDTOMapper.INSTANCE.convertTagPostDTOtoEntity(tagPostDTO);
        Tag updatedTag = tagService.updateTag(id, tag);
        return TagDTOMapper.INSTANCE.convertEntityToTagGetDTO(updatedTag);
    }

    @DeleteMapping("/tags/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public TagGetDTO deleteTag(
            @RequestHeader(value = "Authorization", required = false) String token,
            @PathVariable Long id) {

        

        return TagDTOMapper.INSTANCE.convertEntityToTagGetDTO(tagService.deleteTag(id));
    }
}
