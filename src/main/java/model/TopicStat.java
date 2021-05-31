package model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.annotation.Nonnull;
import javax.persistence.*;

@Entity
@Table(name = "topic_stat")
public class TopicStat {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private long id;
    @Column(length = 100, nullable = false)
    private String topic;
    @Column(nullable = false)
    private int frequency;
    @Column(length = 50_000, nullable = false)
    private String references;
    @Column(name = "analysis_id", nullable = false)
    @JsonIgnore
    private long analysisId;

    public TopicStat() {
    }

    public TopicStat(@Nonnull final String topic, final int frequency, @Nonnull final String references, final long analysisId) {
        this.topic = topic;
        this.frequency = frequency;
        this.references = references;
        this.analysisId = analysisId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public String getReferences() {
        return references;
    }

    public void setReferences(String references) {
        this.references = references;
    }

    public long getAnalysisId() {
        return analysisId;
    }

    public void setAnalysisId(long analysisId) {
        this.analysisId = analysisId;
    }
}