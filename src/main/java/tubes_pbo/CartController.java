package tubes_pbo;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
import javafx.scene.text.TextAlignment;

import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;

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

            // show payment popup with QR and instruction
            Stage st = new Stage();
            st.initModality(Modality.APPLICATION_MODAL);
            st.setTitle("Pembayaran");

            HBox content = new HBox(16);
            content.setPadding(new Insets(12));
            content.setAlignment(Pos.CENTER);

            // try to load real QR image from resources; fallback to placeholder canvas
            javafx.scene.Node qrNode;
            final int qrSize = 420; // larger QR size
            try {
                javafx.scene.image.Image img = null;
                java.io.InputStream is = App.class.getResourceAsStream("/media/Aii qr.png");
                if (is != null) {
                    img = new javafx.scene.image.Image(is);
                }
                if (img != null && !img.isError()) {
                    javafx.scene.image.ImageView v = new javafx.scene.image.ImageView(img);
                    v.setFitWidth(qrSize);
                    v.setFitHeight(qrSize);
                    v.setPreserveRatio(true);
                    qrNode = v;
                } else {
                    Canvas qr = new Canvas(qrSize, qrSize);
                    drawPlaceholderQR(qr.getGraphicsContext2D(), (int)qr.getWidth(), (int)qr.getHeight());
                    qrNode = qr;
                }
            } catch (Exception ex) {
             
                Canvas qr = new Canvas(qrSize, qrSize);
                drawPlaceholderQR(qr.getGraphicsContext2D(), (int)qr.getWidth(), (int)qr.getHeight());
                qrNode = qr;
            }

            VBox right = new VBox(12);
            right.setAlignment(Pos.CENTER);
            Label instr = new Label("Silakan lakukan pembayaran dengan memindai QR code di samping menggunakan aplikasi e-wallet Anda.\n\nKami juga melayani pembayaran via cash di kasir.");
            instr.setStyle("-fx-font-size:16px; -fx-font-weight:bold;");
            instr.setWrapText(true);
            instr.setTextAlignment(TextAlignment.CENTER);
            instr.setAlignment(Pos.CENTER);
            instr.setMaxWidth(340);

            Button ok = new Button("OK");
            ok.setDefaultButton(true);
            ok.setOnAction(okEv -> {
                // mark payment complete and clear cart
                CartManager.getInstance().clear();
                refreshItems();
                st.close();
                try {
                    App.setRoot("primary");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

            right.getChildren().addAll(instr, ok);

            content.getChildren().addAll(qrNode, right);

            // set explicit scene and minimum stage size so popup appears larger
            Scene sc = new Scene(content, qrSize + 380, Math.max(qrSize + 40, 360));
            st.setMinWidth(qrSize + 300);
            st.setMinHeight(qrSize + 120);
            st.setScene(sc);
            st.showAndWait();
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

    private void drawPlaceholderQR(GraphicsContext g, int w, int h) {
        // background
        g.setFill(Color.WHITE);
        g.fillRect(0,0,w,h);
        // draw border
        g.setStroke(Color.BLACK);
        g.strokeRect(0.5,0.5,w-1,h-1);

        // simple pseudo-QR: draw random-ish blocks in a grid pattern deterministically
        g.setFill(Color.BLACK);
        int cells = 21;
        int cellW = Math.max(1, w / cells);
        int cellH = Math.max(1, h / cells);
        long seed = (w*h) ^ 0x9e3779b97f4a7c15L;
        for (int y=0;y<cells;y++) {
            for (int x=0;x<cells;x++) {
                // create a repeatable pattern using simple LCG
                seed = (seed * 6364136223846793005L + 1442695040888963407L) & 0xffffffffffffffffL;
                if (((seed >> 8) & 1L) == 1L) {
                    g.fillRect(x*cellW, y*cellH, cellW, cellH);
                }
            }
        }
        // draw three finder squares
        drawFinder(g, 1*cellW, 1*cellH, 5*cellW, 5*cellH);
        drawFinder(g, (cells-6)*cellW, 1*cellH, 5*cellW, 5*cellH);
        drawFinder(g, 1*cellW, (cells-6)*cellH, 5*cellW, 5*cellH);
    }

    private void drawFinder(GraphicsContext g, int x, int y, int w, int h) {
        g.setFill(Color.WHITE);
        g.fillRect(x-2,y-2,w+4,h+4);
        g.setFill(Color.BLACK);
        g.fillRect(x,y,w,h);
        g.setFill(Color.WHITE);
        g.fillRect(x+4,y+4,w-8,h-8);
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

            Label price = new Label(String.format("$%.2f", s.price * s.count));
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
