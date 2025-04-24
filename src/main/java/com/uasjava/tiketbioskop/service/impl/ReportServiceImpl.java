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

    // @Override
    // public Object generateExcel() throws IOException {
    // Workbook workbook = new XSSFWorkbook();

    // Sheet sheet = workbook.createSheet("DATA USER");

    // CellStyle headerStyle = workbook.createCellStyle();
    // headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
    // headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

    // XSSFFont headerFont = ((XSSFWorkbook) workbook).createFont();
    // headerFont.setFontName("Arial");
    // headerFont.setFontHeightInPoints((short) 12);
    // headerFont.setBold(true);
    // headerFont.setColor(IndexedColors.WHITE.getIndex());

    // headerStyle.setFont(headerFont);

    // Row headerRow = sheet.createRow(0);

    // Cell idCell = headerRow.createCell(0);
    // idCell.setCellValue("ID");
    // idCell.setCellStyle(headerStyle);

    // Cell usernameCell = headerRow.createCell(1);
    // usernameCell.setCellValue("USERNAME");
    // usernameCell.setCellStyle(headerStyle);

    // Cell passwordCell = headerRow.createCell(2);
    // passwordCell.setCellValue("PASSWORD");
    // passwordCell.setCellStyle(headerStyle);

    // Cell emailCell = headerRow.createCell(3);
    // emailCell.setCellValue("EMAIL");
    // emailCell.setCellStyle(headerStyle);

    // Cell nomorCell = headerRow.createCell(4);
    // nomorCell.setCellValue("NOMOR");
    // nomorCell.setCellStyle(headerStyle);

    // Cell bornCell = headerRow.createCell(5);
    // bornCell.setCellValue("TANGGAL LAHIR");
    // bornCell.setCellStyle(headerStyle);

    // Cell statusCell = headerRow.createCell(6);
    // statusCell.setCellValue("Status");
    // statusCell.setCellStyle(headerStyle);

    // List<Users> usersList = usersRepository.findAll();

    // int currentIndexRow = 1; // guna untuk membuat baris pada excel
    // for(Users user : usersList) {
    // Row bodyRow = sheet.createRow(currentIndexRow);

    // Cell cell = bodyRow.createCell(0);
    // cell.setCellValue(user.getId());

    // cell = bodyRow.createCell(1);
    // cell.setCellValue(user.getUsername());

    // cell = bodyRow.createCell(2);
    // cell.setCellValue(user.getPassword());

    // cell = bodyRow.createCell(3);
    // cell.setCellValue(user.getEmail());

    // cell = bodyRow.createCell(4);
    // cell.setCellValue(user.getNomor());

    // cell = bodyRow.createCell(5);
    // cell.setCellValue(user.getTanggal_lahir());

    // cell = bodyRow.createCell(6);
    // cell.setCellValue(user.getStatus());

    // currentIndexRow++;
    // }

    // ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    // try{
    // workbook.write(outputStream);
    // outputStream.toByteArray();
    // }finally{
    // workbook.close();
    // }

    // return outputStream.toByteArray();
    // }

    @Override
    public Object generateExcel() throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("DATA USER");

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
