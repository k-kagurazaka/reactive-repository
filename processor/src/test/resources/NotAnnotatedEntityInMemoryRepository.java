import com.kkagurazaka.reactive.repository.annotation.InMemoryRepository;
import io.reactivex.Flowable;
import io.reactivex.Observable;

@InMemoryRepository(NotAnnotatedEntity.class)
public interface NotAnnotatedEntityInMemoryRepository {

    NotAnnotatedEntity get();

    Observable<NotAnnotatedEntity> observe();

    Flowable<NotAnnotatedEntity> observeWithBackpressure();

    void store(NotAnnotatedEntity entity);
}
