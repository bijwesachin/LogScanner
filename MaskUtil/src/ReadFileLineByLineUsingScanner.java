import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;  
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;



public class ReadFileLineByLineUsingScanner {
	
	public static Scanner logFileScanner = null;
	public static Scanner fileNameScanner = null;
	public static Scanner group1AttributeNameScanner = null;
	public static Scanner group2AttributeNameScanner = null;
	
	public static FileWriter fileWriter = null;
	public static PrintWriter printWriter = null;

	public static void main(String[] args) throws IOException {
		try {
			

			List<String> group1AttribueNames = new ArrayList<>();
			//List<String> group2AttribueNames = new ArrayList<>();
			
			group1AttributeNameScanner = new Scanner(new File("resources\\fields\\Fields_group1.txt"));
			while (group1AttributeNameScanner.hasNextLine()) {
				String fieldName = group1AttributeNameScanner.nextLine();
				group1AttribueNames.add(fieldName.trim());
				
			}
			
		
/*			group2AttributeNameScanner = new Scanner(new File("resources\\fields\\Fields_group2.txt"));
			while (group2AttributeNameScanner.hasNextLine()) {
				String fieldName = group2AttributeNameScanner.nextLine();
				group2AttribueNames.add(fieldName.trim());
				
			}*/
			
			File dir = new File("resources\\");
			  File[] directoryListing = dir.listFiles();
			  if (directoryListing != null) {
			    for (File child : directoryListing) {
			     if (child.getName().startsWith("Group1_")) {
			    	String fileName = child.getName();
			 		fileWriter = new FileWriter("resources\\masked_files\\"+fileName+"_generated.txt");
					printWriter = new PrintWriter(fileWriter);
			    	System.out.println("FileName "+fileName);
			    	group1Scanner(group1AttribueNames, child.getName(), printWriter);
			    	fileWriter.close();
			     }
			     if (child.getName().startsWith("Group2_")) {

			     }
			    }
			  }
			  
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}finally {
			logFileScanner.close();
			group1AttributeNameScanner.close();
		}
	}

	private static void group1Scanner(List<String> attribueNames, String fileName, PrintWriter printWriter) throws FileNotFoundException, IOException {
		logFileScanner = new Scanner(new File("resources\\"+fileName));

		
		List<String> productNames = maskSubProducts();
		while (logFileScanner.hasNextLine()) {
			String logline = logFileScanner.nextLine();

			for(String field : attribueNames) { 
				
				// Masking logs for Fields_group1
				if(logline.contains(field)) {
					int fieldStartIndex = logline.indexOf(field) + field.length();

					String textAfterFieldName = logline.substring(fieldStartIndex);
					int fieldEndIndex = fieldStartIndex + textAfterFieldName.indexOf("n,") -3;

					String secureText = logline.substring(fieldStartIndex, fieldEndIndex);
					logline = logline.replace(secureText, "XXXXXXXX");

				}
				
				 for (String productName : productNames) {
					 String secureText = productName;
		
					 if(logline.indexOf(secureText) > 0)
						 logline = logline.replace(secureText, "XXXXXXXX");
				}
			}
			
			printWriter.println(logline);
				
		}
		
		
	}
	
	private static List<String> maskSubProducts() throws FileNotFoundException, IOException {

		List<String> productNames = new ArrayList<>();
		
		String excelFilePath = "resources\\Pro-Sub.xlsx";
		FileInputStream inputStream = new FileInputStream(new File(excelFilePath));
		Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet firstSheet = workbook.getSheetAt(0);
        Iterator<Row> iterator = firstSheet.iterator();
         
        while (iterator.hasNext()) {
            Row nextRow = iterator.next();
            Iterator<Cell> cellIterator = nextRow.cellIterator();
             
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                 
                switch (cell.getCellType()) {
                    case STRING:
                        productNames.add(cell.getStringCellValue());
                        break;

                }
            }
        }
        
        workbook.close();
        inputStream.close();
        
        return productNames;
		
	}
	
}


