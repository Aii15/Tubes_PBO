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

public class PrimaryController extends BaseProductController {

    @FXML
    private FlowPane productFlow;

    @FXML
    private TextField searchField;


    @FXML
    private Button cartButton;


    private List<Product> products = new ArrayList<>();

    @FXML
    public void initialize() {
        // sample product list (name, price)
        products.add(new Product("Nitro Cold Brew", 65000));
        products.add(new Product("Cold Brew", 55000));
        products.add(new Product("Iced Latte", 60000));
        products.add(new Product("Caffè Mocha", 60000));
        products.add(new Product("Caramel Latte", 65000));
        products.add(new Product("Matcha Latte", 70000));
        products.add(new Product("Vanilla Latte", 63000));
        products.add(new Product("Iced Coffee", 50000));
        products.add(new Product("Cappuccino", 50000));
        products.add(new Product("Chai Latte", 60000));
        products.add(new Product("Café Latte", 55000));
        products.add(new Product("Affogato", 65000));
        products.add(new Product("Flat White", 53000));
        products.add(new Product("Doppio", 45000));
        products.add(new Product("Classic Espresso", 40000));

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

    // popup cart removed; toggleCart no longer needed

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
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

    // map product name to full resource path
    @Override
    protected String imageFileFor(String productName) {
        switch (productName) {
            case "Nitro Cold Brew": return "/media/coffe/nitro_cold_brew.jpg";
            case "Cold Brew": return "/media/coffe/cold_brew.jpg";
            case "Iced Latte": return "/media/coffe/iced_latte.jpg";
            case "Caffè Mocha": return "/media/coffe/caffe_mocha.jpg";
            case "Caramel Latte": return "/media/coffe/caramel_latte.jpg";
            case "Matcha Latte": return "/media/coffe/matcha_latte.jpg";
            case "Vanilla Latte": return "/media/coffe/vanilla_latte.jpg";
            case "Iced Coffee": return "/media/coffe/iced_coffe.jpg";
            case "Cappuccino": return "/media/coffe/cappuchino.jpg";
            case "Chai Latte": return "/media/coffe/chai_latte.jpg";
            case "Café Latte": return "/media/coffe/cafe_latte.jpg";
            case "Affogato": return "/media/coffe/affogato.jpg";
            case "Flat White": return "/media/coffe/flat_white.jpg";
            case "Doppio": return "/media/coffe/doppio.jpg";
            case "Classic Espresso": return "/media/coffe/classic_expresso.jpg";
            default: return null;
        }
    }
}
