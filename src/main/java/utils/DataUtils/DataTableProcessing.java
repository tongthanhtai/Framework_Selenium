package utils.DataUtils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.List;

public class DataTableProcessing {

    public static List<List<String>> getDataTableInCurrentPage(WebDriver driver, int row, int column) {
        List<List<String>> dataTable = new ArrayList<>();
        for (int i=1; i<=row; i++) {
            List<String> dataRow = new ArrayList<>();
            for (int j=1; j<=column; j++) {
                By locator = By.xpath("//tr["+i+"]/td["+j+"]");
                String data = driver.findElement(locator).getText();
                dataRow.add(data);
            }
            dataTable.add(dataRow);
        }
        return dataTable;
    }

    public static List<List<String>> getCustomDataTable(List<List<String>> dataTable, int[] customColumns) throws Exception {
        List<List<String>> customDataTable = new ArrayList<>();
        for (List<String> row: dataTable) {
            List<String> customRow = new ArrayList<>();
            for (int column: customColumns) {
                int index = column - 1;
                if (index < row.size()) {
                    customRow.add(row.get(index));
                } else {
                    throw new Exception("Invalid columns number");
                }
            }
            customDataTable.add(customRow);
        }
        return customDataTable;
    }
}
