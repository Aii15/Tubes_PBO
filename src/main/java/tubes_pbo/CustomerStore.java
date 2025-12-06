package tubes_pbo;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Simple append-only store for customer names.
 * Stores one entry per line in the user's home directory file: ~/.kasir_customers.txt
 */
public final class CustomerStore {

    // Store file moved to project workspace as requested
    // Absolute path: G:\MATKUL\Semester 3\PBO\Program Pelayanan Kasir\kasir\customers.txt
    private static final Path STORE = Paths.get("G:", "MATKUL", "Semester 3", "PBO", "Program Pelayanan Kasir", "kasir", "customers.txt");

    private CustomerStore() { }

    public static synchronized void addCustomer(String name) {
        if (name == null) return;
        addOrder(name, java.util.Collections.emptyMap(), 0.0);
    }

    /**
     * Persist a full order record: timestamp, name, total, and items.
     * Items map is stored as a compact comma-separated list: name:qty
     */
    public static synchronized void addOrder(String name, java.util.Map<String, Integer> items, double total) {
        if (name == null) return;
        StringBuilder sb = new StringBuilder();
        sb.append(LocalDateTime.now().toString()).append('\t');
        sb.append(name.trim()).append('\t');
        sb.append(String.format("%.2f", total)).append('\t');
        // items as name:qty,name:qty
        boolean first = true;
        if (items != null) {
            for (java.util.Map.Entry<String, Integer> e : items.entrySet()) {
                if (!first) sb.append(',');
                first = false;
                sb.append(e.getKey().replace(',', ' ')).append(':').append(e.getValue());
            }
        }
        String line = sb.toString();
        try {
            Path parent = STORE.getParent();
            if (parent != null && !Files.exists(parent)) Files.createDirectories(parent);
            try (BufferedWriter w = Files.newBufferedWriter(STORE, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND)) {
                w.write(line);
                w.newLine();
            }
        } catch (IOException ex) {
            // best-effort: log to stderr but do not throw to UI
            System.err.println("Failed to persist customer order: " + ex.getMessage());
        }
    }

    /**
     * Load all stored entries as raw lines (timestamp TAB name). Returns empty list if none.
     */
    public static List<String> loadAllRaw() {
        try {
            if (!Files.exists(STORE)) return Collections.emptyList();
            return Files.readAllLines(STORE, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            System.err.println("Failed to read customer store: " + ex.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Load only the names (stripping timestamp column).
     */
    public static List<String> loadAllNames() {
        List<String> raw = loadAllRaw();
        if (raw.isEmpty()) return Collections.emptyList();
        List<String> out = new ArrayList<>(raw.size());
        for (String r : raw) {
            int tab = r.indexOf('\t');
            if (tab >= 0 && tab + 1 < r.length()) out.add(r.substring(tab + 1));
            else if (!r.isBlank()) out.add(r);
        }
        return out;
    }
}
