package org.example.common.util;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class GraphUtil {


    public static void createLineGraph(String graphTitle, String axisLabel, String valueAxisLabel) {

//        String filePath = "/Users/simjeonghun/차세대 로그/차세대 에이전트 13대 HTTP 1.1 TPS 300/발송 로그/csv/AGT20240402HTTP1_1AGT13TPS300(1).csv";
//        GraphCreator graphCreator = new GraphCreator(graphTitle, axisLabel, valueAxisLabel);
//        SwingUtilities.invokeLater(new Runnable() {
//            @Override
//            public void run() {
//                graphCreator.setVisible(true);
//            }
//        });
        // CSV 파일 읽기
//        try (FileReader reader = new FileReader(filePath);
//             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader())) {

            // 그래프 데이터를 저장할 데이터셋 생성
//            DefaultCategoryDataset dataset = new DefaultCategoryDataset();

//            // CSV 파일의 각 행을 반복하며 데이터셋에 데이터 추가
//            for (CSVRecord csvRecord : csvParser) {
//                System.out.println("csvRecord : " + csvRecord);
//                String[] values = csvRecord.values();
////                String category = csvRecord.get("Fetch Count");
////                double value = Double.parseDouble(csvRecord.get("Fetch Count"));
////                dataset.addValue(value, "Series", category);
//            }

//
//            JFreeChart chart = ChartFactory.createBarChart(graphTitle, axisLabel, valueAxisLabel, dataset);
//
//            // 엑셀 파일 생성
//            try (Workbook workbook = new XSSFWorkbook()) {
//                Sheet sheet = workbook.createSheet("Graph Data");
//
//                // 그래프를 이미지로 변환
//                int width = 640; // 이미지 폭
//                int height = 480; // 이미지 높이
//                byte[] chartImage = ChartUtils.encodeAsPNG(chart.createBufferedImage(width, height));
//
//                // 엑셀에 그래프 이미지 삽입
//                int rownum = 0;
//                Row row = sheet.createRow(rownum++);
//                Cell cell = row.createCell(0);
//                cell.setCellValue("Graph Data");
//                row = sheet.createRow(rownum++);
//                cell = row.createCell(0);
//                cell.setCellValue("Graph:");
//                Drawing drawing = sheet.createDrawingPatriarch();
//                int pictureIdx = workbook.addPicture(chartImage, Workbook.PICTURE_TYPE_PNG);
//                CreationHelper helper = workbook.getCreationHelper();
//                ClientAnchor anchor = helper.createClientAnchor();
//                anchor.setCol1(1);
//                anchor.setRow1(rownum);
//                Picture pict = drawing.createPicture(anchor, pictureIdx);
//                pict.resize();
//
//                // 엑셀 파일 저장
//                try (FileOutputStream fileOut = new FileOutputStream("/Users/simjeonghun/Desktop/graph_data.xlsx")) {
//                    workbook.write(fileOut);
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//                System.out.println(e.getMessage());
//                System.out.println("HERE");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//
//            System.out.println(e.getMessage());
//            System.out.println("HERE2");
//        }
    }
}
