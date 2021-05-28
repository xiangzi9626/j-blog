package cn.example.blog.test;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
public class JavaTest {
    @Test
    public void test() throws Exception {
        SimpleDateFormat format=new SimpleDateFormat("yyyyMMddHHmmss");
        String str=format.format(new Date());
       int a = (int)(Math.random()*(9999-1000+1))+1000;//产生1000-9999的随机数
       System.out.print(str);
    }
}
