package com.xyauto.newmain;

import com.xyauto.newdomain.MediaParamDomain;
import com.xyauto.utils.CalculateUtils;
import com.xyauto.utils.Constants;
import com.xyauto.utils.DBConnection;
import org.apache.commons.dbutils.QueryRunner;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 市对应车型销量的四个指数
 * Created by zhangmg on 2017/5/9.
 */
public class CalculateMethod {
    private static QueryRunner qr = new QueryRunner(DBConnection.getMasterDataSource());

    public static void calculateProvinceLevel(List<MediaParamDomain> someDatas) {
        //如果是省份，那就按月份算出全国平均值 高平均和低平均
        Map<Integer, List<MediaParamDomain>> timeKeyMap = groupByMonth(someDatas);
        Set<Map.Entry<Integer, List<MediaParamDomain>>> entries = timeKeyMap.entrySet();
        for (Map.Entry<Integer, List<MediaParamDomain>> entry : entries) {
            List<MediaParamDomain> list1 = entry.getValue();  //月份分组的小组
            Map<String, Double> avgMap = calculateAVG(list1);
            setLevel(avgMap, list1);
        }
    }

    public static Map<String, Double> calculateAVG(List<MediaParamDomain> someDatas) {
        Integer lowerSum1 = 0;
        Integer overSum1 = 0;
        Double lowerHAvg1 = null;
        Double overHAvg1 = null;
        int lowerCount1 = 0;
        int overCount1 = 0;

        Integer lowerSum2 = 0;
        Integer overSum2 = 0;
        Double lowerHAvg2 = null;
        Double overHAvg2 = null;
        int lowerCount2 = 0;
        int overCount2 = 0;

        Integer lowerSum3 = 0;
        Integer overSum3 = 0;
        Double lowerHAvg3 = null;
        Double overHAvg3 = null;
        int lowerCount3 = 0;
        int overCount3 = 0;

        Integer lowerSum4 = 0;
        Integer overSum4 = 0;
        Double lowerHAvg4 = null;
        Double overHAvg4 = null;
        int lowerCount4 = 0;
        int overCount4 = 0;

        for (MediaParamDomain temp : someDatas) {
            if (null != temp.getPlateVol_index()) {
                if (temp.getPlateVol_index() >= Constants.indexReference) {
                    overCount1++;
                    overSum1 += temp.getPlateVol_index();
                } else {
                    lowerCount1++;
                    lowerSum1 += temp.getPlateVol_index();
                }
            }

            if (null != temp.getPreference_index()) {
                if (temp.getPreference_index() >= Constants.indexReference) {
                    overCount2++;
                    overSum2 += temp.getPreference_index();
                } else {
                    lowerCount2++;
                    lowerSum2 += temp.getPreference_index();
                }
            }

            if (null != temp.getGrowth_index()) {
                if (temp.getGrowth_index() >= Constants.growthIndexReference) {
                    overCount3++;
                    overSum3 += temp.getGrowth_index();
                } else {
                    lowerCount3++;
                    lowerSum3 += temp.getGrowth_index();
                }
            }

            if (null != temp.getRegion_allDimVal_plateVol_index()) {
                if (temp.getRegion_allDimVal_plateVol_index() >= Constants.indexReference) {
                    overCount4++;
                    overSum4 += temp.getRegion_allDimVal_plateVol_index();
                } else {
                    lowerCount4++;
                    lowerSum4 += temp.getRegion_allDimVal_plateVol_index();
                }
            }

        }
        HashMap<String, Double> map = new HashMap<>();
        if (overCount1 != 0) {
            overHAvg1 = CalculateUtils.div(overSum1, overCount1);
        }
        if (lowerCount1 != 0) {
            lowerHAvg1 = CalculateUtils.div(lowerSum1, lowerCount1);
        }
        if (overCount2 != 0) {
            overHAvg2 = CalculateUtils.div(overSum2, overCount2);
        }
        if (lowerCount2 != 0) {
            lowerHAvg2 = CalculateUtils.div(lowerSum2, lowerCount2);
        }
        if(overCount3!=0){
            overHAvg3 = CalculateUtils.div(overSum3, overCount3);
        }
        if(lowerCount3!=0){
            lowerHAvg3 = CalculateUtils.div(lowerSum3, lowerCount3);
        }
        if(overCount4!=0){
            overHAvg4 = CalculateUtils.div(overSum4, overCount4);
        }
        if(lowerCount4!=0){
            lowerHAvg4 = CalculateUtils.div(lowerSum4, lowerCount4);
        }
        map.put("PlateVol_index_overAvg", overHAvg1);
        map.put("PlateVol_index_lowerAvg", lowerHAvg1);

        map.put("Preference_index_overAvg", overHAvg2);
        map.put("Preference_index_lowerAvg", lowerHAvg2);

        map.put("Growth_index_overAvg", overHAvg3);
        map.put("Growth_index_lowerAvg", lowerHAvg3);

        map.put("Region_allDimVal_plateVol_index_overAvg", overHAvg4);
        map.put("Region_allDimVal_plateVol_index_lowerAvg", lowerHAvg4);
        return map;

    }
        //计算Wholecountry_dimVal_plateVol_proportion_avg 全国或者省份该车型盘量占比均值
    public static void calculateOneNum(boolean isCity, List<MediaParamDomain> someDatas) {
        if (isCity) {//如果是市维度，那就算该省内该月份该车型的盘量占比均值   市维度确定一条数据，按省分组就会有多条，以此求平均
            Map<Integer, List<MediaParamDomain>> timeKeyMap = groupByMonth(someDatas);
            Set<Map.Entry<Integer, List<MediaParamDomain>>> entries = timeKeyMap.entrySet();
            for (Map.Entry<Integer, List<MediaParamDomain>> entry : entries) {
                List<MediaParamDomain> list1 = entry.getValue();//月份小组
                Map<String, List<MediaParamDomain>> provKeyMap = groupByProvince(list1);
                Set<Map.Entry<String, List<MediaParamDomain>>> entries1 = provKeyMap.entrySet();
                for (Map.Entry<String, List<MediaParamDomain>> entry1 : entries1) {
                    List<MediaParamDomain> list2 = entry1.getValue();  //月份组内省份小组
                    Map<String, List<MediaParamDomain>> carIdKeyMap = list2.stream().collect(Collectors.groupingBy(MediaParamDomain::getDimCar));//最后按车型id分组
                    Set<Map.Entry<String, List<MediaParamDomain>>> entries2 = carIdKeyMap.entrySet();
                    for (Map.Entry<String, List<MediaParamDomain>> entry2 : entries2) {
                        List<MediaParamDomain> list3 = entry2.getValue();  //最后的分组为车型
                        setPlateVolAvg(list3);
                    }
                }
            }

        } else {//如果是省维度，那就算全国该月份该车型的盘量占比均值
            Map<Integer, List<MediaParamDomain>> yearKeyMap = groupByMonth(someDatas);
            Set<Map.Entry<Integer, List<MediaParamDomain>>> entries = yearKeyMap.entrySet();

          /*全国该车型盘量占比均值  setWholecountry_dimVal_plateVol_proportion_avg    AAAAA程序处理*/
            for (Map.Entry<Integer, List<MediaParamDomain>> entry : entries) {
                List<MediaParamDomain> thisMonthList = entry.getValue(); //按月份分组
                Map<String, List<MediaParamDomain>> carKeyMap = thisMonthList.stream().collect(Collectors.groupingBy(MediaParamDomain::getDimCar));

                Set<Map.Entry<String, List<MediaParamDomain>>> entries1 = carKeyMap.entrySet();//车型set  长度为车型个数
                for (Map.Entry<String, List<MediaParamDomain>> entry2 : entries1) {
                    List<MediaParamDomain> thisMonthThisCars = entry2.getValue();  //月份车型小组内  长度为城市个数
                    setPlateVolAvg(thisMonthThisCars);
                }
            }
        }
    }

    /*当地该车型盘量指数  city_thiscar_plateVol_index  AAAAA程序处理*/
    /*当地该车型增长率指数 AAAAA程序处理  city_thiscar_growth_index  */
  /*当地该车型偏好指数  city_thiscar_preference_index AAAAA程序处理*/
    /*当地贡献率95%以上车型个数    AAAAA程序处理*/
    /*当地所有车型盘量指数 AAAAA程序处理  city_allcar_plateVol_index  */
    public static void calculateFiveIndex(List<MediaParamDomain> tempDomains) throws SQLException {

        Map<Integer, List<MediaParamDomain>> map1 = tempDomains.stream().collect(Collectors.groupingBy(MediaParamDomain::getDimDate));
        Set<Map.Entry<Integer, List<MediaParamDomain>>> yearkeyEntrys = map1.entrySet();
        for (Map.Entry<Integer, List<MediaParamDomain>> entry1 : yearkeyEntrys) {
            List<MediaParamDomain> CSADS = entry1.getValue(); //每个时间一组
            Map<String, List<MediaParamDomain>> cityKeyMap = CSADS.stream().collect(Collectors.groupingBy(MediaParamDomain::getDimArea));
            Set<Map.Entry<String, List<MediaParamDomain>>> entr2 = cityKeyMap.entrySet();
            for (Map.Entry<String, List<MediaParamDomain>> entry2 : entr2) {
                List<MediaParamDomain> CSADS2 = entry2.getValue(); //时间组中再按省/市分组
                CSADS2.sort(new Comparator<MediaParamDomain>() {
                    @Override
                    public int compare(MediaParamDomain o1, MediaParamDomain o2) {
                        return o1.getPreference_proportion().compareTo(o2.getPreference_proportion());
                    }
                });
                Collections.reverse(CSADS2);
                Integer mainCarCount = 0; //贡献率前百分之95的车型个数
                Double per = 0d; //控制0.95的标记
                for (MediaParamDomain csadBase : CSADS2) {
                    if (CalculateUtils.add(per, csadBase.getPreference_proportion()) < Constants.headNum) {
                        per = CalculateUtils.add(per, csadBase.getPreference_proportion());
                        mainCarCount++;
                    } else {
                        break;
                    }
                }
                for (MediaParamDomain tempDomain : CSADS2) {
                    //1、计算当地该车型盘量指数   （指数为整形）
                    if(tempDomain.getWholecountry_dimVal_plateVol_proportion_avg()!=null){
                        double index1 = CalculateUtils.div(tempDomain.getPlateVol_proportion(), tempDomain.getWholecountry_dimVal_plateVol_proportion_avg());
                        tempDomain.setPlateVol_index(Integer.valueOf(CalculateUtils.formatDouble4(index1 * 100)));
                    }

                    //2、计算当地该车型偏好指数  /*当地贡献率95%以上车型个数  city_main_carCount_thisMonth   AAAAA程序处理*/
                    tempDomain.setRegion_main_dimValCount(mainCarCount);
                    String index2 = CalculateUtils.formatDouble4(CalculateUtils.mul(tempDomain.getPreference_proportion() * 100, tempDomain.getRegion_main_dimValCount()));
                    tempDomain.setPreference_index(Integer.valueOf(index2));
                    //3、当地该车型增长率指数
                    Double thiscarGR = tempDomain.getGrowth_rate();
                    Double allcarGR = tempDomain.getRegion_allDimVal_saleVol_growth_rate();
                    if (null != thiscarGR && null != allcarGR && allcarGR != 0) {
                        tempDomain.setGrowth_index(Integer.valueOf(CalculateUtils.formatDouble4(CalculateUtils.sub(thiscarGR, allcarGR) * 100)));
                    }
                    //4、当地所有车型盘量指数getCity_allcar_plateVol_proportion
                    Double allCarPP = tempDomain.getRegion_allDimVal_plateVol_proportion();
                    Integer areaCount = tempDomain.getAreaCount();
                    //setCity_allcar_plateVol_index
                    tempDomain.setRegion_allDimVal_plateVol_index(Integer.valueOf(CalculateUtils.formatDouble4(allCarPP * areaCount * 100)));
                }
            }
        }
    }

    //根据月份分组
    public static Map<Integer, List<MediaParamDomain>> groupByMonth(List<MediaParamDomain> list) {
        return list.stream().collect(Collectors.groupingBy(MediaParamDomain::getDimDate));
    }

    //根据市分组
    public static Map<String, List<MediaParamDomain>> groupByArea(List<MediaParamDomain> list) {
        return list.stream().collect(Collectors.groupingBy(MediaParamDomain::getDimArea));
    }

    //根据省份分组
    public static Map<String, List<MediaParamDomain>> groupByProvince(List<MediaParamDomain> list) {
        return list.stream().collect(Collectors.groupingBy(MediaParamDomain::getProvince));
    }


    public static void calculateLevel(boolean isCity, List<MediaParamDomain> someDatas) {
        if (isCity) {  //如果是城市，那就按月份省份分组，求省内该车型平均，高平均和低平均
            Map<Integer, List<MediaParamDomain>> timeKeyMap = groupByMonth(someDatas);
            Set<Map.Entry<Integer, List<MediaParamDomain>>> entries = timeKeyMap.entrySet();
            for (Map.Entry<Integer, List<MediaParamDomain>> entry : entries) {
                List<MediaParamDomain> list = entry.getValue();  //此list为月份中的组
                Map<String, List<MediaParamDomain>> provinKeyMap = groupByProvince(list);
                Set<Map.Entry<String, List<MediaParamDomain>>> entries1 = provinKeyMap.entrySet();
                for (Map.Entry<String, List<MediaParamDomain>> entry1 : entries1) {
                    List<MediaParamDomain> list1 = entry1.getValue();  //月份中省份分组
                    Map<String, Double> avgMap = calculateAVG(list1);
                    setLevel(avgMap, list1);
                }
            }
        } else { //如果是省份，那就按月份分组，求全国该车型平均，高平均和低平均
            calculateProvinceLevel(someDatas);
        }
    }

    //设定四个指数的级别
    public static void setLevel(Map<String, Double> avgMap, List<MediaParamDomain> someDatas) {
        Double overHAVG1 = avgMap.get("PlateVol_index_overAvg");
        Double lowerHAVG1 = avgMap.get("PlateVol_index_lowerAvg");

        Double overHAVG2 = avgMap.get("Preference_index_overAvg");
        Double lowerHAVG2 = avgMap.get("Preference_index_lowerAvg");

        Double overHAVG3 = avgMap.get("Growth_index_overAvg");
        Double lowerHAVG3 = avgMap.get("Growth_index_lowerAvg");

        Double overHAVG4 = avgMap.get("Region_allDimVal_plateVol_index_overAvg");
        Double lowerHAVG4 = avgMap.get("Region_allDimVal_plateVol_index_lowerAvg");

        for (MediaParamDomain someData : someDatas) {
            Integer indexTemp1 = someData.getPlateVol_index();
            Integer indexTemp2 = someData.getPreference_index();
            Integer indexTemp3 = someData.getGrowth_index();
            Integer indexTemp4 = someData.getRegion_allDimVal_plateVol_index();
            if (null != indexTemp1) {
                if (indexTemp1 >= Constants.indexReference && indexTemp1 >= overHAVG1) {
                    someData.setPlateVol_level(Constants.level1);
                }
                if (indexTemp1 >= Constants.indexReference && indexTemp1 < overHAVG1) {
                    someData.setPlateVol_level(Constants.level2);
                }
                if (indexTemp1 < Constants.indexReference && indexTemp1 >= lowerHAVG1) {
                    someData.setPlateVol_level(Constants.level3);
                }
                if (indexTemp1 < Constants.indexReference && indexTemp1 < lowerHAVG1) {
                    someData.setPlateVol_level(Constants.level4);
                }
            }

            if (null != indexTemp2) {
                if (indexTemp2 >= Constants.indexReference && indexTemp2 >= overHAVG2) {
                    someData.setPreference_level(Constants.level1);
                }
                if (indexTemp2 >= Constants.indexReference && indexTemp2 < overHAVG2) {
                    someData.setPreference_level(Constants.level2);
                }
                if (indexTemp2 < Constants.indexReference && indexTemp2 >= lowerHAVG2) {
                    someData.setPreference_level(Constants.level3);
                }
                if (indexTemp2 < Constants.indexReference && indexTemp2 < lowerHAVG2) {
                    someData.setPreference_level(Constants.level4);
                }
            }

            if (null != indexTemp3) {
                if (indexTemp3 >= Constants.growthIndexReference && indexTemp3 >= overHAVG3) {
                    someData.setGrowth_level(Constants.level1);
                }
                if (indexTemp3 >= Constants.growthIndexReference && indexTemp3 < overHAVG3) {
                    someData.setGrowth_level(Constants.level2);
                }
                if (indexTemp3 < Constants.growthIndexReference && indexTemp3 >= lowerHAVG3) {
                    someData.setGrowth_level(Constants.level3);
                }
                if (indexTemp3 < Constants.growthIndexReference && indexTemp3 < lowerHAVG3) {
                    someData.setGrowth_level(Constants.level4);
                }

            }

            if (null != indexTemp4) {
                if (indexTemp4 >= Constants.indexReference && indexTemp4 >= overHAVG4) {
                    someData.setRegion_allDimVal_plateVol_level(Constants.level1);
                }
                if (indexTemp4 >= Constants.indexReference && indexTemp4 < overHAVG4) {
                    someData.setRegion_allDimVal_plateVol_level(Constants.level2);
                }
                if (indexTemp4 < Constants.indexReference && indexTemp4 >= lowerHAVG4) {
                    someData.setRegion_allDimVal_plateVol_level(Constants.level3);
                }
                if (indexTemp4 < Constants.indexReference && indexTemp4 < lowerHAVG4) {
                    someData.setRegion_allDimVal_plateVol_level(Constants.level4);
                }
            }

        }
    }

    private static void setPlateVolAvg(List<MediaParamDomain> thisMonthThisCars) {
        int size = thisMonthThisCars.size();
        Double sum = 0d;
        Double avg = null;
        for (MediaParamDomain thisMonthThisCar : thisMonthThisCars) {
            sum += thisMonthThisCar.getPlateVol_proportion();
        }
        if (size != 0) {
            avg = CalculateUtils.div(sum, size);
        }
        for (MediaParamDomain thisMonthThisCar : thisMonthThisCars) {
            thisMonthThisCar.setWholecountry_dimVal_plateVol_proportion_avg(avg);
        }
    }
}
