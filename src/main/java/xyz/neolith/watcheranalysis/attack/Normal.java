package xyz.neolith.watcheranalysis.attack;

import xyz.neolith.watcheranalysis.consts.ConfigConsts;
import xyz.neolith.watcheranalysis.utils.LogWriter;

import java.io.IOException;

/**
 * @author sunlggggg
 * @date 2018/3/27
 */
public class Normal {
    public static void main(String[] args) throws IOException, InterruptedException {
        LogWriter.writeNormal(ConfigConsts.sourceLogFile,ConfigConsts.logFile,100);
    }
}
