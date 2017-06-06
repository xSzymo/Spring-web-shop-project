package com.shop.data.services;

import com.shop.controllers.administratorSite.books.file.FileUploadActions;
import com.shop.data.repositories.BooksRepository;
import com.shop.data.repositories.CategoriesRepository;
import com.shop.data.repositories.OrdersRepository;
import com.shop.data.tables.Book;
import com.shop.data.tables.Category;
import com.shop.data.tables.Order;
import com.shop.data.tables.Picture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Iterator;

@Service
@Transactional
public class BooksService {
	@Autowired
	private BooksRepository repository;
	@Autowired
	private OrdersRepository ordersRepository;
	@Autowired
	private BooksRepository booksRepository;
	@Autowired
	private CategoriesRepository categoriesRepository;

	public void save(Book book) {
		if (book != null)
			if (book.getCategory() != null)
				repository.save(book);
	}

	public void save(Collection<Book> books) {
		if (books.size() > 0)
			books.forEach(
					x -> {
						if (x != null)
							save(x);
					});
	}

	public Book findOne(long id) {
		return repository.findOne(id);
	}

	public Book findOne(Book book) {
		return repository.findOne(book.getId());
	}

	public Iterable<Book> findAll() {
		return repository.findAll();
	}

	public void delete(long id) {
		Book foundBook = booksRepository.findById(id);
		deleteOperation(foundBook);
	}

	public void delete(Book book) {
		delete(book.getId());
	}

	public void delete(Collection<Book> book) {
		if (book.size() > 0)
			book.forEach(x -> {
				if(x.getId() != null)
					delete(x.getId());
			});
	}

	private void deleteOperation(Book book) {
		if (book == null)
			return;

		Iterable<Order> orders = ordersRepository.findAll();

		for (Iterator<Order> iterator = orders.iterator(); iterator.hasNext(); ) {
			Order x = iterator.next();
			for (Iterator<Book> iterator2 = x.getBooks().iterator(); iterator2.hasNext(); ) {
				Book x1 = iterator2.next();
				if (x1.getId() == book.getId()) {
					iterator2.remove();
					ordersRepository.save(x);
				}
			}
		}

		Iterable<Category> categories = categoriesRepository.findAll();

		for (Iterator<Category> iterator = categories.iterator(); iterator.hasNext(); ) {
			Category x2 = iterator.next();
			for (Iterator<Book> iterator2 = x2.getBooks().iterator(); iterator2.hasNext(); ) {
				Book x3 = iterator2.next();
				if (x3.getId() == book.getId()) {
					iterator2.remove();
					categoriesRepository.save(x2);
				}
			}
		}

		for (Picture x : book.getPictures())
			FileUploadActions.deletePicture(x.getName());
		repository.delete(book.getId());

	}
}