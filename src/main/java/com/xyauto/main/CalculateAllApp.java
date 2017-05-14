package com.xyauto.main;

import com.xyauto.SingleCal.*;
import com.xyauto.utils.DBConnection;
import org.apache.commons.dbutils.QueryRunner;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.xyauto.main.CalculateAllApp.getQR;

/**
 * Created by zhangmg on 2017/5/11.
 */
public class CalculateAllApp {
    public static ThreadLocal threadLocalQr = new ThreadLocal<>();
    public static void main(String[] args) throws Exception {

        long t1 = System.currentTimeMillis();
        System.out.println("运算开始");
        ExecutorService pool = Executors.newFixedThreadPool(6);
       Future submit1 = pool.submit(new MyCallable1());
       Future submit2 = pool.submit(new MyCallable2());
        Future submit3 = pool.submit(new MyCallable3());
        Future submit4 = pool.submit(new MyCallable4());
        Future submit5 = pool.submit(new MyCallable5());
        Future submit6 = pool.submit(new MyCallable6());
        Integer o1 = (Integer)submit1.get();
        Integer o2 = (Integer)submit2.get();
        Integer o3 = (Integer)submit3.get();
        Integer o4 = (Integer)submit4.get();
        Integer o5 = (Integer)submit5.get();
        Integer o6 = (Integer) submit6.get();
        long t2 = System.currentTimeMillis();

        System.out.println("运算结束，总共耗时：" + ((t2 - t1) / (1000)) + "秒");
    }
    public static QueryRunner getQR(){
        threadLocalQr.remove();
        QueryRunner qr = (QueryRunner)threadLocalQr.get();
        if(null==qr){
            qr = new QueryRunner(DBConnection.getMasterDataSource());
            threadLocalQr.set(qr);
        }
        return qr;
    }
}

class MyCallable1 implements Callable{
    public Integer call() throws Exception {
        QueryRunner qr = getQR();
        System.out.println("城市车型运算开始");
        CityCarserial ccs = new CityCarserial();
        ccs.computeCityCarserial(qr);
        System.out.println("城市车型运算结束");
        return 1;
    }
}
class MyCallable2 implements Callable{

    public Integer call() throws Exception {
        QueryRunner qr = getQR();
        System.out.println("省份车型运算开始");
        ProvinceCarserial provinceCarserial = new ProvinceCarserial();
        provinceCarserial.computeProAndCarserial(qr);
        System.out.println("省份车型运算结束");
        return 1;
    }
}

class MyCallable3 implements Callable{

    public Integer call() throws Exception {
        QueryRunner qr = getQR();
        System.out.println("城市品牌运算开始");
      CityBrand cityBrand = new CityBrand();
        cityBrand.computeCityBrand(qr);
        System.out.println("城市品牌运算结束");
        return 1;
    }
}

class MyCallable4 implements Callable{
    public Integer call() throws Exception {
        QueryRunner qr = getQR();
        System.out.println("省份品牌呢运算开始");
      ProvinceBrand provinceBrand = new ProvinceBrand();
        provinceBrand.computeProBrand(qr);
        System.out.println("省份品牌运算结束");
        return 1;
    }
}

class MyCallable5 implements Callable{

    public Integer call() throws Exception {
        QueryRunner qr = getQR();
        System.out.println("城市厂商运算开始");
        CityManufacture cityManufacture = new CityManufacture();
        cityManufacture.computeCityManufacture(qr);
        System.out.println("城市厂商运算结束");
        return 1;
    }
}
class MyCallable6 implements Callable{

    public Integer call() throws Exception {
        QueryRunner qr = getQR();
        System.out.println("省份厂商运算开始");
      ProvinceManufacture provinceManufacture = new ProvinceManufacture();
        provinceManufacture.computeProManufacture(qr);
        System.out.println("省份厂商运算结束");
        return 1;
    }
}
