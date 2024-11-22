package br.ufpr.tads.social.social.domain.service;

import br.ufpr.tads.social.social.domain.model.CustomerProfile;
import br.ufpr.tads.social.social.domain.model.ShoppingList;
import br.ufpr.tads.social.social.domain.repository.CustomerProfileRepository;
import br.ufpr.tads.social.social.domain.repository.ShoppingListRepository;
import br.ufpr.tads.social.social.dto.commons.ProductDTO;
import br.ufpr.tads.social.social.dto.commons.ProductItemRequestDTO;
import br.ufpr.tads.social.social.dto.request.ProductsPriceRequestDTO;
import br.ufpr.tads.social.social.dto.request.ShoppingListRequestDTO;
import br.ufpr.tads.social.social.dto.response.ProductsPriceResponseDTO;
import br.ufpr.tads.social.social.infrastructure.adapter.CatalogClientImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ShoppingListService {

    public static final String DEFAULT_LIST_NAME = "Minha Lista";
    @Autowired
    private CatalogClientImpl catalogClient;

    @Autowired
    private ShoppingListRepository shoppingListRepository;

    @Autowired
    private CustomerProfileRepository customerProfileRepository;

    public SliceImpl<ProductDTO> getOrCreateShoppingList(UUID keycloakId) {
        ShoppingList shoppingList = getShoppingList(keycloakId).orElse(createEmptyShoppingList(keycloakId));

        if (shoppingList.getProductList().isEmpty()) {
            return new SliceImpl<>(List.of(), PageRequest.of(0, 1), false);
        }

        Map<UUID, Integer> productQuantities = shoppingList.getProductList().stream()
                .collect(Collectors.toMap(ShoppingList.ProductItem::getProductId, ShoppingList.ProductItem::getQuantity));

        List<UUID> productIds = new ArrayList<>(productQuantities.keySet());

        Slice<ProductDTO> productDetailsSlice = catalogClient.fetchProductsDetails(productIds, PageRequest.of(0, productIds.size()));

        List<ProductDTO> enrichedProducts = productDetailsSlice.getContent().stream()
                .map(product -> {
                    product.setQuantity(productQuantities.getOrDefault(UUID.fromString(product.getId()), 0));
                    return product;
                })
                .toList();

        return new SliceImpl<>(enrichedProducts, productDetailsSlice.getPageable(), productDetailsSlice.hasNext());
    }


    public void addProductsInShoppingList(UUID userId, ShoppingListRequestDTO shoppingListRequestDTO) {
        ShoppingList shoppingList = getOrCreateShoppingListEntity(userId);

        shoppingListRequestDTO.getProducts().forEach(productDTO -> {
            shoppingList.getProductList().stream()
                    .filter(product -> product.getProductId().equals(productDTO.getProductId()))
                    .findFirst()
                    .ifPresentOrElse(
                            product -> product.setQuantity(product.getQuantity() + productDTO.getQuantity()),
                            () -> shoppingList.getProductList().add(new ShoppingList.ProductItem(productDTO.getProductId(), productDTO.getQuantity()))
                    );
        });

        shoppingListRepository.save(shoppingList);
    }

    public void removeProductsInShoppingList(UUID userId, ShoppingListRequestDTO shoppingListRequestDTO) {
        ShoppingList shoppingList = getShoppingList(userId)
                .orElseThrow(() -> new RuntimeException("Shopping list not found for user with keycloakId: " + userId));

        shoppingListRequestDTO.getProducts().forEach(productDTO -> {
            shoppingList.getProductList().stream()
                    .filter(product -> product.getProductId().equals(productDTO.getProductId()))
                    .findFirst()
                    .ifPresent(product -> {
                        int updatedQuantity = product.getQuantity() - productDTO.getQuantity();
                        if (updatedQuantity > 0) {
                            product.setQuantity(updatedQuantity);
                        } else {
                            shoppingList.getProductList().remove(product);
                        }
                    });
        });

        shoppingListRepository.save(shoppingList);
    }


    public SliceImpl<ProductsPriceResponseDTO> calculateShoppingList(UUID keycloakId, String cep, double distance) {
        ShoppingList shoppingList = getShoppingList(keycloakId)
                .orElseThrow(() -> new RuntimeException("Shopping list not found for user with keycloakId: " + keycloakId));

        List<ProductItemRequestDTO> products = shoppingList.getProductList().stream()
                .map(productItem -> new ProductItemRequestDTO(productItem.getProductId(), productItem.getQuantity()))
                .toList();

        Pageable pageable = Pageable.ofSize(products.size());
        ProductsPriceRequestDTO productsPriceRequestDTO = new ProductsPriceRequestDTO(products, cep, distance);
        return catalogClient.fetchProductsPriceByStore(productsPriceRequestDTO, pageable);
    }

    private ShoppingList getOrCreateShoppingListEntity(UUID keycloakId) {
        return getShoppingList(keycloakId).orElse(createEmptyShoppingList(keycloakId));
    }

    private Optional<ShoppingList> getShoppingList(UUID keycloakId) {
        return shoppingListRepository.findByCustomerProfileKeycloakId(keycloakId).stream().findFirst();
    }

    private ShoppingList createEmptyShoppingList(UUID userId) {
        CustomerProfile customerProfile = customerProfileRepository.findByKeycloakId(userId)
                .orElseThrow(() -> new RuntimeException("Customer profile not found for user ID: " + userId));

        ShoppingList newShoppingList = new ShoppingList();
        newShoppingList.setCustomerProfile(customerProfile);
        newShoppingList.setName(DEFAULT_LIST_NAME);
        newShoppingList.setProductList(new ArrayList<>());
        newShoppingList.setCreatedAt(LocalDateTime.now());

        ShoppingList shoppingListSaved = shoppingListRepository.save(newShoppingList);
        customerProfile.getShoppingLists().add(newShoppingList);
        customerProfileRepository.save(customerProfile);

        return shoppingListSaved;
    }
}
