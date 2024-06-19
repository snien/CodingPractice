import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Enrollee implements Comparable<Enrollee> {
    String userId;
    String firstName;
    String lastName;
    int version;
    String insuranceCompany;

    public Enrollee(String userId, String firstName, String lastName, int version, String insuranceCompany) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.version = version;
        this.insuranceCompany = insuranceCompany;
    }

    @Override
    public int compareTo(Enrollee other) {
    	int status = this.lastName.compareTo(other.lastName);
        if (status != 0) {
            return status;
        }
        return this.firstName.compareTo(other.firstName);
    }
}

public class Interview {

    public static void main(String[] args) {
    	String inputFile = "input" + File.separator + "data.csv";
        String outputDirPath = "output";

        // Ensure the output directory exists
        File outputDir = new File(outputDirPath);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }


        Map<String, List<Enrollee>> insuranceCompanyMap = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                String userId = values[0];
                String firstName = values[1];
                String lastName = values[2];
                int version = Integer.parseInt(values[3]);
                String insuranceCompany = values[4];

                Enrollee enrollee = new Enrollee(userId, firstName, lastName, version, insuranceCompany);

                insuranceCompanyMap.putIfAbsent(insuranceCompany, new ArrayList<>());
                insuranceCompanyMap.get(insuranceCompany).add(enrollee);

            }

            // Process each insurance company
            for (Map.Entry<String, List<Enrollee>> entry : insuranceCompanyMap.entrySet()) {
                String insuranceCompany = entry.getKey();
                List<Enrollee> enrollees = entry.getValue();

                // Remove duplicates by keeping only the highest version
                Map<String, Enrollee> uniqueEnrollees = new HashMap<>();
                for (Enrollee enrollee : enrollees) {
                    if (!uniqueEnrollees.containsKey(enrollee.userId)) {
                        uniqueEnrollees.put(enrollee.userId, enrollee);
                    } else if(uniqueEnrollees.get(enrollee.userId).version < enrollee.version) {
                    	uniqueEnrollees.put(enrollee.userId, enrollee);
                    }
                }

                // Convert to list and sort by last name and first name
                List<Enrollee> sortedEnrollees = new ArrayList<>(uniqueEnrollees.values());
                Collections.sort(sortedEnrollees);

                // Write file
                String outputFilePath = outputDirPath + File.separator + insuranceCompany + ".csv";
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFilePath))) {
                    for (Enrollee enrollee : sortedEnrollees) {
                        bw.write(String.format("%s,%s,%s,%d,%s%n", enrollee.userId, enrollee.firstName, enrollee.lastName, enrollee.version, enrollee.insuranceCompany));
                    }
                } catch (IOException e) {
                    System.err.println("Error writing to file " + outputFilePath);
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading the input file.");
            e.printStackTrace();
        }
    }
}