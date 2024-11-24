package co.bebolder.cinemafood;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Service
public class FoodService {

    private static final String FILE_PATH = System.getenv("PATH_FILE");


    public Map<String, Integer> getFoods() {
        Map<String, Integer> foods = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    foods.put(parts[0], Integer.valueOf(parts[1]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return foods;
    }

    public void updateFood(String item, int quantity) throws StockUnavailableException {
        Map<String, Integer> foods = getFoods();
        if (foods.containsKey(item) && foods.get(item) >= quantity) {
            int actualQuantity = foods.get(item);
            foods.put(item, actualQuantity - quantity);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
                for (Map.Entry<String, Integer> entry : foods.entrySet()) {
                    writer.write(entry.getKey() + "," + entry.getValue());
                    writer.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            throw new StockUnavailableException("No hay suficiente stock disponible.");
        }
    }

}
