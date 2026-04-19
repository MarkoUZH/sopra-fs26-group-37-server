package ch.uzh.ifi.hase.soprafs26.rest.dto;
import java.util.List;

public class ProjectPutDTO {
    private String name;
    private String description;
    private List<Long> memberIds;

        public List<Long> getMemberIds() {
            return memberIds;
        }   

    public void setMemberIds(List<Long> memberIds) {    
        this.memberIds = memberIds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
