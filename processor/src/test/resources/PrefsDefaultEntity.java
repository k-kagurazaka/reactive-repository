import com.kkagurazaka.reactive.repository.annotation.PrefsEntity;
import com.kkagurazaka.reactive.repository.annotation.PrefsKey;
import org.jetbrains.annotations.Nullable;

@PrefsEntity(useDefaultPreferences = true)
public class PrefsDefaultEntity {

    @Nullable
    @PrefsKey
    public String name = null;

    @PrefsKey
    public int age = -1;
}
