import com.kkagurazaka.reactive.repository.annotation.PrefsKey;
import org.jetbrains.annotations.Nullable;

public class NotAnnotatedEntity {

    @PrefsKey
    @Nullable
    public String name = null;
}
