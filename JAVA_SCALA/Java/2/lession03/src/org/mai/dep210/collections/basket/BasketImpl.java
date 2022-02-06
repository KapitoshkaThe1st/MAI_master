package org.mai.dep210.collections.basket;

import org.mai.dep210.collections.auction.DuplicateProductException;
import org.mai.dep210.collections.auction.ProductNotFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BasketImpl implements Basket {

    private final HashMap<String, Integer> products = new HashMap<String, Integer>();

    @Override
    public void addProduct(String product, int quantity) {
        if(products.containsKey(product))
            throw new DuplicateProductException("Product is already presented in the basket");

        products.put(product, quantity);
    }

    private void checkProductPresented(String product){
        if(!products.containsKey(product))
            throw new ProductNotFoundException("Product is not presented in the basket");
    }

    @Override
    public void removeProduct(String product) {
        checkProductPresented(product);
        products.remove(product);
    }

    @Override
    public void updateProductQuantity(String product, int quantity) {
        checkProductPresented(product);
        products.replace(product, quantity);
    }

    @Override
    public List<String> getProducts() {
        return products.entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toList());
    }
}
