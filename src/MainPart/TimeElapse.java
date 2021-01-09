package MainPart;


/**
 * simple class that would measure time elapsed between to period of time.
 */
public class TimeElapse {
    private static long timeStart =0;
    private static long timeEnd =0;

    /**
     * set starting time .
     */
    public static void start()
    {
        timeStart = System.currentTimeMillis();
    }

    /**
     * end measuring time.
     */
    public static void end()
    {
        timeEnd = System.currentTimeMillis();
    }

    /**
     * get elapsed time between start and end point.
     * @return time elapsed.
     */
    public static long getElapse ()
    {
        return timeEnd - timeStart;
    }

}
