package com.xyauto.utils;

/**
 * Created by zhangmg on 2017/5/11.
 */
public interface InsertSqlUtils {
    public static String proBrandInsertMediaSQL =
           "INSERT\n" +
                   "\tINTO\n" +
                   "\t\tbi.dbo.province_month_carbrand_sales_analysis_detail(\n" +
                   "\t\t\tDimDate,\n" +
                   "\t\t\tDimCar,\n" +
                   "\t\t\tDimArea,\n" +
                   "\t\t\tsaleVol,\n" +
                   "\t\t\tlast_period_saleVol,\n" +
                   "\t\t\tregion_allDimVal_saleVol,\n" +
                   "\t\t\twholeCountry_allDimVal_saleVol,\n" +
                   "\t\t\twholeCountry_dimVal_saleVol,\n" +
                   "\t\t\tplateVol_proportion,\n" +
                   "\t\t\twholecountry_dimVal_plateVol_proportion_avg,\n" +
                   "\t\t\tpreference_proportion,\n" +
                   "\t\t\tregion_main_dimValCount,\n" +
                   "\t\t\tgrowth_rate,\n" +
                   "\t\t\tregion_allDimVal_saleVol_growth_rate,\n" +
                   "\t\t\tregion_allDimVal_plateVol_proportion,\n" +
                   "\t\t\tareaCount\n" +
                   "\t\t)"+
            "\tVALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    public static String proBrandInsertIndexSQL = "INSERT\n" +
            "\tINTO\n" +
            "\t\tbi.dbo.province_month_carbrand_sales_analysis(\n" +
            "\t\t\tDimDate,\n" +
            "\t\t\tDimCar,\n" +
            "\t\t\tDimArea,\n" +
            "\t\t\tplateVol_index,\n" +
            "\t\t\tpreference_index,\n" +
            "\t\t\tgrowth_index,\n" +
            "\t\t\tregion_allDimVal_plateVol_index,\n" +
            "\t\t\tplateVol_level,\n" +
            "\t\t\tpreference_level,\n" +
            "\t\t\tgrowth_level,\n" +
            "\t\t\tregion_allDimVal_plateVol_level\n" +
            "\t\t)\n" +
            "\tVALUES(?,?,?,?,?,?,?,?,?,?,?)";

    public static String cityBrandInsertMediaSQL = "INSERT\n" +
            "\tINTO\n" +
            "\t\tbi.dbo.city_month_carbrand_sales_analysis_detail(\n" +
            "\t\t\tDimDate,\n" +
            "\t\t\tDimCar,\n" +
            "\t\t\tDimArea,\n" +
            "\t\t\tprovince,\n" +
            "\t\t\tsaleVol,\n" +
            "\t\t\tlast_period_saleVol,\n" +
            "\t\t\tregion_allDimVal_saleVol,\n" +
            "\t\t\twholeCountry_allDimVal_saleVol,\n" +
            "\t\t\twholeCountry_dimVal_saleVol,\n" +
            "\t\t\tplateVol_proportion,\n" +
            "\t\t\twholecountry_dimVal_plateVol_proportion_avg,\n" +
            "\t\t\tpreference_proportion,\n" +
            "\t\t\tregion_main_dimValCount,\n" +
            "\t\t\tgrowth_rate,\n" +
            "\t\t\tregion_allDimVal_saleVol_growth_rate,\n" +
            "\t\t\tregion_allDimVal_plateVol_proportion,\n" +
            "\t\t\tareaCount\n" +
            "\t\t)\n" +
            "\tVALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    public static String cityBrandInsertIndexSQL = "INSERT\n" +
            "\tINTO\n" +
            "\t\tbi.dbo.city_month_carbrand_sales_analysis(\n" +
            "\t\t\tDimDate,\n" +
            "\t\t\tDimCar,\n" +
            "\t\t\tDimArea,\n" +
            "\t\t\tprovince,\n" +
            "\t\t\tplateVol_index,\n" +
            "\t\t\tpreference_index,\n" +
            "\t\t\tgrowth_index,\n" +
            "\t\t\tregion_allDimVal_plateVol_index,\n" +
            "\t\t\tplateVol_level,\n" +
            "\t\t\tpreference_level,\n" +
            "\t\t\tgrowth_level,\n" +
            "\t\t\tregion_allDimVal_plateVol_level\n" +
            "\t\t)\n" +
            "\tVALUES(?,?,?,?,?,?,?,?,?,?,?,?)";

    public static String proManufactInsertMediaSQL = "INSERT\n" +
            "\tINTO\n" +
            "\t\tbi.dbo.province_month_manufacturer_sales_analysis_detail(\n" +
            "\t\t\tDimDate,\n" +
            "\t\t\tDimCar,\n" +
            "\t\t\tDimArea,\n" +
            "\t\t\tsaleVol,\n" +
            "\t\t\tlast_period_saleVol,\n" +
            "\t\t\tregion_allDimVal_saleVol,\n" +
            "\t\t\twholeCountry_allDimVal_saleVol,\n" +
            "\t\t\twholeCountry_dimVal_saleVol,\n" +
            "\t\t\tplateVol_proportion,\n" +
            "\t\t\twholecountry_dimVal_plateVol_proportion_avg,\n" +
            "\t\t\tpreference_proportion,\n" +
            "\t\t\tregion_main_dimValCount,\n" +
            "\t\t\tgrowth_rate,\n" +
            "\t\t\tregion_allDimVal_saleVol_growth_rate,\n" +
            "\t\t\tregion_allDimVal_plateVol_proportion,\n" +
            "\t\t\tareaCount\n" +
            "\t\t)\n" +
            "\tVALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    public static String proManufactInsertIndexSQL = "INSERT\n" +
            "\tINTO\n" +
            "\t\tbi.dbo.province_month_manufacturer_sales_analysis(\n" +
            "\t\t\tDimDate,\n" +
            "\t\t\tDimCar,\n" +
            "\t\t\tDimArea,\n" +
            "\t\t\tplateVol_index,\n" +
            "\t\t\tpreference_index,\n" +
            "\t\t\tgrowth_index,\n" +
            "\t\t\tregion_allDimVal_plateVol_index,\n" +
            "\t\t\tplateVol_level,\n" +
            "\t\t\tpreference_level,\n" +
            "\t\t\tgrowth_level,\n" +
            "\t\t\tregion_allDimVal_plateVol_level\n" +
            "\t\t)\n" +
            "\tVALUES(?,?,?,?,?,?,?,?,?,?,?)";

    public static String cityManufactInsertMediaSQL = "INSERT\n" +
            "\tINTO\n" +
            "\t\tbi.dbo.city_month_manufacturer_sales_analysis_detail(\n" +
            "\t\t\tDimDate,\n" +
            "\t\t\tDimCar,\n" +
            "\t\t\tDimArea,\n" +
            "\t\t\tprovince,\n" +
            "\t\t\tsaleVol,\n" +
            "\t\t\tlast_period_saleVol,\n" +
            "\t\t\tregion_allDimVal_saleVol,\n" +
            "\t\t\twholeCountry_allDimVal_saleVol,\n" +
            "\t\t\twholeCountry_dimVal_saleVol,\n" +
            "\t\t\tplateVol_proportion,\n" +
            "\t\t\twholecountry_dimVal_plateVol_proportion_avg,\n" +
            "\t\t\tpreference_proportion,\n" +
            "\t\t\tregion_main_dimValCount,\n" +
            "\t\t\tgrowth_rate,\n" +
            "\t\t\tregion_allDimVal_saleVol_growth_rate,\n" +
            "\t\t\tregion_allDimVal_plateVol_proportion,\n" +
            "\t\t\tareaCount\n" +
            "\t\t)\n" +
            "\tVALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    public static String cityManufactInsertIndexSQL = "INSERT\n" +
            "\tINTO\n" +
            "\t\tbi.dbo.city_month_manufacturer_sales_analysis(\n" +
            "\t\t\tDimDate,\n" +
            "\t\t\tDimCar,\n" +
            "\t\t\tDimArea,\n" +
            "\t\t\tprovince,\n" +
            "\t\t\tplateVol_index,\n" +
            "\t\t\tpreference_index,\n" +
            "\t\t\tgrowth_index,\n" +
            "\t\t\tregion_allDimVal_plateVol_index,\n" +
            "\t\t\tplateVol_level,\n" +
            "\t\t\tpreference_level,\n" +
            "\t\t\tgrowth_level,\n" +
            "\t\t\tregion_allDimVal_plateVol_level\n" +
            "\t\t)\n" +
            "\tVALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
}
