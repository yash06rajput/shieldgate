package com.yashrajput.shieldgate.entity;

import com.yashrajput.shieldgate.entity.enums.TenderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tenders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tender {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 3000)
    private String description;

    @Column(nullable = false)
    private Double budget;

    @Column(nullable = false)
    private LocalDateTime deadline;

    @Enumerated(EnumType.STRING)
    private TenderStatus status;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;
}