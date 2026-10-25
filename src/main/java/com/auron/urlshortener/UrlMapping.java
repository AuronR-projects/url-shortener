package com.auron.urlshortener;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity 
public class UrlMapping {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
