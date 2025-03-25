package nl.han.ica.datastructures;

import lombok.Data;
import lombok.NonNull;

@Data
public class HANLinearDataNode<T> {
    @NonNull
    private T value;
    private HANLinearDataNode<T> next;
}
