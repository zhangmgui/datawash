package com.xyauto.domain;

import lombok.Data;

/**
 * Created by zhangmg on 2017/5/8.
 */
@Data
public class GroupLevelDomain {
    private Double overHAVG;
    private Double lowerHAVG;
    private Integer ID;
    private Integer preferenceLevel;
    private Integer year_month;
    private String carserial;
    private String x_ways_id;
    private String province;
    private String city;
    private Integer city_thiscar_preference_index;
    private Integer province_thiscar_preference_index;
}
