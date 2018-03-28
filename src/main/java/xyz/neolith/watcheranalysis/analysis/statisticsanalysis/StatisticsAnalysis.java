package xyz.neolith.watcheranalysis.analysis.statisticsanalysis;

import xyz.neolith.watcheranalysis.dao.StatisticsResultDAO;
import xyz.neolith.watcheranalysis.entity.Fwlog;
import xyz.neolith.watcheranalysis.entity.StatisticsResult;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * @author sunlggggg
 * @date 2017/1/14
 */
public class StatisticsAnalysis {

//    private static long INTERVAL = 10 * 60 * 1000;  //ms

    //1000 ms is the minimum Interval , and every interval can merge from this
    public static long MIN_INTERVAL = 1000;

    private static Date lastStatisticsTime = null;
    private static SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    private static StatisticsResultDAO statisticslogDao = new StatisticsResultDAO();


    public void analysis(Fwlog fwlog)  {

//        if (INTERVAL < MIN_INTERVAL) {
//            throw new Exception("the interval is too short! ");
//        }

        if (MIN_INTERVAL % 1000 != 0) {
            try {
                throw new Exception("the MIN_INTERVAL must be multiple of 1000ms！");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Date nowTime = fwlog.getTimestamp();

        if (lastStatisticsTime == null || nowTime.getTime() - lastStatisticsTime.getTime() > MIN_INTERVAL) {
            lastStatisticsTime = fwlog.getTimestamp();
        }
        //statistics this fwlog and update to db
        for (StatisticsResultType statisticsType : StatisticsResultType.values()) {
            // use the reflect to invoke the getter
            char[] cs = statisticsType.name().toCharArray();
            cs[0] -= 32;
            Method getter = null;
            try {
                getter = Fwlog.class.getMethod("get" + String.valueOf(cs));
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
//            Integer id = statisticslogDao.findByValueInTimeQuantum((String) getter.invoke(fwlog), statisticsType, "2016-07-31 17:32:38", "2016-07-31 17:32:39");

//            System.out.println(lastStatisticsTime);
//            System.out.println(sf.format(lastStatisticsTime) +"\t\t"+sf.format(new Date(lastStatisticsTime.getTime() + MIN_INTERVAL)));
            Long id = null;
            try {

                String logValue = (String) getter.invoke(fwlog);

                id = statisticslogDao.findByValueInTimeQuantum(logValue , statisticsType, sf.format(lastStatisticsTime), sf.format(new Date(lastStatisticsTime.getTime() + MIN_INTERVAL)));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

            if (id == null) {
                StatisticsResult statisticslog = new StatisticsResult();
                statisticslog.setCount(1);
                try {
                    System.out.println(getter.invoke(fwlog));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                try {
                    statisticslog.setStatisticsValue((String) getter.invoke(fwlog));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                statisticslog.setType(statisticsType);
                statisticslog.setStartTime(new Timestamp(lastStatisticsTime.getTime()));
                statisticslog.setEndTime(new Timestamp(lastStatisticsTime.getTime() + MIN_INTERVAL));
                statisticslog.setAbnormal(false);
                statisticslogDao.insert(statisticslog);
            } else {
                //update
                statisticslogDao.update(id);
            }
        }


//        if (lastStatisticsTime != null && nowTime.getTime()
//                - lastStatisticsTime.getTime() >= INTERVAL) {
//            lastStatisticsTime = fwlog.getTimestamp();
//
//            // compute the variance and save to db,
//            // compute the statistics of the originalSrcIp's timestamp is
//            // equal or greater than the lastStatisticsTime and is less than
//            // the nowTime
//            new Thread(() -> {
//
//            }).start();
//        }

    }


    public static void main(String[] args) throws InterruptedException, ParseException {

        System.out.println(new Date());
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d = sf.parse("2017-01-15 05:44:42");
        System.out.println(sf.format(d).equals("2017-01-15 05:44:42"));
    }
}
