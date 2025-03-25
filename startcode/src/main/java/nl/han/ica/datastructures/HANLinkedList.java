package nl.han.ica.datastructures;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class HANLinkedList<T> implements IHANLinkedList<T> {
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private HANLinearDataNode<T> first;
    @Setter(AccessLevel.NONE)
    private int size;

    private HANLinearDataNode<T> getElement(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        if (index == 0) {
            return first;
        } else {
            HANLinearDataNode<T> element = first;
            for (int i = 0; i < index; i++) {
                element = element.getNext();
            }
            return element;
        }
    }

    @Override
    public void addFirst(T value) {
        HANLinearDataNode<T> element = new HANLinearDataNode<>(value);
        element.setNext(first);
        first = element;
        size++;
    }

    @Override
    public void clear() {
        first = null;
        size = 0;
    }

    @Override
    public void insert(int index, T value) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException();
        }
        if (index == 0) {
            addFirst(value);
        } else {
            HANLinearDataNode<T> prev = getElement(index - 1);
            HANLinearDataNode<T> element = new HANLinearDataNode<>(value);
            element.setNext(prev.getNext());
            prev.setNext(element);
            size++;
        }
    }

    @Override
    public void delete(int pos) {
        if (pos < 0 || pos > size) {
            throw new IndexOutOfBoundsException();
        }
        if (pos == 0) {
            removeFirst();
        } else {
            HANLinearDataNode<T> prev = getElement(pos - 1);
            prev.setNext(prev.getNext().getNext());
            size--;
        }
    }

    @Override
    public T get(int pos) {
        return getElement(pos).getValue();
    }

    @Override
    public void removeFirst() {
        first = first.getNext();
        size--;
    }

    @Override
    public T getFirst() {
        return first.getValue();
    }

    @Override
    public int getSize() {
        return size;
    }
}
