package com.App.live.Domain.Meeting.Model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

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
    private List<String> panelMembers;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // AI processing fields
    private String summary;
    private String transcript;
    
    // Advanced AI Analytics
    private List<String> keyPoints;
    private List<String> decisions;
    private List<String> risks;
    private List<String> insights;
    
    private Sentiment sentiment;
    private MeetingScore meetingScore;
    private FollowUpEmail followUpEmail;
    private Language language;
    
    @Data
    public static class Sentiment {
        private String overall;
        private Double confidence;
        private List<String> emotions;
    }
    
    @Data
    public static class MeetingScore {
        private Integer score;
        private String feedback;
    }
    
    @Data
    public static class FollowUpEmail {
        private String subject;
        private String body;
    }
    
    @Data
    public static class Language {
        private String detected;
        private String translated;
    }
}