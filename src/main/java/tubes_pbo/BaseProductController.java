package tubes_pbo;

import java.io.InputStream;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Base controller providing shared product card rendering and order popup.
 * Subclasses must implement {@link #imageFileFor(String)} to return a full
 * resource path (e.g. "/media/beverages/iced_tea.jpg") or null if none.
 */
public abstract class BaseProductController {

    @FXML
    protected FlowPane productFlow;

    @FXML
    protected TextField searchField;

    @FXML
    protected Button cartButton;

    protected void renderProducts(List<Product> list) {
        if (productFlow == null) return;
        productFlow.getChildren().clear();
        for (Product p : list) {
            productFlow.getChildren().add(createCard(p));
        }
    }

    protected VBox createCard(Product p) {
        VBox card = new VBox(6);
        card.getStyleClass().add("product-card");

        ImageView imgView = null;
        String resourcePath = imageFileFor(p.getName());
        if (resourcePath != null) {
            InputStream is = getClass().getResourceAsStream(resourcePath);
            if (is != null) {
                Image img = new Image(is, 500, 380, true, true);
                imgView = new ImageView(img);
                imgView.setFitWidth(500);
                imgView.setFitHeight(380);
                imgView.setPreserveRatio(true);
                Rectangle clip = new Rectangle(500, 380);
                clip.setArcWidth(6);
                clip.setArcHeight(6);
                imgView.setClip(clip);
            }
        }
        HBox imgWrap = new HBox();
        imgWrap.setAlignment(Pos.CENTER);
        if (imgView != null) imgWrap.getChildren().add(imgView);
        else {
            Rectangle img = new Rectangle(500, 380, Color.web("#e6d7ca"));
            img.setArcWidth(6);
            img.setArcHeight(6);
            imgWrap.getChildren().add(img);
        }

        Label title = new Label(p.getName());
        title.getStyleClass().add("product-title");

        Label desc = new Label("Hot Drinks");
        desc.setWrapText(true);

        Label price = new Label(String.format("$%.2f", p.getPrice()));
        price.getStyleClass().add("price-label");

        Button add = new Button("Add to Cart");
        add.getStyleClass().add("add-button");
        add.setOnAction(e -> CartManager.getInstance().addItem(p.getName(), p.getPrice()));

        card.getChildren().addAll(imgWrap, title, desc, price, add);
        card.setOnMouseClicked(ev -> openOrder(p));
        return card;
    }

    protected void openOrder(Product p) {
        Stage st = new Stage();
        st.initModality(Modality.APPLICATION_MODAL);
        st.setTitle(p.getName());

        VBox root = new VBox(10);
        root.setPadding(new javafx.geometry.Insets(12));

        Label title = new Label(p.getName());
        title.setStyle("-fx-font-size:18px; -fx-font-weight:bold;");
        Label desc = new Label("Delicious " + p.getName() + " — fresh and ready.");
        desc.setWrapText(true);

        HBox qtyBox = new HBox(8);
        Button minus = new Button("-");
        Label qtyLabel = new Label("1");
        Button plus = new Button("+");
        Label priceLabel = new Label(String.format("$%.2f", p.getPrice()));
        qtyBox.getChildren().addAll(minus, qtyLabel, plus, priceLabel);

        final int[] qty = {1};
        minus.setOnAction(e -> {
            if (qty[0] > 1) qty[0]--;
            qtyLabel.setText(String.valueOf(qty[0]));
            priceLabel.setText(String.format("$%.2f", p.getPrice() * qty[0]));
        });
        plus.setOnAction(e -> {
            qty[0]++;
            qtyLabel.setText(String.valueOf(qty[0]));
            priceLabel.setText(String.format("$%.2f", p.getPrice() * qty[0]));
        });

        HBox actions = new HBox(8);
        Button addBtn = new Button("Add to Cart");
        actions.getChildren().addAll(addBtn);

        addBtn.setOnAction(ev -> {
            CartManager.getInstance().addItem(p.getName(), p.getPrice(), qty[0]);
            st.close();
        });

        root.getChildren().addAll(title, desc, qtyBox, actions);

        st.setScene(new Scene(root));
        st.showAndWait();
    }

    /**
     * Return a resource path for the given product name, or null.
     * Subclasses should return a path like "/media/beverages/iced_tea.jpg".
     */
    protected abstract String imageFileFor(String productName);
}
