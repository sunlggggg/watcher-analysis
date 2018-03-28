package wallanalysis.analysis.preprocess;

import org.apache.log4j.Logger;
import xyz.neolith.wallanalysis.entity.Fwlog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>parse the firewall log for the file
 * because the we need to achieve the goal that could finish the real-time
 * data traffic statistics. Regard the current log's time that read to memory as current time </p>
 *
 * @author sunlggggg
 * @date 2016/12/16
 */
public class ParseLog {

    static Logger logger = Logger.getLogger(ParseLog.class);


    public static Fwlog parse(String log) {


        Fwlog fwlog = new Fwlog();

        //internalIp
        String ipRegex = "(\\d{1,3}\\.){3}\\d{1,3}";


        Pattern r = Pattern.compile(ipRegex);
        Matcher m = r.matcher(log);
        if (m.find()){
            fwlog.setInternalIp(m.group(0));
        }

        //anotherTimestamp
        String anotherTimestampRegex = "\\p{Upper}\\p{Lower}{2} (\\d{2} | \\d )(\\d{2}:){2}\\d{2}";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMM dd HH:mm:ss", Locale.ENGLISH);
        r = Pattern.compile(anotherTimestampRegex);
        m = r.matcher(log);
        if (m.find()) {
            try {
                Calendar a = Calendar.getInstance();

                fwlog.setAnotherTimestamp(simpleDateFormat.parse(a.get(Calendar.YEAR) + m.group(0)));
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        //source ip and port
        String sourceIpAndPort = "(\\d{1,3}\\.){3}\\d{1,3}\\(\\d+\\)->";
        r = Pattern.compile(sourceIpAndPort);
        m = r.matcher(log);
        if (m.find()) {
            String s1 = m.group(0);
            fwlog.setOriginalSrcIp(s1.substring(0, s1.indexOf("(")));
            fwlog.setOriginalSrcPort(s1.substring(s1.indexOf("(") + 1, s1.length() - 3));
            s1 = m.group(0);
            fwlog.setConvertedSrcIp(s1.substring(0, s1.indexOf("(")));
            fwlog.setConvertedSrcPort(s1.substring(s1.indexOf("(") + 1, s1.length() - 3));
        }

        //dest ip and port
        String destIpAndPort = "->(\\d{1,3}\\.){3}\\d{1,3}\\(\\d+\\)";
        r = Pattern.compile(destIpAndPort);
        m = r.matcher(log);
        if (m.find()) {
            String s1 = m.group(0);
            fwlog.setOriginalDestIp(s1.substring(2, s1.indexOf("(")));
            fwlog.setOriginalDestPort(s1.substring(s1.indexOf("(") + 1, s1.length() - 1));
            s1 = m.group(0);
            fwlog.setConvertedDestIp(s1.substring(2, s1.indexOf("(")));
            fwlog.setConvertedDestPort(s1.substring(s1.indexOf("(") + 1, s1.length() - 1));
        }

        //timestamp
        String timestampRegex = "\\d{4}(-\\d{2}){2}: (\\d{2}:){2}\\d{2}";
        r = Pattern.compile(timestampRegex);
        m = r.matcher(log);
        if (m.find()) {
            simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd: HH:mm:ss");
            String s1 = m.group(0);
            try {
                fwlog.setTimestamp(simpleDateFormat.parse(s1));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }



        //matched strategy
        String matchedStrategyRegex = "FW_internetDMZ_Main.+， 原始地址";  //substring (0 , length -6 )
        r = Pattern.compile(matchedStrategyRegex);
        m = r.matcher(log);
        if (m.find()) {
            fwlog.setMathedStrategy(m.group(0).substring(0, m.group(0).length() - 6));
        }

        //protocol
        String protocolRegex = "协议：\\d{1,3}，";
        r = Pattern.compile(protocolRegex);
        m = r.matcher(log);
        if (m.find()) {
            fwlog.setProtocolNumber(Integer.parseInt(m.group(0).substring(3, m.group(0).length() - 1)));
        }

        //safety  margin
        String safetyMarginRegex = "安全域：.+，";
        r = Pattern.compile(safetyMarginRegex);
        m = r.matcher(log);
        while (m.find()) {
            fwlog.setSafefymargin(m.group().substring(4, m.group().length() - 1));
        }

        //action
        r = Pattern.compile("动作：\\D{2}。");
        m = r.matcher(log);
        while (m.find()) {
            if (m.group().substring(3, m.group().length() - 1).equals("允许")) {
                fwlog.setAciton(1);
            } else {
                fwlog.setAciton(0);
            }
        }
        return fwlog;
    }

    public static void main(String[] args) {
        String log = "Aug  1 00:00:00 11.116.14.155 2016-07-31: 23:42:39 FW_internetDMZ_Main:root 03-011-069-0017 Notice Session N/A rep=1 | 匹配到访问策略FOR_SERVER， 原始地址：172.21.181.82(58073)->212.117.201.1(53)， 协议：17，  转换后地址：172.22.181.82(58173)->202.127.211.1(53)， 安全域：outside->server， 动作：允许。";
        System.out.println(parse(log));
    }
}
