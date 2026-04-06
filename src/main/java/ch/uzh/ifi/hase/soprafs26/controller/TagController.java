package ch.uzh.ifi.hase.soprafs26.controller;

import ch.uzh.ifi.hase.soprafs26.entity.Tag;
import ch.uzh.ifi.hase.soprafs26.service.TagService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TagController {

	private final TagService tagService;

	TagController(TagService tagService) {
		this.tagService = tagService;
	}

    @GetMapping("/tags")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<Tag> getAllTags() {
        return null;
    }
}
