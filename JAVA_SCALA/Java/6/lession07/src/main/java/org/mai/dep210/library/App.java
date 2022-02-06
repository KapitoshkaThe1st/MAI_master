package org.mai.dep210.library;

import org.apache.log4j.Logger;
import org.mai.dep210.basket.Basket;
import org.mai.dep210.basket.BasketImpl;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

public class App {

    private static Logger log = Logger.getLogger(App.class);

    public static void main(String[] args) throws SQLException {

        String url = "jdbc:h2:mem:library";

        Library library = null;
        try {
            library = new LibraryImpl(url, "", "");
        }
        catch (Exception ex){
            log.error("Could not connect to db ", ex);
            System.exit(1);
        }

        StringBuilder sb = new StringBuilder();
        try {
            Files.lines(Paths.get("src/main/java/org/mai/dep210/library/tables.sql")).forEach(sb::append);
        }
        catch(Exception ex){
            log.error("Could not read DB creation script", ex);
            System.exit(1);
        }

        Connection connection = DriverManager.getConnection(url);
        try(Statement stmt = connection.createStatement()) {
            stmt.execute(sb.toString());
        }

        try {
            library.addAbonent(new Student(1, "petrov"));
            library.addAbonent(new Student(2, "sidorov"));
            library.addAbonent(new Student(3, "ivanov"));

            library.addNewBook(new Book(1, "Harry Potter"));
            library.addNewBook(new Book(2, "Lord of the rings"));
            library.addNewBook(new Book(3, "Moby-Dick"));
            library.addNewBook(new Book(4, "Treasure island"));

            ((LibraryImpl)library).printDB();

            System.out.println(library.findAvailableBooks());
            System.out.println(library.getAllStudents());

            ((LibraryImpl)library).printDB();

            library.borrowBook(new Book(1, "Harry Potter"), new Student(1, "petrov"));

            System.out.println(library.findAvailableBooks());

            ((LibraryImpl)library).printDB();

            library.returnBook(new Book(1, "Harry Potter"), new Student(1, "petrov"));

            ((LibraryImpl)library).printDB();

        }
        catch (InvalidLibraryOperationException ex){
            log.error("library error occured: ", ex);
        }
    }

}
