package com.xyauto.main;

import com.xyauto.domain.CarSaleAnalysisDomain;
import com.xyauto.utils.CalculateUtils;
import com.xyauto.utils.DBConnection;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 计算省份对应车型销量的4个指数以及所有中间参数
 * Created by Administrator on 2017/05/07.
 */

public class CalculateProvinceCarSaleVoApp {
    private static QueryRunner qr = new QueryRunner(DBConnection.getMasterDataSource());

    public static void main(String[] args) throws Exception {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                long time = System.currentTimeMillis();
                while (true) {
                    long time1 = System.currentTimeMillis();
                    if ((time1-time) %1000 == 0) {
                        double div = CalculateUtils.div(time1-time, 1000);
                        System.out.println("已经过去:" + div+"秒");
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        thread.setDaemon(true);
        thread.start();

        String someDataSQL = "select year_month, province,x_ways_id, carserial,sum(sales_volume) as province_thiscar_sales_volume,(\n" +
                "select sum(sales_volume) from  city_sales_volume as csvIN where  csvIN.year_month = csvOUT.year_month and csvIN.x_ways_id = csvOUT.x_ways_id\n" +
                ") as wholecountry_thiscar_sales_volume,(select sum(sales_volume) from city_sales_volume as csv3 where csv3.year_month = csvOUT.year_month and csv3.province = csvOUT.province ) as province_allcar_sale_volume\n" +
                ",(select sum(sales_volume) from  city_sales_volume as csv4 where csv4.year_month = csvOUT.year_month ) as wholecountry_allcar_sale_volume\n" +
                "\n" +
                "from  city_sales_volume as csvOUT group by  year_month, province, carserial, x_ways_id ";
        long start = System.currentTimeMillis();
        List<CarSaleAnalysisDomain> tempDataDomains = qr.query(someDataSQL, new BeanListHandler<CarSaleAnalysisDomain>(CarSaleAnalysisDomain.class));
        long end = System.currentTimeMillis();
        System.out.println("查询消耗：" + ((end - start) / (1000)) + "秒");
       /* System.out.println("查询消耗：" + (end - start) + "ms");*/
        Map<Integer, List<CarSaleAnalysisDomain>> yearMonthKeyMap = groupByMonth(tempDataDomains);

        for (CarSaleAnalysisDomain tempDataDomain : tempDataDomains) {
            //当地该车型盘量占比
            tempDataDomain.setProvince_thiscar_plate_volume_proportion(CalculateUtils.div(tempDataDomain.getProvince_thiscar_sales_volume(),
                    tempDataDomain.getWholecountry_thiscar_sales_volume()));
            //当地该车型偏好占比
            tempDataDomain.setProvince_thiscar_preference_proportion(CalculateUtils.div(tempDataDomain.getProvince_thiscar_sales_volume(),
                    tempDataDomain.getProvince_allcar_sale_volume()));

            //当地该车型增长率
            //1、上一期该车型当地销量
            Integer lastMonthThisCarVolume = null;
            Integer lastMonthAllCarInThisProvinceVolume = null;
            Map<String, Integer> lastMonthLocalALLSaleAndOneSaleMap = getLastMonthSaleVolume(tempDataDomain.getYear_month(), tempDataDomain.getProvince(), tempDataDomain.getX_ways_id(), yearMonthKeyMap);
            if (null != lastMonthLocalALLSaleAndOneSaleMap) {
                lastMonthThisCarVolume = lastMonthLocalALLSaleAndOneSaleMap.get("lastMonthThisCar");
                lastMonthAllCarInThisProvinceVolume = lastMonthLocalALLSaleAndOneSaleMap.get("lastMonthAllCar");
            }

            if (null != lastMonthThisCarVolume && 0 != lastMonthThisCarVolume) {
                tempDataDomain.setLast_period_thiscar_sale_volume(lastMonthThisCarVolume); //上一期该地该车销量                //getThiscar_province_sales_volume
                tempDataDomain.setProvince_thiscar_growth_rate(CalculateUtils.div(CalculateUtils.sub(tempDataDomain.getProvince_thiscar_sales_volume(),
                        lastMonthThisCarVolume), lastMonthThisCarVolume));
            }
            if (null != lastMonthAllCarInThisProvinceVolume && 0 != lastMonthAllCarInThisProvinceVolume) {
                try {
                    //当地汽车销量增长率
                    double allCarRate = CalculateUtils.div(CalculateUtils.sub(tempDataDomain.getProvince_allcar_sale_volume(), lastMonthAllCarInThisProvinceVolume), lastMonthAllCarInThisProvinceVolume);
                    tempDataDomain.setProvince_allcar_salevolume_growth_rate(allCarRate);
                } catch (Exception e) {
                    System.out.println(lastMonthAllCarInThisProvinceVolume);
                    System.out.println(tempDataDomain.toString());
                }
            }

            //当地所有车型盘量占比
            tempDataDomain.setProvince_allcar_plate_proportion(CalculateUtils.div(tempDataDomain.getProvince_allcar_sale_volume(),
                    tempDataDomain.getWholecountry_allcar_sale_volume()));

        }

       //计算中间参数
        calculateMediaParam(tempDataDomains);
        calculateFourIndex(tempDataDomains);
        long end1 = System.currentTimeMillis();
        System.out.println("数据计算处理耗时：" + ((end1 - end) / (1000)) + "秒");
       insertData(tempDataDomains);
        long end2 = System.currentTimeMillis();
        System.out.println("数据入库耗时：" + ((end2 - end1) / (1000)) + "秒");
    }
    //修改上期销量
    private static void updateGrowth(List<CarSaleAnalysisDomain> tempDataDomains) throws SQLException {
        String updateSQL = "UPDATE\n" +
                "\t\tbi.dbo.province_month_sales_analysis\n" +
                "\tSET\n" +
                "\t\tlast_period_thiscar_sale_volume = ?,\n" +
                "\t\tprovince_thiscar_growth_rate = ?,\n" +
                "\t\tprovince_allcar_salevolume_growth_rate = ?,\n" +
                "\t\tprovince_thiscar_growth_index = ?\n" +
                "\tWHERE province = ? and x_ways_id=? and year_month = ?";
        for (CarSaleAnalysisDomain tempDataDomain : tempDataDomains) {
            qr.update(updateSQL,tempDataDomain.getLast_period_thiscar_sale_volume(),
                    tempDataDomain.getProvince_thiscar_growth_rate(),
                    tempDataDomain.getProvince_allcar_salevolume_growth_rate(),
                    tempDataDomain.getProvince_thiscar_growth_index(),
                    tempDataDomain.getProvince(),
                    tempDataDomain.getX_ways_id(),
                    tempDataDomain.getYear_month()
                    );
        }

    }

    private static void calculateMediaParam(List<CarSaleAnalysisDomain> tempDataDomains) {
        Map<Integer, List<CarSaleAnalysisDomain>> yearKeyMap = groupByMonth(tempDataDomains);

        Set<Map.Entry<Integer, List<CarSaleAnalysisDomain>>> set1 = yearKeyMap.entrySet();
        for (Map.Entry<Integer, List<CarSaleAnalysisDomain>> entry1 : set1) {
            List<CarSaleAnalysisDomain> list1 = entry1.getValue(); //年份中的分组
            //thisMonthAreaCount
            Map<String, List<CarSaleAnalysisDomain>> provKeyMap = groupByProvince(list1); //按照省份分组
            Set<Map.Entry<String, List<CarSaleAnalysisDomain>>> setA = provKeyMap.entrySet();
            int AreaCount = setA.size();
            for (Map.Entry<String, List<CarSaleAnalysisDomain>> entry : setA) {
                List<CarSaleAnalysisDomain> list2 = entry.getValue();  //年份-省份 组
                for (CarSaleAnalysisDomain carSaleAnalysisDomain : list2) {
                    carSaleAnalysisDomain.setThisMonthAreaCount(AreaCount);
                }
            }

            // wholecountry_thiscar_plate_volume_proportion_average 全国该车型盘量占比平均值
            Map<String, List<CarSaleAnalysisDomain>> CSKeyMap = groupByCS(list1);
            Set<Map.Entry<String, List<CarSaleAnalysisDomain>>> set2 = CSKeyMap.entrySet();
            for (Map.Entry<String, List<CarSaleAnalysisDomain>> entry2 : set2) {
                List<CarSaleAnalysisDomain> list2 = entry2.getValue(); //年份组中的车型分组
                double wcpvSUM = 0;
                double WCavg = 0; //全国该车型盘量平均值
                for (CarSaleAnalysisDomain carSaleAnalysisDomain : list2) {
                    wcpvSUM+=carSaleAnalysisDomain.getProvince_thiscar_plate_volume_proportion();
                }
                WCavg = CalculateUtils.div(wcpvSUM,list2.size());
                for (CarSaleAnalysisDomain carSaleAnalysisDomain : list2) {
                    carSaleAnalysisDomain.setWholecountry_thiscar_plate_volume_proportion_average(WCavg);
                }
            }
        }
    }



    //数据入库
    private static void insertData(List<CarSaleAnalysisDomain> tempDataDomains) throws SQLException {

        String inertSQL = "insert into province_month_sales_analysis(" +
                "year_month," +
                "carserial," +
                "x_ways_id," +
                "province," +
                "province_thiscar_sales_volume," +
                "wholecountry_thiscar_sales_volume," +
                "province_thiscar_plate_volume_proportion," +
                "province_allcar_sale_volume," +
                "province_thiscar_preference_proportion," +
                "province_thiscar_growth_rate," +
                "last_period_thiscar_sale_volume," +
                "wholecountry_allcar_sale_volume," +
                "province_allcar_plate_proportion," +
                "wholecountry_thiscar_plate_volume_proportion_average," +
                "province_thiscar_plate_volume_index," +
                "province_main_carCount_thisMonth," +
                "province_thiscar_preference_index," +
                "province_allcar_salevolume_growth_rate, " +
                "province_thiscar_growth_index,  " +
                "thisMonthAreaCount, " +
                "province_allcar_platevolume_index)" +
                "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        for (CarSaleAnalysisDomain tempDataDomain : tempDataDomains) {
            qr.update(inertSQL, tempDataDomain.getYear_month(),
                    tempDataDomain.getCarserial(),
                    tempDataDomain.getX_ways_id(),
                    tempDataDomain.getProvince(),
                    tempDataDomain.getProvince_thiscar_sales_volume(),
                    tempDataDomain.getWholecountry_thiscar_sales_volume(),
                    tempDataDomain.getProvince_thiscar_plate_volume_proportion(),
                    tempDataDomain.getProvince_allcar_sale_volume(),
                    tempDataDomain.getProvince_thiscar_preference_proportion(),
                    tempDataDomain.getProvince_thiscar_growth_rate(),
                    tempDataDomain.getLast_period_thiscar_sale_volume(),
                    tempDataDomain.getWholecountry_allcar_sale_volume(),
                    tempDataDomain.getProvince_allcar_plate_proportion(),
                    tempDataDomain.getWholecountry_thiscar_plate_volume_proportion_average(),
                    tempDataDomain.getProvince_thiscar_plate_volume_index(),
                    tempDataDomain.getProvince_main_carCount_thisMonth(),
                    tempDataDomain.getProvince_thiscar_preference_index(),
                    tempDataDomain.getProvince_allcar_salevolume_growth_rate(),
                    tempDataDomain.getProvince_thiscar_growth_index(),
                    tempDataDomain.getThisMonthAreaCount(),
                    tempDataDomain.getProvince_allcar_platevolume_index()
                    );
        }
    }


    //获取上一期当地该车型的销量和该地所有车型的销量
    private static Map<String, Integer> getLastMonthSaleVolume(Integer curMonth, String province, String x_ways_id, Map<Integer, List<CarSaleAnalysisDomain>> mapBYMonth) {
        return lastMonthScan(curMonth-100, province, x_ways_id, mapBYMonth);
         /*Integer lastMonth = null;
                if (curMonth.toString().endsWith("01")) { //如果是一月份，那就与去年一月比较
                    lastMonth = curMonth - 100;
                    return lastMonthScan(lastMonth, province, x_ways_id, mapBYMonth);
                } else {

                    Map<String, Integer> lastIntegerMap = lastMonthScan(lastMonth, province, x_ways_id, mapBYMonth);
                    if (lastIntegerMap == null) {
                        lastIntegerMap = lastMonthScan(curMonth - 100, province, x_ways_id, mapBYMonth);
                    }
                    return lastIntegerMap;
                }*/
    }

    //根据月份分组
    public static Map<Integer, List<CarSaleAnalysisDomain>> groupByMonth(List<CarSaleAnalysisDomain> list) {
        return list.stream().collect(Collectors.groupingBy(CarSaleAnalysisDomain::getYear_month));
    }

    //根据省份分组
    public static Map<String, List<CarSaleAnalysisDomain>> groupByProvince(List<CarSaleAnalysisDomain> list) {
        return list.stream().collect(Collectors.groupingBy(CarSaleAnalysisDomain::getProvince));
    }

    //根据车型分组
    public static Map<String, List<CarSaleAnalysisDomain>> groupByCS(List<CarSaleAnalysisDomain> list) {
        return list.stream().collect(Collectors.groupingBy(CarSaleAnalysisDomain::getX_ways_id));
    }

    //计算上一期本地所有车型销量和上个月该车型销量
    public static Map<String, Integer> lastMonthScan(Integer lastMonth, String province, String x_ways_id, Map<Integer, List<CarSaleAnalysisDomain>> mapBYMonth) {
        Integer lastMonthSV = 0;//当地该车型上个月销量
        Integer allCarInThisProvinceLastMonthSV = 0; //上月当地所有汽车销量
        HashMap<String, Integer> lastMonthLocalALLSaleAndOneSaleMap = new HashMap<>();
        List<CarSaleAnalysisDomain> lastMonthList = mapBYMonth.get(lastMonth);
        if (null == lastMonthList) {
            return null;
        } else {
            for (CarSaleAnalysisDomain carSaleAnalysisDomain : lastMonthList) {
                if (province.equals(carSaleAnalysisDomain.getProvince())) { //按月
                    allCarInThisProvinceLastMonthSV += carSaleAnalysisDomain.getProvince_thiscar_sales_volume();
                }
                if (carSaleAnalysisDomain.getProvince().equals(province) && carSaleAnalysisDomain.getX_ways_id().equals(x_ways_id)) {
                    lastMonthSV += carSaleAnalysisDomain.getProvince_thiscar_sales_volume();
                }
            }
            lastMonthLocalALLSaleAndOneSaleMap.put("lastMonthThisCar", lastMonthSV);
            lastMonthLocalALLSaleAndOneSaleMap.put("lastMonthAllCar", allCarInThisProvinceLastMonthSV);
            return lastMonthLocalALLSaleAndOneSaleMap;
        }
    }


    public static void calculateFourIndex(List<CarSaleAnalysisDomain> tempDomains) throws SQLException {

        Map<Integer, List<CarSaleAnalysisDomain>> map1 = tempDomains.stream().collect(Collectors.groupingBy(CarSaleAnalysisDomain::getYear_month));
        Set<Map.Entry<Integer, List<CarSaleAnalysisDomain>>> yearkeyEntrys = map1.entrySet();
        for (Map.Entry<Integer, List<CarSaleAnalysisDomain>> entry1 : yearkeyEntrys) {
            List<CarSaleAnalysisDomain> CSADS = entry1.getValue(); //每个时间一组
            Map<String, List<CarSaleAnalysisDomain>> provinceKeyMap = CSADS.stream().collect(Collectors.groupingBy(CarSaleAnalysisDomain::getProvince));
            Set<Map.Entry<String, List<CarSaleAnalysisDomain>>> entr2 = provinceKeyMap.entrySet();
            for (Map.Entry<String, List<CarSaleAnalysisDomain>> entry2 : entr2) {
                List<CarSaleAnalysisDomain> CSADS2 = entry2.getValue(); //时间组中再按省份分组
                CSADS2.sort(new Comparator<CarSaleAnalysisDomain>() {
                    @Override
                    public int compare(CarSaleAnalysisDomain o1, CarSaleAnalysisDomain o2) {
                        return o1.getProvince_thiscar_preference_proportion().compareTo(o2.getProvince_thiscar_preference_proportion());
                    }
                });
                Collections.reverse(CSADS2);
                Integer mainCarCount = 0; //贡献率前百分之95的车型个数
                Double per = 0d; //控制0.95的标记
                for (CarSaleAnalysisDomain csadBase : CSADS2) {
                    if (CalculateUtils.add(per, csadBase.getProvince_thiscar_preference_proportion()) < 0.95) {
                        per = CalculateUtils.add(per, csadBase.getProvince_thiscar_preference_proportion());
                        mainCarCount++;
                    } else {
                        break;
                    }
                }
                for (CarSaleAnalysisDomain tempDomain : CSADS2) {
                    //1、计算当地该车型盘量指数   （指数为整形）
                    double index1 = CalculateUtils.div(tempDomain.getProvince_thiscar_plate_volume_proportion(), tempDomain.getWholecountry_thiscar_plate_volume_proportion_average());
                    tempDomain.setProvince_thiscar_plate_volume_index(Integer.valueOf(CalculateUtils.formatDouble4(index1 * 100)));
                    //2、计算当地该车型偏好指数
                    tempDomain.setProvince_main_carCount_thisMonth(mainCarCount);
                    String index2 = CalculateUtils.formatDouble4(CalculateUtils.mul(tempDomain.getProvince_thiscar_preference_proportion()*100, tempDomain.getProvince_main_carCount_thisMonth()));
                    tempDomain.setProvince_thiscar_preference_index(Integer.valueOf(index2));
                    //3、当地该车型增长率指数
                    Double thiscarGR = tempDomain.getProvince_thiscar_growth_rate();
                    Double allcarGR = tempDomain.getProvince_allcar_salevolume_growth_rate();
                    if (null != thiscarGR && null != allcarGR && allcarGR != 0) {
                        tempDomain.setProvince_thiscar_growth_index(Integer.valueOf(CalculateUtils.formatDouble4(CalculateUtils.div(thiscarGR, allcarGR) * 100)));
                    }
                    //4、当地所有车型盘量指数
                    Double allCarPP = tempDomain.getProvince_allcar_plate_proportion();
                    Integer areaCount = tempDomain.getThisMonthAreaCount();
                    tempDomain.setProvince_allcar_platevolume_index(Integer.valueOf(CalculateUtils.formatDouble4(allCarPP * areaCount*100)));
                }
            }
        }
    }
}
