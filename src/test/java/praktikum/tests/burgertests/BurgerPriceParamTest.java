package praktikum.tests.burgertests;

import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;
import praktikum.*;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class BurgerPriceParamTest {

    private final float bunPrice;
    private final float[] ingredientPrices; // массив цен ингредиентов
    private final float expectedTotal;

    public BurgerPriceParamTest(float bunPrice, float[] ingredientPrices, float expectedTotal) {
        this.bunPrice = bunPrice;
        this.ingredientPrices = ingredientPrices;
        this.expectedTotal = expectedTotal;
    }

    @Parameterized.Parameters(name = "{index}: bun={0}, ingredients={1}, expected={2}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                // 2*bun + sum(ingredients)
                {100f, new float[]{}, 200f},
                {50f,  new float[]{10f}, 110f},              // 2*50 + 10 = 110
                {80f,  new float[]{10f, 20f}, 190f},         // 2*80 + 10 + 20 = 190
                {120f, new float[]{5f, 5f, 5f}, 255f},       // 2*120 + 15 = 255
        });
    }

    @Test
    public void price_isCalculatedCorrectlyTest() {
        Burger burger = new Burger();

        // bun stub
        Bun bun = Mockito.mock(Bun.class);
        when(bun.getPrice()).thenReturn(bunPrice);
        when(bun.getName()).thenReturn("Bun");
        burger.setBuns(bun);

        // ingredients stubs
        for (int i = 0; i < ingredientPrices.length; i++) {
            Ingredient ing = Mockito.mock(Ingredient.class);
            when(ing.getPrice()).thenReturn(ingredientPrices[i]);
            when(ing.getType()).thenReturn(i % 2 == 0 ? IngredientType.SAUCE : IngredientType.FILLING);
            when(ing.getName()).thenReturn("I" + i);
            burger.addIngredient(ing);
        }

        assertEquals(expectedTotal, burger.getPrice(), 0.0001);
    }
}
