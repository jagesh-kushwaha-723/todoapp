package backend.repository;

import backend.model.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.time.LocalDateTime;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findByUserIdAndDoneFalseOrderByOrderIndexAsc(Long userId);
    List<Todo> findByUserIdAndDoneTrueOrderByCompletedAtDesc(Long userId);
    long countByUserIdAndDoneFalse(Long userId);
    List<Todo> findByDoneTrueAndCompletedAtBefore(LocalDateTime dateTime);
}