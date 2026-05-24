package com.yashrajput.shieldgate.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vendors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vendor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false, unique = true)
    private String gstNumber;

    @Column(nullable = false)
    private String address;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}