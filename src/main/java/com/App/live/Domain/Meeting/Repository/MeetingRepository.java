package com.App.live.Domain.Meeting.Repository;

import com.App.live.Domain.Meeting.Model.Meeting;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeetingRepository extends MongoRepository<Meeting, String> {
    List<Meeting> findByOrganizerEmail(String organizerEmail);
}