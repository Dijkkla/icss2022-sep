package nl.han.ica.datastructures;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class HANStack<T> implements IHANStack<T> {
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private HANStackElement<T> top;

    @Override
    public void push(T value) {
        HANStackElement<T> element = new HANStackElement<>(value);
        element.setNext(top);
        top = element;
    }

    @Override
    public T pop() {
        if (top == null) {
            return null;
        }
        HANStackElement<T> element = top;
        top = element.getNext();
        return element.getValue();
    }

    @Override
    public T peek() {
        if (top == null) {
            return null;
        }
        return top.getValue();
    }
}
