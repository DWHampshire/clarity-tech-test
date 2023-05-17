package uk.co.clarity.techtest.metric;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Data
@Entity
//Spec isn't clear, but have assumed system, name and date combined = unique key (not individually unique)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"system", "name", "date"}))
public class Metric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    @NotBlank(message = "System is required")
    private String system;

    @Column(nullable = false)
    @NotBlank(message = "Name is required")
    private String name;

    //This is set to long even though this doesn't match the spec - signed int will only fit date/time until *19/1/2038*
    private Long date;
    private Integer value;

}