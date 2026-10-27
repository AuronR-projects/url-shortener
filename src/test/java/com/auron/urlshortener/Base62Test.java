package com.auron.urlshortener;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class Base62Test {
    
    @Test 
    void encodesZero() {
        assertEquals("0", Base62.encode(0));
    }

    @Test 
    void encodesSmallNumbers() {
        assertEquals("1", Base62.encode(1));
        assertEquals("a", Base62.encode(10));
        assertEquals("21", Base62.encode(125));
    }

    @Test 
    void encodesLargeNumbers() {
        assertEquals("4c92", Base62.encode(1_000_000));
    }

    @Test 
    void decodeReversesEncode() {
        for (long i = 0; i < 10_000; i++) {
            assertEquals(i, Base62.decode(Base62.encode(i)));
        }
    }

    @Test 
    void rejectNegativeNumbers() {
        assertThrows(IllegalArgumentException.class, () -> Base62.encode(-1));
    }

    @Test 
    void rejectsInvalidCharacters() {
        assertThrows(IllegalArgumentException.class, () -> Base62.decode("ab-c"));
    }
}

