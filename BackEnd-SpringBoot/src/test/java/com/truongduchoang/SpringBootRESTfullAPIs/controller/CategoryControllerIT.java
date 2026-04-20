package com.truongduchoang.SpringBootRESTfullAPIs.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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
import com.truongduchoang.SpringBootRESTfullAPIs.dto.request.CategoryCreateRequest;
import com.truongduchoang.SpringBootRESTfullAPIs.dto.request.CategoryUpdateRequest;
import com.truongduchoang.SpringBootRESTfullAPIs.dto.response.CategoryResponse;
import com.truongduchoang.SpringBootRESTfullAPIs.models.ApiResponse;
import com.truongduchoang.SpringBootRESTfullAPIs.models.Category;
import com.truongduchoang.SpringBootRESTfullAPIs.repository.CategoryRepository;
import com.truongduchoang.SpringBootRESTfullAPIs.repository.EventRepository;

@IntegrationTest
@AutoConfigureMockMvc
@Transactional
public class CategoryControllerIT {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @Autowired
        private CategoryRepository categoryRepository;

        @Autowired
        private EventRepository eventRepository;

        @BeforeEach
        public void init() {
                this.eventRepository.deleteAll();
                this.categoryRepository.deleteAll();
        }

        @Test
        public void createCategory_shouldReturnCategory_whenCategoryValid() throws Exception {
                CategoryCreateRequest inputCategory = new CategoryCreateRequest();
                inputCategory.setCategoryName("Workshop");
                inputCategory.setDescription("Workshop events");

                String resultStr = mockMvc.perform(
                                post("/api/categories")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsBytes(inputCategory)))
                                .andExpect(status().isCreated())
                                .andReturn().getResponse().getContentAsString();

                ApiResponse<CategoryResponse> response = objectMapper.readValue(
                                resultStr,
                                new TypeReference<ApiResponse<CategoryResponse>>() {
                                });

                assertEquals("Success", response.getStatus(), "Status phải là 'success'");
                assertNotNull(response.getMessage(), "Message không được null");
                assertNotNull(response.getData(), "Data không được null");
                assertNull(response.getErrorCode());
                assertEquals(inputCategory.getCategoryName(), response.getData().getCategoryName(),
                                "Tên category không khớp");
                assertNotNull(response.getTimeStamp(), "TimeStamp không được null");
        }

        @Test
        public void createCategory_shouldReturnError_whenCategoryNameBlank() throws Exception {
                CategoryCreateRequest inputCategory = new CategoryCreateRequest();
                inputCategory.setCategoryName("");

                String resultStr = mockMvc.perform(
                                post("/api/categories")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsBytes(inputCategory)))
                                .andExpect(status().isBadRequest())
                                .andReturn().getResponse().getContentAsString();

                ApiResponse<CategoryResponse> response = objectMapper.readValue(
                                resultStr,
                                new TypeReference<ApiResponse<CategoryResponse>>() {
                                });

                assertEquals("Error", response.getStatus(), "Status phải là error");
                assertNull(response.getData(), "Data phải là null khi lỗi");
                assertEquals("VALIDATION_ERROR", response.getErrorCode(), "ErrorCode không đúng");
        }

        @Test
        public void createCategory_shouldReturnError_whenCategoryNameDuplicated() throws Exception {
                Category category = new Category();
                category.setCategoryName("Workshop");
                this.categoryRepository.saveAndFlush(category);

                CategoryCreateRequest inputCategory = new CategoryCreateRequest();
                inputCategory.setCategoryName("Workshop");

                String resultStr = mockMvc.perform(
                                post("/api/categories")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsBytes(inputCategory)))
                                .andExpect(status().isConflict())
                                .andReturn().getResponse().getContentAsString();

                ApiResponse<CategoryResponse> response = objectMapper.readValue(
                                resultStr,
                                new TypeReference<ApiResponse<CategoryResponse>>() {
                                });

                assertEquals("Error", response.getStatus(), "Status phải là error");
                assertEquals("CATEGORY_NAME_ALREADY_EXISTS", response.getErrorCode(), "ErrorCode không đúng");
        }

        @Test
        public void getAllCategories_shouldReturnListCategory() throws Exception {
                Category c1 = new Category();
                c1.setCategoryName("Workshop");
                Category c2 = new Category();
                c2.setCategoryName("Music");
                this.categoryRepository.saveAll(List.of(c1, c2));

                String resultStr = this.mockMvc.perform(get("/api/categories"))
                                .andExpect(status().isOk())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();

                ApiResponse<List<CategoryResponse>> response = this.objectMapper.readValue(
                                resultStr,
                                new TypeReference<ApiResponse<List<CategoryResponse>>>() {
                                });

                assertEquals("Success", response.getStatus(), "Status phải là 'Success'");
                assertNotNull(response.getData(), "Data không được null");
                assertEquals(2, response.getData().size(), "Số lượng category không đúng");
                assertEquals("Workshop", response.getData().get(0).getCategoryName(), "Tên category 1 không đúng");
        }

        @Test
        public void getCategoryById_shouldReturnCategory_whenCategoryExist() throws Exception {
                Category category = new Category();
                category.setCategoryName("Workshop");
                Category inputCategory = this.categoryRepository.saveAndFlush(category);

                String resultStr = this.mockMvc.perform(get("/api/categories/{id}", inputCategory.getCategoryId()))
                                .andExpect(status().isOk())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();

                ApiResponse<CategoryResponse> response = this.objectMapper.readValue(
                                resultStr,
                                new TypeReference<ApiResponse<CategoryResponse>>() {
                                });

                assertEquals("Success", response.getStatus(), "Status phải là 'Success'");
                assertNotNull(response.getData(), "Data không được null");
                assertEquals("Workshop", response.getData().getCategoryName(), "Tên category không đúng");
        }

        @Test
        public void getCategoryById_shouldReturnError_whenCategoryNotFound() throws Exception {
                long nonExistentId = 999L;

                String resultStr = this.mockMvc.perform(get("/api/categories/{id}", nonExistentId))
                                .andExpect(status().isNotFound())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();

                ApiResponse<CategoryResponse> response = this.objectMapper.readValue(
                                resultStr,
                                new TypeReference<ApiResponse<CategoryResponse>>() {
                                });

                assertEquals("Error", response.getStatus(), "Status phải là error");
                assertNull(response.getData(), "Data phải là null khi lỗi");
                assertEquals("CATEGORY_NOT_FOUND", response.getErrorCode(), "ErrorCode không đúng");
        }

        @Test
        public void updateCategory_shouldReturnCategory_whenValid() throws Exception {
                Category category = new Category();
                category.setCategoryName("Workshop");
                Category inputCategory = this.categoryRepository.saveAndFlush(category);

                CategoryUpdateRequest updateCategory = new CategoryUpdateRequest();
                updateCategory.setCategoryName("Music");
                updateCategory.setDescription("Music events");

                String resultStr = mockMvc.perform(
                                put("/api/categories/{id}", inputCategory.getCategoryId())
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsBytes(updateCategory)))
                                .andExpect(status().isOk())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();

                ApiResponse<CategoryResponse> response = objectMapper.readValue(
                                resultStr,
                                new TypeReference<ApiResponse<CategoryResponse>>() {
                                });

                assertEquals("Success", response.getStatus(), "Status phải là 'success'");
                assertNotNull(response.getData(), "Data không được null!");
                assertEquals("Music", response.getData().getCategoryName(), "Tên category không khớp");
        }

        @Test
        public void deleteCategory_shouldDeleteCategory_whenCategoryExist() throws Exception {
                Category category = new Category();
                category.setCategoryName("Delete category");
                Category inputCategory = this.categoryRepository.saveAndFlush(category);

                this.mockMvc.perform(
                                delete("/api/categories/{id}", inputCategory.getCategoryId())
                                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNoContent());

                long countDB = this.categoryRepository.count();
                assertEquals(0, countDB, "Database phải không còn category sau khi xoá");
        }
}
