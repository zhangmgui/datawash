package com.xyauto.main;

import com.xyauto.domain.CarSaleAnalysisCityDomain;
import com.xyauto.utils.CalculateUtils;
import com.xyauto.utils.DBConnection;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 市对应车型销量的四个指数
 * Created by zhangmg on 2017/5/9.
 */
public class CalculateCityCarSaleVolApp {
    private static QueryRunner qr = new QueryRunner(DBConnection.getMasterDataSource());

    public static void main(String[] args) throws Exception {
        String someDataSQL = "SELECT\n" +
                "\toutTB.year_month,\n" +
                "\toutTB.province,\n" +
                "\toutTB.city,\n" +
                "\toutTB.x_ways_id,\n" +
                "\toutTB.carserial,\n" +
                "\tSUM(outTB.sales_volume) as city_thiscar_saleVol,\n" +
                "\t(\n" +
                "\t\tSELECT SUM(inTB1.sales_volume)\n" +
                "\t\tFROM dbo.city_sales_volume inTB1 WHERE\n" +
                "\t\tinTB1.year_month = outTB.year_month-100 AND inTB1.province =outTB.province AND \n" +
                "\t\tinTB1.x_ways_id=outTB.x_ways_id AND inTB1.city = outTB.city  AND outTB.year_month>201700\n" +
                "\t) as last_period_city_thiscar_saleVol,\n" +
                "\t(\n" +
                "\t\tSELECT SUM(inTB2.sales_volume) from dbo.city_sales_volume inTB2 WHERE\n" +
                "\t\tinTB2.year_month=outTB.year_month \n" +
                "\t\tand inTB2.province = outTB.province and \n" +
                "\t\tinTB2.city=outTB.city\n" +
                "\t) as city_allcar_saleVol,\n" +
                "\t(\n" +
                "\t\tSELECT SUM(inTB3.sales_volume) from dbo.city_sales_volume inTB3 WHERE\n" +
                "\t\tinTB3.year_month=outTB.year_month\n" +
                "\t) as wholeCountry_allcar_saleVol,\n" +
                "\t(\n" +
                "\t\tSELECT SUM(inTB4.sales_volume) FROM dbo.city_sales_volume inTB4 WHERE \n" +
                "\t\tinTB4.year_month=outTB.year_month AND inTB4.x_ways_id = outTB.x_ways_id\n" +
                "\t) as wholeCountry_thiscar_saleVol,\n" +
                "\t(\t\t/*city_thiscar_saleVol*1.0/wholeCountry_thiscar_saleVol*/\n" +
                "\t\tSUM(outTB.sales_volume)*1.0/(SELECT SUM(inTB4.sales_volume) FROM dbo.city_sales_volume inTB4 WHERE \n" +
                "\t\tinTB4.year_month=outTB.year_month AND inTB4.x_ways_id = outTB.x_ways_id)\n" +
                "\t) as city_thiscar_plateVol_proportion,\n" +
                "\t(/*city_thiscar_saleVol*1.0/city_allcar_saleVol*/\n" +
                "\t\tSUM(outTB.sales_volume)*1.0/(SELECT SUM(inTB2.sales_volume) from dbo.city_sales_volume inTB2 WHERE\n" +
                "\t\tinTB2.year_month=outTB.year_month \n" +
                "\t\tand inTB2.province = outTB.province and \n" +
                "\t\tinTB2.city=outTB.city)\n" +
                "\t) as city_thiscar_preference_proportion,\n" +
                "\t( /*city_thiscar_saleVol-last_period_city_thiscar_saleVol/last_period_city_thiscar_saleVol*/\n" +
                "\t\t(SUM(outTB.sales_volume)-(SELECT SUM(inTB1.sales_volume)\n" +
                "\t\tFROM dbo.city_sales_volume inTB1 WHERE\n" +
                "\t\tinTB1.year_month = outTB.year_month-100 AND inTB1.province =outTB.province AND \n" +
                "\t\tinTB1.x_ways_id=outTB.x_ways_id AND inTB1.city = outTB.city  AND outTB.year_month>201700))*1.0/(SELECT SUM(inTB1.sales_volume)\n" +
                "\t\tFROM dbo.city_sales_volume inTB1 WHERE\n" +
                "\t\tinTB1.year_month = outTB.year_month-100 AND inTB1.province =outTB.province AND \n" +
                "\t\tinTB1.x_ways_id=outTB.x_ways_id AND inTB1.city = outTB.city  AND outTB.year_month>201700)\n" +
                "\t\n" +
                "\t) as city_thiscar_growth_rate,\n" +
                "\t(/*(city_allcar_saleVol-lastZIJI)*1.0/lastZIJI  */\n" +
                "\t\t((SELECT SUM(inTB2.sales_volume) from dbo.city_sales_volume inTB2 WHERE\n" +
                "\t\tinTB2.year_month=outTB.year_month \n" +
                "\t\tand inTB2.province = outTB.province and \n" +
                "\t\tinTB2.city=outTB.city)-(SELECT SUM(inTB5.sales_volume)\n" +
                "\t\tFROM dbo.city_sales_volume inTB5 WHERE\n" +
                "\t\tinTB5.year_month = outTB.year_month-100 AND inTB5.province =outTB.province AND \n" +
                "\t\t inTB5.city = outTB.city  AND outTB.year_month>201700\n" +
                "\t))*1.0/(SELECT SUM(inTB5.sales_volume)\n" +
                "\t\tFROM dbo.city_sales_volume inTB5 WHERE\n" +
                "\t\tinTB5.year_month = outTB.year_month-100 AND inTB5.province =outTB.province AND \n" +
                "\t\t inTB5.city = outTB.city  AND outTB.year_month>201700)\n" +
                "\t) as city_allcar_saleVol_growth_rate,\n" +
                "\t(/*city_allcar_saleVol/wholeCountry_allcar_saleVol  */\n" +
                "\t  (SELECT SUM(inTB2.sales_volume) from dbo.city_sales_volume inTB2 WHERE\n" +
                "\t\tinTB2.year_month=outTB.year_month \n" +
                "\t\tand inTB2.province = outTB.province and \n" +
                "\t\tinTB2.city=outTB.city)*1.0/(SELECT SUM(inTB3.sales_volume) from dbo.city_sales_volume inTB3 WHERE\n" +
                "\t\tinTB3.year_month=outTB.year_month)\n" +
                "\t\t\n" +
                "\t) as city_allcar_plateVol_proportion,\n" +
                "\t(\n" +
                "\t\t SELECT count(1) from (SELECT province,city FROM dbo.city_sales_volume inTBLast where inTBLast.year_month=outTB.year_month GROUP BY province,city ) as A\n" +
                "\t\n" +
                "\t) as thisMonthAreaCount\n" +
                "FROM\n" +
                "\tdbo.city_sales_volume outTB\n" +
                "GROUP BY\n" +
                "\tyear_month,\n" +
                "\tprovince,\n" +
                "\tcity,\n" +
                "\tx_ways_id,\n" +
                "\tcarserial order by outTB.year_month DESC";
        long t1 = System.currentTimeMillis();
        List<CarSaleAnalysisCityDomain> someDatas = qr.query(someDataSQL, new BeanListHandler<CarSaleAnalysisCityDomain>(CarSaleAnalysisCityDomain.class));
        long t2 = System.currentTimeMillis();
        System.out.println("查询耗时：" + ((t2 - t1) / (1000)) + "秒");
        calculateThreeNum(someDatas);
        calculateFourIndex(someDatas);
        long t3 = System.currentTimeMillis();
        System.out.println("计算耗时：" + ((t3 - t2) / (1000)) + "秒");
        insertInto(someDatas);
        long t4 = System.currentTimeMillis();
        System.out.println("入库耗时：" + ((t4 - t3) / (1000)) + "秒");
    }



    private static void calculateThreeNum(List<CarSaleAnalysisCityDomain> someDatas){
        Map<Integer, List<CarSaleAnalysisCityDomain>> yearKeyMap = groupByMonth(someDatas);
        Set<Map.Entry<Integer, List<CarSaleAnalysisCityDomain>>> entries = yearKeyMap.entrySet();

          /*全国该车型盘量占比均值  wholecountry_thiscar_plateVol_proportion_avg    AAAAA程序处理*/
        for (Map.Entry<Integer, List<CarSaleAnalysisCityDomain>> entry : entries) {
            List<CarSaleAnalysisCityDomain> thisMonthList = entry.getValue(); //按月份分组
            Map<String, List<CarSaleAnalysisCityDomain>> carKeyMap = thisMonthList.stream().collect(Collectors.groupingBy(CarSaleAnalysisCityDomain::getX_ways_id));

            Set<Map.Entry<String, List<CarSaleAnalysisCityDomain>>> entries1 = carKeyMap.entrySet();//车型set  长度为车型个数
            for (Map.Entry<String, List<CarSaleAnalysisCityDomain>> entry2 : entries1) {
                List<CarSaleAnalysisCityDomain> thisMonthThisCars = entry2.getValue();  //月份车型小组内  长度为城市个数
                int size = thisMonthThisCars.size();
                Double sum = 0d;
                Double avg = 0d;
                for (CarSaleAnalysisCityDomain thisMonthThisCar : thisMonthThisCars) {
                    sum+= thisMonthThisCar.getCity_thiscar_plateVol_proportion();
                }
                avg = CalculateUtils.div(sum,size);
                for (CarSaleAnalysisCityDomain thisMonthThisCar : thisMonthThisCars) {
                    thisMonthThisCar.setWholecountry_thiscar_plateVol_proportion_avg(avg);
                }

            }
        }
    }

    /*当地该车型盘量指数  city_thiscar_plateVol_index  AAAAA程序处理*/
    /*当地该车型增长率指数 AAAAA程序处理  city_thiscar_growth_index  */
  /*当地该车型偏好指数  city_thiscar_preference_index AAAAA程序处理*/
    /*当地贡献率95%以上车型个数    AAAAA程序处理*/
    /*当地所有车型盘量指数 AAAAA程序处理  city_allcar_plateVol_index  */
    private static void calculateFourIndex(List<CarSaleAnalysisCityDomain> tempDomains) throws SQLException {

        Map<Integer, List<CarSaleAnalysisCityDomain>> map1 = tempDomains.stream().collect(Collectors.groupingBy(CarSaleAnalysisCityDomain::getYear_month));
        Set<Map.Entry<Integer, List<CarSaleAnalysisCityDomain>>> yearkeyEntrys = map1.entrySet();
        for (Map.Entry<Integer, List<CarSaleAnalysisCityDomain>> entry1 : yearkeyEntrys) {
            List<CarSaleAnalysisCityDomain> CSADS = entry1.getValue(); //每个时间一组
            Map<String, List<CarSaleAnalysisCityDomain>> cityKeyMap = CSADS.stream().collect(Collectors.groupingBy(CarSaleAnalysisCityDomain::getCity));
            Set<Map.Entry<String, List<CarSaleAnalysisCityDomain>>> entr2 = cityKeyMap.entrySet();
            for (Map.Entry<String, List<CarSaleAnalysisCityDomain>> entry2 : entr2) {
                List<CarSaleAnalysisCityDomain> CSADS2 = entry2.getValue(); //时间组中再按省份分组
                CSADS2.sort(new Comparator<CarSaleAnalysisCityDomain>() {
                    @Override
                    public int compare(CarSaleAnalysisCityDomain o1, CarSaleAnalysisCityDomain o2) {
                        return o1.getCity_thiscar_preference_proportion().compareTo(o2.getCity_thiscar_preference_proportion());
                    }
                });
                Collections.reverse(CSADS2);
                Integer mainCarCount = 0; //贡献率前百分之95的车型个数
                Double per = 0d; //控制0.95的标记
                for (CarSaleAnalysisCityDomain csadBase : CSADS2) {
                    if (CalculateUtils.add(per, csadBase.getCity_thiscar_preference_proportion()) < 0.95) {
                        per = CalculateUtils.add(per, csadBase.getCity_thiscar_preference_proportion());
                        mainCarCount++;
                    } else {
                        break;
                    }
                }
                for (CarSaleAnalysisCityDomain tempDomain : CSADS2) {
                    //1、计算当地该车型盘量指数   （指数为整形）
                    double index1 = CalculateUtils.div(tempDomain.getCity_thiscar_plateVol_proportion(), tempDomain.getWholecountry_thiscar_plateVol_proportion_avg());
                    tempDomain.setCity_thiscar_plateVol_index(Integer.valueOf(CalculateUtils.formatDouble4(index1 * 100)));
                    //2、计算当地该车型偏好指数  /*当地贡献率95%以上车型个数  city_main_carCount_thisMonth   AAAAA程序处理*/
                    tempDomain.setCity_main_carCount_thisMonth(mainCarCount);
                    String index2 = CalculateUtils.formatDouble4(CalculateUtils.mul(tempDomain.getCity_thiscar_preference_proportion() * 100, tempDomain.getCity_main_carCount_thisMonth()));
                    tempDomain.setCity_thiscar_preference_index(Integer.valueOf(index2));
                    //3、当地该车型增长率指数
                    Double thiscarGR = tempDomain.getCity_thiscar_growth_rate();
                    Double allcarGR = tempDomain.getCity_allcar_saleVol_growth_rate();
                    if (null != thiscarGR && null != allcarGR && allcarGR != 0) {
                        tempDomain.setCity_thiscar_growth_index(Integer.valueOf(CalculateUtils.formatDouble4(CalculateUtils.div(thiscarGR, allcarGR) * 100)));
                    }
                    //4、当地所有车型盘量指数
                    Double allCarPP = tempDomain.getCity_allcar_plateVol_proportion();
                    Integer areaCount = tempDomain.getThisMonthAreaCount();
                    tempDomain.setCity_allcar_plateVol_index(Integer.valueOf(CalculateUtils.formatDouble4(allCarPP * areaCount * 100)));
                }
            }
        }
    }

    //根据月份分组
    private static Map<Integer, List<CarSaleAnalysisCityDomain>> groupByMonth(List<CarSaleAnalysisCityDomain> list) {
        return list.stream().collect(Collectors.groupingBy(CarSaleAnalysisCityDomain::getYear_month));
    }

    //根据市分组
    private static Map<String, List<CarSaleAnalysisCityDomain>> groupByProvince(List<CarSaleAnalysisCityDomain> list) {
        return list.stream().collect(Collectors.groupingBy(CarSaleAnalysisCityDomain::getProvince));
    }

    //根据车型分组
    private static Map<String, List<CarSaleAnalysisCityDomain>> groupByCS(List<CarSaleAnalysisCityDomain> list) {
        return list.stream().collect(Collectors.groupingBy(CarSaleAnalysisCityDomain::getX_ways_id));
    }


    private static void insertInto(List<CarSaleAnalysisCityDomain> list) throws SQLException {
        String insertSQL = "INSERT\n" +
                "\tINTO\n" +
                "\t\tbi.dbo.city_month_sales_analysis(\n" +
                "\t\t\tyear_month,\n" +
                "\t\t\tcarserial,\n" +
                "\t\t\tx_ways_id,\n" +
                "\t\t\tprovince,\n" +
                "\t\t\tcity,\n" +
                "\t\t\tcity_thiscar_saleVol,\n" +
                "\t\t\tlast_period_city_thiscar_saleVol,\n" +
                "\t\t\tcity_allcar_saleVol,\n" +
                "\t\t\twholeCountry_allcar_saleVol,\n" +
                "\t\t\twholeCountry_thiscar_saleVol,\n" +
                "\t\t\tcity_thiscar_plateVol_proportion,\n" +
                "\t\t\twholecountry_thiscar_plateVol_proportion_avg,\n" +
                "\t\t\tcity_thiscar_plateVol_index,\n" +
                "\t\t\tcity_thiscar_preference_proportion,\n" +

                "\t\t\tcity_main_carCount_thisMonth,\n" +

                "\t\t\tcity_thiscar_preference_index,\n" +
                "\t\t\tcity_thiscar_growth_rate,\n" +
                "\t\t\tcity_allcar_saleVol_growth_rate,\n" +
                "\t\t\tcity_thiscar_growth_index,\n" +

                "\t\t\tcity_allcar_plateVol_proportion,\n" +
                "\t\t\tcity_allcar_plateVol_index,\n" +
                "\t\t\tthisMonthAreaCount\n" +
                "\t\t)\n" +
                "\tVALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);\n";
        for (CarSaleAnalysisCityDomain carSaleAnalysisCityDomain : list) {
            qr.update(insertSQL,
                    carSaleAnalysisCityDomain.getYear_month(),
                    carSaleAnalysisCityDomain.getCarserial(),
                    carSaleAnalysisCityDomain.getX_ways_id(),
                    carSaleAnalysisCityDomain.getProvince(),
                    carSaleAnalysisCityDomain.getCity(),
                    carSaleAnalysisCityDomain.getCity_thiscar_saleVol(),
                    carSaleAnalysisCityDomain.getLast_period_city_thiscar_saleVol(),
                    carSaleAnalysisCityDomain.getCity_allcar_saleVol(),
                    carSaleAnalysisCityDomain.getWholeCountry_allcar_saleVol(),
                    carSaleAnalysisCityDomain.getWholeCountry_thiscar_saleVol(),
                    carSaleAnalysisCityDomain.getCity_thiscar_plateVol_proportion(),
                    carSaleAnalysisCityDomain.getWholecountry_thiscar_plateVol_proportion_avg(),
                    carSaleAnalysisCityDomain.getCity_thiscar_plateVol_index(),
                    carSaleAnalysisCityDomain.getCity_thiscar_preference_proportion(),
                    carSaleAnalysisCityDomain.getCity_main_carCount_thisMonth(),
                    carSaleAnalysisCityDomain.getCity_thiscar_preference_index(),
                    carSaleAnalysisCityDomain.getCity_thiscar_growth_rate(),
                    carSaleAnalysisCityDomain.getCity_allcar_saleVol_growth_rate(),
                    carSaleAnalysisCityDomain.getCity_thiscar_growth_index(),
                    carSaleAnalysisCityDomain.getCity_allcar_plateVol_proportion(),
                    carSaleAnalysisCityDomain.getCity_allcar_plateVol_index(),
                    carSaleAnalysisCityDomain.getThisMonthAreaCount()
                    );
        }

    }

}
