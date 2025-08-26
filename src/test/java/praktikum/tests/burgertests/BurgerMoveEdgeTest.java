package praktikum.tests.burgertests;

import org.junit.*;
import praktikum.*;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

public class BurgerMoveEdgeTest {

    @Test
    public void move_fromStartToEnd_and_backTest() {
        Burger burger = new Burger();
        burger.setBuns(new Bun("Булка", 1f));

        Ingredient i0 = new Ingredient(IngredientType.SAUCE, "S0", 1f);
        Ingredient i1 = new Ingredient(IngredientType.FILLING, "F1", 1f);
        Ingredient i2 = new Ingredient(IngredientType.SAUCE, "S2", 1f);

        burger.addIngredient(i0);
        burger.addIngredient(i1);
        burger.addIngredient(i2);

        // 0,1,2 -> перемещаем 0 в конец: 1,2,0
        burger.moveIngredient(0, 2);
        assertThat(burger.ingredients, contains(i1, i2, i0));

        // 1,2,0 -> перемещаем 2 в начало: 0,1,2
        burger.moveIngredient(2, 0);
        assertThat(burger.ingredients, contains(i0, i1, i2));
    }
}
