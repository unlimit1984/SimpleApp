package my.unlimit.simple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;

/**
 * Created by vladimir on 14.03.2016.
 */
public class SimpleClass {
    static Logger log = LoggerFactory.getLogger(SimpleClass.class);

    public static void main(String[] args) {
        System.out.println(Paths.get(".").toAbsolutePath().normalize().toString());
        //System.out.println(Paths.get(".").toAbsolutePath().toString());
        System.out.println("TEST");

        log.trace("all");
        log.debug("debug");
        log.info("info");
        log.warn("warning");
        log.error("error");
    }

}
