package com.example.dop.demo;

import com.github.underscore.U;
import org.junit.jupiter.api.Test;
import java.util.*;

public class Test04 {

    @Test
    public void testLibraryStructuralSharing() {
        Map<String, Object> libraryData = U.fromJson("""
        {
          "catalog": {
            "booksByIsbn": {
              "978-1779501127": {
                "isbn": "978-1779501127",
                "title": "Watchmen",
                "publicactionYear": 1987
              }
            },
            "authorsById": {
              "alan-moore": {
                "name": "Alan Moore",
                "bookIsbns": ["978-1779501127"]
              }
            }
          }
        }
        """);

        // 1. 원본 객체들의 참조 저장
        Map<String, Object> catalog = (Map<String, Object>) libraryData.get("catalog");
        Map<String, Object> booksByIsbn = (Map<String, Object>) catalog.get("booksByIsbn");
        Map<String, Object> authorsById = (Map<String, Object>) catalog.get("authorsById");
        Map<String, Object> watchmen = (Map<String, Object>) booksByIsbn.get("978-1779501127");
        Map<String, Object> alanMoore = (Map<String, Object>) authorsById.get("alan-moore");

        System.out.println("=== Original References ===");
        System.out.println("Catalog: " + System.identityHashCode(catalog));
        System.out.println("BooksByIsbn: " + System.identityHashCode(booksByIsbn));
        System.out.println("AuthorsById: " + System.identityHashCode(authorsById));
        System.out.println("Watchmen: " + System.identityHashCode(watchmen));
        System.out.println("Alan Moore: " + System.identityHashCode(alanMoore));
        System.out.println("Original publicationYear: " + watchmen.get("publicactionYear"));

        // 2. ImmutableDataStore를 사용하여 publicationYear 업데이트
        ImmutableDataStore store = new ImmutableDataStore(libraryData);
        Map<String, Object> updatedData = store.set("catalog.booksByIsbn.978-1779501127.publicactionYear", 1986).getData();

        // 3. 업데이트된 객체들의 참조 가져오기
        Map<String, Object> updatedCatalog = (Map<String, Object>) updatedData.get("catalog");
        Map<String, Object> updatedBooksByIsbn = (Map<String, Object>) updatedCatalog.get("booksByIsbn");
        Map<String, Object> updatedAuthorsById = (Map<String, Object>) updatedCatalog.get("authorsById");
        Map<String, Object> updatedWatchmen = (Map<String, Object>) updatedBooksByIsbn.get("978-1779501127");
        Map<String, Object> updatedAlanMoore = (Map<String, Object>) updatedAuthorsById.get("alan-moore");

        System.out.println("\n=== Updated References ===");
        System.out.println("Updated Catalog: " + System.identityHashCode(updatedCatalog));
        System.out.println("Updated BooksByIsbn: " + System.identityHashCode(updatedBooksByIsbn));
        System.out.println("Updated AuthorsById: " + System.identityHashCode(updatedAuthorsById));
        System.out.println("Updated Watchmen: " + System.identityHashCode(updatedWatchmen));
        System.out.println("Updated Alan Moore: " + System.identityHashCode(updatedAlanMoore));
        System.out.println("Updated publicationYear: " + updatedWatchmen.get("publicactionYear"));

        System.out.println("\n=== Structural Sharing Verification ===");
        // 1. 변경된 경로상의 객체들은 새로운 참조여야 함
        System.out.println("Catalog changed: " + (catalog != updatedCatalog));
        System.out.println("BooksByIsbn changed: " + (booksByIsbn != updatedBooksByIsbn));
        System.out.println("Watchmen changed: " + (watchmen != updatedWatchmen));

        // 2. 변경되지 않은 객체들은 동일한 참조를 공유해야 함
        System.out.println("AuthorsById shared: " + (authorsById == updatedAuthorsById));
        System.out.println("Alan Moore shared: " + (alanMoore == updatedAlanMoore));

        // 3. 변경되지 않은 값들은 동일해야 함
        System.out.println("\n=== Data Verification ===");
        System.out.println("Title unchanged: " + 
            watchmen.get("title").equals(updatedWatchmen.get("title")));
        System.out.println("Author name unchanged: " + 
            alanMoore.get("name").equals(updatedAlanMoore.get("name")));
    }

    @Test
    public void testUpdateBookItemId() {
        Map<String, Object> libraryData = U.fromJson("""
        {
          "catalog": {
            "booksByIsbn": {
              "978-1779501127": {
                "isbn": "978-1779501127",
                "title": "Watchmen",
                "publicactionYear": 1987,
                "authorIds": ["alan-moore", "dave-gibbons"],
                "bookItems": [
                  {
                    "id": "book-item-1",
                    "libId": "nyc-central-lib",
                    "isLent": true
                  },
                  {
                    "id": "book-item-2",
                    "libId": "nyc-central-lib",
                    "isLent": false
                  }
                ]
              }
            }
          }
        }
        """);

        libraryData = Collections.unmodifiableMap(libraryData);

        // 1. 원본 객체들의 참조 저장
        Map<String, Object> catalog = (Map<String, Object>) libraryData.get("catalog");
        Map<String, Object> booksByIsbn = (Map<String, Object>) catalog.get("booksByIsbn");
        Map<String, Object> watchmen = (Map<String, Object>) booksByIsbn.get("978-1779501127");
        List<Map<String, Object>> bookItems = (List<Map<String, Object>>) watchmen.get("bookItems");
        Map<String, Object> firstBookItem = bookItems.get(0);

        System.out.println("=== Original References ===");
        System.out.println("Catalog: " + System.identityHashCode(catalog));
        System.out.println("BooksByIsbn: " + System.identityHashCode(booksByIsbn));
        System.out.println("Watchmen: " + System.identityHashCode(watchmen));
        System.out.println("BookItems: " + System.identityHashCode(bookItems));
        System.out.println("First BookItem: " + System.identityHashCode(firstBookItem));
        System.out.println("Original first book item ID: " + firstBookItem.get("id"));

        // 2. ImmutableDataStore를 사용하여 첫 번째 book item의 id 업데이트
        ImmutableDataStore store = new ImmutableDataStore(libraryData);
        Map<String, Object> updatedData = store.set("catalog.booksByIsbn.978-1779501127.bookItems.0.id", "book-item-3").getData();

        // 3. 업데이트된 객체들의 참조 가져오기
        Map<String, Object> updatedCatalog = (Map<String, Object>) updatedData.get("catalog");
        Map<String, Object> updatedBooksByIsbn = (Map<String, Object>) updatedCatalog.get("booksByIsbn");
        Map<String, Object> updatedWatchmen = (Map<String, Object>) updatedBooksByIsbn.get("978-1779501127");
        List<Map<String, Object>> updatedBookItems = (List<Map<String, Object>>) updatedWatchmen.get("bookItems");
        Map<String, Object> updatedFirstBookItem = updatedBookItems.get(0);

        System.out.println("\n=== Updated References ===");
        System.out.println("Updated Catalog: " + System.identityHashCode(updatedCatalog));
        System.out.println("Updated BooksByIsbn: " + System.identityHashCode(updatedBooksByIsbn));
        System.out.println("Updated Watchmen: " + System.identityHashCode(updatedWatchmen));
        System.out.println("Updated BookItems: " + System.identityHashCode(updatedBookItems));
        System.out.println("Updated First BookItem: " + System.identityHashCode(updatedFirstBookItem));
        System.out.println("Updated first book item ID: " + updatedFirstBookItem.get("id"));

        System.out.println("\n=== Structural Sharing Verification ===");
        // 1. 변경된 경로상의 객체들은 새로운 참조여야 함
        System.out.println("Catalog changed: " + (catalog != updatedCatalog));
        System.out.println("BooksByIsbn changed: " + (booksByIsbn != updatedBooksByIsbn));
        System.out.println("Watchmen changed: " + (watchmen != updatedWatchmen));
        System.out.println("BookItems changed: " + (bookItems != updatedBookItems));
        System.out.println("First BookItem changed: " + (firstBookItem != updatedFirstBookItem));

        // 2. 두 번째 book item은 변경되지 않았으므로 동일한 참조여야 함
        System.out.println("Second BookItem shared: " + (bookItems.get(1) == updatedBookItems.get(1)));

        // 3. 변경되지 않은 값들은 동일해야 함
        System.out.println("\n=== Data Verification ===");
        System.out.println("Title unchanged: " + 
            watchmen.get("title").equals(updatedWatchmen.get("title")));
        System.out.println("Second book item unchanged: " + 
            bookItems.get(1).equals(updatedBookItems.get(1)));
        System.out.println("First book item libId unchanged: " + 
            firstBookItem.get("libId").equals(updatedFirstBookItem.get("libId")));
    }
}

/*
Map<String, Object> libraryData = U.fromJson("""
        {
          "catalog": {
            "booksByIsbn": {
              "978-1779501127": {
                "isbn": "978-1779501127",
                "title": "Watchmen",
                "publicactionYear": 1987,
                "authorIds": ["alan-moore", "dave-gibbons"],
                "bookItems": [
                  {
                    "id": "book-item-1",
                    "libId": "nyc-central-lib",
                    "isLent": true
                  },
                  {
                    "id": "book-item-2",
                    "libId": "nyc-central-lib",
                    "isLent": false
                  }
                ]
              }
            },
            "authorsById": {
              "alan-moore": {
                "name": "Alan Moore",
                "bookIsbns": ["978-1779501127"]
              },
              "dave-gibbons": {
                "name": "Dave Gibbons",
                "bookIsbns": ["978-1779501127"]
              }
            }
          },
          "userManagement":{
              "librariansByEmail": {
                  "franck@gmail.com" : {
                      "email": "franck@gmail.com",
                      "encryptedPassword": "bXlwYXNzd29yZA=="
                  }
              },
              "membersByEmail": {
                  "samantha@gmail.com": {
                      "email": "samantha@gmail.com",
                      "encryptedPassword": "c2VjcmV0",
                      "isBlocked": false,
                      "bookLendings": [
                          {
                              "bookItemId": "book-item-1",
                              "bookIsbn": "978-1779501127",
                              "lendingDate": "2020-04-23"
                          }
                      ]
                  }
              }
          }
        }
        """);
 */