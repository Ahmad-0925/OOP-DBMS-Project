package MyDBMS;



import java.io.*;
import java.util.*;

public class mainclass {

    public static void main(String[] args) {
        show_main_menu();
    }

    static void show_main_menu() {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("1. create db");
            System.out.println("2. use db");
            System.out.println("0. exit");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 0:
                    return;

                case 1:
                    create_db(sc);
                    break;

                case 2:
                    use_db(sc);
                    break;

                default:
                    System.out.println("invalid choice");
            }
        }
    }

    static void create_db(Scanner sc) {
        System.out.print("enter db name: ");
        String dbname = sc.nextLine();
        File dbfolder = new File("C:\\Users\\Laptop\\Desktop\\DBS\\" + dbname);
        if (dbfolder.exists()) {
            System.out.println("db already exists");
        } else {
            dbfolder.mkdir();
            System.out.println("db created");
        }
    }

    static void use_db(Scanner sc) {
        File folder = new File("C:\\Users\\Laptop\\Desktop\\DBS");
        File[] files = folder.listFiles();

        ArrayList<File> dbs = new ArrayList<>();
        System.out.println("available dbs:");
        int index = 1;

        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                System.out.println(index + ". " + files[i].getName());
                dbs.add(files[i]);
                index++;
            }
        }

        if (dbs.size() == 0) {
            System.out.println("no db found");
            return;
        }

        System.out.print("select db: ");
        int dbchoice = sc.nextInt();
        sc.nextLine();

        if (dbchoice < 1 || dbchoice > dbs.size()) {
            System.out.println("invalid choice");
            return;
        }

        File selecteddb = dbs.get(dbchoice - 1);
        inner_menu(sc, selecteddb);
    }

    static void inner_menu(Scanner sc, File selecteddb) {
        while (true) {
            System.out.println("1. create table");
            System.out.println("2. insert");
            System.out.println("3. select");
            System.out.println("4. update");
            System.out.println("0. back");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 0:
                    return;

                case 1:
                    System.out.print("enter create table query: ");
                    String q1 = sc.nextLine();
                   database op1 = new tablemanager();
                    op1.execute(q1, selecteddb);

                    break;

                case 2:
                    System.out.print("enter insert query: ");
                    String q2 = sc.nextLine();
                   database op2 = new inserting();
                    op2.execute(q2, selecteddb);

                    break;

                case 3:
                    System.out.print("enter select query: ");
                    String q3 = sc.nextLine();
                    database op3 = new select();
                    op3.execute(q3, selecteddb);

                    break;

                case 4:
                    System.out.print("enter update query: ");
                    String q4 = sc.nextLine();
                    database op4 = new updating();
                    op4.execute(q4, selecteddb);

                    break;

                default:
                    System.out.println("invalid choice");
            }
        }
    }
}
