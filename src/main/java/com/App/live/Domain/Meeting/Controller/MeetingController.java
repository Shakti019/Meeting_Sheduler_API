package com.App.live.Domain.Meeting.Controller;

import java.time.LocalDateTime;
import com.App.live.Domain.Meeting.Model.Meeting;
import com.App.live.Domain.Meeting.Services.MeetingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/meetings")
@RequiredArgsConstructor
public class MeetingController {

    private final MeetingService meetingService;

    private ResponseEntity<?> checkOverlaps(Meeting meeting, String authenticationName, String excludeMeetingId) {
        List<Meeting> allMeetings = meetingService.getAllMeetings();
        List<String> newInvolved = new ArrayList<>();
        newInvolved.add(authenticationName);
        if (meeting.getPanelMembers() != null) {
            newInvolved.addAll(meeting.getPanelMembers());
        }

        for (Meeting m : allMeetings) {
            if (excludeMeetingId != null && m.getId().equals(excludeMeetingId)) {
                continue;
            }
            if (m.getStartTime() == null || m.getEndTime() == null) {
                continue;
            }
            
            boolean overlaps = meeting.getStartTime().isBefore(m.getEndTime()) && meeting.getEndTime().isAfter(m.getStartTime());
            if (overlaps) {
                List<String> existingInvolved = new ArrayList<>();
                existingInvolved.add(m.getOrganizerEmail());
                if (m.getPanelMembers() != null) {
                    existingInvolved.addAll(m.getPanelMembers());
                }

                for (String involvedUser : newInvolved) {
                    if (existingInvolved.contains(involvedUser)) {
                        if (involvedUser.equals(authenticationName)) {
                            return ResponseEntity.badRequest().body("You are already booked for another meeting during this time.");
                        } else {
                            return ResponseEntity.badRequest().body("Panel member " + involvedUser + " is already booked for another meeting during this time.");
                        }
                    }
                }
            }
        }
        return null;
    }

    @PostMapping
    public ResponseEntity<?> scheduleMeeting(
            @RequestBody Meeting meeting,
            Authentication authentication
    ) {
        if (meeting.getStartTime() == null || meeting.getEndTime() == null) {
            return ResponseEntity.badRequest().body("Start time and end time must be provided.");
        }
        if (meeting.getStartTime().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("Cannot schedule a meeting in the past.");
        }
        if (meeting.getEndTime().isBefore(meeting.getStartTime()) || meeting.getEndTime().isEqual(meeting.getStartTime())) {
            return ResponseEntity.badRequest().body("End time must be strictly after start time.");
        }

        ResponseEntity<?> overlapError = checkOverlaps(meeting, authentication.getName(), null);
        if (overlapError != null) return overlapError;

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
    public ResponseEntity<?> updateMeeting(
            @PathVariable String id,
            @RequestBody Meeting meetingDetails,
            Authentication authentication
    ) {
        if (meetingDetails.getStartTime() == null || meetingDetails.getEndTime() == null) {
            return ResponseEntity.badRequest().body("Start time and end time must be provided.");
        }
        if (meetingDetails.getStartTime().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("Cannot update a meeting to start in the past.");
        }
        if (meetingDetails.getEndTime().isBefore(meetingDetails.getStartTime()) || meetingDetails.getEndTime().isEqual(meetingDetails.getStartTime())) {
            return ResponseEntity.badRequest().body("End time must be strictly after start time.");
        }

        Optional<Meeting> existingMeeting = meetingService.getMeetingById(id);
        if (existingMeeting.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (!existingMeeting.get().getOrganizerEmail().equals(authentication.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to edit this meeting.");
        } // User verification constraint

        ResponseEntity<?> overlapError = checkOverlaps(meetingDetails, authentication.getName(), id);
        if (overlapError != null) return overlapError;

        Meeting updated = meetingService.updateMeeting(id, meetingDetails);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMeeting(@PathVariable String id, Authentication authentication) {
        Optional<Meeting> existingMeeting = meetingService.getMeetingById(id);
        if (existingMeeting.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (!existingMeeting.get().getOrganizerEmail().equals(authentication.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to delete this meeting.");
        }

        meetingService.deleteMeeting(id);
        return ResponseEntity.noContent().build();
    }
}