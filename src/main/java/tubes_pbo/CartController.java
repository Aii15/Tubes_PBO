package tubes_pbo;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.InputStream;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartController {

    @FXML
    private VBox itemsBox;

    @FXML
    private Button buyNowButton;

    @FXML
    private Button backButton;

    @FXML
    public void initialize() {
        refreshItems();

        buyNowButton.setOnAction(e -> {
            List<CartManager.CartItem> items = CartManager.getInstance().getItems();
            if (items.isEmpty()) {
                Alert a = new Alert(Alert.AlertType.INFORMATION, "Your cart is empty.");
                a.showAndWait();
                return;
            }

            // Show order-input popup (customer name + items summary)
            showOrderPopup();
        });

        if (backButton != null) {
            backButton.setOnAction(ev -> {
                try {
                    App.setRoot("primary");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
        }
    }

    private void showOrderPopup() {
        List<CartManager.CartItem> items = CartManager.getInstance().getItems();
        // aggregate by name
        Map<String, ItemSummary> summary = new HashMap<>();
        for (CartManager.CartItem it : items) {
            ItemSummary s = summary.get(it.name);
            if (s == null) summary.put(it.name, new ItemSummary(it.name, it.price, 1));
            else s.count++;
        }

        Stage st = new Stage();
        st.initModality(Modality.APPLICATION_MODAL);
        st.setTitle("Input Nama Pemesanan");

        VBox root = new VBox(12);
        root.setPadding(new Insets(12));
        root.setPrefWidth(920);

        Label title = new Label("Detail Pesanan");
        title.setStyle("-fx-font-size:18px; -fx-font-weight:bold;");

        // list items in a scrollable area
        VBox listBox = new VBox(8);
        double total = 0.0;
        for (ItemSummary s : summary.values()) {
            HBox row = new HBox(8);
            row.setAlignment(Pos.CENTER_LEFT);
            Label name = new Label(s.name + "  x" + s.count);
            name.setPrefWidth(320);
            Label price = new Label(BaseProductController.formatRupiah(s.price * s.count));
            price.setStyle("-fx-font-weight:bold;");
            row.getChildren().addAll(name, price);
            listBox.getChildren().add(row);
                total += s.price * s.count;
        }

            // capture total in an effectively-final variable for use inside the event handler
            final double orderTotal = total;

        ScrollPane sc = new ScrollPane(listBox);
        sc.setFitToWidth(true);
        sc.setPrefViewportHeight(340);

        // prepare QR image for the popup (placed to the right of the list)
        ImageView qrInPopup = null;
        try (InputStream qis = App.class.getResourceAsStream("/media/Aii qr.png")) {
            if (qis != null) {
                Image qimg = new Image(qis, 320, 320, true, true);
                qrInPopup = new ImageView(qimg);
                qrInPopup.setFitWidth(320);
                qrInPopup.setFitHeight(320);
                qrInPopup.setPreserveRatio(true);
            }
        } catch (Exception ex) {
            // ignore — QR optional
        }

        VBox qrBox = new VBox(6);
        qrBox.setAlignment(Pos.CENTER);
        if (qrInPopup != null) {
            qrBox.getChildren().add(qrInPopup);
        } else {
            Region placeholder = new Region();
            placeholder.setPrefSize(320, 320);
            qrBox.getChildren().add(placeholder);
        }
        Label qrLabel = new Label("Scan untuk bayar");
        qrBox.getChildren().add(qrLabel);

        HBox contentBox = new HBox(12);
        contentBox.setAlignment(Pos.CENTER_LEFT);
        contentBox.getChildren().addAll(sc, qrBox);
        HBox.setHgrow(sc, Priority.ALWAYS);

        // ensure stage size reflects larger content
        st.setWidth(920);
        st.setHeight(600);

        Label totalLabel = new Label("Total: " + BaseProductController.formatRupiah(total));
        totalLabel.setStyle("-fx-font-size:16px; -fx-font-weight:bold;");

        HBox nameRow = new HBox(8);
        nameRow.setAlignment(Pos.CENTER_LEFT);
        Label nameLabel = new Label("Nama Pemesan:");
        TextField nameField = new TextField();
        nameField.setPrefWidth(320);
        nameRow.getChildren().addAll(nameLabel, nameField);

        HBox buttons = new HBox(8);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        Button cancel = new Button("Batal");
        Button confirm = new Button("Konfirmasi");

        cancel.setOnAction(ev -> st.close());

        confirm.setOnAction(ev -> {
            String customer = nameField.getText().trim();
            if (customer.isEmpty()) {
                Alert a = new Alert(Alert.AlertType.WARNING, "Silakan masukkan nama pemesan.");
                a.showAndWait();
                return;
            }

            // build simple item->qty map to persist
            java.util.Map<String, Integer> itemsMap = new java.util.LinkedHashMap<>();
            for (ItemSummary s : summary.values()) {
                itemsMap.put(s.name, s.count);
            }

            // persist order record (best-effort)
            CustomerStore.addOrder(customer, itemsMap, orderTotal);

            // Acknowledge order, clear cart, close popup and return to primary
            Alert ok = new Alert(Alert.AlertType.INFORMATION);
            ok.setTitle("Pesanan Diterima");
            ok.setHeaderText(null);
            ok.setContentText("Terima kasih, " + customer + ". Pesanan Anda tercatat.");
            ok.showAndWait();

            CartManager.getInstance().clear();
            refreshItems();
            st.close();
            try {
                App.setRoot("primary");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        buttons.getChildren().addAll(cancel, confirm);

        root.getChildren().addAll(title, contentBox, totalLabel, nameRow, buttons);

        Scene scene = new Scene(root);
        st.setScene(scene);
        st.showAndWait();
    }

    private void refreshItems() {
        itemsBox.getChildren().clear();
        List<CartManager.CartItem> items = CartManager.getInstance().getItems();
        if (items.isEmpty()) {
            Label empty = new Label("Your cart is empty.");
            itemsBox.getChildren().add(empty);
            return;
        }

        // aggregate by name
        Map<String, ItemSummary> summary = new HashMap<>();
        for (CartManager.CartItem it : items) {
            ItemSummary s = summary.get(it.name);
            if (s == null) summary.put(it.name, new ItemSummary(it.name, it.price, 1));
            else s.count++;
        }

        for (ItemSummary s : summary.values()) {
            HBox row = new HBox(12);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(8,10,8,10));
            // style as a small white card with subtle border and shadow
            row.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-border-color: #eee; -fx-border-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 8, 0, 0, 2);");

            // try to find an image resource for this product
            ImageView thumbView = null;
            String imgPath = getImageResourceFor(s.name);
            if (imgPath != null) {
                try (InputStream is = App.class.getResourceAsStream(imgPath)) {
                    if (is != null) {
                        Image img = new Image(is, 180, 100, true, true);
                        thumbView = new ImageView(img);
                        thumbView.setFitWidth(180);
                        thumbView.setFitHeight(100);
                        Rectangle clip = new Rectangle(180,100);
                        clip.setArcWidth(6); clip.setArcHeight(6);
                        thumbView.setClip(clip);
                    }
                } catch (Exception ex) {
                    thumbView = null;
                }
            }

            javafx.scene.Node thumbNode;
            if (thumbView != null) {
                thumbNode = thumbView;
            } else {
                Rectangle thumb = new Rectangle(180, 100, Color.web("#e6d7ca"));
                thumb.setArcWidth(6); thumb.setArcHeight(6);
                thumbNode = thumb;
            }

            // info column: title + quantity + controls
            VBox info = new VBox(6);
            Label name = new Label(s.name);
            name.setStyle("-fx-font-weight:bold; -fx-font-size:18px;");

            HBox qtyRow = new HBox(8);
            qtyRow.setAlignment(Pos.CENTER_LEFT);
            Label qty = new Label(String.valueOf(s.count));
            qty.setStyle("-fx-font-size:16px; -fx-text-fill:#555;");

            Button minus = new Button("-");
            minus.setOnAction(e -> {
                CartManager.getInstance().removeOne(s.name);
                refreshItems();
            });
            Button plus = new Button("+");
            plus.setOnAction(e -> {
                CartManager.getInstance().addItem(s.name, s.price);
                refreshItems();
            });

            qtyRow.getChildren().addAll(minus, qty, plus);
            info.getChildren().addAll(name, qtyRow);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Label price = new Label(BaseProductController.formatRupiah(s.price * s.count));
            price.setStyle("-fx-font-weight:bold; -fx-font-size:18px;");

            row.getChildren().addAll(thumbNode, info, spacer, price);
            VBox.setMargin(row, new Insets(6,0,6,0));
            itemsBox.getChildren().add(row);
        }
    }

    /** Try resource paths in known media folders to find an image for given product name. */
    private String getImageResourceFor(String productName) {
        if (productName == null) return null;
        // normalize: remove diacritics, lower case, replace non-alnum with underscore
        String base = java.text.Normalizer.normalize(productName, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "_")
                .replaceAll("^_+|_+$", "");

        String[] folders = {"/media/beverages/", "/media/pastry/", "/media/coffe/", "/media/side dish/"};
        String[] exts = {".jpg", ".png"};
        for (String f : folders) {
            for (String ext : exts) {
                String path = f + base + ext;
                try (InputStream is = App.class.getResourceAsStream(path)) {
                    if (is != null) return path;
                } catch (Exception ex) {
                    // ignore
                }
                // try capitalized first letter variant (some files use initial caps)
                if (base.length() > 0) {
                    String cap = Character.toUpperCase(base.charAt(0)) + base.substring(1);
                    String path2 = f + cap + ext;
                    try (InputStream is2 = App.class.getResourceAsStream(path2)) {
                        if (is2 != null) return path2;
                    } catch (Exception ex) {}
                }
            }
        }
        return null;
    }

    private static class ItemSummary {
        String name;
        double price;
        int count;

        ItemSummary(String name, double price, int count) {
            this.name = name;
            this.price = price;
            this.count = count;
        }
    }
}