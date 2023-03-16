package com.acme.app;

import com.acme.api.vdom.VNode;
import com.acme.api.vdom.VNodePrinter;

public class Main {

    public static void main(String[] args) {
        VNode root = new MyComponent().render();
        System.out.println(VNodePrinter.print(root));
    }
}
