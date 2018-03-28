package wallanalysis.dao;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import xyz.neolith.wallanalysis.analysis.statisticsanalysis.StatisticsAnalysis;
import xyz.neolith.wallanalysis.analysis.statisticsanalysis.StatisticsResultType;
import xyz.neolith.wallanalysis.entity.StatisticsResult;
import xyz.neolith.wallanalysis.utils.HibernateUtils;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author sunlggggg
 * @date 2017/1/15
 */
public class StatisticsResultDAO {

    /**
     * save the list
     *
     * @param statisticslogs
     */
    public void saveAll(List<StatisticsResult> statisticslogs) {
        Session session = HibernateUtils.getInstance().getSession();
        Transaction tx = session.beginTransaction();
        for (StatisticsResult statisticslog : statisticslogs) {
            session.save(statisticslog);
        }
        tx.commit();
        if(session.isConnected())
            session.close();
    }

    /**
     * find the id of statisticslog by value and type between startTime and endTime
     *
     * @param logValue
     * @param type
     * @param startTime
     * @param endTime
     * @return
     */
    public Long findByValueInTimeQuantum(String logValue, StatisticsResultType type, String startTime, String endTime) {
        Session session = HibernateUtils.getInstance().getSession();
        Transaction tx = session.beginTransaction();
        Query query = session.createQuery(" SELECT sl.id from StatisticsResult as sl WHERE sl.statisticsValue = :value and sl.startTime = :st and sl.endTime = :et and sl.type =:type");
        query.setString("value", logValue);
        query.setString("st", startTime);
        query.setString("et", endTime);
        query.setString("type", String.valueOf(type));
        List list = query.list();
        Long result = null;
        if (list.size() > 0) {
            result = (Long) query.list().get(0);
        }
        tx.commit();
        if(session.isConnected())
            session.close();
        return result;
    }

    /**
     * save one
     *
     * @param statisticslog
     */
    public void insert(StatisticsResult statisticslog) {
        Session session = HibernateUtils.getInstance().getSession();
        Transaction tx = session.beginTransaction();
        session.save(statisticslog);
        tx.commit();
        if(session.isConnected())
            session.close();
    }

    /**
     * update
     *
     * @param id
     */
    public void update(Long id) {
        Session session = HibernateUtils.getInstance().getSession();
        Transaction tx = session.beginTransaction();
        try {
            Query query = session.createQuery("update StatisticsResult sl set sl.count = sl.count + 1 where sl.id = :id");
            query.setLong("id", id);
            query.executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx != null)
                tx.rollback();
            e.printStackTrace();
        }finally {
            if(session.isConnected())
                session.close();
        }
    }


    public void update(StatisticsResult statisticslog) {
        Session session = HibernateUtils.getInstance().getSession();
        Transaction tx = session.beginTransaction();
        try {
            session.update(statisticslog);
            tx.commit();
        } catch (Exception e) {
            if (tx != null)
                tx.rollback();
            e.printStackTrace();
        }finally {
            if(session.isConnected())
                session.close();
        }
    }

    /**
     * find the max interval statisticslogs between startTime and endTime
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public List<StatisticsResult> findFirstsMaxInterval(String startTime, String endTime) {
        Session session = HibernateUtils.getInstance().getSession();
        Transaction tx = session.beginTransaction();
        List<StatisticsResult> result = null;
        try {
            String sql = "SELECT max(TIMESTAMPDIFF(SECOND,statisticslog.startTime, statisticslog.endTime)) as s from statisticslog WHERE startTime >= :st and endTime <= :et";
            Query queryhelper = session.createSQLQuery(sql);
            queryhelper.setString("st", startTime);
            queryhelper.setString("et", endTime);
            BigInteger max_interval = (BigInteger) queryhelper.uniqueResult();
            String hql = "select sl from StatisticsResult sl  where  sl.startTime = (select min(sl.startTime) from StatisticsResult as sl where sl.startTime >=" +
                    " :startTime and sl.endTime <= :endTime and" +
                    " TIMESTAMPDIFF(SECOND,sl.startTime, sl.endTime) = :max_interval ) and TIMESTAMPDIFF(SECOND,sl.startTime, sl.endTime) = :max_interval ";

            Query query = session.createQuery(hql);
            query.setString("startTime", startTime);
            query.setString("endTime", endTime);
            query.setBigInteger("max_interval", max_interval);
            result = query.list();
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        }finally {
            if(session.isConnected())
                session.close();
        }
        return result;
    }


    /**
     * <p><tr>find the statisticslog's date closed <code>nowTime<code/> , </tr>
     * <tr>before this or after this <tr/>
     * </p>
     *
     * @param nowTime
     * @param beforeOrAfterNowTime true is before false is after
     * @return
     */
    public Date findMostClosedTime(String nowTime, boolean beforeOrAfterNowTime) {
        Session session = HibernateUtils.getInstance().getSession();
        Transaction tx = session.beginTransaction();
        Date result = null;
        try {
            String hql;
            //after
            String nowTimeAdd;
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if (!beforeOrAfterNowTime) {
                nowTimeAdd = sf.format(sf.parse(nowTime).getTime() + StatisticsAnalysis.MIN_INTERVAL);
                hql = "select sl.startTime from StatisticsResult  sl where sl.startTime >= :nowTime and sl.startTime < :nowTimeAdd order by sl.startTime";
            }
            //before
            else {
                hql = "select sl.endTime from StatisticsResult  sl where sl.endTime <= :nowTime and sl.endTime > :nowTimeAdd order by sl.startTime ";
                nowTimeAdd = sf.format(sf.parse(nowTime).getTime() - StatisticsAnalysis.MIN_INTERVAL);
            }

            Query query = session.createQuery(hql);
            query.setString("nowTime", nowTime);
            query.setString("nowTimeAdd", nowTimeAdd);
            List list = query.list();
            if (list.size() > 0) {
                result = (Date) list.get(0);
            }
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        }finally {
            if(session.isConnected())
                session.close();
        }
        return result;
    }


    public List<StatisticsResult> findByTypeAndTime(StatisticsResultType statisticsType, String startTime, String endTime) {
        Session session = HibernateUtils.getInstance().getSession();
        Transaction tx = session.beginTransaction();
        List<StatisticsResult> result = null;
        try {
            Query query = session.createQuery("select  sl from StatisticsResult  sl where sl.startTime = :st and sl.endTime = :et and type = :sltype order by sl.count ");
            query.setString("st", startTime);
            query.setString("et", endTime);
            query.setString("sltype", statisticsType.name());
            result = query.list();
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        }
        return result;
    }

//
//    public int countOfValueInTimeQuantum(String logValue, StatisticsResultType type,  Long timeInterval, String startTime, String endTime) {
//        Session session = HibernateUtils.getInstance().getSession();
//        Transaction tx = session.beginTransaction();
//        Query query = session.createQuery(" SELECT sl.count from StatisticsResult as sl WHERE sl.statisticsValue = :value and TIMESTAMPDIFF(SECOND,sl.startTime, sl.endTime) = :interval and sl.startTime >= :st and sl.endTime <= :et and sl.type =:type");
//        query.setString("value", logValue);
//        query.setLong("interval", timeInterval);
//        query.setString("st", startTime);
//        query.setString("et", endTime);
//        query.setString("type", String.valueOf(type));
//        List list = query.list();
//        Integer result = 0;
//        if (list.size() > 0) {
//            result = (Integer) query.list().get(0);
//        }
//        tx.commit();
//        return result;
//    }

    public static void main(String[] args) {
        System.out.println(new StatisticsResultDAO().findFirstsMaxInterval("2016-08-01 01:32:38", "2016-08-01 01:32:59"));
    }
}
