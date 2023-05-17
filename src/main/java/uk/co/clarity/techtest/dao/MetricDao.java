package uk.co.clarity.techtest.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.co.clarity.techtest.model.Metric;

import java.util.List;

@Repository
public interface MetricDao extends JpaRepository<Metric, Integer> {

    @Query("SELECT m FROM Metric m WHERE m.system = :system AND m.name = :name AND m.date >= :from AND m.date <= :to")
    public List<Metric> findMetric(@Param("system") String system,
                                   @Param("name") String name,
                                   @Param("from") long from,
                                   @Param("to") long to);

    @Query("SELECT m FROM Metric m WHERE m.system = :system AND m.date >= :from AND m.date <= :to")
    public List<Metric> findMetric(@Param("system") String system,
                                   @Param("from") long from,
                                   @Param("to") long to);

}
