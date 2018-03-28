package wallanalysis.analysis.eventanalysis;

import org.apache.log4j.Logger;
import xyz.neolith.wallanalysis.dao.EventDAO;
import xyz.neolith.wallanalysis.dao.SensitiveportDAO;
import xyz.neolith.wallanalysis.entity.Event;
import xyz.neolith.wallanalysis.entity.Fwlog;
import xyz.neolith.wallanalysis.entity.Sensitiveport;

import java.util.Iterator;
import java.util.List;

import static xyz.neolith.wallanalysis.analysis.eventanalysis.EventType.*;

/**
 * @author sunlggggg
 * @date 2017/1/14
 */
public class EventAbnormalAnalysis {

    private static int MINREPEAT = 5;
    //the duration of the longest
    private static long MAXINTERVAL = 1000;
    private static Logger logger = Logger.getLogger(EventAbnormalAnalysis.class);
    private static EventDAO eventDao = new EventDAO();
    private static SensitiveportDAO sensitiveportDao = new SensitiveportDAO();

    public static void analysis(List<Event> events, Fwlog fwlog) {
        //if a kind of incident are not find in the events
        // or the event are  so far to discard from the events,
        // if not create this type event and add into events
        boolean[] isFind = new boolean[EventType.values().length];

        for (Iterator<Event> it = events.iterator(); it.hasNext(); ) {

            Event event = it.next();

            // the interval is greater than MAXINTERVAL , the event will be termination
            if (fwlog.getTimestamp().getTime() - event.getLastTime().getTime() > MAXINTERVAL) {

                //judge the attackType
                switch (event.getAttackType()) {
                    case sameDestAttack:
                    case sameSrcAttack:
                        //if it  is finished  save into db and  remove form the events
                        if (event.getFwlogs().size() >= MINREPEAT) {
                            event.setFinished(true);
                            //save into db (event table and relation table)
                            eventDao.insert(event);
                            logger.info(event);
                        }
                        //remove form events
                        it.remove();
                        break;
                    case sensitivePortAccess:
                        event.setFinished(true);
                        //save into db (event table and relation table)
                        eventDao.insert(event);
                        //remove form events
                        it.remove();
                        break;
                }

            } else {
                switch (event.getAttackType()) {
                    // follow up : use reflect
                    case sameDestAttack:
                        if (fwlog.getOriginalDestIp().equals(event.getFwlogs().iterator().next().getOriginalDestIp())) {
                            event.getFwlogs().add(fwlog);
                            event.setLastTime(fwlog.getTimestamp());
                            isFind[sameDestAttack.ordinal()] = true;
                        }
                        break;

                    case sameSrcAttack:
                        if (fwlog.getOriginalSrcIp().equals(event.getFwlogs().iterator().next().getOriginalSrcIp())) {
                            event.getFwlogs().add(fwlog);
                            event.setLastTime(fwlog.getTimestamp());
                            isFind[sameSrcAttack.ordinal()] = true;
                        }
                        break;

                    case sensitivePortAccess:
                        if (fwlog.getOriginalDestPort().equals(event.getFwlogs().iterator().next().getOriginalDestPort())) {
                            event.getFwlogs().add(fwlog);
                            event.setLastTime(fwlog.getTimestamp());
                            isFind[sensitivePortAccess.ordinal()] = true;
                        }
                        break;
                }
            }
        }

        for (int i = 0; i < isFind.length; i++) {
            if (!isFind[i]) {
                Event event = new Event();
                event.getFwlogs().add(fwlog);
                event.setStartTime(fwlog.getTimestamp());
                event.setLastTime(fwlog.getTimestamp());
                event.setFinished(false);

                if (i == sameDestAttack.ordinal()) {
                    event.setAttackType(sameDestAttack);
                    events.add(event);
                } else if (i == sameSrcAttack.ordinal()) {
                    event.setAttackType(EventType.sameSrcAttack);
                    events.add(event);
                } else if (i == sensitivePortAccess.ordinal()) {

                    List ports = sensitiveportDao.findAll();
                    for (Object s : ports) {
                        if (fwlog.getOriginalDestPort().equals(((Sensitiveport) s).getPort().toString())) {
                            event.setAttackType(EventType.sensitivePortAccess);
                            events.add(event);
                            break;
                        }
                    }
                }
            }
        }
    }
}
