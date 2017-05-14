package com.xyauto.main;

import com.xyauto.domain.CountyDomain;
import com.xyauto.utils.DBConnection;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangmg on 2017/5/12.
 */
public class ImportCountyXlsApp {
    private static QueryRunner qr = new QueryRunner(DBConnection.getMasterDataSource());
    private static List<CountyDomain> allData = new ArrayList<>();
    private static int fileNum = 0;

    public static void main(String[] args) throws SQLException {
        String filepath = "C:\\Users\\zhangmg\\Desktop\\bi\\县级数据-清洗数据\\县级数据-清洗数据";
        File fileDir = new File(filepath);
        if (fileDir.isDirectory()) {
            long t1 = System.currentTimeMillis();
            System.out.println("文件夹");
            File[] fileList = fileDir.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                File fileDirIn = fileList[i];
                if (fileDirIn.isDirectory()) {
                    String[] fileNames = fileDirIn.list();
                    String filepath1 = fileDirIn.getName();
                    for (int i1 = 0; i1 < fileNames.length; i1++) {
                        File readfile = new File(filepath + "\\" + filepath1 + "\\" + fileNames[i1]);
                        dealWithFile(readfile, allData);
                        System.out.println("第" + (++fileNum) + "个文件");
                    }
                }


            }
            long t2 = System.currentTimeMillis();
            System.out.println("运算耗时：" + ((t2 - t1) / (1000)) + "秒");
            String insertSQL = "INSERT\n" +
                    "\tINTO\n" +
                    "\t\tbi.dbo.macro_economy_county(\n" +
                    "\t\t\tyear_month,\n" +
                    "\t\t\tcountyName,\n" +
                    "\t\t\tprovinceName,\n" +
                    "\t\t\tfinanceIncome_county,\n" +
                    "\t\t\ttaxIncome_county,\n" +
                    "\t\t\texpenditure_county,\n" +
                    "\t\t\tscience_expenditure_county,\n" +
                    "\t\t\tGDP_county,\n" +
                    "\t\t\thousehold_register_population,\n" +
                    "\t\t\tyearEnd_population,\n" +
                    "\t\t\tTRSCG_county_per,\n" +
                    "\t\t\tland_area_county\n" +
                    "\t\t)\n" +
                    "\tVALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
            for (CountyDomain countyDomain : allData) {
                qr.update(insertSQL,
                        countyDomain.getYearMonth(),
                        countyDomain.getCountyName(),
                        countyDomain.getProvinceName(),
                        countyDomain.getFinanceIncome_county(),
                        countyDomain.getTaxIncome_county(),
                        countyDomain.getExpenditure_county(),
                        countyDomain.getScience_expenditure_county(),
                        countyDomain.getGDP_county(),
                        countyDomain.getHousehold_register_population(),
                        countyDomain.getYearEnd_population(),
                        countyDomain.getTRSCG_county_per(),
                        countyDomain.getLand_area_county()
                );

            }
        }
    }

    private static void dealWithFile(File readfile, List<CountyDomain> allData) {
        String fileName = readfile.getName();
        String[] split = fileName.split("-");

        String provinceName = split[0].trim();
        String dim = split[1].trim();
        try {
            dealFileSEC(provinceName, dim, allData, readfile);

        } catch (Exception e) {
            System.out.println(readfile.getName());
            e.printStackTrace();
        }

    }

    private static void dealFileSEC(String provinceName, String dim, List<CountyDomain> allData, File readfile) throws IOException, InvalidFormatException {
        FileInputStream fis = new FileInputStream(readfile);
        Workbook wb = WorkbookFactory.create(fis);
        Sheet sheet0 = wb.getSheetAt(0);  //第一页
        int lastRowNum = sheet0.getLastRowNum();  //sheet页行数
        Integer cellNum = Integer.valueOf(sheet0.getRow(0).getLastCellNum() + ""); //宽度
        for (int i = 1; i <= cellNum; i++) {
            Cell countyCell = sheet0.getRow(1).getCell(i); //县名
            if(getCellValue(countyCell).equals("")){
               continue;
            }
            for (int j = 4; j <= lastRowNum; j++) {
                Cell timeCell = sheet0.getRow(j).getCell(0);
                Cell valCell = sheet0.getRow(j).getCell(i); //值 4行开始
                setObjectVal(timeCell, valCell, countyCell, provinceName, dim, allData);
            }
        }
    }

    private static void setObjectVal(Cell timeCell, Cell valCell, Cell countyCell, String provinceName, String dim, List<CountyDomain> allData) {
        Integer yearTime = null;
        if (!getCellValue(timeCell).equals("")) {
            yearTime = Integer.valueOf(getCellValue(timeCell));
        }
        String countyName = getCellValue(countyCell);


        if (dim.indexOf("地方公共财政收入") != -1) {
            int flag = 0;
            for (CountyDomain countyDomain : allData) {
                if (countyDomain.getYearMonth().equals(yearTime)  && countyDomain.getCountyName().equals(countyName)) {//已结有了该时间该县，就更新
                    if (!getCellValue(valCell).equals("")) {
                        countyDomain.setFinanceIncome_county(Float.valueOf(getCellValue(valCell)));
                    }
                    flag = 1;
                    break;

                }
            }
            if (flag == 0) { //如果没有就新建对象
                CountyDomain countyDomain = new CountyDomain();
                countyDomain.setYearMonth(yearTime);
                countyDomain.setProvinceName(provinceName);
                countyDomain.setCountyName(countyName);
                if (!getCellValue(valCell).equals("")) {
                    countyDomain.setFinanceIncome_county(Float.valueOf(getCellValue(valCell)));
                }

                allData.add(countyDomain);
            }
        }
        if (dim.indexOf("地方税收收入") != -1) {
            int flag = 0;
            for (CountyDomain countyDomain : allData) {
                if (countyDomain.getYearMonth() .equals(yearTime)  && countyDomain.getCountyName().equals(countyName)) {//已结有了该时间该县，就更新
                    if (!getCellValue(valCell).equals("")) {
                        countyDomain.setTaxIncome_county(Float.valueOf(getCellValue(valCell)));
                    }
                    flag = 1;
                    break;

                }
            }
            if (flag == 0) { //如果没有就新建对象
                CountyDomain countyDomain = new CountyDomain();
                countyDomain.setYearMonth(yearTime);
                countyDomain.setProvinceName(provinceName);
                countyDomain.setCountyName(countyName);
                if (!getCellValue(valCell).equals("")) {
                    countyDomain.setTaxIncome_county(Float.valueOf(getCellValue(valCell)));
                }
                allData.add(countyDomain);
            }

        }
        if (dim.indexOf("地方公共财政支出") != -1) {
            int flag = 0;
            for (CountyDomain countyDomain : allData) {
                if (countyDomain.getYearMonth().equals(yearTime)  && countyDomain.getCountyName().equals(countyName)) {//已结有了该时间该县，就更新
                    if (!getCellValue(valCell).equals("")) {
                        countyDomain.setExpenditure_county(Float.valueOf(getCellValue(valCell)));
                    }
                    flag = 1;
                    break;
                }
            }
            if (flag == 0) { //如果没有就新建对象
                CountyDomain countyDomain = new CountyDomain();
                countyDomain.setYearMonth(yearTime);
                countyDomain.setProvinceName(provinceName);
                countyDomain.setCountyName(countyName);
                if (!getCellValue(valCell).equals("")) {
                    countyDomain.setExpenditure_county(Float.valueOf(getCellValue(valCell)));
                }

                allData.add(countyDomain);
            }

        }
        if (dim.indexOf("地方公共财政支出_科学技术") != -1) {
            int flag = 0;
            for (CountyDomain countyDomain : allData) {
                if (countyDomain.getYearMonth().equals(yearTime) && countyDomain.getCountyName().equals(countyName)) {//已结有了该时间该县，就更新
                    if (!getCellValue(valCell).equals("")) {
                        countyDomain.setScience_expenditure_county(Float.valueOf(getCellValue(valCell)));
                    }

                    flag = 1;
                    break;
                }
            }
            if (flag == 0) { //如果没有就新建对象
                CountyDomain countyDomain = new CountyDomain();
                countyDomain.setYearMonth(yearTime);
                countyDomain.setProvinceName(provinceName);
                countyDomain.setCountyName(countyName);
                if (!getCellValue(valCell).equals("")) {
                    countyDomain.setScience_expenditure_county(Float.valueOf(getCellValue(valCell)));
                }

                allData.add(countyDomain);
            }

        }
        if (dim.indexOf("国内生产总值") != -1) {
            int flag = 0;
            for (CountyDomain countyDomain : allData) {
                if (countyDomain.getYearMonth().equals(yearTime) && countyDomain.getCountyName().equals(countyName)) {//已结有了该时间该县，就更新
                    if (!getCellValue(valCell).equals("")) {
                        countyDomain.setGDP_county(Float.valueOf(getCellValue(valCell)));
                    }

                    flag = 1;
                    break;
                }
            }
            if (flag == 0) { //如果没有就新建对象
                CountyDomain countyDomain = new CountyDomain();
                countyDomain.setYearMonth(yearTime);
                countyDomain.setProvinceName(provinceName);
                countyDomain.setCountyName(countyName);
                if (!getCellValue(valCell).equals("")) {
                    countyDomain.setGDP_county(Float.valueOf(getCellValue(valCell)));
                }

                allData.add(countyDomain);
            }

        }
        if (dim.indexOf("户籍人口数") != -1) {
            int flag = 0;
            for (CountyDomain countyDomain : allData) {
                Integer yearMonth = countyDomain.getYearMonth();
                boolean a = yearMonth == yearTime;
                boolean b = (countyDomain.getCountyName().indexOf(countyName)!=-1);
                if (countyDomain.getYearMonth().equals(yearTime) && (countyDomain.getCountyName().indexOf(countyName)!=-1)) {//已结有了该时间该县，就更新
                    if (!getCellValue(valCell).equals("")) {
                        countyDomain.setHousehold_register_population(Float.valueOf(getCellValue(valCell)));
                    }
                    flag = 1;
                    break;
                }
            }
            if (flag == 0) { //如果没有就新建对象
                CountyDomain countyDomain = new CountyDomain();
                countyDomain.setYearMonth(yearTime);
                countyDomain.setProvinceName(provinceName);
                countyDomain.setCountyName(countyName);
                if (!getCellValue(valCell).equals("")) {
                    countyDomain.setHousehold_register_population(Float.valueOf(getCellValue(valCell)));
                }
                allData.add(countyDomain);
            }

        }
        if (dim.indexOf("年末人口数") != -1) {
            int flag = 0;
            for (CountyDomain countyDomain : allData) {
                if (countyDomain.getYearMonth().equals(yearTime) && countyDomain.getCountyName().equals(countyName)) {//已结有了该时间该县，就更新
                    if (!getCellValue(valCell).equals("")) {
                        countyDomain.setYearEnd_population(Float.valueOf(getCellValue(valCell)));
                    }

                    flag = 1;
                    break;
                }
            }
            if (flag == 0) { //如果没有就新建对象
                CountyDomain countyDomain = new CountyDomain();
                countyDomain.setYearMonth(yearTime);
                countyDomain.setProvinceName(provinceName);
                countyDomain.setCountyName(countyName);
                if (!getCellValue(valCell).equals("")) {
                    countyDomain.setYearEnd_population(Float.valueOf(getCellValue(valCell)));
                }
                allData.add(countyDomain);
            }

        }
        if (dim.indexOf("社会消费品零售总额") != -1) {
            int flag = 0;
            for (CountyDomain countyDomain : allData) {
                if (countyDomain.getYearMonth().equals(yearTime) && countyDomain.getCountyName().equals(countyName)) {//已结有了该时间该县，就更新
                    if (!getCellValue(valCell).equals("")) {
                        countyDomain.setTRSCG_county_per(Float.valueOf(getCellValue(valCell)));
                    }
                    flag = 1;
                    break;
                }
            }
            if (flag == 0) { //如果没有就新建对象
                CountyDomain countyDomain = new CountyDomain();
                countyDomain.setYearMonth(yearTime);
                countyDomain.setProvinceName(provinceName);
                countyDomain.setCountyName(countyName);
                if (!getCellValue(valCell).equals("")) {

                    countyDomain.setTRSCG_county_per(Float.valueOf(getCellValue(valCell)));
                }
                allData.add(countyDomain);
            }

        }
        if (dim.indexOf("土地面积") != -1) {
            int flag = 0;
            for (CountyDomain countyDomain : allData) {
                if (countyDomain.getYearMonth().equals(yearTime) && countyDomain.getCountyName().equals(countyName)) {//已结有了该时间该县，就更新
                    if (!getCellValue(valCell).equals("")) {

                        countyDomain.setLand_area_county(Float.valueOf(getCellValue(valCell)));
                    }
                    flag = 1;
                    break;
                }
            }
            if (flag == 0) { //如果没有就新建对象
                CountyDomain countyDomain = new CountyDomain();
                countyDomain.setYearMonth(yearTime);
                countyDomain.setProvinceName(provinceName);
                countyDomain.setCountyName(countyName);
                if (!getCellValue(valCell).equals("")) {

                    countyDomain.setLand_area_county(Float.valueOf(getCellValue(valCell)));
                }
                allData.add(countyDomain);
            }

        }
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
}
