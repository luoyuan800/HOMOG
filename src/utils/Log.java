package utils;

import java.util.Date;

/**
 * Created by gluo on 3/23/2017.
 */
public class Log {
    public static void info(String msg){
        System.out.println(new Date() + msg);
    }

    public static void err(String s) {
        System.out.println(s);
    }
}
