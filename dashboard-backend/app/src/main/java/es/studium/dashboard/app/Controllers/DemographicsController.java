package es.studium.dashboard.app.Controllers;

import es.studium.dashboard.app.repository.DemographicsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/demographics")
public class DemographicsController {

    @Autowired
    private DemographicsRepository demographicsRepository;

    @GetMapping("/dashboard/occupation-status-pie")
    public Map<String, Object> getOccupationStatusPie() {
        List<Object[]> result = demographicsRepository.countOccupationStatus();
        List<String> labels = new ArrayList<>();
        List<Long> counts = new ArrayList<>();
        for (Object[] row : result) {
            labels.add((String) row[0]);
            counts.add((Long) row[1]);
        }
        Map<String, Object> resp = new HashMap<>();
        resp.put("labels", labels);
        resp.put("counts", counts);
        return resp;
    }
}
