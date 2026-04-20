package com.truongduchoang.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.truongduchoang.SpringBootRESTfullAPIs.dto.request.CategoryCreateRequest;
import com.truongduchoang.SpringBootRESTfullAPIs.dto.request.CategoryUpdateRequest;
import com.truongduchoang.SpringBootRESTfullAPIs.dto.response.CategoryResponse;
import com.truongduchoang.SpringBootRESTfullAPIs.errors.BadRequestException;
import com.truongduchoang.SpringBootRESTfullAPIs.errors.DuplicateResourceException;
import com.truongduchoang.SpringBootRESTfullAPIs.errors.ResourceNotFoundException;
import com.truongduchoang.SpringBootRESTfullAPIs.mapper.CategoryMapper;
import com.truongduchoang.SpringBootRESTfullAPIs.models.Category;
import com.truongduchoang.SpringBootRESTfullAPIs.repository.CategoryRepository;
import com.truongduchoang.SpringBootRESTfullAPIs.repository.EventRepository;
import com.truongduchoang.SpringBootRESTfullAPIs.services.impl.CategoryServiceImpl;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    public void createCategory_shouldReturnCategory_whenCategoryNameValid() {
        CategoryCreateRequest request = new CategoryCreateRequest();
        request.setCategoryName("Workshop");
        request.setDescription("Workshop events");

        Category inputCategory = new Category();
        inputCategory.setCategoryName("Workshop");

        Category outputCategory = new Category();
        outputCategory.setCategoryId(1L);
        outputCategory.setCategoryName("Workshop");

        CategoryResponse outputResponse = new CategoryResponse();
        outputResponse.setCategoryId(1L);
        outputResponse.setCategoryName("Workshop");

        when(this.categoryRepository.existsByCategoryNameIgnoreCase(request.getCategoryName())).thenReturn(false);
        when(this.categoryMapper.toEntity(request)).thenReturn(inputCategory);
        when(this.categoryRepository.save(any())).thenReturn(outputCategory);
        when(this.categoryMapper.toResponse(outputCategory)).thenReturn(outputResponse);

        CategoryResponse resultCategory = this.categoryService.createCategory(request);

        assertEquals(1L, resultCategory.getCategoryId());
        assertEquals("Workshop", resultCategory.getCategoryName());
    }

    @Test
    public void createCategory_shouldThrowException_whenCategoryNameDuplicated() {
        CategoryCreateRequest request = new CategoryCreateRequest();
        request.setCategoryName("Workshop");

        when(this.categoryRepository.existsByCategoryNameIgnoreCase(request.getCategoryName())).thenReturn(true);

        Exception ex = assertThrows(DuplicateResourceException.class, () -> {
            this.categoryService.createCategory(request);
        });

        assertEquals("Category name already exists", ex.getMessage());
    }

    @Test
    public void getAllCategories_shouldReturnListCategory() {
        List<Category> outputCategories = new ArrayList<>();
        Category c1 = new Category();
        c1.setCategoryId(1L);
        c1.setCategoryName("Workshop");
        Category c2 = new Category();
        c2.setCategoryId(2L);
        c2.setCategoryName("Music");
        outputCategories.add(c1);
        outputCategories.add(c2);

        CategoryResponse r1 = new CategoryResponse();
        r1.setCategoryId(1L);
        r1.setCategoryName("Workshop");
        CategoryResponse r2 = new CategoryResponse();
        r2.setCategoryId(2L);
        r2.setCategoryName("Music");

        when(this.categoryRepository.findAll()).thenReturn(outputCategories);
        when(this.categoryMapper.toResponse(c1)).thenReturn(r1);
        when(this.categoryMapper.toResponse(c2)).thenReturn(r2);

        List<CategoryResponse> resultCategories = this.categoryService.getAllCategories();

        assertEquals(2, resultCategories.size());
        assertEquals("Music", resultCategories.get(1).getCategoryName());
    }

    @Test
    public void getCategoryById_shouldReturnCategory_whenCategoryExist() {
        Long inputId = 1L;
        Category inputCategory = new Category();
        inputCategory.setCategoryId(inputId);
        inputCategory.setCategoryName("Workshop");

        CategoryResponse outputResponse = new CategoryResponse();
        outputResponse.setCategoryId(inputId);
        outputResponse.setCategoryName("Workshop");

        when(this.categoryRepository.findById(inputId)).thenReturn(Optional.of(inputCategory));
        when(this.categoryMapper.toResponse(inputCategory)).thenReturn(outputResponse);

        CategoryResponse resultCategory = this.categoryService.getCategoryById(inputId);

        assertEquals(1L, resultCategory.getCategoryId());
    }

    @Test
    public void getCategoryById_shouldThrowException_whenCategoryNotExist() {
        Long inputId = 1L;
        when(this.categoryRepository.findById(inputId)).thenReturn(Optional.empty());

        Exception ex = assertThrows(ResourceNotFoundException.class, () -> {
            this.categoryService.getCategoryById(inputId);
        });

        assertEquals("Category with id 1 not found", ex.getMessage());
    }

    @Test
    public void updateCategory_shouldReturnCategory_whenValid() {
        Long inputId = 1L;
        CategoryUpdateRequest request = new CategoryUpdateRequest();
        request.setCategoryName("Music");

        Category inputCategory = new Category();
        inputCategory.setCategoryId(inputId);
        inputCategory.setCategoryName("Workshop");

        Category outputCategory = new Category();
        outputCategory.setCategoryId(inputId);
        outputCategory.setCategoryName("Music");

        CategoryResponse outputResponse = new CategoryResponse();
        outputResponse.setCategoryId(inputId);
        outputResponse.setCategoryName("Music");

        when(this.categoryRepository.findById(inputId)).thenReturn(Optional.of(inputCategory));
        when(this.categoryRepository.existsByCategoryNameIgnoreCaseAndCategoryIdNot(request.getCategoryName(), inputId)).thenReturn(false);
        when(this.categoryRepository.save(inputCategory)).thenReturn(outputCategory);
        when(this.categoryMapper.toResponse(outputCategory)).thenReturn(outputResponse);

        CategoryResponse resultCategory = this.categoryService.updateCategory(inputId, request);

        assertEquals("Music", resultCategory.getCategoryName());
    }

    @Test
    public void updateCategory_shouldThrowException_whenCategoryNotExist() {
        Long inputId = 1L;
        CategoryUpdateRequest request = new CategoryUpdateRequest();
        request.setCategoryName("Music");
        when(this.categoryRepository.findById(inputId)).thenReturn(Optional.empty());

        Exception ex = assertThrows(ResourceNotFoundException.class, () -> {
            this.categoryService.updateCategory(inputId, request);
        });

        assertEquals("Category with id 1 not found", ex.getMessage());
    }

    @Test
    public void deleteCategory_shouldReturnVoid_whenCategoryExistAndNotUsed() {
        Long inputId = 1L;
        Category inputCategory = new Category();
        inputCategory.setCategoryId(inputId);

        when(this.categoryRepository.findById(inputId)).thenReturn(Optional.of(inputCategory));
        when(this.eventRepository.existsByCategoryCategoryId(inputId)).thenReturn(false);

        this.categoryService.deleteCategory(inputId);

        verify(this.categoryRepository).delete(inputCategory);
    }

    @Test
    public void deleteCategory_shouldThrowException_whenCategoryNotExist() {
        Long inputId = 1L;
        when(this.categoryRepository.findById(inputId)).thenReturn(Optional.empty());

        Exception ex = assertThrows(ResourceNotFoundException.class, () -> {
            this.categoryService.deleteCategory(inputId);
        });

        assertEquals("Category with id 1 not found", ex.getMessage());
    }

    @Test
    public void deleteCategory_shouldThrowException_whenCategoryIsUsedByEvent() {
        Long inputId = 1L;
        Category inputCategory = new Category();
        inputCategory.setCategoryId(inputId);

        when(this.categoryRepository.findById(inputId)).thenReturn(Optional.of(inputCategory));
        when(this.eventRepository.existsByCategoryCategoryId(inputId)).thenReturn(true);

        Exception ex = assertThrows(BadRequestException.class, () -> {
            this.categoryService.deleteCategory(inputId);
        });

        assertEquals("Category is being used by events", ex.getMessage());
    }
}
