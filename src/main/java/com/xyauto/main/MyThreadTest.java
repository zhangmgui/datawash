package com.xyauto.main;

import com.xyauto.domain.City;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.xyauto.main.MyThreadTest.getCity;

/**
 * Created by Administrator on 2017/05/14.
 */
public class MyThreadTest {
    public static  ThreadLocal threadLocal1 = new ThreadLocal();
    public static  Integer flag = 1;
    public static void main(String[] args){
        ExecutorService pool = Executors.newFixedThreadPool(2);
        pool.submit(new task1());
        pool.submit(new task2());
        pool.submit(new task1());
        pool.submit(new Runnable() {
            @Override
            public void run() {
                City city = getCity();
                System.out.println(city);//此时线程是池子中的，因此变量还是以前那个
                //在线程池中使用threadlocal，注意要每次任务结束都要清空
            }
        });
        System.out.println(getCity()); //主线程自己的变量，新建了一个
    }
    public static City getCity(){
        threadLocal1.remove();  //threadlocal配合线程池使用，每次取得线程之后就清空多个threadocal变量
                                //这样保证线程池中的线程被反复用时，线程本地变量不会出现安全问题
        City city = (City)threadLocal1.get();
        if(null==city){
            city = new City(flag++,Thread.currentThread().getName(),"通城");
            threadLocal1.set(city);
        }
        return city;
    }
}
class task1 implements Callable{
    @Override
    public Object call() throws Exception {
        City city = getCity();
        System.out.println(city);
        return null;
    }
}

class task2 implements Callable{

    @Override
    public Object call() throws Exception {
        City city = getCity();
        System.out.println(city);
        return null;
    }
}
