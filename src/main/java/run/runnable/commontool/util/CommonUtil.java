package run.runnable.commontool.util;

import java.util.Iterator;

/**
 * @author Asher
 * on 2023/10/2
 */
public class CommonUtil {

    public static <T> Iterable<T> getIterableFromIterator(Iterator<T> iterator)
    {
        return () -> iterator;
    }
}
