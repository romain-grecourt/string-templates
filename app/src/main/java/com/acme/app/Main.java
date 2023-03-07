package com.acme.app;

import com.acme.api.vdom.VNode;

public class Main {

    public static void main(String[] args) {
        VNode root = new MyComponent().render();
        // TODO traverse root to print
        System.out.println(root);
    }
}
