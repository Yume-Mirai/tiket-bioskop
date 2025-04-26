package com.uasjava.tiketbioskop.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.uasjava.tiketbioskop.dto.GenericResponse;
import com.uasjava.tiketbioskop.service.ReportService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/admin/report")
public class ReportController {

    @Autowired
    private ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }
    
     @GetMapping("/generate-excel")
      @Operation(summary = "Menampilkan data user dan admin dengan excel")
    public ResponseEntity<Object> generateExcel(HttpServletResponse response) {
        response.setHeader("Content-Disposition", "attachment; filename=data_user.xlsx");
        try {
            return ResponseEntity.ok()
                                    .body(reportService.generateExcel());

        } catch (ResponseStatusException rse) {
            return ResponseEntity.status(rse.getStatusCode())
                                        .body(GenericResponse.builder()
                                        .success(false)
                                        .message(rse.getReason())
                                        .data(null)
                                        .build());
        }catch(Exception e){
            // e.printStackTrace();
            return ResponseEntity.internalServerError()
                                        .body(GenericResponse.builder()
                                        .success(false)
                                        .message("Internal Server Error")
                                        .data(null)
                                        .build());
        }
    }
}
