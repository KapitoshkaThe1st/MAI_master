package org.mai.dep210.library;

import java.util.List;

public interface Library {
    /* Регистрация новой книги */
    void addNewBook(Book book) throws InvalidLibraryOperationException;

    /* Добавление нового абонента */
    void addAbonent(Student student) throws InvalidLibraryOperationException;

    /* Студент берет книгу */
    void borrowBook(Book book, Student student) throws InvalidLibraryOperationException;

    /* Студент возвращает книгу */
    void returnBook(Book book, Student student) throws InvalidLibraryOperationException;

    /* Получить список свободных книг */
    List<Book> findAvailableBooks();

    /* Список всех записанных в библиотеку студентов*/
    List<Student> getAllStudents();
}
