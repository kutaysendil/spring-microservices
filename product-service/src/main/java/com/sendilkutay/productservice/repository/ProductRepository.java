package com.sendilkutay.productservice.repository;

import com.sendilkutay.productservice.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product,String> {
}
