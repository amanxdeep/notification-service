package com.self.notificationService.model.entity;

@Entity
@Data
public class ConfigEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String group;

    private String key;

    private String value;
}
