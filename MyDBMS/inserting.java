package MyDBMS;

import java.io.*;
import java.util.*;
import java.nio.file.*;

public class inserting implements database {

    String spacecleaner(String query) {
        query = query.trim();
        String cleaned = "";
        boolean lastspace = false;

        for (int i = 0; i < query.length(); i++) {
            char ch = query.charAt(i);

            if (ch == ' ') {
                if (!lastspace) {
                    cleaned += ch;
                    lastspace = true;
                }
            } else {
                cleaned += ch;
                lastspace = false;
            }
        }

        return cleaned;
    }

    public void execute(String query, File dbpath) {

        try {
            query = spacecleaner(query);
            String[] parts = query.split(" ");
            String tablename = parts[2];

            ArrayList<String[]> allRows = new ArrayList<>();
            int i = 0;

            while (i < query.length()) {
                if (query.charAt(i) == '(') {
                    int j = i + 1;
                    String row = "";

                    while (j < query.length() && query.charAt(j) != ')') {
                        row += query.charAt(j);
                        j++;
                    }

                    String[] values = row.split(",");

                    for (int k = 0; k < values.length; k++) {
                        values[k] = values[k].trim();

                        if (values[k].startsWith("'") && values[k].endsWith("'")) {
                            String temp = "";
                            for (int x = 1; x < values[k].length() - 1; x++) {
                                temp += values[k].charAt(x);
                            }
                            values[k] = temp;
                        }
                    }

                    allRows.add(values);
                    i = j;
                }
                i++;
            }

            String metapath = dbpath.getPath() + "\\" + tablename + ".meta.txt";
            List<String> metalines = Files.readAllLines(Paths.get(metapath));

            ArrayList<String> colnames = new ArrayList<>();
            ArrayList<String> coltypes = new ArrayList<>();
            String primary_key = "";
            int pk_index = -1;

            int index = 0;
            for (String line : metalines) {
                String[] split = line.split(":");
                colnames.add(split[0]);
                coltypes.add(split[1]);

                if (split[1].contains("(PK)")) {
                    primary_key = split[0];
                    pk_index = index;
                }

                index++;
            }

            String datapath = dbpath.getPath() + "\\" + tablename + ".data.txt";
            File datafile = new File(datapath);
            List<String> existingRows = new ArrayList<>();

            if (datafile.exists()) {
                existingRows = Files.readAllLines(Paths.get(datapath));
            }

            FileWriter writer = new FileWriter(datapath, true);

            for (String[] values : allRows) {
                if (pk_index != -1) {
                    String pkValue = values[pk_index].trim();
                    boolean exists = false;

                    for (String line : existingRows) {
                        String[] row_values = line.split(",");
                        if (row_values.length > pk_index && row_values[pk_index].equals(pkValue)) {
                            System.out.println("primary key already exists for value: " + pkValue);
                            exists = true;
                            break;
                        }
                    }

                    if (exists) {
                        continue;
                    }
                }

                for (int v = 0; v < values.length; v++) {
                    writer.write(values[v]);
                    if (v != values.length - 1) {
                        writer.write(",");
                    }
                }
                writer.write("\n");
            }

            writer.close();
            System.out.println("row(s) inserted (command successfully executed)");

        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
        }
    }
}
