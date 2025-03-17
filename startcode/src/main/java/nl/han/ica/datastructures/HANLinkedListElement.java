package nl.han.ica.datastructures;

import lombok.Data;
import lombok.NonNull;

@Data
public class HANLinkedListElement<T> {
    @NonNull
    private T value;
    private HANLinkedListElement<T> next;
}
