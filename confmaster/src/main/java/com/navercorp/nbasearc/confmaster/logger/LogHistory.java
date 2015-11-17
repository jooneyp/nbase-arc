package com.navercorp.nbasearc.confmaster.logger;

import static org.apache.log4j.Level.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.navercorp.nbasearc.confmaster.context.ContextType;

public class LogHistory {
    
    private ContextType type = ContextType.CC;
    
    private final Logger logger = Logger.getLogger("confmasterLogger");
    private final List<Log> logs = new ArrayList<Log>();
    
    public void log(Level level, String msg) {
        add(level, msg, null);
    }
    
    public void log(Level level, String msg, Throwable e) {
        add(level, msg, e);
    }
    
    public void add(Level level, String msg, Throwable e) {
        logs.add(new Log(level, new Date(), decorateMessage(msg), e));
        
        // TODO : don't use magic number!
        if (logs.size() >= 100) {
            flush(DEBUG);
        }
    }
    
    public void flush(Level level) {
        if (logs.isEmpty()) {
            return;
        }
        
        StringBuilder sb = new StringBuilder();
        try {
            
            for (Log log : logs) {
                if (!log.getLevel().isGreaterOrEqual(level)) {
                    continue;
                }
                sb.append(log);

                /*
                 * Adding a line separator for readability of logs, 
                 * but when it has an exception, a line separator is at the end of the exception message. 
                 */
                if (!log.hasException()) {
                    sb.append(System.getProperty("line.separator"));
                }
            }
            logs.clear();
            
            if (sb.length() > 0) {
                logger.info(
                    sb.substring(
                        0, sb.length() - System.getProperty("line.separator").length()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void log(LogHistory prevLogHistory) {
        for (Log log : prevLogHistory.logs) {
            logs.add(log);
        }
    }
    
    private String decorateMessage(String msg) {
        return String.format("%s %s", type.toString(), msg);
    }

    public void setContextType(ContextType type) {
        this.type = type;
    }

}