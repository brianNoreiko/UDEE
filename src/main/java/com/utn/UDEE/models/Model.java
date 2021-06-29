package com.utn.UDEE.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "models")
@Builder

public class Model {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "modelId",unique = true,nullable = false)
    private Integer id;

    @Column(name = "name")
    private String name;

    @ManyToOne (fetch = FetchType.EAGER)
    @JoinColumn(name = "brandId",nullable = false, updatable = false)
    private Brand Brand;

    @OneToMany(mappedBy = "model", fetch = FetchType.LAZY)
    private List<Meter> meterList;
}
