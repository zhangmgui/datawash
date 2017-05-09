package com.xyauto.domain;

import lombok.Data;

/**
 * Created by zhangmg on 2017/5/9.
 */
@Data
public class CarSaleAnalysisCityDomain {
    private Integer ID;
    private Integer year_month;
    private String carserial;
    private String x_ways_id;
    private String province;
    private String city;
    private Integer city_thiscar_saleVol;
    private Integer last_period_city_thiscar_saleVol;
    private Integer city_allcar_saleVol;/*当地所有车型销量*/
    private Integer wholeCountry_allcar_saleVol;  /*全国所有车型销量*/
    private Integer wholeCountry_thiscar_saleVol;/*全国该车型销量*/
    private Double city_thiscar_plateVol_proportion; /*当地该车型盘量占比*/
    private Double wholecountry_thiscar_plateVol_proportion_avg;/*全国该车型盘量占比均值      AAAAA程序处理*/
    private Integer city_thiscar_plateVol_index;/*当地该车型盘量指数    AAAAA程序处理*/
    private Double city_thiscar_preference_proportion;/*当地该车型偏好占比*/
    private Integer city_main_carCount_thisMonth;/*当地贡献率95%以上车型个数    AAAAA程序处理*/
    private Integer city_thiscar_preference_index; /*当地该车型偏好指数  AAAAA程序处理*/
    private Double city_thiscar_growth_rate; /*当地该车型增长率*/
    private Double city_allcar_saleVol_growth_rate; /*当地汽车销量增长率*/
    private Integer city_thiscar_growth_index;/*当地该车型增长率指数 AAAAA程序处理  */
    private Double city_allcar_plateVol_proportion;/*当地所有车型盘量占比 */
    private Integer city_allcar_plateVol_index; /*当地所有车型盘量指数 AAAAA程序处理*/
   // private Double wholeCountry_allcar_plateVol_proportion_avg;  /*全国所有车型盘量占比均值  AAAAA程序处理  就是地区个数  省份漏了这个字段*/
    private Integer thisMonthAreaCount; /*地区个数  */
}
