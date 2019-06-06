import com.kkagurazaka.reactive.repository.annotation.PrefsEntity;
import com.kkagurazaka.reactive.repository.annotation.PrefsKey;
import org.jetbrains.annotations.Nullable;

@PrefsEntity
public class PrefsGetterConstructorEntity {

    @Nullable
    private String name;

    private int age;

    public PrefsGetterConstructorEntity(@Nullable String name, int age) {
        this.name = name;
        this.age = age;
    }

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
