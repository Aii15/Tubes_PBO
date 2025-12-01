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

public class PastryController extends BaseProductController {

    @FXML
    private FlowPane productFlow;

    @FXML
    private TextField searchField;


    @FXML
    private Button cartButton;


    private List<Product> products = new ArrayList<>();

    @FXML
    public void initialize() {
        // pastry product list (name, price)
        products.add(new Product("Butter Croissant", 2.50));
        products.add(new Product("Almond Croissant", 3.20));
        products.add(new Product("Pain au Chocolat", 3.00));
        products.add(new Product("Cinnamon Roll", 2.80));
        products.add(new Product("Blueberry Muffin", 2.40));
        products.add(new Product("Chocolate Croissant", 3.10));
        products.add(new Product("Danish Pastry", 2.90));
        products.add(new Product("Scone", 2.20));
        products.add(new Product("Banana Bread", 2.60));
        products.add(new Product("Lemon Tart", 3.50));
        products.add(new Product("Macaron", 2.99));
        products.add(new Product("Éclair", 3.40));
        products.add(new Product("Fruit Tart", 3.75));
        products.add(new Product("Cheese Danish", 3.15));
        products.add(new Product("Butter Cookie", 1.80));

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
            case "Butter Croissant": return "/media/pastry/butter_croissant.jpg";
            case "Almond Croissant": return "/media/pastry/almond_croissant.jpg";
            case "Pain au Chocolat": return "/media/pastry/Pain_au_Chocolat.jpg";
            case "Cinnamon Roll": return "/media/pastry/cinnamon_roll.jpg";
            case "Blueberry Muffin": return "/media/pastry/blueberry_muffin.jpg";
            case "Chocolate Croissant": return "/media/pastry/chocolate_croissant.jpg";
            case "Danish Pastry": return "/media/pastry/danish_pastry.jpg";
            case "Scone": return "/media/pastry/scone.jpg";
            case "Banana Bread": return "/media/pastry/banana_bread.jpg";
            case "Lemon Tart": return "/media/pastry/lemon_tart.jpg";
            case "Macaron": return "/media/pastry/macaron.jpg";
            case "Éclair": return "/media/pastry/Éclair.jpg";
            case "Fruit Tart": return "/media/pastry/fruit_tart.jpg";
            case "Cheese Danish": return "/media/pastry/chesee_danish.jpg";
            case "Butter Cookie": return "/media/pastry/butter_cookie.jpg";
            default: return null;
        }
    }
}
