package com.uasjava.tiketbioskop.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.uasjava.tiketbioskop.model.Users;
import com.uasjava.tiketbioskop.repository.UserRepository;
import com.uasjava.tiketbioskop.service.ReportService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    private final UserRepository usersRepository;

    public ReportServiceImpl(UserRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public Object generateExcel() throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("DATA USER & ADMIN");

        // ===== Header Style =====
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        XSSFFont headerFont = ((XSSFWorkbook) workbook).createFont();
        headerFont.setFontName("Arial");
        headerFont.setFontHeightInPoints((short) 12);
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);

        // ===== Date Style =====
        CellStyle dateStyle = workbook.createCellStyle();
        short dateFormat = workbook.createDataFormat().getFormat("dd-MM-yyyy");
        dateStyle.setDataFormat(dateFormat);

        // ===== Body Style =====
        CellStyle bodyStyle = workbook.createCellStyle();
        XSSFFont bodyFont = ((XSSFWorkbook) workbook).createFont();
        bodyFont.setFontName("Calibri");
        bodyFont.setFontHeightInPoints((short) 11);
        bodyStyle.setFont(bodyFont);

        // ===== Header Row =====
        Row headerRow = sheet.createRow(0);
        String[] columns = { "ID", "USERNAME", "PASSWORD", "EMAIL", "NOMOR", "TANGGAL LAHIR", "STATUS" };

        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerStyle);
        }

        // ===== Data Rows =====
        List<Users> usersList = usersRepository.findAll();
        int currentIndexRow = 1;

        for (Users user : usersList) {
            Row row = sheet.createRow(currentIndexRow++);

            Cell cell0 = row.createCell(0);
            cell0.setCellValue(user.getId());
            cell0.setCellStyle(bodyStyle);

            Cell cell1 = row.createCell(1);
            cell1.setCellValue(user.getUsername());
            cell1.setCellStyle(bodyStyle);

            Cell cell2 = row.createCell(2);
            cell2.setCellValue(user.getPassword()); // atau sembunyikan password
            cell2.setCellStyle(bodyStyle);

            Cell cell3 = row.createCell(3);
            cell3.setCellValue(user.getEmail());
            cell3.setCellStyle(bodyStyle);

            Cell cell4 = row.createCell(4);
            cell4.setCellValue(user.getNomor());
            cell4.setCellStyle(bodyStyle);

            Cell cell5 = row.createCell(5);
            if (user.getTanggal_lahir() != null) {
                cell5.setCellValue(user.getTanggal_lahir()); // Harus berupa java.util.Date
                cell5.setCellStyle(dateStyle);
            }

            Cell cell6 = row.createCell(6);
            cell6.setCellValue(user.getStatus());
            cell6.setCellStyle(bodyStyle);
        }

        // ===== Auto-size columns =====
        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // ===== Output Stream =====
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            workbook.write(outputStream);
        } finally {
            workbook.close();
        }

        return outputStream.toByteArray();
    }

}
