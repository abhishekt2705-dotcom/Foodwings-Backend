package com.foodwings.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A food category (Pizza, Burger, Chinese, ...).
 */
@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category extends BaseEntity {

    @Column(nullable = false, unique = true, length = 60)
    private String name;

    @Column(length = 255)
    private String description;

    private String imagePath;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;
    
    // Explicit setters/getters for IDEs without Lombok
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setActive(boolean active) { this.active = active; }
    public String getName() { return this.name; }
}
