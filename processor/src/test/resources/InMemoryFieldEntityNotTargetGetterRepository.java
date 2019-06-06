import com.kkagurazaka.reactive.repository.annotation.InMemoryRepository;
import org.jetbrains.annotations.Nullable;

@InMemoryRepository(InMemoryFieldEntity.class)
public interface InMemoryFieldEntityNotTargetGetterRepository {

    @Nullable
    InMemoryNotTargetEntity get();
}
