package my.unlimit.jasper;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.ooxml.JRPptxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.export.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.*;
import java.nio.file.Paths;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

/**
 * Created by vladimir on 16.03.2016.
 */
enum FileFormat{
    XLSX,PPTX;
}

public class SimpleReport {
    static Logger log = LoggerFactory.getLogger(SimpleReport.class);

    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/test";

    //  Database credentials
    static final String USER = "user1";
    static final String PASS = "user1";


    private static final String TEMP_DIR = System.getProperty("java.io.tmpdir") + File.separator + "global-reports";
    private static Integer year=2016;
    private static Integer month=2016;

    private static String CURRENT_PATH = Paths.get(".").toAbsolutePath().normalize().toString();
    private static final String REPORT_FILENAME = "report.jrxml";
    private static final String REPORT_SHEET_NAME = "sheet_report";


    public static void main(String[] args) throws IOException, JRException {
//        connect_mysql();
        make_report();
//        make_report_data2();
//        make_report_data3();

    //    example();
//        log.trace("all");
//        log.debug("debug");
//        log.info("info");
//        log.warn("warning");
//        log.error("error");
    }

    private static void example() throws JRException {
        String sourceFileName = CURRENT_PATH+File.separator+"templates/DataSourceReport.jrxml";
        String destFileName = CURRENT_PATH+File.separator+"build/reports/DataSourceReport.jasper";
        JasperCompileManager.compileReportToFile(sourceFileName,destFileName);

        fill4();
        pdf();
    }
    public static void fill1() throws JRException
    {
        long start = System.currentTimeMillis();
        //Preparing parameters
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("ReportTitle", "Address Report");
        parameters.put("DataFile", "CustomDataSource.java");

        JasperFillManager.fillReportToFile("build/reports/DataSourceReport.jasper", parameters, new CustomDataSource());
        System.err.println("Filling time : " + (System.currentTimeMillis() - start));
    }

    public static void fill4() throws JRException
    {
        long start = System.currentTimeMillis();
        //Preparing parameters
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("ReportTitle", "Address Report");
        parameters.put("DataFile", "CustomBeanFactory.java - Bean Collection");

        JasperFillManager.fillReportToFile("build/reports/DataSourceReport.jasper", parameters, new JRBeanCollectionDataSource(CustomBeanFactory.getBeanCollection()));
        System.err.println("Filling time : " + (System.currentTimeMillis() - start));
    }

    public static void pdf() throws JRException
    {
        long start = System.currentTimeMillis();
        JasperExportManager.exportReportToPdfFile("build/reports/DataSourceReport.jrprint");
        System.err.println("PDF creation time : " + (System.currentTimeMillis() - start));
    }

    private static void connect_mysql() {
        Connection conn = null;
        Statement stmt = null;
        try{
            //Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");

            //Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);

            //Execute a query
            System.out.println("Creating statement...");
            stmt = conn.createStatement();
            String sql;
            sql = "SELECT id, name FROM t1";
            ResultSet rs = stmt.executeQuery(sql);

            //Extract data from result set
            while(rs.next()){
                //Retrieve by column name
                int id  = rs.getInt("id");
                String name = rs.getString("name");

                //Display values
                System.out.print("ID: " + id);
                System.out.print(", NAME: " + name);
                System.out.println();
            }
            //Clean-up environment
            rs.close();
            stmt.close();
            conn.close();
        }catch(SQLException se){
            //Handle errors for JDBC
            se.printStackTrace();
        }catch(Exception e){
            //Handle errors for Class.forName
            e.printStackTrace();
        }finally{
            //finally block used to close resources
            try{
                if(stmt!=null)
                    stmt.close();
            }catch(SQLException se2){
            }// nothing we can do
            try{
                if(conn!=null)
                    conn.close();
            }catch(SQLException se){
                se.printStackTrace();
            }//end finally try
        }//end try
        System.out.println("Goodbye MySql!");
    }
    private static void make_report() throws JRException, IOException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String formattedDate = sdf.format(new Date());

        String path = "reports";
        File dir = new File(TEMP_DIR + File.separator + path);
        dir.mkdirs();
        FileFormat format = FileFormat.PPTX;
        String expansion=".XLSX";
        if(format==FileFormat.XLSX){
            expansion = ".XLSX";
        }
        else if(format==FileFormat.PPTX){
            expansion = ".PPTX";
        }

        File reportFile = new File(dir, "report-" + "_" + formattedDate + expansion);
        //byte[] reportByteArray = generateReport(year,month, "XLSX");
        byte[] reportByteArray = generateReport(year,month, format);

        System.out.println(dir.toString());

        FileOutputStream fos = new FileOutputStream(reportFile);
        fos.write(reportByteArray);
        fos.flush();
        fos.close();
    }
    private static void make_report_data2() {
        //jrxml->jasper
        String sourceFileName = CURRENT_PATH +
                                File.separator +
                                "jasper_report_template2.jrxml";

        System.out.println("Compiling Report Design ...");
        try {
            /**
             * Compile the report to a file name same as
             * the JRXML file name
             */
            JasperCompileManager.compileReportToFile(sourceFileName);
        } catch (JRException e) {
            e.printStackTrace();
        }
        System.out.println("Done compiling!!! ...");

        //jasper->jprint
        sourceFileName = CURRENT_PATH +
                File.separator +
                "jasper_report_template2.jasper";
        DataBeanList DataBeanList = new DataBeanList();
        ArrayList<DataBean> dataList = DataBeanList.getDataBeanList();

        JRBeanCollectionDataSource beanColDataSource =
                new JRBeanCollectionDataSource(dataList);

        Map<String,Object> parameters = new HashMap<>();
        try {
            JasperFillManager.fillReportToFile(
                    sourceFileName,
                    parameters,
                    beanColDataSource);
        } catch (JRException e) {
            e.printStackTrace();
        }

        //jasper->PDF/HTML/XLS
        sourceFileName = CURRENT_PATH +
                File.separator +
                "jasper_report_template2.jasper";

        String printFileName = null;
        try {
            printFileName = JasperFillManager.fillReportToFile(sourceFileName,
                    parameters, beanColDataSource);
            if (printFileName != null) {
                /**
                 * 1- export to PDF
                 */
                JasperExportManager.exportReportToPdfFile(printFileName,
                        "sample_report.pdf");

                /**
                 * 2- export to HTML
                 */
                JasperExportManager.exportReportToHtmlFile(printFileName,
                        "sample_report.html");

                /**
                 * 3- export to Excel sheet
                 */
                JRXlsExporter exporter = new JRXlsExporter();

                exporter.setParameter(JRExporterParameter.INPUT_FILE_NAME,
                        printFileName);
                exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME,
                        "sample_report.xls");

                exporter.exportReport();
            }
        } catch (JRException e) {
            e.printStackTrace();
        }

    }
    private static void make_report_data3() {
        String jrxml = CURRENT_PATH +
                File.separator +
                "jasper_report_template.jrxml";
        String jasper = null;
        try {
            jasper = JasperCompileManager.compileReportToFile(jrxml);
        } catch (JRException e) {
            e.printStackTrace();
        }
        DataBeanList DataBeanList = new DataBeanList();
        ArrayList<DataBean> dataList = DataBeanList.getDataBeanList();

        JRBeanCollectionDataSource beanColDataSource =
                new JRBeanCollectionDataSource(dataList);

        Map parameters = new HashMap();
        try {
            JasperFillManager.fillReportToFile(
                    jasper,//sourceFileName,
                    parameters,
                    beanColDataSource);
        } catch (JRException e) {
            e.printStackTrace();
        }


        String printFileName = null;
        try {
            printFileName = JasperFillManager.fillReportToFile(jasper,//sourceFileName,
                    parameters, beanColDataSource);
            if (printFileName != null) {
                /**
                 * 1- export to PDF
                 */
                JasperExportManager.exportReportToPdfFile(printFileName,
                        "sample_report.pdf");

                /**
                 * 2- export to HTML
                 */
                JasperExportManager.exportReportToHtmlFile(printFileName,
                        "sample_report.html");

                /**
                 * 3- export to Excel sheet
                 */
                JRXlsExporter exporter = new JRXlsExporter();

                exporter.setParameter(JRExporterParameter.INPUT_FILE_NAME,
                        printFileName);
                exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME,
                        "sample_report.xls");

                exporter.exportReport();
            }
        } catch (JRException e) {
            e.printStackTrace();
        }

    }

    private static byte[] generateReport(Integer year, Integer month, FileFormat format) throws JRException {

        if(format == FileFormat.XLSX) {
            ArrayList<JasperPrint> sheetList = new ArrayList<JasperPrint>();
            List<String> sheetNames = new ArrayList<String>();

            //Added polish local settings
            Locale locale = new Locale("pl", "PL");

            // prepare summary report (1st tab in xls)
            JasperDesign jd = JRXmlLoader.load(CURRENT_PATH + File.separator + REPORT_FILENAME);
            JasperReport jr = JasperCompileManager.compileReport(jd);
            JasperPrint jp = null;

            Map<String, Object> params = new HashMap<String, Object>();
            params.put("month", month);
            params.put("year", year);
            //push locale into summary report
            params.put(JRParameter.REPORT_LOCALE, locale);

            jp = JasperFillManager.fillReport(jr, params);

            sheetList.add(jp);
            sheetNames.add(REPORT_SHEET_NAME);


            JRXlsxExporter exporter = new JRXlsxExporter();

            exporter.setExporterInput(SimpleExporterInput.getInstance(sheetList));

            SimpleXlsxReportConfiguration reportConfiguration = new SimpleXlsxReportConfiguration();
            reportConfiguration.setOnePagePerSheet(true);
            reportConfiguration.setSheetNames(sheetNames.toArray(new String[0]));

            exporter.setConfiguration(reportConfiguration);


            ByteArrayOutputStream out = new ByteArrayOutputStream();
            OutputStreamExporterOutput exporterOutput = new SimpleOutputStreamExporterOutput(out);
            exporter.setExporterOutput(exporterOutput);

            exporter.exportReport();

            return out.toByteArray();
        }
        else if(format == FileFormat.PPTX){
            ArrayList<JasperPrint> sheetList = new ArrayList<JasperPrint>();
            List<String> sheetNames = new ArrayList<String>();

            //Added polish local settings
            Locale locale = new Locale("pl", "PL");

            // prepare summary report (1st tab in xls)
            JasperDesign jd = JRXmlLoader.load(CURRENT_PATH + File.separator + REPORT_FILENAME);
            JasperReport jr = JasperCompileManager.compileReport(jd);
            JasperPrint jp = null;

            Map<String, Object> params = new HashMap<String, Object>();
            params.put("month", month);
            params.put("year", year);
            //push locale into summary report
            params.put(JRParameter.REPORT_LOCALE, locale);

            jp = JasperFillManager.fillReport(jr, params);

            sheetList.add(jp);
            sheetNames.add(REPORT_SHEET_NAME);


            JRPptxExporter exporter = new JRPptxExporter();
            exporter.setExporterInput(SimpleExporterInput.getInstance(sheetList));

            SimplePptxReportConfiguration reportConfiguration = new SimplePptxReportConfiguration();

//            SimpleXlsxReportConfiguration reportConfiguration = new SimpleXlsxReportConfiguration();
//            reportConfiguration.setOnePagePerSheet(true);
//            reportConfiguration.setSheetNames(sheetNames.toArray(new String[0]));

            exporter.setConfiguration(reportConfiguration);


            ByteArrayOutputStream out = new ByteArrayOutputStream();
            OutputStreamExporterOutput exporterOutput = new SimpleOutputStreamExporterOutput(out);
            exporter.setExporterOutput(exporterOutput);

            exporter.exportReport();

            return out.toByteArray();
        }

        return null;
    }
}
