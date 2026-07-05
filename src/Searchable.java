import java.util.List;
import java.util.function.Predicate;

public interface Searchable<T> {
    List<T> search(String queary);
    List<T> filter(Predicate<T> predicate);
}
