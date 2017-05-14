package com.xyauto.domain;

import lombok.Data;

/**
 * Created by zhangmg on 2017/5/8.
 */
@Data
public class City {
    private Integer ID;
    private Integer year;
    private String province;
    private String city;
    private Double GDP_city_per; //人均地区生产总额
    private Integer TRSCG_city_per; //社会消费品零售总额
    private Integer Public_Buses_city_No;//市级 公共汽、电车运营数_市辖区
    private Integer Taxi_city_NO;   //市级 出租汽车运营数_市辖区	No. Taxi_city
    private Integer  Urban_Road_Area_nation;   //城市道路面积	Urban Road Area_nation
    private Double  Population_city;    //市级 年末人口数	Population_city

    public City() {
    }

    public City(Integer year, String province, String city) {
        this.year = year;
        this.province = province;
        this.city = city;
    }
}
