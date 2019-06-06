import com.kkagurazaka.reactive.repository.annotation.PrefsEntity;
import com.kkagurazaka.reactive.repository.annotation.PrefsKey;
import org.jetbrains.annotations.Nullable;

@PrefsEntity(preferencesName = "prefs_conflict_entity", useDefaultPreferences = true)
public class PrefsConflictEntity {

    @Nullable
    @PrefsKey
    public String name = null;

    @PrefsKey
    public int age = -1;
}
