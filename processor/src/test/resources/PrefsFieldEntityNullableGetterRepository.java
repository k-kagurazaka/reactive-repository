import com.kkagurazaka.reactive.repository.annotation.PrefsRepository;
import org.jetbrains.annotations.Nullable;

@PrefsRepository(PrefsFieldEntity.class)
public interface PrefsFieldEntityNullableGetterRepository {

    @Nullable
    PrefsFieldEntity get();
}
