import java.io.*;
import java.nio.file.*;
import java.util.*;

public class ReportsGenerator {
    public static void main(String[] args) {
        String inputFile = "input/input.txt";
        String maxFile = "output/raport_max.txt";
        String sumFile = "output/raport_suma.txt";
        String sumSortedFile = "output/raport_suma_sort_po_sumie_wplat.txt";
        String nameSortedFile = "output/raport_suma_sort_po_nazwisku.txt";

        List<String> lines = readFile(inputFile);
        Map<String, Person> people = processLines(lines);

        writeMaxReport(people, maxFile);
        writeSumReport(people, sumFile);
        writeSortedSumReport(people, sumSortedFile);
        writeNameSortedReport(people, nameSortedFile);
    }

    private static List<String> readFile(String filePath) {
        try {
            return Files.readAllLines(Paths.get(filePath));
        } catch (IOException e) {
            System.err.println("Błąd odczytu pliku: " + filePath + " - " + e.getMessage());
            return Collections.emptyList();
        }
    }

    private static Map<String, Person> processLines(List<String> lines) {
        Map<String, Person> people = new HashMap<>();

        for (String line : lines) {
            String[] parts = line.split(" ");
            if (parts.length < 4) continue;

            String id = parts[0];
            String firstName = parts[1];
            String lastName = parts[2];
            int deposit = Integer.parseInt(parts[3]);

            String key = id;
            people.putIfAbsent(key, new Person(id, firstName, lastName));
            people.get(key).addDeposit(deposit);
        }

        return people;
    }

    private static void writeMaxReport(Map<String, Person> people, String filePath) {
        Person maxPerson = people.values().stream()
                .max(Comparator.comparingInt(Person::getMaxDeposit))
                .orElse(null);

        if (maxPerson != null) {
            writeFile(filePath, maxPerson.toStringMax());
        }
    }

    private static void writeSumReport(Map<String, Person> people, String filePath) {
        List<String> lines = new ArrayList<>();

        for (Person person : people.values()) {
            lines.add(person.toStringSum());
        }

        writeFile(filePath, lines);
    }

    private static void writeSortedSumReport(Map<String, Person> people, String filePath) {
        List<Person> sortedPeople = new ArrayList<>(people.values());
        sortedPeople.sort(Comparator.comparingInt(Person::getTotalDeposits).reversed());

        List<String> lines = new ArrayList<>();
        for (Person person : sortedPeople) {
            lines.add(person.toStringSum());
        }

        writeFile(filePath, lines);
    }

    private static void writeNameSortedReport(Map<String, Person> people, String filePath) {
        List<Person> sortedPeople = new ArrayList<>(people.values());
        sortedPeople.sort(Comparator.comparing(Person::getLastName)
                .thenComparing(Person::getFirstName));

        List<String> lines = new ArrayList<>();
        for (Person person : sortedPeople) {
            lines.add(person.toStringSum());
        }

        writeFile(filePath, lines);
    }

    private static void writeFile(String filePath, String content) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(content);
        } catch (IOException e) {
            System.err.println("Błąd zapisu do pliku: " + filePath + " - " + e.getMessage());
        }
    }

    private static void writeFile(String filePath, List<String> lines) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Błąd zapisu do pliku: " + filePath + " - " + e.getMessage());
        }
    }
}

class Person {
    private final String id;
    private final String firstName;
    private final String lastName;
    private int totalDeposits;
    private int maxDeposit;

    public Person(String id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.totalDeposits = 0;
        this.maxDeposit = 0;
    }

    public void addDeposit(int deposit) {
        totalDeposits += deposit;
        if (deposit > maxDeposit) {
            maxDeposit = deposit;
        }
    }

    public int getTotalDeposits() {
        return totalDeposits;
    }

    public int getMaxDeposit() {
        return maxDeposit;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String toStringSum() {
        return id + " " + firstName + " " + lastName + " " + totalDeposits;
    }

    public String toStringMax() {
        return id + " " + firstName + " " + lastName + " " + maxDeposit;
    }
}
