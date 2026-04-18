package com.App.live.Domain.Meeting.Model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "meetings")
public class Meeting {
    @Id
    private String id;
    
    private String title;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String organizerEmail;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}