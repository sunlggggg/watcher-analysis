package xyz.neolith.watcheranalysis.entity;

import javax.persistence.*;
import java.util.Date;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * @author sunlggggg
 * @date 2018/3/25
 */
@Entity
public class AccessFlowResult {

    public AccessFlowResult() {}

    public AccessFlowResult( Date timestamp,int count) {
        this.id = id;
        this.count = count;
        this.timestamp = timestamp;
    }

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    private Long id;

    @Basic
    @Column(name = "count")
    private Integer count;

    @Basic
    @Column(name = "endTime")
    private Date timestamp;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

}
