import com.kkagurazaka.reactive.repository.annotation.InMemoryEntity;
import org.jetbrains.annotations.Nullable;

@InMemoryEntity
public class InMemoryNotTargetEntity {

    @Nullable
    public String name = null;

    public int age = -1;
}
