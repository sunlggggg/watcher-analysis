package xyz.neolith.watcheranalysis;

import org.junit.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author sunlggggg
 * @date 2018/3/27
 */

public class RegexTests {
    @Test
    public void testFindAndReplace(){
        String line = "Aug  1 00:00:00 10.136.14.155 2016-07-31: 23:32:39 FW_internetDMZ_Main:root 03-01-069-0018 Notice Session N/A rep=1 | 匹配到访问策略Deny_远程连接， 原始地址：81.0.24.102(35257)->101.67.162.33(23)， 协议：6，  转换后地址：81.0.24.102(35257)->101.67.162.33(23)， 安全域：outside->server， 动作：阻断。";

        Date date= new Date();
        DateFormat df =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        line = line.replaceFirst("\\d{4}(-\\d{2}){2}: (\\d{2}:){2}\\d{2}",df.format(date));

    }
}
