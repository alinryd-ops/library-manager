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

    public void registerMember(String name) {
        if (memberCount >= members.length) {
            IO.println("Too many members, can't add more members");
            return;
        }

        Member newMember = new Member(nextMemberId, name);
        members[memberCount] = newMember;
        memberCount++;
        nextMemberId++;
        IO.println("Member id number is " + newMember.getId());
    }

    public void borrowBook(String isbn, int memberId) {
        int bookIndex = -1;
        for (int i = 0; i < bookCount; i++) {
            if (books[i].isbn().equalsIgnoreCase(isbn)) {
                bookIndex = i;
                break;
            }
        }

        Member foundMember = null;
        for (int i = 0; i < memberCount; i++) {
            if (members[i].getId() == memberId) {
                foundMember = members[i];
                break;
            }
        }

        if (bookIndex == -1) {
            IO.println("Book not found");
            return;
        }

        if (foundMember == null) {
            IO.println("Member not found");
            return;
        }

        if (borrowedby[bookIndex] != -1) {
            IO.println("This book is already borrowed");
            return;
        }

        if (!foundMember.canBorrow()) {
            IO.println("This member has reached the maximum number of loans");
            return;
        }

        borrowedby[bookIndex] = memberId;
        foundMember.increaseLoans();
        IO.println("Book borrowed successfully");
    }

}
