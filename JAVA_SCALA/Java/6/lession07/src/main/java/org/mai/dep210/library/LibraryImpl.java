package org.mai.dep210.library;

import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LibraryImpl implements Library {

    private static final Logger log = Logger.getLogger(LibraryImpl.class);

    private final String jdbcUrl;
    private final String user;
    private final String password;

    public LibraryImpl(String jdbcUrl, String user, String password) {
        this.jdbcUrl = jdbcUrl;
        this.user = user;
        this.password = password;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl, user, password);
    }

    public void printDB(){
        String fetchBooksString = "select * from BOOKS";

        try(PreparedStatement statement = getConnection().prepareStatement(fetchBooksString)) {
            ResultSet rs = statement.executeQuery();
            System.out.println("book_id\t\tbook_title\t\tstudent_id");
            while(rs.next()){
                System.out.println(rs.getInt("book_id") + "\t\t"
                        + rs.getString("book_title") + "\t\t"
                        + rs.getInt("student_id"));
            }
        }
        catch (Exception ex){
            log.error("fetching all books gone wrong", ex);
        }
        String fetchAbonentsString = "select * from ABONENTS";

        try(PreparedStatement statement = getConnection().prepareStatement(fetchAbonentsString)) {
            ResultSet rs = statement.executeQuery();
            System.out.println("student_id\t\tstudent_name");
            while(rs.next()){
                System.out.println(rs.getInt("student_id") + "\t\t"
                        + rs.getString("student_name"));
            }
        }
        catch (Exception ex){
            log.error("fetching all abonents gone wrong", ex);
        }
    }
    
    @Override
    public void addNewBook(Book book) throws InvalidLibraryOperationException {
        String queryString = "insert into books(book_id, book_title, student_id) values(?, ?, default)";
        try(PreparedStatement stmt = getConnection().prepareStatement(queryString)) {
            stmt.setInt(1, book.getId());
            stmt.setString(2, book.getTitle());

            stmt.execute();
            getConnection().commit();
        }
        catch (SQLException ex){
            if(ex.getSQLState().equals("23505")){ // primary key violation error
                throw new InvalidLibraryOperationException("Book " + book + " is already presented");
            }
            log.error("add new book failed", ex);
        }
    }

    @Override
    public void addAbonent(Student student) throws InvalidLibraryOperationException {
        String queryString = "insert into abonents values(?, ?)";
        try(PreparedStatement stmt = getConnection().prepareStatement(queryString)) {
            stmt.setInt(1, student.getId());
            stmt.setString(2, student.getName());

            stmt.execute();
            getConnection().commit();
        }
        catch (SQLException ex){
            if(ex.getSQLState().equals("23505")){ // primary key violation error
                throw new InvalidLibraryOperationException("Student " + student + " is already presented");
            }
            log.error("add new abonent failed", ex);
        }
    }

    @Override
    public void borrowBook(Book book, Student student) throws InvalidLibraryOperationException {
        String checkQueryString = "select student_id from books where book_id = ?";
        try(PreparedStatement stmt = getConnection().prepareStatement(checkQueryString)) {
            stmt.setInt(1, book.getId());

            ResultSet rs = stmt.executeQuery();
            rs.next();
            int actualBorrowerId = rs.getInt("student_id");
            if(actualBorrowerId != 0){
                throw new InvalidLibraryOperationException("This can not be borrowed now because it is borrowed already");
            }
            getConnection().commit();
        }
        catch (SQLException ex){
            log.error("pre-borrow check failed", ex);
        }

        String queryString = "update books set student_id = ? where book_id = ?";
        try(PreparedStatement stmt = getConnection().prepareStatement(queryString)) {
            stmt.setInt(1, student.getId());
            stmt.setInt(2, book.getId());

            stmt.execute();
            getConnection().commit();
        }
        catch (SQLException ex){
            log.error("borrow book failed", ex);
        }
    }

    @Override
    public void returnBook(Book book, Student student) throws InvalidLibraryOperationException {
        String checkQueryString = "select student_id from books where book_id = ?";
        try(PreparedStatement stmt = getConnection().prepareStatement(checkQueryString)) {
            stmt.setInt(1, book.getId());

            ResultSet rs = stmt.executeQuery();
            rs.next();
            int actualBorrowerId = rs.getInt("student_id");
            if(actualBorrowerId != student.getId()){
                if(actualBorrowerId == 0)
                    throw new InvalidLibraryOperationException("Book is not borrowed");
                else
                    throw new InvalidLibraryOperationException("This abonent did not borrowed this book");
            }
            getConnection().commit();
        }
        catch (SQLException ex){
            log.error("pre-return check failed: ", ex);
        }

        String queryString = "update books set student_id = null where book_id = ?";
        try(PreparedStatement stmt = getConnection().prepareStatement(queryString)) {
            stmt.setInt(1, book.getId());

            stmt.execute();
            getConnection().commit();
        }
        catch (SQLException ex){
            log.error("borrow book failed", ex);
        }
    }

    @Override
    public List<Book> findAvailableBooks() {
        String queryString = "select book_id, book_title from books where student_id is null";

        List<Book> result = new ArrayList<>();
        try(PreparedStatement stmt = getConnection().prepareStatement(queryString)) {
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                result.add(new Book(rs.getInt("book_id"), rs.getString("book_title")));
            }
        }
        catch (Exception ex){
            log.error("find available books failed", ex);
            result = null;
        }

        return result;
    }

    @Override
    public List<Student> getAllStudents() {

        String queryString = "select student_id, student_name from abonents";

        List<Student> result = new ArrayList<>();
        try(PreparedStatement stmt = getConnection().prepareStatement(queryString)) {
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                result.add(new Student(rs.getInt("student_id"), rs.getString("student_name")));
            }
        }
        catch (Exception ex){
            log.error("list all students failed", ex);
            result = null;
        }

        return result;
    }
}
