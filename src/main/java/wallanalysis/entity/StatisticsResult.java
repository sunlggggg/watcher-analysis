package wallanalysis.entity;

import xyz.neolith.wallanalysis.analysis.statisticsanalysis.StatisticsResultType;

import javax.persistence.*;
import java.sql.Timestamp;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * @author sunlggggg
 * @date 2017/1/15
 */
@Entity
public class StatisticsResult {
    private Timestamp startTime;
    private String statisticsValue;
    private int count;
    private StatisticsResultType type;
    private Timestamp endTime;
    private Long id;
    private Boolean isAbnormal;

    @Basic
    @Column(name = "startTime")
    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    @Basic
    @Column(name = "statisticsValue")
    public String getStatisticsValue() {
        return statisticsValue;
    }

    public void setStatisticsValue(String statisticsValue) {
        this.statisticsValue = statisticsValue;
    }

    @Basic
    @Column(name = "count")
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Basic
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    public StatisticsResultType getType() {
        return type;
    }

    public void setType(StatisticsResultType type) {
        this.type = type;
    }

    @Basic
    @Column(name = "endTime")
    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "abnormal")
    public Boolean getAbnormal() {
        return isAbnormal;
    }

    public void setAbnormal(Boolean abnormal) {
        isAbnormal = abnormal;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StatisticsResult that = (StatisticsResult) o;

        if (count != that.count) return false;
        if (startTime != null ? !startTime.equals(that.startTime) : that.startTime != null) return false;
        if (statisticsValue != null ? !statisticsValue.equals(that.statisticsValue) : that.statisticsValue != null)
            return false;
        if (type != that.type) return false;
        if (endTime != null ? !endTime.equals(that.endTime) : that.endTime != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        return isAbnormal != null ? isAbnormal.equals(that.isAbnormal) : that.isAbnormal == null;
    }

    @Override
    public int hashCode() {
        int result = startTime != null ? startTime.hashCode() : 0;
        result = 31 * result + (statisticsValue != null ? statisticsValue.hashCode() : 0);
        result = 31 * result + count;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (endTime != null ? endTime.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (isAbnormal != null ? isAbnormal.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "StatisticsResult{" +
                "startTime=" + startTime +
                ", statisticsValue='" + statisticsValue + '\'' +
                ", count=" + count +
                ", type=" + type +
                ", endTime=" + endTime +
                ", id=" + id +
                ", isAbnormal=" + isAbnormal +
                '}';
    }
}
