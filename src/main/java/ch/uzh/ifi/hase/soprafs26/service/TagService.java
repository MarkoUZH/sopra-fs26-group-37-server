package ch.uzh.ifi.hase.soprafs26.service;

import ch.uzh.ifi.hase.soprafs26.entity.Tag;
import ch.uzh.ifi.hase.soprafs26.repository.TagRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class TagService {

    private final Logger log = LoggerFactory.getLogger(TagService.class);

    private final TagRepository tagRepository;

    public TagService(@Qualifier("tagRepository") TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public List<Tag> getTags() {
        return tagRepository.findAll();
    }

    public Tag getTagById(Long id) {
        log.debug("Fetching tag with id {}", id);
        return tagRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Tag with id " + id + " not found"));
    }

    public Tag createTag(Tag tag) {
        log.debug("Creating tag with name {}", tag.getName());
        validateTagName(tag);
        Tag savedTag = tagRepository.save(tag);
        tagRepository.flush();
        return savedTag;
    }

    public Tag updateTag(Long id, Tag updatedTag) {
        log.debug("Updating tag with id {}", id);
        Tag existingTag = getTagById(id);

        if (updatedTag.getName() != null) {
            existingTag.setName(updatedTag.getName());
        }
        if (updatedTag.getProject() != null) {
            existingTag.setProject(updatedTag.getProject());
        }
        if (updatedTag.getTasks() != null) {
            existingTag.setTasks(updatedTag.getTasks());
        }

        Tag savedTag = tagRepository.save(existingTag);
        tagRepository.flush();
        return savedTag;
    }

    public Tag deleteTag(Long id) {
        log.debug("Deleting tag with id {}", id);
        Tag deletedTag = getTagById(id);
        tagRepository.deleteById(id);

        return deletedTag;
    }

    private void validateTagName(Tag tag) {
        if (tag.getName() == null || tag.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Tag name must not be empty");
        }
    }
}
