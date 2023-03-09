package com.acme.codegen;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleElementVisitor9;
import javax.lang.model.util.SimpleTypeVisitor9;
import javax.lang.model.util.Types;

/**
 * Type information.
 */
@SuppressWarnings("unused")
public final class TypeInfo implements Comparable<TypeInfo> {

    private static final Map<String, TypeInfo> ELEMENT_CACHE = new HashMap<>();
    private static final IsPrimitiveVisitor IS_PRIMITIVE_VISITOR = new IsPrimitiveVisitor();
    private static final IsNotTypeVisitor IS_NOT_TYPE_VISITOR = new IsNotTypeVisitor();

    private final TypeElement typeElement;
    private final Element element;
    private final Types types;
    private final String pkg;
    private final String qualifiedName;
    private final String simpleName;
    private String typeName;
    private TypeInfo[] typeParams;

    private TypeInfo(TypeElement element, Types types) {
        this.element = element;
        this.typeElement = element;
        this.types = types;
        qualifiedName = element.getQualifiedName().toString();
        simpleName = element.getSimpleName().toString();
        Element enclosingElement = element.getEnclosingElement();
        while (enclosingElement.getKind() != ElementKind.PACKAGE) {
            enclosingElement = enclosingElement.getEnclosingElement();
        }
        pkg = ((PackageElement) enclosingElement).getQualifiedName().toString();
    }

    private TypeInfo(VariableElement variableElement, Types types) {
        TypeMirror varType = variableElement.asType();
        boolean primitive = varType.accept(IS_PRIMITIVE_VISITOR, null);
        TypeElement type;
        if (primitive) {
            type = types.boxedClass((PrimitiveType) varType);
        } else {
            type = (TypeElement) types.asElement(variableElement.asType());
        }
        if (type == null) {
            throw new IllegalStateException("Unable to resolve type for variable: " + variableElement);
        }
        this.types = types;
        element = variableElement;
        typeElement = type;
        qualifiedName = type.getQualifiedName().toString();
        simpleName = type.getSimpleName().toString();
        pkg = resolvePackage(type);
    }

    /**
     * Get a {@link TypeInfo} for a given {@link Element}.
     *
     * @param element element to process
     * @param types   types processing utils
     * @return ElementInfo
     */
    static TypeInfo of(Element element, Types types) {
        if (element instanceof TypeElement) {
            return of((TypeElement) element, types);
        } else if (element instanceof VariableElement) {
            return of((VariableElement) element, types);
        }
        throw new IllegalArgumentException("Unsupported element type: " + element);
    }

    /**
     * Get a {@link TypeInfo} for a given {@link TypeElement}.
     *
     * @param typeElement type element to process
     * @param types       types processing utils
     * @return ElementInfo
     */
    static TypeInfo of(TypeElement typeElement, Types types) {
        Objects.requireNonNull(typeElement, "typeElement is null");
        Objects.requireNonNull(types, "types is null");
        String key = typeElement.getQualifiedName().toString();
        return ELEMENT_CACHE.computeIfAbsent(key, k -> new TypeInfo(typeElement, types));
    }

    /**
     * Get a {@link TypeInfo} for a given {@link VariableElement}.
     *
     * @param variableElement variable element to process
     * @param types           types processing utils
     * @return ElementInfo
     */
    static TypeInfo of(VariableElement variableElement, Types types) {
        Objects.requireNonNull(variableElement, "variableElement is null");
        Objects.requireNonNull(types, "types is null");
        String key = cacheKey(variableElement);
        return ELEMENT_CACHE.computeIfAbsent(key, k -> new TypeInfo(variableElement, types));
    }

    /**
     * Test if this type qualified name is equal to the given class name.
     *
     * @param aClass class to be compared
     * @return {@code true} if equal, {@code false} otherwise
     */
    public boolean is(Class<?> aClass) {
        if (qualifiedName().equals(aClass.getTypeName())) {
            return true;
        }
        if (isEnum()) {
            return superClass().map(s -> s.is(aClass)).orElse(false);
        }
        return false;
    }

    /**
     * Test if this type is compatible with any of the given classes (equal, interface, superclass).
     *
     * @param classes classes to be compared
     * @return {@code true} if equal, {@code false} otherwise
     */
    boolean is(List<Class<?>> classes) {
        for (Class<?> aClass : classes) {
            if (is(aClass)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get a flat list of all the types represented by this instance.
     *
     * @return list of type info
     */
    public List<TypeInfo> allTypes() {
        List<TypeInfo> allTypes = new LinkedList<>();
        allTypes.add(this);
        allTypes.addAll(Arrays.asList(interfaces()));
        allTypes.addAll(superClass().stream().flatMap(s -> s.allTypes().stream()).toList());
        return allTypes;
    }

    /**
     * Search this type and its parent for the given interface.
     *
     * @param aClass interface class to look for
     * @return {@code true} if found, {@code} false otherwise
     */
    public boolean hasInterface(Class<?> aClass) {
        return allTypes().stream().anyMatch(t -> t.is(aClass));
    }

    /**
     * Get the described {@link Element}.
     *
     * @return Element
     */
    public Element element() {
        return element;
    }

    /**
     * Get the package of this type.
     *
     * @return package
     */
    public String pkg() {
        return pkg;
    }

    /**
     * Get the qualified name of this type.
     *
     * @return type qualified name
     */
    public String qualifiedName() {
        return qualifiedName;
    }

    /**
     * Get the type name.
     *
     * @return type name
     */
    public String typeName() {
        if (typeName != null) {
            return typeName;
        }
        typeName = pkg + "." + simpleName;
        if (isArray()) {
            typeName += "[]";
        }
        return typeName;
    }

    /**
     * Get the simple name of this type.
     *
     * @return type simple name
     */
    public String simpleName() {
        return simpleName;
    }

    /**
     * Indicate if this type describes a primitive type.
     *
     * @return {@code true} if primitive, {@code false} otherwise
     */
    public boolean isPrimitive() {
        return typeElement.asType().accept(new IsPrimitiveVisitor(), null);
    }

    /**
     * Indicate if this type describes an array type.
     *
     * @return {@code true} if array, {@code false} otherwise
     */
    public boolean isArray() {
        return typeElement.asType().getKind() == TypeKind.ARRAY;
    }

    /**
     * Indicate if this type describes an interface.
     *
     * @return {@code true} if interface, {@code false} otherwise
     */
    public boolean isInterface() {
        return typeElement.getKind() == ElementKind.INTERFACE;
    }


    /**
     * Indicate if this type describes an annotation.
     *
     * @return {@code true} if annotation, {@code false} otherwise
     */
    public boolean isAnnotation() {
        return typeElement.getKind() == ElementKind.ANNOTATION_TYPE;
    }

    /**
     * Indicate if this type describes an enum type.
     *
     * @return {@code true} if enum, {@code false} otherwise
     */
    public boolean isEnum() {
        return typeElement.getKind() == ElementKind.ENUM;
    }

    /**
     * Get the super class.
     *
     * @return optional of TypeInfo
     */
    public Optional<TypeInfo> superClass() {
        TypeMirror superClassMirror = typeElement.getSuperclass();
        if (superClassMirror.getKind() == TypeKind.NONE) {
            return Optional.empty();
        }
        return Optional.of(TypeInfo.of((TypeElement) types.asElement(superClassMirror), types));
    }

    /**
     * Get the type interfaces.
     *
     * @return TypeInfo[]
     */
    public TypeInfo[] interfaces() {
        return typeElement.getInterfaces()
                          .stream()
                          .map(types::asElement)
                          .map(element -> TypeInfo.of((TypeElement) element, types))
                          .toArray(TypeInfo[]::new);
    }

    /**
     * Get type info for the type parameters.
     *
     * @return TypeInfo[]
     */
    public TypeInfo[] typeParams() {
        if (typeParams != null) {
            return typeParams;
        }
        typeParams = element.asType().accept(new TypeParamVisitor(), null);
        return typeParams;
    }

    /**
     * Get the flattened list of all param types.
     *
     * @return list of TypeInfo
     */
    public List<TypeInfo> allTypeParams() {
        List<TypeInfo> types = new ArrayList<>();
        visit(this, new TypeInfoVisitor() {
            @Override
            public void entering(TypeInfoNode node) {
                types.add(node.type);
            }
        });
        return types;
    }

    /**
     * Get the declaration string for this type.
     *
     * @return String
     */
    public String decl() {
        StringBuilder sb = new StringBuilder();
        visit(this, new TypeInfoVisitor() {
            @Override
            public void entering(TypeInfoNode node) {
                sb.append(node.type.simpleName());
                if (node.type.typeParams.length > 0) {
                    sb.append("<");
                }
                int index = node.index();
                if (index >= 0 && index + 1 < node.siblings()) {
                    sb.append(", ");
                }
            }

            @Override
            public void leaving(TypeInfoNode node) {
                if (node.type.typeParams.length > 0) {
                    sb.append(">");
                }
            }
        });
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypeInfo that = (TypeInfo) o;
        return qualifiedName.equals(that.qualifiedName) && Arrays.equals(typeParams, that.typeParams);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(qualifiedName);
        result = 31 * result + Arrays.hashCode(typeParams());
        return result;
    }

    @Override
    public String toString() {
        return qualifiedName;
    }

    @Override
    public int compareTo(TypeInfo o) {
        return qualifiedName().compareTo(o.qualifiedName());
    }

    private static String cacheKey(VariableElement variableElement) {
        StringBuilder key = new StringBuilder(variableElement.toString());
        Element element = variableElement.getEnclosingElement();
        while (element.accept(IS_NOT_TYPE_VISITOR, null)) {
            key.insert(0, element + ".");
            element = element.getEnclosingElement();
        }
        key.insert(0, element + ".");
        return key.toString();
    }

    private static String resolvePackage(TypeElement type) {
        Element enclosingElement = type.getEnclosingElement();
        while (enclosingElement.getKind() != ElementKind.PACKAGE) {
            enclosingElement = enclosingElement.getEnclosingElement();
        }
        return ((PackageElement) enclosingElement).getQualifiedName().toString();
    }

    private static void visit(TypeInfo type, TypeInfoVisitor visitor) {
        Deque<TypeInfoNode> stack = new ArrayDeque<>();
        int[] nextId = new int[]{0};
        stack.push(new TypeInfoNode(null, ++nextId[0], type));
        int parentId = 0;
        while (!stack.isEmpty()) {
            TypeInfoNode node = stack.peek();
            TypeInfoNode parent = node.parent;
            if (node.id() == parentId) {
                // leaving
                parentId = parent != null ? parent.id() : 0;
                stack.pop();
                visitor.leaving(node);
            } else {
                visitor.entering(node);
                TypeInfo[] params = node.type.typeParams();
                if (params.length > 0) {
                    // entering node
                    for (int i = params.length - 1; i >= 0; i--) {
                        stack.push(new TypeInfoNode(node, ++nextId[0], params[i]));
                    }
                } else {
                    // leaf
                    parentId = parent != null ? parent.id() : 0;
                    stack.pop();
                }
            }
        }
    }

    private interface TypeInfoVisitor {
        default void entering(TypeInfoNode node) {
        }

        default void leaving(TypeInfoNode node) {
        }
    }

    private record TypeInfoNode(TypeInfoNode parent, int id, TypeInfo type) {

        int index() {
            if (parent != null) {
                TypeInfo[] params = parent.type.typeParams();
                for (int i = 0; i < params.length; i++) {
                    if (params[i] == type) {
                        return i;
                    }
                }
            }
            return -1;
        }

        int siblings() {
            return parent != null ? parent.type.typeParams().length : 0;
        }
    }

    private static final class IsNotTypeVisitor extends SimpleElementVisitor9<Boolean, Void> {

        @Override
        protected Boolean defaultAction(Element e, Void v) {
            return true;
        }

        @Override
        public Boolean visitType(TypeElement e, Void unused) {
            return false;
        }
    }

    private static final class IsPrimitiveVisitor extends SimpleTypeVisitor9<Boolean, Void> {

        @Override
        protected Boolean defaultAction(TypeMirror e, Void v) {
            return false;
        }

        @Override
        public Boolean visitPrimitive(PrimitiveType t, Void v) {
            return true;
        }
    }

    private final class TypeParamVisitor extends SimpleTypeVisitor9<TypeInfo[], Void> {

        TypeInfo paramTypeInfo(TypeMirror type) {
            Element element;
            if (type.getKind() == TypeKind.TYPEVAR) {
                element = types.asElement(((javax.lang.model.type.TypeVariable) type).getUpperBound());
            } else {
                element = types.asElement(type);
            }
            return TypeInfo.of((TypeElement) element, types);
        }

        @Override
        public TypeInfo[] visitDeclared(DeclaredType type, Void p) {
            return type.getTypeArguments().stream()
                       .map(this::paramTypeInfo)
                       .toArray(TypeInfo[]::new);
        }
    }
}
