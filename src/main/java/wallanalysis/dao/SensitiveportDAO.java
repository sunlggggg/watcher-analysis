package wallanalysis.dao;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import xyz.neolith.wallanalysis.entity.Sensitiveport;
import xyz.neolith.wallanalysis.utils.HibernateUtils;

import java.util.List;

/**
 * @author sunlggggg
 * @date 2017/1/13
 */
public class SensitiveportDAO {
    public List findAll() {
        Session session = HibernateUtils.getInstance().getSession();
        Transaction tx = session.beginTransaction();
        Query query = session.createQuery("select sPort from Sensitiveport sPort");
        List<Sensitiveport> sPorts = query.list();
        tx.commit();
        if(session.isConnected())
            session.close();
        return sPorts;
    }
}
