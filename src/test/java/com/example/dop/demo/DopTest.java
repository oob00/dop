package com.example.dop.demo;

import com.github.underscore.U;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DopTest {

    @Test
    public void test() {
        Map<String, Object> watchmen = Map.<String, Object>of(
                "isbn","978-1779501127",
                "title", "Watchmen",
                "publicactionYear", 1987,
                "authors", List.of("alan-moore", "dave-gibbons"),
                "bookItems", List.of(
                        Map.of(
                                "id", "book-item-1",
                                "libId","nyc-central-lib",
                                "isLent", true
                        ),
                        Map.of(
                                "id", "book-item-2",
                                "libId","nyc-central-lib",
                                "isLent", false
                        )
                )
        );

        System.out.println(watchmen);
        System.out.println(U.get(watchmen, "authors").toString());
        System.out.println(U.get(watchmen, List.of("authors", "1")).toString());
        System.out.println(U.get(watchmen, "bookItems.1.id").toString());
    }

    @Test
    public void test2() {
        Map<String, Object> catalogData = Map.of(
                "booksByIsbn", Map.of(
                        "978-1779501127", Map.of(
                                "isbn","978-1779501127",
                                "title", "Watchmen",
                                "publicactionYear", 1987,
                                "authorIds", List.of("alan-moore", "dave-gibbons"),
                                "bookItems", List.of(
                                        Map.of(
                                                "id", "book-item-1",
                                                "libId","nyc-central-lib",
                                                "isLent", true
                                        ),
                                        Map.of(
                                                "id", "book-item-2",
                                                "libId","nyc-central-lib",
                                                "isLent", false
                                        )
                                )
                        )
                ),
                "authorsById", Map.of(
                        "alan-moore", Map.of(
                                "name", "Alan Moore",
                                "bookIsbns", List.of("978-1779501127")
                        ),
                        "dave-gibbons", Map.of(
                                "name", "Dave Gibbons",
                                "bookIsbns", List.of("978-1779501127")
                        )
                )
        );

        System.out.println( U.get( catalogData, "booksByIsbn.978-1779501127.title" ).toString() ); // Watchmen
        System.out.println( U.get( catalogData, List.of("booksByIsbn","978-1779501127","title" ) ).toString() ); // Watchmen
        System.out.println( U.map( List.of("alan-moore","dave-gibbons"), authorId -> U.get(catalogData, List.of("authorsById", authorId, "name") ) ) ); // [Alan Moore, Dave Gibbons]

        Map<String, Object> book = U.get(catalogData,"booksByIsbn.978-1779501127");

        System.out.println(book);

        System.out.println(Catalog.bookInfo(catalogData,book));

    }
}

class Catalog{

    static List<String> authorNames(Map<String,Object> catalogData, Map<String, Object> book){
        List<String> authorIds = U.get(book,"authorIds");
        return U.map( authorIds, authorId -> U.get(catalogData, List.of("authorsById", authorId, "name")) );
    }

    static Map<String,Object> bookInfo(Map<String,Object> catalogData, Map<String, Object> book){

        return Map.of(
                "title", U.get(book,"title"),
                "isbn", U.get(book,"isbn"),
                "authorNames", authorNames(catalogData,book)
        );
    }
}
