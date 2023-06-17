package com.example.Analytics.repository;

import com.example.Analytics.models.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;


@Repository
public interface SessionRepository extends JpaRepository<Session, UUID> {
    Session findAllByUserName(String userName);
    Session findAllByRoomId(UUID roomId);

    Session findAllByUserNameAndRoomId(String userName, UUID roomId);

    long countAllByUserName(String username);

    long countAllByRoomId(UUID roomId);

    @Query("SELECT s FROM Session s WHERE s.roomId = ?1 ORDER BY s.duration DESC")
    List<Session> getMaxDurationByRoomId(UUID roomId);

    @Query("SELECT s FROM Session s WHERE s.roomId = ?1 ORDER BY s.duration ASC")
    List<Session>  getMinDurationByRoomId(UUID roomId);


    @Query("SELECT s FROM Session s ORDER BY s.duration DESC")
    List<Session> getMaxDuration();

    @Query("SELECT s FROM Session s ORDER BY s.duration ASC")
    List<Session> getMinDuration();

    @Query("SELECT count(roomId) FROM Session group by roomId order by count(*) ASC")
    List<Long> getMinimalParticipation();

    @Query("SELECT count(roomId) FROM Session group by roomId order by count(*) DESC")
    List<Long> getMaximalParticipation();

}
