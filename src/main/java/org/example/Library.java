package org.example;

public class Library {

    private Book[] books;
    private Member[] members;
    private int[] borrowedby;

    private int bookCount;
    private int memberCount;
    private int nextMemberId;

    public Library() {
        books = new Book[30];
        members = new Member[10];
        borrowedby = new int[30];

        bookCount = 0;
        memberCount = 0;
        nextMemberId = 1;

        for (int i = 0; i < borrowedby.length; i++) {
            borrowedby[i] = -1;
        }
    }

    public void addBook(Book book) {
        if (bookCount >= books.length) {
            IO.println("The Library is full, can't add more books");
            return;
        }

        books[bookCount] = book;
        borrowedby[bookCount] = -1;
        bookCount++;
        IO.println("Book is added to the Library");
    }
}
