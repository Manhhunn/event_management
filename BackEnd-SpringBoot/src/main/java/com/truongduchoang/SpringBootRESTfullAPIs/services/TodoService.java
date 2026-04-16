package com.truongduchoang.SpringBootRESTfullAPIs.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.truongduchoang.SpringBootRESTfullAPIs.models.Todo;
import com.truongduchoang.SpringBootRESTfullAPIs.repository.TodoRepository;

@Service
public class TodoService {
    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository){
        this.todoRepository = todoRepository;
    }

    public Todo handleCreateTodo(Todo todo){
        System.out.printf(">>> Run todo service!!!!!!: %s", todo.toString());
        Todo newTodo = this.todoRepository.save(todo);
        return newTodo;
    }

    public Todo handleGetTodoById(long id){
        Optional<Todo> optionalTodo = this.todoRepository.findById(id);
        return optionalTodo.isPresent() ?  optionalTodo.get() : null;
    }

    public List<Todo> handleGetAllTodos(){
        return this.todoRepository.findAll();
    }

    public Todo handleUpdateTodoUsingPUT(long id, Todo inputTodo){
        Optional<Todo> optionalTodo = this.todoRepository.findById(id);
        if(optionalTodo.isPresent()){
            Todo currentTodo = optionalTodo.get();
            currentTodo.setCompleted(inputTodo.isCompleted());
            currentTodo.setUserName(inputTodo.getUserName());
            this.todoRepository.save(currentTodo);
            return currentTodo;
        }else{
            return null;
        }
    }

    public Todo handleUpdateTodoUsingPATCH(long id, Todo inputTodo){
        Todo existTodo = this.todoRepository.findById(id).orElse(null);
        if(existTodo != null){
            if(inputTodo.isCompleted() != null){
                existTodo.setCompleted(inputTodo.isCompleted());
            }

            if(inputTodo.getUserName() != null){
                existTodo.setUserName(inputTodo.getUserName());
            }

            this.todoRepository.save(existTodo);
            return existTodo;
        }else{
            return null;
        }
    }

    public void handleDeleteTodo(Long id){
        this.todoRepository.deleteById(id);
    }
}
