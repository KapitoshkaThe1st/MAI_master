package org.mai.dep210.collections.auction;

public class DuplicateProductException extends RuntimeException {
    public DuplicateProductException(String message){
        super(message);
    }
}
