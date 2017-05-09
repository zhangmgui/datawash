package com.xyauto.main;

import com.xyauto.domain.GroupLevelDomain;
import com.xyauto.utils.DBConnection;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.util.List;

/**
 * 根据省份车型偏好指数进行分组
 * Created by zhangmg on 2017/5/8.
 */
public class DealProvincePreferGroup {
    private static QueryRunner qr = new QueryRunner(DBConnection.getMasterDataSource());
    public static void main(String[] args) throws Exception {

        String queryAVG= "\t\tselect\n" +
                "\t\t\t(\n" +
                "\t\t\t\tselect\n" +
                "\t\t\t\t\tavg( province_thiscar_preference_index )\n" +
                "\t\t\t\tfrom\n" +
                "\t\t\t\t\tdbo.province_month_sales_analysis\n" +
                "\t\t\t\twhere\n" +
                "\t\t\t\t\tprovince_thiscar_preference_index >= 100\n" +
                "\t\t\t) as overHAVG,(select\n" +
                "\t\t\t\t\tavg( province_thiscar_preference_index )\n" +
                "\t\t\t\tfrom\n" +
                "\t\t\t\t\tdbo.province_month_sales_analysis\n" +
                "\t\t\t\twhere\n" +
                "\t\t\t\t\tprovince_thiscar_preference_index < 100) as lowerHAVG ";
        GroupLevelDomain avgs = qr.query(queryAVG, new BeanHandler<GroupLevelDomain>(GroupLevelDomain.class));
        Double lowerHAVG = avgs.getLowerHAVG();
        Double overHAVG = avgs.getOverHAVG();



        String allAnalysisSQL = "\t\tselect\n" +
                "\t\t\t\tprovince,\n" +
                "\t\t\t\tyear_month,\n" +
                "\t\t\t\tx_ways_id,\n" +
                "\t\t\t\tcarserial,\n" +
                "\t\t\t\tprovince_thiscar_preference_index\n" +
                "\t\t\tfrom\n" +
                "\t\t\t\tdbo.province_month_sales_analysis";
        List<GroupLevelDomain> cars = qr.query(allAnalysisSQL, new BeanListHandler<GroupLevelDomain>(GroupLevelDomain.class));
        String insertSQL = "INSERT\n" +
                "\t\t\t\t\tINTO\n" +
                "\t\t\t\t\t\tbi.dbo.carserial_prefer_level(\n" +
                "\t\t\t\t\t\t\tyear_month,\n" +
                "\t\t\t\t\t\t\tcarserial,\n" +
                "\t\t\t\t\t\t\tx_ways_id,\n" +
                "\t\t\t\t\t\t\tprovince,\n" +
                "\t\t\t\t\t\t\tprovince_thiscar_preference_index,\n" +
                "\t\t\t\t\t\t\tpreferenceLevel\n" +
                "\t\t\t\t\t\t)\n" +
                "\t\t\t\t\tVALUES(?,?,?,?,?,?);";
        for (GroupLevelDomain car : cars) {
            Integer indexTemp = car.getProvince_thiscar_preference_index();
            if(indexTemp>=100&&indexTemp>=overHAVG){
                car.setPreferenceLevel(1);
            }
            if(indexTemp>=100&&indexTemp<overHAVG){
                car.setPreferenceLevel(2);
            }
            if(indexTemp<100&&indexTemp>=lowerHAVG){
                car.setPreferenceLevel(3);
            }
            if(indexTemp<100&&indexTemp<lowerHAVG){
                car.setPreferenceLevel(4);
            }
            qr.update(insertSQL,car.getYear_month(),
                    car.getCarserial(),
                    car.getX_ways_id(),
                    car.getProvince(),
                    car.getProvince_thiscar_preference_index(),
                    car.getPreferenceLevel()
            );
        }



    }
}
