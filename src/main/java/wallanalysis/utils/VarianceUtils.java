package wallanalysis.utils;

/**
 * @author sunlggggg
 * @date 2017/1/16
 */
public class VarianceUtils {

    public static double getAverage(int[] array) {
        int sum = 0;
        for (int i = 0; i < array.length; i++) {
            sum += array[i];
        }
        return (sum * 1.0 / array.length);
    }

    public static double getStandardDeviation(int[] array) {
        double sum = 0;
        for (int i = 0; i < array.length; i++) {
            sum += Math.sqrt(((double) array[i] - getAverage(array)) * (array[i] - getAverage(array)));
        }
        return (sum / (array.length - 1));
    }

    public static void main(String[] args) {

        System.out.println(getStandardDeviation(new int[]{1,1,1,900}));
    }
}
