package com.xyauto.SingleCal;

import com.xyauto.newdomain.MediaParamDomain;
import com.xyauto.utils.InsertSqlUtils;
import com.xyauto.utils.QuerySqlUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.SQLException;
import java.util.List;

import static com.xyauto.newmain.CalculateMethod.*;

/**
 * Created by zhangmg on 2017/5/11.
 */
public class CityManufacture {
    public void computeCityManufacture(QueryRunner qr) throws SQLException {
        long t1 = System.currentTimeMillis();
        List<MediaParamDomain> r1 = qr.query(QuerySqlUtils.cityManufactQuery, new BeanListHandler<MediaParamDomain>(MediaParamDomain.class));
        long t2 = System.currentTimeMillis();
        System.out.println("城市厂商查询耗时：" + ((t2 - t1) / (1000)) + "秒");
        calculateOneNum(true,r1);
        calculateFiveIndex(r1);
        calculateLevel(true,r1);
        long t3 = System.currentTimeMillis();
        System.out.println("城市厂商计算耗时：" + ((t3 - t2) / (1000)) + "秒");
        insertParamMethod(r1, qr);
        long t4 = System.currentTimeMillis();
        System.out.println("城市厂商中间数据入库耗时：" + ((t4 - t3) / (1000)) + "秒");

        insertIndexMethod(r1, qr);
        long t5 = System.currentTimeMillis();
        System.out.println("城市厂商间数据入库耗时：" + ((t5 - t4) / (1000)) + "秒");
    }

    private void insertParamMethod(List<MediaParamDomain> r1, QueryRunner qr) throws SQLException {
        for (MediaParamDomain someData : r1) {
            qr.update(InsertSqlUtils.cityManufactInsertMediaSQL,
                    someData.getDimDate(),
                    someData.getDimCar(),
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

    private void insertIndexMethod(List<MediaParamDomain> r1, QueryRunner qr) throws SQLException {
        for (MediaParamDomain someData : r1) {
            qr.update(InsertSqlUtils.cityManufactInsertIndexSQL,
                    someData.getDimDate(),
                    someData.getDimCar(),
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
}
