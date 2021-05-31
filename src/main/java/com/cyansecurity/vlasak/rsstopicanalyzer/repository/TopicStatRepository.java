package com.cyansecurity.vlasak.rsstopicanalyzer.repository;

import model.TopicStat;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import java.util.List;

@Repository
public interface TopicStatRepository extends JpaRepository<TopicStat, Long> {

    @Query("SELECT NVL(MAX(ts.analysisId), 0) + 1 FROM TopicStat ts")
    long findNextAnalysisId();

    @Query("SELECT ts FROM TopicStat ts WHERE ts.analysisId = :analysisId ORDER BY ts.frequency DESC")
    List<TopicStat> findTopicsWithHighestFreq(@Param("analysisId") long analysisId, @Nonnull final Pageable pageable);
}
