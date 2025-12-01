package tubes_pbo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Launcher {
    public static void main(String[] args) throws Exception {
        try {
            // Determine where the code is running from (jar or classes dir)
            URI codeLocation = Launcher.class.getProtectionDomain().getCodeSource().getLocation().toURI();
            File codeFile = new File(codeLocation);

            Path nativeTmp = Files.createTempDirectory("kasir_native_");
            nativeTmp.toFile().deleteOnExit();

            List<Path> extracted = new ArrayList<>();

            if (codeFile.isFile() && codeFile.getName().endsWith(".jar")) {
                try (JarFile jar = new JarFile(codeFile)) {
                    Enumeration<JarEntry> entries = jar.entries();
                    while (entries.hasMoreElements()) {
                        JarEntry e = entries.nextElement();
                        String name = e.getName();
                        if (name.endsWith(".dll") || name.endsWith(".so") || name.endsWith(".dylib")) {
                            String fileName = Paths.get(name).getFileName().toString();
                            Path out = nativeTmp.resolve(fileName);
                            try (InputStream in = jar.getInputStream(e); OutputStream outStream = new FileOutputStream(out.toFile())) {
                                byte[] buf = new byte[8192];
                                int len;
                                while ((len = in.read(buf)) != -1) outStream.write(buf, 0, len);
                            }
                            out.toFile().deleteOnExit();
                            extracted.add(out);
                        }
                    }
                }
            } else if (codeFile.isDirectory()) {
                // Running from classes directory (IDE). Copy any native libs present there.
                Files.walk(codeFile.toPath()).filter(p -> {
                    String s = p.toString().toLowerCase();
                    return s.endsWith(".dll") || s.endsWith(".so") || s.endsWith(".dylib");
                }).forEach(p -> {
                    try {
                        Path out = nativeTmp.resolve(p.getFileName().toString());
                        Files.copy(p, out);
                        out.toFile().deleteOnExit();
                        extracted.add(out);
                    } catch (Exception ex) {
                        // ignore
                    }
                });
            }

            // Try to load common VC runtimes first (Windows) to satisfy dependencies
            String[] preferLoad = {"ucrtbase", "vcruntime140", "msvcp140"};
            for (String pref : preferLoad) {
                for (Path p : extracted) {
                    String n = p.getFileName().toString().toLowerCase();
                    if (n.contains(pref) && !n.endsWith(".class")) {
                        try { System.load(p.toAbsolutePath().toString()); } catch (UnsatisfiedLinkError ignore) {}
                    }
                }
            }

            // Load remaining native libraries
            for (Path p : extracted) {
                String fname = p.getFileName().toString().toLowerCase();
                if (fname.endsWith(".dll") || fname.endsWith(".so") || fname.endsWith(".dylib")) {
                    // skip VC runtimes already attempted
                    if (fname.contains("ucrtbase") || fname.contains("vcruntime140") || fname.contains("msvcp140")) continue;
                    try { System.load(p.toAbsolutePath().toString()); } catch (UnsatisfiedLinkError ignore) {}
                }
            }
        } catch (Exception ex) {
            System.err.println("Launcher native extraction failed: " + ex.getMessage());
        }

        // Start JavaFX application
        App.main(args);
    }
}
