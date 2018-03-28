package xyz.neolith.watcheranalysis.attack;


import xyz.neolith.watcheranalysis.consts.ConfigConsts;
import xyz.neolith.watcheranalysis.utils.LogWriter;

import java.io.IOException;

/**
 * @author sunlggggg
 * @date 2018/3/27
 */
public class DosAttack {
    public static void main(String[] args) throws IOException, InterruptedException {
        LogWriter.writeDos(ConfigConsts.sourceLogFile,ConfigConsts.logFile,ConfigConsts.dosSourceIp,10);
    }
}
