package com.truongduchoang.SpringBootRESTfullAPIs.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.truongduchoang.SpringBootRESTfullAPIs.models.User;
import com.truongduchoang.SpringBootRESTfullAPIs.repository.UserRepository;

@IntegrationTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void init() {
        this.userRepository.deleteAll();
    }

    @Test
    public void createUser_shouldReturnUser_whenUserValid() throws Exception {
        User inputUser = new User(null, "demo9@gmail.com", "duc hoang");

        String resultStr = mockMvc.perform(
                post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(inputUser)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        System.out.println("resultStr: " + resultStr);

        User outputUser = objectMapper.readValue(resultStr, User.class);
        assertEquals(inputUser.getName(), outputUser.getName());
    }

    @Test
    public void getAllUsers() throws Exception {
        User u1 = new User(null, "demo3@gmail.com", "duc hoang u1");
        User u2 = new User(null, "demo4@gmail.com", "duc hoang u2");
        List<User> listUser = List.of(u1, u2);
        this.userRepository.saveAll(listUser);

        String resultStr = this.mockMvc.perform(get("/users")).andExpect(status().isOk()).andReturn().getResponse()
                .getContentAsString();

        List<User> resultListUser = this.objectMapper.readValue(resultStr, new TypeReference<List<User>>() {
        });

        assertEquals(2, resultListUser.size());
        assertEquals("demo3@gmail.com", resultListUser.get(0).getEmail());
    }

    @Test
    public void getUserById() throws Exception {
        this.userRepository.deleteAll();
        User u1 = new User(null, "demo3@gmail.com", "name get by id");
        User inputUser = this.userRepository.saveAndFlush(u1);

        String resultStr = this.mockMvc.perform(get("/users/{id}", inputUser.getId())).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        User outputUser = this.objectMapper.readValue(resultStr, User.class);

        assertEquals("name get by id", outputUser.getName());
    }

    @Test
    public void getUserById_shouldEmpty_whenIdNotFound() throws Exception {
        this.mockMvc.perform(get("/users/{id}", 0)).andExpect(status().isNotFound());
    }

    @Test
    public void updateUser() throws Exception {
        this.userRepository.deleteAll();
        User user = new User(null, "oldGmail@gmail.com", "old name");
        User inputUser = this.userRepository.saveAndFlush(user);

        User updateUser = new User(inputUser.getId(), "newGmail@gmail.com", "new name");

        String resultStr = mockMvc.perform(
                put("/users/{id}", inputUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(updateUser)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        System.out.println("resultStr: " + resultStr);

        User outputUser = objectMapper.readValue(resultStr, User.class);
        assertEquals("new name", outputUser.getName());
    }

    @Test
    public void deleteUser() throws Exception {
        this.userRepository.deleteAll();
        User u = new User(null, "delete@gmail.com", "delete name");
        User inputUser = this.userRepository.saveAndFlush(u);

        this.mockMvc.perform(delete("/users/{id}", inputUser.getId()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        long countDB = this.userRepository.count();

        assertEquals(0, countDB);
    }
}
