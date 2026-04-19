package ch.uzh.ifi.hase.soprafs26.rest.mapper;

import ch.uzh.ifi.hase.soprafs26.entity.Tag;
import ch.uzh.ifi.hase.soprafs26.entity.Task;
import ch.uzh.ifi.hase.soprafs26.rest.dto.TagGetDTO;
import ch.uzh.ifi.hase.soprafs26.rest.dto.TagPostDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface TagDTOMapper {
    TagDTOMapper INSTANCE = Mappers.getMapper(TagDTOMapper.class);

    @Mapping(source = "name", target = "name")
    @Mapping(source = "projectId", target = "project.id")
    @Mapping(source = "taskIds", target = "tasks")
    Tag convertTagPostDTOtoEntity(TagPostDTO tagPostDTO);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "tasks", target = "taskIds")
    TagGetDTO convertEntityToTagGetDTO(Tag tag);

    default List<Long> tasksToTaskIds(List<Task> tasks) {
        if (tasks == null) return null;
        return tasks.stream().map(Task::getId).toList();
    }

    default List<Task> taskIdsToTasks(List<Long> taskIds) {
        if (taskIds == null) return null;
        return taskIds.stream().map(id -> {
            Task task = new Task();
            task.setId(id);
            return task;
        }).toList();
    }
}
