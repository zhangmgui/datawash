package com.xyauto.utils;

/**
 * Created by zhangmg on 2017/5/11.
 */
public interface QuerySqlUtils {
    public static String cityCarserialQuery = "SELECT\n" +
            "\toutTB.year_month as DimDate,\n" +
            "\toutTB.province,\n" +
            "\toutTB.city as DimArea,\n" +
            "\toutTB.x_ways_id as DimCar,\n" +
            "\toutTB.carserial,\n" +
            "\tSUM(outTB.sales_volume) as saleVol,\n" +
            "\t(\n" +
            "\t\tSELECT SUM(inTB1.sales_volume)\n" +
            "\t\tFROM dbo.city_sales_volume inTB1 WHERE\n" +
            "\t\tinTB1.year_month = outTB.year_month-100 AND inTB1.province =outTB.province AND \n" +
            "\t\tinTB1.x_ways_id=outTB.x_ways_id AND inTB1.city = outTB.city  AND outTB.year_month>201700\n" +
            "\t) as last_period_saleVol,\n" +
            "\t(\n" +
            "\t\tSELECT SUM(inTB2.sales_volume) from dbo.city_sales_volume inTB2 WHERE\n" +
            "\t\tinTB2.year_month=outTB.year_month \n" +
            "\t\tand inTB2.province = outTB.province and \n" +
            "\t\tinTB2.city=outTB.city\n" +
            "\t) as region_allDimVal_saleVol,\n" +
            "\t(\n" +
            "\t\tSELECT SUM(inTB3.sales_volume) from dbo.city_sales_volume inTB3 WHERE\n" +
            "\t\tinTB3.year_month=outTB.year_month\n" +
            "\t) as wholeCountry_allDimVal_saleVol,\n" +
            "\t(\n" +
            "\t\tSELECT SUM(inTB4.sales_volume) FROM dbo.city_sales_volume inTB4 WHERE \n" +
            "\t\tinTB4.year_month=outTB.year_month AND inTB4.x_ways_id = outTB.x_ways_id\n" +
            "\t) as wholeCountry_dimVal_saleVol,\n" +
            "\t(\t\t/*city_thiscar_saleVol*1.0/wholeCountry_thiscar_saleVol*/\n" +
            "\t\tSUM(outTB.sales_volume)*1.0/(SELECT SUM(inTB4.sales_volume) FROM dbo.city_sales_volume inTB4 WHERE \n" +
            "\t\tinTB4.year_month=outTB.year_month AND inTB4.x_ways_id = outTB.x_ways_id)\n" +
            "\t) as plateVol_proportion,\n" +
            "\t(/*city_thiscar_saleVol*1.0/city_allcar_saleVol*/\n" +
            "\t\tSUM(outTB.sales_volume)*1.0/(SELECT SUM(inTB2.sales_volume) from dbo.city_sales_volume inTB2 WHERE\n" +
            "\t\tinTB2.year_month=outTB.year_month \n" +
            "\t\tand inTB2.province = outTB.province and \n" +
            "\t\tinTB2.city=outTB.city)\n" +
            "\t) as preference_proportion,\n" +
            "\t( /*city_thiscar_saleVol-last_period_city_thiscar_saleVol/last_period_city_thiscar_saleVol*/\n" +
            "\t\t(SUM(outTB.sales_volume)-(SELECT SUM(inTB1.sales_volume)\n" +
            "\t\tFROM dbo.city_sales_volume inTB1 WHERE\n" +
            "\t\tinTB1.year_month = outTB.year_month-100 AND inTB1.province =outTB.province AND \n" +
            "\t\tinTB1.x_ways_id=outTB.x_ways_id AND inTB1.city = outTB.city  AND outTB.year_month>201700))*1.0/(SELECT SUM(inTB1.sales_volume)\n" +
            "\t\tFROM dbo.city_sales_volume inTB1 WHERE\n" +
            "\t\tinTB1.year_month = outTB.year_month-100 AND inTB1.province =outTB.province AND \n" +
            "\t\tinTB1.x_ways_id=outTB.x_ways_id AND inTB1.city = outTB.city  AND outTB.year_month>201700)\n" +
            "\t\n" +
            "\t) as growth_rate,\n" +
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
            "\t) as region_allDimVal_saleVol_growth_rate,\n" +
            "\t(/*city_allcar_saleVol/wholeCountry_allcar_saleVol  */\n" +
            "\t  (SELECT SUM(inTB2.sales_volume) from dbo.city_sales_volume inTB2 WHERE\n" +
            "\t\tinTB2.year_month=outTB.year_month \n" +
            "\t\tand inTB2.province = outTB.province and \n" +
            "\t\tinTB2.city=outTB.city)*1.0/(SELECT SUM(inTB3.sales_volume) from dbo.city_sales_volume inTB3 WHERE\n" +
            "\t\tinTB3.year_month=outTB.year_month)\n" +
            "\t\t\n" +
            "\t) as region_allDimVal_plateVol_proportion,\n" +
            "\t(\n" +
            "\t\t SELECT count(1) from (SELECT province,city FROM dbo.city_sales_volume inTBLast where inTBLast.year_month=outTB.year_month AND inTBLast.province=outTB.province  GROUP BY province,city ) as A\n" +
            "\t\n" +
            "\t) as areaCount\n" +
            "FROM\n" +
            "\tdbo.city_sales_volume outTB\n" +
            "GROUP BY\n" +
            "\tyear_month,\n" +
            "\tprovince,\n" +
            "\tcity,\n" +
            "\tx_ways_id,\n" +
            "\tcarserial order by outTB.year_month DESC\n" +
            "\t";

    public static String proCarserialQuery = "SELECT\n" +
            "\toutTB.year_month DimDate,\n" +
            "\toutTB.province as DimArea,\n" +
            "\toutTB.x_ways_id as DimCar,\n" +
            "\toutTB.carserial,\n" +
            "\tSUM(outTB.sales_volume) as saleVol,\n" +
            "\t(\n" +
            "\t\tSELECT SUM(inTB1.sales_volume)\n" +
            "\t\tFROM dbo.city_sales_volume inTB1 WHERE\n" +
            "\t\tinTB1.year_month = outTB.year_month-100 AND inTB1.province =outTB.province AND \n" +
            "\t\tinTB1.x_ways_id=outTB.x_ways_id   AND outTB.year_month>201700\n" +
            "\t) as last_period_saleVol,\n" +
            "\t(\n" +
            "\t\tSELECT SUM(inTB2.sales_volume) from dbo.city_sales_volume inTB2 WHERE\n" +
            "\t\tinTB2.year_month=outTB.year_month \n" +
            "\t\tand inTB2.province = outTB.province \n" +
            "\t) as region_allDimVal_saleVol,\n" +
            "\t(\n" +
            "\t\tSELECT SUM(inTB3.sales_volume) from dbo.city_sales_volume inTB3 WHERE\n" +
            "\t\tinTB3.year_month=outTB.year_month\n" +
            "\t) as wholeCountry_allDimVal_saleVol,\n" +
            "\t(\n" +
            "\t\tSELECT SUM(inTB4.sales_volume) FROM dbo.city_sales_volume inTB4 WHERE \n" +
            "\t\tinTB4.year_month=outTB.year_month AND inTB4.x_ways_id = outTB.x_ways_id\n" +
            "\t) as wholeCountry_dimVal_saleVol,\n" +
            "\t(\t\t/*city_thiscar_saleVol*1.0/wholeCountry_thiscar_saleVol*/\n" +
            "\t\tSUM(outTB.sales_volume)*1.0/(SELECT SUM(inTB4.sales_volume) FROM dbo.city_sales_volume inTB4 WHERE \n" +
            "\t\tinTB4.year_month=outTB.year_month AND inTB4.x_ways_id = outTB.x_ways_id)\n" +
            "\t) as plateVol_proportion,\n" +
            "\t(/*city_thiscar_saleVol*1.0/city_allcar_saleVol*/\n" +
            "\t\tSUM(outTB.sales_volume)*1.0/(SELECT SUM(inTB2.sales_volume) from dbo.city_sales_volume inTB2 WHERE\n" +
            "\t\tinTB2.year_month=outTB.year_month \n" +
            "\t\tand inTB2.province = outTB.province)\n" +
            "\t) as preference_proportion,\n" +
            "\t( /*city_thiscar_saleVol-last_period_city_thiscar_saleVol/last_period_city_thiscar_saleVol*/\n" +
            "\t\t(SUM(outTB.sales_volume)-(SELECT SUM(inTB1.sales_volume)\n" +
            "\t\tFROM dbo.city_sales_volume inTB1 WHERE\n" +
            "\t\tinTB1.year_month = outTB.year_month-100 AND inTB1.province =outTB.province AND \n" +
            "\t\tinTB1.x_ways_id=outTB.x_ways_id   AND outTB.year_month>201700))*1.0/(SELECT SUM(inTB1.sales_volume)\n" +
            "\t\tFROM dbo.city_sales_volume inTB1 WHERE\n" +
            "\t\tinTB1.year_month = outTB.year_month-100 AND inTB1.province =outTB.province AND \n" +
            "\t\tinTB1.x_ways_id=outTB.x_ways_id   AND outTB.year_month>201700)\n" +
            "\t\n" +
            "\t) as growth_rate,\n" +
            "\t(/*(city_allcar_saleVol-lastZIJI)*1.0/lastZIJI  */\n" +
            "\t\t((SELECT SUM(inTB2.sales_volume) from dbo.city_sales_volume inTB2 WHERE\n" +
            "\t\tinTB2.year_month=outTB.year_month \n" +
            "\t\tand inTB2.province = outTB.province)-(SELECT SUM(inTB5.sales_volume)\n" +
            "\t\tFROM dbo.city_sales_volume inTB5 WHERE\n" +
            "\t\tinTB5.year_month = outTB.year_month-100 AND inTB5.province =outTB.province AND \n" +
            "\t\toutTB.year_month>201700\n" +
            "\t))*1.0/(SELECT SUM(inTB5.sales_volume)\n" +
            "\t\tFROM dbo.city_sales_volume inTB5 WHERE\n" +
            "\t\tinTB5.year_month = outTB.year_month-100 AND inTB5.province =outTB.province AND \n" +
            "\t\t outTB.year_month>201700)\n" +
            "\t) as region_allDimVal_saleVol_growth_rate,\n" +
            "\t(/*city_allcar_saleVol/wholeCountry_allcar_saleVol  */\n" +
            "\t  (SELECT SUM(inTB2.sales_volume) from dbo.city_sales_volume inTB2 WHERE\n" +
            "\t\tinTB2.year_month=outTB.year_month \n" +
            "\t\tand inTB2.province = outTB.province)*1.0/(SELECT SUM(inTB3.sales_volume) from dbo.city_sales_volume inTB3 WHERE\n" +
            "\t\tinTB3.year_month=outTB.year_month)\n" +
            "\t\t\n" +
            "\t) as region_allDimVal_plateVol_proportion,\n" +
            "\t(\n" +
            "\t\t SELECT count(1) from (SELECT province FROM dbo.city_sales_volume inTBLast where inTBLast.year_month=outTB.year_month GROUP BY province ) as A\n" +
            "\t) as areaCount\n" +
            "FROM\n" +
            "\tdbo.city_sales_volume outTB\n" +
            "GROUP BY\n" +
            "\tyear_month,\n" +
            "\tprovince,\n" +
            "\tx_ways_id,\n" +
            "\tcarserial order by outTB.year_month DESC\n" +
            "\t\n" +
            "\t";

    public static String proBrandQuery = "SELECT\n" +
            "\toutTB.year_month DimDate,\n" +
            "\toutTB.province as DimArea,\n" +
            "\toutTB.carbrand as DimCar,\n" +
            "\tSUM(outTB.sales_volume) as saleVol,\n" +
            "\t(\n" +
            "\t\tSELECT SUM(inTB1.sales_volume)\n" +
            "\t\tFROM dbo.city_sales_volume inTB1 WHERE\n" +
            "\t\tinTB1.year_month = outTB.year_month-100 AND inTB1.province =outTB.province AND \n" +
            "\t\tinTB1.carbrand=outTB.carbrand   AND outTB.year_month>201700\n" +
            "\t) as last_period_saleVol,\n" +
            "\t(\n" +
            "\t\tSELECT SUM(inTB2.sales_volume) from dbo.city_sales_volume inTB2 WHERE\n" +
            "\t\tinTB2.year_month=outTB.year_month \n" +
            "\t\tand inTB2.province = outTB.province \n" +
            "\t) as region_allDimVal_saleVol,\n" +
            "\t(\n" +
            "\t\tSELECT SUM(inTB3.sales_volume) from dbo.city_sales_volume inTB3 WHERE\n" +
            "\t\tinTB3.year_month=outTB.year_month\n" +
            "\t) as wholeCountry_allDimVal_saleVol,\n" +
            "\t(\n" +
            "\t\tSELECT SUM(inTB4.sales_volume) FROM dbo.city_sales_volume inTB4 WHERE \n" +
            "\t\tinTB4.year_month=outTB.year_month AND inTB4.carbrand = outTB.carbrand\n" +
            "\t) as wholeCountry_dimVal_saleVol,\n" +
            "\t(\t\t/*city_thiscar_saleVol*1.0/wholeCountry_thiscar_saleVol*/\n" +
            "\t\tSUM(outTB.sales_volume)*1.0/(SELECT SUM(inTB4.sales_volume) FROM dbo.city_sales_volume inTB4 WHERE \n" +
            "\t\tinTB4.year_month=outTB.year_month AND inTB4.carbrand = outTB.carbrand)\n" +
            "\t) as plateVol_proportion,\n" +
            "\t(/*city_thiscar_saleVol*1.0/city_allcar_saleVol*/\n" +
            "\t\tSUM(outTB.sales_volume)*1.0/(SELECT SUM(inTB2.sales_volume) from dbo.city_sales_volume inTB2 WHERE\n" +
            "\t\tinTB2.year_month=outTB.year_month \n" +
            "\t\tand inTB2.province = outTB.province)\n" +
            "\t) as preference_proportion,\n" +
            "\t( /*city_thiscar_saleVol-last_period_city_thiscar_saleVol/last_period_city_thiscar_saleVol*/\n" +
            "\t\t(SUM(outTB.sales_volume)-(SELECT SUM(inTB1.sales_volume)\n" +
            "\t\tFROM dbo.city_sales_volume inTB1 WHERE\n" +
            "\t\tinTB1.year_month = outTB.year_month-100 AND inTB1.province =outTB.province AND \n" +
            "\t\tinTB1.carbrand=outTB.carbrand   AND outTB.year_month>201700))*1.0/(SELECT SUM(inTB1.sales_volume)\n" +
            "\t\tFROM dbo.city_sales_volume inTB1 WHERE\n" +
            "\t\tinTB1.year_month = outTB.year_month-100 AND inTB1.province =outTB.province AND \n" +
            "\t\tinTB1.carbrand=outTB.carbrand   AND outTB.year_month>201700)\n" +
            "\t\n" +
            "\t) as growth_rate,\n" +
            "\t(/*(city_allcar_saleVol-lastZIJI)*1.0/lastZIJI  */\n" +
            "\t\t((SELECT SUM(inTB2.sales_volume) from dbo.city_sales_volume inTB2 WHERE\n" +
            "\t\tinTB2.year_month=outTB.year_month \n" +
            "\t\tand inTB2.province = outTB.province)-(SELECT SUM(inTB5.sales_volume)\n" +
            "\t\tFROM dbo.city_sales_volume inTB5 WHERE\n" +
            "\t\tinTB5.year_month = outTB.year_month-100 AND inTB5.province =outTB.province AND \n" +
            "\t\toutTB.year_month>201700\n" +
            "\t))*1.0/(SELECT SUM(inTB5.sales_volume)\n" +
            "\t\tFROM dbo.city_sales_volume inTB5 WHERE\n" +
            "\t\tinTB5.year_month = outTB.year_month-100 AND inTB5.province =outTB.province AND \n" +
            "\t\t outTB.year_month>201700)\n" +
            "\t) as region_allDimVal_saleVol_growth_rate,\n" +
            "\t(/*city_allcar_saleVol/wholeCountry_allcar_saleVol  */\n" +
            "\t  (SELECT SUM(inTB2.sales_volume) from dbo.city_sales_volume inTB2 WHERE\n" +
            "\t\tinTB2.year_month=outTB.year_month \n" +
            "\t\tand inTB2.province = outTB.province)*1.0/(SELECT SUM(inTB3.sales_volume) from dbo.city_sales_volume inTB3 WHERE\n" +
            "\t\tinTB3.year_month=outTB.year_month)\n" +
            "\t\t\n" +
            "\t) as region_allDimVal_plateVol_proportion,\n" +
            "\t(\n" +
            "\t\t SELECT count(1) from (SELECT province FROM dbo.city_sales_volume inTBLast WHERE inTBLast.year_month=outTB.year_month  GROUP BY province ) as A\n" +
            "\t) as areaCount\n" +
            "FROM\n" +
            "\tdbo.city_sales_volume outTB\n" +
            "GROUP BY\n" +
            "\tyear_month,\n" +
            "\tprovince,\n" +
            "\tcarbrand\n" +
            "\t order by outTB.year_month DESC\n";

    public static String cityBrandQuery = "SELECT\n" +
            "\toutTB.year_month as DimDate,\n" +
            "\toutTB.province,\n" +
            "\toutTB.city as DimArea,\n" +
            "\toutTB.carbrand as DimCar,\n" +
            "\tSUM(outTB.sales_volume) as saleVol,\n" +
            "\t(\n" +
            "\t\tSELECT SUM(inTB1.sales_volume)\n" +
            "\t\tFROM dbo.city_sales_volume inTB1 WHERE\n" +
            "\t\tinTB1.year_month = outTB.year_month-100 AND inTB1.province =outTB.province AND \n" +
            "\t\tinTB1.carbrand=outTB.carbrand AND inTB1.city = outTB.city  AND outTB.year_month>201700\n" +
            "\t) as last_period_saleVol,\n" +
            "\t(\n" +
            "\t\tSELECT SUM(inTB2.sales_volume) from dbo.city_sales_volume inTB2 WHERE\n" +
            "\t\tinTB2.year_month=outTB.year_month \n" +
            "\t\tand inTB2.province = outTB.province and \n" +
            "\t\tinTB2.city=outTB.city\n" +
            "\t) as region_allDimVal_saleVol,\n" +
            "\t(\n" +
            "\t\tSELECT SUM(inTB3.sales_volume) from dbo.city_sales_volume inTB3 WHERE\n" +
            "\t\tinTB3.year_month=outTB.year_month\n" +
            "\t) as wholeCountry_allDimVal_saleVol,\n" +
            "\t(\n" +
            "\t\tSELECT SUM(inTB4.sales_volume) FROM dbo.city_sales_volume inTB4 WHERE \n" +
            "\t\tinTB4.year_month=outTB.year_month AND inTB4.carbrand = outTB.carbrand\n" +
            "\t) as wholeCountry_dimVal_saleVol,\n" +
            "\t(\t\t/*city_thiscar_saleVol*1.0/wholeCountry_thiscar_saleVol*/\n" +
            "\t\tSUM(outTB.sales_volume)*1.0/(SELECT SUM(inTB4.sales_volume) FROM dbo.city_sales_volume inTB4 WHERE \n" +
            "\t\tinTB4.year_month=outTB.year_month AND inTB4.carbrand = outTB.carbrand)\n" +
            "\t) as plateVol_proportion,\n" +
            "\t(/*city_thiscar_saleVol*1.0/city_allcar_saleVol*/\n" +
            "\t\tSUM(outTB.sales_volume)*1.0/(SELECT SUM(inTB2.sales_volume) from dbo.city_sales_volume inTB2 WHERE\n" +
            "\t\tinTB2.year_month=outTB.year_month \n" +
            "\t\tand inTB2.province = outTB.province and \n" +
            "\t\tinTB2.city=outTB.city)\n" +
            "\t) as preference_proportion,\n" +
            "\t( /*city_thiscar_saleVol-last_period_city_thiscar_saleVol/last_period_city_thiscar_saleVol*/\n" +
            "\t\t(SUM(outTB.sales_volume)-(SELECT SUM(inTB1.sales_volume)\n" +
            "\t\tFROM dbo.city_sales_volume inTB1 WHERE\n" +
            "\t\tinTB1.year_month = outTB.year_month-100 AND inTB1.province =outTB.province AND \n" +
            "\t\tinTB1.carbrand=outTB.carbrand AND inTB1.city = outTB.city  AND outTB.year_month>201700))*1.0/(SELECT SUM(inTB1.sales_volume)\n" +
            "\t\tFROM dbo.city_sales_volume inTB1 WHERE\n" +
            "\t\tinTB1.year_month = outTB.year_month-100 AND inTB1.province =outTB.province AND \n" +
            "\t\tinTB1.carbrand=outTB.carbrand AND inTB1.city = outTB.city  AND outTB.year_month>201700)\n" +
            "\t\n" +
            "\t) as growth_rate,\n" +
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
            "\t) as region_allDimVal_saleVol_growth_rate,\n" +
            "\t(/*city_allcar_saleVol/wholeCountry_allcar_saleVol  */\n" +
            "\t  (SELECT SUM(inTB2.sales_volume) from dbo.city_sales_volume inTB2 WHERE\n" +
            "\t\tinTB2.year_month=outTB.year_month \n" +
            "\t\tand inTB2.province = outTB.province and \n" +
            "\t\tinTB2.city=outTB.city)*1.0/(SELECT SUM(inTB3.sales_volume) from dbo.city_sales_volume inTB3 WHERE\n" +
            "\t\tinTB3.year_month=outTB.year_month)\n" +
            "\t\t\n" +
            "\t) as region_allDimVal_plateVol_proportion,\n" +
            "\t(\n" +
            "\t\t SELECT count(1) from (SELECT province,city FROM dbo.city_sales_volume inTBLast where inTBLast.year_month=outTB.year_month AND inTBLast.province=outTB.province   GROUP BY province,city ) as A\n" +
            "\t\n" +
            "\t) as areaCount\n" +
            "FROM\n" +
            "\tdbo.city_sales_volume outTB\n" +
            "GROUP BY\n" +
            "\tyear_month,\n" +
            "\tprovince,\n" +
            "\tcity,\n" +
            "\tcarbrand\n" +
            "\torder by outTB.year_month DESC";

    public static String proManufactQuery = "SELECT\n" +
            "\toutTB.year_month DimDate,\n" +
            "\toutTB.province as DimArea,\n" +
            "\toutTB.manufacturer as DimCar,\n" +
            "\t\n" +
            "\tSUM(outTB.sales_volume) as saleVol,\n" +
            "\t(\n" +
            "\t\tSELECT SUM(inTB1.sales_volume)\n" +
            "\t\tFROM dbo.city_sales_volume inTB1 WHERE\n" +
            "\t\tinTB1.year_month = outTB.year_month-100 AND inTB1.province =outTB.province AND \n" +
            "\t\tinTB1.manufacturer=outTB.manufacturer   AND outTB.year_month>201700\n" +
            "\t) as last_period_saleVol,\n" +
            "\t(\n" +
            "\t\tSELECT SUM(inTB2.sales_volume) from dbo.city_sales_volume inTB2 WHERE\n" +
            "\t\tinTB2.year_month=outTB.year_month \n" +
            "\t\tand inTB2.province = outTB.province \n" +
            "\t) as region_allDimVal_saleVol,\n" +
            "\t(\n" +
            "\t\tSELECT SUM(inTB3.sales_volume) from dbo.city_sales_volume inTB3 WHERE\n" +
            "\t\tinTB3.year_month=outTB.year_month\n" +
            "\t) as wholeCountry_allDimVal_saleVol,\n" +
            "\t(\n" +
            "\t\tSELECT SUM(inTB4.sales_volume) FROM dbo.city_sales_volume inTB4 WHERE \n" +
            "\t\tinTB4.year_month=outTB.year_month AND inTB4.manufacturer = outTB.manufacturer\n" +
            "\t) as wholeCountry_dimVal_saleVol,\n" +
            "\t(\t\t/*city_thiscar_saleVol*1.0/wholeCountry_thiscar_saleVol*/\n" +
            "\t\tSUM(outTB.sales_volume)*1.0/(SELECT SUM(inTB4.sales_volume) FROM dbo.city_sales_volume inTB4 WHERE \n" +
            "\t\tinTB4.year_month=outTB.year_month AND inTB4.manufacturer = outTB.manufacturer)\n" +
            "\t) as plateVol_proportion,\n" +
            "\t(/*city_thiscar_saleVol*1.0/city_allcar_saleVol*/\n" +
            "\t\tSUM(outTB.sales_volume)*1.0/(SELECT SUM(inTB2.sales_volume) from dbo.city_sales_volume inTB2 WHERE\n" +
            "\t\tinTB2.year_month=outTB.year_month \n" +
            "\t\tand inTB2.province = outTB.province)\n" +
            "\t) as preference_proportion,\n" +
            "\t( /*city_thiscar_saleVol-last_period_city_thiscar_saleVol/last_period_city_thiscar_saleVol*/\n" +
            "\t\t(SUM(outTB.sales_volume)-(SELECT SUM(inTB1.sales_volume)\n" +
            "\t\tFROM dbo.city_sales_volume inTB1 WHERE\n" +
            "\t\tinTB1.year_month = outTB.year_month-100 AND inTB1.province =outTB.province AND \n" +
            "\t\tinTB1.manufacturer=outTB.manufacturer   AND outTB.year_month>201700))*1.0/(SELECT SUM(inTB1.sales_volume)\n" +
            "\t\tFROM dbo.city_sales_volume inTB1 WHERE\n" +
            "\t\tinTB1.year_month = outTB.year_month-100 AND inTB1.province =outTB.province AND \n" +
            "\t\tinTB1.manufacturer=outTB.manufacturer   AND outTB.year_month>201700)\n" +
            "\t\n" +
            "\t) as growth_rate,\n" +
            "\t(/*(city_allcar_saleVol-lastZIJI)*1.0/lastZIJI  */\n" +
            "\t\t((SELECT SUM(inTB2.sales_volume) from dbo.city_sales_volume inTB2 WHERE\n" +
            "\t\tinTB2.year_month=outTB.year_month \n" +
            "\t\tand inTB2.province = outTB.province)-(SELECT SUM(inTB5.sales_volume)\n" +
            "\t\tFROM dbo.city_sales_volume inTB5 WHERE\n" +
            "\t\tinTB5.year_month = outTB.year_month-100 AND inTB5.province =outTB.province AND \n" +
            "\t\toutTB.year_month>201700\n" +
            "\t))*1.0/(SELECT SUM(inTB5.sales_volume)\n" +
            "\t\tFROM dbo.city_sales_volume inTB5 WHERE\n" +
            "\t\tinTB5.year_month = outTB.year_month-100 AND inTB5.province =outTB.province AND \n" +
            "\t\t outTB.year_month>201700)\n" +
            "\t) as region_allDimVal_saleVol_growth_rate,\n" +
            "\t(/*city_allcar_saleVol/wholeCountry_allcar_saleVol  */\n" +
            "\t  (SELECT SUM(inTB2.sales_volume) from dbo.city_sales_volume inTB2 WHERE\n" +
            "\t\tinTB2.year_month=outTB.year_month \n" +
            "\t\tand inTB2.province = outTB.province)*1.0/(SELECT SUM(inTB3.sales_volume) from dbo.city_sales_volume inTB3 WHERE\n" +
            "\t\tinTB3.year_month=outTB.year_month)\n" +
            "\t\t\n" +
            "\t) as region_allDimVal_plateVol_proportion,\n" +
            "\t(\n" +
            "\t\t SELECT count(1) from (SELECT province FROM dbo.city_sales_volume inTBLast where inTBLast.year_month=outTB.year_month GROUP BY province ) as A\n" +
            "\t) as areaCount\n" +
            "FROM\n" +
            "\tdbo.city_sales_volume outTB\n" +
            "GROUP BY\n" +
            "\tyear_month,\n" +
            "\tprovince,\n" +
            "\tmanufacturer\n" +
            "\t order by outTB.year_month DESC";

    public static String cityManufactQuery = "SELECT\n" +
            "\toutTB.year_month as DimDate,\n" +
            "\toutTB.province,\n" +
            "\toutTB.city as DimArea,\n" +
            "\toutTB.manufacturer as DimCar,\n" +
            "\tSUM(outTB.sales_volume) as saleVol,\n" +
            "\t(\n" +
            "\t\tSELECT SUM(inTB1.sales_volume)\n" +
            "\t\tFROM dbo.city_sales_volume inTB1 WHERE\n" +
            "\t\tinTB1.year_month = outTB.year_month-100 AND inTB1.province =outTB.province AND \n" +
            "\t\tinTB1.manufacturer=outTB.manufacturer AND inTB1.city = outTB.city  AND outTB.year_month>201700\n" +
            "\t) as last_period_saleVol,\n" +
            "\t(\n" +
            "\t\tSELECT SUM(inTB2.sales_volume) from dbo.city_sales_volume inTB2 WHERE\n" +
            "\t\tinTB2.year_month=outTB.year_month \n" +
            "\t\tand inTB2.province = outTB.province and \n" +
            "\t\tinTB2.city=outTB.city\n" +
            "\t) as region_allDimVal_saleVol,\n" +
            "\t(\n" +
            "\t\tSELECT SUM(inTB3.sales_volume) from dbo.city_sales_volume inTB3 WHERE\n" +
            "\t\tinTB3.year_month=outTB.year_month\n" +
            "\t) as wholeCountry_allDimVal_saleVol,\n" +
            "\t(\n" +
            "\t\tSELECT SUM(inTB4.sales_volume) FROM dbo.city_sales_volume inTB4 WHERE \n" +
            "\t\tinTB4.year_month=outTB.year_month AND inTB4.manufacturer = outTB.manufacturer\n" +
            "\t) as wholeCountry_dimVal_saleVol,\n" +
            "\t(\t\t/*city_thiscar_saleVol*1.0/wholeCountry_thiscar_saleVol*/\n" +
            "\t\tSUM(outTB.sales_volume)*1.0/(SELECT SUM(inTB4.sales_volume) FROM dbo.city_sales_volume inTB4 WHERE \n" +
            "\t\tinTB4.year_month=outTB.year_month AND inTB4.manufacturer = outTB.manufacturer)\n" +
            "\t) as plateVol_proportion,\n" +
            "\t(/*city_thiscar_saleVol*1.0/city_allcar_saleVol*/\n" +
            "\t\tSUM(outTB.sales_volume)*1.0/(SELECT SUM(inTB2.sales_volume) from dbo.city_sales_volume inTB2 WHERE\n" +
            "\t\tinTB2.year_month=outTB.year_month \n" +
            "\t\tand inTB2.province = outTB.province and \n" +
            "\t\tinTB2.city=outTB.city)\n" +
            "\t) as preference_proportion,\n" +
            "\t( /*city_thiscar_saleVol-last_period_city_thiscar_saleVol/last_period_city_thiscar_saleVol*/\n" +
            "\t\t(SUM(outTB.sales_volume)-(SELECT SUM(inTB1.sales_volume)\n" +
            "\t\tFROM dbo.city_sales_volume inTB1 WHERE\n" +
            "\t\tinTB1.year_month = outTB.year_month-100 AND inTB1.province =outTB.province AND \n" +
            "\t\tinTB1.manufacturer=outTB.manufacturer AND inTB1.city = outTB.city  AND outTB.year_month>201700))*1.0/(SELECT SUM(inTB1.sales_volume)\n" +
            "\t\tFROM dbo.city_sales_volume inTB1 WHERE\n" +
            "\t\tinTB1.year_month = outTB.year_month-100 AND inTB1.province =outTB.province AND \n" +
            "\t\tinTB1.manufacturer=outTB.manufacturer AND inTB1.city = outTB.city  AND outTB.year_month>201700)\n" +
            "\t\n" +
            "\t) as growth_rate,\n" +
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
            "\t) as region_allDimVal_saleVol_growth_rate,\n" +
            "\t(/*city_allcar_saleVol/wholeCountry_allcar_saleVol  */\n" +
            "\t  (SELECT SUM(inTB2.sales_volume) from dbo.city_sales_volume inTB2 WHERE\n" +
            "\t\tinTB2.year_month=outTB.year_month \n" +
            "\t\tand inTB2.province = outTB.province and \n" +
            "\t\tinTB2.city=outTB.city)*1.0/(SELECT SUM(inTB3.sales_volume) from dbo.city_sales_volume inTB3 WHERE\n" +
            "\t\tinTB3.year_month=outTB.year_month)\n" +
            "\t\t\n" +
            "\t) as region_allDimVal_plateVol_proportion,\n" +
            "\t(\n" +
            "\t\t SELECT count(1) from (SELECT province,city FROM dbo.city_sales_volume inTBLast where inTBLast.year_month=outTB.year_month AND inTBLast.province=outTB.province  GROUP BY province,city ) as A\n" +
            "\t\n" +
            "\t) as areaCount\n" +
            "FROM\n" +
            "\tdbo.city_sales_volume outTB\n" +
            "GROUP BY\n" +
            "\tyear_month,\n" +
            "\tprovince,\n" +
            "\tcity,\n" +
            "\tmanufacturer\n" +
            "\torder by outTB.year_month DESC\n" +
            "\t\n" +
            "\t\n";
}
