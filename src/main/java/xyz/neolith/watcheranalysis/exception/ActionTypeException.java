package xyz.neolith.watcheranalysis.exception;

/**
 * @author sunlggggg
 * @date 2016/12/17
 */
public class ActionTypeException extends RuntimeException {
    public ActionTypeException() {
        super("log's action is 0 or 1");
    }
}
