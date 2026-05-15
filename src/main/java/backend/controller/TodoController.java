package backend.controller;

import backend.dto.TodoRequest;
import backend.dto.TodoResponse;
import backend.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @GetMapping
    public ResponseEntity<List<TodoResponse>> getTodos(
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(todoService.getTodos(email));
    }

    @PostMapping
    public ResponseEntity<TodoResponse> createTodo(
            @AuthenticationPrincipal String email,
            @RequestBody TodoRequest request) {
        return ResponseEntity.ok(todoService.createTodo(email, request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TodoResponse> updateTodo(
            @AuthenticationPrincipal String email,
            @PathVariable Long id,
            @RequestBody TodoRequest request) {
        return ResponseEntity.ok(todoService.updateTodo(id, email, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(
            @AuthenticationPrincipal String email,
            @PathVariable Long id) {
        todoService.deleteTodo(id, email);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/reorder")
    public ResponseEntity<Void> reorderTodos(
            @AuthenticationPrincipal String email,
            @RequestBody List<Long> orderedIds) {
        todoService.reorderTodos(email, orderedIds);
        return ResponseEntity.ok().build();
    }
}