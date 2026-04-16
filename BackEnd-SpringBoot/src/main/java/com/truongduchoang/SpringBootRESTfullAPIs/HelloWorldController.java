package com.truongduchoang.SpringBootRESTfullAPIs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.truongduchoang.SpringBootRESTfullAPIs.models.Todo;
import com.truongduchoang.SpringBootRESTfullAPIs.models.User;
import com.fasterxml.jackson.core.JsonProcessingException;



@RestController
public class HelloWorldController{

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/")
    public ResponseEntity<String> index(){
        try{
            String json = """
                {
                    "name": "truong duc hoang",
                    "email": "demo1@gmail.com"
                }
                """;

        User testUser = objectMapper.readValue(json, User.class);

        User newUser = new User(null, "demojson@gmail.com", "Hoang json");
        String newJson = objectMapper.writeValueAsString(newUser);
        return ResponseEntity.ok().body(newJson);
        }catch(JsonProcessingException e){
            return ResponseEntity.internalServerError().body("loi xu ly JSON: " + e.getMessage());
        }
    }

    @GetMapping("/demo")
    public ResponseEntity<Todo> demo(){
        Todo testTodo = new Todo("Tran Dinh Phi Hung", true);
        return ResponseEntity.ok().body(testTodo);
    }
}
