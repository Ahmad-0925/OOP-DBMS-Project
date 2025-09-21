package MyDBMS;

import java.io.*;
import java.util.*;
import java.nio.file.*;

public class select implements database {

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
            ArrayList<String> qquery = new ArrayList<String>();
            for (String data : parts) {
                if (!data.isEmpty())
                    qquery.add(data);
            }

            String col_part = parts[1].trim();
            if (col_part.equals("*")) {
                col_part = "*";
            }

            String tablename = parts[3];

            boolean has_where = false;
            String where_col = "";
            String where_val = "";

    
            if (parts.length > 4 && parts[4].equals("where")) {
                has_where = true;
                String condition = parts[5];

                int eqIndex = condition.indexOf('=');
                if (eqIndex != -1) {
                    where_col = condition.substring(0, eqIndex).trim();
                    where_val = condition.substring(eqIndex + 1).trim();

                   
                    if (where_val.startsWith("'") && where_val.endsWith("'")) {
                        String temp = "";
                        for (int z = 1; z < where_val.length() - 1; z++) {
                            temp += where_val.charAt(z);
                        }
                        where_val = temp;
                    }
                }
            }

            String[] selected_cols = col_part.split(",");

            String metapath = dbpath.getPath() + "\\" + tablename + ".meta.txt";
            String datapath = dbpath.getPath() + "\\" + tablename + ".data.txt";

            List<String> metalines = Files.readAllLines(Paths.get(metapath));
            ArrayList<String> colnames = new ArrayList<>();

            for (String line : metalines) {
                String[] split = line.split(":");
                colnames.add(split[0]);
            }

            int where_index = -1;
            if (has_where) {
                for (int i = 0; i < colnames.size(); i++) {
                    if (colnames.get(i).equals(where_col)) {
                        where_index = i;
                    }
                }
            }

            ArrayList<Integer> selected_indexes = new ArrayList<>();

            if (col_part.equals("*")) {
                for (int i = 0; i < colnames.size(); i++) {
                    selected_indexes.add(i);
                }
            } else {
                for (int i = 0; i < selected_cols.length; i++) {
                    for (int j = 0; j < colnames.size(); j++) {
                        if (selected_cols[i].equals(colnames.get(j))) {
                            selected_indexes.add(j);
                        }
                    }
                }
            }

            List<String> datalines = Files.readAllLines(Paths.get(datapath));

            for (String rowline : datalines) {
                String[] row = rowline.split(",");

                if (!has_where || row[where_index].trim().equals(where_val)) {
                    for (int i = 0; i < selected_indexes.size(); i++) {
                        int col_index = selected_indexes.get(i);
                        System.out.print(row[col_index].trim());
                        if (i != selected_indexes.size() - 1) {
                            System.out.print(", ");
                        }
                    }
                    System.out.println();
                }
            }

        } catch (Exception e) {
            System.out.println("error coming :" + e.getMessage());
        }
    }
}
