package uk.co.clarity.techtest.integration;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.clarity.techtest.dao.MetricDao;
import uk.co.clarity.techtest.model.Metric;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class MetricControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private MetricDao metricRepository;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        metricRepository.deleteAll();
    }

    @Test
    public void testCreateMetric() {
        Metric metric = new Metric();
        metric.setSystem("Test System");
        metric.setName("Test Name");

        given()
                .contentType(ContentType.JSON)
                .body(metric)
                .when()
                .post("/metrics")
                .then()
                .statusCode(201)
                .body("system", equalTo("Test System"))
                .body("name", equalTo("Test Name"))
                .body("value", equalTo(1));
    }

    @Test
    public void testGetMetricById() {
    }

    @Test
    public void testUpdateMetric() {
    }
}
