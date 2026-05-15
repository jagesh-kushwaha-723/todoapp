package backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TodoResponse {
    private Long id;
    private String title;
    private Boolean done;
    private Integer orderIndex;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;
}