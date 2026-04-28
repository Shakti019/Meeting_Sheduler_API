package com.App.live.Domain.Meeting.Controller;

import com.App.live.Domain.Meeting.DTO.Request.N8nWebhookRequest;
import com.App.live.Domain.Meeting.Model.ActionItem;
import com.App.live.Domain.Meeting.Model.Meeting;
import com.App.live.Domain.Meeting.Repository.ActionItemRepository;
import com.App.live.Domain.Meeting.Services.MeetingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/webhook/n8n")
@RequiredArgsConstructor
@Slf4j
public class N8nWebhookController {

    private final MeetingService meetingService;
    private final ActionItemRepository actionItemRepository;

    @PostMapping("/meeting-results")
    public ResponseEntity<String> receiveMeetingResults(@RequestBody N8nWebhookRequest payload) {
        log.info("Received n8n AI processed results for meeting ID: {}", payload.getMeetingId());

        Optional<Meeting> meetingOpt = meetingService.getMeetingById(payload.getMeetingId());
        
        if (meetingOpt.isEmpty()) {
            log.error("Meeting with ID {} not found", payload.getMeetingId());
            return ResponseEntity.notFound().build();
        }

        Meeting meeting = meetingOpt.get();

        // Update Meeting Details
        meeting.setSummary(payload.getSummary());
        meeting.setTranscript(payload.getTranscript());
        meeting.setKeyPoints(payload.getKeyPoints());
        meeting.setDecisions(payload.getDecisions());
        meeting.setRisks(payload.getRisks());
        meeting.setInsights(payload.getInsights());
        meeting.setSentiment(payload.getSentiment());
        meeting.setMeetingScore(payload.getMeetingScore());
        meeting.setFollowUpEmail(payload.getFollowUpEmail());
        meeting.setLanguage(payload.getLanguage());

        meetingService.scheduleMeeting(meeting); // Save the updated meeting back to DB

        // Create and Save Action Items
        if (payload.getActionItems() != null && !payload.getActionItems().isEmpty()) {
            for (N8nWebhookRequest.ActionItemDTO aiTask : payload.getActionItems()) {
                ActionItem task = new ActionItem();
                task.setMeetingId(meeting.getId());
                task.setDescription(aiTask.getDescription());
                task.setAssigneeEmail(aiTask.getAssigneeEmail());
                task.setDeadline(aiTask.getDeadline());
                task.setPriority(aiTask.getPriority());
                task.setCompletionProbability(aiTask.getCompletionProbability());
                task.setCompleted(false);

                actionItemRepository.save(task);
            }
        }

        log.info("Successfully processed and saved all standard and advanced AI metrics.");
        return ResponseEntity.ok("Received and saved meeting AI results.");
    }
}