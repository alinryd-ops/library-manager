package org.example;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Library library = new Library();
        boolean running = true;
        addSampleData(library);


        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim().toLowerCase();

            switch (choice) {
                case "1": {
                    String title = readText(scanner, "Title: ");
                    String author = readText(scanner, "Author: ");
                    library.addBook(title, author);
                    break;
                }
                case "2": {
                    String name = readText(scanner, "Name: ");
                    library.registerMember(name);
                    break;
                }
                case "3": {
                    String isbn = readText(scanner, "Book ISBN: ");
                    int memberId = readInt(scanner, "Member ID: ");
                    library.borrowBook(isbn, memberId);
                    break;
                }
                case "4": {
                    String isbn = readText(scanner, "Book ISBN: ");
                    library.returnBook(isbn);
                    break;
                }
                case "5": {
                    String query = readText(scanner, "Search (title or author): ");
                    library.searchBooks(query);
                    break;
                }
                case "6": {
                    library.showAllBooks();
                    break;
                }
                case "e": {
                    running = false;
                    IO.println("Goodbye!");
                    break;
                }
                default: {
                    IO.println("Invalid choice, please try again.");
                    break;
                }
            }
        }
        scanner.close();
    }

    private static void printMenu() {
        IO.println("");
        IO.println("Library Manager");
        IO.println("===============");
        IO.println("1. Add book");
        IO.println("2. Register member");
        IO.println("3. Borrow book");
        IO.println("4. Return book");
        IO.println("5. Search book (title or author)");
        IO.println("6. Show all books and status");
        IO.println("e. Exit");
        IO.print("Choose: ");
    }

    private static void addSampleData(Library library) {
        library.addBook("The Hobbit", "J.R.R. Tolkien");
        library.addBook("1984", "George Orwell");
        library.addBook("Pride and Prejudice", "Jane Austen");
        library.addBook("To Kill a Mockingbird", "Harper Lee");
        library.addBook("The Great Gatsby", "F. Scott Fitzgerald");
        library.addBook("Harry Potter and the Sorcerer's Stone", "J.K. Rowling");

        library.registerMember("Anna Svensson");
        library.registerMember("Erik Johansson");
        library.registerMember("Sara Lindqvist");
    }

    // Asks until the user enters something that is not blank
    private static String readText(Scanner scanner, String prompt) {
        while (true) {
            IO.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isBlank()) {
                return input;
            }
            IO.println("This field cannot be empty.");
        }
    }

    // Asks until the user enters a valid whole number
    private static int readInt(Scanner scanner, String prompt) {
        while (true) {
            IO.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                IO.println("Please enter a whole number, e.g. 1.");
            }
        }
    }
}
