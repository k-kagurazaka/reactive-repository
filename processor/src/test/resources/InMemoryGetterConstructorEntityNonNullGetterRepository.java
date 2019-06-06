import com.kkagurazaka.reactive.repository.annotation.InMemoryRepository;
import org.jetbrains.annotations.NotNull;

@InMemoryRepository(InMemoryGetterConstructorEntity.class)
public interface InMemoryGetterConstructorEntityNonNullGetterRepository {

    @NotNull
    InMemoryGetterConstructorEntity get();
}
