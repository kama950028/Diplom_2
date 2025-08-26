package praktikum.tests.burgertests;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import praktikum.Bun;
import praktikum.Burger;
import praktikum.Ingredient;
import praktikum.IngredientType;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BurgerCoreTest {

    private Burger burger;

    @Mock
    private Bun bun;

    @Mock
    private Ingredient sauce;

    @Mock
    private Ingredient filling;

    @Before
    public void setUp() {
        burger = new Burger();

        // стабы для булки
        when(bun.getName()).thenReturn("Краторная булка N200i");
        when(bun.getPrice()).thenReturn(100f);

        // стабы для ингредиентов
        when(sauce.getType()).thenReturn(IngredientType.SAUCE);
        when(sauce.getName()).thenReturn("Соус Spicy-X");
        when(sauce.getPrice()).thenReturn(10f);

        when(filling.getType()).thenReturn(IngredientType.FILLING);
        when(filling.getName()).thenReturn("Говяжий метеорит");
        when(filling.getPrice()).thenReturn(20f);

        burger.setBuns(bun);
    }

    @Test
    public void addRemoveMove_worksCorrectlyTest() {
        // add
        burger.addIngredient(sauce);
        burger.addIngredient(filling);
        assertThat(burger.ingredients, hasSize(2));
        assertThat(burger.ingredients, contains(sauce, filling));

        // move: переставим местами
        burger.moveIngredient(1, 0);
        assertThat(burger.ingredients, contains(filling, sauce));

        // remove: удалим первый
        burger.removeIngredient(0);
        assertThat(burger.ingredients, contains(sauce));
        assertThat(burger.ingredients, hasSize(1));
    }

    @Test
    public void price_usesBunTwice_andAllIngredientsTest() {
        burger.addIngredient(sauce);
        burger.addIngredient(filling);

        float price = burger.getPrice();

        // 2 * bun + sauce + filling  => 2*100 + 10 + 20 = 230
        Assert.assertEquals(230f, price, 0.0001);

        // В текущей реализации bun.getPrice() вызывается один раз и далее *2
        verify(bun, times(1)).getPrice();
        verify(sauce, times(1)).getPrice();
        verify(filling, times(1)).getPrice();
        verifyNoMoreInteractions(bun, sauce, filling);
    }

    @Test
    public void receipt_containsBunTwice_ingredients_andTotalTest() {
        burger.addIngredient(sauce);
        burger.addIngredient(filling);

        String receipt = burger.getReceipt();

        // имя булки встречается как минимум 2 раза
        Assert.assertTrue(receipt.split(bun.getName(), -1).length - 1 >= 2);

        assertThat(receipt, containsString("sauce " + sauce.getName()));
        assertThat(receipt, containsString("filling " + filling.getName()));

        // не завязываемся на локаль: достаточно проверить "Price: 230"
        assertThat(receipt, containsString("Price: 230"));
    }

}
