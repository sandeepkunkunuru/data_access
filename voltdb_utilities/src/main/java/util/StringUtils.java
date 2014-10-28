package util;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Created by sandeep on 10/28/14.
 */
public class StringUtils {
    public static String join(String s, Object... a) {
        StringBuilder o = new StringBuilder();
        for (Iterator<Object> i = Arrays.asList(a).iterator(); i.hasNext();)
            o.append(i.next()).append(i.hasNext() ? s : "");
        return o.toString();
    }
}
