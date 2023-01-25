package com.ocp7bibliotheque.bibliothequebook.Services;

import com.ocp7bibliotheque.bibliothequebook.Entites.Book;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface IBookService {
    public void removeBook(int idBook) throws Exception;
    public Book addBook(int idLibrary,Book book) throws Exception;
    public Book modifyBook(int idBook, int numberExemplar) throws Exception;
    public List<Book> searchBook(String name, String author) throws Exception;
    //public LocalDateTime nextReturn(int idBook) throws Exception;
}
