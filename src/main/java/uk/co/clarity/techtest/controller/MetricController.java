package uk.co.clarity.techtest.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.co.clarity.techtest.model.Metric;
import uk.co.clarity.techtest.dao.MetricDao;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping()
public class MetricController {

    private static final int MILLISECOND_TO_SECOND = 1000;
    private static final Logger LOGGER = LoggerFactory.getLogger(MetricController.class);
    private final MetricDao metricDao;

    @Autowired
    public MetricController(MetricDao metricDao) {
        this.metricDao = metricDao;
    }

    //TODO: Split out into separate class
    @GetMapping("metricsummary")
    public ResponseEntity<Integer> getMetricSummary(@RequestParam(required = true) String system,
                                                   @RequestParam(required = false) String name,
                                                   @RequestParam(required = false) Long from,
                                                   @RequestParam(required = false) Long to) {
        List<Metric> metrics = getMetricsFromDb(system, name, from, to);
        int value = 0;
        for(Metric metric : metrics) {
            value += metric.getValue();
        }
        return ResponseEntity.ok(value);
    }

    @GetMapping("metrics")
    public ResponseEntity<List<Metric>> getMetrics(@RequestParam(required = true) String system,
                                                   @RequestParam(required = false) String name,
                                                   @RequestParam(required = false) Long from,
                                                   @RequestParam(required = false) Long to) {
        return ResponseEntity.ok(getMetricsFromDb(system, name, from, to));
    }


    @GetMapping("metrics/{id}")
    public ResponseEntity<Metric> getMetricById(@PathVariable int id) {
        Optional<Metric> metric = metricDao.findById(id);
        return metric.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("metrics")
    public ResponseEntity<Metric> createMetric(@RequestBody Metric metric) {
        try {
            if (metric.getDate() == null) {
                // Set current Unix time
                metric.setDate(System.currentTimeMillis() / MILLISECOND_TO_SECOND);
            }
            if (metric.getValue() == null) metric.setValue(1);

            Metric savedMetric = metricDao.save(metric);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedMetric);
        }
        //TODO: Need to add a proper constraints check to stop Postgres throwing constraints violations when system, name and date combined constraint isn't unique.
        // Catching the resulting violation from Postgres definitely isn't the correct way to enforce this. (And neither is swallowing the exception).
        catch(DataIntegrityViolationException exception) {
            LOGGER.info("Exception thrown: {0}", exception);
            return ResponseEntity.badRequest().build();
        }

    }

    @PutMapping("metrics/{id}")
    public ResponseEntity<Metric> updateMetric(@PathVariable int id, @RequestBody Metric updatedMetric) {
        Optional<Metric> existingMetricOptional = metricDao.findById(id);
        if (!existingMetricOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Metric existingMetric = existingMetricOptional.get();
        //Check if system, name and date match as per spec
        if(!existingMetric.getSystem().equals(updatedMetric.getSystem()) ||
                !existingMetric.getName().equals(updatedMetric.getName()) ||
                !existingMetric.getDate().equals(updatedMetric.getDate())) {
            return ResponseEntity.badRequest().build();
        }

        existingMetric.setSystem(updatedMetric.getSystem());
        existingMetric.setName(updatedMetric.getName());
        existingMetric.setDate(updatedMetric.getDate());
        if(updatedMetric.getValue() != null) {
            existingMetric.setValue(updatedMetric.getValue());
        }
        else {
            //Increment value by 1 if not set
            existingMetric.setValue(existingMetric.getValue()+1);
        }
        metricDao.save(existingMetric);
        return ResponseEntity.ok(existingMetric);
    }

    private List<Metric> getMetricsFromDb(String system, String name, Long from, Long to) {
        List<Metric> metrics;
        if(from == null) from = Long.MIN_VALUE;
        if(to == null) to = Long.MAX_VALUE;
        // Perform filtering based on the parameters
        if(name == null) {
            metrics = metricDao.findMetric(system, from, to);
        }
        else {
            metrics = metricDao.findMetric(system, name, from, to);
        }
        return metrics;
    }
}
