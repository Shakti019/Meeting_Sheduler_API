package com.App.live.Controller;

import com.App.live.Domain.Meeting.Model.Meeting;
import com.App.live.Domain.Meeting.Services.MeetingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import com.App.live.Domain.Meeting.Services.AssemblyAIService;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.client.RestTemplate;
import java.util.concurrent.CompletableFuture;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/meeting")
@RequiredArgsConstructor
@Slf4j
public class MeetingRoomController {

    private final MeetingService meetingService;
    private final AssemblyAIService assemblyAIService;

    // Serves the meeting room UI
    @GetMapping("/{id}/room")
    public String getMeetingRoom(@PathVariable String id, Model model) {
        Optional<Meeting> meeting = meetingService.getMeetingById(id);
        if (meeting.isEmpty()) {
            return "redirect:/dashboard?error=MeetingNotFound";
        }
        model.addAttribute("meeting", meeting.get());
        return "room";
    }

    @Value("${n8n.webhook.url}")
    private String n8nWebhookUrlConfig;

    // Receives the final transcript from the browser and forwards to n8n
    @PostMapping("/api/{id}/end")
    @ResponseBody
    public ResponseEntity<?> endMeetingAndAnalyze(
            @PathVariable String id,
            @RequestParam("file") MultipartFile audioFile
    ) {
        log.info("Ending meeting {}. Received Audio File size: {} bytes", id, audioFile.getSize());

        // Process asynchronously so we don't lock the web UI waiting for the transcription job
        CompletableFuture.runAsync(() -> {
            try {
                // 1. Send Audio to AssemblyAI -> Convert speech to text
                String finalTranscriptText = assemblyAIService.transcribeAudio(audioFile);

                // 2. Save transcript to database for this meeting
                Optional<Meeting> optMeeting = meetingService.getMeetingById(id);
                if (optMeeting.isPresent()) {
                    Meeting meeting = optMeeting.get();
                    meeting.setTranscript(finalTranscriptText);
                    meetingService.scheduleMeeting(meeting);
                    log.info("Successfully pushed transcript to database!");
                }
                
                // 3. Forward the text to n8n webhook for LLM Tasks processing
                String n8nWebhookUrl = n8nWebhookUrlConfig + "?meetingId=" + id;
                RestTemplate restTemplate = new RestTemplate();
                Map<String, String> n8nPayload = new HashMap<>();
                n8nPayload.put("meetingId", id);
                n8nPayload.put("text", finalTranscriptText); 

                restTemplate.postForEntity(n8nWebhookUrl, n8nPayload, String.class);
                log.info("Successfully passed text to n8n for LLM analytics.");

            } catch (Exception e) {
                log.error("Failed during Async AI Audio-to-n8n Pipeline", e);
            }
        });

        return ResponseEntity.ok().body("{\"status\": \"Audio Uploaded to Backend. AI Processing started!\"}");
    }
}