package com.App.live.Domain.Meeting.Model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "action_items")
public class ActionItem {
    @Id
    private String id;
    private String meetingId;
    private String description;
    private boolean isCompleted;
    private String assigneeEmail;
    
    // AI Agent extracted fields
    private String deadline;
    private String priority;
    private Double completionProbability;
}
