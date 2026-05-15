package backend.service;

import backend.dto.TodoRequest;
import backend.dto.TodoResponse;
import backend.model.Todo;
import backend.model.User;
import backend.repository.TodoRepository;
import backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    // Get all todos for a user
    public List<TodoResponse> getTodos(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Todo> active = todoRepository
                .findByUserIdAndDoneFalseOrderByOrderIndexAsc(user.getId());
        List<Todo> completed = todoRepository
                .findByUserIdAndDoneTrueOrderByCompletedAtDesc(user.getId());

        // combine: active first, completed at bottom
        active.addAll(completed);
        return active.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Create a new todo
    public TodoResponse createTodo(String email, TodoRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // enforce 50 active todo limit
        long activeCount = todoRepository.countByUserIdAndDoneFalse(user.getId());
        if (activeCount >= 50) {
            throw new RuntimeException("Active todo limit of 50 reached");
        }

        // set order_index to bottom of active list
        int orderIndex = (int) activeCount;

        Todo todo = new Todo();
        todo.setTitle(request.getTitle());
        todo.setDone(false);
        todo.setOrderIndex(orderIndex);
        todo.setUser(user);

        return toResponse(todoRepository.save(todo));
    }

    // Update a todo (edit title or mark done/undone)
    public TodoResponse updateTodo(Long id, String email, TodoRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found"));

        // make sure this todo belongs to this user
        if (!todo.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        // update title if provided
        if (request.getTitle() != null) {
            todo.setTitle(request.getTitle());
        }

        // handle done/undone toggle
        if (request.getDone() != null) {
            if (request.getDone() && !todo.isDone()) {
                // marking as done
                todo.setDone(true);
                todo.setCompletedAt(LocalDateTime.now());
                todo.setOrderIndex(null);
            } else if (!request.getDone() && todo.isDone()) {
                // marking as undone — goes to bottom of active list
                long activeCount = todoRepository
                        .countByUserIdAndDoneFalse(user.getId());
                todo.setDone(false);
                todo.setCompletedAt(null);
                todo.setOrderIndex((int) activeCount);
            }
        }

        return toResponse(todoRepository.save(todo));
    }

    // Delete a todo
    public void deleteTodo(Long id, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found"));

        if (!todo.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        todoRepository.delete(todo);
    }

    // Save new order after drag and drop
    public void reorderTodos(String email, List<Long> orderedIds) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        for (int i = 0; i < orderedIds.size(); i++) {
            Todo todo = todoRepository.findById(orderedIds.get(i))
                    .orElseThrow(() -> new RuntimeException("Todo not found"));

            if (!todo.getUser().getId().equals(user.getId())) {
                throw new RuntimeException("Unauthorized");
            }

            todo.setOrderIndex(i);
            todoRepository.save(todo);
        }
    }

    // Auto-delete completed todos older than 30 days
    // Runs every day at midnight
    @Scheduled(cron = "0 0 0 * * *")
    public void deleteOldCompletedTodos() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(30);
        List<Todo> old = todoRepository.findByDoneTrueAndCompletedAtBefore(cutoff);
        todoRepository.deleteAll(old);
    }

    // Convert Todo entity to TodoResponse DTO
    private TodoResponse toResponse(Todo todo) {
        TodoResponse response = new TodoResponse();
        response.setId(todo.getId());
        response.setTitle(todo.getTitle());
        response.setDone(todo.isDone());
        response.setOrderIndex(todo.getOrderIndex());
        response.setCompletedAt(todo.getCompletedAt());
        response.setCreatedAt(todo.getCreatedAt());
        return response;
    }
}