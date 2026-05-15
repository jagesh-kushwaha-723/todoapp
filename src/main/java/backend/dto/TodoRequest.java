package backend.dto;

import lombok.Data;

@Data
public class TodoRequest {
    private String title;
    private Boolean done;
    private Integer orderIndex;
}