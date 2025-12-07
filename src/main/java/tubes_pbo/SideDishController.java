package tubes_pbo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;

public class SideDishController extends BaseProductController {

    @FXML
    private FlowPane productFlow;

    @FXML
    private TextField searchField;


    @FXML
    private Button cartButton;


    private List<Product> products = new ArrayList<>();

    @FXML
    public void initialize() {
        // side-dish product list (name, price)
        products.add(new Product("French Fries", 35000));
        products.add(new Product("Garlic Bread", 30000));
        products.add(new Product("Cheese Plate", 65000));
        products.add(new Product("Chicken Skewers", 60000));
        products.add(new Product("Potato Wedges", 37500));
        products.add(new Product("Garden Salad", 45000));
        products.add(new Product("Mini Sandwich", 50000));
        products.add(new Product("Mashed Potatoes", 42500));
        products.add(new Product("Nachos", 55000));
        products.add(new Product("Mozzarella Sticks", 50000));
        products.add(new Product("Onion Rings", 38000));
        products.add(new Product("Bruschetta", 42000));
        products.add(new Product("Sausage Roll", 36000));
        products.add(new Product("Spring Rolls", 41000));
        products.add(new Product("Coleslaw", 25000));

        // initial render
        renderProducts(products);
        if (cartButton != null) {
            cartButton.textProperty().bind(CartManager.getInstance().countProperty().asString("cart %d"));
            cartButton.setOnAction(ev -> {
                try {
                    App.setRoot("cart");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
        }

        // Let FlowPane compute wrapping based on its layout — no sidebar binding needed.
    }

    

    

    @FXML
    private void onSearch(KeyEvent ev) {
        Object src = ev.getSource();
        String q = null;
        if (src instanceof TextField) {
            q = ((TextField) src).getText();
        } else if (searchField != null) {
            q = searchField.getText();
        }
        if (q == null || q.isBlank()) {
            renderProducts(products);
            return;
        }
        final String ql = q.toLowerCase(Locale.ROOT);
        List<Product> filtered = products.stream()
            .filter(p -> p.getName().toLowerCase(Locale.ROOT).contains(ql))
            .collect(Collectors.toList());
        renderProducts(filtered);
    }

    @FXML
    private void goToCoffee() {
        try {
            App.setRoot("primary");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToPastry() {
        try {
            App.setRoot("pastry");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToBeverages() {
        try {
            App.setRoot("beverages");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToSideDish() {
        try {
            App.setRoot("sidedish");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String imageFileFor(String productName) {
        switch (productName) {
            case "French Fries": return "/media/side dish/french_fries.jpg";
            case "Garlic Bread": return "/media/side dish/garlic_bread.jpg";
            case "Cheese Plate": return "/media/side dish/cheese_plate.jpg";
            case "Chicken Skewers": return "/media/side dish/chicken_skewers.jpg";
            case "Potato Wedges": return "/media/side dish/potato_wedges.jpg";
            case "Garden Salad": return "/media/side dish/garden_salad.jpg";
            case "Mini Sandwich": return "/media/side dish/mini_sandwich.jpg";
            case "Mashed Potatoes": return "/media/side dish/mashed_potatoes.jpg";
            case "Nachos": return "/media/side dish/nachos.jpg";
            case "Mozzarella Sticks": return "/media/side dish/mozzarella_sticks.jpg";
            case "Onion Rings": return "/media/side dish/onion_rings.jpg";
            case "Bruschetta": return "/media/side dish/Bruschetta.jpg";
            case "Sausage Roll": return "/media/side dish/sausage_rolls.jpg";
            case "Spring Rolls": return "/media/side dish/spring_rolls.jpg";
            case "Coleslaw": return "/media/side dish/coleslaw.jpg";
            default: return null;
        }
    }
}
