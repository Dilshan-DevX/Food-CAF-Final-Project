package com.codex.foodcaf.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    private String productId;
    private String categoryId;
    private String foodRating;
    private String foodTitle;
    private double productPrice;
    private List<String> productImage;
    private String foodTime;
    private String foodDetail;
    private boolean availability;

}
