package com.truongduchoang.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.truongduchoang.SpringBootRESTfullAPIs.dto.request.EventCreateRequest;
import com.truongduchoang.SpringBootRESTfullAPIs.dto.request.EventUpdateRequest;
import com.truongduchoang.SpringBootRESTfullAPIs.dto.response.EventResponse;
import com.truongduchoang.SpringBootRESTfullAPIs.errors.BadRequestException;
import com.truongduchoang.SpringBootRESTfullAPIs.errors.DuplicateResourceException;
import com.truongduchoang.SpringBootRESTfullAPIs.errors.ResourceNotFoundException;
import com.truongduchoang.SpringBootRESTfullAPIs.mapper.EventMapper;
import com.truongduchoang.SpringBootRESTfullAPIs.models.Category;
import com.truongduchoang.SpringBootRESTfullAPIs.models.Event;
import com.truongduchoang.SpringBootRESTfullAPIs.models.OrganizerProfile;
import com.truongduchoang.SpringBootRESTfullAPIs.models.enums.LocationType;
import com.truongduchoang.SpringBootRESTfullAPIs.repository.CategoryRepository;
import com.truongduchoang.SpringBootRESTfullAPIs.repository.EventRepository;
import com.truongduchoang.SpringBootRESTfullAPIs.repository.OrderRepository;
import com.truongduchoang.SpringBootRESTfullAPIs.repository.OrganizerProfileRepository;
import com.truongduchoang.SpringBootRESTfullAPIs.repository.TicketRepository;
import com.truongduchoang.SpringBootRESTfullAPIs.services.CloudinaryService;
import com.truongduchoang.SpringBootRESTfullAPIs.services.impl.EventServiceImpl;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private OrganizerProfileRepository organizerProfileRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private EventMapper eventMapper;

    @Mock
    private CloudinaryService cloudinaryService;

    @InjectMocks
    private EventServiceImpl eventService;

    @Test
    public void createEvent_shouldReturnEvent_whenRequestValid() {
        EventCreateRequest request = createEventRequest();
        Category category = new Category();
        category.setCategoryId(1L);
        OrganizerProfile organizer = new OrganizerProfile();
        organizer.setOrganizerId(1L);
        Event inputEvent = createEvent();
        Event outputEvent = createEvent();
        outputEvent.setEventId(1L);
        EventResponse outputResponse = new EventResponse();
        outputResponse.setEventId(1L);
        outputResponse.setTitle("Workshop UI UX");

        when(this.eventRepository.existsBySlug(request.getSlug())).thenReturn(false);
        when(this.categoryRepository.findById(request.getCategoryId())).thenReturn(Optional.of(category));
        when(this.organizerProfileRepository.findById(request.getOrganizerId())).thenReturn(Optional.of(organizer));
        when(this.eventMapper.toEntity(request, category, organizer)).thenReturn(inputEvent);
        when(this.eventRepository.save(any())).thenReturn(outputEvent);
        when(this.eventMapper.toResponse(outputEvent)).thenReturn(outputResponse);

        EventResponse resultEvent = this.eventService.createEvent(request, null);

        assertEquals(1L, resultEvent.getEventId());
        assertEquals("Workshop UI UX", resultEvent.getTitle());
    }

    @Test
    public void createEvent_shouldThrowException_whenCategoryNotExist() {
        EventCreateRequest request = createEventRequest();

        when(this.eventRepository.existsBySlug(request.getSlug())).thenReturn(false);
        when(this.categoryRepository.findById(request.getCategoryId())).thenReturn(Optional.empty());

        Exception ex = assertThrows(ResourceNotFoundException.class, () -> {
            this.eventService.createEvent(request, null);
        });

        assertEquals("Category with id 1 not found", ex.getMessage());
    }

    @Test
    public void createEvent_shouldThrowException_whenOrganizerNotExist() {
        EventCreateRequest request = createEventRequest();
        Category category = new Category();
        category.setCategoryId(1L);

        when(this.eventRepository.existsBySlug(request.getSlug())).thenReturn(false);
        when(this.categoryRepository.findById(request.getCategoryId())).thenReturn(Optional.of(category));
        when(this.organizerProfileRepository.findById(request.getOrganizerId())).thenReturn(Optional.empty());

        Exception ex = assertThrows(ResourceNotFoundException.class, () -> {
            this.eventService.createEvent(request, null);
        });

        assertEquals("Organizer with id 1 not found", ex.getMessage());
    }

    @Test
    public void createEvent_shouldThrowException_whenSlugDuplicated() {
        EventCreateRequest request = createEventRequest();
        when(this.eventRepository.existsBySlug(request.getSlug())).thenReturn(true);

        Exception ex = assertThrows(DuplicateResourceException.class, () -> {
            this.eventService.createEvent(request, null);
        });

        assertEquals("Event slug already exists", ex.getMessage());
    }

    @Test
    public void createEvent_shouldThrowException_whenEndTimeBeforeStartTime() {
        EventCreateRequest request = createEventRequest();
        request.setEndTime(request.getStartTime());

        Exception ex = assertThrows(BadRequestException.class, () -> {
            this.eventService.createEvent(request, null);
        });

        assertEquals("End time must be after start time", ex.getMessage());
    }

    @Test
    public void getAllEvents_shouldReturnListEvent() {
        Event e1 = createEvent();
        e1.setEventId(1L);
        Event e2 = createEvent();
        e2.setEventId(2L);
        List<Event> outputEvents = new ArrayList<>(List.of(e1, e2));

        EventResponse r1 = new EventResponse();
        r1.setEventId(1L);
        EventResponse r2 = new EventResponse();
        r2.setEventId(2L);

        when(this.eventRepository.findAll()).thenReturn(outputEvents);
        when(this.eventMapper.toResponse(e1)).thenReturn(r1);
        when(this.eventMapper.toResponse(e2)).thenReturn(r2);

        List<EventResponse> resultEvents = this.eventService.getAllEvents();

        assertEquals(2, resultEvents.size());
        assertEquals(2L, resultEvents.get(1).getEventId());
    }

    @Test
    public void getEventById_shouldReturnEvent_whenEventExist() {
        Long inputId = 1L;
        Event inputEvent = createEvent();
        inputEvent.setEventId(inputId);
        EventResponse outputResponse = new EventResponse();
        outputResponse.setEventId(inputId);

        when(this.eventRepository.findById(inputId)).thenReturn(Optional.of(inputEvent));
        when(this.eventMapper.toResponse(inputEvent)).thenReturn(outputResponse);

        EventResponse resultEvent = this.eventService.getEventById(inputId);

        assertEquals(1L, resultEvent.getEventId());
    }

    @Test
    public void getEventById_shouldThrowException_whenEventNotExist() {
        Long inputId = 1L;
        when(this.eventRepository.findById(inputId)).thenReturn(Optional.empty());

        Exception ex = assertThrows(ResourceNotFoundException.class, () -> {
            this.eventService.getEventById(inputId);
        });

        assertEquals("Event with id 1 not found", ex.getMessage());
    }

    @Test
    public void updateEvent_shouldReturnEvent_whenValid() {
        Long inputId = 1L;
        EventUpdateRequest request = new EventUpdateRequest();
        request.setTitle("New title");

        Event inputEvent = createEvent();
        inputEvent.setEventId(inputId);
        Event outputEvent = createEvent();
        outputEvent.setEventId(inputId);
        EventResponse outputResponse = new EventResponse();
        outputResponse.setEventId(inputId);
        outputResponse.setTitle("New title");

        when(this.eventRepository.findById(inputId)).thenReturn(Optional.of(inputEvent));
        when(this.eventRepository.save(inputEvent)).thenReturn(outputEvent);
        when(this.eventMapper.toResponse(outputEvent)).thenReturn(outputResponse);

        EventResponse resultEvent = this.eventService.updateEvent(inputId, request, null);

        assertEquals("New title", resultEvent.getTitle());
    }

    @Test
    public void updateEvent_shouldThrowException_whenEventNotExist() {
        Long inputId = 1L;
        EventUpdateRequest request = new EventUpdateRequest();
        request.setTitle("New title");
        when(this.eventRepository.findById(inputId)).thenReturn(Optional.empty());

        Exception ex = assertThrows(ResourceNotFoundException.class, () -> {
            this.eventService.updateEvent(inputId, request, null);
        });

        assertEquals("Event with id 1 not found", ex.getMessage());
    }

    @Test
    public void updateEvent_shouldThrowException_whenCategoryNotExist() {
        Long inputId = 1L;
        EventUpdateRequest request = new EventUpdateRequest();
        request.setCategoryId(99L);

        Event inputEvent = createEvent();
        inputEvent.setEventId(inputId);

        when(this.eventRepository.findById(inputId)).thenReturn(Optional.of(inputEvent));
        when(this.categoryRepository.findById(request.getCategoryId())).thenReturn(Optional.empty());

        Exception ex = assertThrows(ResourceNotFoundException.class, () -> {
            this.eventService.updateEvent(inputId, request, null);
        });

        assertEquals("Category with id 99 not found", ex.getMessage());
    }

    @Test
    public void deleteEvent_shouldReturnVoid_whenEventExistAndNotUsed() {
        Long inputId = 1L;
        Event inputEvent = createEvent();
        inputEvent.setEventId(inputId);

        when(this.eventRepository.findById(inputId)).thenReturn(Optional.of(inputEvent));
        when(this.orderRepository.existsByEventEventId(inputId)).thenReturn(false);
        when(this.ticketRepository.existsByEventEventId(inputId)).thenReturn(false);

        this.eventService.deleteEvent(inputId);

        verify(this.eventRepository).delete(inputEvent);
    }

    @Test
    public void deleteEvent_shouldThrowException_whenEventNotExist() {
        Long inputId = 1L;
        when(this.eventRepository.findById(inputId)).thenReturn(Optional.empty());

        Exception ex = assertThrows(ResourceNotFoundException.class, () -> {
            this.eventService.deleteEvent(inputId);
        });

        assertEquals("Event with id 1 not found", ex.getMessage());
    }

    private EventCreateRequest createEventRequest() {
        EventCreateRequest request = new EventCreateRequest();
        request.setOrganizerId(1L);
        request.setCategoryId(1L);
        request.setTitle("Workshop UI UX");
        request.setSlug("workshop-ui-ux");
        request.setLocationType(LocationType.OFFLINE);
        request.setStartTime(LocalDateTime.of(2026, 5, 20, 9, 0));
        request.setEndTime(LocalDateTime.of(2026, 5, 20, 17, 0));
        return request;
    }

    private Event createEvent() {
        Event event = new Event();
        event.setTitle("Workshop UI UX");
        event.setSlug("workshop-ui-ux");
        event.setLocationType(LocationType.OFFLINE);
        event.setStartTime(LocalDateTime.of(2026, 5, 20, 9, 0));
        event.setEndTime(LocalDateTime.of(2026, 5, 20, 17, 0));
        return event;
    }
}
