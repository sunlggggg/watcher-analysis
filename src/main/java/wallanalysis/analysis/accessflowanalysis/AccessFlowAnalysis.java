package wallanalysis.analysis.accessflowanalysis;

import xyz.neolith.wallanalysis.dao.AccessFlowResultDAO;
import xyz.neolith.wallanalysis.dao.FwlogDAO;
import xyz.neolith.wallanalysis.entity.AccessFlowResult;

import java.sql.Timestamp;
import java.util.Date;


/**
 * @author sunlggggg
 * @date 2018/3/25
 */
public class AccessFlowAnalysis implements Runnable {
    static FwlogDAO fwlogDAO = new FwlogDAO();
    static AccessFlowResultDAO accessFlowResultDAO = new AccessFlowResultDAO();
    /**
     * 进行统计当前秒钟的日志数目
     */
    @Override
    public void run() {
        System.out.println("Thread: accessFlowAnalysis ");
        //最后一条日志的时间
        Timestamp lastTime = fwlogDAO.lastLogTimestamp();
        do{
            Timestamp currentTime  = fwlogDAO.lastLogTimestamp();
            if( lastTime != null && currentTime.getTime() - lastTime.getTime() > 1000){
                accessFlowResultDAO.insert(new AccessFlowResult(currentTime,fwlogDAO.count(new Date(lastTime.getTime()),new Date(currentTime.getTime()))));
                lastTime = currentTime;
            }else {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }while (true);
    }
}
