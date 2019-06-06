import com.kkagurazaka.reactive.repository.annotation.InMemoryRepository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@InMemoryRepository(InMemoryFieldEntity.class)
public interface InMemoryFieldEntityMultipleSettersRepository {

    void set(@NotNull InMemoryFieldEntity entity);

    void setNullable(@Nullable InMemoryFieldEntity entity);
}
