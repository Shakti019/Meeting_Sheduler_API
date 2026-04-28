package com.App.live.Domain.Meeting.Repository;

import com.App.live.Domain.Meeting.Model.ActionItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActionItemRepository extends MongoRepository<ActionItem, String> {
    List<ActionItem> findByMeetingId(String meetingId);
    List<ActionItem> findByAssigneeEmail(String assigneeEmail);
}
