package com.ll.rsv.global.scope.transaction;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
public class TransactionScopeImpl implements Scope {
    private final Map<String, Map<String, Object>> scopedObjects = new HashMap<>();
    private final Map<String, String> transactionIds = new HashMap<>();
    @Override
    public Object get(String name, ObjectFactory<?> objectFactory) {
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            throw new IllegalStateException("No active transaction");
        }
        String transactionId = transactionIds.computeIfAbsent(
                TransactionSynchronizationManager.getCurrentTransactionName(),
                k -> UUID.randomUUID().toString()
        );
        scopedObjects.putIfAbsent(transactionId, new HashMap<>());
        Map<String, Object> objectsInScope = scopedObjects.get(transactionId);
        if (!objectsInScope.containsKey(name)) {
            objectsInScope.put(name, objectFactory.getObject());
            registerDestruction(transactionId, name);
        }
        return objectsInScope.get(name);
    }
    private void registerDestruction(String transactionId, String beanName) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                Map<String, Object> objects = scopedObjects.get(transactionId);
                if (objects != null) {
                    objects.remove(beanName);
                    if (objects.isEmpty()) {
                        scopedObjects.remove(transactionId);
                        transactionIds.remove(TransactionSynchronizationManager.getCurrentTransactionName());
                    }
                }
            }
        });
    }
    @Override
    public Object remove(String name) {
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            return null;
        }
        String transactionId = transactionIds.get(TransactionSynchronizationManager.getCurrentTransactionName());
        if (transactionId != null && scopedObjects.containsKey(transactionId)) {
            return scopedObjects.get(transactionId).remove(name);
        }
        return null;
    }
    @Override
    public void registerDestructionCallback(String name, Runnable callback) {
        // Destruction callback is managed by the TransactionSynchronization
    }
    @Override
    public Object resolveContextualObject(String key) {
        return null;
    }
    @Override
    public String getConversationId() {
        return TransactionSynchronizationManager.getCurrentTransactionName();
    }
}