package MyDBMS;

import java.io.*;
import java.util.*;

public class tablemanager implements database {

	
	 String spacecleaner(String query){
		 
		 
	 
	    	query = query.trim();
	    	String cleaned = "";
	    	boolean lastWasSpace = false;

	    	for (int i = 0; i < query.length(); i++) {
	    	    char ch = query.charAt(i);
	    	    
	    	    if (ch == ' ') {
	    	        if (!lastWasSpace) {
	    	            cleaned += ch;
	    	            lastWasSpace = true;
	    	        }
	    	    } else {
	    	        cleaned += ch;
	    	        lastWasSpace = false;
	    	    }
	    	}

	    	query = cleaned;
	    	return cleaned;
	    }

        
    public void execute(String query, File dbpath) {

        try {
        	query=spacecleaner(query);
            String tablename = "";
            int check = 13;
            while (check < query.length() && query.charAt(check) != '(') {
                tablename += query.charAt(check);
                check++;
            }

            int columnsstart = query.indexOf('(');
            
            
            int columnsend = query.indexOf(')');
            String columnarea = query.substring(columnsstart + 1, columnsend);

            String[] columns = columnarea.split(",");

            ArrayList<String> colnames = new ArrayList<>();
            
            ArrayList<String> coltype = new ArrayList<>();
            
            String primarykey = "";

            for (int i = 0; i < columns.length; i++) {
                String[] words = columns[i].trim().split(" ");

                if (words.length >= 2) {
                    String columnname = words[0];
                    String columndatatype = words[1];
                    String checkprimary = "";
                    String key = "";

                    if (words.length == 4) {
                        checkprimary = words[2];
                        key = words[3];
                    }

                    if (checkprimary.equals("primary") && key.equals("key")) {
                        if (!primarykey.equals("")) {
                            System.out.println("only 1 primary key can be made(composite key concept is not used here)");
                            return;
                        }

                        primarykey = columnname;
                        columndatatype += "(PK)";
                    }

                    colnames.add(columnname);
                    coltype.add(columndatatype);
                } else {
                    System.out.println("invalid making of column format");
                    return;
                }
            }

            String metapath = dbpath.getPath() + "\\" + tablename.trim() + ".meta.txt";
            String datapath = dbpath.getPath() + "\\" + tablename.trim() + ".data.txt";

            File checkMeta = new File(metapath);
            if (checkMeta.exists()) {
                System.out.println(" Table with this name already exists");
                return;
            }

            
            try (
                FileWriter metaWriter = new FileWriter(metapath);
                FileWriter dataWriter = new FileWriter(datapath)
            ) {
                for (int i = 0; i < colnames.size(); i++) {
                    String line = colnames.get(i) + ":" + coltype.get(i) + "\n";
                    metaWriter.write(line);
                }
                
            }

            System.out.println("table '" + tablename.trim() + "' created successfully(Command exectued successfully");

        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }
}
