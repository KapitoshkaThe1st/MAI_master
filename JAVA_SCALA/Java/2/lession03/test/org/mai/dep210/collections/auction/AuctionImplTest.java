package org.mai.dep210.collections.auction;
import org.hamcrest.Matcher;
import org.junit.*;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.*;

import org.hamcrest.MatcherAssert;
import static org.hamcrest.CoreMatchers.*;

public class AuctionImplTest {

    Auction auction;
    String product4thEdition = "Thinking Java 4th edition";
    String product3rdEdition = "Thinking Java 3rd edition";

    String user1 = "user1";
    String user2 = "user2";

    BigDecimal defaultPrice = BigDecimal.TEN;

    /*Вызыватется при инициализации класса AuctionImplTest*/
    @BeforeClass
    public static void setupClass(){

    }

    /*Вызыватется перед вызовом каждого метода помеченного аннотацией @Test*/
    @Before
    public void setup(){
        auction = new AuctionImpl();
        auction.placeProduct(product3rdEdition, defaultPrice);
        auction.placeProduct(product4thEdition, defaultPrice);
    }

    /*Вызыватется после вызова каждого метода помеченного аннотацией @Test*/
    @After
    public void clear() { auction = null; }

    /*Вызывается после вызова всех тестовых методов*/
    @AfterClass
    public static void releaseRecources() {

    }

    @Test(expected = ProductNotFoundException.class)
    public void placeProduct() throws Exception {
        // получаем список продуктыв на аукционе
        List<String> products = auction.getProducts();
        // проверяем содержатся ли в списке добавленные продукты, должны содержаться
        MatcherAssert.assertThat(products, hasItems(product3rdEdition, product4thEdition));

        String product5thEdition = "Thinking Java 5th edition";
        // и не должно содержать не добавленных туда продуктов
        MatcherAssert.assertThat(products, not(hasItem(product5thEdition)));

        // продукты только что добавлены, цены должны быть равны начальной ставке
        assertEquals(auction.getProductPrice(product3rdEdition), defaultPrice);

        // получение цены на несуществующий продукт должно бросить ProductNotFoundException
        BigDecimal notExistingproductPrice = auction.getProductPrice(product5thEdition);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addBid() throws Exception {

        // добавляем одну ставку с ценой большей начальной
        auction.addBid(user1, product3rdEdition, new BigDecimal(11));

        // цена за продукт должна стать равной только что добавленной ставке
        assertEquals(auction.getProductPrice(product3rdEdition), new BigDecimal(11));

        boolean thrown = false;
        try {
            // добавление ставки на несуществующий продукт должно бросить ProductNotFoundException
            auction.addBid(user1, "Thinking Java 5th edition", new BigDecimal(11));
        }
        catch (ProductNotFoundException ex){
            thrown = true;
        }

        assertTrue(thrown);

        thrown = false;
        try {
            // добавляем одну ставку с ценой меньшей начальной
            auction.addBid(user2, product4thEdition, new BigDecimal(8));
        }
        catch (IllegalArgumentException ex){
            thrown = true;
        }

        assertTrue(thrown);

        // попытка добавления ставки от null-юзера, который зарезервирован
        // под начальную ставку аукциона, должна бросать IllegalArgumentException
        auction.addBid(null, product3rdEdition, new BigDecimal(10));
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeBid() throws Exception {

        // добавляем одну ставку с ценой большей начальной, получается начальная ставка, а за ней эта
        auction.addBid(user1, product3rdEdition, new BigDecimal(11));

        // получаем цену только что добавленной ставки
        BigDecimal curPrice = auction.getProductPrice(product3rdEdition);

        // удаляем ставку, должна остаться начальная ставка, цена которой меньше, чем у удаленной
        auction.removeBid(user1, product3rdEdition);

        // получаем цену оставшейся начальной ставки
        BigDecimal newPrice = auction.getProductPrice(product3rdEdition);

        // цена удаленной ставки должна быть больше
        assertTrue(curPrice.compareTo(newPrice) > 0);

        boolean thrown = false;
        try {
            // этот пользователь удалил ставку, второй раз нельзя, должно бросить BidNotFoundException
            auction.removeBid(user1, product3rdEdition);
        }
        catch (BidNotFoundException ex){
            thrown = true;
        }

        assertTrue(thrown);

        // попытка удаления ставки от null-юзера, который зарезервирован
        // под начальную ставку аукциона, должна бросать IllegalArgumentException
        auction.removeBid(null, product3rdEdition);
    }

    @Test
    public void sellProduct() throws Exception {
        auction.addBid(user1, product3rdEdition, new BigDecimal(100));
        assertTrue(auction.sellProduct(product3rdEdition));

        assertFalse(auction.sellProduct(product4thEdition));

    }

    @Test
    public void getProducts() {
        List<String> products = auction.getProducts();
        // проверка на равенство списков действительно добавленных продуктов и добавляемых
        // проверяем есть ли все продукты, которые должны быть
        MatcherAssert.assertThat(products, hasItems(product3rdEdition, product3rdEdition));
        // проверяем что только они и есть
        assertTrue(products.size() == 2);
        // (я так понял в актуальной версии Hamcrest'а это делается в одно действие contains'ом, тут же пришлось изобрести)

    }

    @Test
    public void getProductPrice() {

        // добавляем одну ставку с ценой большей начальной
        auction.addBid(user1, product3rdEdition, new BigDecimal(11));

        BigDecimal newPrice = auction.getProductPrice(product3rdEdition);

        // цена за продукт должна стать равной только что добавленной ставке
        assertEquals(newPrice, new BigDecimal(11));

        // добавляем одну ставку с ценой большей начальной на другой продукт
        auction.addBid(user1, product4thEdition, new BigDecimal(112));

        newPrice = auction.getProductPrice(product4thEdition);

        // цена за продукт должна стать равной только что добавленной ставке
        assertEquals(newPrice, new BigDecimal(112));

        // проверка не повлияло ли на другой продукт
        assertNotEquals(newPrice, auction.getProductPrice(product3rdEdition));
    }
}
