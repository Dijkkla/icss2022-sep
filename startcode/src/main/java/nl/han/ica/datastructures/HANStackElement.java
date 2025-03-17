package nl.han.ica.datastructures;

import lombok.Data;
import lombok.NonNull;

@Data
public class HANStackElement<T> {
    @NonNull
    private T value;
    private HANStackElement<T> next;
}
