package learn;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.Region;

public class ExcelTest {
	public static void printCell(HSSFCell cell) {
		int type = cell.getCellType();
		switch (type) {
		case 1:
			System.out.print(cell.getStringCellValue());
			break;
		case 0:
			System.out.print(cell.getNumericCellValue());
			break;
		case 3:
			System.out.print(" ");
			break;
		default:
			System.out.print(cell.getStringCellValue());
		}
	}
	public static void main(String[] args) {
		InputStream s = null;
		try {
			s = new FileInputStream("d:\\workbook.xls");
			HSSFWorkbook wb = new HSSFWorkbook(s);
			HSSFSheet sheet = wb.getSheetAt(0);
			int n = sheet.getLastRowNum();
			for(int i=0;i<=n;++i){
				HSSFRow row = sheet.getRow(i);
				int m = row.getLastCellNum();
				for(int j=0;j<m;++j){
					HSSFCell cell = row.getCell(j);
					printCell(cell);
					System.out.print("\t");
				}
				System.out.println();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			try {
				if(null!=s)s.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    }
	public static void writeFile() {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("理财资金报表");   //--->创建了一个工作簿
		sheet.addMergedRegion(new Region((short)0, (short)0, (short)1, (short)1));
		HSSFRow row = sheet.createRow(0);
		HSSFCell cell = row.createCell(0);
		cell.setCellValue("abc");
          
        FileOutputStream fileOut = null;  
        try{              
            fileOut = new FileOutputStream("d:\\workbook.xls");  
            wb.write(fileOut);  
            //fileOut.close();  
            System.out.print("OK");  
        }catch(Exception e){  
            e.printStackTrace();  
        }  
        finally{  
            if(fileOut != null){  
                try {
					fileOut.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  
            }  
        }  
	}
}
