package wallanalysis.analysis.statisticsanalysis;

import xyz.neolith.wallanalysis.dao.StatisticsResultDAO;
import xyz.neolith.wallanalysis.entity.StatisticsResult;
import xyz.neolith.wallanalysis.exception.NeverMergeException;
import xyz.neolith.wallanalysis.utils.VarianceUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * TODO ... need to optimize the Variance computer method
 * 1、computer the originalSrcIp or originalDestIp variance between startTime and endTime
 * 2、judge the variance is exceed threshold
 *
 * @author sunlggggg
 * @date 2017/1/16
 */
public class VarianceAnalysis {
    //variance threshold
    public static final int ABNORMAL_THRESHOLD = 40;
    private static SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static StatisticsResultDAO statisticslogDao = new StatisticsResultDAO();

    private boolean isNeedMerged(String firstTime, String lastTime) {
        //the statisticslogs need to merge
        List<StatisticsResult> maxIntervalStatisticslogs = statisticslogDao.findFirstsMaxInterval(firstTime, lastTime);

        //no statisticslog need to merge
        if (maxIntervalStatisticslogs.size() > 0 && sf.format(maxIntervalStatisticslogs.get(0).getStartTime()).equals(firstTime) && sf.format(maxIntervalStatisticslogs.get(0).getEndTime()).equals(lastTime)) {
            return false;
        } else {
            return true;
        }
    }


    public void analysis(StatisticsResultType statisticsType, String startTime, String endTime) throws Exception {

        //the start of the statisticslogs that need to merge
        Date firstTime = statisticslogDao.findMostClosedTime(startTime, false);
        //the end of the statisticslogs that need to merge
        Date lastTime = statisticslogDao.findMostClosedTime(endTime, true);


        if (firstTime == null || lastTime == null) {
            throw new Exception("the startTime or endTime is not to short or too big!");
        } else if (firstTime.equals(lastTime)) {
            throw new Exception("the startTime is too closing to endTime");
        } else {
            if (isNeedMerged(sf.format(firstTime), sf.format(lastTime))) {
                throw new NeverMergeException(startTime, endTime);
            } else {
                List<StatisticsResult> statisticslogs = statisticslogDao.findByTypeAndTime(statisticsType, sf.format(firstTime), sf.format(lastTime));
                if (statisticslogs.size() >= 2) {
                    StatisticsResult abnormalSl = findAbnormal(statisticslogs);
                    if (abnormalSl != null) {
                        for (StatisticsResult statisticslog : statisticslogs.subList(statisticslogs.indexOf(abnormalSl),statisticslogs.size())) {
//                            if (Math.abs(statisticslog.getCount() - abnormalSl.getCount()) < computerAverage(statisticslogs))  {
                                statisticslog.setAbnormal(true);
                                statisticslogDao.update(statisticslog);
//                            }
                        }
                    }
                }
            }
        }
    }


    private double computerVariance(List<StatisticsResult> statisticslogs) {
        int numbers[] = new int[statisticslogs.size()];
        for (int i = 0; i < statisticslogs.size(); i++) {
            numbers[i] = statisticslogs.get(i).getCount();
        }
        return Math.pow(VarianceUtils.getStandardDeviation(numbers), 2);
    }

    private double computerAverage(List<StatisticsResult> statisticslogs) {
        int numbers[] = new int[statisticslogs.size()];
        for (int i = 0; i < statisticslogs.size(); i++) {
            numbers[i] = statisticslogs.get(i).getCount();
        }
        return VarianceUtils.getAverage(numbers);
    }

    private StatisticsResult findAbnormal(List<StatisticsResult> statisticslogs) {
        if (statisticslogs.size() == 1) {
            return null;
        }
        return findAbnormalHelper(statisticslogs, null);
    }

    private StatisticsResult findAbnormalHelper(List<StatisticsResult> statisticslogs, StatisticsResult statisticslog) {
        if (computerVariance(statisticslogs) < ABNORMAL_THRESHOLD) {
            if (statisticslog == null) {
                return null;
            } else {
                return statisticslog;
            }
        } else {
            if (statisticslogs.size() == 2) {
                if (statisticslogs.get(0).getCount() > statisticslogs.get(1).getCount()) {
                    return statisticslogs.get(0);
                } else {
                    return statisticslogs.get(1);
                }
            } else {
                return findAbnormalHelper(statisticslogs.subList(0, statisticslogs.size() - 1), statisticslogs.get(statisticslogs.size() - 1));
            }
        }
    }


    public static void main(String[] args) throws Exception {
//        List<StatisticsResult> ls = new ArrayList<>();
//        StatisticsResult s1 = new StatisticsResult();
//        StatisticsResult s2 = new StatisticsResult();
//        StatisticsResult s3 = new StatisticsResult();
//        StatisticsResult s4 = new StatisticsResult();
//        s1.setCount(1);
//        s2.setCount(1);
//        s3.setCount(17);
//        s4.setCount(17);
//        ls.add(s1);
//        ls.add(s2);
//        ls.add(s3);
//        ls.add(s4);
//
//        System.out.println(VarianceUtils.getStandardDeviation(new int[]{1, 10}));
//
//        System.out.println();
//        for (StatisticsResult statisticslog : ls.subList(ls.indexOf(s3), ls.size())) {
//            if (Math.abs(statisticslog.getCount() - s3.getCount()) < VarianceUtils.getAverage(new int[]{1, 1, 17, 17})){
//                System.out.println(statisticslog.getCount());
//            }
//        }

//        System.out.println(new VarianceAnalysis().findAbnormal(ls));
//
////        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


//        List<StatisticsResult> list = new ArrayList<>();
//        try {
//            new StatisticsResultMerge().merge(list, "2016-08-01 01:32:38", "2016-08-01 01:33:01");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//        System.out.println(Arrays.asList(list));

        new VarianceAnalysis().analysis(StatisticsResultType.originalSrcIp, "2016-08-01 01:32:38", "2016-08-01 01:32:39");

    }
}
