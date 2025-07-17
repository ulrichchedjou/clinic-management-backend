package com.center.clinicManagementSystem.service;

import com.center.clinicManagementSystem.dto.report.ReportRequest;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class ReportExportService {

    private final ReportService reportService;
    private final ChartService chartService;

    public ReportExportService(ReportService reportService, ChartService chartService) {
        this.reportService = reportService;
        this.chartService = chartService;
    }

    public CompletableFuture<byte[]> exportToPdf(ReportRequest request) {
        return reportService.generateReport(request)
                .thenApply(report -> {
                    try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                        Document document = new Document();
                        PdfWriter.getInstance(document, out);
                        document.open();

                        // En-tête du document
                        addTitle(document, "Rapport - " + request.getReportType().name());
                        addMetadata(document, request);
                        addNewLine(document);

                        // Contenu du rapport
                        addReportContent(document, report, request);

                        document.close();
                        return out.toByteArray();
                    } catch (Exception e) {
                        throw new RuntimeException("Erreur lors de la génération du PDF", e);
                    }
                });
    }

    @Async
    public CompletableFuture<byte[]> exportToExcel(ReportRequest request) {
        return reportService.generateReport(request).thenCompose(report -> {
            try (Workbook workbook = new XSSFWorkbook();
                 ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                
                Sheet sheet = workbook.createSheet("Rapport");
                
                // En-tête
                Row headerRow = sheet.createRow(0);
                createCell(headerRow, 0, "Rapport", workbook.createCellStyle(), workbook);
                createCell(headerRow, 1, request.getReportType().name(), workbook.createCellStyle(), workbook);
                
                // Contenu
                int rowNum = 2;
                for (Map.Entry<String, Object> entry : report.entrySet()) {
                    Row row = sheet.createRow(rowNum++);
                    createCell(row, 0, entry.getKey(), workbook.createCellStyle(), workbook);
                    createCell(row, 1, String.valueOf(entry.getValue()), workbook.createCellStyle(), workbook);
                }
                
                // Ajuster la largeur des colonnes
                for (int i = 0; i < 2; i++) {
                    sheet.autoSizeColumn(i);
                }
                
                workbook.write(out);
                return CompletableFuture.completedFuture(out.toByteArray());
                
            } catch (Exception e) {
                log.error("Erreur lors de la génération du fichier Excel", e);
                return CompletableFuture.failedFuture(new RuntimeException("Erreur lors de la génération du fichier Excel", e));
            }
        }).exceptionally(throwable -> {
            log.error("Erreur lors de la génération du rapport", throwable);
            return CompletableFuture.failedFuture(throwable);
        }).thenCompose(completableFuture -> completableFuture);
    }

    private void addTitle(Document document, String title) throws DocumentException {
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
        Paragraph titleParagraph = new Paragraph(title, titleFont);
        titleParagraph.setAlignment(Element.ALIGN_CENTER);
        titleParagraph.setSpacingAfter(20);
        document.add(titleParagraph);
    }

    private void addMetadata(Document document, ReportRequest request) throws DocumentException {
        Font metadataFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.GRAY);
        Paragraph metadata = new Paragraph();
        metadata.setFont(metadataFont);
        metadata.add("Période: " + request.getStartDate() + " à " + request.getEndDate() + "\n");
        metadata.add("Généré le: " + LocalDate.now().format(DateTimeFormatter.ISO_DATE));
        metadata.setSpacingAfter(20);
        document.add(metadata);
    }

    private void addReportContent(Document document, Map<String, Object> report, ReportRequest request) 
            throws DocumentException {
        
        switch (request.getReportType()) {
            case APPOINTMENT_STATS:
                addAppointmentStats(document, report);
                break;
            case USER_ACTIVITY:
                addUserActivity(document, report);
                break;
            case SYSTEM_PERFORMANCE:
                addSystemPerformance(document, report);
                break;
            case ERROR_ANALYSIS:
                addErrorAnalysis(document, report);
                break;
        }
    }

    private void addAppointmentStats(Document document, Map<String, Object> report) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        addTableHeader(table, "Métrique", "Valeur");
        
        for (Map.Entry<String, Object> entry : report.entrySet()) {
            addTableRow(table, entry.getKey(), String.valueOf(entry.getValue()));
        }
        
        document.add(table);
    }

    private void addUserActivity(Document document, Map<String, Object> report) throws DocumentException {
        // Implémentation similaire pour l'activité des utilisateurs
        // ...
    }

    private void addSystemPerformance(Document document, Map<String, Object> report) throws DocumentException {
        // Implémentation pour les performances système
        // ...
    }

    private void addErrorAnalysis(Document document, Map<String, Object> report) throws DocumentException {
        // Implémentation pour l'analyse des erreurs
        // ...
    }

    private void addTableHeader(PdfPTable table, String... headers) {
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);
        for (String header : headers) {
            PdfPCell cell = new PdfPCell();
            cell.setBackgroundColor(new BaseColor(66, 139, 202));
            cell.setPadding(8);
            cell.setPhrase(new Phrase(header, headerFont));
            table.addCell(cell);
        }
    }

    private void addTableRow(PdfPTable table, String... cells) {
        for (String cell : cells) {
            table.addCell(cell);
        }
    }

    private void addNewLine(Document document) throws DocumentException {
        document.add(new Paragraph(" "));
    }

    private void createCell(Row row, int column, String value, CellStyle style, Workbook workbook) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value);
        
        if (style == null) {
            style = workbook.createCellStyle();
            style.setWrapText(true);
        }
        
        cell.setCellStyle(style);
    }
}
