import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class ReportsGenerator {
    public static class Payment {
        public String id, imie, nazwisko;
        public double suma = 0;

        public Payment(String id, String imie, String nazwisko) {
            this.id = id;
            this.imie = imie;
            this.nazwisko = nazwisko;
        }
    }

    public static Map<String, Payment> processInput(String filename, String delimiter, List<String> columns) throws IOException {
        Path path = Paths.get(filename);
        if (!Files.exists(path)) {
            System.err.println("Plik nie istnieje: " + filename);
            throw new FileNotFoundException("Nie można odnaleźć pliku: " + filename);
        }

        Map<String, Payment> payments = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.trim().split(delimiter);
                
                int idIdx = columns.indexOf("id");
                int imieIdx = columns.indexOf("imie");
                int nazwiskoIdx = columns.indexOf("nazwisko");
                int sumaIdx = columns.indexOf("suma_wplat");

                String id = parts[idIdx];
                String imie = parts[imieIdx];
                String nazwisko = parts[nazwiskoIdx];
                double suma = Double.parseDouble(parts[sumaIdx]);

                payments.computeIfAbsent(id, k -> new Payment(id, imie, nazwisko)).suma += suma;
            }
        }
        return payments;
    }

    public static void generateReports(Map<String, Payment> payments) throws IOException {
        // Raport po ID
        try (PrintWriter writer = new PrintWriter("raport_po_id.txt")) {
            payments.values().stream()
                .sorted(Comparator.comparing(p -> p.id))
                .forEach(p -> writer.printf("%s %s %s %.2f%n", p.id, p.imie, p.nazwisko, p.suma));
        }

        // Raport po sumie wpłat
        try (PrintWriter writer = new PrintWriter("raport_po_sumie_wplat.txt")) {
            payments.values().stream()
                .sorted(Comparator.comparing((Payment p) -> p.suma).reversed())
                .forEach(p -> writer.printf("%s %s %s %.2f%n", p.id, p.imie, p.nazwisko, p.suma));
        }

        // Raport po nazwisku
        try (PrintWriter writer = new PrintWriter("raport_po_nazwisku.txt")) {
            payments.values().stream()
                .sorted(Comparator.comparing((Payment p) -> p.nazwisko)
                    .thenComparing(p -> p.imie))
                .forEach(p -> writer.printf("%s %s %s %.2f%n", p.id, p.imie, p.nazwisko, p.suma));
        }
    }

    public static void main(String[] args) throws IOException {
        String filename = "input.txt";
        String delimiter = " ";
        List<String> columns = Arrays.asList("id", "imie", "nazwisko", "suma_wplat");

        if (args.length > 0) {
            filename = args[0];
            if (filename.contains("inputB") || filename.contains("inputD")) {
                delimiter = ";";
            }
            if (filename.contains("inputC") || filename.contains("inputD")) {
                columns = Arrays.asList("id", "suma_wplat", "imie", "nazwisko");
            }
        }

        Map<String, Payment> payments = processInput(filename, delimiter, columns);
        generateReports(payments);
    }
}
