import com.kkagurazaka.reactive.repository.annotation.InMemoryRepository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@InMemoryRepository(InMemoryFieldEntity.class)
public interface InMemoryFieldEntityMultipleGettersRepository {

    @NotNull
    InMemoryFieldEntity get();

    @Nullable
    InMemoryFieldEntity getOrNull();
}
