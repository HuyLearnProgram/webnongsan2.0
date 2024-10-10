package com.app.webnongsan.service;

import com.app.webnongsan.domain.Product;
import com.app.webnongsan.domain.User;
import com.app.webnongsan.domain.response.PaginationDTO;
import com.app.webnongsan.domain.response.product.ResProductDTO;
import com.app.webnongsan.repository.CategoryRepository;
import com.app.webnongsan.repository.ProductRepository;
import com.app.webnongsan.util.PaginationHelper;
import com.app.webnongsan.util.exception.ResourceInvalidException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ProductService{
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public boolean checkValidCategoryId(long categoryId){
        return this.categoryRepository.existsById(categoryId);
    }

    public Product create(Product p){
        return this.productRepository.save(p);
    }


    public boolean checkValidProductId(long id) {
        return this.productRepository.existsById(id);
    }

    public Product get(long id) {
        return this.productRepository.findById(id).orElse(null);
    }

    public void delete(long id){
        this.productRepository.deleteById(id);
    }

    public PaginationDTO getAll(Specification<Product> spec, Pageable pageable){
        Page<Product> productPage = this.productRepository.findAll(spec, pageable);

        PaginationDTO p = new PaginationDTO();
        PaginationDTO.Meta meta = new PaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(productPage.getTotalPages());
        meta.setTotal(productPage.getTotalElements());

        p.setMeta(meta);

        List<ResProductDTO> listProducts = productPage.getContent().stream().map(this::convertToProductDTO).toList();
        p.setResult(listProducts);
        return p;
    }

    public ResProductDTO convertToProductDTO(Product p) {
        ResProductDTO res = new ResProductDTO();
        res.setId(p.getId());
        res.setProduct_name(p.getProduct_name());
        res.setCategory(p.getCategory().getName());
        res.setPrice(p.getPrice());
        res.setSold(p.getSold());
        res.setQuantity(p.getQuantity());
        res.setImageUrl(p.getImageUrl());
        res.setUnit(p.getUnit());
        res.setDescription(p.getDescription());
        res.setRating(p.getRating());
        return res;
    }

    public Product findById(long id){
        return this.productRepository.findById(id).orElse(null);
    }

    public Product update(Product p){
        Product curr = this.findById(p.getId());
        if (curr != null){
            curr.setProduct_name(p.getProduct_name());
            curr.setPrice(p.getPrice());
            curr.setImageUrl(p.getImageUrl());
            curr.setDescription(p.getDescription());
            curr.setQuantity(p.getQuantity());
            curr.setUnit(p.getUnit());
        }
        assert curr != null;
        return this.productRepository.save(curr);
    }

    public double getMaxPrice(String category, String productName) throws ResourceInvalidException {
        if (category != null && !category.isEmpty() && !this.categoryRepository.existsByName(category)) {
            throw new ResourceInvalidException("Category không tồn tại");
        }

        return this.productRepository.getMaxPriceByCategoryAndProductName(category, productName);
    }


}
