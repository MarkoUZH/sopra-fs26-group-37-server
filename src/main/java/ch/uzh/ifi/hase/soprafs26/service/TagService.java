package ch.uzh.ifi.hase.soprafs26.service;

import ch.uzh.ifi.hase.soprafs26.repository.TagRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TagService {

	private final Logger log = LoggerFactory.getLogger(TagService.class);

	private final TagRepository tagRepository;

	public TagService(@Qualifier("tagRepository") TagRepository tagRepository) {
		this.tagRepository = tagRepository;
	}
}
