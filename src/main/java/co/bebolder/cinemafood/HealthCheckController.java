package co.bebolder.cinemafood;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/health-check")
public class HealthCheckController {

    @Autowired
    private BuildProperties buildProperties;

    @CrossOrigin(origins = "*")
    @GetMapping()
    public ResponseEntity<Map<String, String>> getAvailableSeats() {
        Map<String, String> response = new HashMap<>();
        response.put("version", buildProperties.getVersion());
        return ResponseEntity.status(200).body(response);
    }

}
