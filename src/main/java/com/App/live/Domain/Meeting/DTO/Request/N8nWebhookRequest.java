package com.App.live.Domain.Meeting.DTO.Request;

import com.App.live.Domain.Meeting.Model.Meeting;
import lombok.Data;

import java.util.List;

@Data
public class N8nWebhookRequest {
    private String meetingId;
    private String transcript;
    private String summary;
    
    private List<ActionItemDTO> actionItems;
    private List<String> keyPoints;
    private List<String> decisions;
    private List<String> risks;
    private List<String> insights;
    
    private Meeting.Sentiment sentiment;
    private Meeting.MeetingScore meetingScore;
    private Meeting.FollowUpEmail followUpEmail;
    private Meeting.Language language;

    @Data
    public static class ActionItemDTO {
        private String description;
        private String assigneeEmail;
        private String deadline;
        private String priority;
        private Double completionProbability;
    }
}