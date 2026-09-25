package org.example;

public class Library {

    private Book[] books;
    private Member[] members;
    private int[] borrowedBy;

    private int bookCount;
    private int memberCount;
    private int nextMemberId;
    private int nextIsbn;

    public Library() {
        books = new Book[6];
        members = new Member[3];
        borrowedBy = new int[6];

        bookCount = 0;
        memberCount = 0;
        nextMemberId = 1;
        nextIsbn = 101;

        for (int i = 0; i < borrowedBy.length; i++) {
            borrowedBy[i] = -1;
        }
    }

    public void addBook(String title, String author) {
        if (bookCount >= books.length) {
            growBooks();
        }

        Book newBook = new Book(String.valueOf(nextIsbn), title, author);
        books[bookCount] = newBook;
        borrowedBy[bookCount] = -1;
        bookCount++;
        nextIsbn++;
        IO.println("Book added with ISBN " + newBook.isbn());
    }

    public void registerMember(String name) {
        if (memberCount >= members.length) {
            growMembers();
        }

        Member newMember = new Member(nextMemberId, name);
        members[memberCount] = newMember;
        memberCount++;
        nextMemberId++;
        IO.println("Member id number is " + newMember.getId());
    }

    public void borrowBook(String isbn, int memberId) {

        int bookIndex = findBookIndex(isbn);
        Member foundMember = findMember(memberId);


        if (bookIndex == -1) {
            IO.println("Book not found");
            return;
        }

        if (foundMember == null) {
            IO.println("Member not found");
            return;
        }

        if (borrowedBy[bookIndex] != -1) {
            IO.println("This book is already borrowed");
            return;
        }

        if (!foundMember.canBorrow()) {
            IO.println("This member has reached the maximum number of loans");
            return;
        }

        borrowedBy[bookIndex] = memberId;
        foundMember.increaseLoans();
        IO.println("Book borrowed successfully");
    }

    public void returnBook(String isbn) {

        int bookIndex = findBookIndex(isbn);


        if (bookIndex == -1) {
            IO.println("Book not found");
            return;
        }

        if (borrowedBy[bookIndex] == -1) {
            IO.println("This book is not currently borrowed");
            return;
        }

        int memberId = borrowedBy[bookIndex];
        Member foundMember = findMember(memberId);


        borrowedBy[bookIndex] = -1;

        if (foundMember != null) {
            foundMember.decreaseLoans();
        }

        IO.println("Book returned successfully");
    }



    public void searchBooks(String query) {
        if (query == null || query.isBlank()) {
            IO.println("You need to enter a query!");
            return;
        }

        String q = query.toLowerCase();
        boolean found = false;

        for (int i = 0; i < bookCount; i++) {
            String title = books[i].title().toLowerCase();
            String author = books[i].author().toLowerCase();

            if (title.contains(q) || author.contains(q)) {
                printBookWithStatus(i);
                found = true;
            }
        }

        if (!found) {
            IO.println("No books matched with \"" + query + "\".");
        }
    }

    public void showAllBooks () {
        if (bookCount == 0) {
            IO.println("There are no books in the Library");
        }
        IO.println("All books");
        for (int i = 0; i < bookCount; i++) {
            printBookWithStatus(i);
        }
    }

    public void showTopBorrower() {
        if (memberCount == 0) {
            IO.println("There are no members yet.");
            return;
        }

        Member top = members[0];
        for (int i = 1; i < memberCount; i++) {
            if (members[i].getActiveLoans() > top.getActiveLoans()) {
                top = members[i];
            }
        }

        if (top.getActiveLoans() == 0) {
            IO.println("No member has any active loans right now.");
            return;
        }

        IO.println("Member with most active loans: " + top.getName()
                + " (ID " + top.getId() + ") with " + top.getActiveLoans() + " loan(s)");
    }

    private int findBookIndex(String isbn) {
        for (int i = 0; i < bookCount; i++) {
            if (books[i].isbn().equalsIgnoreCase(isbn)) {
                return i;
            }
        }
        return -1;
    }

    private Member findMember(int memberId) {
        for (int i = 0; i < memberCount; i++) {
            if (members[i].getId() == memberId) {
                return members[i];
            }
        }
        return null;
    }

    private void printBookWithStatus(int index) {
        Book book = books[index];
        int borrowerId = borrowedBy[index];

        String status;
        if (borrowerId == -1) {
            status = "Available";
        } else {
            Member borrower = findMember(borrowerId);
            if (borrower != null) {
                status = "Borrowed by " + borrower.getName();
            } else {
                status = "Borrowed by unknown member";
            }
        }

        IO.println(book.title() + " by " + book.author() + " (ISBN " + book.isbn() + ") - " + status);
    }

    private void growBooks() {
        int newSize = books.length * 2;
        Book[] newBooks = new Book[newSize];
        int[] newBorrowedBy = new int[newSize];

        for (int i = 0; i < newSize; i++) {
            newBorrowedBy[i] = -1;
        }

        for (int i = 0; i < bookCount; i++) {
            newBooks[i] = books[i];
            newBorrowedBy[i] = borrowedBy[i];
        }

        books = newBooks;
        borrowedBy = newBorrowedBy;
        IO.println("Book storage expanded to " + newSize + " slots");
    }

    private void growMembers() {
        int newSize = members.length * 2;
        Member[] newMembers = new Member[newSize];

        for (int i = 0; i < memberCount; i++) {
            newMembers[i] = members[i];
        }

        members = newMembers;
        IO.println("Member storage expanded to " + newSize + " slots");
    }

}
