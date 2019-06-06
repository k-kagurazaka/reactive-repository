import com.kkagurazaka.reactive.repository.annotation.PrefsRepository;
import io.reactivex.Flowable;
import io.reactivex.Observable;

@PrefsRepository(NotAnnotatedEntity.class)
public interface NotAnnotatedEntityPrefsRepository {

    NotAnnotatedEntity get();

    Observable<NotAnnotatedEntity> observe();

    Flowable<NotAnnotatedEntity> observeWithBackpressure();

    void store(NotAnnotatedEntity entity);
}
