package tubes_pbo;

import java.io.IOException;
import java.util.function.Consumer;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;

public class OrderController {

    @FXML
    private Label titleLabel;
    @FXML
    private Label subtitleLabel;
    @FXML
    private Label descLabel;
    @FXML
    private ToggleButton sizeSmall;
    @FXML
    private ToggleButton sizeMedium;
    @FXML
    private ToggleButton sizeLarge;
    @FXML
    private Button minusBtn;
    @FXML
    private Button plusBtn;
    @FXML
    private Label qtyLabel;
    @FXML
    private Label priceLabel;
    @FXML
    private Button addToCartBtn;
    @FXML
    private Button buyNowBtn;

    private String name;
    private double price;
    private int qty = 1;

    private Consumer<ProductData> onAdd;

    public static class ProductData {
        public final String name;
        public final double price;
        public final int qty;

        public ProductData(String name, double price, int qty) {
            this.name = name;
            this.price = price;
            this.qty = qty;
        }
    }

    @FXML
    private void initialize() {
        if (qtyLabel != null) qtyLabel.setText(String.valueOf(qty));

        if (minusBtn != null) minusBtn.setOnAction(e -> {
            if (qty > 1) qty--;
            qtyLabel.setText(String.valueOf(qty));
            updatePriceLabel();
        });

        if (plusBtn != null) plusBtn.setOnAction(e -> {
            qty++;
            qtyLabel.setText(String.valueOf(qty));
            updatePriceLabel();
        });

        if (addToCartBtn != null) addToCartBtn.setOnAction(e -> {
            System.out.println("OrderController: Add to Cart clicked -> " + name + " x" + qty + " @ " + price);
            if (onAdd != null) onAdd.accept(new ProductData(name, price, qty));
            closeWindow();
        });

        if (buyNowBtn != null) buyNowBtn.setOnAction(e -> {
            // For now, buy now just closes window. Could extend to payment flow.
            closeWindow();
        });
    }

    private void updatePriceLabel() {
        if (priceLabel != null) priceLabel.setText(String.format("$%.2f", price * qty));
    }

    public void setProduct(String name, double price) {
        this.name = name;
        this.price = price;
        if (titleLabel != null) titleLabel.setText(name);
        if (priceLabel != null) priceLabel.setText(String.format("$%.2f", price * qty));
        if (descLabel != null) descLabel.setText("Delicious " + name + " — fresh and ready.");
    }

    public void setOnAdd(Consumer<ProductData> c) {
        this.onAdd = c;
    }

    private void closeWindow() {
        Stage st = (Stage) (addToCartBtn != null ? addToCartBtn.getScene().getWindow() : null);
        if (st != null) st.close();
    }

}
