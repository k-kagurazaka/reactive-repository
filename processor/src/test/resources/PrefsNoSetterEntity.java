import com.kkagurazaka.reactive.repository.annotation.PrefsEntity;
import com.kkagurazaka.reactive.repository.annotation.PrefsKey;
import org.jetbrains.annotations.Nullable;

@PrefsEntity
public class PrefsNoSetterEntity {

    @Nullable
    private String name;

    private int age;

    @Nullable
    @PrefsKey
    public String getName() {
        return this.name;
    }

    @PrefsKey
    public int getAge() {
        return this.age;
    }
}
