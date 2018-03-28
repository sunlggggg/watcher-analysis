package wallanalysis.exception;

/**
 * @author sunlggggg
 * @date 2017/1/16
 */
public class NeverMergeException extends Exception {
    public NeverMergeException() {
        super("need to merge !");
    }

    public NeverMergeException(String startTime, String endTime) {
        super("from " + startTime + " to " + endTime + " need to merge !");
    }
}
