package com.example.dell.myapplication;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;


public class MainActivity extends Activity {
    Button writeExcelButton, readExcelButton;
    ListView lvInternalStorage;
    static String TAG = "ExelLog";

    String lastDirectory;
    ArrayList<String> pathHistory;
    int count = 0;

    private String[] FilePathStrings;
    private String[] FileNameStrings;
    private File[] listFile;
    File file;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lvInternalStorage = (ListView) findViewById(R.id.lvInternalStorage);
        writeExcelButton = (Button) findViewById(R.id.writeExcel);
//        writeExcelButton.setOnClickListener(this);
        readExcelButton = (Button) findViewById(R.id.readExcel);
        //Opens the SDCard or phone memory
        readExcelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count = 0;
                pathHistory = new ArrayList<String>();
                pathHistory.add(count, System.getenv("EXTERNAL_STORAGE"));
                Log.d(TAG, "btnSDCard: " + pathHistory.get(count));
                checkInternalStorage();
            }
        });

        lvInternalStorage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                lastDirectory = pathHistory.get(count);
                if (lastDirectory.equals(adapterView.getItemAtPosition(i))) {
                    Log.d(TAG, "lvInternalStorage: Selected a file for upload: " + lastDirectory);

                    //Execute method for reading the excel data.
                    readExcelFile(MainActivity.this, lastDirectory);

                } else {
                    count++;
                    pathHistory.add(count, (String) adapterView.getItemAtPosition(i));
                    checkInternalStorage();
                    Log.d(TAG, "lvInternalStorage: " + pathHistory.get(count));
                }
            }
        });
    }

    private void checkInternalStorage() {
        Log.d(TAG, "checkInternalStorage: Started.");
        try {
            if (!Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                Toast.makeText(this, "\"No SD card found.\"", Toast.LENGTH_SHORT).show();
            } else {
                // Locate the image folder in your SD Car;d
                file = new File(pathHistory.get(count));
                Log.d(TAG, "checkInternalStorage: directory path: " + pathHistory.get(count));
            }

            listFile = file.listFiles();

            // Create a String array for FilePathStrings
            FilePathStrings = new String[listFile.length];

            // Create a String array for FileNameStrings
            FileNameStrings = new String[listFile.length];

            for (int i = 0; i < listFile.length; i++) {
                // Get the path of the image file
                FilePathStrings[i] = listFile[i].getAbsolutePath();
                // Get the name image file
                FileNameStrings[i] = listFile[i].getName();
            }

            for (int i = 0; i < listFile.length; i++) {
                Log.d("Files", "FileName:" + listFile[i].getName());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, FilePathStrings);
            lvInternalStorage.setAdapter(adapter);
        } catch (NullPointerException e) {
            Log.e(TAG, "checkInternalStorage: NULLPOINTEREXCEPTION " + e.getMessage());
        }
    }

    private static boolean saveExcelFile(Context context, Workbook wb, String fileName) {

        // check if available and not read only
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Log.e(TAG, "Storage not available or read only");
            return false;
        }

        boolean success = false;


        // Create a path where we will place our List of objects on external storage
        File file = new File(context.getExternalFilesDir(null), fileName);
        FileOutputStream os = null;

        try {
            os = new FileOutputStream(file);
            wb.write(os);
            Log.w("FileUtils", "Writing file" + file);
            success = true;
        } catch (IOException e) {
            Log.w("FileUtils", "Error writing " + file, e);
        } catch (Exception e) {
            Log.w("FileUtils", "Failed to save file", e);
        } finally {
            try {
                if (null != os)
                    os.close();
            } catch (Exception ex) {
            }
        }
        return success;
    }

    private static void readExcelFile(Context context, String filePath) {

        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Log.e(TAG, "Storage not available or read only");
            return;
        }

        //New Workbook
        Workbook wb2 = new HSSFWorkbook();

        Cell c2 = null;

        //Cell style for header row
        CellStyle cs2 = wb2.createCellStyle();
        cs2.setFillForegroundColor(HSSFColor.LIME.index);
        cs2.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        //Cell style for header row
        CellStyle cs3 = wb2.createCellStyle();

        //New Sheet
        Sheet writesheet1 = null;
        writesheet1 = wb2.createSheet("TAKATAK");

        // Generate column headings
        Row writerow = writesheet1.createRow(0);

        c2 = writerow.createCell(0);
        c2.setCellValue("Item Number");
        c2.setCellStyle(cs2);

        c2 = writerow.createCell(1);
        c2.setCellValue("hfgsdf");
        c2.setCellStyle(cs2);

        c2 = writerow.createCell(2);
        c2.setCellValue("Price");
        c2.setCellStyle(cs2);

        writesheet1.setColumnWidth(0, (15 * 500));
        writesheet1.setColumnWidth(1, (15 * 500));
        writesheet1.setColumnWidth(2, (15 * 500));


        try {
            //New Workbook
            Workbook wb = new HSSFWorkbook();

            Cell c = null;

            //Cell style for header row
            CellStyle cs = wb.createCellStyle();
            cs.setFillForegroundColor(HSSFColor.LIME.index);
            cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

            //New Sheet
            Sheet sheet1 = null;
            sheet1 = wb.createSheet("myOrder");

            // Generate column headings
            Row row = sheet1.createRow(0);

            c = row.createCell(0);
            c.setCellValue("Item Number");
            c.setCellStyle(cs);

            c = row.createCell(1);
            c.setCellValue("hfgsdf");
            c.setCellStyle(cs);

            c = row.createCell(2);
            c.setCellValue("Price");
            c.setCellStyle(cs);

            sheet1.setColumnWidth(0, (15 * 500));
            sheet1.setColumnWidth(1, (15 * 500));
            sheet1.setColumnWidth(2, (15 * 500));


            // Creating Input Stream
//            File file = new File(context.getExternalFilesDir(null), filename);
            Toast.makeText(context, "Aa gaya!", Toast.LENGTH_SHORT).show();
            File file = new File(filePath);
            FileInputStream myInput = new FileInputStream(file);

            // Create a POIFSFileSystem object
            POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);

            // Create a workbook using the File System
            HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);

            // Get the first sheet from workbook
            HSSFSheet mySheet = myWorkBook.getSheetAt(0);

            /** We now need something to iterate through the cells.**/
            Iterator rowIter = mySheet.rowIterator();

            int i = 1, j = 0;

            while (rowIter.hasNext()) {
                HSSFRow myRow = (HSSFRow) rowIter.next();
                Iterator cellIter = myRow.cellIterator();
                Row iwriterow = writesheet1.createRow(i);
                i++;
                j = 0;
                while (cellIter.hasNext()) {
                    HSSFCell myCell = (HSSFCell) cellIter.next();
                    Log.d(TAG, "Cell Value: " + myCell.toString());
                    c2 = iwriterow.createCell(j);
                    c2.setCellValue(myCell.toString());
                    c2.setCellStyle(cs3);
                    j++;
                    Toast.makeText(context, "cell Value: " + myCell.toString(), Toast.LENGTH_SHORT).show();
                }
            }
            Toast.makeText(context, "Completed!", Toast.LENGTH_SHORT).show();
            saveExcelFile(context, wb2, "Successismine.xls");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return;
    }

    public static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    public static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }
}
