import com.kkagurazaka.reactive.repository.annotation.PrefsEntity;
import com.kkagurazaka.reactive.repository.annotation.PrefsKey;

@PrefsEntity
public class PrefsUnsupportedTypeEntity {

    @PrefsKey
    public double pie = 3.1415;
}
