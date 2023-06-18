package com.example.analytics.repository;

import com.example.analytics.dto.CountEventViews;
import com.example.analytics.models.ViewEventAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ViewEventRepository extends JpaRepository<ViewEventAction, UUID> {
    long countAllByEventId(UUID eventId);
    long countAllByUserName(String userName);

    @Query("select count(eventId) as countViews, eventId as eventId  FROM ViewEventAction group by eventId order by countViews ASC")
    List<CountEventViews> countMinViews();

    @Query("SELECT count(eventId) as countViews , eventId as eventId  FROM ViewEventAction group by eventId order by countViews DESC")
    List<CountEventViews> countMaxViews();

}
