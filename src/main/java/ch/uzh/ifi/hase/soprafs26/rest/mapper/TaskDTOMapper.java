package ch.uzh.ifi.hase.soprafs26.rest.mapper;

import ch.uzh.ifi.hase.soprafs26.entity.Tag;
import ch.uzh.ifi.hase.soprafs26.entity.Task;
import ch.uzh.ifi.hase.soprafs26.entity.User;
import ch.uzh.ifi.hase.soprafs26.rest.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface TaskDTOMapper {
	TaskDTOMapper INSTANCE = Mappers.getMapper(TaskDTOMapper.class);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "tags", target = "tags")
    @Mapping(source = "assignedUsers", target = "assignedUsers")
    @Mapping(source = "priority", target = "priority")
    @Mapping(source = "dueDate", target = "dueDate")
    @Mapping(source = "timeEstimate", target = "timeEstimate")
    @Mapping(source = "sprint", target = "sprint")
    @Mapping(source = "project", target = "project")
    @Mapping(source = "acceptanceCriteria", target = "acceptanceCriteria")
    @Mapping(source = "status", target = "status")
    TaskGetDTO convertEntityToTaskGetDTO(Task task);

    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "priority", target = "priority")
    @Mapping(source = "dueDate", target = "dueDate")
    @Mapping(source = "timeEstimate", target = "timeEstimate")
    @Mapping(source = "acceptanceCriteria", target = "acceptanceCriteria")
    @Mapping(source = "sprintId", target = "sprint.id")
    @Mapping(source = "projectId", target = "project.id")
    @Mapping(source = "tagIds", target = "tags", qualifiedByName = "idsToTags")
    @Mapping(source = "assignedUserIds", target = "assignedUsers", qualifiedByName = "idsToUsers")
    Task convertTaskPostDTOtoEntity(TaskPostDTO taskPostDTO);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "priority", target = "priority")
    @Mapping(source = "dueDate", target = "dueDate")
    @Mapping(source = "timeEstimate", target = "timeEstimate")
    @Mapping(source = "acceptanceCriteria", target = "acceptanceCriteria")
    @Mapping(source = "sprintId", target = "sprint.id")
    @Mapping(source = "tagIds", target = "tags", qualifiedByName = "idsToTags")
    @Mapping(source = "assignedUserIds", target = "assignedUsers", qualifiedByName = "idsToUsers")
    Task convertTaskPutDTOtoEntity(TaskPutDTO taskPutDTO);

    @Named("tagsToIds")
    default List<Long> tagsToIds(List<Tag> tags) {
        return tags == null ? null : tags.stream().map(Tag::getId).toList();
    }

    @Named("usersToIds")
    default List<Long> usersToIds(List<User> users) {
        return users == null ? null : users.stream().map(User::getId).toList();
    }

    @Named("idsToTags")
    default List<Tag> idsToTags(List<Long> ids) {
        return ids == null ? null : ids.stream().map(id -> {
            Tag tag = new Tag();
            tag.setId(id);
            return tag;
        }).toList();
    }

    @Named("idsToUsers")
    default List<User> idsToUsers(List<Long> ids) {
        return ids == null ? null : ids.stream().map(id -> {
            User user = new User();
            user.setId(id);
            return user;
        }).toList();
    }
}
