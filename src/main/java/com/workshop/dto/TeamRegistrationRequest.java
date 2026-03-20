package com.workshop.dto;

import lombok.Data;
import java.util.List;

@Data
public class TeamRegistrationRequest {
    private Integer teamMembersCount = 1;

    // We can accept a list of Maps or just a list of Strings (JSON stringified from frontend, or raw object)
    // To make it structured, let's accept a JSON string or a List of generic objects.
    // If frontend sends an array of objects `[{name: '...', roll: '...'}]`, it maps to List<TeamMember>.
    private List<TeamMember> teamMembersList;

    @Data
    public static class TeamMember {
        private String name;
        private String rollNumber;
    }
}
