package praktikum.tests.stellarburgers.model;

import java.util.List;

public class OrderRequest {
    public List<String> ingredients;

    public OrderRequest(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public static OrderRequest of(List<String> ingredients) {
        return new OrderRequest(ingredients);
    }
}
