package com.acme.api.dom.events;

public interface EventListener<T> {
    void onEvent(T event);
}
