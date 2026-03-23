package com.workshop.service;

import com.workshop.dto.WorkshopResponse;
import com.workshop.model.Workshop;
import com.workshop.repository.WorkshopRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WorkshopServiceTest {

    @Mock
    private WorkshopRepository workshopRepository;

    @InjectMocks
    private WorkshopService workshopService;

    private Workshop sampleWorkshop;

    @BeforeEach
    void setUp() {
        sampleWorkshop = Workshop.builder()
                .id(1L)
                .title("Intro to React")
                .description("Learn React basics")
                .instructor("Jane Doe")
                .venue("Virtual")
                .startDate(LocalDateTime.now().plusDays(2))
                .endDate(LocalDateTime.now().plusDays(3))
                .capacity(50)
                .teamSize(1)
                .seatsAvailable(50)
                .status("UPCOMING")
                .build();
    }

    @Test
    @DisplayName("Test finding workshop by existing ID returns response")
    void getWorkshopById_Exists_ReturnsResponse() {
        // Arrange
        when(workshopRepository.findById(1L)).thenReturn(Optional.of(sampleWorkshop));

        // Act
        WorkshopResponse response = workshopService.getWorkshopById(1L);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertEquals("Intro to React", response.getTitle());
        assertEquals("Virtual", response.getVenue());
        assertEquals(50, response.getCapacity());
        verify(workshopRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Test toResponse mapping utility")
    void testToResponseMapping() {
        // Act
        WorkshopResponse response = workshopService.toResponse(sampleWorkshop);

        // Assert
        assertEquals(sampleWorkshop.getId(), response.getId());
        assertEquals(sampleWorkshop.getTitle(), response.getTitle());
        assertEquals(sampleWorkshop.getInstructor(), response.getInstructor());
        assertEquals(sampleWorkshop.getCapacity(), response.getCapacity());
        assertEquals(sampleWorkshop.getSeatsAvailable(), response.getSeatsAvailable());
    }
}
