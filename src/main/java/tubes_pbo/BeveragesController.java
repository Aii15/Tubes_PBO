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
public class BeveragesController extends BaseProductController {

    @FXML
    private FlowPane productFlow;

    @FXML
    private TextField searchField;


    @FXML
    private Button cartButton;


    private List<Product> products = new ArrayList<>();

    @FXML
    public void initialize() {
        // beverages product list (name, price)
        products.add(new Product("Iced Tea", 2.99));
        products.add(new Product("Lemonade", 2.79));
        products.add(new Product("Hot Chocolate", 3.49));
        products.add(new Product("Iced Matcha", 4.99));
        products.add(new Product("Strawberry Smoothie", 4.50));
        products.add(new Product("Mango Smoothie", 4.50));
        products.add(new Product("Green Tea", 2.50));
        products.add(new Product("Black Tea", 2.50));
        products.add(new Product("Chai Tea", 3.25));
        products.add(new Product("Herbal Infusion", 3.00));
        products.add(new Product("Mixed Fruit Juice", 3.75));
        products.add(new Product("Iced Chocolate", 3.95));
        products.add(new Product("Fruit Punch", 3.20));
        products.add(new Product("Mineral Water", 4.20));
        products.add(new Product("Milkshake", 5.50));

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

    @Override
    protected String imageFileFor(String productName) {
        switch (productName) {
            case "Iced Tea": return "/media/beverages/iced_tea.jpg";
            case "Lemonade": return "/media/beverages/lemonade.jpg";
            case "Hot Chocolate": return "/media/beverages/hot_chocolate.jpg";
            case "Iced Matcha": return "/media/beverages/iced_matcha.jpg";
            case "Strawberry Smoothie": return "/media/beverages/stawberry_smoothie.jpg";
            case "Mango Smoothie": return "/media/beverages/mango_smoothie.jpg";
            case "Green Tea": return "/media/beverages/green_tea.jpg";
            case "Black Tea": return "/media/beverages/black_tea.jpg";
            case "Chai Tea": return "/media/beverages/chai_tea.jpg";
            case "Herbal Infusion": return "/media/beverages/herbal_infusion.jpg";
            case "Mixed Fruit Juice": return "/media/beverages/mixed_fruite_juice.jpg";
            case "Iced Chocolate": return "/media/beverages/iced_chocolate.jpg";
            case "Fruit Punch": return "/media/beverages/fruit_punch.jpg";
            case "Mineral Water": return "/media/beverages/mineral_water.jpg";
            case "Milkshake": return "/media/beverages/milkshake.jpg";
            default: return null;
        }
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

    
}
