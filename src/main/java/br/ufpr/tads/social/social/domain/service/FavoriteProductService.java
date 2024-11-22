package br.ufpr.tads.social.social.domain.service;

import br.ufpr.tads.social.social.domain.exception.BusinessException;
import br.ufpr.tads.social.social.domain.model.CustomerProfile;
import br.ufpr.tads.social.social.domain.model.FavoriteProducts;
import br.ufpr.tads.social.social.domain.repository.CustomerProfileRepository;
import br.ufpr.tads.social.social.domain.repository.FavoriteProductsRepository;
import br.ufpr.tads.social.social.dto.commons.ProductDTO;
import br.ufpr.tads.social.social.infrastructure.adapter.CatalogClientImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class FavoriteProductService {

    @Autowired
    private FavoriteProductsRepository favoriteProductsRepository;

    @Autowired
    private CustomerProfileRepository customerProfileRepository;

    @Autowired
    private CatalogClientImpl productClient;

    public void addFavorite(UUID keycloakId, UUID productId) throws BusinessException {
        Optional<FavoriteProducts> favoriteProductsOptional = favoriteProductsRepository.findByCustomerProfileKeycloakId(keycloakId);

        if (favoriteProductsOptional.isPresent()) {
            favoriteProductsOptional.get().getProductIds().add(productId);
            favoriteProductsRepository.save(favoriteProductsOptional.get());
            return;
        }

        Optional<CustomerProfile> customerProfile = customerProfileRepository.findByKeycloakId(keycloakId);
        if (customerProfile.isEmpty()) {
            throw new BusinessException("Customer not found");
        }
        FavoriteProducts favoriteProducts = new FavoriteProducts();
        favoriteProducts.setProductIds(List.of(productId));
        favoriteProducts.setCustomerProfile(customerProfile.get());
        favoriteProductsRepository.save(favoriteProducts);
    }

    public void removeFavorite(UUID keycloakId, UUID productId) {
        Optional<FavoriteProducts> favoriteProductsOptional = favoriteProductsRepository.findByCustomerProfileKeycloakId(keycloakId);

        if (favoriteProductsOptional.isPresent()) {
            favoriteProductsOptional.get().getProductIds().remove(productId);
            favoriteProductsRepository.save(favoriteProductsOptional.get());
        }
    }

    public Slice<ProductDTO> getFavorites(UUID keycloakId, Pageable pageable) {
        Optional<FavoriteProducts> favoriteProducts = favoriteProductsRepository.findByCustomerProfileKeycloakId(keycloakId);

        if (favoriteProducts.isPresent() && !favoriteProducts.get().getProductIds().isEmpty()) {
            return productClient.fetchProductsDetails(favoriteProducts.get().getProductIds(), pageable);
        }
        return new SliceImpl<>(Collections.emptyList(), pageable, false);
    }

}
