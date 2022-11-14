package com.ljk;

import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

public class DateTest {
    @Test
    public void test(){
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-dd-MM HH:mm:ss");
        String datePath = sdf.format(new Date());
        System.out.println(datePath);
    }

    @Test
    public void test1(){
        LocalDateTime localDateTime=LocalDateTime.now();
        System.out.println(localDateTime);
    }
}
