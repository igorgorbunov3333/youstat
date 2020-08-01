package com.company.youstat;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesResponse;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.SpreadsheetProperties;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GoogleSheetApiService {
    private Sheets service;

    public GoogleSheetApiService(Sheets service) {
        this.service = service;
    }

    public String create(String title, List<VideoItem> videoItems) throws IOException {
        Sheets service = this.service;
        // [START sheets_create]
        Spreadsheet spreadsheet = new Spreadsheet()
                .setProperties(new SpreadsheetProperties()
                        .setTitle(title));
        Sheets.Spreadsheets.Create s = service.spreadsheets().create(spreadsheet)
                .setFields("spreadsheetId");
        spreadsheet = s.execute();

        List<ValueRange> data = new ArrayList<>();
        data.add(new ValueRange()
                .setRange("A1")
                .setValues(new SheetRange("A1", videoItems).getValues()));
        data.add(new ValueRange()
                .setRange("B1")
                .setValues(new SheetRange("B1", videoItems).getValues()));
        data.add(new ValueRange()
                .setRange("C1")
                .setValues(new SheetRange("C1", videoItems).getValues()));
        data.add(new ValueRange()
                .setRange("D1")
                .setValues(new SheetRange("D1", videoItems).getValues()));
        data.add(new ValueRange()
                .setRange("E1")
                .setValues(new SheetRange("E1", videoItems).getValues()));
        data.add(new ValueRange()
                .setRange("F1")
                .setValues(new SheetRange("F1", videoItems).getValues()));
        data.add(new ValueRange()
                .setRange("G1")
                .setValues(new SheetRange("G1", videoItems).getValues()));
        data.add(new ValueRange()
                .setRange("H1")
                .setValues(new SheetRange("H1", videoItems).getValues()));

        BatchUpdateValuesRequest batchBody = new BatchUpdateValuesRequest()
                .setValueInputOption("USER_ENTERED")
                .setData(data);

        BatchUpdateValuesResponse batchResult = service.spreadsheets().values()
                .batchUpdate(spreadsheet.getSpreadsheetId(), batchBody)
                .execute();

        System.out.println("Spreadsheet ID: " + spreadsheet.getSpreadsheetId());
        // [END sheets_create]
        return spreadsheet.getSpreadsheetId();
    }

}

