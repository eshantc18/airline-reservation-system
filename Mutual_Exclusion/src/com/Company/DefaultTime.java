package com.Company;
import java.time.Instant;

public class DefaultTime implements SystemTime{

    public long getTime(){
        long time = Instant.now().toEpochMilli();
        return time;
    }
}
