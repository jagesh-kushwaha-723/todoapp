package backend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TodoResponse {
    private Long id;
    private String title;
    private Boolean done;
    private Integer orderIndex;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;

    public TodoResponse(Long id, String title, Boolean done, 
                        Integer orderIndex, LocalDateTime completedAt, 
                        LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.done = done;
        this.orderIndex = orderIndex;
        this.completedAt = completedAt;
        this.createdAt = createdAt;
    }
}