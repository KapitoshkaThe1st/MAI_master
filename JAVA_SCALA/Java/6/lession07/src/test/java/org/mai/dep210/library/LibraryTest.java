package org.mai.dep210.library;

import org.apache.commons.lang.mutable.MutableBoolean;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static jdk.nashorn.internal.objects.Global.print;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

public class LibraryTest {

    private static Logger log = Logger.getLogger(App.class);

    Library library = null;
    private String url = "jdbc:h2:mem:library";

    private void init(){
        try {
            library = new LibraryImpl(url, "", "");

            StringBuilder sb = new StringBuilder();
            Files.lines(Paths.get("src/main/java/org/mai/dep210/library/tables.sql")).forEach(sb::append);

            Connection connection = DriverManager.getConnection(url);
            try(Statement stmt = connection.createStatement()) {
                stmt.execute(sb.toString());
            }

        }
        catch(Exception ex){
            log.error("initialization failed", ex);
            fail();
        }
    }

    private void deinit(){
        try {
            String query = "drop table books;\ndrop table abonents;";

            Connection connection = DriverManager.getConnection(url);
            try(Statement stmt = connection.createStatement()) {
                stmt.execute(query);
            }

        }
        catch(Exception ex){
            log.error("initialization failed", ex);
            fail();
        }
    }

    @Before
    public void setUp() throws Exception {
        init();
    }

    @After
    public void tearDown() throws Exception {
        deinit();
    }

    @Test
    public void addNewBook() throws Exception {
        Book book = new Book(1, "Kolobok");

        List<Book> books = library.findAvailableBooks();
        assertThat(books, not(hasItem(book)));

        library.addNewBook(book);

        books = library.findAvailableBooks();
        assertThat(books, hasItem(book));
    }

    @Test(expected = InvalidLibraryOperationException.class)
    public void addNewBookCopy() throws Exception {
        Book book = new Book(1, "Kolobok");

        library.addNewBook(book);
        library.addNewBook(book);
    }

    @Test
    public void addAbonent() throws Exception {

        Student student = new Student(1, "petrov");

        List<Student> students = library.getAllStudents();
        assertThat(students, not(hasItem(student)));

        library.addAbonent(student);

        students = library.getAllStudents();

        assertThat(students, hasItem(student));
    }

    @Test(expected = InvalidLibraryOperationException.class)
    public void addAbonentCopy() throws Exception {
        Student student = new Student(1, "petrov");

        library.addAbonent(student);
        library.addAbonent(student);
    }

    @Test
    public void borrowBook() throws Exception {
        Student student = new Student(1, "petrov");
        Book book = new Book(1, "Kolobok");
        library.addNewBook(book);

        List<Book> availableBooks = library.findAvailableBooks();

        assertFalse(availableBooks.isEmpty());

        library.borrowBook(availableBooks.get(0), student);

        availableBooks = library.findAvailableBooks();

        assertTrue(availableBooks.isEmpty());
    }

    @Test(expected = InvalidLibraryOperationException.class)
    public void borrowBorrowedBook() throws Exception {
        Student student1 = new Student(1, "petrov");
        Student student2 = new Student(2, "sidorov");
        library.addAbonent(student1);
        library.addAbonent(student2);

        Book book = new Book(1, "Kolobok");
        library.addNewBook(book);

        library.borrowBook(book, student1);
        library.borrowBook(book, student2);
    }

    @Test
    public void returnBook() throws Exception {
        Student student1 = new Student(1, "petrov");
        Book book = new Book(1, "Kolobok");

        library.addNewBook(book);

        library.borrowBook(book, student1);

        List<Book> availableBooks = library.findAvailableBooks();
        assertTrue(availableBooks.isEmpty());

        library.returnBook(book, student1);
        availableBooks = library.findAvailableBooks();
        assertThat(availableBooks, hasItem(book));
    }

    @Test(expected = InvalidLibraryOperationException.class)
    public void returnBorrowedByOtherBook() throws Exception {
        Student student1 = new Student(1, "petrov");
        Student student2 = new Student(2, "sidorov");
        Book book = new Book(1, "Kolobok");

        library.addNewBook(book);

        library.borrowBook(book, student1);
        library.returnBook(book, student2);
    }

    @Test(expected = InvalidLibraryOperationException.class)
    public void returnNotBorrowedBook() throws Exception {
        Student student = new Student(1, "petrov");
        Book book = new Book(1, "Kolobok");

        library.addNewBook(book);
        library.returnBook(book, student);
    }

    @Test
    public void findAvailableBooks() throws Exception {
        Book[] books = new Book[] { new Book(1, "Kolobok"),
                                new Book(2, "LotR"),
                                new Book(3,"Treasure Island")};

        MutableBoolean thrown = new MutableBoolean(false);
        Arrays.stream(books).forEach(book -> {
            try {
                library.addNewBook(book);
            } catch (InvalidLibraryOperationException ex) {
                thrown.setValue(true);
            }
        });

        assertFalse(thrown.toBoolean());

        List<Book> availableBooks = library.findAvailableBooks();
        assertThat(availableBooks, hasItems(books));
        assertEquals(books.length, availableBooks.size());
    }

    @Test
    public void getAllStudents() throws Exception {
        Student[] students = new Student[] { new Student(1, "ivanov"),
                new Student(2, "petrov"),
                new Student(3,"sidorov")};

        MutableBoolean thrown = new MutableBoolean(false);
        Arrays.stream(students).forEach(student -> {
            try {
                library.addAbonent(student);
            } catch (InvalidLibraryOperationException ex) {
                thrown.setValue(true);
            }
        });

        assertFalse(thrown.toBoolean());

        List<Student> allStudents = library.getAllStudents();
        assertThat(allStudents, hasItems(students));
        assertEquals(students.length, allStudents.size());
    }

}