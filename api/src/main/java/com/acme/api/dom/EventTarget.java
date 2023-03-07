package com.acme.api.dom;

import com.acme.api.dom.events.EventListener;
import com.acme.api.dom.events.KeyboardEvent;
import com.acme.api.dom.events.MouseEvent;

public interface EventTarget {

    void addKeyUpListener(EventListener<KeyboardEvent> listener);
    void addClickListener(EventListener<MouseEvent> listener);
}
