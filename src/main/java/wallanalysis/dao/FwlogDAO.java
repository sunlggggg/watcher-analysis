package wallanalysis.dao;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import xyz.neolith.wallanalysis.entity.Fwlog;
import xyz.neolith.wallanalysis.utils.HibernateUtils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author sunlggggg
 * @date 2017/1/12
 */
public class FwlogDAO {
    public void insert(Fwlog fwlog) {

        Transaction tx = null;
        Session session = null;
        try {
            session = HibernateUtils.getInstance().getSession();
            tx = session.beginTransaction();
            session.save(fwlog);
            tx.commit();
        } catch (Exception e) {
            if (tx != null)
                tx.rollback();
        }finally {
            if(session.isConnected())
                session.close();
        }

    }

    public Timestamp lastLogTimestamp(){
        Timestamp timestamp = null;
        Session session;
        Transaction tx = null;
        session = HibernateUtils.getInstance().getSession();
        Query query;
        try {
            tx = session.beginTransaction();
            query = session.createSQLQuery(
                    "select fwlog.`timestamp` " +
                            "from fwlog order by id desc LIMIT 1;");
                    timestamp = (Timestamp) query.uniqueResult();
            tx.commit();
        } catch (Exception e) {
            if (tx != null)
                tx.rollback();
        }finally {
            if(session.isConnected())
                session.close();
        }
        return timestamp;
    }

    public Integer count(Date startTimeStamp, Date endTimeStamp){
        Integer count = 0 ;
        Session session;
        Transaction tx = null;
        session = HibernateUtils.getInstance().getSession();
        try {
            tx = session.beginTransaction();
            Query query = session.createQuery("select count(*) from Fwlog as fl where fl.timestamp >= :st and  fl.timestamp <= :et  ");
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            query.setString("st", dateFormat.format(startTimeStamp));
            query.setString("et", dateFormat.format(endTimeStamp));
            count= ((Number)query.uniqueResult()).intValue();
            tx.commit();
        } catch (Exception e) {
            if (tx != null)
                tx.rollback();
        }finally {
            if(session.isConnected())
                session.close();
        }
        return count;
    }

    public static void main(String[] args) throws ParseException {
       DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
       System.out.println(new FwlogDAO().count(dateFormat.parse("2016-07-31 15:32:39"),
               dateFormat.parse("2016-07-31 15:32:39") ));
    }
}
