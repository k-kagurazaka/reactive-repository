import com.kkagurazaka.reactive.repository.annotation.PrefsEntity;
import com.kkagurazaka.reactive.repository.annotation.PrefsKey;
import org.jetbrains.annotations.Nullable;

@PrefsEntity(preferencesName = "prefs_field_entity")
public class PrefsConflictKeyEntity {

    @Nullable
    @PrefsKey
    public String name = null;
}
