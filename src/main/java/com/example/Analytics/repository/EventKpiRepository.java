package com.example.Analytics.repository;

import com.example.Analytics.models.EventKpi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EventKpiRepository extends JpaRepository<EventKpi, UUID> {
    public long countByUserName(String Username);
    @Query("SELECT COUNT(e) FROM EventKpi e WHERE FUNCTION('DATE', e.createdAt) = FUNCTION('CURRENT_DATE')")
    Long findTotalByToday();

    @Query("SELECT COUNT(e) FROM EventKpi e WHERE EXTRACT('year' FROM e.createdAt) = EXTRACT('year' FROM CURRENT_DATE) AND TO_CHAR(e.createdAt, 'IW') = TO_CHAR(CURRENT_DATE, 'IW')")
    Long findTotalByCurrentWeek();

    @Query("SELECT COUNT(e) FROM EventKpi e WHERE FUNCTION('YEAR', e.createdAt) = FUNCTION('YEAR', FUNCTION('CURRENT_DATE')) AND FUNCTION('MONTH', e.createdAt) = FUNCTION('MONTH', FUNCTION('CURRENT_DATE'))")
    Long findTotalByCurrentMonth();
    @Query("SELECT COUNT(e) FROM EventKpi e WHERE  FUNCTION('DATE', e.createdAt) = FUNCTION('CURRENT_DATE') AND e.userName=:userName")
    Long findTotalByUsernameToday(String userName);

    @Query("SELECT COUNT(e) FROM EventKpi e WHERE EXTRACT('year' FROM e.createdAt) = EXTRACT('year' FROM CURRENT_DATE) AND TO_CHAR(e.createdAt, 'IW') = TO_CHAR(CURRENT_DATE, 'IW') AND e.userName=:userName")
    Long findTotalByUserNameAndCurrentWeek(String userName);

    @Query("SELECT COUNT(e) FROM EventKpi e WHERE FUNCTION('YEAR', e.createdAt) = FUNCTION('YEAR', FUNCTION('CURRENT_DATE')) AND FUNCTION('MONTH', e.createdAt) = FUNCTION('MONTH', FUNCTION('CURRENT_DATE')) AND e.userName=:userName")
    Long findTotalByUserNameAndCurrentMonth(String userName);
}
