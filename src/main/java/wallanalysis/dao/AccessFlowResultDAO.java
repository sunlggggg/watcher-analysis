package wallanalysis.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import xyz.neolith.wallanalysis.entity.AccessFlowResult;
import xyz.neolith.wallanalysis.utils.HibernateUtils;

import java.util.Date;


/**
 * @author sunlggggg
 * @date 2018/3/25
 */
public class AccessFlowResultDAO {

    public void insert(AccessFlowResult accessFlowResult ) {
        Transaction tx = null;
        Session session = null;
        try {
            session = HibernateUtils.getInstance().getSession();
            tx = session.beginTransaction();
            session.save(accessFlowResult);
            tx.commit();
        } catch (Exception e) {
            if (tx != null)
                tx.rollback();
        }finally {
            if(session.isConnected())
                session.close();
        }
    }

    public static void main(String[] args) {
        new AccessFlowResultDAO().insert(new AccessFlowResult(new Date(),1));
    }
}
