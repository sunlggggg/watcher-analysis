package xyz.neolith.watcheranalysis.analysis.preprocess;

import org.apache.log4j.Logger;
import xyz.neolith.watcheranalysis.consts.ConfigConsts;
import xyz.neolith.watcheranalysis.entity.Fwlog;
import xyz.neolith.watcheranalysis.exception.FwlogsLargeException;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author sunlggggg
 * @date 2017/1/11
 */
public class ReadFormLogFile {
    private static Logger logger = Logger.getLogger(ReadFormLogFile.class);
    private static long fileLen;
    private static long newfileLen;

    public static List<Fwlog> fwlogs = new LinkedList<>();

    // open a thread to monitor the log file, if new log is coming , to read it
    public static void monitorLogFileIncreased(File file) {
        fileLen = file.length();
        new Thread(() -> {
            while (true) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }

                newfileLen = file.length();

                if (newfileLen > fileLen) {

                    RandomAccessFile raf = null;
                    try {
                        raf = new RandomAccessFile(file, "r");
                        raf.seek(fileLen);
                        while (true) {
                            String line = raf.readLine();
                            if (line != null && !line.equals("")) {
                                line = new String(line.getBytes("ISO-8859-1"), "utf-8");
                                Fwlog fwlog = ParseLog.parse(line);
                                //更改为当前时间
                                fwlog.setTimestamp(new Date());
                                fwlogs.add(fwlog);

                                //to prevent fwlogs too large
                                if (fwlogs.size() > 20 && fwlogs.size() < 50) {
                                    try {
                                        Thread.sleep(100);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                } else if (fwlogs.size() > 50 && fwlogs.size() < 100) {
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                } else if (fwlogs.size() > 100) {
                                    throw new FwlogsLargeException();
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (FwlogsLargeException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (raf != null)
                                raf.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

    public static void main(String[] args) {
        ReadFormLogFile.monitorLogFileIncreased(new File(ConfigConsts.logFile));
    }
}
