package com.utn.UDEE.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity(name = "rates")
public class Rate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rateId",unique = true,nullable = false)
    private Integer id;

    @Column (name = "value")
    private Double value;

    @Column (name = "type_rate")
    private String typeRate;

    @OneToMany(mappedBy = "rate", cascade = CascadeType.ALL)
    private List<Address> addressList;
}
