package com.truongduchoang.SpringBootRESTfullAPIs.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
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
import com.truongduchoang.SpringBootRESTfullAPIs.dto.request.EventCreateRequest;
import com.truongduchoang.SpringBootRESTfullAPIs.dto.request.EventUpdateRequest;
import com.truongduchoang.SpringBootRESTfullAPIs.dto.response.EventResponse;
import com.truongduchoang.SpringBootRESTfullAPIs.models.ApiResponse;
import com.truongduchoang.SpringBootRESTfullAPIs.models.Category;
import com.truongduchoang.SpringBootRESTfullAPIs.models.Event;
import com.truongduchoang.SpringBootRESTfullAPIs.models.OrganizerProfile;
import com.truongduchoang.SpringBootRESTfullAPIs.models.User;
import com.truongduchoang.SpringBootRESTfullAPIs.models.enums.LocationType;
import com.truongduchoang.SpringBootRESTfullAPIs.repository.CategoryRepository;
import com.truongduchoang.SpringBootRESTfullAPIs.repository.EventRepository;
import com.truongduchoang.SpringBootRESTfullAPIs.repository.OrganizerProfileRepository;
import com.truongduchoang.SpringBootRESTfullAPIs.repository.UserRepository;

@IntegrationTest
@AutoConfigureMockMvc
@Transactional
public class EventControllerIT {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @Autowired
        private EventRepository eventRepository;

        @Autowired
        private CategoryRepository categoryRepository;

        @Autowired
        private OrganizerProfileRepository organizerProfileRepository;

        @Autowired
        private UserRepository userRepository;

        @BeforeEach
        public void init() {
                this.eventRepository.deleteAll();
                this.organizerProfileRepository.deleteAll();
                this.categoryRepository.deleteAll();
                this.userRepository.deleteAll();
        }

        @Test
        public void createEvent_shouldReturnEvent_whenEventValid() throws Exception {
                Category category = createCategory("Workshop");
                OrganizerProfile organizer = createOrganizer();
                EventCreateRequest inputEvent = createEventRequest(category.getCategoryId(), organizer.getOrganizerId(),
                                "workshop-ui-ux");

                String resultStr = mockMvc.perform(
                                post("/api/events")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsBytes(inputEvent)))
                                .andExpect(status().isCreated())
                                .andReturn().getResponse().getContentAsString();

                ApiResponse<EventResponse> response = objectMapper.readValue(
                                resultStr,
                                new TypeReference<ApiResponse<EventResponse>>() {
                                });

                assertEquals("Success", response.getStatus(), "Status phải là 'success'");
                assertNotNull(response.getMessage(), "Message không được null");
                assertNotNull(response.getData(), "Data không được null");
                assertNull(response.getErrorCode());
                assertEquals(inputEvent.getTitle(), response.getData().getTitle(), "Tên event không khớp");
                assertEquals(inputEvent.getSlug(), response.getData().getSlug(), "Slug event không khớp");
                assertNotNull(response.getTimeStamp(), "TimeStamp không được null");
        }

        @Test
        public void createEvent_shouldReturnError_whenTitleBlank() throws Exception {
                Category category = createCategory("Workshop");
                OrganizerProfile organizer = createOrganizer();
                EventCreateRequest inputEvent = createEventRequest(category.getCategoryId(), organizer.getOrganizerId(),
                                "workshop-invalid");
                inputEvent.setTitle("");

                String resultStr = mockMvc.perform(
                                post("/api/events")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsBytes(inputEvent)))
                                .andExpect(status().isBadRequest())
                                .andReturn().getResponse().getContentAsString();

                ApiResponse<EventResponse> response = objectMapper.readValue(
                                resultStr,
                                new TypeReference<ApiResponse<EventResponse>>() {
                                });

                assertEquals("Error", response.getStatus(), "Status phải là error");
                assertNull(response.getData(), "Data phải là null khi lỗi");
                assertEquals("VALIDATION_ERROR", response.getErrorCode(), "ErrorCode không đúng");
        }

        @Test
        public void createEvent_shouldReturnError_whenSlugDuplicated() throws Exception {
                Category category = createCategory("Workshop");
                OrganizerProfile organizer = createOrganizer();
                Event event = createEvent(category, organizer, "duplicated-slug");
                this.eventRepository.saveAndFlush(event);

                EventCreateRequest inputEvent = createEventRequest(category.getCategoryId(), organizer.getOrganizerId(),
                                "duplicated-slug");

                String resultStr = mockMvc.perform(
                                post("/api/events")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsBytes(inputEvent)))
                                .andExpect(status().isConflict())
                                .andReturn().getResponse().getContentAsString();

                ApiResponse<EventResponse> response = objectMapper.readValue(
                                resultStr,
                                new TypeReference<ApiResponse<EventResponse>>() {
                                });

                assertEquals("Error", response.getStatus(), "Status phải là error");
                assertEquals("EVENT_SLUG_ALREADY_EXISTS", response.getErrorCode(), "ErrorCode không đúng");
        }

        @Test
        public void createEvent_shouldReturnError_whenCategoryNotFound() throws Exception {
                OrganizerProfile organizer = createOrganizer();
                EventCreateRequest inputEvent = createEventRequest(999L, organizer.getOrganizerId(),
                                "category-not-found");

                String resultStr = mockMvc.perform(
                                post("/api/events")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsBytes(inputEvent)))
                                .andExpect(status().isNotFound())
                                .andReturn().getResponse().getContentAsString();

                ApiResponse<EventResponse> response = objectMapper.readValue(
                                resultStr,
                                new TypeReference<ApiResponse<EventResponse>>() {
                                });

                assertEquals("Error", response.getStatus(), "Status phải là error");
                assertEquals("CATEGORY_NOT_FOUND", response.getErrorCode(), "ErrorCode không đúng");
        }

        @Test
        public void getAllEvents_shouldReturnListEvent() throws Exception {
                Category category = createCategory("Workshop");
                OrganizerProfile organizer = createOrganizer();
                Event e1 = createEvent(category, organizer, "event-1");
                Event e2 = createEvent(category, organizer, "event-2");
                this.eventRepository.saveAll(List.of(e1, e2));

                String resultStr = this.mockMvc.perform(get("/api/events"))
                                .andExpect(status().isOk())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();

                ApiResponse<List<EventResponse>> response = this.objectMapper.readValue(
                                resultStr,
                                new TypeReference<ApiResponse<List<EventResponse>>>() {
                                });

                assertEquals("Success", response.getStatus(), "Status phải là 'Success'");
                assertNotNull(response.getData(), "Data không được null");
                assertEquals(2, response.getData().size(), "Số lượng event không đúng");
        }

        @Test
        public void getEventById_shouldReturnEvent_whenEventExist() throws Exception {
                Category category = createCategory("Workshop");
                OrganizerProfile organizer = createOrganizer();
                Event event = this.eventRepository.saveAndFlush(createEvent(category, organizer, "event-get-by-id"));

                String resultStr = this.mockMvc.perform(get("/api/events/{id}", event.getEventId()))
                                .andExpect(status().isOk())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();

                ApiResponse<EventResponse> response = this.objectMapper.readValue(
                                resultStr,
                                new TypeReference<ApiResponse<EventResponse>>() {
                                });

                assertEquals("Success", response.getStatus(), "Status phải là 'Success'");
                assertNotNull(response.getData(), "Data không được null");
                assertEquals("event-get-by-id", response.getData().getSlug(), "Slug event không đúng");
        }

        @Test
        public void getEventById_shouldReturnError_whenEventNotFound() throws Exception {
                long nonExistentId = 999L;

                String resultStr = this.mockMvc.perform(get("/api/events/{id}", nonExistentId))
                                .andExpect(status().isNotFound())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();

                ApiResponse<EventResponse> response = this.objectMapper.readValue(
                                resultStr,
                                new TypeReference<ApiResponse<EventResponse>>() {
                                });

                assertEquals("Error", response.getStatus(), "Status phải là error");
                assertNull(response.getData(), "Data phải là null khi lỗi");
                assertEquals("EVENT_NOT_FOUND", response.getErrorCode(), "ErrorCode không đúng");
        }

        @Test
        public void updateEvent_shouldReturnEvent_whenValid() throws Exception {
                Category category = createCategory("Workshop");
                OrganizerProfile organizer = createOrganizer();
                Event event = this.eventRepository.saveAndFlush(createEvent(category, organizer, "event-update"));

                EventUpdateRequest updateEvent = new EventUpdateRequest();
                updateEvent.setTitle("Updated event");
                updateEvent.setSlug("event-updated");

                String resultStr = mockMvc.perform(
                                put("/api/events/{id}", event.getEventId())
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsBytes(updateEvent)))
                                .andExpect(status().isOk())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();

                ApiResponse<EventResponse> response = objectMapper.readValue(
                                resultStr,
                                new TypeReference<ApiResponse<EventResponse>>() {
                                });

                assertEquals("Success", response.getStatus(), "Status phải là 'success'");
                assertNotNull(response.getData(), "Data không được null!");
                assertEquals("Updated event", response.getData().getTitle(), "Tên event không khớp");
                assertEquals("event-updated", response.getData().getSlug(), "Slug event không khớp");
        }

        @Test
        public void deleteEvent_shouldDeleteEvent_whenEventExist() throws Exception {
                Category category = createCategory("Workshop");
                OrganizerProfile organizer = createOrganizer();
                Event event = this.eventRepository.saveAndFlush(createEvent(category, organizer, "event-delete"));

                this.mockMvc.perform(
                                delete("/api/events/{id}", event.getEventId())
                                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNoContent());

                long countDB = this.eventRepository.count();
                assertEquals(0, countDB, "Database phải không còn event sau khi xoá");
        }

        private Category createCategory(String categoryName) {
                Category category = new Category();
                category.setCategoryName(categoryName);
                return this.categoryRepository.saveAndFlush(category);
        }

        private OrganizerProfile createOrganizer() {
                User user = new User(null, "organizer@gmail.com", "Organizer User");
                User savedUser = this.userRepository.saveAndFlush(user);

                OrganizerProfile organizer = new OrganizerProfile();
                organizer.setUser(savedUser);
                organizer.setOrganizationName("Organizer Company");
                return this.organizerProfileRepository.saveAndFlush(organizer);
        }

        private EventCreateRequest createEventRequest(Long categoryId, Long organizerId, String slug) {
                EventCreateRequest request = new EventCreateRequest();
                request.setOrganizerId(organizerId);
                request.setCategoryId(categoryId);
                request.setTitle("Workshop UI UX");
                request.setSlug(slug);
                request.setShortDescription("Workshop short description");
                request.setLocationType(LocationType.OFFLINE);
                request.setStartTime(LocalDateTime.of(2026, 5, 20, 9, 0));
                request.setEndTime(LocalDateTime.of(2026, 5, 20, 17, 0));
                return request;
        }

        private Event createEvent(Category category, OrganizerProfile organizer, String slug) {
                Event event = new Event();
                event.setCategory(category);
                event.setOrganizer(organizer);
                event.setTitle("Workshop UI UX");
                event.setSlug(slug);
                event.setLocationType(LocationType.OFFLINE);
                event.setStartTime(LocalDateTime.of(2026, 5, 20, 9, 0));
                event.setEndTime(LocalDateTime.of(2026, 5, 20, 17, 0));
                return event;
        }
}
