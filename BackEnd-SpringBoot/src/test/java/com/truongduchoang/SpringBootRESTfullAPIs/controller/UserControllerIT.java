package com.truongduchoang.SpringBootRESTfullAPIs.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
import com.truongduchoang.SpringBootRESTfullAPIs.dto.request.UserCreateRequest;
import com.truongduchoang.SpringBootRESTfullAPIs.dto.request.UserUpdateRequest;
import com.truongduchoang.SpringBootRESTfullAPIs.dto.response.UserResponse;
import com.truongduchoang.SpringBootRESTfullAPIs.models.ApiResponse;
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
        public void createApiUser_shouldReturnUser_whenUserValid() throws Exception {
                UserCreateRequest inputUser = new UserCreateRequest();
                inputUser.setFullName("api user");
                inputUser.setEmail("api@gmail.com");
                inputUser.setPhone("0909000000");

                String resultStr = mockMvc.perform(
                                post("/api/users")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsBytes(inputUser)))
                                .andExpect(status().isCreated())
                                .andReturn().getResponse().getContentAsString();

                ApiResponse<UserResponse> response = objectMapper.readValue(
                                resultStr,
                                new TypeReference<ApiResponse<UserResponse>>() {
                                });

                assertEquals("Success", response.getStatus(), "Status phải là 'success'");
                assertNotNull(response.getMessage(), "Message không được null");
                assertNotNull(response.getData(), "Data không được null");
                assertNull(response.getErrorCode());
                assertEquals(inputUser.getFullName(), response.getData().getFullName(), "Tên user không khớp");
                assertEquals(inputUser.getEmail(), response.getData().getEmail(), "Email user không khớp");
                assertNotNull(response.getTimeStamp(), "TimeStamp không được null");
        }

        @Test
        public void createApiUser_shouldReturnError_whenEmailInvalid() throws Exception {
                UserCreateRequest inputUser = new UserCreateRequest();
                inputUser.setFullName("api user");
                inputUser.setEmail("invalid-email");

                String resultStr = mockMvc.perform(
                                post("/api/users")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsBytes(inputUser)))
                                .andExpect(status().isBadRequest())
                                .andReturn().getResponse().getContentAsString();

                ApiResponse<UserResponse> response = objectMapper.readValue(
                                resultStr,
                                new TypeReference<ApiResponse<UserResponse>>() {
                                });

                assertEquals("Error", response.getStatus(), "Status phải là error");
                assertNull(response.getData(), "Data phải là null khi lỗi");
                assertEquals("VALIDATION_ERROR", response.getErrorCode(), "ErrorCode không đúng");
        }

        @Test
        public void getAllApiUsers_shouldReturnListUser() throws Exception {
                User u1 = new User(null, "api1@gmail.com", "api user 1");
                User u2 = new User(null, "api2@gmail.com", "api user 2");
                this.userRepository.saveAll(List.of(u1, u2));

                String resultStr = this.mockMvc.perform(get("/api/users"))
                                .andExpect(status().isOk())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();

                ApiResponse<List<UserResponse>> response = this.objectMapper.readValue(
                                resultStr,
                                new TypeReference<ApiResponse<List<UserResponse>>>() {
                                });

                assertEquals("Success", response.getStatus(), "Status phải là 'Success'");
                assertNotNull(response.getData(), "Data không được null");
                assertEquals(2, response.getData().size(), "Số lượng user không đúng");
                assertEquals("api1@gmail.com", response.getData().get(0).getEmail(), "Email user 1 không đúng");
        }

        @Test
        public void getApiUserById_shouldReturnUser_whenUserExist() throws Exception {
                User u1 = new User(null, "api3@gmail.com", "api name get by id");
                User inputUser = this.userRepository.saveAndFlush(u1);

                String resultStr = this.mockMvc.perform(get("/api/users/{id}", inputUser.getId()))
                                .andExpect(status().isOk())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();

                ApiResponse<UserResponse> response = this.objectMapper.readValue(
                                resultStr,
                                new TypeReference<ApiResponse<UserResponse>>() {
                                });

                assertEquals("Success", response.getStatus(), "Status phải là 'Success'");
                assertNotNull(response.getData(), "Data không được null");
                assertEquals("api name get by id", response.getData().getFullName(), "Tên user không đúng");
        }

        @Test
        public void updateApiUser_shouldReturnUser_whenValid() throws Exception {
                User user = new User(null, "apiold@gmail.com", "old api name");
                User inputUser = this.userRepository.saveAndFlush(user);

                UserUpdateRequest updateUser = new UserUpdateRequest();
                updateUser.setFullName("new api name");
                updateUser.setEmail("apinew@gmail.com");

                String resultStr = mockMvc.perform(
                                put("/api/users/{id}", inputUser.getId())
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsBytes(updateUser)))
                                .andExpect(status().isOk())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();

                ApiResponse<UserResponse> response = objectMapper.readValue(
                                resultStr,
                                new TypeReference<ApiResponse<UserResponse>>() {
                                });

                assertEquals("Success", response.getStatus(), "Status phải là 'success'");
                assertNotNull(response.getData(), "Data không được null!");
                assertEquals("new api name", response.getData().getFullName(), "Tên user không khớp");
                assertEquals("apinew@gmail.com", response.getData().getEmail(), "Email user không khớp");
        }

        @Test
        public void deleteApiUser_shouldDeleteUser_whenUserExist() throws Exception {
                User u = new User(null, "apidelete@gmail.com", "delete api name");
                User inputUser = this.userRepository.saveAndFlush(u);

                this.mockMvc.perform(
                                delete("/api/users/{id}", inputUser.getId())
                                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNoContent());

                long countDB = this.userRepository.count();
                assertEquals(0, countDB, "Database phải không còn user sau khi xoá");
        }

        @Test
        public void deleteApiUser_shouldReturnError_whenUserNotFound() throws Exception {
                long nonExistentId = 999L;

                String resultStr = this.mockMvc.perform(
                                delete("/api/users/{id}", nonExistentId)
                                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();

                ApiResponse<Void> response = objectMapper.readValue(
                                resultStr,
                                new TypeReference<ApiResponse<Void>>() {
                                });

                assertEquals("Error", response.getStatus(), "Status phải là error");
                assertEquals("USER_NOT_FOUND", response.getErrorCode(), "ErrorCode không đúng");
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

                ApiResponse<User> response = objectMapper.readValue(
                                resultStr,
                                new TypeReference<ApiResponse<User>>() {
                                });

                assertEquals("Success", response.getStatus(), "Status phải là 'success'");
                assertNotNull(response.getMessage(), "Message không được null");
                assertTrue(response.getData() instanceof User,
                                "Data phải là User, nhưng nhận được: " + response.getData().getClass().getSimpleName());
                assertNull(response.getErrorCode());

                assertEquals(inputUser.getName(), response.getData().getName(), "Tên user không khớp");
                assertEquals(inputUser.getEmail(), response.getData().getEmail(), "Email user không khớp");
                assertNull(response.getErrorCode(), "Error Code phải là null khi thành công!");
                assertNotNull(response.getTimeStamp(), "TimeStamp không được null");
        }

        @Test
        public void getAllUsers() throws Exception {
                User u1 = new User(null, "demo3@gmail.com", "duc hoang u1");
                User u2 = new User(null, "demo4@gmail.com", "duc hoang u2");
                this.userRepository.saveAll(List.of(u1, u2));

                String resultStr = this.mockMvc.perform(get("/users"))
                                .andExpect(status().isOk())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();

                ApiResponse<List<User>> response = this.objectMapper.readValue(
                                resultStr,
                                new TypeReference<ApiResponse<List<User>>>() {
                                });

                assertEquals("Success", response.getStatus(), "Status phải là 'Success'");
                assertNotNull(response.getMessage(), "Message không được null");
                assertNotNull(response.getData(), "Data không được null");
                assertNull(response.getErrorCode(), "ErrorCode phải là null");
                assertNotNull(response.getTimeStamp(), "TimeStamp không được null");
                assertEquals(2, response.getData().size(), "Số lượng user không đúng");
                assertEquals("demo3@gmail.com", response.getData().get(0).getEmail(), "Email user 1 không đúng");
                assertEquals("demo4@gmail.com", response.getData().get(1).getEmail(), "Email user 2 không đúng");
        }

        @Test
        public void getUserById() throws Exception {
                User u1 = new User(null, "demo3@gmail.com", "name get by id");
                User inputUser = this.userRepository.saveAndFlush(u1);

                String resultStr = this.mockMvc.perform(get("/users/{id}", inputUser.getId()))
                                .andExpect(status().isOk())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();

                ApiResponse<User> response = this.objectMapper.readValue(
                                resultStr,
                                new TypeReference<ApiResponse<User>>() {
                                });

                assertEquals("Success", response.getStatus(), "Status phải là 'Success'");
                assertNotNull(response.getMessage(), "Message không được null");
                assertNotNull(response.getData(), "Data không được null");
                assertNull(response.getErrorCode(), "ErrorCode phải là null");
                assertNotNull(response.getTimeStamp(), "TimeStamp không được null");
                assertEquals("name get by id", response.getData().getName(), "Tên user không đúng");
                assertEquals("demo3@gmail.com", response.getData().getEmail(), "Email user không đúng");
        }

        @Test
        public void getUserById_shouldReturnError_whenIdNotFound() throws Exception {
                long nonExistentId = 0L;

                String resultStr = this.mockMvc.perform(get("/users/{id}", nonExistentId))
                                .andExpect(status().isNotFound())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();

                ApiResponse<User> response = this.objectMapper.readValue(
                                resultStr,
                                new TypeReference<ApiResponse<User>>() {
                                });

                assertEquals("Error", response.getStatus(), "Status phải là error");
                assertNotNull(response.getMessage(), "Message không được null");
                assertNull(response.getData(), "Data phải là null khi lỗi");
                assertEquals("USER_NOT_FOUND", response.getErrorCode(), "ErrorCode không đúng");
                assertNotNull(response.getTimeStamp(), "Timestamp không được null!");
        }

        @Test
        public void updateUser() throws Exception {
                User user = new User(null, "oldGmail@gmail.com", "old name");
                User inputUser = this.userRepository.saveAndFlush(user);

                User updateUser = new User(inputUser.getId(), "newGmail@gmail.com", "new name");

                String resultStr = mockMvc.perform(
                                put("/users/{id}", inputUser.getId())
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsBytes(updateUser)))
                                .andExpect(status().isOk())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();

                ApiResponse<User> response = objectMapper.readValue(
                                resultStr,
                                new TypeReference<ApiResponse<User>>() {
                                });

                assertEquals("Success", response.getStatus(), "Status phải là 'success'");
                assertNotNull(response.getMessage(), "Message không được null");
                assertNotNull(response.getData(), "Data không được null!");
                assertTrue(response.getData() instanceof User,
                                "Data phải là User, nhưng nhận được: " + response.getData().getClass().getSimpleName());
                assertEquals("new name", response.getData().getName(), "Tên user không khớp");
                assertEquals("newGmail@gmail.com", response.getData().getEmail(), "Email user không khớp");
                assertNull(response.getErrorCode(), "ErrorCode phải là null khi thành công!");
                assertNotNull(response.getTimeStamp(), "TimeStamp không được null");
        }

        @Test
        public void updateUser_shouldReturnError_whenIdNotFound() throws Exception {
                long nonExistentId = 999L;
                User updateUser = new User(nonExistentId, "newGmail@gmail.com", "new name");

                String resultStr = mockMvc.perform(
                                put("/users/{id}", nonExistentId)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsBytes(updateUser)))
                                .andExpect(status().isNotFound())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();

                ApiResponse<User> response = objectMapper.readValue(
                                resultStr,
                                new TypeReference<ApiResponse<User>>() {
                                });

                assertEquals("Error", response.getStatus(), "Status phải là 'error'");
                assertNotNull(response.getMessage(), "Message không được null");
                assertNull(response.getData(), "Data phải là null khi lỗi");
                assertEquals("USER_NOT_FOUND", response.getErrorCode(), "ErrorCode không đúng");
                assertNotNull(response.getTimeStamp(), "TimeStamp không được null!");
        }

        @Test
        public void deleteUser() throws Exception {
                User u = new User(null, "delete@gmail.com", "delete name");
                User inputUser = this.userRepository.saveAndFlush(u);

                String resultStr = this.mockMvc.perform(
                                delete("/users/{id}", inputUser.getId())
                                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNoContent())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();

                if (!resultStr.isEmpty()) {
                        ApiResponse<Void> response = objectMapper.readValue(
                                        resultStr,
                                        new TypeReference<ApiResponse<Void>>() {
                                        });

                        assertEquals("Success", response.getStatus(), "Status phải là 'success'");
                        assertNotNull(response.getMessage(), "Message không được null");
                        assertNull(response.getData(), "Data phải là null khi xoá");
                        assertNull(response.getErrorCode(), "ErrorCode phải là null khi thành công");
                        assertNotNull(response.getTimeStamp(), "TimeStamp không được null!");
                }

                long countDB = this.userRepository.count();
                assertEquals(0, countDB, "Database phải không còn user sau khi xoá");
        }

        @Test
        public void deleteUser_shouldReturnError_whenIdNotFound() throws Exception {
                long nonExistentId = 999L;

                this.mockMvc.perform(
                                delete("/users/{id}", nonExistentId)
                                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isInternalServerError());
        }
}
