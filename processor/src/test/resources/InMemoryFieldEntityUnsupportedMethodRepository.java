import com.kkagurazaka.reactive.repository.annotation.InMemoryRepository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@InMemoryRepository(InMemoryFieldEntity.class)
public interface InMemoryFieldEntityUnsupportedMethodRepository {

    void store(@Nullable InMemoryFieldEntity entity1, @NotNull InMemoryFieldEntity entity2);
}
