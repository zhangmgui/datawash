package com.xyauto.main;

import com.xyauto.domain.City;
import com.xyauto.utils.CalculateUtils;
import com.xyauto.utils.DBConnection;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 市级宏观经济数据xls入库
 * Created by zhangmg on 2017/5/8.
 */
public class ImportCityXlsApp {
    private static QueryRunner qr = new QueryRunner(DBConnection.getMasterDataSource());

    public static void main(String[] args) throws IOException, InvalidFormatException, SQLException {
        String filepath = "C:\\Users\\zhangmg\\Desktop\\湖南-长沙市.xls";
        File fileDir = new File(filepath);
        ArrayList<City> cities = dealwithXlsFile(fileDir);
        insertIntoDB(cities);
       /* if (fileDir.isDirectory()) {
            System.out.println("文件夹");
            String[] filelist = fileDir.list();
            for (int i = 0; i < filelist.length; i++) {
                File readfile = new File(filepath + "\\" + filelist[i]);
                if (!readfile.isDirectory()) {
                    try {
                        ArrayList<City> cities = dealwithXlsFile(readfile);
                        insertIntoDB(cities);
                    }catch (Exception e){
                        System.out.println(readfile.getName());
                        e.printStackTrace();
                    }
                }
            }
        }*/
    }



    protected static ArrayList<City> dealwithXlsFile(File file) throws IOException, InvalidFormatException {
        ArrayList<City> cities = new ArrayList<City>();
        String fileName = file.getName();
        String[] split = fileName.substring(0, fileName.length() - 4).split("-");
        Map<String, Integer> noMap = null;

            String provinceNameThisFile = null;
            String CityNameThisFile = null;
            if(split.length==2){
                provinceNameThisFile = split[0];
                CityNameThisFile = split[1];
            }
            if(split.length==1){
                provinceNameThisFile = split[0];
                CityNameThisFile = split[0];
            }
            FileInputStream fis = new FileInputStream(file);

            Workbook wb = WorkbookFactory.create(fis);
            Sheet sheet0 = wb.getSheetAt(1);  //第一页
            int lastRowNum = sheet0.getLastRowNum();  //sheet页行数
            Integer cellNum = Integer.valueOf(sheet0.getRow(0).getLastCellNum() + ""); //宽度
            Row row0 = sheet0.getRow(0);
            noMap = getThisFileSixPColNo(row0, cellNum);

            for (int i = 4; i <=lastRowNum; i++) {
                City city = new City();
                Row row = sheet0.getRow(i);
                city.setProvince(provinceNameThisFile);
                city.setCity(CityNameThisFile);
                String year = getCellValue(row.getCell(0)); //年份
                if(year.equals("")||year==null){
                    continue;
                }

                Integer gdp_city_per1 = noMap.get("GDP_city_per");
                String gdp_city_per = null;
                if(null!=gdp_city_per1){
                    gdp_city_per = getCellValue(row.getCell(noMap.get("GDP_city_per")));
                }

                Integer trscg_city_per1 = noMap.get("TRSCG_city_per");
                String trscg_city_per = null;
                if(null!=trscg_city_per1){
                    trscg_city_per = getCellValue(row.getCell(noMap.get("TRSCG_city_per")));
                }


                Integer public_buses_city_no1 = noMap.get("Public_Buses_city_No");
                String public_buses_city_no =null;
                if(null!=public_buses_city_no1){
                    public_buses_city_no = getCellValue(row.getCell(noMap.get("Public_Buses_city_No")));
                }

                Integer taxi_city_no1 = noMap.get("Taxi_city_NO");
                String taxi_city_no = null;
                if(taxi_city_no1!=null){
                    taxi_city_no = getCellValue(row.getCell(taxi_city_no1));
                }

                Integer urban_road_area_nation1 = noMap.get("Urban_Road_Area_nation");
                String urban_road_area_nation =null;
                if(urban_road_area_nation1!=null){
                    urban_road_area_nation = getCellValue(row.getCell(noMap.get("Urban_Road_Area_nation")));
                }

                Integer population_city1 = noMap.get("Population_city");
                String population_city = null;
                if(null!=population_city1){
                    population_city = getCellValue(row.getCell(noMap.get("Population_city")));
                }

                city.setYear(Integer.valueOf(year.trim()));

                if(gdp_city_per!=null&&!gdp_city_per.equals("")){
                    city.setGDP_city_per(Double.valueOf(gdp_city_per));
                }
                if(trscg_city_per!=null&&!trscg_city_per.equals("")){
                    city.setTRSCG_city_per(Integer.valueOf(CalculateUtils.formatDouble4(Double.valueOf(trscg_city_per))));
                }
                if(public_buses_city_no!=null&&!public_buses_city_no.equals("")){
                    city.setPublic_Buses_city_No(Integer.valueOf(CalculateUtils.formatDouble4(Double.valueOf(public_buses_city_no))));
                }

                if(taxi_city_no!=null&&!taxi_city_no.equals("")){
                    city.setTaxi_city_NO(Integer.valueOf(CalculateUtils.formatDouble4(Double.valueOf(taxi_city_no))));
                }
                if(urban_road_area_nation!=null&&!urban_road_area_nation.equals("")){
                    city.setUrban_Road_Area_nation(Integer.valueOf(CalculateUtils.formatDouble4(Double.valueOf(urban_road_area_nation))));
                }
                if(population_city!=null&&!population_city.equals("")){
                    city.setPopulation_city(Double.valueOf(population_city));
                }
                cities.add(city);
            }
        return cities;
    }


    public static Map<String,Integer> getThisFileSixPColNo(Row row, Integer xlsWidth){
        HashMap<String, Integer> noMap = new HashMap<>();
        for (Integer i = 0; i < xlsWidth; i++) {
            Cell cell = row.getCell(i);
            String cellHeadStr = getCellValue(cell);
            if(cellHeadStr.equals("人均地区生产总值_市辖区")){
                noMap.put("GDP_city_per",i);
            }
            if(cellHeadStr.equals("社会消费品零售总额_市辖区")){
                noMap.put("TRSCG_city_per",i);
            }
            if(cellHeadStr.equals("公共汽、电车运营数_市辖区")){
                noMap.put("Public_Buses_city_No",i);
            }
            if(cellHeadStr.equals("出租汽车运营数_市辖区")){
                noMap.put("Taxi_city_NO",i);
            }
            if(cellHeadStr.equals("城市道路面积")){
                noMap.put("Urban_Road_Area_nation",i);
            }
            if(cellHeadStr.equals("年末人口数_市辖区")){
                noMap.put("Population_city",i);
            }

        }
        return noMap;
    }
    
    public static String getCellValue(Cell cell) {

        if (cell == null)
            return "";

        if (cell.getCellType() == Cell.CELL_TYPE_STRING) {

            return cell.getStringCellValue();

        } else if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {

            return String.valueOf(cell.getBooleanCellValue());

        } else if (cell.getCellType() == Cell.CELL_TYPE_FORMULA) {

            return cell.getCellFormula();

        } else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {

            return String.valueOf(cell.getNumericCellValue());

        }
        return "";
    }
   protected static void insertIntoDB(List<City> cities) throws SQLException {
        String insertSQL = "INSERT\n" +
                "\tINTO\n" +
                "\t\tbi.dbo.macro_economy_city(\n" +
                "\t\t\tyear,\n" +
                "\t\t\tprovince,\n" +
                "\t\t\tcity,\n" +
                "\t\t\tGDP_city_per,\n" +
                "\t\t\tTRSCG_city_per,\n" +
                "\t\t\tPublic_Buses_city_No,\n" +
                "\t\t\tTaxi_city_NO,\n" +
                "\t\t\tUrban_Road_Area_nation,\n" +
                "\t\t\tPopulation_city\n" +
                "\t\t)\n" +
                "\tVALUES(?,?,?,?,?,?,?,?,?)";
        for (City city : cities) {
            qr.update(insertSQL,
                    city.getYear(),
                    city.getProvince(),
                    city.getCity(),
                    city.getGDP_city_per(),
                    city.getTRSCG_city_per(),
                    city.getPublic_Buses_city_No(),
                    city.getTaxi_city_NO(),
                    city.getUrban_Road_Area_nation(),
                    city.getPopulation_city()
                    );
        }
    }
}
