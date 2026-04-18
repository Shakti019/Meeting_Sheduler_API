package com.App.live.Domain.Meeting.Services;

import com.App.live.Domain.Meeting.Model.Meeting;
import com.App.live.Domain.Meeting.Repository.MeetingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MeetingService {

    private final MeetingRepository meetingRepository;

    public Meeting scheduleMeeting(Meeting meeting) {
        meeting.setCreatedAt(LocalDateTime.now());
        meeting.setUpdatedAt(LocalDateTime.now());
        return meetingRepository.save(meeting);
    }

    public List<Meeting> getAllMeetings() {
        return meetingRepository.findAll();
    }

    public List<Meeting> getMeetingsByOrganizerEmail(String email) {
        return meetingRepository.findByOrganizerEmail(email);
    }

    public Optional<Meeting> getMeetingById(String id) {
        return meetingRepository.findById(id);
    }

    public Meeting updateMeeting(String id, Meeting meetingDetails) {
        Optional<Meeting> meeting = meetingRepository.findById(id);
        if (meeting.isPresent()) {
            Meeting existing = meeting.get();
            existing.setTitle(meetingDetails.getTitle());
            existing.setDescription(meetingDetails.getDescription());
            existing.setStartTime(meetingDetails.getStartTime());
            existing.setEndTime(meetingDetails.getEndTime());
            existing.setUpdatedAt(LocalDateTime.now());
            return meetingRepository.save(existing);
        }
        return null;
    }

    public void deleteMeeting(String id) {
        meetingRepository.deleteById(id);
    }
}