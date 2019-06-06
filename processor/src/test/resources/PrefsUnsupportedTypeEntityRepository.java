import com.kkagurazaka.reactive.repository.annotation.PrefsRepository;
import org.jetbrains.annotations.NotNull;

@PrefsRepository(PrefsUnsupportedTypeEntity.class)
public interface PrefsUnsupportedTypeEntityRepository {

    @NotNull
    PrefsUnsupportedTypeEntity get();
}
