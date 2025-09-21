package MyDBMS;

import java.io.*;
import java.util.*;
import java.nio.file.*;

public class updating implements database {

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
            
            query=spacecleaner(query);
            String[] parts = query.split(" ");

            String tablename = parts[1];

            String set_part = parts[3];
            String where_part = parts[5];

            if (set_part.contains("=")) {
                String[] set_split = set_part.split("=");
                set_part = set_split[0] + " " + set_split[1];
            }

            if (where_part.contains("=")) {
                String[] where_split = where_part.split("=");
                where_part = where_split[0] + " " + where_split[1];
            }

            String[] set_parts = set_part.split(" ");
            String[] where_parts = where_part.split(" ");

            String set_col = set_parts[0];
            String set_val = set_parts[set_parts.length-1];
            String where_col = where_parts[0];
            String where_val = where_parts[where_parts.length-1];

            String metapath = dbpath.getPath() + "\\" + tablename + ".meta.txt";
            String datapath = dbpath.getPath() + "\\" + tablename + ".data.txt";

            List<String> metalines = Files.readAllLines(Paths.get(metapath));
            ArrayList<String> colnames = new ArrayList<>();

            for (String line : metalines) {
                String[] split = line.split(":");
                colnames.add(split[0]);
            }

            int set_index = -1;
            int where_index = -1;

            for (int i = 0; i < colnames.size(); i++) {
                if (colnames.get(i).equals(set_col)) {
                    set_index = i;
                }
                if (colnames.get(i).equals(where_col)) {
                    where_index = i;
                }
            }

            List<String> datalines = Files.readAllLines(Paths.get(datapath));
            ArrayList<String> updated_rows = new ArrayList<>();

            for (String rowline : datalines) {
                String[] row = rowline.split(",");

                if (row[where_index].trim().equals(where_val)) {
                    row[set_index] = set_val;
                }

                String newline = "";
                for (int i = 0; i < row.length; i++) {
                    newline += row[i].trim();
                    if (i != row.length - 1) {
                        newline += ",";
                    }
                }

                updated_rows.add(newline);
            }

            FileWriter fw = new FileWriter(datapath);
            for (String row : updated_rows) {
                fw.write(row + "\n");
            }
            fw.close();

            System.out.println("row updated");

        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
        }
    }
}
