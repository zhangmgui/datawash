package com.xyauto.SingleCal;

import com.xyauto.domain.MediaParamDomain;
import com.xyauto.utils.QuerySqlUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.SQLException;
import java.util.List;

import static com.xyauto.main.CalculateMethod.*;

/**
 * Created by zhangmg on 2017/5/10.
 */
public class CityCarserial {

    public void computeCityCarserial(QueryRunner qr) throws SQLException {
        long t1 = System.currentTimeMillis();
        List<MediaParamDomain> someDatas = qr.query(QuerySqlUtils.cityCarserialQuery, new BeanListHandler<MediaParamDomain>(MediaParamDomain.class));
        long t2 = System.currentTimeMillis();
        System.out.println("城市车型查询耗时：" + ((t2 - t1) / (1000)) + "秒");
        calculateOneNum(true,someDatas);
        calculateFiveIndex(someDatas);
        calculateLevel(true,someDatas);
        long t3 = System.currentTimeMillis();
        System.out.println("城市车型计算耗时：" + ((t3 - t2) / (1000)) + "秒");
        insertParamMethod(someDatas, qr);
        long t4 = System.currentTimeMillis();
        System.out.println("城市车型中间数据入库耗时：" + ((t4 - t3) / (1000)) + "秒");

        insertIndexMethod(someDatas, qr);
        long t5 = System.currentTimeMillis();
        System.out.println("城市车型中间数据入库耗时：" + ((t5 - t4) / (1000)) + "秒");
    }

    private void insertIndexMethod(List<MediaParamDomain> someDatas, QueryRunner qr) throws SQLException {
        String insertSQL = "INSERT\n" +
                "\tINTO\n" +
                "\t\tbi.dbo.city_month_carseiral_sales_analysis(\n" +
                "\t\t\tDimDate,\n" +
                "\t\t\tDimCar,\n" +
                "\t\t\tcarserial,\n" +
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
                "\tVALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)\n";
        for (MediaParamDomain someData : someDatas) {
            qr.update(insertSQL,
                    someData.getDimDate(),
                    someData.getDimCar(),
                    someData.getCarserial(),
                    someData.getDimArea(),
                    someData.getProvince(),
                    someData.getPlateVol_index(),
                    someData.getPreference_index(),
                    someData.getGrowth_index(),
                    someData.getRegion_allDimVal_plateVol_index(),
                    someData.getPlateVol_level(),
                    someData.getPreference_level(),
                    someData.getGrowth_level(),
                    someData.getRegion_allDimVal_plateVol_level()
            );
        }
    }

    private void insertParamMethod(List<MediaParamDomain> someDatas, QueryRunner qr) throws SQLException {
        String insertSQL = "INSERT\n" +
                "\tINTO\n" +
                "bi.dbo.city_month_carseiral_sales_analysis_detail(\n" +
                "\t\t\tDimDate,\n" +
                "\t\t\tDimCar,\n" +
                "\t\t\tcarserial,\n" +
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
                "\tVALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        for (MediaParamDomain someData : someDatas) {
            qr.update(insertSQL,
                    someData.getDimDate(),
                    someData.getDimCar(),
                    someData.getCarserial(),
                    someData.getDimArea(),
                    someData.getProvince(),
                    someData.getSaleVol(),
                    someData.getLast_period_saleVol(),
                    someData.getRegion_allDimVal_saleVol(),
                    someData.getWholeCountry_allDimVal_saleVol(),
                    someData.getWholeCountry_dimVal_saleVol(),
                    someData.getPlateVol_proportion(),
                    someData.getWholecountry_dimVal_plateVol_proportion_avg(),
                    someData.getPreference_proportion(),
                    someData.getRegion_main_dimValCount(),
                    someData.getGrowth_rate(),
                    someData.getRegion_allDimVal_saleVol_growth_rate(),
                    someData.getRegion_allDimVal_plateVol_proportion(),
                    someData.getAreaCount()
            );

        }
    }
}
