package com.xyauto.domain;

import lombok.Data;

/**
 * Created by zhangmg on 2017/5/12.
 */
@Data
public class CountyDomain {
    private Integer ID;
    private Integer yearMonth;
    private String countyName;
    private String provinceName;
    private Float financeIncome_county;
    private Float taxIncome_county;
    private Float expenditure_county;
    private Float science_expenditure_county;
    private Float GDP_county;
    private Float household_register_population;
    private Float yearEnd_population;
    private Float TRSCG_county_per;
    private Float land_area_county;
}
