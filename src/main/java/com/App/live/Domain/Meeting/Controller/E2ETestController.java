package com.App.live.Domain.Meeting.Controller;

import com.App.live.Domain.Meeting.Model.ActionItem;
import com.App.live.Domain.Meeting.Model.Meeting;
import com.App.live.Domain.Meeting.Repository.ActionItemRepository;
import com.App.live.Domain.Meeting.Services.MeetingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/e2e-test")
@RequiredArgsConstructor
public class E2ETestController {

    private final MeetingService meetingService;
    private final ActionItemRepository actionItemRepository;

    @GetMapping("/setup")
    public ResponseEntity<Map<String, String>> setupTestMeeting() {
        Meeting meeting = new Meeting();
        meeting.setTitle("E2E n8n Test Meeting");
        meeting.setDescription("A dummy meeting to test the complete asynchronous AI audio pipeline.");
        meeting.setOrganizerEmail("test-admin@example.com");
        meeting.setStartTime(LocalDateTime.now().minusMinutes(30));
        meeting.setEndTime(LocalDateTime.now().minusMinutes(10));
        
        Meeting saved = meetingService.scheduleMeeting(meeting);
        
        Map<String, String> response = new HashMap<>();
        response.put("meetingId", saved.getId());
        response.put("status", "Dummy meeting created. Now send audio to n8n with this meetingId!");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/results/{meetingId}")
    public ResponseEntity<Map<String, Object>> getTestResults(@PathVariable String meetingId) {
        Meeting meeting = meetingService.getMeetingById(meetingId).orElse(null);
        if (meeting == null) {
            return ResponseEntity.notFound().build();
        }
        
        List<ActionItem> actionItems = actionItemRepository.findByMeetingId(meetingId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("meeting", meeting);
        response.put("generatedActionItems", actionItems);
        return ResponseEntity.ok(response);
    }
}