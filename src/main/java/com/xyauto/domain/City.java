package com.xyauto.domain;

/**
 * Created by zhangmg on 2017/5/8.
 */
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

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    @Override
    public String toString() {
        return "City{" +
                "ID=" + ID +
                ", year=" + year +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", GDP_city_per=" + GDP_city_per +
                ", TRSCG_city_per=" + TRSCG_city_per +
                ", Public_Buses_city_No=" + Public_Buses_city_No +
                ", Taxi_city_NO=" + Taxi_city_NO +
                ", Urban_Road_Area_nation=" + Urban_Road_Area_nation +
                ", Population_city=" + Population_city +
                '}';
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Double getGDP_city_per() {
        return GDP_city_per;
    }

    public void setGDP_city_per(Double GDP_city_per) {
        this.GDP_city_per = GDP_city_per;
    }

    public Integer getTRSCG_city_per() {
        return TRSCG_city_per;
    }

    public void setTRSCG_city_per(Integer TRSCG_city_per) {
        this.TRSCG_city_per = TRSCG_city_per;
    }

    public Integer getPublic_Buses_city_No() {
        return Public_Buses_city_No;
    }

    public void setPublic_Buses_city_No(Integer public_Buses_city_No) {
        Public_Buses_city_No = public_Buses_city_No;
    }

    public Integer getTaxi_city_NO() {
        return Taxi_city_NO;
    }

    public void setTaxi_city_NO(Integer taxi_city_NO) {
        Taxi_city_NO = taxi_city_NO;
    }

    public Integer getUrban_Road_Area_nation() {
        return Urban_Road_Area_nation;
    }

    public void setUrban_Road_Area_nation(Integer urban_Road_Area_nation) {
        Urban_Road_Area_nation = urban_Road_Area_nation;
    }

    public Double getPopulation_city() {
        return Population_city;
    }

    public void setPopulation_city(Double population_city) {
        Population_city = population_city;
    }

    public City(Integer year, String province, String city) {
        this.year = year;
        this.province = province;
        this.city = city;
    }
}
