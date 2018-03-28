package xyz.neolith.watcheranalysis;

import org.apache.log4j.Logger;
import xyz.neolith.watcheranalysis.analysis.eventanalysis.EventAbnormalAnalysis;
import xyz.neolith.watcheranalysis.analysis.preprocess.ParseLog;
import xyz.neolith.watcheranalysis.analysis.statisticsanalysis.StatisticsAnalysis;
import xyz.neolith.watcheranalysis.consts.ConfigConsts;
import xyz.neolith.watcheranalysis.dao.AccessFlowResultDAO;
import xyz.neolith.watcheranalysis.dao.FwlogDAO;
import xyz.neolith.watcheranalysis.entity.AccessFlowResult;
import xyz.neolith.watcheranalysis.entity.Event;
import xyz.neolith.watcheranalysis.entity.Fwlog;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author sunlggggg
 * @date 2016/12/22
 */
public class Application {

    private static Logger logger = Logger.getLogger(Application.class);

    private static File file = new File(ConfigConsts.logFile);
    private static long fileLen = file.length();
    static FwlogDAO fwlogDAO = new FwlogDAO();
    //上次保存流量的时间
    private static Timestamp lastTime = fwlogDAO.lastLogTimestamp();
    static {
        if(lastTime == null){
            lastTime = new Timestamp(new Date().getTime());
        }
    }

    private Timestamp currentTime ;

    private static List<Event> events = new ArrayList<>();

    private static AccessFlowResultDAO accessFlowResultDAO = new AccessFlowResultDAO();


    private void analysisFwlogs() throws IOException {
        RandomAccessFile raf;
        raf = new RandomAccessFile(file, "r");
        while (true) {
            //无新的日志
            if (file.length() == fileLen) {
                continue;
            }
            //加1到句首
            raf.seek(fileLen + 1);
            String line = raf.readLine();
            if(line == null) {
                continue;
            }
            line = new String(line.getBytes("ISO-8859-1"), "utf-8");

            logger.info(line);

            fileLen+=line.getBytes().length+1;
            if (!"".equals(line)) {
                line = new String(line.getBytes("ISO-8859-1"), "utf-8");
                Fwlog fwlog = ParseLog.parse(line);

                //save the fwlog into db
                fwlogDAO.insert(fwlog);
                monitorEventsSize();

                //计算event
                EventAbnormalAnalysis.analysis(events, fwlog);


                new StatisticsAnalysis().analysis(fwlog);

                //当前日志时间
                currentTime = fwlogDAO.lastLogTimestamp();
                //流量统计
                if (lastTime != null && currentTime.getTime() - lastTime.getTime() > 1000) {
                    accessFlowResultDAO.insert(new AccessFlowResult(currentTime, fwlogDAO.count(new Date(lastTime.getTime()), new Date(currentTime.getTime()))));
                    lastTime = currentTime;
                }
            }
        }
    }

    private void monitorEventsSize() {
        if (events != null && events.size() > 100000) {
            System.out.println("events is so big ....");
        }
    }

    public static void main(String[] args) throws IOException {
        new Application().analysisFwlogs();
    }
}
