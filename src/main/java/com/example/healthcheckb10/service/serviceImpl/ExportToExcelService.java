package com.example.healthcheckb10.service.serviceImpl;

import com.example.healthcheckb10.dto.doctor.DoctorResponse;
import com.example.healthcheckb10.dto.schedule.responce.DateInfo;
import com.example.healthcheckb10.dto.schedule.responce.ScheduleResponse;
import com.example.healthcheckb10.dto.schedule.responce.TimeSlot;
import com.example.healthcheckb10.exceptions.BadRequestException;
import com.example.healthcheckb10.service.DoctorService;
import com.example.healthcheckb10.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExportToExcelService {
    private final ScheduleService scheduleService;
    private final DoctorService doctorService;

    public byte[] exportExcel(String dateFrom, String dateUntil) throws IOException {
        if(isValidDateFormat(dateFrom) && isValidDateFormat(dateUntil)){
            if(LocalDate.parse(dateFrom).isBefore(LocalDate.parse(dateUntil))){
                List<ScheduleResponse> schedules = scheduleService.getAll(dateFrom, dateUntil);
                if(schedules.size() > 0){
                    return export(schedules);
                }
                else{
                    List<DoctorResponse> doctors = doctorService.getAllDoctors();
                    return exportForEmptySchedule(doctors,dateFrom,dateUntil);
                }
            }
            else{
                throw new BadRequestException("Ошибка: неверный временной интервал," + dateFrom + " идет после " +dateUntil);
            }
        }
        else {
            throw new BadRequestException("Ошибка: укажите корректную дату. Формат должен быть \"yyyy-MM-dd\"");
        }
    }

    public byte[] export(List<ScheduleResponse> schedules) throws IOException {
        log.info("Начало экспорта расписания в электронную таблицу Excel.");
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Расписание");
            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyle.setWrapText(true);
            int rowNum = 0;
            for (ScheduleResponse schedule : schedules) {
                if (rowNum == 0) {
                    Row headerRow = sheet.createRow(rowNum);
                    headerRow.createCell(0).setCellValue("Специалисты");
                    headerRow.createCell(1).setCellValue("Должность");
                    headerRow.setHeight((short) 1000);
                    int cellNum = 2;
                    for (DateInfo dateInfo : schedule.getDateDayTimeInfos()) {
                        Cell cell = headerRow.createCell(cellNum++);
                        cell.setCellValue(cell.getStringCellValue() + dateInfo.getDayOfWeek() + "\n" + dateInfo.getDateDay());
                    }
                    for (Cell cell : headerRow) {
                        cell.setCellStyle(cellStyle);
                        sheet.setColumnWidth(cell.getColumnIndex(), 4500);
                    }
                }
                rowNum++;
                Row row = sheet.createRow(rowNum);
                row.setHeight((short) 2500);
                row.createCell(0).setCellValue(schedule.getDoctorFullName());
                row.createCell(1).setCellValue(schedule.getDoctorPosition());
                int cellNum = 2;
                for (DateInfo dateInfo : schedule.getDateDayTimeInfos()) {
                    Cell cell = row.createCell(cellNum);
                    for (TimeSlot timeSlot : dateInfo.getTimeIntervals()) {
                        cell.setCellValue(cell.getStringCellValue() + timeSlot.getStartTime() + " - " + timeSlot.getEndTime() + "\n");
                    }
                    cell.setCellValue(String.valueOf(cell));
                    cellNum++;
                }
                for (Cell cell : row) {
                    cell.setCellStyle(cellStyle);
                }
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            log.info("Завершено экспортирование расписания в электронную таблицу Excel.");
            return outputStream.toByteArray();
        }
    }

    public byte[] exportForEmptySchedule(List<DoctorResponse> doctors, String dateFrom, String dateUntil) throws IOException {
        log.info("Начало экспорта расписания в электронную таблицу Excel.");
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Расписание");
            int rowNum = 0;
            for (DoctorResponse doctor : doctors) {
                if (rowNum == 0) {
                    Row headerRow = sheet.createRow(rowNum);
                    headerRow.createCell(0).setCellValue("Специалисты");
                    headerRow.createCell(1).setCellValue("Должность");
                    int cellNum = 2;
                    LocalDate startDate= LocalDate.parse(dateFrom);
                    LocalDate endDate= LocalDate.parse(dateUntil);
                    for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                        headerRow.createCell(cellNum++).setCellValue(String.valueOf(date));
                    }
                    for (Cell cell : headerRow) {
                        sheet.setColumnWidth(cell.getColumnIndex(), 4500);
                    }
                }
                rowNum++;
                Row row = sheet.createRow(rowNum);
                row.createCell(0).setCellValue(doctor.getFirstName() + " " + doctor.getLastName());
                row.createCell(1).setCellValue(doctor.getPosition());
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            log.info("Завершено экспортирование расписания в электронную таблицу Excel.");
            return outputStream.toByteArray();
        }
    }

    public boolean isValidDateFormat(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        try {
            sdf.parse(dateStr);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}