package xyz.neolith.watcheranalysis.analysis.statisticsanalysis;

import xyz.neolith.watcheranalysis.dao.StatisticsResultDAO;
import xyz.neolith.watcheranalysis.entity.StatisticsResult;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author sunlggggg
 * @date 2017/1/16
 */
public class StatisticsResultMerge {
    private static StatisticsResultDAO statisticslogDao = new StatisticsResultDAO();
    private static SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * prepare for merge
     * @param statisticslogs
     * @param startTime
     * @param endTime
     */
    public void merge(List<StatisticsResult> statisticslogs, String startTime, String endTime) throws Exception {

        //the start of the statisticslogs that need to merge
        Date firstTime = statisticslogDao.findMostClosedTime(startTime, false);
        //the end of the statisticslogs that need to merge
        Date lastTime = statisticslogDao.findMostClosedTime(endTime, true);
        if (firstTime == null || lastTime == null ){
            throw new Exception("the startTime or endTime is not to short or too big!");
        }
        //the statisticslogs need to merge
        List<StatisticsResult> maxIntervalStatisticslogs = statisticslogDao.findFirstsMaxInterval(startTime, endTime);

        //no statisticslog need to merge
        if(maxIntervalStatisticslogs.size() > 0 && maxIntervalStatisticslogs.get(0).getStartTime().equals(firstTime) && maxIntervalStatisticslogs.get(0).getEndTime().equals(lastTime)){
            return;
        }

        //start merge
        merge(statisticslogs, firstTime, lastTime, startTime, endTime);

        //save merge'result into dao
        statisticslogDao.saveAll(statisticslogs);
    }

    /**
     * find <code>statisticslog</code> is or is not existed in <code>statisticslogs</code>
     * @param statisticslogs
     * @param statisticslog
     * @return
     */
    private StatisticsResult find(List<StatisticsResult> statisticslogs,
                                  StatisticsResult statisticslog) {
        StatisticsResult ret = null;
        for (StatisticsResult sl : statisticslogs) {
            if (sl.getType().equals(statisticslog.getType()) &&
                    sl.getStatisticsValue().equals(statisticslog.getStatisticsValue())) {
                ret = sl;
                break;
            }
        }
        return ret;
    }

    /**
     * recursion merge
     * @param statisticslogs
     * @param firstTime
     * @param lastTime
     * @param startTime
     * @param endTime
     */
    private void merge(List<StatisticsResult> statisticslogs,
                       Date firstTime, Date lastTime,
                       String startTime, String endTime) {


        //this trip need merge into statisticslogs
        List<StatisticsResult> maxIntervalStatisticslogs = statisticslogDao.findFirstsMaxInterval(startTime, endTime);

        if (maxIntervalStatisticslogs.size() > 0) {
            //update statisticslogs
            for (StatisticsResult statisticslogInMaxInterval : maxIntervalStatisticslogs) {
                StatisticsResult findedStatisticslog = find(statisticslogs, statisticslogInMaxInterval);
                //if not existed in statisticslogs
                if (findedStatisticslog == null) {
                    StatisticsResult newStatisticslog = new StatisticsResult();
                    newStatisticslog.setType(statisticslogInMaxInterval.getType());
                    //the startTime and endTime
                    newStatisticslog.setStartTime(new Timestamp(firstTime.getTime()));
                    newStatisticslog.setEndTime(new Timestamp(lastTime.getTime()));
                    newStatisticslog.setStatisticsValue(statisticslogInMaxInterval.getStatisticsValue());
                    newStatisticslog.setAbnormal(false);
                    //for debug
//                    if (statisticslogInMaxInterval.getStatisticsValue().equals("53")) {
//                        try {
//                            Thread.sleep(1000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }

                    newStatisticslog.setCount(statisticslogInMaxInterval.getCount());
                    //add into statisticslogs
                    statisticslogs.add(newStatisticslog);
                } else {
                    //update the statisticslog in statisticslogs
                    findedStatisticslog.setCount(statisticslogInMaxInterval.getCount() + findedStatisticslog.getCount());
                    //for debug
//                    if (statisticslogInMaxInterval.getStatisticsValue().equals("53")) {
//                        try {
//                            Thread.sleep(1000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
                }
            }
            //forward (time is litter)
            merge(statisticslogs, firstTime, lastTime, startTime, sf.format(maxIntervalStatisticslogs.get(0).getStartTime()));
            //backward (time is greater)
            merge(statisticslogs, firstTime, lastTime, sf.format(maxIntervalStatisticslogs.get(0).getEndTime()), endTime);
        }
    }

    public static void main(String[] args) {
        List<StatisticsResult> list = new ArrayList<>();
        try {
            new StatisticsResultMerge().merge(list, "2016-08-01 01:32:38", "2016-08-01 01:32:43");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(Arrays.asList(list));
    }
}
