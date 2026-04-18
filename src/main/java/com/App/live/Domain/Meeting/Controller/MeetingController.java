package com.App.live.Domain.Meeting.Controller;

import com.App.live.Domain.Meeting.Model.Meeting;
import com.App.live.Domain.Meeting.Services.MeetingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/meetings")
@RequiredArgsConstructor
public class MeetingController {

    private final MeetingService meetingService;

    @PostMapping
    public ResponseEntity<Meeting> scheduleMeeting(
            @RequestBody Meeting meeting,
            Authentication authentication
    ) {
        meeting.setOrganizerEmail(authentication.getName());
        Meeting saved = meetingService.scheduleMeeting(meeting);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public ResponseEntity<List<Meeting>> getAllMeetings() {
        return ResponseEntity.ok(meetingService.getAllMeetings());
    }

    @GetMapping("/my-meetings")
    public ResponseEntity<List<Meeting>> getMyMeetings(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(meetingService.getMeetingsByOrganizerEmail(email));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Meeting> getMeetingById(@PathVariable String id) {
        Optional<Meeting> meeting = meetingService.getMeetingById(id);
        return meeting.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Meeting> updateMeeting(
            @PathVariable String id,
            @RequestBody Meeting meetingDetails
    ) {
        Meeting updated = meetingService.updateMeeting(id, meetingDetails);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMeeting(@PathVariable String id) {
        meetingService.deleteMeeting(id);
        return ResponseEntity.noContent().build();
    }
}