package uk.co.clarity.techtest.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.co.clarity.techtest.dao.MetricDao;
import uk.co.clarity.techtest.model.Metric;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class MetricControllerTest {

    @Mock
    private MetricDao metricDao;

    @InjectMocks
    private MetricController metricController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetMetricSummary_Success() {
        String system = "TestSystemValue";
        String name = "TestNameValue";
        Long from = null;
        Long to = null;

        Metric metric1 = new Metric();
        metric1.setValue(2);

        Metric metric2 = new Metric();
        metric2.setValue(3);

        List<Metric> metrics = Arrays.asList(metric1, metric2);

        when(metricDao.findMetric(system, name, Long.MIN_VALUE, Long.MAX_VALUE)).thenReturn(metrics);

        ResponseEntity<Integer> response = metricController.getMetricSummary(system, name, from, to);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(5, response.getBody());
        verify(metricDao, times(1)).findMetric(system, name, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    @Test
    void testGetMetrics_Success() {
        String system = "TestSystemValue";
        String name = "TestMetricValue";
        Long from = null;
        Long to = null;

        Metric metric1 = new Metric();
        metric1.setValue(2);

        Metric metric2 = new Metric();
        metric2.setValue(3);

        List<Metric> metrics = Arrays.asList(metric1, metric2);

        when(metricDao.findMetric(system, name, Long.MIN_VALUE, Long.MAX_VALUE)).thenReturn(metrics);

        ResponseEntity<List<Metric>> response = metricController.getMetrics(system, name, from, to);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(metrics, response.getBody());
        verify(metricDao, times(1)).findMetric(system, name, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    @Test
    void testGetMetricById_ExistingId_Success() {
        int id = 1;
        Metric metric = new Metric();

        when(metricDao.findById(id)).thenReturn(Optional.of(metric));

        ResponseEntity<Metric> response = metricController.getMetricById(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(metric, response.getBody());
        verify(metricDao, times(1)).findById(id);
    }

    @Test
    void testGetMetricById_NonExistingId_NotFound() {
        int id = 1;

        when(metricDao.findById(id)).thenReturn(Optional.empty());

        ResponseEntity<Metric> response = metricController.getMetricById(id);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(metricDao, times(1)).findById(id);
    }

    @Test
    void testCreateMetric_Success() {
        Metric metric = new Metric();
        metric.setSystem("TestSystemValue");
        metric.setName("TestMetricValue");

        when(metricDao.save(metric)).thenReturn(metric);

        ResponseEntity<Metric> response = metricController.createMetric(metric);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(metric, response.getBody());
        verify(metricDao, times(1)).save(metric);
    }

    @Test
    void testUpdateMetric_ExistingId_Success() {
        int id = 1;
        Metric existingMetric = new Metric();
        existingMetric.setSystem("TestSystemValue");
        existingMetric.setName("TestMetricValue");
        existingMetric.setDate(123456L);
        existingMetric.setValue(5);

        Metric updatedMetric = new Metric();
        updatedMetric.setSystem("TestSystemValue");
        updatedMetric.setName("TestMetricValue");
        updatedMetric.setDate(123456L);
        updatedMetric.setValue(10);

        when(metricDao.findById(id)).thenReturn(Optional.of(existingMetric));
        when(metricDao.save(existingMetric)).thenReturn(existingMetric);

        ResponseEntity<Metric> response = metricController.updateMetric(id, updatedMetric);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(existingMetric, response.getBody());
        assertEquals(10, existingMetric.getValue());
        verify(metricDao, times(1)).findById(id);
        verify(metricDao, times(1)).save(existingMetric);
    }

    @Test
    void testUpdateMetric_NonExistingId_NotFound() {
        int id = 1;
        Metric updatedMetric = new Metric();
        updatedMetric.setSystem("TestSystemValue");
        updatedMetric.setName("TestMetricValue");
        updatedMetric.setDate(123456L);
        updatedMetric.setValue(10);

        when(metricDao.findById(id)).thenReturn(Optional.empty());

        ResponseEntity<Metric> response = metricController.updateMetric(id, updatedMetric);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(metricDao, times(1)).findById(id);
        verify(metricDao, never()).save(any(Metric.class));
    }
}
