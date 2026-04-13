package ch.uzh.ifi.hase.soprafs26.controller;

import ch.uzh.ifi.hase.soprafs26.service.UserService;


import ch.uzh.ifi.hase.soprafs26.entity.Tag;
import ch.uzh.ifi.hase.soprafs26.service.TagService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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
    public List<Tag> getAllTags(@RequestHeader(value = "Authorization", required = false) String token) {
        
        userService.verifyToken(token);

        return null;
    }
}
