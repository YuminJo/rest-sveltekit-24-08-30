package com.ll.rsv.global.transactionCache;
import com.ll.rsv.global.scope.transaction.TransactionScope;
import lombok.experimental.Delegate;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;
@TransactionScope
@Component
public class TransactionCache {
    @Delegate
    private final Map<String, Object> data = new HashMap<>();
    public <T> T get(String key) {
        return (T) data.get(key);
    }
}