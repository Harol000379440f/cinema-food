package co.bebolder.cinemafood;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/food")
public class FoodController {

    @Autowired
    private FoodService foodService;

    @CrossOrigin(origins = "*")
    @GetMapping("/available")
    public Map<String, Integer> getAvailableFood() {
        return foodService.getFoods();
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/order")
    public ResponseEntity<Map<String, String>> orderFood(@RequestBody Map<String, String> orderFood) {
        Map<String, String> response = new HashMap<>();
        String item = String.valueOf(orderFood.get("item"));
        int quantity = Integer.parseInt(orderFood.get("quantity"));
        try {
            foodService.updateFood(item, quantity);
            response.put("message", "Orden realizada: " + quantity + " " + item + "(s)");
            return ResponseEntity.status(200).body(response);
        } catch (StockUnavailableException e) {
            response.put("message", "No hay suficiente stock disponible.");
            return ResponseEntity.status(409).body(response);
        }
    }
}
