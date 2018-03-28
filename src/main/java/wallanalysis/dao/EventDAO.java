package wallanalysis.dao;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import xyz.neolith.wallanalysis.entity.Event;
import xyz.neolith.wallanalysis.utils.HibernateUtils;

/**
 * @author sunlggggg
 * @date 2017/1/12
 */
public class EventDAO {
    public void insert(Event event) {
        Transaction tx = null;
        Session session = null;
        try {
            session = HibernateUtils.getInstance().getSession();
            tx = session.beginTransaction();
            session.save(event);
            tx.commit();
        } catch (Exception e) {
            if (tx != null)
                tx.rollback();
        }finally {
            if(session.isConnected())
                session.close();
        }
    }

    public Event findById(Long id) {
        Session session = HibernateUtils.getInstance().getSession();
        Transaction tx = session.beginTransaction();
        String hql = "from Event as event where event.id=:id";
        Query query = session.createQuery(hql);
        query.setLong("id", id);
        Event event = null;
        if (query.list().size() > 0) {
            event = (Event) query.list().get(0);
        }
        tx.commit();
        if(session.isConnected())
            session.close();
        return event;
    }


    public Event findFirstNotFinished() {
        Session session = HibernateUtils.getInstance().getSession();
        Transaction tx = session.beginTransaction();
        String hql = "from Event as event where event.isFinished = '0' and event.startTime = (select min (event.startTime) from Event )";
        Query query = session.createQuery(hql);
        Event event = null;
        if (query.list().size() > 0) {
            event = (Event) query.list().get(0);
        }
        tx.commit();
        if(session.isConnected())
            session.close();
        return event;
    }
}
