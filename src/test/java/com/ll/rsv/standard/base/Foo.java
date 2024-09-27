package com.ll.rsv.standard.base;
import com.ll.rsv.global.scope.transaction.TransactionScope;
import org.springframework.stereotype.Component;
@TransactionScope
@Component
public class Foo {
    private final String name;
    public Foo() {
        this.name = "foo " + Math.random();
    }
    public String toString() {
        return this.name;
    }
}