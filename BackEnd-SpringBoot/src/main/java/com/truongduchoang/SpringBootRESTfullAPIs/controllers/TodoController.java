package com.truongduchoang.SpringBootRESTfullAPIs.controllers;


import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.truongduchoang.SpringBootRESTfullAPIs.models.Todo;
import com.truongduchoang.SpringBootRESTfullAPIs.services.TodoService;

@RestController
public class TodoController {
    private final TodoService todoService;

    public TodoController(TodoService todoService){
        this.todoService = todoService;
    }

    @PostMapping("/todos")
    public ResponseEntity<Todo> createTodo(@RequestBody Todo inputTodo){
        Todo newTodo = this.todoService.handleCreateTodo(inputTodo);
        return ResponseEntity.status(HttpStatus.CREATED).body(newTodo);
    }

    @GetMapping("/todos/{id}")
    public ResponseEntity<Todo> getTodoById(@PathVariable("id") long idTodo){
        Todo newTodo = this.todoService.handleGetTodoById(idTodo);
        return ResponseEntity.ok().body(newTodo);
    }

    @GetMapping("/todos")
    public ResponseEntity<List<Todo>> getAllTodos(){
        List<Todo> listTodo = this.todoService.handleGetAllTodos();
        return ResponseEntity.ok().body(listTodo);
    }

    @PutMapping("/todos/{id}")
    public ResponseEntity<Todo> updateTodoUsingPUT(@PathVariable long id, @RequestBody Todo updateTodo){
        Todo updatedTodo = this.todoService.handleUpdateTodoUsingPUT(id, updateTodo);
        return ResponseEntity.ok().body(updatedTodo);
    }

    @PatchMapping("/todos/{id}")
    public ResponseEntity<Todo> updateTodoUsingPATCH(@PathVariable long id, @RequestBody Todo updateTodo){
        Todo updatedTodo = this.todoService.handleUpdateTodoUsingPATCH(id, updateTodo);
        return ResponseEntity.ok().body(updatedTodo);
    }

    @DeleteMapping("/todos/{id}")
    public ResponseEntity<String> deleteTodo(@PathVariable long id){
        this.todoService.handleDeleteTodo(id);
        return ResponseEntity.ok().body("Success delete todo with id = " + id);
    }
}
