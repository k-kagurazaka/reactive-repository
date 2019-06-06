import com.kkagurazaka.reactive.repository.annotation.PrefsEntity;
import com.kkagurazaka.reactive.repository.annotation.PrefsKey;
import org.jetbrains.annotations.Nullable;

@PrefsEntity
public class PrefsMixEntity {

    @Nullable
    @PrefsKey
    public String name = null;

    private int age = -1;

    @PrefsKey
    public int getAge() {
        return this.age;
    }

    @PrefsKey
    public void setAge(int age) {
        this.age = age;
    }
}
