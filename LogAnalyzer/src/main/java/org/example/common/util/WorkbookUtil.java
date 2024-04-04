package org.example.common.util;

import com.spire.xls.*;

public class WorkbookUtil {

    public static void main(String[] args) {
        Workbook workbook = new Workbook();
        Worksheet worksheet = workbook.getWorksheets().get(0);

        createWorkbook();
    }
    public static void createWorkbook() {
        Workbook workbook = new Workbook();
        Worksheet worksheet = workbook.getWorksheets().get(0);
        worksheet.setName("Sheet1");
        worksheet.setGridLinesVisible(false);
    }

    @SafeVarargs
    public static void writeRow(Worksheet worksheet, int writeRowIndex, Long orDefault, Long aDefault, Long responseDatasOrDefault, String... datas) {
        for (int i = 0; i < datas.length; i++) {
            worksheet.get(writeRowIndex, i).setValue(datas[i]);
        }
    }


}
