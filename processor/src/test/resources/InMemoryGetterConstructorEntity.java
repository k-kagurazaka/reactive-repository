import com.kkagurazaka.reactive.repository.annotation.InMemoryEntity;
import org.jetbrains.annotations.Nullable;

@InMemoryEntity
public class InMemoryGetterConstructorEntity {

    @Nullable
    private String name;

    private int age;

    public InMemoryGetterConstructorEntity(@Nullable String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Nullable
    public String getName() {
        return this.name;
    }

    public int getAge() {
        return this.age;
    }
}
