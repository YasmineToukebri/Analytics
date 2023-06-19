package com.example.analytics.repository;

import com.example.analytics.models.EventKpi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EventKpiRepository extends JpaRepository<EventKpi, UUID> {
    long countByUserName(String username);

    @Query("SELECT e.userName FROM EventKpi e GROUP BY e.userName ORDER BY COUNT(e) DESC ")
    List<String> findUsernameWithMostEvents();

    @Query("SELECT e.userName FROM EventKpi e GROUP BY e.userName ORDER BY COUNT(e) ASC")
    List<String> findUsernameWithLeastEvents();

    @Query("SELECT COUNT(e) / COUNT(DISTINCT e.userName) FROM EventKpi e")
    double calculateAverageEventsPerUser();

    @Query("SELECT COUNT(e) FROM EventKpi e WHERE FUNCTION('DATE', e.actionAt) = FUNCTION('CURRENT_DATE')")
    Long findTotalByToday();

    @Query("SELECT COUNT(e) FROM EventKpi e WHERE EXTRACT('year' FROM e.actionAt) = EXTRACT('year' FROM CURRENT_DATE) AND TO_CHAR(e.actionAt, 'IW') = TO_CHAR(CURRENT_DATE, 'IW')")
    Long findTotalByCurrentWeek();

    @Query("SELECT COUNT(e) FROM EventKpi e WHERE FUNCTION('YEAR', e.actionAt) = FUNCTION('YEAR', FUNCTION('CURRENT_DATE')) AND FUNCTION('MONTH', e.actionAt) = FUNCTION('MONTH', FUNCTION('CURRENT_DATE'))")
    Long findTotalByCurrentMonth();

    @Query("SELECT COUNT(e) FROM EventKpi e WHERE FUNCTION('DATE', e.actionAt) = FUNCTION('CURRENT_DATE') AND e.userName=:userName")
    Long findTotalByUsernameToday(String userName);

    @Query("SELECT COUNT(e) FROM EventKpi e WHERE EXTRACT('year' FROM e.actionAt) = EXTRACT('year' FROM CURRENT_DATE) AND TO_CHAR(e.actionAt, 'IW') = TO_CHAR(CURRENT_DATE, 'IW') AND e.userName=:userName")
    Long findTotalByUserNameAndCurrentWeek(String userName);

    @Query("SELECT COUNT(e) FROM EventKpi e WHERE FUNCTION('YEAR', e.actionAt) = FUNCTION('YEAR', FUNCTION('CURRENT_DATE')) AND FUNCTION('MONTH', e.actionAt) = FUNCTION('MONTH', FUNCTION('CURRENT_DATE')) AND e.userName=:userName")
    Long findTotalByUserNameAndCurrentMonth(String userName);
}
