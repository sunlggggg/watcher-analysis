package wallanalysis.utils;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author sunlggggg
 * @date 2018/3/27
 */
public class LogWriter {

    public static void writeDos(String sourceLogFilePath, String destLogFilePath, String srcIp, int writeSpeed ) throws IOException, InterruptedException {
        File src = new File(sourceLogFilePath);
        File dest = new File(destLogFilePath);
        BufferedReader br;
        br = new BufferedReader(new FileReader(src));
        FileWriter fo = new FileWriter(dest,true);

        DateFormat df =  new SimpleDateFormat("yyyy-MM-dd: HH:mm:ss");
        Date date = new Date();

        String line;

        int i = 0;
        while ((line = br.readLine()) != null) {

            i++;
            if (i % 10 == 0) {
                Thread.sleep(100);
            }

            //处理line ...
            //Aug  1 00:00:00 10.136.14.155 2016-07-31: 23:32:39 FW_internetDMZ_Main:root 03-01-069-0017 Notice Session N/A rep=1 | 匹配到访问策略FOR_SERVER， 原始地址：
            String sourceIpAndPort = "(\\d{1,3}\\.){3}\\d{1,3}\\(\\d+\\)->";
            Pattern r = Pattern.compile(sourceIpAndPort);
            Matcher m = r.matcher(line);
            if (m.find()) {
                String s1 = m.group(0);
                String oldSrcIp = s1.substring(0, s1.indexOf("("));
                line = line.replaceFirst(oldSrcIp, srcIp);
            }

            line = line.replaceFirst("\\d{4}(-\\d{2}){2}: (\\d{2}:){2}\\d{2}",df.format(date));

            date.setTime(date.getTime() + (long) (writeSpeed + writeSpeed*Math.random()));

            fo.write(line + "\n");
            fo.flush();
        }
    }


    public static void writeNormal(String sourceLogFilePath, String destLogFilePath,  int writeSpeed ) throws IOException, InterruptedException {
        File src = new File(sourceLogFilePath);
        File dest = new File(destLogFilePath);
        BufferedReader br;
        br = new BufferedReader(new FileReader(src));
        FileWriter fo = new FileWriter(dest,true);

        DateFormat df =  new SimpleDateFormat("yyyy-MM-dd: HH:mm:ss");
        Date date = new Date();

        String line;

        int i = 0;
        while ((line = br.readLine()) != null) {

            i++;
            if (i % 10 == 0) {
                Thread.sleep(100);
            }

            line = line.replaceFirst("\\d{4}(-\\d{2}){2}: (\\d{2}:){2}\\d{2}",df.format(date));
            date.setTime(date.getTime() + (long) (writeSpeed + writeSpeed * Math.random()));

            fo.write(line + "\n");
            fo.flush();
        }
    }

}
