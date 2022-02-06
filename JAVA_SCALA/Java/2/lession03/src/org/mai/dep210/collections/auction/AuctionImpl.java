package org.mai.dep210.collections.auction;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AuctionImpl implements Auction {

    private final HashMap<String, HashMap<String, BigDecimal>> bids = new HashMap<>();

    private HashMap<String, BigDecimal> getProductBids(String product) throws ProductNotFoundException {
        HashMap<String, BigDecimal> bidsForProduct = bids.get(product);
        if(bidsForProduct == null)
            throw new ProductNotFoundException("There is no product '" + product + "' at auction");

        return bidsForProduct;
    }

    @Override
    public void placeProduct(String product, BigDecimal initialPrice) {
        HashMap<String, BigDecimal> newProductBids = new HashMap<String, BigDecimal> ();
        newProductBids.put(null, initialPrice); // первая ставка за организатором аукциона (пусть будет null вместо имени пользователя)

        if(bids.containsKey(product)){
            throw new DuplicateProductException("Product is already presented");
        }
        bids.put(product, newProductBids);
    }

    private void checkUserNull(String user){
        if(user == null)
            throw new IllegalArgumentException("User name can not be null");
    }

    @Override
    public void addBid(String user, String product, BigDecimal price) {
        checkUserNull(user);
        if(getProductPrice(product).compareTo(price) >= 0)
            throw new IllegalArgumentException("New bid price must be greater than previous");

        getProductBids(product).put(user, price);
    }

    @Override
    public void removeBid(String user, String product) {
        checkUserNull(user);
        if(getProductBids(product).remove(user) == null)
            throw new BidNotFoundException("There is no bid from user '" + user + "' on product '" + product);
    }

    private Map.Entry<String, BigDecimal> getMaxBidForProduct(String product){
        return getProductBids(product).entrySet().stream().max(Comparator.comparing(Map.Entry::getValue)).get();
    }

    @Override
    public boolean sellProduct(String product) {
        Map.Entry<String, BigDecimal> maxBidForProduct = getMaxBidForProduct(product);
        boolean sold = maxBidForProduct.getKey() != null; // максимальная ставка не за организатором аукциона, значит продано успешно

        bids.remove(product);

        return sold;
    }

    @Override
    public List<String> getProducts() {
        return bids.entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toList());
    }

    @Override
    public BigDecimal getProductPrice(String product) {
        return getMaxBidForProduct(product).getValue();
    }
}
