package com.xyauto.newmain;

import com.xyauto.SingleCal.*;
import com.xyauto.utils.DBConnection;
import org.apache.commons.dbutils.QueryRunner;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by zhangmg on 2017/5/11.
 */
public class CalculateAllApp {
    private static QueryRunner qr = new QueryRunner(DBConnection.getMasterDataSource());

    public static void main(String[] args) throws Exception {
        long t1 = System.currentTimeMillis();
        System.out.println("运算开始");
        ExecutorService pool = Executors.newFixedThreadPool(6);
      /*  Future submit1 = pool.submit(new MyCallable1(qr));
        Future submit2 = pool.submit(new MyCallable2(qr));
        Future submit3 = pool.submit(new MyCallable3(qr));
        Future submit4 = pool.submit(new MyCallable4(qr));*/
        Future submit5 = pool.submit(new MyCallable5(qr));
       // Future submit6 = pool.submit(new MyCallable6(qr));
       // Integer o1 = (Integer)submit1.get();
       /* Integer o2 = (Integer)submit2.get();
        Integer o3 = (Integer)submit3.get();
        Integer o4 = (Integer)submit4.get();
        Integer o5 = (Integer)submit5.get();
        Integer o6 = (Integer) submit6.get();*/
        long t2 = System.currentTimeMillis();
     /*   if(o1==1&&o2==1&&o3==1&&o4==1&&o5==1&&o6==1){
            System.out.println("运算结束，总共耗时：" + ((t2 - t1) / (1000)) + "秒");
        }*/
        System.out.println("运算结束，总共耗时：" + ((t2 - t1) / (1000)) + "秒");
    }
}

class MyCallable1 implements Callable{
    private QueryRunner qr;

    MyCallable1(QueryRunner qr) {
        this.qr = qr;
    }
    public Integer call() throws Exception {

        System.out.println("城市车型运算开始");
        CityCarserial ccs = new CityCarserial();
        ccs.computeCityCarserial(qr);
        System.out.println("城市车型运算结束");
        return 1;
    }
}
class MyCallable2 implements Callable{
    private QueryRunner qr;

    MyCallable2(QueryRunner qr) {
        this.qr = qr;
    }
    public Integer call() throws Exception {

        System.out.println("省份车型运算开始");
        ProvinceCarserial provinceCarserial = new ProvinceCarserial();
        provinceCarserial.computeProAndCarserial(qr);
        System.out.println("省份车型运算结束");
        return 1;
    }
}




class MyCallable3 implements Callable{
    private QueryRunner qr;

    MyCallable3(QueryRunner qr) {
        this.qr = qr;
    }
    public Integer call() throws Exception {

        System.out.println("城市品牌运算开始");
      CityBrand cityBrand = new CityBrand();
        cityBrand.computeCityBrand(qr);
        System.out.println("城市品牌运算结束");
        return 1;
    }
}

class MyCallable4 implements Callable{
    private QueryRunner qr;

    MyCallable4(QueryRunner qr) {
        this.qr = qr;
    }
    public Integer call() throws Exception {

        System.out.println("省份品牌呢运算开始");
      ProvinceBrand provinceBrand = new ProvinceBrand();
        provinceBrand.computeProBrand(qr);
        System.out.println("省份品牌运算结束");
        return 1;
    }
}

class MyCallable5 implements Callable{
    private QueryRunner qr;

    MyCallable5(QueryRunner qr) {
        this.qr = qr;
    }
    public Integer call() throws Exception {
        System.out.println("城市厂商运算开始");

        CityManufacture cityManufacture = new CityManufacture();
        cityManufacture.computeCityManufacture(qr);
        System.out.println("城市厂商运算结束");
        return 1;
    }
}
class MyCallable6 implements Callable{
    private QueryRunner qr;

    MyCallable6(QueryRunner qr) {
        this.qr = qr;
    }
    public Integer call() throws Exception {
        System.out.println("省份厂商运算开始");
      ProvinceManufacture provinceManufacture = new ProvinceManufacture();
        provinceManufacture.computeProManufacture(qr);
        System.out.println("省份厂商运算结束");
        return 1;
    }
}
